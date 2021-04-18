package org.yggdrasil.core.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

/**
 * Provides the necessary functionality to correctly deserialize incoming
 * transactions by the rest endpoint.
 *
 * @since 0.0.3
 * @author nathanielbunch
 */
public class TransactionDeserializer extends JsonDeserializer<Transaction> {

    Logger logger = LoggerFactory.getLogger(TransactionDeserializer.class);

    /**
     * Accepts a JSON payload in the form of a JsonParser then pulls values
     * and passes to the SSTransaction builder to create a new transaction
     * object in memory.
     *
     * @param jsonParser
     * @param deserializationContext
     * @return
     * @throws IOException
     * @throws JsonProcessingException
     */
    @Override
    public Transaction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String origin = node.get("origin").asText();
        String destination = node.get("destination").asText();
        BigDecimal value = new BigDecimal(node.get("value").asText());
        String note = node.get("note").asText();

        try {
            return Transaction.Builder.Builder()
                    .setOrigin(origin)
                    .setDestination(destination)
                    .setValue(value)
                    .setNote(note)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Deserialization of txn failed with: {}", e.toString());
        }

        return null;

    }
}
