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
		int[] indecies = chebyshevIndexies(x, 10);
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
		double[][] fileData = parseFile(file);
		
		//Return coefficient array from interpolation
		return polynomialInterpolation(fileData[1], fileData[2]);
	}
	
	
	/*
	 * Parses tracking data from filepath to three arrays of doubles.
	 * Format: Trackers' default export (.txt)
	 * Output [double[] t, double[] x, double[] y]:
	 *  - double[] x: array of time value in strictly increasing order
	 *  - double[] x: array of x coordinates in strictly increasing order
	 *  - double[] y: array of y coordinates at x coordinate specified in fist array
	 */
	public static double[][] parseFile(File file)  {
		//Local variables
		Scanner fileScanner = null;
		StringBuilder stringBuilder = new StringBuilder();

		//Try reading file
		try { 
			fileScanner = new Scanner(new FileReader(file));
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Read file to StringBuilder object
		while (fileScanner.hasNextLine())
			stringBuilder.append(fileScanner.nextLine() + "\n");
		fileScanner.close();
		
		//Convert StringBuilder object to string
		String fileString = stringBuilder.toString();
		
		//Convert dataset to array of strings
		String[] fileArray = fileString.split("\n");
		
		//Initialize output arrays
		List<Double> outputT = new ArrayList<Double>();
		List<Double> outputX = new ArrayList<Double>();
		List<Double> outputY = new ArrayList<Double>();
		
		//Trim each string and split on any type of whitespace
		for (int i = 2; i < fileArray.length; i++) {
			String[] lineArray = fileArray[i].trim().split("\\s++");
			
			//Append values to output arrays
			outputT.add(Double.parseDouble(lineArray[0].replace(',', '.')));
			outputX.add(Double.parseDouble(lineArray[1].replace(',', '.')));
			outputY.add(Double.parseDouble(lineArray[2].replace(',', '.')));
		}
		
		//Map to primitive type
		
		double[] primitiveT = outputT.stream().mapToDouble(doub -> doub.doubleValue()).toArray();
		double[] primitiveX = outputX.stream().mapToDouble(doub -> doub.doubleValue()).toArray();
		double[] primitiveY = outputY.stream().mapToDouble(doub -> doub.doubleValue()).toArray();
		
		return new double[][] { primitiveT, primitiveX, primitiveY };
	}
	
	/**
	 * Converts an array to a reduced array by picking n evenly indexed elements.
	 */
	private static double[] reduceArray(double[] inputArr, int n) {
		chebyshevIndexies(inputArr, n);
		
		// Verify that reduction is necessary
		if (n >= inputArr.length)
			return inputArr;
		
		// Create output list
		double[] outputArr = new double[n];
		
		// Calculate step size
		double step = ((double) inputArr.length - 1d)  /  ((double) n - 1d);
		
		// Fill list
		for (int i = 0; i < n; i++)
				outputArr[i] = inputArr[(int) Math.round(step * (double) i)];
		
		// Return reduced list
		return outputArr;
	}
	
	/**
	 * Reduces array using an array of integers.
	 */
	private static double[] reduceArray(double[] inputArr, int[] indecies) {
		// Create output list
		double[] outputArr = new double[indecies.length];
		
		// Fill list
		for (int i = 0; i < indecies.length; i++)
				outputArr[i] = inputArr[indecies[i]];
		
		// Return reduced list
		System.out.println(outputArr.length);
		return outputArr;
	}
	
	
	
	/**
	 * Returns an array containing the indecies for n equidistant elements in input array.
	 */
	private static double[] equidistantIndecies(double[] inputArr, int n) {
		// Verify that reduction is necessary
		if (n >= inputArr.length)
			return inputArr;
		
		// Create output list
		double[] outputArr = new double[n];
		
		// Calculate step size
		double firstVal = inputArr[0];
		double lastVal = inputArr[inputArr.length - 1];
		double step = (lastVal - firstVal) / ((double) n - 1d);
		
//		// Fill list
//		for (int i = 0; i < n; i++)
//			for (double d : inputArr)
//			outputArr[i] = Arrays.stream(inputArr).filter(elem -> (elem >= step * i)).findFirst().getAsDouble();
		
		// Return reduced list
		System.out.println(outputArr.length);
		return outputArr;
	}
	
	/**
	 * Returns a list of Chebyshev node indecies for the given array. To be used in Polynomial interpolation.
	 * Elements must be in strictly increasing order.
	 */
	private static int[] chebyshevIndexies(double[] inputArr, int n) {
		// Output array
		int[] indecies = new int[n];
		
		// Endpoints
		double a = inputArr[0];
		double b = inputArr[inputArr.length - 1];
		
		// Create list of actual chebyshev values
		double[] chebyshevValues = new double[n];
		for (int k = 1; k <= n; k++)
			chebyshevValues[k - 1] = (1d/2d)*(a+b) + (1d/2d)*(a-b) * Math.cos((((double)(2*k - 1d)) / (2*n)) * Math.PI);
		
		// Loop for index generation
		for (int i = 0; i < n; i++) {
			// Value to find in input array
			double currentVal = chebyshevValues[i];
			
			// Loop through input array to find index
			for (int j = 0; j < inputArr.length; j++) {
				if (inputArr[j] >= currentVal) {
					indecies[i] = j;
					break;
				}
			}
		}
		
		return indecies;
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
