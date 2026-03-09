package com.airport_sim_2.queues;

import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

/**
 * Unit tests for the TakeOff Queue.
 * Validates the FIFO behavior and the cancellation wait-time maths
 */
public class TakeOffQueueTest {

    private Aircraft createPlane(String callsign) {
        return new Aircraft(callsign, "OP", "ORG", "DST", 200f, 1000f, 100f, AircraftStatus.NORMAL, LocalDateTime.now());
    }

    @Test
    public void testTakeOffFIFO() {
        TakeOffQueue toq = new TakeOffQueue();
        toq.enqueue(createPlane("A"));
        toq.enqueue(createPlane("B"));

        String[] calls = toq.getCallsign();
        assertEquals("A", calls[0]);
        assertEquals("B", calls[1]);
        
        assertEquals("A", toq.dequeue().getCallsign());
    }

    @Test
    public void testCancellationLogic() {
        TakeOffQueue toq = new TakeOffQueue();
        
        //Setup: 1 runway, 10 mins per takeoff, 15 min max wait
        //Plane 1: Wait = 10 mins (Safe)
        toq.enqueue(createPlane("SAFE"));
        //Plane 2: Wait = 20 mins (Should cancel)
        toq.enqueue(createPlane("CANCEL"));

        //Params: 1 runway, 10 min takeoff, 15 min limit
        int result = toq.checkCancelled(1, 10, 15);

        assertEquals("One aircraft should have cancelled", 1, result);
        assertEquals(1, toq.size());
        assertEquals("SAFE", toq.dequeue().getCallsign());
    }
}
