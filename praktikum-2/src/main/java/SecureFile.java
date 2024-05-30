import des.TripleDES;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SecureFile {
    private TripleDES tripleDES;
    private byte[] iv;

    /**
     * Constructor to initialize the SecureFile object with TripleDES and an initialization vector (IV).
     *
     * @param keyPart1 The first part of the TripleDES key.
     * @param keyPart2 The second part of the TripleDES key.
     * @param keyPart3 The third part of the TripleDES key.
     * @param iv The initialization vector.
     */
    public SecureFile(byte[] keyPart1, byte[] keyPart2, byte[] keyPart3, byte[] iv) {
        tripleDES = new TripleDES(keyPart1, keyPart2, keyPart3);
        this.iv = iv;
    }

    /**
     * Processes the file by either encrypting or decrypting it using TripleDES in CFB mode.
     *
     * @param inputFile The path of the input file.
     * @param outputFile The path of the output file.
     * @param encrypt True for encryption, false for decryption.
     * @throws IOException If an I/O error occurs.
     */
    private void processFile(String inputFile, String outputFile, boolean encrypt) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {

            byte[] inputBytes = new byte[8];
            byte[] outputBytes = new byte[8];

            int bytesRead;
            while ((bytesRead = fileInputStream.read(inputBytes)) != -1) {
                byte[] processedBytes = tripleDES.encryptBytes(iv);
                for (int i = 0; i < bytesRead; i++) {
                    outputBytes[i] = (byte) (inputBytes[i] ^ processedBytes[i]); // XOR operation
                }

                fileOutputStream.write(outputBytes, 0, bytesRead);

                // Update the IV for the next block
                iv = encrypt ? outputBytes.clone() : inputBytes.clone();
            }
        }
        System.out.println(encrypt ? "Datei erfolgreich verschlüsselt." : "Datei erfolgreich entschlüsselt.");
    }

    /**
     * Encrypts the input file and writes the result to the output file.
     *
     * @param inputFile The path of the input file to be encrypted.
     * @param outputFile The path of the output file to write the encrypted data.
     * @throws IOException If an I/O error occurs.
     */
    public void encryptFile(String inputFile, String outputFile) throws IOException {
        processFile(inputFile, outputFile, true);
    }

    /**
     * Decrypts the input file and writes the result to the output file.
     *
     * @param inputFile The path of the input file to be decrypted.
     * @param outputFile The path of the output file to write the decrypted data.
     * @throws IOException If an I/O error occurs.
     */
    public void decryptFile(String inputFile, String outputFile) throws IOException {
        processFile(inputFile, outputFile, false);
    }

    /**
     * Reads a section of the key file and returns it as a byte array.
     *
     * @param keyFile The path of the key file.
     * @param startByte The start byte index in the key file.
     * @param endByte The end byte index in the key file.
     * @return The byte array containing the key part.
     */
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
        // Ensure there are exactly 4 arguments
        if (args.length != 4) {
            System.out.println("Usage: java SecureFile <inputFile> <keyFile> <outputFile> <encrypt|decrypt>");
            return;
        }

        // Parse arguments
        String inputFile = args[0];
        String keyFile = args[1];
        String outputFile = args[2];
        String mode = args[3];

        // Read key parts and IV from the key file
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
                System.out.println("Invalid mode. Use 'encrypt' or 'decrypt'.");
                return;
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Verarbeiten der Datei: " + e.getMessage());
            return;
        }

        System.out.println("Datei erfolgreich verarbeitet.");
    }
}
