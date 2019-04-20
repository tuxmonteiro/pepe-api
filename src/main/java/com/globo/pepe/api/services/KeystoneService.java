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

import static com.globo.pepe.api.util.ComplianceChecker.throwIfNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.exceptions.AuthenticationException;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.v3.Token;
import org.openstack4j.model.identity.v3.User;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class KeystoneService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Value("${keystone.url}") String keystoneUrl;
    @Value("${keystone.domain}") String keystoneDomainContext;

    private boolean ignore = false;

    public KeystoneService ignore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    public boolean isValid(String project, String token) {
        if (ignore) return true;
        try {
            return userExist(project, token);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    private AuthenticationException getAuthException(String objName) {
        return new AuthenticationException("{\"error\":\"" + objName + " is null\"}", 401);
    }

    private OSClientV3 authenticate(String project, String token) throws RuntimeException {
        return OSFactory.builderV3()
            .endpoint(keystoneUrl)
            .token(token)
            .scopeToProject(Identifier.byName(project), Identifier.byName(keystoneDomainContext))
            .authenticate();
    }

    @Cacheable(cacheNames = "userids", key = "{#project,#token}", unless="#result == null")
    public boolean userExist(String project, String token) throws RuntimeException {
        OSClientV3 osClientV3;
        Token tokenOSv3;
        throwIfNull(osClientV3 = authenticate(project, token), getAuthException(OSClientV3.class.getSimpleName()));
        throwIfNull(tokenOSv3 = osClientV3.getToken(), getAuthException(Token.class.getSimpleName()));
        throwIfNull(tokenOSv3.getUser(), getAuthException(User.class.getSimpleName()));
        return true;
    }
}
