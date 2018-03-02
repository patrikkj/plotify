package app;

import enums.Style;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.paint.Color;

public class Graph {
	// Graph data properties
	private XYChart.Series<Number, Number> series;
	private StringProperty name;
	private ObjectProperty<ObservableList<Float>> xData;
	private ObjectProperty<ObservableList<Float>> yData;
	private ObjectProperty<Trace> trace;
	private ObjectProperty<Double> minX;
	private ObjectProperty<Double> maxX;
	private ObjectProperty<Double> minY;
	private ObjectProperty<Double> maxY;
	
	// Graph layout properties
	private ObjectProperty<Color> color;
	private ObjectProperty<Double> stroke;
	private ObjectProperty<Double> detail;
	private ObjectProperty<Style> style;
	private BooleanProperty smooth;
	private BooleanProperty points;
	private BooleanProperty visible;
	
	public Graph() {
		// Initialize graph properties
		initializeProperties();

		// Set sample graph
		setName("Sample graph");
		series = new XYChart.Series<>();
		series.setName("My series");
		
		
//		ChangeListener cl = new ChangeListener<T>() {
//
//			@Override
//			public void changed(ObservableValue<? extends T> arg0, T arg1, T arg2) {
//				
//			}
//		};
		
		
		// Set default values
//		setName("New graph");
	}

	@SuppressWarnings("unchecked")
	public void setDefaultSeries() {
//		ObservableList<XYChart.Data<Number, Number>> dataList = FXCollections.observableList(list)
		XYChart.Series<Number, Number> ser = new XYChart.Series<>();
		
		series.getData().setAll(new XYChart.Data<Number, Number>(0, 10),
				new XYChart.Data<Number, Number>(1, 12),
				new XYChart.Data<Number, Number>(2, 14),
				new XYChart.Data<Number, Number>(3, 16));
	}
	
	public void updateSeries() {
		
	}
	
	private void initializeSeries() {
		
	}
	
	private void initializeProperties() {
		// Graph data properties
		name = new SimpleStringProperty();
		xData = new SimpleObjectProperty<>();
		yData = new SimpleObjectProperty<>();
		trace = new SimpleObjectProperty<>();
		minX = new SimpleObjectProperty<>();
		maxX = new SimpleObjectProperty<>();
		minY = new SimpleObjectProperty<>();
		maxY = new SimpleObjectProperty<>();
		
		// Graph layout properties
		color = new SimpleObjectProperty<>();
		stroke = new SimpleObjectProperty<>();
		detail = new SimpleObjectProperty<>();
		style = new SimpleObjectProperty<>();
		smooth = new SimpleBooleanProperty();
		points = new SimpleBooleanProperty();
		visible = new SimpleBooleanProperty();
	}


	// Data getters
	public Series<Number, Number> getSeries() {return series;}
	public String getName() {return name.get();}
	public ObservableList<Float> getXData() {return xData.get();}
	public ObservableList<Float> getYData() {return yData.get();}
	public Trace getTrace() {return trace.get();}
	public Double getMinX() {return minX.get();}
	public Double getMaxX() {return maxX.get();}
	public Double getMinY() {return minY.get();}
	public Double getMaxY() {return maxY.get();}

	// Layout getters
	public Color getColor() {return color.get();}
	public Double getStroke() {return stroke.get();}
	public Double getDetail() {return detail.get();}
	public Style getStyle() {return style.get();}
	public Boolean getSmooth() {return smooth.get();}
	public Boolean getPoints() {return points.get();}
	public Boolean getVisible() {return visible.get();}
	
	
	// Data setters
	public void setName(String name) {this.name.set(name);}
	public void setXData(ObservableList<Float> xData) {this.xData.set(xData);}
	public void setYData(ObservableList<Float> yData) {this.yData.set(yData);}
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
	public ObjectProperty<ObservableList<Float>> getXDataProperty() {return xData;}
	public ObjectProperty<ObservableList<Float>> getYDataProperty() {return yData;}
	public ObjectProperty<Trace> getTraceProperty() {return trace;}
	public ObjectProperty<Double> getMinXProperty() {return minX;}
	public ObjectProperty<Double> getMaxXProperty() {return maxX;}
	public ObjectProperty<Double> getMinYProperty() {return minY;}
	public ObjectProperty<Double> getMaxYProperty() {return maxY;}
	
	// Layout property getters
	public ObjectProperty<Color> getColorProperty() {return color;}
	public ObjectProperty<Double> getStrokeProperty() {return stroke;}
	public ObjectProperty<Double> getDetailProperty() {return detail;}
	public ObjectProperty<Style> getStyleProperty() {return style;}
	public BooleanProperty getSmoothProperty() {return smooth;}
	public BooleanProperty getPointsProperty() {return points;}
	public BooleanProperty getVisibleProperty() {return visible;}
	
	
	public String toString() {
		return getName();
	}
	
	
	
}