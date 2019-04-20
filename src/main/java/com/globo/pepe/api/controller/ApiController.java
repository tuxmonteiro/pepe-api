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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.globo.pepe.api.model.Event;
import com.globo.pepe.api.model.Metadata;
import com.globo.pepe.api.services.KeystoneService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;

import static com.globo.pepe.api.util.ComplianceChecker.throwIfNull;

@SuppressWarnings("unused")
@RestController
public class ApiController {

    private static final Logger LOGGER = LogManager.getLogger();

    private final KeystoneService keystoneService;
    private final ObjectMapper mapper;

    public ApiController(KeystoneService keystoneService, ObjectMapper mapper) {
        this.keystoneService = keystoneService;
        this.mapper = mapper;
    }

    @PostMapping(name = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> post(@RequestBody JsonNode body) {
        try {
            final Event event = mapper.convertValue(body, Event.class);
            throwIfNull(event.getId(), new RuntimeException("id NOT FOUND"));
            final Metadata metadata;
            throwIfNull(metadata = event.getMetadata(), new RuntimeException("metadata NOT FOUND"));
            throwIfNull(event.getPayload(), new RuntimeException("payload NOT FOUND"));

            final String project = Optional.ofNullable(metadata.getProject()).orElse("");
            final String token = Optional.ofNullable(metadata.getToken()).orElse("");
            if (keystoneService.ignore(token.isEmpty() || project.isEmpty()).isValid(project, token)) {
                throwIfNull(metadata.getSource(), new RuntimeException("metadata.source NOT FOUND"));
                throwIfNull(metadata.getTimestamp(), new RuntimeException("metadata.timestamp NOT FOUND"));

                final JsonNode customAttributes = Optional.ofNullable(metadata.getCustomAttributes()).orElse(mapper.createObjectNode());
                ((ObjectNode)customAttributes).put("received_at", Instant.now().toString());
                metadata.setCustomAttributes(customAttributes);

                return ResponseEntity.created(URI.create("/api")).body(mapper.valueToTree(event));
            }
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage() + ": " + body, e);
            return ResponseEntity.status(400).body(mapper.createObjectNode());
        }

        return ResponseEntity.status(401).body(mapper.createObjectNode());
    }

}
