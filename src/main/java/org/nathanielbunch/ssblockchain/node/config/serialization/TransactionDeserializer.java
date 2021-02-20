package org.nathanielbunch.ssblockchain.node.config.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.nathanielbunch.ssblockchain.core.ledger.SSTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionDeserializer extends StdDeserializer<SSTransaction> {

    Logger logger = LoggerFactory.getLogger(TransactionDeserializer.class);

    public TransactionDeserializer(){
        super(SSTransaction.class);
    }

    @Override
    public SSTransaction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        TreeNode tn = jsonParser.readValueAsTree();
        UUID index = null;
        LocalDateTime timestamp = null;
        String origin = null;
        String destination = null;
        Double amount = null;
        String note = null;
        byte[] transactionHash;

        if(tn.get("index") != null) {
            index = UUID.fromString(tn.get("index").toString());
        }

        if(tn.get("timestamp") != null) {
            timestamp = LocalDateTime.parse(tn.get("timestamp").toString());
        }

        if(tn.get("origin") != null){
            origin = tn.get("origin").toString();
        }

        if(tn.get("destination") != null){
            destination = tn.get("destination").toString();
        }

        if(tn.get("amount") != null){
            amount = Double.parseDouble(tn.get("amount").toString());
        }

        if(tn.get("note") != null){
            note = tn.get("note").toString();
        }

        if(tn.get("transactionHash") != null){
            transactionHash = tn.get("transactionHash").traverse().getBinaryValue();
        }

        try {
            return SSTransaction.TBuilder.newSSTransactionBuilder()
                    .setIndex(index)
                    .setTimestamp(timestamp)
                    .setOrigin(origin)
                    .setDestination(destination)
                    .setAmountValue(amount)
                    .setNote(note)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Transaction deserialization failed: {}", e.toString());
        }

        return null;
    }
}
