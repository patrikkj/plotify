package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import enums.Inertia;
import enums.Integration;
import enums.Interpolation;
import functions.Differentiable;
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
//	private ObjectProperty<Double> radius;
	private ObjectProperty<Double> minX;
	private ObjectProperty<Double> maxX;
	private ObjectProperty<Double> initV;
	private ObjectProperty<Double> step;
	private BooleanProperty initialized;
	
	// Trace Details (Only Getters)
	private Integration integrationType;
	private Interpolation interpolationType;
	private String stepSize;
	private String iterations;
	private String totalTime;
	private String computationTime;
	private String energyDifference;

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
		
		//Set default values
		setName("New trace");
		setIntegration(Integration.EULER_METHOD);
		setInterpolation(Interpolation.POLYNOMIAL_SPLINE);
		setInertia(Inertia.POINT_OF_MASS);
		setMass(1d);
//		setRadius(1d);
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
//		setRadius(radius);
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
//		radius = new SimpleObjectProperty<>();
		minX = new SimpleObjectProperty<>();
		maxX = new SimpleObjectProperty<>();
		initV = new SimpleObjectProperty<>();
		step = new SimpleObjectProperty<>();
		initialized = new SimpleBooleanProperty();
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
		
		//Set trace details
		interpolationType = getInterpolation();
		
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
	
	//Validation
	public void validateTrace() {
		initializeFunc();
		
		//Validate mass
		if (getMass() <= 0)
			throw new IllegalArgumentException("Mass must be positive.");
		
//		//Validate radius
//		if (getRadius() < 0)
//			throw new IllegalArgumentException("Radius cannot be negative.");
		
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
//	public Double getRadius() {return radius.get();}
	public Double getMinX() {return minX.get();}
	public Double getMaxX() {return maxX.get();}
	public Double getInitV() {return initV.get();}
	public Double getStep() {return step.get();}
	public boolean isInitialized() {return initialized.get();}
	
	//Getters (Trace details - Strings)
	public Interpolation getInterpolationType() {return interpolationType;}
	public Integration getIntegrationType() {return integrationType;}
	public String getStepSize() {return stepSize;}
	public String getIterations() {return iterations;}
	public String getTotalTime() {return totalTime;}
	public String getComputationTime() {return computationTime;}
	public String getEnergyDifference() {return energyDifference;}
	
	//Setters (Trace property values)
	public void setName(String name) {this.name.set(name);}
	public void setFile(File file) {this.file.set(file);}
	public void setIntegration(Integration integration) {this.integration.set(integration);}
	public void setInterpolation(Interpolation interpolation) {this.interpolation.set(interpolation);}
	public void setInertia(Inertia inertia) {this.inertia.set(inertia);}
	public void setMass(Double mass) {this.mass.set(mass);}
//	public void setRadius(Double radius) {this.radius.set(radius);}
	public void setMinX(Double minX) {this.minX.set(minX);}
	public void setMaxX(Double maxX) {this.maxX.set(maxX);}
	public void setInitV(Double initV) {this.initV.set(initV);;}
	public void setStep(Double step) {this.step.set(step);}
	public void setInitialized(Boolean initialized) { this.initialized.set(initialized);}

	//Getters (Trace properties)
	public StringProperty getNameProperty() {return name;}
	public ObjectProperty<File> getFileProperty() {return file;}
	public ObjectProperty<Interpolation> getInterpolationProperty() {return interpolation;}
	public ObjectProperty<Integration> getIntegrationProperty() {return integration;}
	public ObjectProperty<Inertia> getInertiaProperty() {return inertia;}
	public ObjectProperty<Double> getMassProperty() {return mass;}
//	public ObjectProperty<Double> getRadiusProperty() {return radius;}
	public ObjectProperty<Double> getMinXProperty() {return minX;}
	public ObjectProperty<Double> getMaxXProperty() {return maxX;}
	public ObjectProperty<Double> getInitVProperty() {return initV;}
	public ObjectProperty<Double> getStepProperty() {return step;}
	public BooleanProperty getInitializedProperty() {return initialized;}
	
	
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
		integrationType = getIntegration();
		
		//Print results if requested
		if (printResults) printResults();
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
		System.out.println("Processing");
		Instant start = Instant.now();
		
		//Keeps track of iterations
		int iter = 0;
		int approxIter = (int) ((domain[1] - domain[0]) / getStep()); 
		
		
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
			
			iter++;
//			//Increment iteration counter
//			if (iter++ % (approxIter/10) == 0)
//				System.out.print('.');

			//Print iteration results
			if (printIterations)
				System.out.println(String.format("%d\t\ta = %.8f\t\t v = %.8f\t\t x = %.8f\t\t eKin = %.8f\t\t ePot = %.8f\t\t eTot = %.8f\t\t", iter, a, v, x, eKin, ePot, eTot).replace(',', '.'));
		}
		
		
		// End computation timer
		Instant end = Instant.now();

		// Update trace details
		energyDifference = String.format("%.9f %%", ((initTotEnergy - eTot)/eTot)*100);
		iterations = String.valueOf(iter * 2);
		stepSize = String.valueOf(getStep());
		totalTime = String.format("%f", iter * getStep()).replace(',', '.');
		computationTime = String.format("%.3f seconds", (double) Duration.between(start, end).toMillis()/1000).replace(',', '.');
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
//		double radius = 0.2;											//Radius of rolling object
		double minX = 0;												//Min x-coordinate
		double maxX = Double.POSITIVE_INFINITY;							//Max x-coordinate
		double initV = 0;												//Initial velocity
		double step = 0.000001;											//Integration step size
		
		//Initialize new experiment
		Trace testTrace = new Trace(name, file, integration, interpolation, inertia, mass, minX, maxX, initV, step);
		
		
		//Perform trace
		testTrace.trace(false, true);
		
	}

	@Override
	public String toString() {
		return getName();
	}
}






