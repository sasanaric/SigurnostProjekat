package com.example.sigurnostprojekat.crypto;
import com.example.sigurnostprojekat.models.request.MessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class Crypto {
    private static final SecretKey aesKey;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        aesKey = generateAESKey();
    }

    private static SecretKey generateAESKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // for AES-256
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }

    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);  // Key size
        return keyPairGenerator.generateKeyPair();
    }

    public static String keyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey stringToPublicKey(String pubKey) throws Exception {
        byte[] byteKey = Base64.getDecoder().decode(pubKey.getBytes());
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(X509publicKey);
    }

    public static PrivateKey stringToPrivateKey(String privKey) throws Exception {
        byte[] byteKey = Base64.getDecoder().decode(privKey.getBytes());
        PKCS8EncodedKeySpec PKCS8privateKey = new PKCS8EncodedKeySpec(byteKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(PKCS8privateKey);
    }

    public static String serializeAndEncryptWithAES(MessageRequest message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            return encryptWithAES(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt and serialize message", e);
        }
    }

    public static MessageRequest decryptAndDeserializeWithAES(String ciphertext) {
        try {
            String decryptedJson = decryptWithAES(ciphertext);
            return objectMapper.readValue(decryptedJson, MessageRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt and deserialize message", e);
        }
    }

    public static String encryptWithAES(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] iv = cipher.getIV();  // Get generated IV
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to encrypted output
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt message", e);
        }
    }


    public static String decryptWithAES(String ciphertext) {
        try {
            byte[] combined = Base64.getDecoder().decode(ciphertext);

            // Extract IV from combined array
            byte[] iv = Arrays.copyOfRange(combined, 0, 12);  // Assuming a 12-byte IV for GCM
            byte[] encryptedBytes = Arrays.copyOfRange(combined, 12, combined.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);  // 128-bit authentication tag size
            cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);

            return new String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt message", e);
        }
    }

    public static List<String> splitMessageIntoParts(String encryptedMessage) {
        Random rand = new Random();
        int n = rand.nextInt(9) + 3;

        int partLength = encryptedMessage.length() / n;
        List<String> parts = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                parts.add(encryptedMessage.substring(i * partLength));
            } else {
                parts.add(encryptedMessage.substring(i * partLength, (i + 1) * partLength));
            }
        }

        return parts;
    }
    public static String signMessageRequest(String privKeyString, MessageRequest requestMessage) {
        try {
            PrivateKey privateKey = stringToPrivateKey(privKeyString);

            String messageString = objectMapper.writeValueAsString(requestMessage);
            byte[] bytes = messageString.getBytes(StandardCharsets.UTF_8);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(bytes);
            byte[] signedBytes = signature.sign();

            return Base64.getEncoder().encodeToString(signedBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to sign MessageRequest", e);
        }
    }
    public static boolean verifyMessageSignature(String pubKeyString, MessageRequest requestMessage, String signatureString) {
        try {
            PublicKey publicKey = stringToPublicKey(pubKeyString);

            String messageString = objectMapper.writeValueAsString(requestMessage);
            byte[] bytes = messageString.getBytes(StandardCharsets.UTF_8);

            byte[] signatureBytes = Base64.getDecoder().decode(signatureString);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(bytes);

            return signature.verify(signatureBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify MessageRequest signature", e);
        }
    }

}
