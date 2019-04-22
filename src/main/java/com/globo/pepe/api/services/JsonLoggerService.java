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

    public JsonLogger newLogger(Class<?> klazz) {
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

        private void processThrowable(Throwable throwable) {
            node.put("throwable_message", throwable.getMessage());
            node.set("throwable_stack", mapper.convertValue(throwable, JsonNode.class));
        }

        public JsonLogger put(String key, String value) {
            node.put(key, value);
            return this;
        }

        public String sendDebug() {
            if (logger.isDebugEnabled()) {
                String jsonStr = node.toString();
                logger.debug(jsonStr);
                return jsonStr;
            }
            return null;
        }

        public String sendDebug(Throwable throwable) {
            if (logger.isDebugEnabled()) {
                processThrowable(throwable);
                return sendDebug();
            }
            return null;
        }

        public String sendInfo() {
            String jsonStr = node.toString();
            logger.info(jsonStr);
            return jsonStr;
        }

        public String sendInfo(Throwable throwable) {
            processThrowable(throwable);
            return sendInfo();
        }

        public String sendWarn() {
            String jsonStr = node.toString();
            logger.warn(jsonStr);
            return jsonStr;

        }

        public String sendWarn(Throwable throwable) {
            processThrowable(throwable);
            return sendWarn();
        }

        public String sendError() {
            String jsonStr = node.toString();
            logger.error(jsonStr);
            return jsonStr;
        }

        public String sendError(Throwable throwable) {
            processThrowable(throwable);
            return sendError();
        }
    }
}
