package org.yggdrasil.core.utils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.*;

/**
 * This component serves as the public and private key pair generator
 * for different use cases in the Blockchain.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
@Component
public class CryptoKeyGenerator {

    private static final String _KEY_PAIR_ALGORITHM = "DSA";
    private static final String _KEY_PAIR_PROVIDER = "SUN";
    private static final String _SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String _SECURE_RANDOM_PROVIDER = "SUN";
    private static final int _KEY_LENGTH = 1024;

    private KeyPairGenerator keyGenerator;
    private SecureRandom secureRandom;

    @PostConstruct
    public void init() throws NoSuchProviderException, NoSuchAlgorithmException {
        this.keyGenerator = KeyPairGenerator.getInstance(_KEY_PAIR_ALGORITHM, _KEY_PAIR_PROVIDER);
        this.secureRandom = SecureRandom.getInstance(_SECURE_RANDOM_ALGORITHM, _SECURE_RANDOM_PROVIDER);
    }

    /**
     * Generates a new public/private key pair.
     *
     * @return
     */
    public KeyPair generatePublicPrivateKeys() {
        keyGenerator.initialize(_KEY_LENGTH, secureRandom);
        return keyGenerator.generateKeyPair();
    }

}
