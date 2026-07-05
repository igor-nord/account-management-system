package com.homework.history.infrastructure.controller;

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
class HistoryControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    private void credit(String amount) throws Exception {
        mockMvc().perform(post("/api/account/credit")
                .header("X-Customer-Id", "1").header("X-Account-Id", "1000011")
                .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"" + amount + "\"}"));
    }

    @Test
    void paginatesNewestFirstWithCursor() throws Exception {
        credit("10.00");
        credit("20.00");
        credit("30.00");

        String body = mockMvc().perform(get("/api/account/transactions")
                        .header("X-Customer-Id", "1").header("X-Account-Id", "1000011").param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].amount").value("30.00"))
                .andReturn().getResponse().getContentAsString();
        String cursor = JsonPath.read(body, "$.nextCursor");

        mockMvc().perform(get("/api/account/transactions")
                        .header("X-Customer-Id", "1").header("X-Account-Id", "1000011")
                        .param("limit", "2").param("cursor", cursor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].amount").value("10.00"))
                .andExpect(jsonPath("$.nextCursor").doesNotExist());
    }

    @Test
    void balanceSeriesEndsAtCurrentBalance() throws Exception {
        credit("10.00");
        credit("50.00");

        mockMvc().perform(get("/api/account/balance-series")
                        .header("X-Customer-Id", "1").header("X-Account-Id", "1000011"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points.length()").value(2))
                .andExpect(jsonPath("$.points[0].balance").value("10.00"))
                .andExpect(jsonPath("$.points[1].balance").value("60.00"));
    }

    @Test
    void notOwnedAccountReturns404() throws Exception {
        mockMvc().perform(get("/api/account/transactions")
                        .header("X-Customer-Id", "1").header("X-Account-Id", "1000001"))
                .andExpect(status().isNotFound());
    }
}
