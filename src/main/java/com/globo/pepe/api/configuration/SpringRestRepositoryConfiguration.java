/*
 * Copyright (c) 2019 - Globo.com - ATeam
 * All rights reserved.
 *
 * This source is subject to the Apache License, Version 2.0.
 * Please see the LICENSE file for more information.
 *
 * Authors: See AUTHORS file
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globo.pepe.api.configuration;

import com.globo.pepe.api.projections.ConnectionProjection;
import com.globo.pepe.api.projections.MetricProjection;
import com.globo.pepe.common.model.munin.AbstractEntity;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.core.DefaultRelProvider;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SpringRestRepositoryConfiguration {

    private String allowedOrigins;
    private String allowedMethods;

    public SpringRestRepositoryConfiguration(
        RepositoryRestConfiguration repositoryRestConfiguration,
        @Value("${pepe.cors.origins}") String allowedOrigins,
        @Value("${pepe.cors.methods}") String allowedMethods) {

        this.allowedOrigins = allowedOrigins;
        this.allowedMethods = allowedMethods;
        repositoryRestConfiguration.getProjectionConfiguration().addProjection(MetricProjection.class);
        repositoryRestConfiguration.getProjectionConfiguration().addProjection(ConnectionProjection.class);
        disableEvo(repositoryRestConfiguration);
        exposeIdsEntities(repositoryRestConfiguration);
        setupCors(repositoryRestConfiguration);
    }

    private void disableEvo(RepositoryRestConfiguration config) {
        config.setRelProvider(new DefaultRelProvider());
    }

    private void exposeIdsEntities(RepositoryRestConfiguration config) {
        final Set<BeanDefinition> beans = allBeansDomain();
        for (BeanDefinition bean : beans) {
            try {
                Class<?> idExposedClasses = Class.forName(bean.getBeanClassName());
                config.exposeIdsFor(Class.forName(idExposedClasses.getName()));
            } catch (ClassNotFoundException e) {
                // Can't throw ClassNotFoundException due to the method signature. Need to cast it
                throw new RuntimeException("Failed to expose `id` field due to", e);
            }
        }
    }

    private Set<BeanDefinition> allBeansDomain() {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
        return provider.findCandidateComponents(AbstractEntity.class.getPackage().getName());
    }

    private void setupCors(RepositoryRestConfiguration config) {
        String pathPatternCors = "/**";
        config.getCorsRegistry().addMapping(pathPatternCors);
        CorsConfiguration corsConfiguration = config.getCorsRegistry().getCorsConfigurations().get(pathPatternCors);
        corsConfiguration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        corsConfiguration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        corsConfiguration.setAllowCredentials(true);
    }
}
