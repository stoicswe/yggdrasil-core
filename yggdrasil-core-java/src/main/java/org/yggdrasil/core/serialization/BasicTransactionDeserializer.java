package org.yggdrasil.core.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.json.JsonParseException;
import org.yggdrasil.core.ledger.transaction.BasicTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

/**
 * Provides the necessary functionality to correctly deserialize incoming
 * transactions by the rest endpoint.
 *
 * @since 0.0.3
 * @author nathanielbunch
 */
public class BasicTransactionDeserializer extends JsonDeserializer<BasicTransaction> {

    Logger logger = LoggerFactory.getLogger(BasicTransactionDeserializer.class);

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
    public BasicTransaction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonParseException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String origin = node.get("origin").asText();
        String destination = node.get("destination").asText();
        BigDecimal value = new BigDecimal(node.get("value").asText());

        try {
            return BasicTransaction.Builder.builder()
                    .setOrigin(origin)
                    .setDestination(destination)
                    .setValue(value)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            logger.debug("Error while deserializing transaction: {}", e.getMessage());
            throw new JsonParseException(e);
        }
    }
}
