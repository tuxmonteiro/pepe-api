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

package com.globo.pepe.api.services;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.exceptions.AuthenticationException;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.v3.Token;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Component;

@Component
public class KeystoneService {

    private static final Logger LOGGER                  = LogManager.getLogger();
    private static final String KEYSTONE_URL            = Optional.ofNullable(System.getenv("KEYSTONE_URL")).orElse("http://127.0.0.1:5000/v3");
    private static final String KEYSTONE_DOMAIN_CONTEXT = Optional.ofNullable(System.getenv("KEYSTONE_DOMAIN_CONTEXT")).orElse("default");

    private boolean ignore = false;

    public KeystoneService ignore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    public boolean isValid(String project, String token) {
        if (ignore) return true;
        try {
            Token tokenOSv3 = authenticate(KEYSTONE_URL, project, token).getToken();
            return tokenOSv3 != null && tokenOSv3.getUser() != null;
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public OSClientV3 authenticate(String url, String project, String token) throws AuthenticationException {
        return OSFactory.builderV3()
            .endpoint(url)
            .token(token)
            .scopeToProject(Identifier.byName(project), Identifier.byName(KEYSTONE_DOMAIN_CONTEXT))
            .authenticate();
    }
}
