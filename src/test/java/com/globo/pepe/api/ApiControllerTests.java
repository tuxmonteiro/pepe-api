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

package com.globo.pepe.api;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.globo.pepe.api.controller.ApiController;
import com.globo.pepe.api.services.KeystoneService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest({ApiController.class, KeystoneService.class})
public class ApiControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private static ClientAndServer mockServer;

    @BeforeClass
    public static void setupClass() throws IOException {
        mockServer = ClientAndServer.startClientAndServer(5000);

        InputStream resourceAuthOk = ApiControllerTests.class.getResourceAsStream("/keystone-auth.json");
        String bodyAuthOk = IOUtils.toString(resourceAuthOk, Charset.defaultCharset());
        mockServer.when(request().withMethod("POST").withPath("/v3/auth/tokens").withBody("{\n"
            + "  \"auth\" : {\n"
            + "    \"identity\" : {\n"
            + "      \"token\" : {\n"
            + "        \"id\" : \"token-ok\"\n"
            + "      },\n"
            + "      \"methods\" : [ \"token\" ]\n"
            + "    },\n"
            + "    \"scope\" : {\n"
            + "      \"project\" : {\n"
            + "        \"name\" : \"admin\",\n"
            + "        \"domain\" : {\n"
            + "          \"name\" : \"default\"\n"
            + "        }\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}"))
            .respond(response().withBody(bodyAuthOk).withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(201));

        InputStream resourceAuthFail = ApiControllerTests.class.getResourceAsStream("/keystone-auth-fail.json");
        String bodyAuthFail = IOUtils.toString(resourceAuthFail, Charset.defaultCharset());
        mockServer.when(request().withMethod("POST").withPath("/v3/auth/tokens").withBody("{\n"
            + "  \"auth\" : {\n"
            + "    \"identity\" : {\n"
            + "      \"token\" : {\n"
            + "        \"id\" : \"wrong-token\"\n"
            + "      },\n"
            + "      \"methods\" : [ \"token\" ]\n"
            + "    },\n"
            + "    \"scope\" : {\n"
            + "      \"project\" : {\n"
            + "        \"name\" : \"admin\",\n"
            + "        \"domain\" : {\n"
            + "          \"name\" : \"default\"\n"
            + "        }\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}"))
            .respond(response().withBody(bodyAuthFail).withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(401));

    }

    @AfterClass
    public static void cleanup() {
        if (mockServer.isRunning()) {
            mockServer.stop();
        }
    }

    @Test
    public void apiControllerNotAuthenticated() throws Exception {
        String eventWithoutAuth = "{}";
        mockMvc.perform(post("/api").content(eventWithoutAuth)
            .contentType(APPLICATION_JSON_VALUE)).andExpect(status().isCreated());
    }

    @Test
    public void apiControllerAuthenticationOk() throws Exception {
        String eventWithAuthOK = "{\"token\":\"token-ok\",\"project\":\"admin\"}";
        mockMvc.perform(post("/api").content(eventWithAuthOK)
            .contentType(APPLICATION_JSON_VALUE)).andExpect(status().isCreated());
    }

    @Test
    public void apiControllerAuthenticationFail() throws Exception {
        String eventWithAuthFail = "{\"token\":\"wrong-token\",\"project\":\"admin\"}";
        mockMvc.perform(post("/api").content(eventWithAuthFail)
            .contentType(APPLICATION_JSON_VALUE)).andExpect(status().isUnauthorized());
    }
}
