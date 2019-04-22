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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JsonLoggerService {

    @Value("${pepe.logging.tags}")
    private String loggingTags;

    private final ObjectMapper mapper;

    public JsonLoggerService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonLogger instance(Class<?> klazz) {
        return new JsonLogger(klazz);
    }

    public class JsonLogger {

        private final Logger logger;
        private final ObjectNode node = mapper.createObjectNode();

        public JsonLogger(final Class<?> klazz) {
            this.logger = LogManager.getLogger(klazz);
            node.put("class", klazz.getSimpleName());
            node.put("host", System.getenv("HOSTNAME"));
            node.put("tags", loggingTags);
            node.put("timestamp", Instant.now().getEpochSecond());
        }

        private void processThrowwable(Throwable throwable) {
            node.put("throwableMessage", throwable.getMessage());
            try {
                node.set("throwable_stack", mapper.convertValue(throwable, JsonNode.class));
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
            }
        }

        public JsonLogger put(String key, String value) {
            node.put(key, value);
            return this;
        }

        public void sendDebug() {
            if (logger.isDebugEnabled()) {
                logger.debug(node.toString());
            }
        }

        public void sendDebug(Throwable throwable) {
            if (logger.isDebugEnabled()) {
                processThrowwable(throwable);
                sendDebug();
            }
        }

        public void sendInfo() {
            logger.info(node.toString());
        }

        public void sendInfo(Throwable throwable) {
            processThrowwable(throwable);
            sendInfo();
        }

        public void sendWarn() {
            logger.warn(node.toString());
        }

        public void sendWarn(Throwable throwable) {
            processThrowwable(throwable);
            sendWarn();
        }

        public void sendError() {
            logger.error(node.toString());
        }

        public void sendError(Throwable throwable) {
            processThrowwable(throwable);
            sendError();
        }
    }
}
