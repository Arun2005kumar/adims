package com.antidoping.intelligence.util;

import java.time.Year;

public final class CaseNumberGenerator {

    private CaseNumberGenerator() {
    }

    public static String generate(long sequence) {
        int year = Year.now().getValue();
        return String.format("INV-%d-%04d", year, sequence);
    }
}
