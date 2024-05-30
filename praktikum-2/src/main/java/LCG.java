import java.util.HashSet;

public class LCG {

    private long startValue;

    /**
     * Constructor to initialize the LCG with a starting value.
     *
     * @param startValue The initial seed value for the LCG.
     */
    public LCG(int startValue) {
        this.startValue = startValue;
    }

    /**
     * Generates the next pseudorandom integer using the linear congruential generator formula.
     *
     * @param a The multiplier.
     * @param b The increment.
     * @param m The modulus.
     * @return The next pseudorandom integer.
     */
    int nextInt(long a, long b, long m) {
        startValue = Math.floorMod((a * startValue + b), m);
        return (int) startValue;
    }

    public static void main(String[] args) {
        // Initialize the LCG with a specific start value
        LCG lcg = new LCG(12345);

        // Constants for the LCG
        long a = 69069;
        long b = 1;
        long m = (long) Math.pow(2, 32);

        // A set to store unique least significant bytes of generated pseudorandom numbers
        // Parameterkombination: VAX VMS-Generator von Marsaglia
        long a = 69069;
        long b = 1;
        long m = (long) Math.pow(2, 32);
        HashSet<Integer> hashSet = new HashSet<>();

        // Generate 256 pseudorandom numbers and store their least significant bytes
        for (int i = 0; i < 256; i++) {
            int randomValue = lcg.nextInt(a, b, m);
            int leastSignificantByte = randomValue & 0x000000FF;
            hashSet.add(leastSignificantByte);
        }

        // Print the number of unique least significant bytes generated
        System.out.println("Anzahl unterschiedlicher niederwertiger Bytes: " + hashSet.size());
    }
}
