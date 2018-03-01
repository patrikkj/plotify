package app;

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
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;;

public class MainController {
	// Variable declarations
	// Root 
    @FXML private VBox rootNode;

    // Chart
    @FXML private LineChart<?, ?> lineChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
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
    
    // Traces
    @FXML private JFXListView<Trace> traceListView;
    // Trace properties
    @FXML private JFXTextField traceName;
    @FXML private JFXComboBox<String> traceFilepath;
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

    // Graphs
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

    //  Observable lists (Used in listViews)
    private ObservableList<Trace> traceList;
    private ObservableList<Graph> graphList;
    
    
    // Initializer
    @FXML private void initialize() {    	
    	initializeTraces();
    	initializeGraphs();
    	initializeLists();
    }
    
    private void initializeTraces() {
    	// Initialize trace lists
    	traceList = FXCollections.observableArrayList();
    	
    	// Bind list view to observable list
    	traceListView.setItems(traceList);
    }
    
    private void initializeGraphs() {
    	// Initialize graph lists
    	graphList = FXCollections.observableArrayList();

    	// Bind list view to observable list
    	graphListView.setItems(graphList);
    }
    
    private void initializeLists() {
//    	traceFilepath.setItems();
        traceIntegration.setItems(FXCollections.observableList(Integration.getElements()));
        traceInterpolation.setItems(FXCollections.observableList(Interpolation.getElements()));
        traceInertia.setItems(FXCollections.observableList(Inertia.getElements()));
        
    }
    
    
    // Updaters
	@SuppressWarnings("unused")
	private void updateTraceView() {
		// Retrive selected Trace
		int traceIndex = traceListView.getSelectionModel().getSelectedIndex();
		Trace trace = traceList.get(traceIndex);
		
		//Update trace properties
		traceName								.setText(trace.getName());
	    traceFilepath			.getEditor()	.setText(trace.getFilepath());
	    traceIntegration		.getEditor()	.setText(trace.getIntegration().TEXT);
	    traceInterpolation		.getEditor()	.setText(trace.getInterpolation().TEXT);
	    traceInertia			.getEditor()	.setText(trace.getInertia().TEXT);;
	    traceMass								.setText(trace.getMass().toString());;
	    traceRadius								.setText(trace.getRadius().toString());;
	    traceMinX								.setText(trace.getMinX().toString());
	    traceMaxX								.setText(trace.getMaxX().toString());
	    traceInitV								.setText(trace.getInitV().toString());
	    traceStep								.setText(trace.getStep().toString());
		
		//Update trace details
	    funcTypeLabel			.setText(trace.getInterpolationType());
	    integrationTypeLabel	.setText(trace.getIntegrationType());
	    stepSizeLabel			.setText(trace.getStepSize());
	    iterationsLabel			.setText(trace.getIterations());
	    totalTimeLabel			.setText(trace.getTotalTime());
	    computationTimeLabel	.setText(trace.getComputationTime());
	    energyDifferenceLabel	.setText(trace.getEnergyDifference());
    }
    
    @SuppressWarnings("unused")
	private void updateGraphs() {
    	
    }
    
    
    // Validators
    @SuppressWarnings("unused")
    private void validateTrace() {
    	
    }
    
    
    // Button handlers
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
    
    @FXML private void handleDeleteTraceClick(ActionEvent event) {}
    @FXML private void handleComputeClick(ActionEvent event) {}
    @FXML private void handleComputeAllClick(ActionEvent event) {}

    // Graph button handlers
    @FXML private void handleNewGraphClick(ActionEvent event) {
    	graphList.add(new Graph());
    }
    
    @FXML private void handleDeleteGraphClick(ActionEvent event) {}
    @FXML private void handleGraphUpClick(ActionEvent event) {}
    @FXML private void handleGraphDownClick(ActionEvent event) {}

    
    // Other methods
    
    

}
