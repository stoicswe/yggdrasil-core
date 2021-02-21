package org.nathanielbunch.ssblockchain.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.nathanielbunch.ssblockchain.core.ledger.SSTransaction;
import org.nathanielbunch.ssblockchain.node.controller.SSRestController;
import org.nathanielbunch.ssblockchain.node.service.SSBlockChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SSRestController.class)
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SSBlockChainService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testTransactionSubmit() throws Exception {
        mockMvc.perform(put("/transaction")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"origin\":\"TestAddr\",\"destination\":\"TestAddr2\",\"amount\": 0.12345,\"note\":\"Test\"}"))
        .andExpect(status().isCreated());
    }

    @Test
    void testGetTransaction() throws Exception {
        when(service.getTransaction()).thenReturn(this.buildTestTransaction());

        MvcResult result = mockMvc.perform(get("/transaction")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        assertThat(responseBody).contains("\"origin\":\"TestAddress\"");
        assertThat(responseBody).contains("\"destination\":\"TestDestination\"");
        assertThat(responseBody).contains("\"amount\":0.1234");
        assertThat(responseBody).contains("\"note\":\"Test transaction\"");
    }

    private SSTransaction buildTestTransaction() throws NoSuchAlgorithmException {
        return SSTransaction.TBuilder.newSSTransactionBuilder()
                .setOrigin("TestAddress")
                .setDestination("TestDestination")
                .setValue(new BigDecimal("0.1234"))
                .setNote("Test transaction")
                .build();
    }

}
