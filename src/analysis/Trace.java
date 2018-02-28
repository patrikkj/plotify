package analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import functions.Differentiable;
import functions.PolySpline;


public class Trace {
	// Trace properties
	private String name;
	private String filepath;
	private Integer integration;
	private Integer interpolation;
	private Double step;
	private Double inertiaConst;
	private Double mass;
	private Double radius;
	private Double initX;
	private Double initV;
	
	// Trace Details
	private String funcType;
	private String integrationType;
	private String interpolationType;
	private String stepSize;
	private String iterations;
	private String totalTime;
	private String computationTime;
	private String energyPreserved;

	//Instance variables
	private Differentiable func;
	private double[] domain;
	
	
	//Container
	List<Float> aList = new ArrayList<Float>();
	List<Float> vList = new ArrayList<Float>();
	List<Float> xList = new ArrayList<Float>();
	List<Float> totList = new ArrayList<Float>();
	List<Float> kinList = new ArrayList<Float>();
	List<Float> potList = new ArrayList<Float>();
	List<Float> timeList = new ArrayList<Float>();
	List<Float> yList = new ArrayList<Float>();
	
	
	//Constants (can use Enum)
	public static final double SPHERE = 2d/5d, SPHERE_HOLLOW = 2d/3d;
	public static final int POLY_SPLINE = 0, POLYNOMIAL = 1;
	public static final double G = 9.81;
	
	
	//Mapping
	private static final Map<Integer, String> integrationMap;
    static {
    	integrationMap = new HashMap<>();
    	integrationMap.put(0, "Eulers method");
    	integrationMap.put(1, "Eulers improved method");
    	integrationMap.put(2, "Runge-Kutta method");
    }
    private static final Map<Integer, String> interpolationMap;
    static {
    	interpolationMap = new HashMap<>();
    	interpolationMap.put(0, "Polynomial Spline");
    	interpolationMap.put(1, "Polynomial");
    }
    private static final Map<Double, String> inertiaMap;
    static {
    	inertiaMap = new HashMap<>();
    	inertiaMap.put(null, "Point of mass");
    	inertiaMap.put(1d/2d, "Disc (Solid)");
    	inertiaMap.put(1d/1d, "Disc (Hollow)");
    	inertiaMap.put(2d/5d, "Sphere (Solid)");
    	inertiaMap.put(2d/3d, "Sphere (Hollow)");
    	inertiaMap.put(1d/2d, "Cylinder (Solid)");
    	inertiaMap.put(1d/1d, "Cylinder (Hollow)");
    }
	
	
	//Constructors
    /**
     * Constructor used by GUI.
     */
	public Trace() {
		// Assign default interpolation type
		this.interpolation = 0; 

		// Assign default integration type
		this.integration = 0; 
	}	
	
	/**
	 * Creates a Trace object used for numerical analysis.
	 * @param func - Differentiable interpolation of track (analysis.Interpolation)
	 * @param m - Mass of rolling object (kg)
	 * @param c - Constant for calculating moment of inertia (predefined in Trace.SHAPES)
	 * @param x0 - Initial x-coordinate
	 * @param v0 - Initial velocity (m/s)
	 * @param step - Step size for numerical integration (Eulers' method)
	 */
	public Trace(String filepath, double mass, double radius, double inertiaConst, double initX, double initV, double step) {
		// Assign default interpolation type
		this.interpolation = 0; 
		
		// Assign default integration type
		this.integration = 0; 

		//Assign values
		this.filepath = filepath;
		this.mass = mass;
		this.radius = radius;
		this.inertiaConst = inertiaConst;
		this.initX = initX;
		this.initV = initV;
		this.step = step;
		
		//Initializes function and validates input parameters
		validateTrace();		
	}
	
	
	private void initializeFunc() {
		//Perform interpolation and set domain
		switch (interpolation) {
		case 0:
			func = Interpolation.polynomialSplineInterpolation(filepath);
			domain = func.getDomain();
			return;
		case 1:
			func = Interpolation.polynomialInterpolation(filepath);
			domain = func.getDomain();
			return;
		}
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
		if (inertiaConst < 0)
			throw new IllegalArgumentException("Constant describing moment of inertia must be positive.");
		
		//Validate initial x-value
		if (!isValidInitX())
			throw new IllegalArgumentException("Initial x value must be within function domain.");

	}
	
	public boolean isValidFilepath() {
		return (new File(filepath).isFile());
	}
	public boolean isValidMass() {
		return (mass > 0);
	}
	public boolean isValidRadius() {
		return (radius > 0);
	}
	public boolean isValidInertiaConst() {
		return (inertiaConst >= 0);
	}
	public boolean isValidInitX() {			
		return !(initX < func.getDomain()[0]  ||  initX > func.getDomain()[1]);
	}
	
	
	//Getters (Strings)
	public String getName() {return name;}
	public String getFilepath() {return filepath;}
	public String getMass() {return mass.toString();}
	public String getRadius() {return radius.toString();}
	public String getInterpolationType() {return interpolationMap.get(interpolation);}
	public String getInertiaConst() {return inertiaMap.get(inertiaConst);}
	public String getInitX() {return initX.toString();}
	public String getInitV() {return initV.toString();}
	
	//Setters
	public void setName(String name) {this.name = name;}
	public void setFilepath(String filepath) {this.filepath = filepath;}
	public void setMass(Double mass) {this.mass = mass;}
	public void setRadius(Double radius) {this.radius = radius;}
	public void setInertiaConst(Double inertiaConst) {this.inertiaConst = inertiaConst;}
	public void setInitX(Double initX) {this.initX = initX;}
	public void setInitV(Double initV) {this.initV = initV;}

	
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
		double denominator = 1 + inertiaConst;
		
		// Return acceleration
		return numerator / denominator;
	}

	/**Returns the kinetic energy for a given velocity v*/
	public double getKineticEnergy(double v) {
		return 0.5*mass*v*v  +  0.5*mass*inertiaConst*v*v;
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
	/**Prints a trace of the experiment described by this Trace object using Eulers' Method twice*/
	public void eulerTrace() {
		// Set initial parameters
		int iter = 0;
		double x = initX, v = initV, a = getAccel(x);	
		double eKin = getKineticEnergy(v);
		double ePot = getPotentialEnergy(x);
		double eTot = getTotalEnergy(v, x);
		
		//Save initial energy for comparison
		double initTotEnergy = eTot;
		
		//Used to compute simulation time
		System.out.println("Processing");
		Instant start = Instant.now();
		
		//Processing
		int approxIter = (int) ((domain[1] - domain[0]) / step); 
		
		//Iterate until track is complete (x has reached its' end value)
		while (x < domain[1]) {
			
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
		//Expermient data
		String filepath = "C:\\Users\\Patrik\\git\\Patrik-Forked\\Physics\\src\\imports\\mass_A.txt";
		double mass = 10000;						//Mass of rolling object
		double radius = 0.2;						//Radius of rolling object
		double inertiaConst = Trace.SPHERE;			//Constant for calculating moment of inertia
		double initV = 0;							//Initial velocity
		double initX = 0.13133756649499462;			//Initial x-coordinate
		double step = 0.00001;
		
		//Initialize new experiment
		Trace experiment = new Trace(filepath, mass, radius, inertiaConst, initX, initV, step);
		
		//Print a trace using Eulers' method
		experiment.eulerTrace();
		
	}
	
}
