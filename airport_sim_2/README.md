# Airport Simulation System - JavaFX Frontend

A comprehensive JavaFX-based frontend for visualizing and managing airport simulation operations. This application provides real-time monitoring, configuration management, and detailed analysis of airport traffic flow.

## Features

### 🛫 Configuration View
- Configure up to 10 runways with individual settings
- Set runway operational modes (Landing, Takeoff, Mixed)
- Define runway status (Available, Under Inspection, Snow Clearance, etc.)
- Configure traffic flow parameters (inbound/outbound rates)
- Set simulation duration
- Input validation and error handling

### ✈️ Simulation View
- **Real-time Monitoring**
  - Live aircraft tracking in holding patterns
  - Takeoff queue visualization
  - Real-time metrics dashboard
  - Elapsed time tracking
  
- **Control Panel**
  - Play/Pause/Stop simulation controls
  - Status indicators
  - Event logging with timestamps

- **Aircraft Queues**
  - Holding Pattern Queue with priority sorting
  - Takeoff Queue with FIFO ordering
  - Emergency highlighting (fuel emergencies shown in red)
  - Live updates as aircraft enter/leave queues

### 📊 Results & Analysis View
- **Summary Metrics**
  - Total aircraft processed
  - Average wait times
  - Maximum queue lengths
  - Diversions and cancellations
  - Runway utilization percentages

- **Performance Charts**
  - Queue length over time (line chart)
  - Delay distribution (bar chart)
  - Cumulative throughput (line chart)

- **Runway Performance Table**
  - Per-runway statistics
  - Operation counts
  - Utilization metrics

- **Export Capabilities**
  - Export results to PDF
  - Export data to CSV
  - Detailed text reports

## Prerequisites

- **Java Development Kit (JDK) 17 or higher**
- **Apache Maven 3.6+**
- **JavaFX 17.0.10** (included via Maven dependencies)

## Project Structure

```
javafx-frontend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/airport_sim_2/
│   │   │       ├── ui/
│   │   │       │   ├── AirportSimulationApp.java       # Main application
│   │   │       │   ├── controllers/
│   │   │       │   │   ├── MainController.java         # Main navigation
│   │   │       │   │   ├── ConfigurationController.java # Config view
│   │   │       │   │   ├── SimulationViewController.java # Simulation view
│   │   │       │   │   └── ResultsController.java       # Results view
│   │   │       │   ├── views/
│   │   │       │   └── components/
│   │   │       ├── model/                  # Backend simulation engine
│   │   │       ├── objects/                # Aircraft, Runway classes
│   │   │       ├── queues/                 # Queue implementations
│   │   │       └── events/                 # Event system
│   │   └── resources/
│   │       ├── fxml/
│   │       │   ├── main-layout.fxml
│   │       │   ├── configuration-view.fxml
│   │       │   ├── simulation-view.fxml
│   │       │   └── results-view.fxml
│   │       ├── css/
│   │       │   └── styles.css              # Application styling
│   │       └── icons/
│   └── test/
└── pom.xml
```

## Installation & Setup

### 1. Clone or Extract the Project

```bash
cd javafx-frontend
```

### 2. Build with Maven

```bash
mvn clean install
```

### 3. Run the Application

**Option A: Using Maven (Recommended)**
```bash
mvn javafx:run
```

**Option B: Using Java directly**
```bash
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/airport_sim_2-1.0-SNAPSHOT.jar
```

## Integration with Existing Backend

This frontend is designed to integrate with your existing Java simulation backend located in:
- `com.airport_sim_2.model.*` - Simulation engine
- `com.airport_sim_2.objects.*` - Domain objects
- `com.airport_sim_2.queues.*` - Queue management
- `com.airport_sim_2.events.*` - Event system

### Integration Steps:

1. **Copy Backend Files**
   - Copy your existing backend packages (`model`, `objects`, `queues`, `events`) into the `src/main/java/com/airport_sim_2/` directory

2. **Update Dependencies**
   - Ensure all backend dependencies are included in `pom.xml`

3. **Connect Controllers**
   - `SimulationViewController` calls `SimulationEngine.run()`
   - Real-time updates use `Platform.runLater()` for thread safety
   - Queue updates pull from `SimulationContext.getHoldingPattern()` and `getTakeOffQueue()`

## Usage Guide

### Starting a Simulation

1. **Configure Runways**
   - Click "Add Runway" to add runways (up to 10)
   - Edit runway properties directly in the table
   - Set Mode (LANDING, TAKEOFF, MIXED)
   - Set Status (AVAILABLE, etc.)

2. **Set Traffic Parameters**
   - Enter inbound aircraft rate (aircraft/hour)
   - Enter outbound aircraft rate (aircraft/hour)
   - Set simulation duration (minutes)

3. **Start Simulation**
   - Click "Start Simulation"
   - Application validates configuration
   - Switches to Simulation View automatically

### Monitoring Simulation

- **Control Buttons**: Use Play/Pause/Stop to control simulation
- **Metrics Dashboard**: View real-time statistics
- **Aircraft Tables**: Monitor holding pattern and takeoff queues
- **Simulation Log**: View timestamped events

### Analyzing Results

After simulation completes:
- Review summary metrics
- Examine performance charts
- Check runway performance table
- Read detailed report
- Export data for further analysis

## Customization

### Styling

Edit `src/main/resources/css/styles.css` to customize:
- Color scheme (defined in `:root` CSS variables)
- Button styles
- Table appearance
- Chart colors
- Metric card designs

### Adding Features

1. **New Metrics**: Extend `SimulationViewController` to track additional data
2. **Custom Charts**: Add new chart types in `ResultsController`
3. **Export Formats**: Implement PDF/CSV export in `ResultsController`

## Design Specification

This frontend implementation is based on the design specification document included in your project. Key design decisions:

- **Color Palette**: Uses blue (#3B82F6) for primary actions, green (#10B981) for success, red (#EF4444) for errors/emergencies
- **Layout**: BorderPane-based three-tab navigation
- **Responsiveness**: Minimum 1024x768, scales to 4K
- **Accessibility**: Full keyboard navigation support

## Troubleshooting

### JavaFX Not Found
```bash
# Ensure JavaFX modules are accessible
export PATH_TO_FX=/path/to/javafx-sdk-17/lib
mvn javafx:run
```

### Module Errors
```bash
# Add required modules explicitly
--add-modules javafx.controls,javafx.fxml,javafx.graphics
```

### Build Failures
```bash
# Clean and rebuild
mvn clean
mvn install -U
```

## Future Enhancements

Planned features for future releases:

- [ ] 3D runway visualization
- [ ] Animation of aircraft movements
- [ ] Configuration presets (Heathrow-style, Gatwick-style)
- [ ] Event injection during simulation (manual emergencies)
- [ ] Comparison mode (multiple simulation runs)
- [ ] Real-time speed multiplier (2x, 10x, etc.)
- [ ] PDF/CSV export implementation
- [ ] Weather effects modeling
- [ ] Integration with real flight data APIs

## Contributing

To contribute to this project:

1. Follow the existing code style
2. Add JavaDoc comments to public methods
3. Update FXML files for UI changes
4. Test on multiple screen sizes
5. Ensure accessibility standards

## License

This project is part of the Airport Simulation coursework.

## Contact

For questions or issues related to the frontend implementation, please refer to:
- Design specification document
- Inline code comments
- FXML layout files

---

**Version**: 1.0  
**JavaFX Version**: 17.0.10  
**Target JDK**: 17+  
**Last Updated**: March 2026
