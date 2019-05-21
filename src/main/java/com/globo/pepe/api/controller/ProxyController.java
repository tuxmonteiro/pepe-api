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

package com.globo.pepe.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static java.util.Collections.singletonList;

@RestController
@RequestMapping(value = "/{path:^(?!event|admin).*}")
public class ProxyController {

    private final RestTemplate restTemplate;
    private final Map<String, String> destinations;

    public ProxyController(RestTemplate restTemplate,
        @Value("${pepe.stackstorm.api}") String pepeStackstormApi,
        @Value("${pepe.stackstorm.auth}") String pepeStackstormAuth,
        @Value("${pepe.stackstorm.stream}") String pepeStackstormStream) {

        this.restTemplate = restTemplate;
        this.destinations = Map.of(
            "api", pepeStackstormApi,
            "auth", pepeStackstormAuth,
            "stream", pepeStackstormStream
        );
    }

    @ResponseBody
    public ResponseEntity<String> proxy(
        @PathVariable String path,
        @RequestBody(required = false) String body,
        @RequestHeader(required = false) HttpHeaders headers,
        @RequestParam(required = false) Map<String, String> params,
        HttpMethod method) throws RuntimeException {

        String destUrl = destinations.get(path);
        if (destUrl == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("path /" + path + " NOT FOUND");
        }

        final MultiValueMap<String, String> multiMapParams = new LinkedMultiValueMap<>();
        if (params != null) {
            params.forEach((k, v) -> multiMapParams.put(k, singletonList(v)));
        }
        String newUri = UriComponentsBuilder.fromUri(URI.create(destUrl)).path("/" + path)
            .queryParams(multiMapParams).build().toUriString();
        final HttpEntity<?> requestData = new HttpEntity<>(body, headers);

        return restTemplate.exchange(newUri, method, requestData, String.class);
    }

}
