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

import static com.globo.pepe.common.util.ComplianceChecker.throwIfNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globo.pepe.common.services.JsonLoggerService;
import com.globo.pepe.common.model.Event;
import com.globo.pepe.common.model.Metadata;
import com.globo.pepe.api.services.ChapolinService;
import com.globo.pepe.api.services.KeystoneService;
import java.net.URI;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
public class ApiController {

    private final KeystoneService keystoneService;
    private final ChapolinService chapolinService;
    private final ObjectMapper mapper;
    private final JsonLoggerService jsonLoggerService;

    public ApiController(KeystoneService keystoneService,
        ChapolinService chapolinService,
        ObjectMapper mapper, JsonLoggerService jsonLoggerService) {
        this.keystoneService = keystoneService;
        this.chapolinService = chapolinService;
        this.mapper = mapper;
        this.jsonLoggerService = jsonLoggerService;
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
            throwIfNull(metadata.getProject(), new RuntimeException("metadata.project NOT FOUND"));
            throwIfNull(metadata.getToken(), new RuntimeException("metadata.token NOT FOUND"));
            throwIfNull(metadata.getSource(), new RuntimeException("metadata.source NOT FOUND"));
            throwIfNull(metadata.getTimestamp(), new RuntimeException("metadata.timestamp NOT FOUND"));
            throwIfNull(metadata.getTriggerName(), new RuntimeException("metadata.trigger_name NOT FOUND"));
            if (keystoneService.isValid(project, token)) {
                chapolinService.eventInstance(event).prepareQueueAndTrigger().defineCustomAttributes().send();
                final JsonNode resultBody = mapper.valueToTree(event);
                return ResponseEntity.created(URI.create("/api")).body(resultBody);
            }
        } catch (RuntimeException e) {
            jsonLoggerService.newLogger(getClass()).put("short_message", e.getMessage() + ": " + body).sendError();
            return ResponseEntity.status(400).body(mapper.createObjectNode());
        }

        return ResponseEntity.status(401).body(mapper.createObjectNode());
    }

}
