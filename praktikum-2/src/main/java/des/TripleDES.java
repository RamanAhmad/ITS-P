package des;

import javax.crypto.*;
import javax.crypto.spec.*;

public class TripleDES {

    private final DES des_1;
    private final DES des_2;
    private final DES des_3;

    /**
     * Constructor initializes the three DES instances with the provided key parts.
     * Each key part must be 8 bytes long.
     *
     * @param keyPart1 The first 8-byte key.
     * @param keyPart2 The second 8-byte key.
     * @param keyPart3 The third 8-byte key.
     */
    public TripleDES(byte[] keyPart1, byte[] keyPart2, byte[] keyPart3) {
        if (keyPart1.length != 8 || keyPart2.length != 8 || keyPart3.length != 8) {
            throw new IllegalArgumentException("keyParts must be 8 bytes.");
        }

        this.des_1 = new DES(keyPart1);
        this.des_2 = new DES(keyPart2);
        this.des_3 = new DES(keyPart3);
    }

    /**
     * Encrypts the given plaintext bytes using Triple DES encryption.
     *
     * @param plaintextBytes The plaintext bytes to encrypt.
     * @return The encrypted byte array.
     */
    public byte[] encryptBytes(byte[] plaintextBytes) {
        byte[] resultBytes = new byte[8];
        this.des_1.encrypt(plaintextBytes, 0, resultBytes, 0);  // First DES encryption
        this.des_2.decrypt(resultBytes, 0, resultBytes, 0);     // Second DES decryption
        this.des_3.encrypt(resultBytes, 0, resultBytes, 0);     // Third DES encryption

        return resultBytes;
    }

    /**
     * Decrypts the given ciphertext bytes using Triple DES decryption.
     *
     * @param chiffreBytes The ciphertext bytes to decrypt.
     * @return The decrypted byte array.
     */
    public byte[] decryptBytes(byte[] chiffreBytes) {
        byte[] resultBytes = new byte[8];
        this.des_3.decrypt(chiffreBytes, 0, resultBytes, 0);    // First DES decryption
        this.des_2.encrypt(resultBytes, 0, resultBytes, 0);     // Second DES encryption
        this.des_1.decrypt(resultBytes, 0, resultBytes, 0);     // Third DES decryption

        return resultBytes;
    }

    private String byteArraytoHexString(byte[] byteArray) {
        StringBuilder ret = new StringBuilder();
        for (byte b : byteArray) {
            ret.append(String.format("%02x", b));
            ret.append(" ");
        }
        return ret.toString();
    }

    public static void main(String[] args) {
        /* Testcode */
        TripleDES cipher = new TripleDES("qwertzui".getBytes(), "asdfghjk".getBytes(), "yxcvbnm,".getBytes());

        byte[] plain = "12345678".getBytes();
        byte[] chiffre = cipher.encryptBytes(plain);
        System.out.println(" Encrypted: " + cipher.byteArraytoHexString(plain) + " to: " + cipher.byteArraytoHexString(chiffre));

        byte[] plainNew = cipher.decryptBytes(chiffre);
        System.out.println(" Decrypted: " + cipher.byteArraytoHexString(plainNew));

        if (java.util.Arrays.equals(plain, plainNew)) {
            System.out.println(" ---> Erfolg!");
        } else {
            System.out.println(" ---> Hat leider noch nicht funktioniert ...!");
        }
    }
}
