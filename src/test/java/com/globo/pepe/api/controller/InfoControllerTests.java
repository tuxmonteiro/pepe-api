package com.globo.pepe.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({InfoController.class})
public class InfoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Value("${build.project}")
    private String buildProject;

    @Value("${build.version}")
    private String buildVersion;

    @Value("${build.timestamp}")
    private String buildTimestamp;

    @Test
    public void buildInfoCheck() throws Exception{
        mockMvc.perform(get("/info"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(String.format("{\"name\":\"%s\", \"version\":\"%s\", \"build\":\"%s\", \"health\":\"WORKING\"}", buildProject, buildVersion, buildTimestamp)))
                .andExpect(status().isOk());
    }
}
