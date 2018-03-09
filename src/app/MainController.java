package app;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import enums.Inertia;
import enums.Integration;
import enums.Interpolation;
import enums.Style;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
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
    @FXML private JFXTabPane traceTabPane;
    // Trace properties
    @FXML private JFXTextField traceName;
    @FXML private JFXComboBox<File> traceFile;
    @FXML private JFXComboBox<Integration> traceIntegration;
    @FXML private JFXComboBox<Interpolation> traceInterpolation;
    @FXML private JFXComboBox<Inertia> traceInertia;
    @FXML private JFXTextField traceMass;
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
    @FXML private JFXComboBox<String> graphXData;
    @FXML private JFXComboBox<String> graphYData;
    @FXML private JFXComboBox<Trace> graphTrace;
    @FXML private JFXTextField graphMinX;
    @FXML private JFXTextField graphMaxX;
    // Graph layout
    @FXML private JFXComboBox<Style> graphStyle;
    @FXML private JFXColorPicker graphColorPicker;
    @FXML private JFXSlider graphDetailSlider;
    @FXML private JFXToggleButton graphPointsToggleButton;
    @FXML private JFXSlider graphStrokeSlider;
    @FXML private JFXToggleButton graphSmoothToggleButton;
    @FXML private JFXToggleButton graphVisibleToggleButton; 
    
    //// NON-FXML FIELDS
    /**
     * Used in Bidirectional bindings to convert String <-> Double.
     * <dt>String to Double:</dt>
     * <li>Allows both ',' and '.' as decimal separator.
     * <li>Returns null if String is empty ("").
     * <br></br>
     * <dt>Double to String:</dt>
     * <li>Returns null if Double is null.
     */
    private StringConverter<Double> customStringConverter;
    
    /*
     * Cached traces and graphs, used to manage property bindings.
     */
    private Trace selectedTrace;
    private Trace prevTrace;
    private Graph selectedGraph;
    private Graph prevGraph;
    
    /*
     * Observable lists used in ListViews and ChoiceBoxes
     */
    private ObservableList<Trace> traceList;
    private ObservableList<Graph> graphList;
    private ObservableList<File> fileList;
    private ObservableList<String> dataList;
    
    /*
     * Listener list for cells within choiceBox
     */
    private List<ListCell<Trace>> listenerList = new ArrayList<>();
    
    // Initialization
    /**
     * Initializes application, called after FXML fields has been invoked.
     */
	@FXML private void initialize() {    
    	// Initialize converter (used for trace bindings)
    	customStringConverter = new StringConverter<>() {
			@Override
			public Double fromString(String arg0) {
				return (arg0.equals("")) ? null : Double.valueOf(arg0.replace(',', '.'));}
			
			@Override
			public String toString(Double arg0) {
				return (arg0 == null) ? null : String.valueOf(arg0);}
		};

    	initializeLists();
    	initializeTraceView();
    	initializeGraphView();
    	
    }

	/**
	 * Initializes trace view and adds default trace to listView. 
	 */
	private void initializeTraceView() {
		// Set default trace
    	traceList.add(new Trace());
    	traceListView.getSelectionModel().selectFirst();
    	
    	// Add name listener
		traceName.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// Update ListView entries
				traceListView.refresh();
				
				// Update choicebox entries
				listenerList.stream()
					.filter(cell -> cell != null)
					.filter(cell -> cell.getItem() != null)
					.forEach(cell -> cell.setText(cell.getItem().getName()));
				graphTrace.getButtonCell().setText(selectedGraph.getTrace().getName());
			}
		});
    	
    	// Updaters
    	updateTraceView();
	}

	/**
	 * Initializes chart, sets default chart and graph properties.
	 */
	private void initializeGraphView() {
		// Initialize chart
		xAxis = new NumberAxis();
		yAxis = new NumberAxis();
		lineChart = new LineChart<>(xAxis, yAxis);
		
		// Set chart properties
		xAxis.setLabel("xAxis");
		yAxis.setLabel("yAxis");
		lineChart.setTitle("My Chart");
		lineChart.setCreateSymbols(false);
		lineChart.setAnimated(false);
		
		// Add chart to GUI
		chartPane.getChildren().setAll(lineChart);
		
		// Set default graph
		graphList.add(new Graph(selectedTrace));
		graphListView.getSelectionModel().selectFirst();
		
		// Set graph trace cell factory
		graphTrace.setCellFactory(new Callback<ListView<Trace>, ListCell<Trace>>(){
			@Override
            public ListCell<Trace> call(ListView<Trace> arg) {
                ListCell<Trace> cell = new JFXListCell<Trace>(); 
                listenerList.add(cell);
                return cell;
            }
		});

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
    	dataList = FXCollections.observableList(Arrays.asList(Trace.MAP_KEYS));
    	
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
    	
    	// Fill trace choiceBoxes
    	traceIntegration.setItems(FXCollections.observableList(Integration.getElements()));
        traceInterpolation.setItems(FXCollections.observableList(Interpolation.getElements()));
        traceInertia.setItems(FXCollections.observableList(Inertia.getElements()));
        
        // Fill graph choiceBoxes
        graphTrace.setItems(traceList);
        graphXData.setItems(dataList);
        graphYData.setItems(dataList);
        graphStyle.setItems(FXCollections.observableList(Style.getElements()));
    }
    
    
    // GUI Update
    /**
     * Manages trace bindings and updates trace details.
     * Called when selected trace has been changed.
     */
	private void updateTraceView() {
		// Retrive selected list entry
		selectedTrace = traceListView.getSelectionModel().getSelectedItem();
		
		// Unbind previous trace
		if (prevTrace != null)
			unbindTrace();
		
		// Cache selected trace
		prevTrace = selectedTrace;
		
		// Bind selected trace
		bindTrace();
    }
    
	/**
     * Manages graph bindings and updates graph details.
     * Called when selected graph has been changed.
     */
	private void updateGraphView() {
		// Retrive selected list entry
		selectedGraph = graphListView.getSelectionModel().getSelectedItem();
		
		// Unbind previous graph
		if (prevGraph != null)
			unbindGraph();
		
		// Cache selected graph
		prevGraph = selectedGraph;
		
		// Bind selected graph
		bindGraph();
		
		// Prevent duplicate graphs
		if (!lineChart.getData().contains(selectedGraph.getSeries()))
			lineChart.getData().add(selectedGraph.getSeries());
    }

	
	// Manage bindings
	/**
	 * Bind TraceView inputs to selected trace.
	 */
	private void bindTrace() {
		// Bind trace properties
		traceName					.textProperty().bindBidirectional(selectedTrace.getNameProperty());
	    traceFile					.valueProperty().bindBidirectional(selectedTrace.getFileProperty());
	    traceIntegration			.valueProperty().bindBidirectional(selectedTrace.getIntegrationProperty());
	    traceInterpolation			.valueProperty().bindBidirectional(selectedTrace.getInterpolationProperty());
	    traceInertia				.valueProperty().bindBidirectional(selectedTrace.getInertiaProperty());
	    traceMass					.textProperty().bindBidirectional(selectedTrace.getMassProperty(), customStringConverter);
//	    traceRadius					.textProperty().bindBidirectional(selectedTrace.getRadiusProperty(), customStringConverter);
	    traceMinX					.textProperty().bindBidirectional(selectedTrace.getMinXProperty(), customStringConverter);
	    traceMaxX					.textProperty().bindBidirectional(selectedTrace.getMaxXProperty(), customStringConverter);
	    traceInitV					.textProperty().bindBidirectional(selectedTrace.getInitVProperty(), customStringConverter);
	    traceStep					.textProperty().bindBidirectional(selectedTrace.getStepProperty(), customStringConverter);
		
		// Set trace details
	    funcTypeLabel.textProperty().bind(selectedTrace.getInterpolationTypeProperty());
	    integrationTypeLabel.textProperty().bind(selectedTrace.getIntegrationTypeProperty());
	    stepSizeLabel.textProperty().bind(selectedTrace.getStepSizeProperty());
	    iterationsLabel.textProperty().bind(selectedTrace.getIterationsProperty());
	    totalTimeLabel.textProperty().bind(selectedTrace.getTotalTimeProperty());
	    computationTimeLabel.textProperty().bind(selectedTrace.getComputationTimeProperty());
	    energyDifferenceLabel.textProperty().bind(selectedTrace.getEnergyDifferenceProperty());
//		funcTypeLabel			.setText(selectedTrace.getInterpolationType() != null ? selectedTrace.getInterpolationType() : "-");
//		integrationTypeLabel	.setText(selectedTrace.getIntegrationType() != null ? selectedTrace.getIntegrationType() : "-");
//		stepSizeLabel			.setText(selectedTrace.getStepSize() != null ? selectedTrace.getStepSize() : "-");
//		iterationsLabel			.setText(selectedTrace.getIterations() != null ? selectedTrace.getIterations() : "-");
//		totalTimeLabel			.setText(selectedTrace.getTotalTime() != null ? selectedTrace.getTotalTime() : "-");
//		computationTimeLabel	.setText(selectedTrace.getComputationTime() != null ? selectedTrace.getComputationTime() : "-");
//		energyDifferenceLabel	.setText(selectedTrace.getEnergyDifference() != null ? selectedTrace.getEnergyDifference() : "-");
	}
	
	/**
	 * Bind GraphView inputs to selected graph.
	 */
	private void bindGraph() {
		// Bind graph properties
		graphName					.textProperty().bindBidirectional(selectedGraph.getNameProperty());
		graphXData					.valueProperty().bindBidirectional(selectedGraph.getXDataProperty());
		graphYData					.valueProperty().bindBidirectional(selectedGraph.getYDataProperty());
		graphTrace					.valueProperty().bindBidirectional(selectedGraph.getTraceProperty());
		graphMinX					.textProperty().bindBidirectional(selectedGraph.getMinXProperty(), customStringConverter);
		graphMaxX					.textProperty().bindBidirectional(selectedGraph.getMaxXProperty(), customStringConverter);
		

		
		// Bind graph layout properties
		graphStyle					.valueProperty().bindBidirectional(selectedGraph.getStyleProperty());
		graphColorPicker			.valueProperty().bindBidirectional(selectedGraph.getColorProperty());
		graphDetailSlider			.valueProperty().bindBidirectional(selectedGraph.getDetailProperty());
		graphStrokeSlider			.valueProperty().bindBidirectional(selectedGraph.getStrokeProperty());
		graphPointsToggleButton		.selectedProperty().bindBidirectional(selectedGraph.getPointsProperty());
		graphSmoothToggleButton		.selectedProperty().bindBidirectional(selectedGraph.getSmoothProperty());
		graphVisibleToggleButton	.selectedProperty().bindBidirectional(selectedGraph.getVisibleProperty());
	}
	
	/**
	 * Unbinds TraceView inputs from previously selected trace.
	 */
	private void unbindTrace() {
		// Unbind trace properties
		traceName				.textProperty().unbindBidirectional(prevTrace.getNameProperty());
		traceFile				.valueProperty().unbindBidirectional(prevTrace.getFileProperty());
		traceIntegration		.valueProperty().unbindBidirectional(prevTrace.getIntegrationProperty());
		traceInterpolation		.valueProperty().unbindBidirectional(prevTrace.getInterpolationProperty());
		traceInertia			.valueProperty().unbindBidirectional(prevTrace.getInertiaProperty());
		traceMass				.textProperty().unbindBidirectional(prevTrace.getMassProperty());
//		traceRadius				.textProperty().unbindBidirectional(prevTrace.getRadiusProperty());
		traceMinX				.textProperty().unbindBidirectional(prevTrace.getMinXProperty());
		traceMaxX				.textProperty().unbindBidirectional(prevTrace.getMaxXProperty());
		traceInitV				.textProperty().unbindBidirectional(prevTrace.getInitVProperty());
		traceStep				.textProperty().unbindBidirectional(prevTrace.getStepProperty());
	}
	
	/**
	 * Unbinds GraphView inputs from previously selected graph.
	 */
	private void unbindGraph() {
		// Unbind graph properties
		graphName				.textProperty().unbindBidirectional(prevGraph.getNameProperty());
		graphXData				.valueProperty().unbindBidirectional(prevGraph.getXDataProperty());
		graphYData				.valueProperty().unbindBidirectional(prevGraph.getYDataProperty());
		graphTrace				.valueProperty().unbindBidirectional(prevGraph.getTraceProperty());
		graphMinX				.textProperty().unbindBidirectional(prevGraph.getMinXProperty());
		graphMaxX				.textProperty().unbindBidirectional(prevGraph.getMaxXProperty());
		
		// Unbind graph layout properties
		graphStyle				.valueProperty().unbindBidirectional(prevGraph.getStyleProperty());
		graphColorPicker		.valueProperty().unbindBidirectional(prevGraph.getColorProperty());
		graphDetailSlider		.valueProperty().unbindBidirectional(prevGraph.getDetailProperty());
		graphStrokeSlider		.valueProperty().unbindBidirectional(prevGraph.getStrokeProperty());
		graphPointsToggleButton	.selectedProperty().unbindBidirectional(prevGraph.getPointsProperty());
		graphSmoothToggleButton	.selectedProperty().unbindBidirectional(prevGraph.getSmoothProperty());
		graphVisibleToggleButton.selectedProperty().unbindBidirectional(prevGraph.getVisibleProperty());
	}
    
    
    //// Button handlers
    // Main menu button handlers
    @FXML private void handleNewProjectClick(ActionEvent event) {}
    
    @FXML private void handleSaveClick(ActionEvent event) {}
    
    @FXML private void handleLoadClick(ActionEvent event) {}
    
    @FXML private void handleImportClick(ActionEvent event) {}
    
    @FXML private void handleExportClick(ActionEvent event) {}
    
    
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
    	// Select trace details view
    	traceTabPane.getSelectionModel().selectLast();
    	
    	// Run trace in parallel
    	selectedTrace.parallelTrace();
    	
    	// Update
    	updateTraceView();
    }
    
    @FXML private void handleComputeAllClick(ActionEvent event) {
    	// Select trace details view
    	traceTabPane.getSelectionModel().selectLast();
    	
    	// Run all traces in parallel
    	traceList.forEach(trace -> trace.parallelTrace());
    	
    	// Update
    	updateTraceView();
    }
    
    @FXML private void handleTraceListClick(Event event) {
    	updateTraceView();
    }
    
    
    // Graph button handlers
    @FXML private void handleNewGraphClick(ActionEvent event) {
    	graphList.add(new Graph(selectedTrace));
    	
    	graphListView.getSelectionModel().selectLast();
    	updateGraphView();
    }

    @FXML private void handleDeleteGraphClick(ActionEvent event) {
    	selectedGraph.getSeries().getNode().setStyle("-fx-stroke: #450000;");
    }
    
    @FXML private void handleGraphUpClick(ActionEvent event) {
    	System.out.println(selectedGraph.getTrace());
    }
    
    @FXML private void handleGraphDownClick(ActionEvent event) {
    	selectedGraph.printDetails();
    }
    
    @FXML private void handleGraphListClick(Event event) {
    	updateGraphView();
    }
    
    
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
    
    
    
    
}
