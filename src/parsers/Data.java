package parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class for manipulating data collections.
 */
public class Data {

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
	
	
	// Node patterns
	
	
	
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
	private static int[] chebyshevIndecies(double[] inputArr, int n) {
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

	
}
