package net.brabenetz.tools.smart.naming;

import org.junit.Test;

public class SmartNamingBannerTest {

    @Test
    public void testPrintBanner() throws Exception {
        new SmartNamingBanner().printBanner(null, null, System.out);
    }
}