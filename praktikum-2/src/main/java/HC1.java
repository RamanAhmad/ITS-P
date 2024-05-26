import util.Util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class HC1 {
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        modifyTextFile(Util.PLAIN_TEXT_PATH, Util.ENCRYPTED_TEXT_PATH);
        modifyTextFile(Util.ENCRYPTED_TEXT_PATH, Util.DECRYPTED_TEXT_PATH);
    }

    public static void modifyTextFile(String from, String to){
        System.out.print("Enter start value: ");
        int startValue = scanner.nextInt();
        scanner.nextLine();
        LCG lcg = new LCG(startValue);
        long a = 69069;
        long b = 1;
        long m = (long) Math.pow(2, 32);

        try (FileInputStream fileInputStream = new FileInputStream(from);
             FileOutputStream fileOutputStream = new FileOutputStream(to)) {

            int byteRead;
            while ((byteRead = fileInputStream.read()) != -1) {
                int randomValue = lcg.nextInt(a, b, m);
                int leastSignificantByte = randomValue & 0xFF;
                int encryptedByte = byteRead ^ leastSignificantByte;
                fileOutputStream.write(encryptedByte);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
