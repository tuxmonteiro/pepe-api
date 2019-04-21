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

package com.globo.pepe.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Metadata {

    private static final long serialVersionUID = 1L;

    private String source;

    private String project;

    private String token;

    private Long timestamp;

    @JsonProperty("trigger_name")
    private String triggerName;

    @JsonProperty("custom_attributes")
    private JsonNode customAttributes;

    public String getSource() {
        return source;
    }

    public Metadata setSource(String source) {
        this.source = source;
        return this;
    }

    public String getProject() {
        return project;
    }

    public Metadata setProject(String project) {
        this.project = project;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Metadata setToken(String token) {
        this.token = token;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Metadata setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public Metadata setTriggerName(String triggerName) {
        this.triggerName = triggerName;
        return this;
    }

    public JsonNode getCustomAttributes() {
        return customAttributes;
    }

    public Metadata setCustomAttributes(JsonNode customAttributes) {
        this.customAttributes = customAttributes;
        return this;
    }
}
