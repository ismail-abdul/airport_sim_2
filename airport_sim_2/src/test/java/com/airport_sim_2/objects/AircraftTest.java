package com.airport_simulation;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.airport_simulation.objects.Aircraft;
import com.airport_simulation.objects.AircraftStatus;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
    
    @Test
    public void aircraftGettersTest()
    {
        LocalDateTime current_time = LocalDateTime.now();
        Aircraft aircraft = new Aircraft("BA238","BA","JFK","LHR",400.0f,1000.0f,200.0f,AircraftStatus.NORMAL,current_time);
        assertEquals("BA238", aircraft.getCallsign());
        assertEquals("BA", aircraft.getOperator());
        assertEquals("JFK", aircraft.getOrigin());
        assertEquals("LHR", aircraft.getDestination());
        assertEquals(400, aircraft.getGroundSpeed());
        assertEquals(1000, aircraft.getAltitude());
        assertEquals(200, aircraft.getFuel());
        assertEquals(AircraftStatus.NORMAL, aircraft.getStatus());
        assertEquals(current_time, aircraft.getScheduledTime());
        
    }

    @Test
    public void aircraftReduceFuelTest()
    {
        Aircraft aircraft = new Aircraft("BA238","BA","JFK","LHR",400.0f,1000.0f,200.0f,AircraftStatus.NORMAL,LocalDateTime.now(););
        aircraft.reduceFuel(100);
        assertEquals(100, aircraft.getFuel());
        assertEquals(AircraftStatus.NORMAL, aircraft.getStatus());
        aircraft.reduceFuel(90);
        assertEquals(10, aircraft.getFuel());
        assertEquals(AircraftStatus.FUEL, aircraft.getStatus());
        aircraft.reduceFuel(20);
        assertEquals(0, aircraft.getFuel());
        assertEquals(AircraftStatus.FUEL, aircraft.getStatus());
    }

    @Test
    public void aircraftToStringTest()
    {
        Aircraft aircraft = new Aircraft("BA238","BA","JFK","LHR",400.0f,1000.0f,200.0f,AircraftStatus.NORMAL,LocalDateTime.now(););
        assertEquals("BA238 Normal", aircraft.toString());
    }

    @Test
    public void aircraftStatusPriorityTest()
    {
        assertEquals(0, AircraftStatus.NORMAL.getPriority());
        assertEquals(2, AircraftStatus.FUEL.getPriority());
        assertEquals(1, AircraftStatus.MECH_FAILURE.getPriority());
        assertEquals(1, AircraftStatus.PASSENGER_HEALTH.getPriority());
    }

    @Test
    public void aircraftStatusGetStatusTest()
    {
        assertEquals("Normal", AircraftStatus.NORMAL.getStatus());
        assertEquals("Fuel below 10%.", AircraftStatus.FUEL.getStatus());
        assertEquals("Mechanical Failure", AircraftStatus.MECH_FAILURE.getStatus());
        assertEquals("Passenger(s) poor health.", AircraftStatus.PASSENGER_HEALTH.getStatus());
    }

    @Test
    public void aircraftStatusPriorityLevelTest()
    {
        assertTrue(AircraftStatus.FUEL.getPriority() > AircraftStatus.NORMAL.getPriority() && AircraftStatus.PASSENGER_HEALTH.getPriority() > AircraftStatus.NORMAL.getPriority() && AircraftStatus.MECH_FAILURE.getPriority() > AircraftStatus.NORMAL.getPriority());
        assertTrue(AircraftStatus.PASSENGER_HEALTH.getPriority() == AircraftStatus.MECH_FAILURE.getPriority());
        assertTrue(AircraftStatus.FUEL.getPriority() == AircraftStatus.PASSENGER_HEALTH.getPriority());
        
    }

  //Verifies that the static sequence counter increments for FIFO ordering
    @Test
    public void aircraftSequenceNumberTest() {
        Aircraft plane1 = new Aircraft("P1", "BA", "JFK", "LHR", 400.0f, 1000.0f, 200.0f, AircraftStatus.NORMAL, LocalDateTime.now());
        Aircraft plane2 = new Aircraft("P2", "BA", "JFK", "LHR", 400.0f, 1000.0f, 200.0f, AircraftStatus.NORMAL, LocalDateTime.now());
        
        //Sequence number of plane 2 should be exactly 1 higher than plane 1
        assertTrue(plane2.getSequenceNumber() > plane1.getSequenceNumber());
        assertEquals(plane1.getSequenceNumber() + 1, plane2.getSequenceNumber());
    }

    //Verifies the delay maths logic
    @Test
    public void aircraftDelayCalculationTest() {
        LocalDateTime scheduled = LocalDateTime.now();
        Aircraft aircraft = new Aircraft("BA238", "BA", "JFK", "LHR", 400.0f, 1000.0f, 200.0f, AircraftStatus.NORMAL, scheduled);
        
        //No actual time set yet, delay should be 0
        assertEquals(0, aircraft.getDelayMinutes());

        //Actual time is exactly 30 minutes later
        LocalDateTime actual = scheduled.plusMinutes(30);
        aircraft.setActualTime(actual);
        
        assertEquals(actual, aircraft.getActualTime());
        assertEquals(30, aircraft.getDelayMinutes());
    }
  

