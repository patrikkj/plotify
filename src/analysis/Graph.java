package analysis;

import java.util.List;

public class Graph {
	private List<Float> xCollection, yCollection;
	private Trace trace;
	private boolean drawn;
	private boolean dotted;
	private double drawStep;
	
	//Layout
	private String name;
	private String hexColor;
	private double thickness;
	
	
	public Graph(String name, List<Float> xCollection, List<Float> yCollection, Trace trace) {
		this.name = name;
		this.xCollection = xCollection;
		this.yCollection = yCollection;
		this.trace = trace;
	}
	
}
