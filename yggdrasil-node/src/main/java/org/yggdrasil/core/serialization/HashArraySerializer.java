package org.yggdrasil.core.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.yggdrasil.core.utils.CryptoHasher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HashArraySerializer extends JsonSerializer<byte[][]> {

    @Override
    public void serialize(byte[][] hashes, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (hashes != null && hashes.length > 0) {
            List<String> readableHashes = new ArrayList<>();
            for(byte[] hash : hashes) {
                readableHashes.add(CryptoHasher.humanReadableHash(hash));
            }
            jsonGenerator.writeArray(readableHashes.toArray(String[]::new), 0, readableHashes.size());
        } else {
            jsonGenerator.writeNull();
        }
    }

}
