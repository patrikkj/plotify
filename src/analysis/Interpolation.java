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
		
		//Perform interpolation
		PolynomialFunctionLagrangeForm rawPolynomial = new PolynomialFunctionLagrangeForm(x, y);
		
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
	
	public static void main(String[] args) throws FileNotFoundException {
		PolySpline polySpline = polynomialSplineInterpolation(new File("C:\\Users\\Patrik\\git\\Patrik-Forked\\Physics Plotter\\src\\imports\\mass_A.txt"));
		
		for (double x = 0.132; x < 1.44; x += 0.001) {
			System.out.printf("x: %.3f \t\t y: %s\n", x, polySpline.eval(x));
		}
		
		System.out.println(polySpline.toString(true, true));
		System.out.println(polySpline.derivative().toString(true, true));
		
	}
}
