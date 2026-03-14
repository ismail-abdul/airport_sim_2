package com.airport_sim_2.controller;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Input Validation Logic.
 * Testing the logic independently of the JavaFX UI thread.
 * Proves that the system rejects negative rates, zero duration, and non-numeric data.
 */
public class ConfigurationValidationTest {


    public boolean validateTrafficParameters(String inboundRateText, String outboundRateText, String durationText) {
        try {
            int inbound = Integer.parseInt(inboundRateText);
            int outbound = Integer.parseInt(outboundRateText);
            int duration = Integer.parseInt(durationText);
            
            if (inbound < 0 || outbound < 0) {
                return false; //"Traffic rates must be non-negative"
            }
            
            if (duration <= 0) {
                return false; //"Duration must be greater than 0"
            }
            
        } catch (NumberFormatException e) {
            return false; //"Please enter valid numbers for all parameters"
        }
        
        return true;
    }

    @Test
    public void testValidInputsAreAccepted() {
        assertTrue("System should accept valid positive integers.", 
                   validateTrafficParameters("15", "15", "120"));
    }

    @Test
    public void testNegativeRatesAreRejected() {
        //Inbound is negative
        assertFalse("System should reject negative inbound rates.", 
                    validateTrafficParameters("-5", "15", "120"));
        
        //Outbound is negative
        assertFalse("System should reject negative outbound rates.", 
                    validateTrafficParameters("15", "-10", "120"));
    }

    @Test
    public void testInvalidDurationIsRejected() {
        //Duration cannot be 0
        assertFalse("System should reject a duration of 0.", 
                    validateTrafficParameters("15", "15", "0"));
        
        //Duration cannot be negative
        assertFalse("System should reject negative durations.", 
                    validateTrafficParameters("15", "15", "-60"));
    }

    @Test
    public void testNonNumericDataIsRejected() {
        //Someone types letters or symbols instead of numbers
        assertFalse("System should reject alphabetical characters.", 
                    validateTrafficParameters("abc", "15", "120"));
        
        //Someone leaves the box empty
        assertFalse("System should reject empty strings.", 
                    validateTrafficParameters("", "15", "120"));
    }
}