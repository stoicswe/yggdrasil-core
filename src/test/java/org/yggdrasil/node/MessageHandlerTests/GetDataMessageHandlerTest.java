package org.yggdrasil.node.MessageHandlerTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.node.network.Node;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.enums.GetDataType;
import org.yggdrasil.node.network.messages.enums.HeaderType;
import org.yggdrasil.node.network.messages.handlers.GetDataMessageHandler;
import org.yggdrasil.node.network.messages.payloads.GetDataMessage;
import org.yggdrasil.node.network.messages.payloads.HeaderMessage;
import org.yggdrasil.node.network.messages.payloads.HeaderPayload;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GetDataMessageHandlerTest {

    @MockBean
    private NodeConfig nodeConfig;
    @MockBean
    private Node node;
    @MockBean
    private Blockchain blockchain;
    @MockBean
    private Mempool mempool;
    @MockBean
    private MessagePool messagePool;
    @Autowired
    private GetDataMessageHandler getDataMessageHandler;

    private byte[] blockHash;
    private Block block;
    private byte[] txnHash;
    private Transaction txn;

    @Before
    public void setTestData() throws Exception {

        this.txn = Transaction.Builder.Builder()
                .setDestination("0890ba439df33d9facc63ce73b8177a239cd8be2")
                .setOrigin("6ad28d3fda4e10bdc0aaf7112f7818e181defa7e")
                .setSignature((byte[]) null)
                .setNote("This is a test txn")
                .setValue(BigDecimal.valueOf(0.1234567))
                .build();
        this.block = Block.Builder.newBuilder()
                .setPreviousBlock(new byte[0])
                .setData(Collections.singletonList(txn))
                .build();
        this.blockHash = this.block.getBlockHash();
        this.txnHash = this.txn.getTxnHash();

        Mockito.when(blockchain.getBlock(any())).thenReturn(Optional.of(block));
        Mockito.when(mempool.peekTransaction(any())).thenReturn(List.of(txn));
    }

    // Positive Tests
    @Test
    public void testHandleGetBlock() throws Exception {

        GetDataMessage getDataMessage = GetDataMessage.Builder.newBuilder()
                .setHashCount(1)
                .setDataType(GetDataType.BLOCK)
                .setObjectHashes(new byte[0][])
                .setVersion(-1)
                .setStopHash(this.blockHash)
                .build();

        MessagePayload returnMessagePayload = this.getDataMessageHandler.handleMessagePayload(getDataMessage, null);
        assertThat(returnMessagePayload instanceof HeaderMessage);
        HeaderMessage headerMessage = (HeaderMessage) returnMessagePayload;
        assertThat(headerMessage.getHeaderCount() == 1);
        assertThat(HeaderType.TXN_HEADER.equals(HeaderType.getByValue(headerMessage.getHeaderType())));
        HeaderPayload headerPayload = headerMessage.getHeaders()[0];
        assertThat(this.txn.compareTxnHash(headerPayload.getHash()));
        assertThat(this.txn.getIndex().toString().contentEquals(String.valueOf(headerPayload.getIndex())));
        assertThat(headerPayload.getNonce() == txn.getNonce());
        assertThat(headerPayload.getTime() == txn.getTimestamp().toEpochSecond());
    }

    @Test
    public void testHandleGetBlockchain() {

        byte[][] blockHash = new byte[1][];
        blockHash[0] = this.blockHash;

        GetDataMessage getDataMessage = GetDataMessage.Builder.newBuilder()
                .setHashCount(1)
                .setDataType(GetDataType.BLOCKCHAIN)
                .setObjectHashes(blockHash)
                .setVersion(-1)
                .setStopHash(new byte[0])
                .build();

        MessagePayload returnMessagePayload = this.getDataMessageHandler.handleMessagePayload(getDataMessage, null);
        assertThat(returnMessagePayload instanceof HeaderMessage);
        HeaderMessage headerMessage = (HeaderMessage) returnMessagePayload;
        assertThat(headerMessage.getHeaderCount() == 1);
        assertThat(HeaderType.BLOCK_HEADER.equals(HeaderType.getByValue(headerMessage.getHeaderType())));
        HeaderPayload headerPayload = headerMessage.getHeaders()[0];
        assertThat(this.block.getIndex().toString().contentEquals(String.valueOf(headerPayload.getIndex())));
        assertThat(this.block.compareBlockHash(headerPayload.getHash()));
        assertThat(headerPayload.getTransactionCount() == 1);
        assertThat(this.block.getTimestamp().toEpochSecond() == headerPayload.getTime());
        assertThat(this.block.getNonce() == headerPayload.getNonce());
        assertThat(headerPayload.getPrevHash().length == 0);
    }

    @Test
    public void testHandleGetTransaction() {

    }

    @Test
    public void testHandleGetMempool() {

    }

    // Negative Tests
    @Test
    public void testGetTooManyBlocks() {

    }

    @Test
    public void testReportedCountHashMismatch() {

    }

    @Test
    public void testGetTooManyTransactions() {

    }

}
