package app;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import enums.Style;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.paint.Color;

public class Graph {
	// Graph data properties
	private StringProperty name;
	private XYChart.Series<Number, Number> series;
	private StringProperty xData;
	private StringProperty yData;
	private ObjectProperty<Trace> trace;
	private ObjectProperty<Double> minX;
	private ObjectProperty<Double> maxX;
	
	// Graph layout properties
	private ObjectProperty<Color> color;
	private ObjectProperty<Style> style;
	private DoubleProperty width;
	private DoubleProperty detail;
	private BooleanProperty visible;
	List<Data<Number, Number>> dataList; 

	
	/////////////////////////////
	/////  ADD COMMENTS :)  /////
	/////////////////////////////
	
	// Constructor
	public Graph(Trace initTrace) {
		// Initialize graph properties
		initializeProperties();
		
		// Initialize series
		series = new XYChart.Series<>();
		series.nameProperty().bindBidirectional(name);

		// Set default data properties
		setName("New graph");
		setXData("Position (x)");
		setYData("Velocity");
		setTrace(initTrace);
		setMinX(Double.NEGATIVE_INFINITY);
		setMaxX(Double.POSITIVE_INFINITY);
		
		// Set default layout properties
		setColor(Color.valueOf("#454545"));
		setStyle(Style.FULL_LINE);
		setWidth(50d);
		setDetail(100d);
		setVisible(true);
		
		// Add trace link
		addTraceLink();
		
		// Apply change listener
		applyChangeListeners();
	}
	
	// Initialization
	private void initializeProperties() {
		// Graph data properties
		name = new SimpleStringProperty();
		xData = new SimpleStringProperty();
		yData = new SimpleStringProperty();
		trace = new SimpleObjectProperty<>();
		minX = new SimpleObjectProperty<>();
		maxX = new SimpleObjectProperty<>();
		
		// Graph layout properties
		color = new SimpleObjectProperty<>();
		width = new SimpleDoubleProperty();
		style = new SimpleObjectProperty<>();
		detail = new SimpleDoubleProperty();
		visible = new SimpleBooleanProperty();
	}
	
	private void applyChangeListeners() {
		// Create changeListeners
		ChangeListener<Object> dataChangeListener = new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2) {
				updateGraph();
			}
		};
		ChangeListener<Trace> traceChangeListener = new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Trace> arg0, Trace prev, Trace current) {
				updateTraceLink(prev, current);
			}
		};
		
		// Add listener to manage Trace <-> Graph link
		trace.addListener(traceChangeListener);

		// Add listener to data-related properties
		xData.addListener(dataChangeListener);
		yData.addListener(dataChangeListener);
		trace.addListener(dataChangeListener);
		minX.addListener(dataChangeListener);
		maxX.addListener(dataChangeListener);
		color.addListener(dataChangeListener); 
		style.addListener(dataChangeListener); 
		width.addListener(dataChangeListener);
		detail.addListener(dataChangeListener);
		visible.addListener(dataChangeListener);
	}

	// Validation
	private boolean isValidGraph() {
		if (getName() == null) return false;
		if (getSeries() == null) return false;
		if (getXData() == null) return false;
		if (getYData() == null) return false;
		if (getTrace() == null) return false;
		if (getMinX() == null) return false;
		if (getMaxX() == null) return false;
		if (getColor() == null) return false;
		if (getStyle() == null) return false;
		if (getWidth() == null) return false;
		if (getDetail() == null) return false;
		if (getVisible() == null) return false;
		
		return true;
	}
	
	// Manage series
	private List<Double> reduceList(List<Double> inputList, double n) {
		n = (double) Math.round(n);
		// Verify that reduction is necessary
		if (n >= inputList.size())
			return inputList;
		
		// Create output list
		List<Double> outputList = new ArrayList<>();
		
		// Calculate step size
		double step = ((double) inputList.size() - 1d)  /  (n - 1d); 
		
		// Fill list
		for (int i = 0; i < n; i++)
				outputList.add(inputList.get((int) Math.round(step * (double) i)));
		
		// Return reduced list
		return outputList;
	}
	
	public void updateGraph() {
		Instant start = Instant.now();
		
		// Break if graph is invalid
		if (!isValidGraph()) return;
		
		// Update graph
		updateSeries();
		updateStyle();
		System.out.println(getSeries().getData().size());
		Instant end = Instant.now();
		System.out.println(String.format("%.3f seconds", (double) Duration.between(start, end).toMillis()/1000).replace(',', '.'));
	}
	
	private void updateSeries() {
		// Empty list used to build data set
		dataList = new ArrayList<>();
		
		// Raw data sets
		ObservableList<Double> rawXData = getTrace().getDataMap().get(getXData());
		ObservableList<Double> rawYData = getTrace().getDataMap().get(getYData());
		
		// Break if any data sets are missing
		if (rawXData == null  ||  rawYData == null) return;
		
		// Set lower boundary based on data set sizes
		double listSize = Math.min(rawXData.size(), rawYData.size());
		double actualSize = Math.min(listSize, getDetail());
		
		// Reduce lists
		List<Double> reducedXData = reduceList(rawXData, actualSize);
		List<Double> reducedYData = reduceList(rawYData, actualSize);
		
		// Construct output list
		for (int i = 0; i < reducedXData.size(); i++)
			dataList.add(new Data<Number, Number>(reducedXData.get(i), reducedYData.get(i)));
		
		// Update series
		series.getData().setAll(dataList);
	}
	
	private void updateStyle() {
		String lineStyle, nodeStyle;
		
		if (getVisible())
			lineStyle = String.format("-fx-stroke: #%s;"
									+ "-fx-stroke-width: %s;"
									+ "-fx-stroke-dash-array: %s;", 
									getHexColor(), getWidth(), getStyle());
		else 
			lineStyle = String.format("visibility: %s;", false);
			
		getSeries().getNode().setStyle(lineStyle);
		
	//		String nodeStyle = String.format("-fx-background-color: #%s, #FFFFFF;"
	//										+ "-fx-background-radius: 100, 100;"
	//										+ "-fx-background-insets: %s, 2;", getHexColor(), 2d - getWidth());
	//		getSeries().getData().forEach(data -> data.getNode().setStyle(nodeStyle));
		// WORKING
	//		getSeries().getChart().lookupAll(".chart-legend-item").stream()
	////		.forEach(elem -> ((Labeled) elem).setGraphic(new Circle(3, Paint.valueOf("#FF0000"))));
	//		.forEach(elem -> ((Labeled) elem).getGraphic().setStyle(nodeStyle));
	
	}
	
	
	// Manage Trace <-> Graph link
	public void updateTraceLink(Trace prevTrace, Trace currentTrace) {
		prevTrace.removeGraph(this);
		if (currentTrace != null)
			currentTrace.addGraph(this);
	}
	
	public void addTraceLink() {
		getTrace().addGraph(this);
	}
	
	public void removeTraceLink() {
		getTrace().removeGraph(this);
	}
	

	// Data getters
	public Series<Number, Number> getSeries() {return series;}
	public String getName() {return series.nameProperty().get();}
	public String getXData() {return xData.get();}
	public String getYData() {return yData.get();}
	public Trace getTrace() {return trace.get();}
	public Double getMinX() {return minX.get();}
	public Double getMaxX() {return maxX.get();}

	// Layout getters
	public Color getColor() {return color.get();}
	public String getHexColor() {return getColor().toString().substring(2, 8);}
	public Double getWidth() {return width.get()* (6d/100d);}
	public Style getStyle() {return style.get();}
	public Double getDetail() {return 10d / ((0.5d - detail.get())/100d + 1d) - 7;}
	public Boolean getVisible() {return visible.get();}
	
	
	// Data setters
	public void setName(String name) {this.name.set(name);}
	public void setXData(String xData) {this.xData.set(xData);}
	public void setYData(String yData) {this.yData.set(yData);}
	public void setTrace(Trace trace) {this.trace.set(trace);}
	public void setMinX(Double minX) {this.minX.set(minX);}
	public void setMaxX(Double maxX) {this.maxX.set(maxX);}
	
	// Layout setters
	public void setColor(Color color) {this.color.set(color);}
	public void setWidth(Double width) {this.width.set(width);}
	public void setStyle(Style style) {this.style.set(style);}
	public void setDetail(Double detail) {this.detail.set(detail);}
	public void setVisible(Boolean visible) {this.visible.set(visible);}

	
	// Data property getters
	public StringProperty getNameProperty() {return name;}
	public StringProperty getXDataProperty() {return xData;}
	public StringProperty getYDataProperty() {return yData;}
	public ObjectProperty<Trace> getTraceProperty() {return trace;}
	public ObjectProperty<Double> getMinXProperty() {return minX;}
	public ObjectProperty<Double> getMaxXProperty() {return maxX;}
	
	// Layout property getters
	public ObjectProperty<Color> getColorProperty() {return color;}
	public DoubleProperty getWidthProperty() {return width;}
	public ObjectProperty<Style> getStyleProperty() {return style;}
	public DoubleProperty getDetailProperty() {return detail;}
	public BooleanProperty getVisibleProperty() {return visible;}
	
	public void printDetails() {
		System.out.printf("\nSeries: %s\n"
				+ "Name: %s\n"
				+ "XData: %s\n"
				+ "YData: %s\n"
				+ "Trace: %s\n"
				+ "MinX: %s\n"
				+ "MaxX: %s\n"
				+ "Color: %s\n"
				+ "Stroke width: %s\n"
				+ "Stroke style: %s\n"
				+ "Detail: %s\n"
				+ "Dataset length: %s\n"
				+ "Requested length: %s\n"
				+ "Trace xData length: %s\n"
				+ "Trace yData length: %s\n",
				getSeries(),
				getName(),
				getXData(),
				getYData(),
				getTrace(),
				getMinX(),
				getMaxX(),
				getColor(),
				getWidth(),
				getStyle(),
				getDetail(),
				dataList.size(),
				getDetail(),
				getTrace().getDataMap().get(getXData()).size(),
				getTrace().getDataMap().get(getYData()).size());
	}
	
	public String toString() {
		return getName();
	}
	
	
	
}