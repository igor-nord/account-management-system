package com.homework.transaction.controller;

import com.homework.transaction.exception.ExternalLoggingException;
import com.homework.transaction.integration.ExternalLoggingClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class DebitEndpointTest {

    @Autowired
    private WebApplicationContext context;
    @MockitoBean
    private ExternalLoggingClient externalLogging;

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    private void credit(String accountId, String amount) throws Exception {
        mockMvc().perform(post("/api/account/credit")
                .header("X-Username", "demo").header("X-Account-Id", accountId)
                .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"" + amount + "\"}"));
    }

    @Test
    void debitAfterCreditLowersBalance() throws Exception {
        doNothing().when(externalLogging).logBeforeDebit(ArgumentMatchers.anyString());
        credit("1000011", "100.00");

        mockMvc().perform(post("/api/account/debit")
                        .header("X-Username", "demo").header("X-Account-Id", "1000011")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"30.00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legs[0].type").value("DEBIT"));

        mockMvc().perform(get("/api/account").header("X-Username", "demo").header("X-Account-Id", "1000011"))
                .andExpect(jsonPath("$.balance").value("70.00"));
    }

    @Test
    void insufficientFundsReturns422() throws Exception {
        doNothing().when(externalLogging).logBeforeDebit(ArgumentMatchers.anyString());
        mockMvc().perform(post("/api/account/debit")
                        .header("X-Username", "demo").header("X-Account-Id", "1000011")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"5.00\"}"))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void debitNonEuroAccountReturns422() throws Exception {
        credit("1000012", "50.00");

        mockMvc().perform(post("/api/account/debit")
                        .header("X-Username", "demo").header("X-Account-Id", "1000012")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"10.00\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail", containsString("EUR")));
    }

    @Test
    void externalLoggingFailureReturns502AndWritesNothing() throws Exception {
        credit("1000011", "40.00");
        doThrow(new ExternalLoggingException("down", null))
                .when(externalLogging).logBeforeDebit(ArgumentMatchers.anyString());

        mockMvc().perform(post("/api/account/debit")
                        .header("X-Username", "demo").header("X-Account-Id", "1000011")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"10.00\"}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.detail").value("Internal error. Please try again later or contact our support."));

        mockMvc().perform(get("/api/account").header("X-Username", "demo").header("X-Account-Id", "1000011"))
                .andExpect(jsonPath("$.balance").value("40.00"));
    }
}
