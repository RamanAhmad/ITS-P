import javax.crypto.*;
import javax.crypto.spec.*;

public class TripleDES {

    private SecretKey key;
    private Cipher cipher;


    public TripleDES(byte[] keyPart1, byte[] keyPart2, byte[] keyPart3) throws Exception {
        if (keyPart1.length != 8 | keyPart2.length != 8 | keyPart3.length != 8) {
            throw new IllegalArgumentException("keyParts must be 8 bytes.");
        }

        byte[] fullKey = new byte[24];
        System.arraycopy(keyPart1, 0, fullKey, 0, 8);
        System.arraycopy(keyPart2, 0, fullKey, 8, 8);
        System.arraycopy(keyPart3, 0, fullKey, 16, 8);

        this.key = new SecretKeySpec(fullKey, "DESede");
        this.cipher = Cipher.getInstance("DESede/ECB/NoPadding");
    }

    public byte[] encryptBytes(byte[] plaintextBytes) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintextBytes);
    }

    public byte[] decryptBytes(byte[] chiffreBytes) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(chiffreBytes);
    }

    private String byteArraytoHexString(byte[] byteArray) {
        StringBuilder ret = new StringBuilder();
        for (byte b : byteArray) {
            ret.append(String.format("%02x", b));
            ret.append(" ");
        }
        return ret.toString();
    }

    public static void main(String[] args) throws Exception {
        /* Testcode */
        TripleDES cipher = new TripleDES("qwertzui".getBytes(), "asdfghjk".getBytes(), "yxcvbnm,".getBytes());

        byte[] plain = "12345678".getBytes();
        byte[] chiffre = cipher.encryptBytes(plain);
        System.out.println(" Encrypted: " +  cipher.byteArraytoHexString(plain) + " to: " + cipher.byteArraytoHexString(chiffre));

        byte[] plainNew = cipher.decryptBytes(chiffre);
        System.out.println(" Decrypted: " + cipher.byteArraytoHexString(plainNew) );

        if (java.util.Arrays.equals(plain, plainNew)) {
            System.out.println(" ---> Erfolg!");
        } else {
            System.out.println(" ---> Hat leider noch nicht funktioniert ...!");
        }
    }
}
