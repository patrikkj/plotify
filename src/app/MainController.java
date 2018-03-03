package app;

import java.io.File;
import java.io.FilenameFilter;

import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import enums.Inertia;
import enums.Integration;
import enums.Interpolation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class MainController {
	//// FXML fields
	// ROOT 
    @FXML private VBox rootNode;

    
    // CHART
    @FXML private StackPane chartPane;
    private LineChart<Number, Number> lineChart;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    
    // Chart properties
    @FXML private JFXTextField chartTitle;
    @FXML private JFXTextField chartWidth;
    @FXML private JFXTextField chartHeight;
    
    // X-Axis properties
    @FXML private JFXTextField xAxisName;
    @FXML private JFXTextField xAxisTickSize;
    @FXML private JFXTextField xAxisMinRange;
    @FXML private JFXTextField xAxisMaxRange;
    
    // Y-Axis properties
    @FXML private JFXTextField yAxisName;
    @FXML private JFXTextField yAxisTickSize;
    @FXML private JFXTextField yAxisMinRange;
    @FXML private JFXTextField yAxisMaxRange;
    
    
    // TRACES
    @FXML private JFXListView<Trace> traceListView;
    
    // Trace properties
    @FXML private JFXTextField traceName;
    @FXML private JFXComboBox<File> traceFile;
    @FXML private JFXComboBox<Integration> traceIntegration;
    @FXML private JFXComboBox<Interpolation> traceInterpolation;
    @FXML private JFXComboBox<Inertia> traceInertia;
    @FXML private JFXTextField traceMass;
    @FXML private JFXTextField traceRadius;
    @FXML private JFXTextField traceMinX;
    @FXML private JFXTextField traceMaxX;
    @FXML private JFXTextField traceInitV;
    @FXML private JFXTextField traceStep;
    
    // Trace details
    @FXML private Label funcTypeLabel;
    @FXML private Label integrationTypeLabel;
    @FXML private Label stepSizeLabel;
    @FXML private Label iterationsLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Label computationTimeLabel;
    @FXML private Label energyDifferenceLabel;

    
    // GRAPHS
    @FXML private JFXListView<Graph> graphListView;
    
    // Graph properties
    @FXML private JFXTextField graphName;
    @FXML private JFXComboBox<?> graphXData;
    @FXML private JFXComboBox<?> graphYData;
    @FXML private JFXComboBox<?> graphTrace;
    @FXML private JFXTextField graphXMin;
    @FXML private JFXTextField graphXMax;
    @FXML private JFXTextField graphYMin;
    @FXML private JFXTextField graphYMax;
    
    // Graph layout
    @FXML private JFXComboBox<?> graphStyle;
    @FXML private JFXColorPicker graphColorPicker;
    @FXML private JFXSlider graphDetailSlider;
    @FXML private JFXToggleButton graphPointsToggleButton;
    @FXML private JFXSlider graphStrokeSlider;
    @FXML private JFXToggleButton graphSmoothToggleButton;
    @FXML private JFXToggleButton graphVisibleToggleButton;
    
    
    //// NON-FXML FIELDS
    // Property converter
    private StringConverter<Double> customStringConverter;
    
    // Cached trace (Used to manage property bindings)
    private Trace selectedTrace;
    private Trace prevTrace;

    // Cached graph (Used to manage property bindings)
    private Graph selectedGraph;
    private Graph prevGraph;
    
    //  Observable lists
    private ObservableList<Trace> traceList;
    private ObservableList<Graph> graphList;
    private ObservableList<File> fileList;
    
    
    // Initialization
    /**
     * Initializes application, called after FXML fields has been invoked.
     */
	@FXML private void initialize() {    
    	// Initialize converter (used for trace bindings)
    	customStringConverter = new StringConverter<>() {
			@Override 
			public Double fromString(String arg0) {
				return (arg0.equals("")) ? null : Double.valueOf(arg0);}

			@Override
			public String toString(Double arg0) {
				return (arg0 == null) ? null : String.valueOf(arg0);}
		};
		
    	initializeLists();
    	initializeTraceView();
    	initializeGraphView();
    	
    }
    
	private void initializeTraceView() {
		// Set default trace
    	traceList.add(new Trace());
    	traceListView.getSelectionModel().selectFirst();
    	
    	// Add name listener
		traceName.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				traceListView.refresh();
			}
		});
    	
    	// Updaters
    	updateTraceView();
	}

	private void initializeGraphView() {
		// Initialize chart
		xAxis = new NumberAxis();
		yAxis = new NumberAxis();
		lineChart = new LineChart<>(xAxis, yAxis);
		
		// Set chart properties
		xAxis.setLabel("xAxis");
		yAxis.setLabel("yAxis");
		lineChart.setTitle("My Chart");
		
		// Add chart to GUI
		chartPane.getChildren().setAll(lineChart);
		
		// Set default graph
		graphList.add(new Graph());
		graphListView.getSelectionModel().selectFirst();
		
		// Add name listener
		graphName.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				graphListView.refresh();
			}
		});
		
		// Updaters
		updateGraphView();
	}
	
	/**
     * Initializes and binds ListViews, loads imported files and initializes ChoiceBoxes.
     */
    private void initializeLists() {
    	// Initialize observable lists
    	traceList = FXCollections.observableArrayList();
    	graphList = FXCollections.observableArrayList();
    	fileList = FXCollections.observableArrayList();
    	
    	// Import tracker files from 'import'
    	File importFolder = new File(getClass().getResource("../imports").getPath());
    	FilenameFilter fileFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".txt");
			}
		};
    	for (File dataFile : importFolder.listFiles(fileFilter))
    		fileList.add(dataFile);
    	
    	// Bind observable lists
    	traceListView.setItems(traceList);
    	graphListView.setItems(graphList);
    	traceFile.setItems(fileList);
    	
    	// Fill choiceBoxes
    	traceIntegration.setItems(FXCollections.observableList(Integration.getElements()));
        traceInterpolation.setItems(FXCollections.observableList(Interpolation.getElements()));
        traceInertia.setItems(FXCollections.observableList(Inertia.getElements()));
        
    }
    
    
    // GUI Updates
    /**
     * Manages trace bindings and updates trace details.
     * Called when selected trace has been changed.
     */
    // Updaters
	private void updateTraceView() {
		// Retrive selected list entry
		selectedTrace = traceListView.getSelectionModel().getSelectedItem();
		
		// Unbind previous trace
		if (prevTrace != null) {
			traceName				.textProperty().unbindBidirectional(prevTrace.getNameProperty());
		    traceFile				.valueProperty().unbindBidirectional(prevTrace.getFileProperty());
		    traceIntegration		.valueProperty().unbindBidirectional(prevTrace.getIntegrationProperty());
		    traceInterpolation		.valueProperty().unbindBidirectional(prevTrace.getInterpolationProperty());
		    traceInertia			.valueProperty().unbindBidirectional(prevTrace.getInertiaProperty());
		    traceMass				.textProperty().unbindBidirectional(prevTrace.getMassProperty());
		    traceRadius				.textProperty().unbindBidirectional(prevTrace.getRadiusProperty());
		    traceMinX				.textProperty().unbindBidirectional(prevTrace.getMinXProperty());
		    traceMaxX				.textProperty().unbindBidirectional(prevTrace.getMaxXProperty());
		    traceInitV				.textProperty().unbindBidirectional(prevTrace.getInitVProperty());
		    traceStep				.textProperty().unbindBidirectional(prevTrace.getStepProperty());
		}
		
		// Cache selected trace
		prevTrace = selectedTrace;
		
		// Bind trace properties
		traceName				.textProperty().bindBidirectional(selectedTrace.getNameProperty());
	    traceFile				.valueProperty().bindBidirectional(selectedTrace.getFileProperty());
	    traceIntegration		.valueProperty().bindBidirectional(selectedTrace.getIntegrationProperty());
	    traceInterpolation		.valueProperty().bindBidirectional(selectedTrace.getInterpolationProperty());
	    traceInertia			.valueProperty().bindBidirectional(selectedTrace.getInertiaProperty());
	    traceMass				.textProperty().bindBidirectional(selectedTrace.getMassProperty(), customStringConverter);
	    traceRadius				.textProperty().bindBidirectional(selectedTrace.getRadiusProperty(), customStringConverter);
	    traceMinX				.textProperty().bindBidirectional(selectedTrace.getMinXProperty(), customStringConverter);
	    traceMaxX				.textProperty().bindBidirectional(selectedTrace.getMaxXProperty(), customStringConverter);
	    traceInitV				.textProperty().bindBidirectional(selectedTrace.getInitVProperty(), customStringConverter);
	    traceStep				.textProperty().bindBidirectional(selectedTrace.getStepProperty(), customStringConverter);
		
		// Update trace details
		if (selectedTrace.isInitialized()) {
		    funcTypeLabel			.setText(selectedTrace.getInterpolationType());
		    integrationTypeLabel	.setText(selectedTrace.getIntegrationType());
		    stepSizeLabel			.setText(selectedTrace.getStepSize());
		    iterationsLabel			.setText(selectedTrace.getIterations());
		    totalTimeLabel			.setText(selectedTrace.getTotalTime());
		    computationTimeLabel	.setText(selectedTrace.getComputationTime());
		    energyDifferenceLabel	.setText(selectedTrace.getEnergyDifference());
		}
    }
    
	private void updateGraphView() {
		// Retrive selected list entry
		selectedGraph = graphListView.getSelectionModel().getSelectedItem();
		
		// Bind graph properties
		
		lineChart.getData().add(selectedGraph.getSeries());
    }
    
 
    
    
    
    
    //// Button handlers
    // Main menu button handlers
    @FXML private void handleNewProjectClick(ActionEvent event) {}
    
    @FXML private void handleSaveClick(ActionEvent event) {}
    
    @FXML private void handleLoadClick(ActionEvent event) {}
    
    @FXML private void handleImportClick(ActionEvent event) {}
    
    @FXML private void handleExportClick(ActionEvent event) {}
    
    
    // Trace view button handlers
    // Trace button handlers
    @FXML private void handleNewTraceClick(ActionEvent event) {
    	traceList.add(new Trace());
    }
    
    @FXML private void handleDeleteTraceClick(ActionEvent event) {
    	
    	System.out.println(traceListView.getSelectionModel().getSelectedItem());
    	System.out.printf("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n",
    			selectedTrace.getName(),
    			selectedTrace.getFile(),
    			selectedTrace.getIntegration(),
    			selectedTrace.getInterpolation(),
    			selectedTrace.getInertia(),
    			selectedTrace.getMass(),
    			selectedTrace.getMinX(),
    			selectedTrace.getMaxX(),
    			selectedTrace.getInitV(),
    			selectedTrace.getStep());
    }
    
    @FXML private void handleComputeClick(ActionEvent event) {
    	// Run trace
    	selectedTrace.trace(false, true);

    	// Update
    	updateTraceView();
    }
    
    @FXML private void handleComputeAllClick(ActionEvent event) {}
    
    
    // Graph view button handlers
    // Graph button handlers
    @FXML private void handleNewGraphClick(ActionEvent event) {
    	graphList.add(new Graph());
    	
    	updateGraphView();
    }
    private double xval = 0;
    private double yval = 0;
    @FXML private void handleDeleteGraphClick(ActionEvent event) {
    	selectedGraph.getSeries().getNode().setStyle("-fx-stroke: #450000;");
    	lineChart.setCreateSymbols(false);
    	for (int i = 0; i < 100; i++) {
    		xval += 0.1;
    		yval += Math.random() - 0.5d;
    		selectedGraph.getSeries().getData().add(new XYChart.Data<Number, Number>(xval, yval));
    	}
    }
    
    @FXML private void handleGraphUpClick(ActionEvent event) {
    	selectedGraph.setDefaultSeries();
    }
    
    @FXML private void handleGraphDownClick(ActionEvent event) {}

    
    // Other
    // Trace file opener
    @FXML private void handleFileOpenClick(ActionEvent event) {
    	// Retrive parent for file chooser
    	Stage mainStage = (Stage) rootNode.getScene().getWindow();
    	
    	// Construct file chooser
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Data File");
    	fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("Tracker file (*.txt)", "*.txt"),
    	         new ExtensionFilter("All Files", "*.*"));
    	
    	// Launch file chooser and retrive selected file
    	File selectedFile = fileChooser.showOpenDialog(mainStage);
    	
    	// Add file to fileList and update trace file
    	fileList.add(selectedFile);
    	selectedTrace.setFile(selectedFile);
    }
    
    @FXML private void handleTraceListClick(Event event) {
    	updateTraceView();
    }
    
    @FXML private void handleGraphListClick(Event event) {
    }
}
