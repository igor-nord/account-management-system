package com.homework.transaction.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class CreditEndpointTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void creditRaisesBalanceAndReturnsTransaction() throws Exception {
        mockMvc().perform(post("/api/account/credit")
                        .header("X-Username", "demo")
                        .header("X-Account-Id", "1000011")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"25.00\",\"description\":\"salary\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legs.length()").value(1))
                .andExpect(jsonPath("$.legs[0].type").value("CREDIT"))
                .andExpect(jsonPath("$.legs[0].amount").value("25.00"));

        mockMvc().perform(get("/api/account")
                        .header("X-Username", "demo")
                        .header("X-Account-Id", "1000011"))
                .andExpect(jsonPath("$.balance").value("25.00"));
    }

    @Test
    void rejectsNonPositiveAmount() throws Exception {
        mockMvc().perform(post("/api/account/credit")
                        .header("X-Username", "demo")
                        .header("X-Account-Id", "1000011")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"0\",\"description\":\"x\"}"))
                .andExpect(status().isBadRequest());
    }
}
