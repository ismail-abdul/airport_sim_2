# Backend Integration Guide

This guide explains how to integrate the JavaFX frontend with your existing airport simulation backend.

## Overview

The JavaFX frontend is designed to work with your existing simulation backend located in:
- `com.airport_sim_2.model.*`
- `com.airport_sim_2.objects.*`
- `com.airport_sim_2.queues.*`
- `com.airport_sim_2.events.*`

## Integration Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    JavaFX Frontend Layer                     │
├─────────────────────────────────────────────────────────────┤
│  MainController                                              │
│    ├── ConfigurationController  (User Input)                │
│    ├── SimulationViewController (Real-time Display)         │
│    └── ResultsController        (Analysis)                  │
├─────────────────────────────────────────────────────────────┤
│                    Backend Layer (Your Code)                 │
├─────────────────────────────────────────────────────────────┤
│  SimulationEngine (model package)                           │
│    ├── SimulationContext                                     │
│    ├── Event Queue Management                               │
│    └── Statistics Collection                                │
├─────────────────────────────────────────────────────────────┤
│  Domain Objects (objects package)                           │
│    ├── Aircraft                                              │
│    ├── Runway                                                │
│    ├── RunwayOpMode (enum)                                   │
│    └── AircraftStatus (enum)                                │
├─────────────────────────────────────────────────────────────┤
│  Queues (queues package)                                    │
│    ├── HoldingPattern                                        │
│    ├── TakeOffQueue                                          │
│    └── AircraftQueue (interface)                            │
├─────────────────────────────────────────────────────────────┤
│  Events (events package)                                    │
│    ├── Event (interface)                                     │
│    ├── Landing, EnterHP, LeaveHP                            │
│    ├── AircraftTakeOff, RunwayTakeOff                       │
│    └── Diversion, RunwayStatusChangeEvent                   │
└─────────────────────────────────────────────────────────────┘
```

## Step-by-Step Integration

### Step 1: Copy Backend Files

Copy your existing backend code into the frontend project:

```bash
# From your airport_sim_2 project
cp -r src/main/java/com/airport_sim_2/model \
      javafx-frontend/src/main/java/com/airport_sim_2/

cp -r src/main/java/com/airport_sim_2/objects \
      javafx-frontend/src/main/java/com/airport_sim_2/

cp -r src/main/java/com/airport_sim_2/queues \
      javafx-frontend/src/main/java/com/airport_sim_2/

cp -r src/main/java/com/airport_sim_2/events \
      javafx-frontend/src/main/java/com/airport_sim_2/
```

### Step 2: Verify Package Structure

Your directory should now look like:

```
javafx-frontend/src/main/java/com/airport_sim_2/
├── ui/                    # Frontend code (already present)
│   ├── AirportSimulationApp.java
│   ├── controllers/
│   └── components/
├── model/                 # Backend (copied from your project)
│   ├── SimulationEngine.java
│   ├── SimulationContext.java
│   └── ...
├── objects/               # Domain objects (copied)
│   ├── Aircraft.java
│   ├── Runway.java
│   └── ...
├── queues/                # Queue implementations (copied)
│   ├── HoldingPattern.java
│   ├── TakeOffQueue.java
│   └── ...
└── events/                # Event system (copied)
    ├── Event.java
    ├── Landing.java
    └── ...
```

### Step 3: Update MainController

The `MainController.startSimulation()` method already creates and runs the simulation:

```java
public void startSimulation(SimulationConfig config) {
    // Create simulation context from config
    simulationContext = createSimulationContext(config);
    
    // Create simulation engine
    simulationEngine = new SimulationEngine(
        config.getDuration() * 60.0, // Convert minutes to seconds
        simulationContext
    );
    
    // Pass to SimulationViewController
    simulationController.startSimulation(simulationEngine, simulationContext);
}
```

**No changes needed!** The controller is already set up to use your backend.

### Step 4: Configure Statistics Collection

To enable real-time updates, modify your `SimulationEngine` to support callbacks:

**Option A: Add listeners to SimulationEngine**

```java
public class SimulationEngine {
    private List<SimulationListener> listeners = new ArrayList<>();
    
    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }
    
    private void notifyAircraftArrived(Aircraft aircraft) {
        for (SimulationListener listener : listeners) {
            listener.onAircraftArrived(aircraft);
        }
    }
    
    // In your event processing:
    event.processEvent(this);
    notifyAircraftArrived(aircraft); // Notify UI
}
```

**Option B: Use existing StatisticsCollector**

The frontend includes `StatisticsCollector` in the controller package. You can use this to collect metrics during simulation.

### Step 5: Thread Safety for UI Updates

**Critical**: All UI updates must happen on the JavaFX Application Thread.

The `SimulationViewController` already handles this:

```java
// In SimulationViewController
simulationThread = new Thread(() -> {
    try {
        engine.run(); // Runs on background thread
        
        // UI updates wrapped in Platform.runLater()
        Platform.runLater(() -> {
            updateStatusIndicator("COMPLETED");
            logMessage("INFO", "Simulation completed");
        });
    } catch (Exception e) {
        e.printStackTrace();
    }
});
```

**What you need to do**: Ensure your `SimulationEngine.run()` method doesn't make any JavaFX calls directly. Use callbacks or polling instead.

### Step 6: Real-time Queue Updates

The `SimulationViewController.updateUI()` method polls queues every second:

```java
private void updateUI() {
    if (context == null) return;
    
    // Get queue sizes
    int hpSize = context.getHoldingPattern().size();
    int toSize = context.getTakeOffQueue().size();
    
    // Update tables
    updateHoldingPatternTable();
    updateTakeoffQueueTable();
}
```

**Requirements for your backend**:
1. `HoldingPattern` and `TakeOffQueue` must implement `getQueue()` method
2. `getQueue()` should return a thread-safe collection (or copy)

**Example implementation**:

```java
public class HoldingPattern implements AircraftQueue {
    private PriorityQueue<Aircraft> queue;
    
    @Override
    public Collection<Aircraft> getQueue() {
        // Return a copy for thread safety
        return new ArrayList<>(queue);
    }
}
```

### Step 7: Statistics for Results View

The `ResultsController` needs access to simulation statistics. 

**Recommended approach**: Extend `SimulationContext` to track statistics:

```java
public class SimulationContext {
    private List<Runway> runways;
    private HoldingPattern holdingPattern;
    private TakeOffQueue takeOffQueue;
    
    // Add statistics tracking
    private int totalArrived = 0;
    private int totalDeparted = 0;
    private int diversions = 0;
    private int maxHoldingQueueSize = 0;
    
    public void recordArrival() {
        totalArrived++;
    }
    
    public void recordDeparture() {
        totalDeparted++;
    }
    
    public void updateMaxQueueSize() {
        int currentSize = holdingPattern.size();
        if (currentSize > maxHoldingQueueSize) {
            maxHoldingQueueSize = currentSize;
        }
    }
    
    // Getters for statistics
    public int getTotalArrived() { return totalArrived; }
    public int getTotalDeparted() { return totalDeparted; }
    // ... etc
}
```

Then update `ResultsController.calculateAndDisplayMetrics()` to use these values:

```java
private void calculateAndDisplayMetrics() {
    totalAircraftLabel.setText(String.valueOf(
        context.getTotalArrived() + context.getTotalDeparted()
    ));
    diversionsCountLabel.setText(String.valueOf(context.getDiversions()));
    // ... etc
}
```

## Testing the Integration

### Test 1: Basic Simulation Run

1. Start the application
2. Keep default configuration (1 runway, 15/15 aircraft/hour, 120 min)
3. Click "Start Simulation"
4. Verify:
   - Simulation tab becomes active
   - Status changes to "RUNNING"
   - Log shows events being processed
   - Queues update (even if empty initially)

### Test 2: Queue Visibility

1. Configure simulation with high inbound rate (30 aircraft/hour)
2. Use single runway in TAKEOFF mode
3. Start simulation
4. Verify:
   - Holding pattern queue grows (aircraft can't land)
   - Table updates with aircraft details
   - Emergency highlighting works (when fuel low)

### Test 3: Results Display

1. Run a complete simulation (short duration, e.g., 30 minutes)
2. Wait for completion
3. Verify:
   - Results tab becomes active automatically
   - Summary metrics show data
   - Charts render (even with placeholder data)

## Common Integration Issues

### Issue: NullPointerException in getQueue()

**Cause**: Queue not initialized in SimulationContext

**Solution**:
```java
public SimulationContext(List<Runway> runways) {
    this.runways = runways;
    this.holdingPattern = new HoldingPattern();  // Initialize!
    this.takeOffQueue = new TakeOffQueue();      // Initialize!
}
```

### Issue: UI freezes during simulation

**Cause**: Simulation running on JavaFX Application Thread

**Solution**: The `SimulationViewController` already runs simulation in a background thread. Don't change this!

### Issue: Tables not updating

**Cause**: `getQueue()` returns null or throws exception

**Solution**: Add null checks:
```java
private void updateHoldingPatternTable() {
    holdingPatternList.clear();
    
    if (context.getHoldingPattern() == null) return;
    
    Collection<Aircraft> queue = context.getHoldingPattern().getQueue();
    if (queue == null) return;
    
    for (Aircraft aircraft : queue) {
        // ... populate table
    }
}
```

### Issue: ConcurrentModificationException

**Cause**: Frontend reading queue while backend modifies it

**Solution**: Return defensive copy from `getQueue()`:
```java
@Override
public Collection<Aircraft> getQueue() {
    synchronized(queue) {
        return new ArrayList<>(queue);
    }
}
```

## Performance Considerations

### UI Update Frequency

The frontend updates UI every 1 second. For long simulations (>4 hours), consider:

1. **Reducing update frequency**:
```java
// In SimulationViewController
updateTimeline = new Timeline(new KeyFrame(
    Duration.seconds(2), // Change from 1 to 2 seconds
    event -> updateUI()
));
```

2. **Throttling queue displays**:
```java
// Only show first 50 aircraft in tables
private void updateHoldingPatternTable() {
    Collection<Aircraft> queue = context.getHoldingPattern().getQueue();
    int count = 0;
    for (Aircraft aircraft : queue) {
        if (count++ >= 50) break; // Limit display
        // Add to table...
    }
}
```

### Memory Management

For simulations processing 500+ aircraft:

1. Clear logs periodically:
```java
// In SimulationViewController
public void logMessage(String level, String message) {
    Platform.runLater(() -> {
        String logText = simulationLog.getText();
        if (logText.split("\n").length > 1000) {
            // Keep only last 500 lines
            simulationLog.clear();
        }
        // Add new message...
    });
}
```

## Next Steps

Once integration is complete:

1. ✅ Test with small simulations (30 min, 2 aircraft/hour)
2. ✅ Gradually increase complexity
3. ✅ Implement actual statistics calculation in ResultsController
4. ✅ Add export functionality (PDF/CSV)
5. ✅ Consider adding runway visualization component

## Support

If you encounter issues during integration:

1. Check that all backend classes are copied correctly
2. Verify package names match: `com.airport_sim_2.*`
3. Ensure all backend dependencies are in `pom.xml`
4. Review console output for stack traces
5. Add debug logging to your `SimulationEngine.run()` method

---

**Integration Version**: 1.0  
**Last Updated**: March 2026
