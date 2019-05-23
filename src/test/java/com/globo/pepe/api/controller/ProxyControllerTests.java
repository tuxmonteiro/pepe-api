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
import com.globo.pepe.common.services.JsonLoggerService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({ProxyController.class, RestTemplateConfiguration.class, RestTemplate.class, JsonLoggerService.class})
@TestPropertySource(properties = {
    "pepe.logging.tags=default",
    "pepe.stackstorm.api=http://127.0.0.1:9000",
    "pepe.stackstorm.auth=http://127.0.0.1:9000",
    "pepe.stackstorm.stream=http://127.0.0.1:9000",
    "pepe.api.origins=http://xxx"
})
public class ProxyControllerTests {

    private static ClientAndServer mockServer;

    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void setupClass() throws IOException {
        mockServer = ClientAndServer.startClientAndServer(9000);

        mockServer.when(request().withMethod(POST.name()).withPath("/auth/testauth").withBody("{\"path\":\"auth\"}"))
            .respond(response().withBody("{\"path\":\"auth\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(CREATED.value()));

        mockServer.when(request().withMethod(POST.name()).withPath("/api/testapi").withBody("{\"path\":\"api\"}"))
            .respond(response().withBody("{\"path\":\"api\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(CREATED.value()));

        mockServer.when(request().withMethod(POST.name()).withPath("/stream/teststream").withBody("{\"path\":\"stream\"}"))
            .respond(response().withBody("{\"path\":\"stream\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(CREATED.value()));

        mockServer.when(request().withMethod(PUT.name()).withPath("/auth/testauth").withBody("{\"path\":\"auth\"}"))
            .respond(response().withBody("{\"path\":\"auth\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(ACCEPTED.value()));

        mockServer.when(request().withMethod(PUT.name()).withPath("/api/testapi").withBody("{\"path\":\"api\"}"))
            .respond(response().withBody("{\"path\":\"api\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(ACCEPTED.value()));

        mockServer.when(request().withMethod(PUT.name()).withPath("/stream/teststream").withBody("{\"path\":\"stream\"}"))
            .respond(response().withBody("{\"path\":\"stream\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(ACCEPTED.value()));

        mockServer.when(request().withMethod(PATCH.name()).withPath("/auth/testauth").withBody("{\"path\":\"auth\"}"))
            .respond(response().withBody("{\"path\":\"auth\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(ACCEPTED.value()));

        mockServer.when(request().withMethod(PATCH.name()).withPath("/api/testapi").withBody("{\"path\":\"api\"}"))
            .respond(response().withBody("{\"path\":\"api\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(ACCEPTED.value()));

        mockServer.when(request().withMethod(PATCH.name()).withPath("/stream/teststream").withBody("{\"path\":\"stream\"}"))
            .respond(response().withBody("{\"path\":\"stream\"}").withHeader("Content-Type", APPLICATION_JSON_VALUE).withStatusCode(ACCEPTED.value()));

        mockServer.when(request().withMethod(DELETE.name()).withPath("/auth/testauth"))
            .respond(response().withStatusCode(ACCEPTED.value()));

        mockServer.when(request().withMethod(DELETE.name()).withPath("/api/testapi"))
            .respond(response().withStatusCode(ACCEPTED.value()));

        mockServer.when(request().withMethod(DELETE.name()).withPath("/stream/teststream"))
            .respond(response().withStatusCode(ACCEPTED.value()));

        mockServer.when(request().withMethod(GET.name()).withPath("/auth/testauth"))
                .respond(response().withBody("{\"path\":\"auth\"}").withHeader("Accept", APPLICATION_JSON_VALUE).withStatusCode(OK.value()));

        mockServer.when(request().withMethod(GET.name()).withPath("/api/testapi"))
                .respond(response().withBody("{\"path\":\"api\"}").withHeader("Accept", APPLICATION_JSON_VALUE).withStatusCode(OK.value()));

        mockServer.when(request().withMethod(GET.name()).withPath("/stream/teststream"))
                .respond(response().withBody("{\"path\":\"stream\"}").withHeader("Accept", APPLICATION_JSON_VALUE).withStatusCode(OK.value()));

        mockServer.when(request().withMethod(HEAD.name()).withPath("/auth/testauth"))
            .respond(response().withStatusCode(OK.value()));

        mockServer.when(request().withMethod(HEAD.name()).withPath("/api/testapi"))
            .respond(response().withStatusCode(OK.value()));

        mockServer.when(request().withMethod(HEAD.name()).withPath("/stream/teststream"))
            .respond(response().withStatusCode(OK.value()));

    }

    @AfterClass
    public static void cleanupClass() {
        mockServer.stop();
    }


    @Test
    public void proxyApiPostTest() throws Exception {
        mockMvc.perform(post("/admin/api/testapi").content("{\"path\":\"api\"}")
                .contentType(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"api\"}"))
            .andExpect(status().isCreated());
    }

    @Test
    public void proxyAuthPostTest() throws Exception {
        mockMvc.perform(post("/admin/auth/testauth").content("{\"path\":\"auth\"}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"auth\"}"))
            .andExpect(status().isCreated());
    }

    @Test
    public void proxyStreamPostTest() throws Exception {
        mockMvc.perform(post("/admin/stream/teststream").content("{\"path\":\"stream\"}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"stream\"}"))
            .andExpect(status().isCreated());
    }

    @Test
    public void proxyOtherPostTest() throws Exception {
        mockMvc.perform(post("/admin/other/testother").content("{}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void proxyApiPatchTest() throws Exception {
        mockMvc.perform(patch("/admin/api/testapi").content("{\"path\":\"api\"}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"api\"}"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void proxyAuthPatchTest() throws Exception {
        mockMvc.perform(patch("/admin/auth/testauth").content("{\"path\":\"auth\"}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"auth\"}"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void proxyStreamPatchTest() throws Exception {
        mockMvc.perform(patch("/admin/stream/teststream").content("{\"path\":\"stream\"}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"stream\"}"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void proxyOtherPatchTest() throws Exception {
        mockMvc.perform(patch("/admin/other/testother").content("{}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void proxyApiPutTest() throws Exception {
        mockMvc.perform(put("/admin/api/testapi").content("{\"path\":\"api\"}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"api\"}"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void proxyAuthPutTest() throws Exception {
        mockMvc.perform(put("/admin/auth/testauth").content("{\"path\":\"auth\"}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"auth\"}"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void proxyStreamPutTest() throws Exception {
        mockMvc.perform(put("/admin/stream/teststream").content("{\"path\":\"stream\"}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"stream\"}"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void proxyOtherPutTest() throws Exception {
        mockMvc.perform(put("/admin/other/testother").content("{}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void proxyApiDeleteTest() throws Exception {
        mockMvc.perform(delete("/admin/api/testapi")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted());
    }

    @Test
    public void proxyAuthDeleteTest() throws Exception {
        mockMvc.perform(delete("/admin/auth/testauth")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted());
    }

    @Test
    public void proxyStreamDeleteTest() throws Exception {
        mockMvc.perform(delete("/admin/stream/teststream")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted());
    }

    @Test
    public void proxyOtherDeleteTest() throws Exception {
        mockMvc.perform(delete("/admin/other/testother").content("{}")
            .contentType(APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void proxyApiGetTest() throws Exception {
        mockMvc.perform(get("/admin/api/testapi")
            .accept(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"api\"}"))
            .andExpect(status().isOk());
    }

    @Test
    public void proxyAuthGetTest() throws Exception {
        mockMvc.perform(get("/admin/auth/testauth")
            .accept(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"auth\"}"))
            .andExpect(status().isOk());
    }

    @Test
    public void proxyStreamGetTest() throws Exception {
        mockMvc.perform(get("/admin/stream/teststream")
            .accept(APPLICATION_JSON_VALUE))
            .andExpect(content().string("{\"path\":\"stream\"}"))
            .andExpect(status().isOk());
    }

    @Test
    public void proxyOtherGetTest() throws Exception {
        mockMvc.perform(get("/admin/other/testother")
            .accept(APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void proxyApiOptionsTest() throws Exception {
        // OPTIONS: mockserver doesn't support custom matcher. Using inner support
        mockMvc.perform(options("/admin/api/testapi"))
            .andExpect(header().string("Allow", "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS"))
            .andExpect(status().isOk());
    }

    @Test
    public void proxyAuthOptionsTest() throws Exception {
        // OPTIONS: mockserver doesn't support custom matcher. Using inner support
        mockMvc.perform(options("/admin/auth/testauth"))
            .andExpect(header().string("Allow", "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS"))
            .andExpect(status().isOk());
    }

    @Test
    public void proxyStreamOptionsTest() throws Exception {
        // OPTIONS: mockserver doesn't support custom matcher. Using inner support
        mockMvc.perform(options("/admin/stream/teststream"))
            .andExpect(header().string("Allow", "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS"))
            .andExpect(status().isOk());
    }

    @Test
    public void proxyApiHeadTest() throws Exception {
        mockMvc.perform(head("/admin/api/testapi"))
            .andExpect(status().isOk());
    }

    @Test
    public void proxyAuthHeadTest() throws Exception {
        mockMvc.perform(head("/admin/auth/testauth"))
            .andExpect(status().isOk());
    }

    @Test
    public void proxyStreamHeadTest() throws Exception {
        mockMvc.perform(head("/admin/stream/teststream"))
            .andExpect(status().isOk());
    }

    @Test
    public void proxyOtherHeadTest() throws Exception {
        mockMvc.perform(head("/admin/other/testother"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void corsTest() throws Exception {
        mockMvc.perform(get("/healthcheck")
            .header("Origin", "http://xxx"))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    public void corsFailTest() throws Exception {
        mockMvc.perform(get("/healthcheck")
            .header("Origin", "http://yyy"))
            .andExpect(status().isForbidden())
            .andDo(print());
    }

}
