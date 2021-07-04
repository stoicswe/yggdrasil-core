package org.yggdrasil.core.utils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * This component serves as the public and private key pair generator
 * for different use cases in the Blockchain.
 *
 * @since 0.0.5
 * @author nathanielbunch
 */
@Component
public class CryptoKeyGenerator {

    private static final String _KEY_PAIR_ALGORITHM = "EC";
    private static final String _SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String _SIGNATURE_ALGORITHM = "SHA256withECDSA";
    private static final String _EC_SPECIFICATION = "secp256r1";

    private ECGenParameterSpec keyGenSpecification;
    private KeyPairGenerator keyGenerator;
    private SecureRandom secureRandom;

    public CryptoKeyGenerator() throws NoSuchAlgorithmException, NoSuchProviderException {
    }

    @PostConstruct
    public void init() throws NoSuchProviderException, NoSuchAlgorithmException {
        this.keyGenerator = KeyPairGenerator.getInstance(_KEY_PAIR_ALGORITHM);
        this.secureRandom = SecureRandom.getInstance(_SECURE_RANDOM_ALGORITHM);
        this.keyGenSpecification = new ECGenParameterSpec(_EC_SPECIFICATION);
    }

    public static String getKeyPairAlgorithm() {
        return _KEY_PAIR_ALGORITHM;
    }

    public static String getSecureRandomAlgorithm() {
        return _SECURE_RANDOM_ALGORITHM;
    }

    public static String getSignatureAlgorithm() {
        return _SIGNATURE_ALGORITHM;
    }

    /**
     * Generates a new public/private key pair.
     *
     * @return
     */
    public KeyPair generatePublicPrivateKeys() throws InvalidAlgorithmParameterException {
        keyGenerator.initialize(keyGenSpecification, secureRandom);
        return keyGenerator.generateKeyPair();
    }

    public static PublicKey readPublicKeyFromBytes(byte[] encPublicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(_KEY_PAIR_ALGORITHM);
        return keyFactory.generatePublic(pubKeySpec);
    }

    public static PrivateKey readPrivateKeyFromBytes(byte[] encPublicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(_KEY_PAIR_ALGORITHM);
        return keyFactory.generatePrivate(privateKeySpec);
    }

}
