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
	private ObjectProperty<Double> minY;
	private ObjectProperty<Double> maxY;
	
	// Graph layout properties
	private ObjectProperty<Color> color;
	private DoubleProperty stroke;
	private DoubleProperty detail;
	private ObjectProperty<Style> style;
	private BooleanProperty smooth;
	private BooleanProperty points;
	private BooleanProperty visible;
	
	List<Data<Number, Number>> dataList; 
	
	public Graph(Trace initTrace) {
		// Initialize graph properties
		initializeProperties();
		series = new XYChart.Series<>();
		series.nameProperty().bindBidirectional(name);

		// Set default values
		setName("Sample graph");
		setTrace(initTrace);
		setDetail(100d);
		setColor(Color.valueOf("#FFFFFF"));
		
		// Apply change listener
		applyChangeListeners();
	}
	
	
	private void applyChangeListeners() {
		// Create changeListeners
		ChangeListener<Object> dataChangeListener = new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2) {
				updateSeries();
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
		xData.addListener(dataChangeListener);
		minX.addListener(dataChangeListener);
		maxX.addListener(dataChangeListener);
		minY.addListener(dataChangeListener);
		maxY.addListener(dataChangeListener);
		smooth.addListener(dataChangeListener);
		detail.addListener(dataChangeListener);
	}
	
	private List<Float> reduceList(List<Float> inputList, double n) {
		n = (double) Math.round(n);
		// Verify that reduction is necessary
		if (n >= inputList.size())
			return inputList;
		
		// Create output list
		List<Float> outputList = new ArrayList<>();
		
		// Calculate step size
		double step = ((double) inputList.size() - 1d)  /  (n - 1d); 
		
		// Fill list
		for (int i = 0; i < n; i++)
				outputList.add(inputList.get((int) Math.round(step * (double) i)));
		
		// Return reduced list
		return outputList;
	}
	
	public void updateSeries() {
		// Empty list used to build data set
		dataList = new ArrayList<>();
		
		// Raw data sets
		ObservableList<Float> rawXData = getTrace().getTraceMap().get(getXData());
		ObservableList<Float> rawYData = getTrace().getTraceMap().get(getYData());
		
		// Break if any data sets are missing
		if (rawXData == null  ||  rawYData == null) return;
		
		// Reduced lists
		List<Float> reducedXData = reduceList(rawXData, getDetail());
		List<Float> reducedYData = reduceList(rawYData, getDetail());
		
		// Construct output list
		for (int i = 0; i < reducedXData.size(); i++)
			dataList.add(new Data<Number, Number>(reducedXData.get(i), reducedYData.get(i)));
		
		// Update series
		series.getData().setAll(dataList);
	}
	
	public void updateTraceLink(Trace prevTrace, Trace currentTrace) {
		prevTrace.removeGraph(this);
		currentTrace.addGraph(this);
	}
	
	private void initializeProperties() {
		// Graph data properties
		name = new SimpleStringProperty();
		xData = new SimpleStringProperty();
		yData = new SimpleStringProperty();
		trace = new SimpleObjectProperty<>();
		minX = new SimpleObjectProperty<>();
		maxX = new SimpleObjectProperty<>();
		minY = new SimpleObjectProperty<>();
		maxY = new SimpleObjectProperty<>();
		
		// Graph layout properties
		color = new SimpleObjectProperty<>();
		stroke = new SimpleDoubleProperty();
		detail = new SimpleDoubleProperty();
		style = new SimpleObjectProperty<>();
		smooth = new SimpleBooleanProperty();
		points = new SimpleBooleanProperty();
		visible = new SimpleBooleanProperty();
	}


	// Data getters
	public Series<Number, Number> getSeries() {return series;}
	public String getName() {return series.nameProperty().get();}
	public String getXData() {return xData.get();}
	public String getYData() {return yData.get();}
	public Trace getTrace() {return trace.get();}
	public Double getMinX() {return minX.get();}
	public Double getMaxX() {return maxX.get();}
	public Double getMinY() {return minY.get();}
	public Double getMaxY() {return maxY.get();}

	// Layout getters
	public Color getColor() {return color.get();}
	public Double getStroke() {return stroke.get();}
	public Double getDetail() {return 10d / ((0.5d - detail.get())/100d + 1d) - 7;}
	public Style getStyle() {return style.get();}
	public Boolean getSmooth() {return smooth.get();}
	public Boolean getPoints() {return points.get();}
	public Boolean getVisible() {return visible.get();}
	
	
	// Data setters
	public void setName(String name) {this.name.set(name);}
	public void setXData(String xData) {this.xData.set(xData);}
	public void setYData(String yData) {this.yData.set(yData);}
	public void setTrace(Trace trace) {this.trace.set(trace);}
	public void setMinX(Double minX) {this.minX.set(minX);}
	public void setMaxX(Double maxX) {this.maxX.set(maxX);}
	public void setMinY(Double minY) {this.minY.set(minY);}
	public void setMaxY(Double maxY) {this.maxY.set(maxY);}
	
	// Layout setters
	public void setColor(Color color) {this.color.set(color);}
	public void setStroke(Double stroke) {this.stroke.set(stroke);}
	public void setDetail(Double detail) {this.detail.set(detail);}
	public void setStyle(Style style) {this.style.set(style);}
	public void setSmooth(Boolean smooth) {this.smooth.set(smooth);}
	public void setPoints(Boolean points) {this.points.set(points);}
	public void setVisible(Boolean visible) {this.visible.set(visible);}

	
	// Data property getters
	public StringProperty getNameProperty() {return name;}
	public StringProperty getXDataProperty() {return xData;}
	public StringProperty getYDataProperty() {return yData;}
	public ObjectProperty<Trace> getTraceProperty() {return trace;}
	public ObjectProperty<Double> getMinXProperty() {return minX;}
	public ObjectProperty<Double> getMaxXProperty() {return maxX;}
	public ObjectProperty<Double> getMinYProperty() {return minY;}
	public ObjectProperty<Double> getMaxYProperty() {return maxY;}
	
	// Layout property getters
	public ObjectProperty<Color> getColorProperty() {return color;}
	public DoubleProperty getStrokeProperty() {return stroke;}
	public DoubleProperty getDetailProperty() {return detail;}
	public ObjectProperty<Style> getStyleProperty() {return style;}
	public BooleanProperty getSmoothProperty() {return smooth;}
	public BooleanProperty getPointsProperty() {return points;}
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
				+ "Stroke: %s\n"
				+ "Detail: %s\n"
				+ "Style: %s\n"
				+ "Smooth: %s\n"
				+ "Points: %s\n"
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
				getStroke(),
				getDetail(),
				getStyle(),
				getSmooth(),
				getPoints(),
				dataList.size(),
				getDetail(),
				getTrace().getTraceMap().get(getXData()).size(),
				getTrace().getTraceMap().get(getYData()).size());
		
		
//		// Data getters
//		public Series<Number, Number> getSeries() {return series;}
//		public String getName() {return series.nameProperty().get();}
//		public String getXData() {return xData.get();}
//		public String getYData() {return yData.get();}
//		public Trace getTrace() {return trace.get();}
//		public Double getMinX() {return minX.get();}
//		public Double getMaxX() {return maxX.get();}
//		public Double getMinY() {return minY.get();}
//		public Double getMaxY() {return maxY.get();}
//
//		// Layout getters
//		public Color getColor() {return color.get();}
//		public Double getStroke() {return stroke.get();}
//		public Double getDetail() {return detail.get();}
//		public Style getStyle() {return style.get();}
//		public Boolean getSmooth() {return smooth.get();}
//		public Boolean getPoints() {return points.get();}
//		public Boolean getVisible() {return visible.get();}
	}
	
	public String toString() {
		return getName();
	}
	
	
	
}