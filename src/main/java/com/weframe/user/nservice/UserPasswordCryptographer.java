package com.weframe.user.nservice;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class UserPasswordCryptographer {

    private final int HASH_ITERATIONS;
    private final SecretKeyFactory secretKeyFactory;

    public UserPasswordCryptographer(final int hashIterations) throws GeneralSecurityException {
        this.HASH_ITERATIONS = hashIterations;
        this.secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    }

    public String generateStoringPasswordHash(final String password) throws GeneralSecurityException {
        int iterations = HASH_ITERATIONS;
        char[] chars = password.toCharArray();
        byte[] salt = generateSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);

        byte[] hash = secretKeyFactory.generateSecret(spec).getEncoded();
        return iterations + ":" + bytesToHex(salt) + ":" + bytesToHex(hash);
    }

    public boolean isValidPassword(String originalPassword, String storedPassword) throws GeneralSecurityException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = hexToBytes(parts[1]);
        byte[] hash = hexToBytes(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = skf.generateSecret(spec).getEncoded();
            int diff = hash.length ^ testHash.length;
            for(int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new GeneralSecurityException(e);
        }
    }

    private byte[] generateSalt()
    {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String bytesToHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    private byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++) {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

}
