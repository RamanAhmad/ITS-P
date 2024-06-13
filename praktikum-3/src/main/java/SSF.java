import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class SSF {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: java SSF <senderPrvKey> <receiverPubKey> <inputFile> <outputFile>");
            System.exit(1);
        }

        try {
            PrivateKey senderPrvKey = readPrivateKey(args[0]);
            PublicKey receiverPubKey = readPublicKey(args[1]);

            // Generate AES key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey aesKey = keyGen.generateKey();

            // Encrypt AES key with RSA
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, receiverPubKey);
            byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());

            // Sign the AES key
            Signature signature = Signature.getInstance("SHA512withRSA");
            signature.initSign(senderPrvKey);
            signature.update(aesKey.getEncoded());
            byte[] aesKeySignature = signature.sign();

            // Encrypt the file with AES
            Cipher aesCipher = Cipher.getInstance("AES/CTR/NoPadding");
            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[aesCipher.getBlockSize()];
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);

            try (FileInputStream fis = new FileInputStream(args[2]);
                 FileOutputStream fos = new FileOutputStream(args[3]);
                 DataOutputStream dos = new DataOutputStream(fos)) {

                dos.writeInt(encryptedAesKey.length);
                dos.write(encryptedAesKey);
                dos.writeInt(aesKeySignature.length);
                dos.write(aesKeySignature);

                AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
                params.init(ivSpec);
                byte[] encodedParams = params.getEncoded();
                dos.writeInt(encodedParams.length);
                dos.write(encodedParams);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    byte[] encryptedBuffer = aesCipher.update(buffer, 0, bytesRead);
                    if (encryptedBuffer != null) {
                        dos.write(encryptedBuffer);
                    }
                }
                byte[] finalBuffer = aesCipher.doFinal();
                if (finalBuffer != null) {
                    dos.write(finalBuffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PrivateKey readPrivateKey(String filename) throws Exception {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            int nameLength = dis.readInt();
            byte[] ownerBytes = new byte[nameLength];
            dis.readFully(ownerBytes);

            int keyLength = dis.readInt();
            byte[] keyBytes = new byte[keyLength];
            dis.readFully(keyBytes);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
    }

    private static PublicKey readPublicKey(String filename) throws Exception {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            int nameLength = dis.readInt();
            byte[] ownerBytes = new byte[nameLength];
            dis.readFully(ownerBytes);

            int keyLength = dis.readInt();
            byte[] keyBytes = new byte[keyLength];
            dis.readFully(keyBytes);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }
    }
}
