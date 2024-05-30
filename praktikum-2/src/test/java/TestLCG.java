import java.util.HashSet;

public class TestLCG {

    LCG lcg;

    void test_nextInt_for_chiang_hwang_kao() {
        testLCG(19496, 0, (long) Math.pow(2, 15) - 19, "Chiang, Hwang, Kao");
    }

    void test_nextInt_for_basic() {
        testLCG(214013, 13523655, (long) Math.pow(2, 24), "BASIC");
    }

    void test_nextInt_for_qbasic() {
        testLCG(16598013, 12820163, (long) Math.pow(2, 24), "Qbasic");
    }

    void test_nextInt_for_berkeley_unix_pascal() {
        testLCG(62605, 113218009, (long) Math.pow(2, 29), "Berkeley UNIX Pascal");
    }

    void test_nextInt_for_urn12() {
        testLCG(452807053, 0, (long) Math.pow(2, 31), "URN12");
    }

    void test_nextInt_for_minimal_standard() {
        testLCG(16807, 0, (long) Math.pow(2, 31) - 1, "_Minimal Standard_ von Lewis, et al.");
    }

    void test_nextInt_for_l_ecuyer() {
        testLCG(41358, 0, (long) Math.pow(2, 31) - 1, "L'Ecuyer");
    }

    void test_nextInt_for_park_miller_1() {
        testLCG(48271, 0, (long) Math.pow(2, 31) - 1, "Park & Miller");
    }

    void test_nextInt_for_hartel() {
        testLCG(51081, 0, (long) Math.pow(2, 31) - 1, "Härtel");
    }

    void test_nextInt_for_park_miller_2() {
        testLCG(69621, 0, (long) Math.pow(2, 31) - 1, "Park & Miller");
    }

    void test_nextInt_for_fish() {
        testLCG(950706376, 0, (long) Math.pow(2, 31) - 1, "FISH von Fishman & Moore");
    }

    void test_nextInt_for_simsript_ii() {
        testLCG(63060016, 0, (long) Math.pow(2, 31) - 1, "SIMSCRIPT II");
    }

    void test_nextInt_for_sas_imsl() {
        testLCG(397204094, 0, (long) Math.pow(2, 31) - 1, "SAS & IMSL-Library von Hoaglin");
    }

    void test_nextInt_for_ibm_randu() {
        testLCG(65539, 0, (long) Math.pow(2, 31), "IBM's RANDU");
    }

    void test_nextInt_for_unix_rand() {
        testLCG(1103515245, 12345, (long) Math.pow(2, 31), "UNIX rand [Rip90], ANSI C");
    }

    void test_nextInt_for_turbo_pascal() {
        testLCG(129, 907633385, (long) Math.pow(2, 32), "Turbo Pascal");
    }

    void test_nextInt_for_vax_vms() {
        testLCG(69069, 1, (long) Math.pow(2, 32), "VAX VMS-Generator von Marsaglia");
    }

    void test_nextInt_for_c_rand() {
        testLCG(663608941, 0, (long) Math.pow(2, 32), "C-RAND von Ahrens");
    }

    void test_nextInt_for_fishman() {
        testLCG(1099087573, 0, (long) Math.pow(2, 32), "Fishman");
    }

    void test_nextInt_for_derive() {
        testLCG(3141592653L, 1, (long) Math.pow(2, 32), "DERIVE");
    }

    void test_nextInt_for_bcpl() {
        testLCG(2147001325, 715136305, (long) Math.pow(2, 32), "BCPL von Richards, Whitby-Strevens");
    }

    void test_nextInt_for_boeing_bcs() {
        testLCG((long) Math.pow(5, 15), 7261067085L, (long) Math.pow(2, 35), "Boeing Computer Services BCSLIB");
    }

    void test_nextInt_for_lecuyer() {
        testLCG(71971110957370L, 0, (long) Math.pow(2, 47) - 115, "L'Ecuyer");
    }

    void test_nextInt_for_sun_unix_drand48() {
        testLCG(25214903917L, 11, (long) Math.pow(2, 48), "SUN-UNIX drand48");
    }

    void test_nextInt_for_cray_x_mp() {
        testLCG(44485709377909L, 0, (long) Math.pow(2, 48), "Cray X-MP Library");
    }

    void test_nextInt_for_fishman_2() {
        testLCG(68909602460261L, 0, (long) Math.pow(2, 48), "Fishman");
    }

    void test_nextInt_for_nag_fortran() {
        testLCG((long) Math.pow(13, 13), 0, (long) Math.pow(2, 59), "NAG Fortran Library");
    }

    void test_nextInt_for_lecuyer_2() {
        testLCG(2307085864L, 0, (long) Math.pow(2, 63) - 25, "L'Ecuyer");
    }

    void test_nextInt_for_maple() {
        testLCG(427419669081L, 0, (long) Math.pow(10, 12) - 11, "MAPLE");
    }

    private void testLCG(long a, long b, long m, String reference) {
        lcg = new LCG(2424);
        HashSet<Integer> hashSet = new HashSet<>();

        for (int i = 0; i < 256; i++) {
            int randomValue = lcg.nextInt(a, b, m);
            int leastSignificantByte = randomValue & 0x000000FF;
            hashSet.add(leastSignificantByte);
        }
        System.out.println("Anzahl unterschiedlicher niederwertiger Bytes fuer " + reference + ": " + hashSet.size());
    }

    public static void main(String[] args) {
        TestLCG test = new TestLCG();
        test.test_nextInt_for_chiang_hwang_kao();
        test.test_nextInt_for_basic();
        test.test_nextInt_for_qbasic();
        test.test_nextInt_for_berkeley_unix_pascal();
        test.test_nextInt_for_urn12();
        test.test_nextInt_for_minimal_standard();
        test.test_nextInt_for_l_ecuyer();
        test.test_nextInt_for_park_miller_1();
        test.test_nextInt_for_hartel();
        test.test_nextInt_for_park_miller_2();
        test.test_nextInt_for_fish();
        test.test_nextInt_for_simsript_ii();
        test.test_nextInt_for_sas_imsl();
        test.test_nextInt_for_ibm_randu();
        test.test_nextInt_for_unix_rand();
        test.test_nextInt_for_turbo_pascal();
        test.test_nextInt_for_vax_vms();
        test.test_nextInt_for_c_rand();
        test.test_nextInt_for_fishman();
        test.test_nextInt_for_derive();
        test.test_nextInt_for_bcpl();
        test.test_nextInt_for_boeing_bcs();
        test.test_nextInt_for_lecuyer();
        test.test_nextInt_for_sun_unix_drand48();
        test.test_nextInt_for_cray_x_mp();
        test.test_nextInt_for_nag_fortran();
        test.test_nextInt_for_lecuyer_2();
        test.test_nextInt_for_maple();
    }
}
