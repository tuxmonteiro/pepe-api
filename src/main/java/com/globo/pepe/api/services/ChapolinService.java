/*
 * Copyright (c) 2017-2019 Globo.com
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.globo.pepe.common.model.Event;
import com.globo.pepe.common.model.Metadata;
import com.globo.pepe.common.services.AmqpService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class ChapolinService {

    public static final String QUEUE_TRIGGER_PREFIX = "pepe.trigger.";

    private final AmqpService amqpService;
    private final ObjectMapper mapper;

    public ChapolinService(AmqpService amqpService, ObjectMapper mapper) {
        this.amqpService = amqpService;
        this.mapper = mapper;
    }

    public EventInstance eventInstance(final Event event) {
        Metadata metadata = event.getMetadata();
        String queueTriggerName = QUEUE_TRIGGER_PREFIX + metadata.getTriggerName();
        return new EventInstance(event, metadata, queueTriggerName);
    }

    public class EventInstance {

        private final Event event;
        private final Metadata metadata;
        private final String queueTriggerName;

        private EventInstance(Event event, Metadata metadata, String queueTriggerName) {
            this.event = event;
            this.metadata = metadata;
            this.queueTriggerName = queueTriggerName;
        }

        public EventInstance prepareQueueAndTrigger() {
            String queueName = QUEUE_TRIGGER_PREFIX + metadata.getTriggerName();
            amqpService.newQueue(queueName);
            //TODO: create trigger using command queue

            return this;
        }

        public Event send() {
            amqpService.convertAndSend(queueTriggerName, mapper.convertValue(event, JsonNode.class).toString(), 10000);
            return event;
        }

        public EventInstance defineCustomAttributes() {
            final JsonNode customAttributes = Optional.ofNullable(metadata.getCustomAttributes()).orElse(mapper.createObjectNode());
            ((ObjectNode)customAttributes)
                    .put("received_at", Instant.now().toString())
                    .put("trigger", queueTriggerName);
            metadata.setCustomAttributes(customAttributes);
            return this;
        }

    }
}
