package org.yggdrasil.core.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.util.SerializationUtils;
import org.yggdrasil.core.ledger.transaction.TransactionOutput;

import java.io.IOException;
import java.util.Base64;

public class TxnOutputSerializer extends JsonSerializer<TransactionOutput[]> {

    @Override
    public void serialize(TransactionOutput[] transactionOutputs, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (transactionOutputs != null && transactionOutputs.length > 0) {
            jsonGenerator.writeString(Base64.getEncoder().encodeToString(SerializationUtils.serialize(transactionOutputs)));
        } else {
            jsonGenerator.writeNull();
        }
    }
}
