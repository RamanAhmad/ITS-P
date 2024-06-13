import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RSF {
    public static void main(String[] args) {
        try {
            PrivateKey receiverPrvKey = readPrivateKey(args[0]);
            PublicKey senderPubKey = readPublicKey(args[1]);

            try (DataInputStream dis = new DataInputStream(new FileInputStream(args[2]));
                 FileOutputStream fos = new FileOutputStream(args[3])) {

                int encryptedKeyLength = dis.readInt();
                byte[] encryptedAesKey = new byte[encryptedKeyLength];
                dis.readFully(encryptedAesKey);

                int signatureLength = dis.readInt();
                byte[] aesKeySignature = new byte[signatureLength];
                dis.readFully(aesKeySignature);

                int paramsLength = dis.readInt();
                byte[] encodedParams = new byte[paramsLength];
                dis.readFully(encodedParams);

                AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
                params.init(encodedParams);

                // Decrypt the AES key
                Cipher rsaCipher = Cipher.getInstance("RSA");
                rsaCipher.init(Cipher.DECRYPT_MODE, receiverPrvKey);
                byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
                SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");

                // Verify the AES key signature
                Signature signature = Signature.getInstance("SHA512withRSA");
                signature.initVerify(senderPubKey);
                signature.update(aesKeyBytes);

                if (!signature.verify(aesKeySignature)) {
                    throw new SecurityException("Invalid signature for AES key");
                }

                // Decrypt the file content
                Cipher aesCipher = Cipher.getInstance("AES/CTR/NoPadding");
                aesCipher.init(Cipher.DECRYPT_MODE, aesKey, params);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dis.read(buffer)) != -1) {
                    byte[] decryptedBuffer = aesCipher.update(buffer, 0, bytesRead);
                    if (decryptedBuffer != null) {
                        fos.write(decryptedBuffer);
                    }
                }
                byte[] finalBuffer = aesCipher.doFinal();
                if (finalBuffer != null) {
                    fos.write(finalBuffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PrivateKey readPrivateKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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

    private static PublicKey readPublicKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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
