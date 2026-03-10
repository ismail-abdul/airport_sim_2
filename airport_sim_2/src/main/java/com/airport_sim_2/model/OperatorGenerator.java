package com.airport_sim_2.model;
import java.util.Random;

public class OperatorGenerator {

    private static final String[] OPERATORS = {
        "British Airways",
        "Ryanair",
        "EasyJet",
        "Lufthansa",
        "Air France",
        "Emirates",
        "Qatar Airways",
        "American Airlines",
        "Delta Air Lines",
        "United Airlines",
        "KLM",
        "Singapore Airlines"
    };

    private static final Random random = new Random();

    public static String generateOperator() {
        return OPERATORS[random.nextInt(OPERATORS.length)];
    }
}