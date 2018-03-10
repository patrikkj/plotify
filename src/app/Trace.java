package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import enums.Inertia;
import enums.Integration;
import enums.Interpolation;
import functions.Differentiable;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;


public class Trace {
	// Trace properties (Setters & Getters)
	private StringProperty name;
	private ObjectProperty<File> file;
	private ObjectProperty<Integration> integration;
	private ObjectProperty<Interpolation> interpolation;
	private ObjectProperty<Inertia> inertia;
	private ObjectProperty<Double> mass;
	private ObjectProperty<Double> minX;
	private ObjectProperty<Double> maxX;
	private ObjectProperty<Double> initV;
	private ObjectProperty<Double> step;
	private BooleanProperty initialized;
	
	// Trace Details (Setters & Getters)
	private StringProperty 
		integrationType,
		interpolationType,
		stepSize,
		iterations,
		totalTime,
		computationTime,
		energyDifference;
	
	// Temporary fields to avoid multithread UI updates
	private String 
		tempIntegrationType,
		tempInterpolationType,
		tempStepSize,
		tempIterations,
		tempTotalTime,
		tempComputationTime,
		tempEnergyDifference;
	
	
	//Function
	private Differentiable func;
	private double[] domain;
	
	//Containers
	private ObservableMap<String, ObservableList<Float>> traceMap;
	private ObservableList<Float> aList;
	private ObservableList<Float> vList;
	private ObservableList<Float> xList;
	private ObservableList<Float> yList;
	private ObservableList<Float> totList;
	private ObservableList<Float> kinList;
	private ObservableList<Float> potList;
	private ObservableList<Float> tList;
	
	//Raw data
	private ObservableList<Float> tListRaw;
	private ObservableList<Float> xListRaw;
	private ObservableList<Float> yListRaw;
	
	//Associations
	public HashSet<Graph> linkedGraphs;
	
	//Constants
	public static final double G = 9.82814;
	public static final String[] MAP_KEYS = new String[] {
			"Acceleration",
			"Velocity",
			"Position (x)",
			"Position (y)",
			"Time (s)",
			"Total Energy",
			"Kinetic Energy",
			"Potential energy"};
	
	
	//Constructors
    /**
     * Constructor used by GUI.
     */
	public Trace() {
		//Initialize properties
		initializeProperties();
		
		//Initialize map
		initializeMap();
		
		//Initialize linked graph container
		linkedGraphs = new HashSet<>();
		
		//Set default values
		setName("New trace");
		setIntegration(Integration.EULER_METHOD);
		setInterpolation(Interpolation.POLYNOMIAL_SPLINE);
		setInertia(Inertia.POINT_OF_MASS);
		setMass(1d);
		setMinX(Double.NEGATIVE_INFINITY);
		setMaxX(Double.POSITIVE_INFINITY);
		setInitV(0d);
		setStep(0.001);
	}	

	/**
	 * Creates a Trace object used for numerical analysis.
	 * @param name
	 * @param file
	 * @param integration
	 * @param interpolation
	 * @param inertia
	 * @param mass
	 * @param minX
	 * @param maxX
	 * @param initV
	 * @param step
	 */
	public Trace(String name, File file, 
				Integration integration, Interpolation interpolation, Inertia inertia, 
				double mass, double minX, double maxX, double initV, double step) 
	{
		//Initialize properties
		initializeProperties();
		
		//Assign values
		setName(name);
		setFile(file);
		setIntegration(integration);
		setInterpolation(interpolation);
		setInertia(inertia);
		setMass(mass);
		setMinX(minX);
		setMaxX(maxX);
		setInitV(initV);
		setStep(step);
	}
	
	
	//Initialization
	private void initializeProperties() {
		name = new SimpleStringProperty();
		file = new SimpleObjectProperty<>();
		integration = new SimpleObjectProperty<>();
		interpolation = new SimpleObjectProperty<>();
		inertia = new SimpleObjectProperty<>();
		mass = new SimpleObjectProperty<>();
		minX = new SimpleObjectProperty<>();
		maxX = new SimpleObjectProperty<>();
		initV = new SimpleObjectProperty<>();
		step = new SimpleObjectProperty<>();
		initialized = new SimpleBooleanProperty();
		
		integrationType = new SimpleStringProperty();
		interpolationType = new SimpleStringProperty();
		stepSize = new SimpleStringProperty();
		iterations = new SimpleStringProperty();
		totalTime = new SimpleStringProperty();
		computationTime = new SimpleStringProperty();
		energyDifference = new SimpleStringProperty();
	}
	
	private void initializeFunc() {
		//Perform interpolation and set domain
		switch (getInterpolation()) {
		case POLYNOMIAL:
			func = analysis.Interpolation.polynomialInterpolation(getFile());
			break;
		case POLYNOMIAL_SPLINE:
			func = analysis.Interpolation.polynomialSplineInterpolation(getFile());
			break;
		}
		
		//Fill raw data collections
		double[][] rawData = analysis.Interpolation.parseFile(getFile());
		tListRaw.setAll(Arrays.stream(rawData[0])
				.mapToObj(doub -> Float.valueOf((float) doub))
				.collect(Collectors.toList()));
		xListRaw.setAll(Arrays.stream(rawData[1])
				.mapToObj(doub -> Float.valueOf((float) doub))
				.collect(Collectors.toList()));
		yListRaw.setAll(Arrays.stream(rawData[2])
				.mapToObj(doub -> Float.valueOf((float) doub))
				.collect(Collectors.toList()));
		
		//Set trace details
		tempInterpolationType = getInterpolation().TEXT;
		
		//Set domain and x-range
		domain = func.getDomain();
		setMinX(Math.max(getMinX(), domain[0]));
		setMaxX(Math.min(getMaxX(), domain[1]));
	}
	
	private void initializeMap() {
		// Initialize map and observable lists
		traceMap = FXCollections.observableHashMap();
		aList = FXCollections.observableArrayList();
		vList = FXCollections.observableArrayList();
		xList = FXCollections.observableArrayList();
		yList = FXCollections.observableArrayList();
		totList = FXCollections.observableArrayList();
		kinList = FXCollections.observableArrayList();
		potList = FXCollections.observableArrayList();
		tList = FXCollections.observableArrayList();
		
		//Initialize raw data collections
		tListRaw = FXCollections.observableArrayList();
		xListRaw = FXCollections.observableArrayList();
		yListRaw = FXCollections.observableArrayList();
		
		// Fill map
		traceMap.put("Acceleration", aList);
		traceMap.put("Velocity", vList);
		traceMap.put("Position (x)", xList);
		traceMap.put("Position (y)", yList);
		traceMap.put("Time (s)", tList);
		traceMap.put("Total Energy", totList);
		traceMap.put("Kinetic Energy", kinList);
		traceMap.put("Potential energy", potList);
	}
	
	//Updates
	public void updateTrace() {
		// Update trace details
		setIntegrationType(tempIntegrationType);
		setInterpolationType(tempInterpolationType);
		setEnergyDifference(tempEnergyDifference);
		setIterations(tempIterations);
		setStepSize(tempStepSize);
		setTotalTime(tempTotalTime);
		setComputationTime(tempComputationTime);
		
		//Update subscribing graphs
		linkedGraphs.forEach(graph -> graph.updateGraph());
	}
	
	//Validation
	public void validateTrace() {
		initializeFunc();
		
		//Validate mass
		if (getMass() <= 0)
			throw new IllegalArgumentException("Mass must be positive.");
		
		//Validate inertia constant
		if (getInertia().VALUE < 0)
			throw new IllegalArgumentException("Moment of inertia cannot be negative.");
		
		//Initialization complete
		setInitialized(true);
	}
	
	
	//Getters
	public ObservableMap<String, ObservableList<Float>> getTraceMap() {return traceMap;}
	public String getKey(ObservableList<Float> value) {
		for (String key : traceMap.keySet())
			if (traceMap.get(key).equals(value))
				return key;
		return null;
	}
	
	//Getters (Trace property values)
	public String getName() {return name.get();}
	public File getFile() {return file.get();}
	public Interpolation getInterpolation() {return interpolation.get();}
	public Integration getIntegration() {return integration.get();}
	public Inertia getInertia() {return inertia.get();}
	public Double getMass() {return mass.get();}
	public Double getMinX() {return minX.get();}
	public Double getMaxX() {return maxX.get();}
	public Double getInitV() {return initV.get();}
	public Double getStep() {return step.get();}
	public boolean isInitialized() {return initialized.get();}
	
	//Getters (Trace details - Strings)
	public String getInterpolationType() {return interpolationType.get();}
	public String getIntegrationType() {return integrationType.get();}
	public String getStepSize() {return stepSize.get();}
	public String getIterations() {return iterations.get();}
	public String getTotalTime() {return totalTime.get();}
	public String getComputationTime() {return computationTime.get();}
	public String getEnergyDifference() {return energyDifference.get();}
	
	//Setters (Trace property values)
	public void setName(String name) {this.name.set(name);}
	public void setFile(File file) {this.file.set(file);}
	public void setIntegration(Integration integration) {this.integration.set(integration);}
	public void setInterpolation(Interpolation interpolation) {this.interpolation.set(interpolation);}
	public void setInertia(Inertia inertia) {this.inertia.set(inertia);}
	public void setMass(Double mass) {this.mass.set(mass);}
	public void setMinX(Double minX) {this.minX.set(minX);}
	public void setMaxX(Double maxX) {this.maxX.set(maxX);}
	public void setInitV(Double initV) {this.initV.set(initV);;}
	public void setStep(Double step) {this.step.set(step);}
	public void setInitialized(Boolean initialized) { this.initialized.set(initialized);}

	//Setters (Trace property detail values)
	public void setIntegrationType(String integrationType) {this.integrationType.set(integrationType);}
	public void setInterpolationType(String interpolationType) {this.interpolationType.set(interpolationType);}
	public void setStepSize(String stepSize) {this.stepSize.set(stepSize);}
	public void setIterations(String iterations) { this.iterations.set(iterations);}
	public void setTotalTime(String totalTime) { this.totalTime.set(totalTime);}
	public void setComputationTime(String computationTime) { this.computationTime.set(computationTime);}
	public void setEnergyDifference(String energyDifference) { this.energyDifference.set(energyDifference);}
		
	//Getters (Trace properties)
	public StringProperty getNameProperty() {return name;}
	public ObjectProperty<File> getFileProperty() {return file;}
	public ObjectProperty<Interpolation> getInterpolationProperty() {return interpolation;}
	public ObjectProperty<Integration> getIntegrationProperty() {return integration;}
	public ObjectProperty<Inertia> getInertiaProperty() {return inertia;}
	public ObjectProperty<Double> getMassProperty() {return mass;}
	public ObjectProperty<Double> getMinXProperty() {return minX;}
	public ObjectProperty<Double> getMaxXProperty() {return maxX;}
	public ObjectProperty<Double> getInitVProperty() {return initV;}
	public ObjectProperty<Double> getStepProperty() {return step;}
	public BooleanProperty getInitializedProperty() {return initialized;}
	
	//Getters (Trace property - details)
	public StringProperty getIntegrationTypeProperty() {return integrationType;}
	public StringProperty getInterpolationTypeProperty() {return interpolationType;}
	public StringProperty getStepSizeProperty() {return stepSize;}
	public StringProperty getIterationsProperty() {return iterations;}
	public StringProperty getTotalTimeProperty() {return totalTime;}
	public StringProperty getComputationTimeProperty() {return computationTime;}
	public StringProperty getEnergyDifferenceProperty() {return energyDifference;}
	
	//Graph link
	public void addGraph(Graph graph) {
		linkedGraphs.add(graph);
	}
	public void removeGraph(Graph graph) {
		linkedGraphs.remove(graph);
	}
	
	//Calculations
	/**Method for evaulating the acceleration at given value of x*/
	public double getAccel(double x) {
		// Evaluates the slope angle α(x) 
		double angle = func.slopeAngle(x);
		
		// Numerator: g * sin α(x)				| 
		double numerator = G * Math.sin(angle);
		
		// Formula for moment of inertia: I₀ = c * mr²
		// Denominator: 1 + I₀ / mr²
		// 			  = 1 + c * mr² / mr²		| insert I₀ = c * mr²	
		// 			  = 1 + c					| cancel terms
		double denominator = 1 + getInertia().VALUE;
		
		// Return acceleration
		return numerator / denominator;
	}

	/**Returns the kinetic energy for a given velocity v*/
	public double getKineticEnergy(double v) {
		return 0.5*getMass()*v*v  +  0.5*getMass()*getInertia().VALUE*v*v;
   	}
	
	/**Returns the potential energy for a given value of x*/
	public double getPotentialEnergy(double x) {
		return getMass() * Trace.G * func.eval(x);
	}
	
	/**Returns the total energy for a given velocity v and value of x*/
	public double getTotalEnergy(double v, double x) {
		return getKineticEnergy(v) + getPotentialEnergy(x);
	}
	
	
	//Traces
	/**Performs a trace of the experiment described by this Trace object using specified integration method*/
	public void trace(boolean printIterations, boolean printResults) {
		// Validate instance variables
		validateTrace();
		
		// Clear logs if initialized
		if (isInitialized())
			initializeMap();
		
		// Set initialized property
		setInitialized(true);
		
		// Perform trace using given integration method
		switch (getIntegration()) {
		case EULER_METHOD:
			eulerTrace(printIterations);
			break;
		case EULER_IMPROVED_METHOD:
			System.out.println("Integration type not supported: " + getIntegration().TEXT);
			break;
		case RUNGE_KUTTA_METHOD:
			System.out.println("Integration type not supported: " + getIntegration().TEXT);
			break;
		}
		
		//Set trace details
		tempIntegrationType = getIntegration().TEXT;
		
		//If Trace is not processed in parallel thread, update GUI bindings
//		if (Platform.isFxApplicationThread())
//			updateDetails();
		
		//Print results if requested
		if (printResults) printResults();
	}
	
	/**
	 * Performs trace in using separate thread.
	 */
	public void parallelTrace() {
		//Perform trace
		new Thread(new TraceProcessor(this)).start();
	}
	
	/**Trace performed using Eulers method*/
	private void eulerTrace(boolean printIterations) {
		// Set initial parameters
		double x = getMinX(); 
		double v = getInitV();
		double a = getAccel(x);	
		double eKin = getKineticEnergy(v);
		double ePot = getPotentialEnergy(x);
		double eTot = getTotalEnergy(v, x);
		
		//Save initial energy for comparison
		double initTotEnergy = eTot;
		
		//Used to compute simulation time
		Instant start = Instant.now();
		
		//Keeps track of iterations
		int iter = 0;
		
		//Iterate until track is complete (x has reached its' end value)
		while (x < getMaxX()) {
			//Using floats for lower memory consumption
			aList.add((float) a);
			vList.add((float) v);
			xList.add((float) x);
			yList.add((float) func.eval(x));
			tList.add((float) (iter*getStep()));
			totList.add((float) eTot);
			kinList.add((float) eKin);
			potList.add((float) ePot);
			
			eTot = getTotalEnergy(v, x);
			eKin = getKineticEnergy(v);
			ePot = getPotentialEnergy(x);
			
			a = getAccel(x);	
			v = v + a * getStep();
			x = x + v * Math.cos(func.slopeAngle(x)) * getStep();
			
//			//Increment iteration counter
			iter++;

			//Print iteration results
			if (printIterations)
				System.out.println(String.format("%d\t\ta = %.8f\t\t v = %.8f\t\t x = %.8f\t\t eKin = %.8f\t\t ePot = %.8f\t\t eTot = %.8f\t\t", iter, a, v, x, eKin, ePot, eTot).replace(',', '.'));
		}
		
		
		// End computation timer
		Instant end = Instant.now();

		// Update trace details
		tempEnergyDifference = String.format("%.9f %%", ((initTotEnergy - eTot)/eTot)*100);
		tempIterations = String.valueOf(iter * 2);
		tempStepSize = String.valueOf(getStep());
		tempTotalTime = String.format("%f", iter * getStep()).replace(',', '.');
		tempComputationTime = String.format("%.3f seconds", (double) Duration.between(start, end).toMillis()/1000).replace(',', '.');
	}
	
	
	//Other
	public void printResults() {
		System.out.printf("\nFunction type: %s\n"
				+ "Integration type: %s\n"
				+ "Iterations: %s\n"
				+ "Step size (Δt): %s\n"
				+ "Total time: %s\n"
				+ "\n"
				+ "Computation time: %s\n"
				+ "Energy difference: %s\n", 
					getInterpolationType(),
					getIntegrationType(),
					getIterations(),
					getStep(),
					getTotalTime(),
					getComputationTime(),
					getEnergyDifference());
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		// Initial parameters
		String name = "Test";
		File file = new File("C:\\Users\\Patrik\\git\\Patrik-Forked\\Physics\\src\\imports\\mass_B.txt");
		Integration integration = Integration.EULER_METHOD;				//Integration type
		Interpolation interpolation = Interpolation.POLYNOMIAL_SPLINE;	//Interpolation type
		Inertia inertia = Inertia.POINT_OF_MASS;							//Moment of inertia
		double mass = 10;												//Mass of rolling object
		double minX = 0;												//Min x-coordinate
		double maxX = Double.POSITIVE_INFINITY;							//Max x-coordinate
		double initV = 0;												//Initial velocity
		double step = 0.000002;											//Integration step size
		
		//Initialize new experiment
		Trace testTrace = new Trace(name, file, integration, interpolation, inertia, mass, minX, maxX, initV, step);
		
		
		//Perform trace
		testTrace.parallelTrace();
//		testTrace.trace(false, true);
		
	}

	@Override
	public String toString() {
		return getName();
	}
}






