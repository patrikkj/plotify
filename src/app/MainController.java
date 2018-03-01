package app;

import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

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
    @FXML private JFXListView<Label> traceListView;
    // Trace properties
    @FXML private JFXTextField traceName;
    @FXML private JFXComboBox<?> traceFilepath;
    @FXML private JFXComboBox<?> traceInterpolation;
    @FXML private JFXComboBox<?> traceObject;
    @FXML private JFXTextField traceMass;
    @FXML private JFXTextField traceRadius;
    @FXML private JFXTextField traceInitX;
    @FXML private JFXTextField traceInitV;
    // Trace details
    @FXML private Label funcTypeLabel;
    @FXML private Label integrationTypeLabel;
    @FXML private Label stepSizeLabel;
    @FXML private Label iterationsLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Label computationTimeLabel;
    @FXML private Label energyPreservedLabel;

    // Graphs
    @FXML private JFXListView<Label> graphListView;
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

    // Trace lists
    private List<Trace> traceList;
    private ObservableList<Label> traceLabelList;
    
    // Graph lists
    private List<Graph> graphList;
    private ObservableList<Label> graphLabelList;
    
    
    // Initializer
    @FXML private void initialize() {    	
    	initializeTraces();
    	initializeGraphs();
    	
    }
    
    private void initializeTraces() {
    	// Initialize trace lists
    	traceList = new ArrayList<>();
    	traceLabelList = FXCollections.observableArrayList();
    	
    	// Bind list view to observable label list
    	traceListView.setItems(traceLabelList);
    }
    
    private void initializeGraphs() {
    	// Initialize graph lists
    	graphList = new ArrayList<>();
    	graphLabelList = FXCollections.observableArrayList();
    	
    	// Bind list view to observable label list
    	graphListView.setItems(graphLabelList);
    }
    
    // Updaters
	@SuppressWarnings("unused")
	private void updateTraceView() {
		// Retrive selected Trace
		int traceIndex = traceListView.getSelectionModel().getSelectedIndex();
		Trace trace = traceList.get(traceIndex);
		
		
		
		//Update trace properties
		traceName.setText(trace.getName());
		traceFilepath.getEditor().setText(trace.getFilepath());
		traceInterpolation.getEditor().setText(trace.getInterpolation().TEXT);
		traceObject.getEditor().setText(trace.getInertia().TEXT);
		traceRadius.setText(trace.getRadius().toString());
		traceInitX.setText(trace.getInitX());
		traceInitV.setText(trace.getInitV());
		
		//Update trace details
		
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
    	traceLabelList.add(new Label("New Trace"));
    }
    
    @FXML private void handleDeleteTraceClick(ActionEvent event) {}
    @FXML private void handleComputeClick(ActionEvent event) {}
    @FXML private void handleComputeAllClick(ActionEvent event) {}

    // Graph button handlers
    @FXML private void handleNewGraphClick(ActionEvent event) {
    	graphList.add(new Graph());
    	graphLabelList.add(new Label("New Graph"));
    }
    
    @FXML private void handleDeleteGraphClick(ActionEvent event) {}
    @FXML private void handleGraphUpClick(ActionEvent event) {}
    @FXML private void handleGraphDownClick(ActionEvent event) {}

    
    // Other methods
    
    

}
