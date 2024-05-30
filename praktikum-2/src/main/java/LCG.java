import java.util.HashSet;

public class LCG {

    private long startValue;

    public LCG(int startValue) {
        this.startValue = startValue;
    }

    int nextInt(long a, long b, long m) {
        startValue = Math.floorMod((a * startValue + b), m);
        return (int) startValue;
    }

    public static void main(String[] args) {
        LCG lcg = new LCG(12345);
        // Parameterkombination: VAX VMS-Generator von Marsaglia
        long a = 69069;
        long b = 1;
        long m = (long) Math.pow(2, 32);
        HashSet<Integer> hashSet = new HashSet<>();

        for (int i = 0; i < 256; i++) {
            int randomValue = lcg.nextInt(a, b, m);
            int leastSignificantByte = randomValue & 0x000000FF;
            hashSet.add(leastSignificantByte);
        }
        System.out.println("Anzahl unterschiedlicher niederwertiger Bytes: " + hashSet.size());
    }
}
