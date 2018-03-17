package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;

import enums.Inertia;
import enums.Integration;
import enums.Interpolation;
import functions.AbstractDifferentiable;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import parsers.Data;


public class Trace {
	// Data
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
	// Details
	private StringProperty integrationType;
	private StringProperty interpolationType;
	private StringProperty stepSize;
	private StringProperty iterations;
	private StringProperty totalTime;
	private StringProperty computationTime;
	private StringProperty energyDifference;
	// Progress bar
	private IntegerProperty progress;
	// Temporary fields to avoid multithread UI updates
	private String tempIntegrationType;
	private String tempInterpolationType;
	private String tempStepSize;
	private String tempIterations;
	private String tempTotalTime;
	private String tempComputationTime;
	private String tempEnergyDifference;
	//Function
	private AbstractDifferentiable func;
	private double[] domain;
	private double rawIterations;
	private double min, max;
	//Collections
	private ObservableMap<String, ObservableList<Double>> traceMap;
	private ObservableList<Double> aList, vList, xList, yList, tList;
	private ObservableList<Double> totList, kinList, potList;
	private ObservableList<Double> normForceList, fricForceList;
	private ObservableList<Double> slopeAngleList, slopeAngleDegList, radCurvatureList;
	private ObservableList<Double> tListRaw, xListRaw, yListRaw;
	private HashSet<Graph> linkedGraphs;
	//Change listeners
	private ChangeListener<File> fileChangeListener;
	//Constants
	public static final int SIZE_LIMIT = 10000;
	public static final double G = 9.82814;
	public static final String[] MAP_KEYS = new String[] {
			"Acceleration",
			"Velocity",
			"Position (x)",
			"Position (y)",
			"Time (s)",
			"Total energy",
			"Kinetic energy",
			"Potential energy",
			"Raw data (t)",
			"Raw data (x)",
			"Raw data (y)",
			"Normal force",
			"Friction force",
			"Slope angle (Rad)",
			"Slope angle (Deg)",
			"Radius of Curvature"};
	
	int listSize = 0;

	
	//Constructors
    /**
     * Constructor used by GUI.
     */
	public Trace() {
		//Initialize properties
		initializeProperties();
		
		//Initialize map
		initializeCollections();

		//Initialize change listeners
		initializeChangeListeners();
		
		//Set default data values
		setName("New trace");
		setIntegration(Integration.EULER_METHOD);
		setInterpolation(Interpolation.POLYNOMIAL);
		setInertia(Inertia.POINT_OF_MASS);
		setMass(1d);
		setMinX(Double.NEGATIVE_INFINITY);
		setMaxX(Double.POSITIVE_INFINITY);
		setInitV(0d);
		setStep(0.001);
	}	

	/**
	 * Creates a Trace object used for numerical analysis.
	 */
	public Trace(String name, File file, 
				Integration integration, Interpolation interpolation, Inertia inertia, 
				double mass, double minX, double maxX, double initV, double step) 
	{
		//Initialize properties
		initializeProperties();
		
		//Initialize collections
		initializeCollections();
		
		//Initialize change listeners
		initializeChangeListeners();
		
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
	/**
	 * Initializes all trace properties.
	 */
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
		
		progress = new SimpleIntegerProperty();
	}

	/**
	 * Initializes all trace collections.
	 */
	private void initializeCollections() {
		// Initialize map
		traceMap = FXCollections.observableHashMap();

		// Initialize collections
		initializeLists();
		initializeRawLists();

		//Initialize linked graph container
		linkedGraphs = new HashSet<>();
	}

	/**
	 * Initialize raw data lists and fill if a file has been selected.
	 * Called on initialization and whenever a new file is selected.
	 */
	private void initializeRawLists() {
		//Initialize raw data collections
		tListRaw = FXCollections.observableArrayList();
		xListRaw = FXCollections.observableArrayList();
		yListRaw = FXCollections.observableArrayList();

		//Fill raw data collections
		if (getFile() != null) {
			double[][] rawData = Data.parseFile(getFile());
			Arrays.stream(rawData[0]).forEach(doub -> tListRaw.add(Double.valueOf(doub)));
			Arrays.stream(rawData[1]).forEach(doub -> xListRaw.add(Double.valueOf(doub)));
			Arrays.stream(rawData[2]).forEach(doub -> yListRaw.add(Double.valueOf(doub)));
		}
		
		//Fill map
		traceMap.put("Raw data (t)", tListRaw);
		traceMap.put("Raw data (x)", xListRaw);
		traceMap.put("Raw data (y)", yListRaw);
	}
	
	/**
	 * Initialize data lists.
	 */
	private void initializeLists() {
		// Initialize observable lists
		aList = FXCollections.observableArrayList();
		vList = FXCollections.observableArrayList();
		xList = FXCollections.observableArrayList();
		yList = FXCollections.observableArrayList();
		tList = FXCollections.observableArrayList();
		totList = FXCollections.observableArrayList();
		kinList = FXCollections.observableArrayList();
		potList = FXCollections.observableArrayList();
		normForceList = FXCollections.observableArrayList();
		fricForceList = FXCollections.observableArrayList();
		slopeAngleList = FXCollections.observableArrayList();
		slopeAngleDegList = FXCollections.observableArrayList();
		radCurvatureList = FXCollections.observableArrayList();

		// Fill map
		traceMap.put("Acceleration", aList);
		traceMap.put("Velocity", vList);
		traceMap.put("Position (x)", xList);
		traceMap.put("Position (y)", yList);
		traceMap.put("Time (s)", tList);
		traceMap.put("Total energy", totList);
		traceMap.put("Kinetic energy", kinList);
		traceMap.put("Potential energy", potList);
		traceMap.put("Normal force", normForceList);
		traceMap.put("Friction force", fricForceList);
		traceMap.put("Slope angle (Rad)", slopeAngleList);
		traceMap.put("Slope angle (Deg)", slopeAngleDegList);
		traceMap.put("Radius of Curvature", radCurvatureList);
	}
	
	/**
	 * Change listener for selected file, updating raw data sets
	 */
	private void initializeChangeListeners() {
		fileChangeListener = new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends File> arg0, File arg1, File arg2) {
				initializeRawLists();
				
				//Update subscribing graphs if trace is run from GUI
				if (linkedGraphs != null)  
					linkedGraphs.forEach(graph -> graph.updateGraph());
			}
		};
		
		file.addListener(fileChangeListener);
	}
	
	
	// Helpers
	/**
	 * Returns {@code true} if Trace is runnable, else {@code false}.
	 */
	private void validateTrace() {
		//Validate mass
		if (getMass() <= 0)
			throw new IllegalArgumentException("Mass must be positive.");
		
		//Validate inertia constant
		if (getInertia().VALUE < 0)
			throw new IllegalArgumentException("Moment of inertia cannot be negative.");
	}
	
	/**
	 * Clears all data collections, called upon when reevaluating trace.
	 */
	private void clearCollections() {
		initializeLists();
		initializeRawLists();
	}
	
	/**
	 * Update trace details. Only call this method from FXApplication Thread.
	 */
	public void updateTrace() {
		// Update trace details
		setIntegrationType(tempIntegrationType);
		setInterpolationType(tempInterpolationType);
		setEnergyDifference(tempEnergyDifference);
		setIterations(tempIterations);
		setStepSize(tempStepSize);
		setTotalTime(tempTotalTime);
		setComputationTime(tempComputationTime);
		
		//Update subscribing graphs if trace is run from GUI
		if (linkedGraphs != null)  
			linkedGraphs.forEach(graph -> graph.updateGraph());
	}

	
	// Trace
	/**
	 * Run selected interpolation.
	 */
	private void interpolate() {  /// IS INTERPOLATABLE... RAW / FULL TRACE
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
		tempInterpolationType = getInterpolation().TEXT;
		
		//Set domain
		domain = func.getDomain();
		min = Math.max(getMinX(), domain[0]);
		max = Math.min(getMaxX(), domain[1]);
	}
	
	/**
	 * Perform selected integration.
	 */
	private void integrate() {
		// Perform trace using given integration method
		switch (getIntegration()) {
		case EULER_METHOD:
			eulerTrace();
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
	}
	
	
	// Trace <-> Graph links 
	/*
	 * Returns a HashSet containing all Trace <-> Graph links.
	 */
	public HashSet<Graph> getGraphs() {
		return linkedGraphs;
	}
	
	/**
	 * Adds given graph to set of linked graphs. Called indirectly from Graph.
	 */
	public void addGraph(Graph graph) {
		linkedGraphs.add(graph);
	}
	
	/**
	 * Removes given graph from set of linked graphs. Called indirectly from Graph.
	 */
	public void removeGraph(Graph graph) {
		linkedGraphs.remove(graph);
	}
	
	
	// Calculations
	/**
	 * Evaluates the acceleration at given x-coordinate.
	 */
	private double getAccel(double x) {
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

	/**
	 * Returns the kinetic energy for a given velocity.
	 */
	private double getKineticEnergy(double v) {
		return 0.5*getMass()*v*v  +  0.5*getMass()*getInertia().VALUE*v*v;
   	}
	
	/**
	 * Returns the potential energy for a given x-coordinate.
	 */
	private double getPotentialEnergy(double x) {
		return getMass() * Trace.G * func.eval(x);
	}
	
	/**
	 * Returns the total energy for a given velocity and x-coordinate.
	 */
	private double getTotalEnergy(double x, double v) {
		return getKineticEnergy(v) + getPotentialEnergy(x);
	}
	
	/**
	 * Returns the fricton force for a given x-coordinate.
	 * Explanation:
	 *  -> SUM(F_x) = Parallel - Friction
	 *  -> Friction = SUM(F_x) - Parallel
	 */
	private double getFrictionForce(double x) {
		return getMass() * (G * Math.sin(func.slopeAngle(x)) - getAccel(x));
	}
	
	/**
	 * Returns the normal force for a given velocity and x-coordinate.
	 * This is equivalent to the sum of the normal and the centripetal force.
	 */
	private double getNormalForce(double x, double v) {
		// Normal force
		double normal = getMass() * G * Math.cos(func.slopeAngle(x));
		
		// Centripetal force
		double centripetal = (getMass()*v*v) / func.radiusOfCurvature(x);
		
//		System.out.printf("x: %s\tv: %s\tNormal: %s\tCentripetal: %s\t Total: %s%n", x, v, normal, centripetal, normal + centripetal);
		return normal + centripetal;
	}
	
	
	// Trace
	/**
	 * Performs a trace of the experiment.
	 */
	public void trace() {
		// Reset progress bar
		resetProgress();
		
		// Validate instance variables
		validateTrace();
		
		// Clear data collections if initialized
		if (isInitialized())
			clearCollections();		
		setInitialized(true);
		
		// Run interpolation
		interpolate();
		
		// Run integration
		integrate();
		
		//If Trace is not processed in parallel thread, update GUI bindings
//		if (Platform.isFxApplicationThread())
//			updateDetails();

	}
	
	/**
	 * Performs trace using separate thread.
	 */
	public void parallelTrace() {
		// Reset progress bar
		resetProgress();
		listSize = 0;
		
		//Perform trace
		new Thread(new TraceProcessor(this)).start();
	}
	
	/**
	 * Trace performed using Eulers method.
	 */
	private void eulerTrace() {
		//Used to compute simulation time
		Instant start = Instant.now();
		
		// Calibrate using raw trace
		rawEulerTrace();
		
		// Set initial parameters
		double x = min; 
		double v = getInitV();
		double a = getAccel(x);	
		
		//Keeps track of iterations
		int iter = 0;
		
		// Calculate step size
		double step = ((double) rawIterations - 1d)  /  ((double) SIZE_LIMIT - 1d);
		
		// Fill list
		HashSet<Integer> indexSet = new HashSet<>();
		for (int i = 0; i < SIZE_LIMIT; i++)
				indexSet.add((int) Math.round(step * (double) i));
		
		
		//Iterate until track is complete (x has reached its' end value)
		while (x < max) {
			if (indexSet.contains(iter++)) {
				aList.add(a);
				vList.add(v);
				xList.add(x);
				yList.add(func.eval(x));
				tList.add(iter*getStep());
				totList.add(getTotalEnergy(x, v));
				kinList.add(getKineticEnergy(v));
				potList.add(getPotentialEnergy(x));
				normForceList.add(getNormalForce(x, v));
				fricForceList.add(getFrictionForce(x));
				slopeAngleList.add(func.slopeAngle(x));
				slopeAngleDegList.add(func.slopeAngleDegrees(x));
//				radCurvatureList.add(func.radiusOfCurvature(x) == Double.POSITIVE_INFINITY ? 0 : func.radiusOfCurvature(x));
				radCurvatureList.add(func.radiusOfCurvature(x));
				
				//Perform GUI Updates in FX Application Thread
				if (listSize++ % 100 == 0) {
					Platform.runLater(() -> incrementProgress());
//					System.out.println(getProgress());
				}
			}
				
			a = getAccel(x);	
			v = v + a * getStep();
			x = x + v * Math.cos(func.slopeAngle(x)) * getStep();
			
			//Print iteration results
//			if (printIterations)
//				System.out.println(String.format("%d\t\ta = %.8f\t\t v = %.8f\t\t x = %.8f\t\t eKin = %.8f\t\t ePot = %.8f\t\t eTot = %.8f\t\t", iter, a, v, x, eKin, ePot, eTot).replace(',', '.'));
		}
		
		
		// End computation timer
		Instant end = Instant.now();

		// Update trace details
		tempEnergyDifference = String.format("%.9f %%", ((
				getTotalEnergy(min, getInitV()) - 
				getTotalEnergy(xList.get(xList.size() - 1), vList.get(vList.size() - 1)))/
				getTotalEnergy(xList.get(xList.size() - 1), vList.get(vList.size() - 1)))*100);
		tempIterations = String.valueOf(iter * 2);
		tempIterations = String.format("%,d", iter*2).replace(',', ' ');
		tempStepSize = String.valueOf(getStep());
		tempTotalTime = String.format("%f", iter * getStep()).replace(',', '.');
		tempComputationTime = String.format("%.3f seconds", (double) Duration.between(start, end).toMillis()/1000).replace(',', '.');
		System.out.println(func.toString());
	}
	
	/**
	 * Raw trace performed without data storage.
	 */
	private void rawEulerTrace() {
		// Set initial parameters
		double i = 0;
		double x = min; 
		double v = getInitV();
		double step = getStep();

		while (x < max) {
			v += getAccel(x) * step;
			x += v * Math.cos(func.slopeAngle(x)) * step;
			i++;
		}
		
		rawIterations = i;
	}
	
	
	//Other
	/**
	 * Prints an overview of trace results.
	 */
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
	
	/**
	 * Returns the name of current String, used by GUI components.
	 */
	@Override
	public String toString() {
		return getName();
	}

	

	/*
	 * Property Getters
	 */
	// Data
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
	// Details
	public StringProperty getIntegrationTypeProperty() {return integrationType;}
	public StringProperty getInterpolationTypeProperty() {return interpolationType;}
	public StringProperty getStepSizeProperty() {return stepSize;}
	public StringProperty getIterationsProperty() {return iterations;}
	public StringProperty getTotalTimeProperty() {return totalTime;}
	public StringProperty getComputationTimeProperty() {return computationTime;}
	public StringProperty getEnergyDifferenceProperty() {return energyDifference;}
	// Progress bar
	public IntegerProperty getProgressProperty() {return progress;}

	/*
	 * Getters
	 */
	// Data
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
	public ObservableMap<String, ObservableList<Double>> getDataMap() {return traceMap;}
	// Details
	public String getInterpolationType() {return interpolationType.get();}
	public String getIntegrationType() {return integrationType.get();}
	public String getStepSize() {return stepSize.get();}
	public String getIterations() {return iterations.get();}
	public String getTotalTime() {return totalTime.get();}
	public String getComputationTime() {return computationTime.get();}
	public String getEnergyDifference() {return energyDifference.get();}
	// Progress bar
	public Integer getProgress() {return progress.get();}
	
	/*
	 * Setters
	 */
	// Data
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
	// Details
	public void setIntegrationType(String integrationType) {this.integrationType.set(integrationType);}
	public void setInterpolationType(String interpolationType) {this.interpolationType.set(interpolationType);}
	public void setStepSize(String stepSize) {this.stepSize.set(stepSize);}
	public void setIterations(String iterations) { this.iterations.set(iterations);}
	public void setTotalTime(String totalTime) { this.totalTime.set(totalTime);}
	public void setComputationTime(String computationTime) { this.computationTime.set(computationTime);}
	public void setEnergyDifference(String energyDifference) { this.energyDifference.set(energyDifference);}
	// Progress bar
	public void incrementProgress() {this.progress.set(progress.get() + 1);}
	public void resetProgress() {this.progress.set(0);}
	
	public static void main(String[] args) throws FileNotFoundException {
		// Initial parameters
		String name = "Test";
		File file = new File("C:\\Users\\Patrik\\git\\Patrik-Forked\\Physics\\src\\imports\\mass_B.txt");
		Integration integration = Integration.EULER_METHOD;				//Integration type
		Interpolation interpolation = Interpolation.POLYNOMIAL_SPLINE;	//Interpolation type
		Inertia inertia = Inertia.POINT_OF_MASS;						//Moment of inertia
		double mass = 10;												//Mass of rolling object
		double minX = 0;												//Min x-coordinate
		double maxX = Double.POSITIVE_INFINITY;							//Max x-coordinate
		double initV = 0;												//Initial velocity
		double step = 0.00001;											//Integration step size
		
		//Initialize new trace
		Trace testTrace = new Trace(name, file, integration, interpolation, inertia, mass, minX, maxX, initV, step);
		
		//Perform trace
		testTrace.trace();
		
		//Update results
		testTrace.updateTrace();
		
		// Print results
		testTrace.printResults();
		
		testTrace.clearCollections();
		System.out.println("He");
		
	}
}
