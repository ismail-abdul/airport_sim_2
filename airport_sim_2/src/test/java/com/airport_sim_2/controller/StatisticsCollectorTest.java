package com.airport_sim_2.controller;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *Unit tests for the StatisticsCollector.
 * Proves that all final dashboard metrics and averages are calculated correctly
 */
public class StatisticsCollectorTest {

    @Test
    public void testBasicCounters() {
        StatisticsCollector stats = new StatisticsCollector();
        
        //Test Incrementers
        stats.incrementArrived();
        stats.incrementArrived();
        stats.incrementDeparted();
        stats.incrementCancelled();
        stats.incrementDiverted();
        
        //Test addDiverted
        stats.addDiverted(5);

        assertEquals("Should have 2 arrivals", 2, stats.getArrivedCount());
        assertEquals("Should have 1 departure", 1, stats.getDepartedCount());
        assertEquals("Should have 1 cancellation", 1, stats.getCancelledCount());
        assertEquals("Should have 6 diversions total (1 increment + 5 bulk)", 6, stats.getDivertedCount());
    }

    @Test
    public void testAverageWaitTimeMath() {
        StatisticsCollector stats = new StatisticsCollector();
        
        //Empty lists should return 0, not crash
        assertEquals(0.0, stats.getAverageArrivalWait(), 0.001);
        assertEquals(0.0, stats.getAverageDepartureWait(), 0.001);

        //Record some arrival wait times 
        stats.recordArrivalWait(10.0);
        stats.recordArrivalWait(20.0);
        
        //Record some departure wait times
        stats.recordDepartureWait(5.0);
        stats.recordDepartureWait(15.0);

        //Averages should be perfectly calculated
        assertEquals("Average of 10 and 20 is 15", 15.0, stats.getAverageArrivalWait(), 0.001);
        assertEquals("Average of 5 and 15 is 10", 10.0, stats.getAverageDepartureWait(), 0.001);
    }

    @Test
    public void testMaxValuesLogic() {
        StatisticsCollector stats = new StatisticsCollector();
        
        //Queue sizes should only update if the new number is higher
        stats.updateMaxHoldingSize(5);
        stats.updateMaxHoldingSize(3); //Should ignore this
        stats.updateMaxHoldingSize(10); //Should update to this
        assertEquals(10, stats.getMaxHoldingSize());

        stats.updateMaxTakeoffQueueSize(7);
        assertEquals(7, stats.getMaxTakeoffQueueSize());

        //Max Wait Time is automatically updated when recording wait times
        stats.recordArrivalWait(25.0);
        stats.recordDepartureWait(45.0);
        stats.recordArrivalWait(10.0);
        
        assertEquals("Max wait time across all records should be 45.0", 45.0, stats.getMaxWaitTimeRecorded(), 0.001);
    }

    @Test
    public void testTimeSeriesLists() {
        StatisticsCollector stats = new StatisticsCollector();
        
        //Using the TimeSeriesPoint class to verify data capture
        stats.diversion_ts_add(new TimeSeriesPoint(10.5, 1.0));
        stats.diversion_ts_add(new TimeSeriesPoint(20.0, 2.0));
        
        stats.hp_ts_add(new TimeSeriesPoint(5.0, 4.0));
        
        assertEquals("Diversion TS should have 2 points", 2, stats.getDiversion_time_series().size());
        assertEquals("First diversion point should record correct time", 10.5, stats.getDiversion_time_series().get(0).getTime(), 0.001);
        assertEquals("First diversion point should record correct value", 1.0, stats.getDiversion_time_series().get(0).getValue(), 0.001);
        
        assertEquals("Holding Pattern TS should have 1 point", 1, stats.getHp_time_series().size());
        assertEquals("Takeoff Queue TS should be empty initially", 0, stats.getToq_time_series().size());
    }
}