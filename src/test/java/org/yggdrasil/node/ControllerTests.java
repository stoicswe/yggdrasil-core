package org.yggdrasil.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.node.controller.BlockchainController;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.service.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BlockchainController.class)
@RunWith(MockitoJUnitRunner.class)
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Blockchain blockchain;

    @MockBean
    private Mempool mempool;

    @MockBean
    private Messenger messenger;

    @MockBean
    private NodeConfig nodeConfig;

    @MockBean
    private BlockchainService service;

    @Autowired
    private ObjectMapper objectMapper;

    //write the tests

}
