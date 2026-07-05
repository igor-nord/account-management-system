package com.homework.reporting.infrastructure.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class ReportControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void downloadsTransactionPdf() throws Exception {
        String body = mockMvc().perform(post("/api/account/credit")
                        .header("X-Customer-Id", "1").header("X-Account-Id", "1000011")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"25.00\"}"))
                .andReturn().getResponse().getContentAsString();
        String transactionId = JsonPath.read(body, "$.transactionId");

        mockMvc().perform(get("/api/transaction/pdf")
                        .header("X-Customer-Id", "1").header("X-Transaction-Id", transactionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", containsString("attachment")))
                .andExpect(header().string("Content-Disposition", containsString("transaction-" + transactionId)));
    }

    @Test
    void unknownTransactionReturns404() throws Exception {
        mockMvc().perform(get("/api/transaction/pdf")
                        .header("X-Customer-Id", "1").header("X-Transaction-Id", "NOPE"))
                .andExpect(status().isNotFound());
    }
}
