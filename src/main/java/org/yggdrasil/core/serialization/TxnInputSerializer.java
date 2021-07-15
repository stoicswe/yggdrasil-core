package org.yggdrasil.core.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.util.SerializationUtils;
import org.yggdrasil.core.ledger.transaction.TransactionInput;

import java.io.IOException;
import java.util.Base64;

public class TxnInputSerializer extends JsonSerializer<TransactionInput[]> {

    @Override
    public void serialize(TransactionInput[] transactionInputs, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(transactionInputs != null && transactionInputs.length > 0) {
            jsonGenerator.writeString(Base64.getEncoder().encodeToString(SerializationUtils.serialize(transactionInputs)));
        } else {
            jsonGenerator.writeNull();
        }
    }
}
