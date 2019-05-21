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

package com.globo.pepe.api.controller;

import com.globo.pepe.api.configuration.RestTemplateConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({ProxyController.class, RestTemplateConfiguration.class, RestTemplate.class})
public class ProxyControllerTests {

    private static ClientAndServer mockServer;

    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void setupClass() throws IOException {
        mockServer = ClientAndServer.startClientAndServer(9000);

        mockServer.when(request().withMethod("POST").withPath("/admin/auth/testauth").withBody("{\"path\":\"auth\"}"))
            .respond(response().withBody("{\"path\":\"auth\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(201));

        mockServer.when(request().withMethod("POST").withPath("/admin/api/testapi").withBody("{\"path\":\"api\"}"))
            .respond(response().withBody("{\"path\":\"api\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(201));

        mockServer.when(request().withMethod("POST").withPath("/admin/stream/teststream").withBody("{\"path\":\"stream\"}"))
            .respond(response().withBody("{\"path\":\"stream\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(201));

        mockServer.when(request().withMethod("GET").withPath("/admin/auth/testauth"))
                .respond(response().withBody("{\"path\":\"auth\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(200));

        mockServer.when(request().withMethod("GET").withPath("/admin/api/testapi"))
                .respond(response().withBody("{\"path\":\"api\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(200));

        mockServer.when(request().withMethod("GET").withPath("/admin/stream/teststream"))
                .respond(response().withBody("{\"path\":\"stream\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(200));

    }

    @AfterClass
    public static void cleanupClass() {
        mockServer.stop();
    }


    @Test
    public void proxyPostTest() throws Exception {
        mockMvc.perform(post("/admin/api/testapi").content("{\"path\":\"api\"}")
                .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andDo(print());
    }

}
