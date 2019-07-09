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

package com.globo.pepe.api.security;

import com.globo.pepe.api.configuration.HttpClientConfiguration.HttpClient;
import com.globo.pepe.common.services.JsonLoggerService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class StackStormAuthenticationProvider implements AuthenticationProvider {

    private static final String ST2_TOKEN_HEADER   = "X-Auth-Token";

    @Value("${pepe.stackstorm.api}")
    private String stackStormApiUrl;

    private final JsonLoggerService loggerService;
    private final HttpClient httpClient;

    public StackStormAuthenticationProvider(JsonLoggerService loggerService, HttpClient httpClient) {
        this.loggerService = loggerService;
        this.httpClient = httpClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        String password = authentication.getCredentials().toString();
        boolean authenticated = false;

        final Map<CharSequence, Iterable<String>> tokenHeader =
            Collections.singletonMap(ST2_TOKEN_HEADER, Collections.singleton(password));
        try {
            Response responseApikey = httpClient.get(stackStormApiUrl + "/", tokenHeader);
            authenticated = responseApikey.getStatusCode() == HttpStatus.OK.value();
        } catch (Exception e) {
            loggerService.newLogger(getClass()).message(String.valueOf(e.getCause())).sendError();
        }
        if (authenticated) {
            loggerService.newLogger(getClass()).message("Login " + login + " OK ").sendInfo();
            final UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(login, password, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(principal);
            return principal;
        }
        loggerService.newLogger(getClass()).message("Login: " + login + " FAILED. Token Problem.").sendWarn();
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
