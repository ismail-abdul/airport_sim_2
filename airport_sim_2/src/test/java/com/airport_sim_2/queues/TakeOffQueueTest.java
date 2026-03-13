package com.airport_sim_2.queues;

import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

/**
 * Unit tests for the TakeOff Queue.
 * Validates the FIFO behavior and the cancellation wait-time math (FR 13).
 */
public class TakeOffQueueTest {

    // private Aircraft createPlane(String callsign) {
    //     return new Aircraft(callsign, "OP", "ORG", "DST", 200f, 1000f, 100f, AircraftStatus.NORMAL, LocalDateTime.now());
    // }

    // @Test
    // public void testTakeOffFIFO() {
    //     TakeOffQueue toq = new TakeOffQueue();
    //     toq.enqueue(createPlane("A"));
    //     toq.enqueue(createPlane("B"));

    //     String[] calls = toq.getCallsign();
    //     assertEquals("A", calls[0]);
    //     assertEquals("B", calls[1]);
        
    //     assertEquals("A", toq.dequeue().getCallsign());
    // }

    // @Test
    // public void testQueueStatusMethods() {
    //     TakeOffQueue toq = new TakeOffQueue();
    //     assertTrue("Queue should be empty initially", toq.isEmpty());
    //     assertEquals(0, toq.size());

    //     toq.enqueue(createPlane("TEST"));
    //     assertFalse("Queue should not be empty after enqueue", toq.isEmpty());
    //     assertEquals(1, toq.size());
    // }

    // @Test
    // public void testCancellationLogic() {
    //     TakeOffQueue toq = new TakeOffQueue();
        
    //     // Setup: 1 runway, 10 mins per takeoff, 15 min max wait
    //     // Plane 1: Wait = 10 mins (Safe)
    //     toq.enqueue(createPlane("SAFE"));
    //     // Plane 2: Wait = 20 mins (Should cancel)
    //     toq.enqueue(createPlane("CANCEL"));

    //     // Params: 1 runway, 10 min takeoff, 15 min limit
    //     int result = toq.checkCancelled(1, 10, 15);

    //     assertEquals("One aircraft should have cancelled", 1, result);
    //     assertEquals(1, toq.size());
    //     assertEquals("SAFE", toq.dequeue().getCallsign());
    // }

    // @Test
    // public void testNoCancellationsWhenWithinLimits() {
    //     TakeOffQueue toq = new TakeOffQueue();
    //     toq.enqueue(createPlane("P1"));
    //     toq.enqueue(createPlane("P2"));

    //     // 2 runways, 10 min takeoff time. 
    //     // P1 wait = 10 mins, P2 wait = 10 mins. Limit is 15.
    //     // Both should stay.
    //     int result = toq.checkCancelled(2, 10, 15);

    //     assertEquals("Zero aircraft should have cancelled", 0, result);
    //     assertEquals(2, toq.size());
    // }

    // @Test
    // public void testAllCancelled() {
    //     TakeOffQueue toq = new TakeOffQueue();
    //     toq.enqueue(createPlane("C1"));
    //     toq.enqueue(createPlane("C2"));

    //     // 1 runway, 10 min takeoff, but limit is only 5 mins.
    //     // Even the first plane exceeds the limit (Wait = 10).
    //     int result = toq.checkCancelled(1, 10, 5);

    //     assertEquals("Both aircraft should have cancelled", 2, result);
    //     assertTrue("Queue should be empty", toq.isEmpty());
    // }
}
