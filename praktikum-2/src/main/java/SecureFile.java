import des.TripleDES;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SecureFile {
    private TripleDES tripleDES;
    private byte[] iv;

    public SecureFile(byte[] keyPart1, byte[] keyPart2, byte[] keyPart3, byte[] iv) {
        tripleDES = new TripleDES(keyPart1, keyPart2, keyPart3);
        this.iv = iv;
    }

    private void processFile(String inputFile, String outputFile, boolean encrypt) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {

            byte[] inputBytes = new byte[8];
            byte[] outputBytes = new byte[8];

            int bytesRead;
            while ((bytesRead = fileInputStream.read(inputBytes)) != -1) {
                byte[] processedBytes = tripleDES.encryptBytes(iv);
                for (int i = 0; i < bytesRead; i++) {
                    outputBytes[i] = (byte) (inputBytes[i] ^ processedBytes[i]); // XOR-Verknüpfung
                }

                fileOutputStream.write(outputBytes, 0, bytesRead);

                iv = encrypt ? outputBytes.clone() : inputBytes.clone();
            }
        }
        System.out.println(encrypt ? "Datei erfolgreich verschlüsselt." : "Datei erfolgreich entschlüsselt.");
    }

    public void encryptFile(String inputFile, String outputFile) throws IOException {
        processFile(inputFile, outputFile, true);
    }

    public void decryptFile(String inputFile, String outputFile) throws IOException {
        processFile(inputFile, outputFile, false);
    }

    private static byte[] readKeyFile(String keyFile, int startByte, int endByte) {
        try (FileInputStream fileInputStream = new FileInputStream(keyFile)) {
            byte[] keyBytes = new byte[endByte - startByte];
            fileInputStream.skip(startByte);
            fileInputStream.read(keyBytes);
            return keyBytes;
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Schlüsseldatei: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            return;
        }

        String inputFile = args[0];
        String keyFile = args[1];
        String outputFile = args[2];
        String mode = args[3];

        byte[] keyPart1 = readKeyFile(keyFile, 0, 8);
        byte[] keyPart2 = readKeyFile(keyFile, 8, 16);
        byte[] keyPart3 = readKeyFile(keyFile, 16, 24);
        byte[] iv = readKeyFile(keyFile, 24, 32);

        SecureFile secureFile = new SecureFile(keyPart1, keyPart2, keyPart3, iv);

        try {
            if (mode.equals("encrypt")) {
                secureFile.encryptFile(inputFile, outputFile);
            } else if (mode.equals("decrypt")) {
                secureFile.decryptFile(inputFile, outputFile);
            } else {
                System.out.println("Verwenden Sie 'encrypt' oder 'decrypt'.");
               return;
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Verarbeiten der Datei: " + e.getMessage());
            return;
        }

        System.out.println("Datei erfolgreich verarbeitet.");
    }
}
