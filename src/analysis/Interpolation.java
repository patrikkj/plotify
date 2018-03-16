package analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

import functions.PolySpline;
import functions.Polynomial;
import parsers.Data;


public class Interpolation {
	
	/*
	 * Returns a PolySpline object representing a set of third degree polynomials,
	 * replicating a best-fit curve to the given set of coordinates.
	 * Input parameters:
	 *  - double[] x: array of x coordinates in strictly increasing order
	 *  - double[] y: array of y coordinates at x coordinate specified in fist array
	 */
	public static PolySpline polynomialSplineInterpolation(double[] x, double[] y) {
		//Validate array lengths
		if (x.length != y.length)
			throw new IllegalArgumentException("Arrays of x and y coordinates must be of equal length.");
		
		//Validate x coordinate array
		for (int i = 0; i < x.length - 1; i++)
			if (x[i] >= x[i+1])
				throw new IllegalArgumentException("Values in array of x coordinates must be strictly increasing.");
		
//		//Reduce array
//		x = reduceArray(x, 8);
//		y = reduceArray(y, 8);
//		
		
		//Perform interpolation
		return new PolySpline(new SplineInterpolator().interpolate(x, y));
	}
	
	/*
	 * Returns a PolySpline object representing a set of third degree polynomials,
	 * replicating a best-fit curve to the given set of coordinates.
	 * Input parameters:
	 *  - String filepath: Absolute filepath to standard Tracker export file (.txt)
	 */
	public static PolySpline polynomialSplineInterpolation(File file) {
		//Parse tracking data from filepath to two arrays of doubles.
		double[][] fileData = parseFile(file);

		//Perform interpolation
		return polynomialSplineInterpolation(fileData[1], fileData[2]);
	}
	
	
	/*
	 * Returns an array of coefficients, corresponding to an N-1 degree polynomial interpolation.
	 * NOTE: Arrays of x and y coordinates must be of equal length
	 * Input parameters:
	 *  - double[] x: array of x coordinates in strictly increasing order
	 *  - double[] y: array of y coordinates at x coordinate specified in fist array
	 */
	public static Polynomial polynomialInterpolation(double[] x, double[] y) {
		//Validate array lengths
		if (x.length != y.length)
			throw new IllegalArgumentException("Arrays of x and y coordinates must be of equal length.");
		
		//Validate x coordinate array
		for (int i = 0; i < x.length - 1; i++)
			if (x[i] >= x[i+1])
				throw new IllegalArgumentException("Values in array of x coordinates must be strictly increasing.");
		
		//Reduced indecies
		int[] indecies = chebyshevIndecies(x, 10);
		//Reduce arrays
		double[] xReduced = reduceArray(x, indecies);
		double[] yReduced = reduceArray(y, indecies);
		
		//Perform interpolation
		PolynomialFunctionLagrangeForm rawPolynomial = new PolynomialFunctionLagrangeForm(xReduced, yReduced);
		
		//Get disordered coefficients from polynomial
		double[] coeffArrayDisordered = rawPolynomial.getCoefficients();
		
		//Convert coefficient array to list in order to reverse
		ArrayList<Double> coeffList = new ArrayList<Double>();
		for (double doub : coeffArrayDisordered) coeffList.add(doub);
		
		//Reverse list order
		Collections.reverse(coeffList);
		
		//Represent coefficients as a primitive array
		double[] coeffArray =  coeffList.stream().mapToDouble(doub -> doub.doubleValue()).toArray();
		
		//Get function domain
		double[] domain = new double[] { x[0], x[x.length - 1] };
		
		//Return a Polynomail function
		return new Polynomial(coeffArray, domain);
	}
	
	/*
	 * Returns an array of coefficients, corresponding to an N-1 degree polynomial interpolation.
	 * NOTE: Arrays of x and y coordinates must be of equal length
	 * Input parameters:
	 *  - String filepath: Absolute filepath to standard Tracker export file (.txt)
	 */
	public static Polynomial polynomialInterpolation(File file) {
		//Parse tracking data from filepath to two arrays of doubles.
		double[][] fileData = Data.parseFile(file);
		
		//Return coefficient array from interpolation
		return polynomialInterpolation(fileData[1], fileData[2]);
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		PolySpline polySpline = polynomialSplineInterpolation(new File("C:\\Users\\Patrik\\git\\Patrik-Forked\\Physics Plotter\\src\\imports\\mass_A.txt"));
		
		for (double x = 0.132; x < 1.44; x += 0.001) {
			System.out.printf("x: %.3f \t\t y: %s\n", x, polySpline.eval(x));
		}
		
		System.out.println(polySpline.toString());
		System.out.println(polySpline.derivative().toString());
		
	}
}
