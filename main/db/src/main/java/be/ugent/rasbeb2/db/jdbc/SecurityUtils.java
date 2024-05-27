/*
 * SecurityUtils.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import com.google.common.io.BaseEncoding;
import lombok.SneakyThrows;

/**
 * Some helper functions for password hashing and checking
 *
 * @see "https://www.baeldung.com/java-password-hashing"
 */
public final class SecurityUtils {

    private static final SecureRandom RG = new SecureRandom();
    private static final SecretKeyFactory SKF;

    static {
        try {
            SKF = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] hexToBytes(String str) {
        return BaseEncoding.base16().decode(str.toUpperCase());
    }

    /**
     * Convert byte array to hex.
     */
    private static String bytesToHex(byte[] bytes) {
        return BaseEncoding.base16().encode(bytes);
    }

    /**
     * Create a random salt and hash for the given password
     */
    @SneakyThrows(InvalidKeySpecException.class)
    public static String[] saltAndHash(String password) {
        byte[] salt = new byte[16];
        RG.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        byte[] hash = SKF.generateSecret(spec).getEncoded();
        return new String[]{bytesToHex(salt), bytesToHex(hash)};
    }

    /**
     * Check whether the given password correctly corresponds to salt and hash.
     */
    @SneakyThrows(InvalidKeySpecException.class)
    public static boolean isCorrectPassword(String salt, String hash, String password) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), hexToBytes(salt), 65536, 128);
            byte[] computedHash = SKF.generateSecret(spec).getEncoded();
            return Arrays.equals(hexToBytes(hash), computedHash);
        } catch (IllegalArgumentException ex) {
            // e.g., when salt or hash is "NONE"
            return false;
        }
    }

}
