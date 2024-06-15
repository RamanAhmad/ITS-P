import java.io.*;
import java.security.*;

public class RSAKeyCreation {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java RSAKeyCreation <ownerName>");
            System.exit(1);
        }

        String ownerName = args[0];
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(4096);
            KeyPair pair = keyGen.generateKeyPair();
            PublicKey pubKey = pair.getPublic();
            PrivateKey prvKey = pair.getPrivate();

            saveKey(ownerName, pubKey, ".pub");
            saveKey(ownerName, prvKey, ".prv");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void saveKey(String ownerName, Key key, String extension) {
        String directoryPath = System.getProperty("user.dir") + "\\praktikum-3\\docs";
        String filePath = directoryPath + "\\" + ownerName + extension;

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {
            byte[] ownerBytes = ownerName.getBytes();
            byte[] keyBytes = key.getEncoded();

            dos.writeInt(ownerBytes.length);
            dos.write(ownerBytes);
            dos.writeInt(keyBytes.length);
            dos.write(keyBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

