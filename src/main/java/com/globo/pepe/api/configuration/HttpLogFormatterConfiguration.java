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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.JsonHttpLogFormatter;
import org.zalando.logbook.Precorrelation;

import static org.zalando.logbook.BodyFilter.*;
import static org.zalando.logbook.BodyFilters.*;

@Configuration
public class HttpLogFormatterConfiguration {

    @Value("${pepe.logging.tags}")
    private String loggingTags;

    @Bean
    public HttpLogFormatter jsonFormatter(final ObjectMapper mapper) {
        return new PrincipalHttpLogFormatter(new JsonHttpLogFormatter(mapper));
    }

    private class PrincipalHttpLogFormatter implements HttpLogFormatter {

        private final JsonHttpLogFormatter delegate;

        public PrincipalHttpLogFormatter(JsonHttpLogFormatter delegate) {
            this.delegate = delegate;
        }

        @Override
        public String format(Precorrelation<HttpRequest> precorrelation) throws IOException {
            final Map<String, Object> content = delegate.prepare(precorrelation);
            content.put("short_message", "Request message");
            content.put("principal", getPrincipal());
            content.put("tags", loggingTags);
            return delegate.format(content);
        }

        @Override
        public String format(Correlation<HttpRequest, HttpResponse> correlation) throws IOException {
            final Map<String, Object> content = delegate.prepare(correlation);
            content.put("body_response", correlation.getOriginalResponse().getBodyAsString());
            content.put("short_message", "Response message");
            content.put("principal", getPrincipal());
            content.put("tags", loggingTags);
            return delegate.format(content);
        }

        private String getPrincipal() {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            final String principal = authentication.getName();
            return principal == null ? "anonymous" : principal;
        }
    }

    @Bean
    public BodyFilter bodyFilter() {
        return merge(defaultValue(), replaceJsonStringProperty(Collections.singleton("password"), "XXX"));
    }
}
