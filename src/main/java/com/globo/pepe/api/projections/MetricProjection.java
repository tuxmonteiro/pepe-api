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

package com.globo.pepe.api.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.globo.pepe.common.model.munin.Metric;
import java.util.Date;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "recursive", types = { Metric.class })
public interface MetricProjection {

    Long getId();

    @JsonProperty("_created_by")
    String getCreatedBy();

    @JsonProperty("_created_at")
    Date getCreatedAt();

    @JsonProperty("_last_modified_by")
    String getLastModifiedBy();

    @JsonProperty("_last_modified_at")
    Date getLastModifiedAt();

    String getName();

    String getQuery();

    String getTrigger();

    @JsonProperty("enable")
    Boolean getEnable();

    Long getInterval();

    @JsonProperty("last_processing")
    Date getLastProcessing();

    ConnectionProjection getConnection();

    ProjectProjection getProject();
}
