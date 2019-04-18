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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globo.pepe.api.services.KeystoneService;
import java.net.URI;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
public class ApiController {

    @Autowired
    KeystoneService keystoneService;

    @Autowired
    ObjectMapper mapper;

    @PostMapping(name = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> post(@RequestBody JsonNode body) {
        String token = Optional.ofNullable(body.get("token")).orElse(mapper.createObjectNode()).asText();
        String project = Optional.ofNullable(body.get("project")).orElse(mapper.createObjectNode()).asText();
        if (keystoneService.ignore(token.isEmpty() || project.isEmpty()).isValid(project, token)) {
            return ResponseEntity.created(URI.create("/api")).body(body);
        }
        return ResponseEntity.status(401).body(mapper.createObjectNode());
    }

}
