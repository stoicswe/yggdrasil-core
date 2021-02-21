package org.nathanielbunch.ssblockchain.core.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.nathanielbunch.ssblockchain.core.ledger.SSTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

public class SSTransactionDeserializer extends JsonDeserializer<SSTransaction> {

    Logger logger = LoggerFactory.getLogger(SSTransactionDeserializer.class);

    @Override
    public SSTransaction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String origin = node.get("origin").asText();
        String destination = node.get("destination").asText();
        BigDecimal amount = new BigDecimal(node.get("amount").asText());
        String note = node.get("note").asText();
        try {
            return SSTransaction.TBuilder.newSSTransactionBuilder()
                    .setOrigin(origin)
                    .setDestination(destination)
                    .setValue(amount)
                    .setNote(note)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Deserialization of SSTransaction failed with: {}", e.toString());
        }

        return null;

    }
}
