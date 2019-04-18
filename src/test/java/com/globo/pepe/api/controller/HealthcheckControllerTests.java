package com.globo.pepe.api.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@WebMvcTest(HealthcheckController.class)
public class HealthcheckControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void healthcheckControllerCheck() throws Exception{
        mockMvc.perform(get("/healthcheck")).andExpect(content().string("WORKING"));
    }
}
