package org.yggdrasil.node.MessageHandlerTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.Node;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.enums.GetDataType;
import org.yggdrasil.node.network.messages.enums.HeaderType;
import org.yggdrasil.node.network.messages.handlers.GetDataMessageHandler;
import org.yggdrasil.node.network.messages.payloads.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

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
    private List<Transaction> txns = new ArrayList<>();

    @Before
    public void setTestData() throws Exception {

        this.txn = Transaction.Builder.Builder()
                .setDestination("0890ba439df33d9facc63ce73b8177a239cd8be2")
                .setOrigin("6ad28d3fda4e10bdc0aaf7112f7818e181defa7e")
                .setSignature(new byte[0])
                .setNote("This is a test txn")
                .setValue(BigDecimal.valueOf(0.1234567))
                .build();
        txns = new ArrayList<>();
        txns.add(txn);
        this.block = Block.Builder.newBuilder()
                .setPreviousBlock(new byte[0])
                .setData(txns)
                .build();
        this.blockHash = this.block.getBlockHash();
        this.txnHash = this.txn.getTxnHash();

    }

    // Positive Tests
    /*
    @Test
    public void testHandleGetBlock() throws Exception {
        Mockito.when(blockchain.getBlock(any())).thenReturn(Optional.of(block));

        GetDataMessage getDataMessage = GetDataMessage.Builder.newBuilder()
                .setHashCount(0)
                .setDataType(GetDataType.BLOCK)
                .setObjectHashes(new byte[0][])
                .setVersion(-1)
                .setStopHash(this.blockHash)
                .build();

        MessagePayload returnMessagePayload = this.getDataMessageHandler.handleMessagePayload(getDataMessage, null);
        assertThat(returnMessagePayload instanceof BlockchainMessage);
        BlockchainMessage blockchainMessage = (BlockchainMessage) returnMessagePayload;
        assertThat(blockchainMessage.getHeaderCount() == 1);
        assertThat(HeaderType.TXN_HEADER.equals(HeaderType.getByValue(blockchainMessage.getHeaderType())));
        TransactionHeaderPayload blockHeaderPayload = (TransactionHeaderPayload) blockchainMessage.getHeaders()[0];
        assertThat(this.txn.compareTxnHash(blockHeaderPayload.getHash()));
        assertThat(this.txn.getIndex().toString().contentEquals(String.valueOf(blockHeaderPayload.getIndex())));
        assertThat(blockHeaderPayload.getNonce() == txn.getNonce());
        assertThat(blockHeaderPayload.getTime() == txn.getTimestamp().toEpochSecond());
    }
    */

    @Test
    public void testHandleGetBlockchain() {
        Mockito.when(blockchain.getBlock(any())).thenReturn(Optional.of(block));

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
        assertThat(returnMessagePayload instanceof BlockchainMessage);
        BlockchainMessage blockchainMessage = (BlockchainMessage) returnMessagePayload;
        assertThat(blockchainMessage.getHeaderCount() == 1);
        assertThat(HeaderType.BLOCK_HEADER.equals(HeaderType.getByValue(blockchainMessage.getHeaderType())));
        BlockHeaderPayload blockHeaderPayload = (BlockHeaderPayload) blockchainMessage.getHeaders()[0];
        assertThat(this.block.getIndex().toString().contentEquals(String.valueOf(blockHeaderPayload.getIndex())));
        assertThat(this.block.compareBlockHash(blockHeaderPayload.getHash()));
        assertThat(blockHeaderPayload.getTransactionCount() == 1);
        assertThat(this.block.getTimestamp().toEpochSecond() == blockHeaderPayload.getTimestamp());
        assertThat(this.block.getNonce() == blockHeaderPayload.getNonce());
        assertThat(blockHeaderPayload.getPrevHash().length == 0);
    }

    @Test
    public void testHandleGetTransaction() {
        Mockito.when(blockchain.getBlock(any())).thenReturn(Optional.of(block));

        GetDataMessage getDataMessage = GetDataMessage.Builder.newBuilder()
                .setHashCount(0)
                .setDataType(GetDataType.TRANSACTION)
                .setObjectHashes(new byte[0][])
                .setVersion(-1)
                .setStopHash(this.txnHash)
                .build();

        MessagePayload returnMessagePayload = this.getDataMessageHandler.handleMessagePayload(getDataMessage, null);
        assertThat(returnMessagePayload instanceof TransactionMessage);
        TransactionMessage txnMessage = (TransactionMessage) returnMessagePayload;
        assertThat(txnMessage.getTxns().length == 1);
        assertThat(txnMessage.getTxnCount() == 1);
        TransactionPayload txnPayload = txnMessage.getTxns()[0];
        assertThat(this.txn.getIndex().toString().contentEquals(String.valueOf(txnPayload.getIndex())));
        assertThat(this.txn.getTimestamp().toEpochSecond() == txnPayload.getTimestamp());
        assertThat(this.txn.compareTxnHash(txnPayload.getTransactionHash()));
        assertThat(this.block.compareBlockHash(txnPayload.getBlockHash()));
        assertThat(this.txn.getValue().compareTo(txnPayload.getValue()) == 0);
        assertThat(CryptoHasher.compareHashes(this.txn.getDestination(), txnPayload.getDestinationAddress()));
        assertThat(CryptoHasher.compareHashes(this.txn.getOrigin(), txnPayload.getOriginAddress()));
        assertThat(this.txn.getNonce() == txnPayload.getNonce());
        assertThat(txnPayload.getSignature().length == 0);

    }

    @Test
    public void testHandleGetMempool() {
        Mockito.when(mempool.peekTransaction(anyInt())).thenReturn(txns);

        GetDataMessage getDataMessage = GetDataMessage.Builder.newBuilder()
                .setDataType(GetDataType.MEMPOOL)
                .setHashCount(1)
                .setStopHash(new byte[0])
                .setObjectHashes(new byte[0][])
                .setVersion(-1)
                .build();

        MessagePayload returnMessagePayload = this.getDataMessageHandler.handleMessagePayload(getDataMessage, null);
        assertThat(returnMessagePayload instanceof TransactionMessage);
        TransactionMessage txnMessage = (TransactionMessage) returnMessagePayload;
        assertThat(txnMessage.getTxns().length == 1);
        assertThat(txnMessage.getTxnCount() == 1);
        TransactionPayload txnPayload = txnMessage.getTxns()[0];
        assertThat(this.txn.getIndex().toString().contentEquals(String.valueOf(txnPayload.getIndex())));
        assertThat(this.txn.getTimestamp().toEpochSecond() == txnPayload.getTimestamp());
        assertThat(this.txn.compareTxnHash(txnPayload.getTransactionHash()));
        assertThat(this.block.compareBlockHash(txnPayload.getBlockHash()));
        assertThat(this.txn.getValue().compareTo(txnPayload.getValue()) == 0);
        assertThat(CryptoHasher.compareHashes(this.txn.getDestination(), txnPayload.getDestinationAddress()));
        assertThat(CryptoHasher.compareHashes(this.txn.getOrigin(), txnPayload.getOriginAddress()));
        assertThat(this.txn.getNonce() == txnPayload.getNonce());
        assertThat(txnPayload.getSignature().length == 0);
    }

    // Negative Tests
    @Test(expected = InvalidMessageException.class)
    public void testGetTooManyBlocks() {
        GetDataMessage getDataMessage = GetDataMessage.Builder.newBuilder()
                .setHashCount(1)
                .setDataType(GetDataType.BLOCK)
                .setObjectHashes(new byte[1][])
                .setVersion(-1)
                .setStopHash(this.blockHash)
                .build();
        this.getDataMessageHandler.handleMessagePayload(getDataMessage, null);
    }

    @Test(expected = InvalidMessageException.class)
    public void testReportedCountHashMismatch() {
        GetDataMessage getDataMessage = GetDataMessage.Builder.newBuilder()
                .setHashCount(1)
                .setDataType(GetDataType.BLOCKCHAIN)
                .setObjectHashes(new byte[0][])
                .setVersion(-1)
                .setStopHash(new byte[0])
                .build();
        this.getDataMessageHandler.handleMessagePayload(getDataMessage, null);
    }

    @Test(expected = InvalidMessageException.class)
    public void testGetTooManyTransactions() {
        GetDataMessage getDataMessage = GetDataMessage.Builder.newBuilder()
                .setHashCount(1)
                .setDataType(GetDataType.TRANSACTION)
                .setObjectHashes(new byte[1][])
                .setVersion(-1)
                .setStopHash(new byte[0])
                .build();
        this.getDataMessageHandler.handleMessagePayload(getDataMessage, null);
    }

}
