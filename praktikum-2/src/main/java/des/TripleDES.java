package des;

import javax.crypto.*;
import javax.crypto.spec.*;

public class TripleDES {

    private final DES des_1;
    private final DES des_2;
    private final DES des_3;


    public TripleDES(byte[] keyPart1, byte[] keyPart2, byte[] keyPart3) {
        if (keyPart1.length != 8 | keyPart2.length != 8 | keyPart3.length != 8) {
            throw new IllegalArgumentException("keyParts must be 8 bytes.");
        }

        this.des_1= new DES(keyPart1);
        this.des_2= new DES(keyPart2);
        this.des_3=new DES(keyPart3);
    }

    public byte[] encryptBytes(byte[] plaintextBytes) throws Exception {
        byte[] resultBytes= new byte[8];
        this.des_1.encrypt(plaintextBytes,0,resultBytes,0);
        this.des_2.decrypt(resultBytes,0,resultBytes,0);
        this.des_3.encrypt(resultBytes,0,resultBytes,0);

        return resultBytes;
    }

    public byte[] decryptBytes(byte[] chiffreBytes)  {
        byte [] resultBytes= new byte[8];
        this.des_3.decrypt(chiffreBytes,0,resultBytes,0);
        this.des_2.encrypt(resultBytes,0,resultBytes,0);
        this.des_1.decrypt(resultBytes,0,resultBytes,0);

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
