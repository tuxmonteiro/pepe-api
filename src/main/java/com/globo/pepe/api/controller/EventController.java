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
import com.globo.pepe.common.services.JsonLoggerService;
import com.globo.pepe.common.model.Event;
import com.globo.pepe.common.model.Metadata;
import com.globo.pepe.api.services.ChapolinService;
import com.globo.pepe.api.services.KeystoneService;
import java.net.URI;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping(value = {"/event", "/{apiVersion:.+}/event"},
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    private final KeystoneService keystoneService;
    private final ChapolinService chapolinService;
    private final ObjectMapper mapper;
    private final JsonLoggerService jsonLoggerService;

    public EventController(KeystoneService keystoneService,
        ChapolinService chapolinService,
        ObjectMapper mapper, JsonLoggerService jsonLoggerService) {
        this.keystoneService = keystoneService;
        this.chapolinService = chapolinService;
        this.mapper = mapper;
        this.jsonLoggerService = jsonLoggerService;
    }

    @PostMapping
    public ResponseEntity<JsonNode> post(@PathVariable(required = false) String apiVersion, @RequestBody JsonNode body) {
        try {
            final Event event = mapper.convertValue(body, Event.class);
            final Metadata metadata = sanitizeAndExtractMetadata(event);
            final String project = Optional.ofNullable(metadata.getProject()).orElse("");
            final String token = Optional.ofNullable(metadata.getToken()).orElse("");
            if (keystoneService.authenticate(project, token)) {
                chapolinService.from(event).send();
                final JsonNode resultBody = mapper.valueToTree(event);
                return ResponseEntity.created(URI.create("/event")).body(resultBody);
            }
        } catch (RuntimeException e) {
            jsonLoggerService.newLogger(getClass()).put("short_message", e.getMessage() + ": " + body).sendError();
            return ResponseEntity.status(400).body(mapper.createObjectNode().put("error", e.getMessage()));
        }

        return ResponseEntity.status(401).body(mapper.createObjectNode());
    }

    private Metadata sanitizeAndExtractMetadata(Event event) throws RuntimeException {
        final Metadata metadata;
        Assert.notNull(event.getId(), "id NOT FOUND");
        Assert.notNull(metadata = event.getMetadata(), "metadata NOT FOUND");
        Assert.notNull(event.getPayload(), "payload NOT FOUND");
        Assert.notNull(metadata.getProject(), "metadata.project NOT FOUND");
        Assert.notNull(metadata.getToken(), "metadata.token NOT FOUND");
        Assert.notNull(metadata.getSource(), "metadata.source NOT FOUND");
        Assert.notNull(metadata.getTimestamp(), "metadata.timestamp NOT FOUND");
        Assert.notNull(metadata.getTriggerName(), "metadata.trigger_name NOT FOUND");
        return metadata;
    }

}
