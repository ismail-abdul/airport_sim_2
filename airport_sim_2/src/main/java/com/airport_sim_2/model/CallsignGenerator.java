package com.airport_sim_2.model;

import java.util.Random;

public class CallsignGenerator {

    private static final String[] AIRLINE_CODES = {
        "BAW", // British Airways
        "DAL", // Delta
        "AAL", // American Airlines
        "UAE", // Emirates
        "RYR", // Ryanair
        "EZY", // EasyJet
        "QFA", // Qantas
        "AFR", // Air France
        "DLH"  // Lufthansa
    };

    private static final Random random = new Random();

    public static String generateCallsign() {
        String airline = AIRLINE_CODES[random.nextInt(AIRLINE_CODES.length)];
        int flightNumber = 1 + random.nextInt(9999); // 1–9999
        return airline + flightNumber;
    }
}