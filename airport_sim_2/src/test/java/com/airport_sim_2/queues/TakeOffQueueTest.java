package com.airport_sim_2.queues;

import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the TakeOff Queue.
 * Validates the FIFO behavior and the cancellation wait-time math.
 */
public class TakeOffQueueTest {

    private Aircraft createPlane(String callsign) {
        return new Aircraft(callsign, "OP", "ORG", "DST", 200.0f, 100.0f, 0, AircraftStatus.NORMAL, 0.0);
    }

    @Test
    public void testTakeOffFIFO() {
        TakeOffQueue toq = new TakeOffQueue();
        toq.enqueue(createPlane("FIRST"));
        toq.enqueue(createPlane("SECOND"));

        String[] calls = toq.getCallsign();
        assertEquals("FIRST", calls[0]);
        assertEquals("SECOND", calls[1]);
        
        assertEquals("FIRST", toq.dequeue().getCallsign());
    }

    @Test
    public void testQueueStatusMethods() {
        TakeOffQueue toq = new TakeOffQueue();
        assertTrue("Queue should be empty initially", toq.isEmpty());
        assertEquals(0, toq.size());

        toq.enqueue(createPlane("TEST"));
        assertFalse("Queue should not be empty after enqueue", toq.isEmpty());
        assertEquals(1, toq.size());
    }

    @Test
    public void testCancellationLogic() {
        TakeOffQueue toq = new TakeOffQueue();
        
        //Setup: 1 runway, 10 mins per takeoff, 15 min max wait
        //Plane 1: Wait = (1/1) * 10 = 10 mins (Safe)
        toq.enqueue(createPlane("SAFE"));
        // Plane 2: Wait = (2/1) * 10 = 20 mins (Should cancel)
        toq.enqueue(createPlane("CANCEL"));

        //Params: 1 runway, 10 min takeoff, 15 min limit
        int cancelledCount = toq.checkCancelled(1, 10, 15);

        assertEquals("One aircraft should have cancelled", 1, cancelledCount);
        assertEquals(1, toq.size());
        assertEquals("SAFE", toq.dequeue().getCallsign());
    }
}