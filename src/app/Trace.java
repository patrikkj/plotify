package app;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import enums.Inertia;
import enums.Integration;
import enums.Interpolation;
import functions.Differentiable;


public class Trace {
	// Trace properties
	private String name;
	private String filepath;
	private Integration integration;
	private Interpolation interpolation;
	private Inertia inertia;
	private Double mass;
	private Double radius;
	private Double minX;
	private Double maxX;
	private Double initV;
	private Double step;
	
	// Trace Details
	private Integration integrationType;
	private Interpolation interpolationType;
	private String funcType;
	private String stepSize;
	private String iterations;
	private String totalTime;
	private String computationTime;
	private String energyPreserved;

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
	public static final double G = 9.81;
	
	
	//Constructors
    /**
     * Constructor used by GUI.
     */
	public Trace() {

	}	
	
	/**
	 * Creates a Trace object used for numerical analysis.
	 * @param name
	 * @param filepath
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
	public Trace(String name, String filepath, 
				Integration integration, Interpolation interpolation, Inertia inertia, 
				double mass, double radius, double minX, double maxX, double initV, double step) 
	{
		//Assign values
		this.name = name;
		this.filepath = filepath;
		this.integration = integration;
		this.interpolation = interpolation;
		this.inertia = inertia;
		this.mass = mass;
		this.radius = radius;
		this.minX = minX;
		this.maxX= maxX;
		this.initV = initV;
		this.step = step;
	}
	
	
	private void initializeFunc() {
		//Perform interpolation and set domain
		switch (interpolation) {
		case POLYNOMIAL:
			func = analysis.Interpolation.polynomialInterpolation(filepath);
			break;
		case POLYNOMIAL_SPLINE:
			func = analysis.Interpolation.polynomialSplineInterpolation(filepath);
			break;
		}
		
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
	}
	
	
	//Getters (Strings)
	public String getName() {return name;}
	public String getFilepath() {return filepath;}
	public String getInterpolationType() {return interpolation.TEXT;}
	public String getIntegrationType() {return integration.TEXT;}
	public String getInertia() {return inertia.TEXT;}
	public String getMass() {return mass.toString();}
	public String getRadius() {return radius.toString();}
	public String getMinX() {return minX.toString();}
	public String getMaxX() {return maxX.toString();}
	public String getInitV() {return initV.toString();}
	public String getStep() {return step.toString();}
	
	//Setters
	public void setName(String name) {this.name = name;}
	public void setFilepath(String filepath) {this.filepath = filepath;}
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
	public void trace() {
		// Validate instance variables
		validateTrace();
		
		// Perform trace using given integration method
		switch (integration) {
		case EULER_METHOD:
			eulerTrace();
			return;
		case EULER_IMPROVED_METHOD:
			System.out.println("Integration type not supported: " + integration.TEXT);
			return;
		case RUNGE_KUTTA_METHOD:
			System.out.println("Integration type not supported: " + integration.TEXT);
			return;
		}
	}
	
	/**Trace performed using Eulers method*/
	private void eulerTrace() {
		// Set initial parameters
		int iter = 0;
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
			System.out.println(String.format("%d\t\ta = %.8f\t\t v = %.8f\t\t x = %.8f\t\t eKin = %.8f\t\t ePot = %.8f\t\t eTot = %.8f\t\t", iter, a, v, x, eKin, ePot, eTot).replace(',', '.'));
		}
		
		//Print final result
//		System.out.println("\nFinished!");
		System.out.printf("\n\nAccuracy: %s%%\n", (1 - (initTotEnergy - eTot)/eTot)*100);
		System.out.printf("\nIterations: %,d\nStep size (seconds): %s\nTotal time (seconds): %s\n", iter, step, iter * step);
		
		//Print computation time
		Instant end = Instant.now();
		System.out.println(String.format("\nComputation time: %.3f seconds", (double) Duration.between(start, end).toMillis()/1000).replace(',', '.'));
	}
	
	
	//Other
	public static void main(String[] args) throws FileNotFoundException {
		// Initial parameters
		String name = "Test";
		String filepath = "C:\\Users\\Patrik\\git\\Patrik-Forked\\Physics\\src\\imports\\mass_A.txt";
		Integration integration = Integration.EULER_METHOD;				//Integration type
		Interpolation interpolation = Interpolation.POLYNOMIAL_SPLINE;	//Interpolation type
		Inertia inertia = Inertia.SPHERE_SOLID;							//Moment of inertia
		double mass = 10;												//Mass of rolling object
		double radius = 0.2;											//Radius of rolling object
		double minX = 0;												//Min x-coordinate
		double maxX = Double.POSITIVE_INFINITY;							//Max x-coordinate
		double initV = 0;												//Initial velocity
		double step = 0.00001;											//Integration step size
		
		//Initialize new experiment
		Trace testTrace = new Trace(name, filepath, integration, interpolation, inertia, mass, radius, minX, maxX, initV, step);
		
		//Perform trace
		testTrace.trace();
		
	}
}






