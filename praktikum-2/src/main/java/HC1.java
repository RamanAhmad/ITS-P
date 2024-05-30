import util.Util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class HC1 {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Encrypt the plain text file and then decrypt the encrypted file
        modifyTextFile(Util.PLAIN_TEXT_PATH, Util.ENCRYPTED_TEXT_PATH);
        modifyTextFile(Util.ENCRYPTED_TEXT_PATH, Util.DECRYPTED_TEXT_PATH);

        // Check if the plain text file and decrypted file are identical
        try {
            byte[] originalPlainText = Files.readAllBytes(Paths.get(Util.PLAIN_TEXT_PATH));
            byte[] decryptedText = Files.readAllBytes(Paths.get(Util.DECRYPTED_TEXT_PATH));

            if (java.util.Arrays.equals(originalPlainText, decryptedText)) {
                System.out.println("The plain text and decrypted files are identical.");
            } else {
                System.out.println("The plain text and decrypted files are NOT identical.");
            }
        } catch (IOException e) {
            System.err.println("Error reading files for comparison: " + e.getMessage());
        }
    }

    /**
     * This method modifies the contents of a file by encrypting or decrypting it
     * using an XOR operation with a pseudorandom number generator.
     *
     * @param from The path of the input file.
     * @param to   The path of the output file.
     */
    public static void modifyTextFile(String from, String to) {
        System.out.print("Enter start value: ");
        int startValue = scanner.nextInt();
        scanner.nextLine();  // Consume the newline character
        LCG lcg = new LCG(startValue);  // Initialize the LCG with the start value

        // Constants for the linear congruential generator (LCG)
        long a = 69069;
        long b = 1;
        long m = (long) Math.pow(2, 32);

        try (FileInputStream fileInputStream = new FileInputStream(from);
             FileOutputStream fileOutputStream = new FileOutputStream(to)) {

            int byteRead;
            while ((byteRead = fileInputStream.read()) != -1) {
                // Generate the next pseudorandom number
                int randomValue = lcg.nextInt(a, b, m);
                // Extract the least significant byte from the random number
                int leastSignificantByte = randomValue & 0xFF;
                // Encrypt/Decrypt the byte using XOR operation
                int encryptedByte = byteRead ^ leastSignificantByte;
                // Write the resulting byte to the output file
                fileOutputStream.write(encryptedByte);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
