package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import enums.Inertia;
import enums.Integration;
import enums.Interpolation;
import functions.Differentiable;
import javafx.beans.property.StringProperty;


public class Trace {
	// Trace properties (Setters & Getters)
	private String name;
	private File file;
	private Integration integration;
	private Interpolation interpolation;
	private Inertia inertia;
	private Double mass;
	private Double radius;
	private Double minX;
	private Double maxX;
	private Double initV;
	private Double step;
	private boolean initialized;
	private StringProperty nameProperty;
	
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
	List<Float> aList = new ArrayList<Float>();
	List<Float> vList = new ArrayList<Float>();
	List<Float> xList = new ArrayList<Float>();
	List<Float> totList = new ArrayList<Float>();
	List<Float> kinList = new ArrayList<Float>();
	List<Float> potList = new ArrayList<Float>();
	List<Float> timeList = new ArrayList<Float>();
	List<Float> yList = new ArrayList<Float>();

	
	///////////////////////////////////////////////
	// Enumerations in separate package (.enums) //
	///////////////////////////////////////////////
	public static final double G = 9.82814;
	
	
	//Constructors
    /**
     * Constructor used by GUI.
     */
	public Trace() {
		nameProperty.setValue(v);
		this.name = "New trace";
	}	
	
	/**
	 * Creates a Trace object used for numerical analysis.
	 * @param name
	 * @param file
	 * @param integration
	 * @param interpolation
	 * @param inertia
	 * @param mass
	 * @param radius
	 * @param minX
	 * @param maxX
	 * @param initV
	 * @param step
	 */
	public Trace(String name, File file, 
				Integration integration, Interpolation interpolation, Inertia inertia, 
				double mass, double radius, double minX, double maxX, double initV, double step) 
	{
		//Assign values
		this.name = name;
		this.file = file;
		this.integration = integration;
		this.interpolation = interpolation;
		this.inertia = inertia;
		this.mass = mass;
		this.radius = radius;
		this.minX = minX;
		this.maxX= maxX;
		this.initV = initV;
		this.step = step;
		System.out.println(file);
	}
	
	
	private void initializeFunc() {
		//Perform interpolation and set domain
		switch (interpolation) {
		case POLYNOMIAL:
			func = analysis.Interpolation.polynomialInterpolation(file);
			break;
		case POLYNOMIAL_SPLINE:
			func = analysis.Interpolation.polynomialSplineInterpolation(file);
			break;
		}
		
		//Set trace details
		interpolationType = interpolation;
		
		//Set domain and x-range
		domain = func.getDomain();
		minX = Math.max(minX, domain[0]);
		maxX = Math.min(maxX, domain[1]);
	}
	
	//Validation
	public void validateTrace() {
		initializeFunc();
		
		//Validate mass
		if (mass <= 0)
			throw new IllegalArgumentException("Mass must be positive.");
		
		//Validate radius
		if (radius < 0)
			throw new IllegalArgumentException("Radius cannot be negative.");
		
		//Validate inertia constant
		if (inertia.VALUE < 0)
			throw new IllegalArgumentException("Moment of inertia cannot be negative.");
		
		//Initialization complete
		initialized = true;
	}
	
	
	//Getters (Trace properties)
	public String getName() {return name;}
	public File getFile() {return file;}
	public Interpolation getInterpolation() {return interpolation;}
	public Integration getIntegration() {return integration;}
	public Inertia getInertia() {return inertia;}
	public Double getMass() {return mass;}
	public Double getRadius() {return radius;}
	public Double getMinX() {return minX;}
	public Double getMaxX() {return maxX;}
	public Double getInitV() {return initV;}
	public Double getStep() {return step;}
	public boolean isInitialized() {return initialized;}
	
	//Getters (Other)
	public StringProperty nameProperty() {
		return 
	}
	
	//Getters (Trace details - Strings)
	public String getInterpolationType() {return interpolationType.TEXT;}
	public String getIntegrationType() {return integrationType.TEXT;}
	public String getStepSize() {return stepSize;}
	public String getIterations() {return iterations;}
	public String getTotalTime() {return totalTime;}
	public String getComputationTime() {return computationTime;}
	public String getEnergyDifference() {return energyDifference;}
	
	//Setters (Trace properties)
	public void setName(String name) {this.name = name;}
	public void setFile(File file) {this.file = file;}
	public void setIntegration(Integration integration) {this.integration = integration;}
	public void setInterpolation(Interpolation interpolation) {this.interpolation = interpolation;}
	public void setInertia(Inertia inertia) {this.inertia = inertia;}
	public void setMass(Double mass) {this.mass = mass;}
	public void setRadius(Double radius) {this.radius = radius;}
	public void setMinX(Double minX) {this.minX = minX;}
	public void setMaxX(Double maxX) {this.maxX = maxX;}
	public void setInitV(Double initV) {this.initV = initV;}
	public void setStep(Double step) {this.step = step;}

	
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
		double denominator = 1 + inertia.VALUE;
		
		// Return acceleration
		return numerator / denominator;
	}

	/**Returns the kinetic energy for a given velocity v*/
	public double getKineticEnergy(double v) {
		return 0.5*mass*v*v  +  0.5*mass*inertia.VALUE*v*v;
   	}
	
	/**Returns the potential energy for a given value of x*/
	public double getPotentialEnergy(double x) {
		return mass * Trace.G * func.eval(x);
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
		
		// Perform trace using given integration method
		switch (integration) {
		case EULER_METHOD:
			eulerTrace(printIterations);
			break;
		case EULER_IMPROVED_METHOD:
			System.out.println("Integration type not supported: " + integration.TEXT);
			break;
		case RUNGE_KUTTA_METHOD:
			System.out.println("Integration type not supported: " + integration.TEXT);
			break;
		}
		
		//Set trace details
		integrationType = integration;
		
		//Print results if requested
		if (printResults) printResults();
	}
	
	/**Trace performed using Eulers method*/
	private void eulerTrace(boolean printIterations) {
		// Set initial parameters
		double x = minX; 
		double v = initV;
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
		int approxIter = (int) ((domain[1] - domain[0]) / step); 
		
		
		//Iterate until track is complete (x has reached its' end value)
		while (x < maxX) {
			//Using floats for lower memory consumption
			aList.add((float) a);
			vList.add((float) v);
			xList.add((float) x);
			totList.add((float) eTot);
			kinList.add((float) eKin);
			potList.add((float) ePot);
			timeList.add((float) (iter*step));
			yList.add((float) func.eval(x));
			
			eTot = getTotalEnergy(v, x);
			eKin = getKineticEnergy(v);
			ePot = getPotentialEnergy(x);
			
			a = getAccel(x);	
			v = v + a * step;
			x = x + v * Math.cos(func.slopeAngle(x)) * step;
			
			//Increment iteration counter
			if (iter++ % (approxIter/10) == 0)
				System.out.print('.');

			//Print iteration results
			if (printIterations)
				System.out.println(String.format("%d\t\ta = %.8f\t\t v = %.8f\t\t x = %.8f\t\t eKin = %.8f\t\t ePot = %.8f\t\t eTot = %.8f\t\t", iter, a, v, x, eKin, ePot, eTot).replace(',', '.'));
		}
		
		
		// End computation timer
		Instant end = Instant.now();

		// Update trace details
		energyDifference = String.format("%.9f %%", ((initTotEnergy - eTot)/eTot)*100);
		iterations = String.valueOf(iter * 2);
		stepSize = String.valueOf(step);
		totalTime = String.format("%f", iter * step).replace(',', '.');
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
		double radius = 0.2;											//Radius of rolling object
		double minX = 0;												//Min x-coordinate
		double maxX = Double.POSITIVE_INFINITY;							//Max x-coordinate
		double initV = 0;												//Initial velocity
		double step = 0.000001;											//Integration step size
		
		//Initialize new experiment
		Trace testTrace = new Trace(name, file, integration, interpolation, inertia, mass, radius, minX, maxX, initV, step);
		
		//Perform trace
		testTrace.trace(false, true);
		
	}

	public String toString() {
		return name;
	}
}






