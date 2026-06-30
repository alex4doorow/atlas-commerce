package com.afa.atlas.crypto.service;

public interface CryptoService {

    String encrypt(String value);

    String decrypt(String encrypted);
}
