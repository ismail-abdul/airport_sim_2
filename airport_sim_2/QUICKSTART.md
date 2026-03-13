# Quick Start Guide - Airport Simulation JavaFX Frontend

## 🚀 Getting Started in 5 Minutes

### Step 1: Prerequisites Check

Ensure you have:
```bash
java -version  # Should show Java 17 or higher
mvn -version   # Should show Maven 3.6+
```

If not installed:
- **Java 17**: Download from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)
- **Maven**: Download from [Apache Maven](https://maven.apache.org/download.cgi)

### Step 2: Project Setup

```bash
# Navigate to the frontend directory
cd javafx-frontend

# Build the project
mvn clean install
```

### Step 3: Run the Application

```bash
# Start the application
mvn javafx:run
```

The application window should appear within a few seconds!

## 📋 First Simulation

### 1. Configure Your Airport (Configuration Tab)

**Default Setup** - The app starts with one runway configured:
- Runway: 01L
- Mode: MIXED
- Status: AVAILABLE

**Add More Runways** (Optional):
1. Click "Add Runway" button
2. Double-click cells in the table to edit
3. Change mode to LANDING, TAKEOFF, or MIXED
4. Set status as needed

**Set Traffic Parameters**:
- Inbound Rate: `15` aircraft/hour (default)
- Outbound Rate: `15` aircraft/hour (default)  
- Duration: `120` minutes (default)

### 2. Start Simulation

Click the big blue **"Start Simulation"** button

The app will:
- ✅ Validate your configuration
- ✅ Switch to Simulation tab automatically
- ✅ Begin running the simulation

### 3. Monitor Progress (Simulation Tab)

Watch the simulation in real-time:

**Control Panel**:
- ▶ Play - Resume if paused
- ⏸ Pause - Pause simulation
- ⏹ Stop - End simulation early

**Live Metrics**:
- Total arrived/departed aircraft
- Current queue sizes
- Diversions and cancellations

**Aircraft Queues**:
- **Holding Pattern**: Aircraft waiting to land
- **Takeoff Queue**: Aircraft waiting to depart
- Emergency aircraft highlighted in RED

**Simulation Log**:
- Timestamped events scroll at the bottom

### 4. View Results (Results Tab)

When simulation completes, you'll automatically see:

**Summary Cards**:
- Total aircraft processed
- Average wait times
- Maximum queue lengths
- Performance metrics

**Charts**:
- Queue length over time
- Delay distribution
- Cumulative throughput

**Detailed Report**:
- Executive summary
- Runway performance breakdown
- Recommendations

**Export Options**:
- Export to PDF (coming soon)
- Export to CSV (coming soon)

### 5. Run Another Simulation

Click **"New Simulation"** button to start fresh!

## 🎯 Quick Tips

### Understanding Runway Modes

| Mode | Description | Use Case |
|------|-------------|----------|
| **LANDING** | Only arrivals | During peak arrival times |
| **TAKEOFF** | Only departures | During peak departure times |
| **MIXED** | Both operations | Balanced traffic |

### Understanding Runway Status

| Status | Meaning | Visual Indicator |
|--------|---------|-----------------|
| **AVAILABLE** | Fully operational | Green background |
| **RUNWAY_INSPECTION** | Temporary closure | Amber background |
| **SNOW_CLEARANCE** | Weather maintenance | Amber background |
| **EQUIPMENT_FAILURE** | Out of service | Red background |

### Emergency Situations

Aircraft with **low fuel** (<10 units):
- Automatically marked as FUEL emergency
- Status changes from NORMAL to FUEL
- Highlighted in RED in the holding pattern table
- Given priority in landing queue

## 🔧 Common Issues & Solutions

### Issue: Application won't start

**Solution**:
```bash
# Try running with explicit JavaFX module path
export PATH_TO_FX=/path/to/javafx-sdk-17/lib
mvn javafx:run
```

### Issue: "Module not found" error

**Solution**:
```bash
# Rebuild with dependencies
mvn clean install -U
```

### Issue: Tables not updating during simulation

**Solution**: This is normal - tables update every 1 second. Wait a moment to see changes.

### Issue: Charts show no data

**Solution**: This is expected in v1.0 - the charts use placeholder data. Integration with actual simulation statistics is coming in a future update.

## 📊 Example Configurations

### Small Airport (Single Runway)
```
Runways: 1
Inbound: 10 aircraft/hour
Outbound: 10 aircraft/hour
Duration: 60 minutes
```

### Medium Airport (Two Runways)
```
Runways: 2
- Runway 09L: LANDING
- Runway 27R: TAKEOFF
Inbound: 20 aircraft/hour
Outbound: 20 aircraft/hour
Duration: 120 minutes
```

### Large Airport (Four Runways)
```
Runways: 4
- Runway 09L: LANDING
- Runway 09R: LANDING
- Runway 27L: TAKEOFF
- Runway 27R: MIXED
Inbound: 30 aircraft/hour
Outbound: 25 aircraft/hour
Duration: 180 minutes
```

## 🎓 Learning More

### Next Steps
1. Experiment with different runway configurations
2. Try varying traffic rates
3. Test emergency scenarios (close runways mid-simulation using status changes)
4. Analyze the results charts to understand bottlenecks

### Documentation
- Full README.md in the project root
- Design Specification document
- Inline code comments in all controllers

## 🆘 Need Help?

1. Check the full README.md for detailed documentation
2. Review the Design Specification document
3. Examine the FXML files to understand the UI layout
4. Look at controller code comments for logic explanations

---

**Enjoy simulating! ✈️🛫🛬**

Version 1.0 | March 2026
