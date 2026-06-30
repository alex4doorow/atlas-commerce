package com.afa.atlas.crypto.service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesGcmCryptoService implements CryptoService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int IV_LENGTH_BYTES = 12;

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public AesGcmCryptoService(final String secret) {
        final byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("AES-256 secret must be exactly 32 bytes");
        }

        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    @Override
    public String encrypt(final String value) {
        if (value == null) {
            return null;
        }

        try {
            final byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            final GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            final byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            final byte[] result = new byte[iv.length + encrypted.length];

            // Prepend IV to ciphertext so it can be extracted during decryption.
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(result);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Failed to encrypt value", ex);
        }
    }

    @Override
    public String decrypt(final String encryptedValue) {
        if (encryptedValue == null) {
            return null;
        }

        try {
            final byte[] decoded = Base64.getDecoder().decode(encryptedValue);

            final byte[] iv = new byte[IV_LENGTH_BYTES];
            final byte[] encrypted = new byte[decoded.length - IV_LENGTH_BYTES];

            System.arraycopy(decoded, 0, iv, 0, iv.length);
            System.arraycopy(decoded, IV_LENGTH_BYTES, encrypted, 0, encrypted.length);

            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            final GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            final byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new IllegalStateException("Failed to decrypt value", ex);
        }
    }
}