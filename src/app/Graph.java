package app;

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
	private BooleanProperty points;
	List<Data<Number, Number>> dataList; 
	// Initial color selection
	public static int initColorID;
	public static Color[] initColors = new Color[] {
		Color.valueOf("#450000"),
		Color.valueOf("#0060AA"),
		Color.valueOf("#206020"),
		Color.valueOf("#105060"),
		Color.valueOf("#BB0000")
	};
	
	
	// Constructors
	/**
	 * Constructor used by Trace.
	 */
	public Graph(Trace initTrace) {
		// Initialize graph properties
		initializeProperties();
		
		// Initialize series
		series = new XYChart.Series<>();
		series.nameProperty().bindBidirectional(name);

		// Set default values
		setDefault(initTrace);
		
		// Add trace link
		updateTraceLink(null, initTrace);
		
		// Apply change listener
		initializeChangeListeners();
	}

	
	// Initialization
	/**
	 * Initialize all data and layout properties.
	 */
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
		points = new SimpleBooleanProperty();
	}
	
	/**
	 * Apply change listeners to dynamic data.
	 */
	private void initializeChangeListeners() {
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

	/**
	 * Assign default graph values.
	 */
	private void setDefault(Trace initTrace) {
		// Set default data properties
		setName("New graph");
		setXData("Raw data (x)");
		setYData("Raw data (y)");
		setTrace(initTrace);
		setMinX(Double.NEGATIVE_INFINITY);
		setMaxX(Double.POSITIVE_INFINITY);
		
		// Set default layout properties
		setColor(initColors[initColorID++ % initColors.length]);
		setStyle(Style.FULL_LINE);
		setWidth(50d);
		setDetail(100d);
		setVisible(true);
	}
	
	
	// Validation
	/**
	 * Returns {@code true} if graph is valid, else {@code false}.
	 */
	private boolean isValidGraph() {
		if (getName() == null) return false;
		if (getSeries() == null) return false;
		if (getXData() == null) return false;
		if (getYData() == null) return false;
		if (getTrace() == null) return false;
		if (getTrace().getFile() == null) return false;
		if (getMinX() == null) return false;
		if (getMaxX() == null) return false;
		if (getColor() == null) return false;
		if (getStyle() == null) return false;
		if (getWidth() == null) return false;
		if (getDetail() == null) return false;
		if (getVisible() == null) return false;
		if (getPoints() == null) return false;
		
		return true;
	}
	
	
	// Update
	/**
	 * Updates data set and graph styling.
	 */
	public void updateGraph() {
		// Break if graph is invalid
		if (!isValidGraph()) return;
		
		// Update graph
		updateSeries();
		updateStyle();
	}
	
	/**
	 * Updates data set.
	 */
	private void updateSeries() {
		// Empty list used to build data set
		dataList = new ArrayList<>();
		
		// Raw data sets
		ObservableList<Double> rawXData = getTrace().getDataMap().get(getXData());
		ObservableList<Double> rawYData = getTrace().getDataMap().get(getYData());
		
		// Break if any data sets are missing
		if (rawXData == null  ||  rawYData == null) return;
		if (rawXData.size() == 0  ||  rawYData.size() == 0) return;
		
		// Set lower boundary based on data set sizes
		double listSize = Math.min(rawXData.size(), rawYData.size());
		double actualSize = Math.min(listSize, getDetail());
		
		// Reduced indices
		int[] indices = parsers.Data.equidistantIndices(rawXData.stream().mapToDouble(doub -> doub.doubleValue()).toArray(), (int) actualSize);
		// Reduce lists
		List<Double> reducedXData = parsers.Data.reduceList(rawXData, indices);
		List<Double> reducedYData = parsers.Data.reduceList(rawYData, indices);
		
		// Construct output list
		for (int i = 0; i < reducedXData.size(); i++)
			dataList.add(new Data<Number, Number>(reducedXData.get(i), reducedYData.get(i)));
		
		// Update series
		series.getData().setAll(dataList);
		System.out.println("Data series size: " + series.getData().size());
	}
	
	/**
	 * Updates graph styling.
	 */
	private void updateStyle() {
		// Break if graph is not plotted
		if (getSeries().getNode() == null) return;
		
		// Set line styling
		getSeries().getNode().setStyle(String.format("-fx-stroke: #%s;"
													+ "-fx-stroke-width: %s;"
													+ "-fx-stroke-dash-array: %s;"
													+ "-fx-stroke-line-cap: %s;", 
													getHexColor(), getWidth(), getStyle(), "ROUND"));
		
		// If points are plotted, set point styling
		if (getPoints()) {
			String nodeStyle = String.format("-fx-background-color: #%s, #FFFFFF;"
											+ "-fx-background-radius: 100, 100;"
											+ "-fx-background-insets: %s, 2;", getHexColor(), 2d - getWidth());
			getSeries().getData().forEach(data -> data.getNode().setStyle(nodeStyle));
		}
	}
	
	
	// Links
	/**
	 * Update Trace <-> Graph 1-n association.
	 */
	public void updateTraceLink(Trace prevTrace, Trace currentTrace) {
		if (prevTrace != null)
			prevTrace.removeGraph(this);
		if (currentTrace != null)
			currentTrace.addGraph(this);
	}

	
	// Other 
	/**
	 * String representation of this Graph, used in ListViews.
	 */
	public String toString() {
		return getName();
	}
	
	
	
	///////////////////////////////
	/////  Setters / Getters  /////
	///////////////////////////////
	
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
	public Double getWidth() {return Math.pow(width.get(), 1.5) / 120d;}
	public Style getStyle() {return style.get();}
	public Double getDetail() {return 10d / ((0.5d - detail.get())/100d + 1d) - 7;}
	public Boolean getVisible() {return visible.get();}
	public Boolean getPoints() {return points.get();}
	
	
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
	public void setPoints(Boolean points) {this.points.set(points);}

	
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
	public BooleanProperty getPointsProperty() {return points;}
	
}