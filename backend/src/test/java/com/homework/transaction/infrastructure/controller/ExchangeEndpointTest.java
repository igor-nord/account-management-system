package com.homework.transaction.infrastructure.controller;

import com.homework.transaction.port.ExternalLoggingPort;
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

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class ExchangeEndpointTest {

    @Autowired
    private WebApplicationContext context;
    @MockitoBean
    private ExternalLoggingPort externalLogging;

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void exchangeMovesMoneyBetweenOwnAccounts() throws Exception {
        doNothing().when(externalLogging).logBeforeDebit(ArgumentMatchers.anyString());
        mockMvc().perform(post("/api/account/credit")
                .header("X-Customer-Id", "1").header("X-Account-Id", "1000011")
                .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"100.00\"}"));

        mockMvc().perform(post("/api/exchange")
                        .header("X-Customer-Id", "1")
                        .header("X-Source-Account-Id", "1000011")
                        .header("X-Target-Account-Id", "1000012")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"100.00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legs.length()").value(2));

        mockMvc().perform(get("/api/account").header("X-Customer-Id", "1").header("X-Account-Id", "1000011"))
                .andExpect(jsonPath("$.balance").value("0.00"));
        mockMvc().perform(get("/api/account").header("X-Customer-Id", "1").header("X-Account-Id", "1000012"))
                .andExpect(jsonPath("$.balance").value("108.00"));
    }

    @Test
    void sameAccountExchangeReturns400() throws Exception {
        doNothing().when(externalLogging).logBeforeDebit(ArgumentMatchers.anyString());
        mockMvc().perform(post("/api/exchange")
                        .header("X-Customer-Id", "1")
                        .header("X-Source-Account-Id", "1000011")
                        .header("X-Target-Account-Id", "1000011")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":\"1.00\"}"))
                .andExpect(status().isBadRequest());
    }
}
