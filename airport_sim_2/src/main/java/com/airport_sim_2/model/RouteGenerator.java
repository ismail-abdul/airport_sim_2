package com.airport_sim_2.model;

import java.util.Random;

public class RouteGenerator {

    private static final String[] AIRPORTS = {
        "EGLL", // London Heathrow
        "EGKK", // London Gatwick
        "EGCC", // Manchester
        "EGPH", // Edinburgh
        "LFPG", // Paris CDG
        "EDDF", // Frankfurt
        "EHAM", // Amsterdam
        "LEMD", // Madrid
        "OMDB", // Dubai
        "KJFK", // New York JFK
        "KLAX", // Los Angeles
        "WSSS"  // Singapore
    };

    private static final Random random = new Random();

    public static String generateAirport() {
        return AIRPORTS[random.nextInt(AIRPORTS.length)];
    }

    public static String[] generateRoute() {
        String origin = generateAirport();
        String destination;

        do {
            destination = generateAirport();
        } while (destination.equals(origin));

        return new String[]{origin, destination};
    }
}
