package analysis;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import functions.Differentiable;
import functions.PolySpline;

public class Trace {
	// Instance variables (Check constructor comment for details)
	private Differentiable func;
	private double m, c, v0, x0;
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
	
	// Constants
	public static final double SPHERE = 2d/5d, SPHERE_HOLLOW = 2d/3d;
	public static final double G = 9.81;
	
	// Constructor
	/**
	 * Creates a Trace object used for numerical analysis
	 * 
	 * @param func - Differentiable interpolation of track (analysis.Interpolation)
	 * @param m - Mass of rolling object (kg)
	 * @param c - Constant for calculating moment of inertia (predefined in Trace.SHAPES)
	 * @param v0 - Initial velocity (m/s)
	 * @param x0 - Initial x-coordinate
	 * @param step - Step size for numerical integration (Eulers' method)
	 */
	public Trace(Differentiable func, double m, double c, double v0, double x0) {
		//Validate initial x-value
		if (x0 < func.getDomain()[0]  ||  x0 > func.getDomain()[1])
			throw new IllegalArgumentException("Initial x value must be within function domain.");
		
		//Validate mass
		if (m <= 0)
			throw new IllegalArgumentException("Mass must be positive.");
		
		//Validate c
		if (c < 0)
			throw new IllegalArgumentException("Constant describing moment of inertia must be positive.");
		

		//Assign values
		this.func = func;
		this.m = m;
		this.c = c;
		this.v0 = v0;
		this.x0 = x0;
		this.domain = func.getDomain();
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
		double denominator = 1 + c;
		
		// Return acceleration
		return numerator / denominator;
	}
	
	/**Returns the kinetic energy for a given velocity v*/
	public double getKineticEnergy(double v) {
		return 0.5*m*v*v  +  0.5*m*c*v*v;
   	}
	
	/**Returns the potential energy for a given value of x*/
	public double getPotentialEnergy(double x) {
		return m * Trace.G * func.eval(x);
	}
	
	/**Returns the total energy for a given velocity v and value of x*/
	public double getTotalEnergy(double v, double x) {
		return getKineticEnergy(v) + getPotentialEnergy(x);
	}
	
	
	//Traces
	/**Prints a trace of the experiment described by this Trace object using Eulers' Method twice*/
	public void eulerTrace(double step) {
		// Set initial parameters
		int iter = 0;
		double x = x0, v = v0, a = getAccel(x);	
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
//			System.out.println(String.format("%d\t\ta = %.8f\t\t v = %.8f\t\t x = %.8f\t\t eKin = %.8f\t\t ePot = %.8f\t\t eTot = %.8f\t\t", iter, a, v, x, eKin, ePot, eTot).replace(',', '.'));
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
		PolySpline polySpline = Interpolation.polynomialSplineInterpolation("C:\\Users\\Patrik\\git\\Patrik-Forked\\Physics\\src\\imports\\mass_A.txt");
		double minX = polySpline.getDomain()[0];	
		double m = 10000;							//Mass of rolling object
		double c = Trace.SPHERE;					//Constant for calculating moment of inertia
		double v0 = 0;								//Initial velocity
		double x0 = minX;							//Initial x-coordinate
		double step = 0.000001;						//Step size for numerical integration (Eulers' method)
		
		//Initialize new experiment
		Trace experiment = new Trace(polySpline, m, c, v0, x0);
		
		//Print a trace using Eulers' method
		experiment.eulerTrace(step);
		
	}
	
}
