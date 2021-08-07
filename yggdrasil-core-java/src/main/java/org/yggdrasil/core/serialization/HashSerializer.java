package org.yggdrasil.core.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.core.utils.CryptoHasher;

import java.io.IOException;

public class HashSerializer extends JsonSerializer<byte[]> {

    Logger logger = LoggerFactory.getLogger(HashSerializer.class);

    @Override
    public void serialize(byte[] hash, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (hash != null && hash.length > 0) {
            jsonGenerator.writeString(CryptoHasher.humanReadableHash(hash));
        } else {
            jsonGenerator.writeNull();
        }
    }
}
