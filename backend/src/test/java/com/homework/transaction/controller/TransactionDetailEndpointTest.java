package com.homework.transaction.controller;

import com.jayway.jsonpath.JsonPath;
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
class TransactionDetailEndpointTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void returnsTransactionByIdForOwnedAccount() throws Exception {
        String body = mockMvc().perform(post("/api/account/credit")
                        .header("X-Username", "demo").header("X-Account-Id", "1000011")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"12.00\"}"))
                .andReturn().getResponse().getContentAsString();
        String transactionId = JsonPath.read(body, "$.transactionId");

        mockMvc().perform(get("/api/transaction")
                        .header("X-Username", "demo").header("X-Transaction-Id", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId))
                .andExpect(jsonPath("$.legs[0].amount").value("12.00"));
    }

    @Test
    void unknownTransactionReturns404() throws Exception {
        mockMvc().perform(get("/api/transaction")
                        .header("X-Username", "demo").header("X-Transaction-Id", "DOESNOTEXIST0"))
                .andExpect(status().isNotFound());
    }
}
