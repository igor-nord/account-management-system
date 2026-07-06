package com.homework.account.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void listsSeededDemoAccounts() throws Exception {
        mockMvc().perform(get("/api/accounts").header("X-Username", "demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void returnsOneAccountWithStringBalance() throws Exception {
        mockMvc().perform(get("/api/account")
                        .header("X-Username", "demo")
                        .header("X-Account-Id", "1000011"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1000011))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.balance").value("0.00"));
    }

    @Test
    void unknownAccountReturns404() throws Exception {
        mockMvc().perform(get("/api/account")
                        .header("X-Username", "demo")
                        .header("X-Account-Id", "9999999"))
                .andExpect(status().isNotFound());
    }
}
