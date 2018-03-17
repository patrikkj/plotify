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
	// File parsing
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
	
	
	// Array reduction
	/**
	 * Converts an array to a reduced array by picking n evenly indexed elements.
	 */
	public static double[] reduceArray(double[] inputArr, int n) {
		// Verify that reduction is necessary
		if (n >= inputArr.length)
			return inputArr;
		
		double[] output = new double[n];
		double step = ((double) inputArr.length - 1d)  /  ((double) n - 1d);
		
		// Fill output
		for (int i = 0; i < n; i++)
				output[i] = inputArr[(int) Math.round(step * (double) i)];
		
		return output;
	}
	
	/**
	 * Reduces array using an array of integers.
	 */
	public static double[] reduceArray(double[] inputArr, int[] indices) {
		// Verify that reduction is necessary / possible
		if (indices[indices.length - 1] >= inputArr.length)
			return inputArr;
		
		double[] output = new double[indices.length];
		
		// Fill list
		for (int i = 0; i < indices.length; i++)
				output[i] = inputArr[indices[i]];
		
		return output;
	}
	
	/**
	 * Reduces array using given node distribution and size.
	 */
	public static double[] reduceArray(double[] inputArr, Nodes nodes, int n) {
		// Verify that reduction is necessary
		if (n >= inputArr.length)
			return inputArr;		
		
		int[] indices = new int[n];
		double[] output = new double[n];
		
		switch (nodes) {
		case UNIFORM:
			indices = uniformIndices(inputArr, n);
			break;
		case EQUIDISTANT:
			indices = equidistantIndices(inputArr, n);
			break;
		case CHEBYSHEV:
			indices = chebyshevIndices(inputArr, n);
			break;
		}
		
		// Fill output
		for (int i = 0; i < indices.length; i++)
				output[i] = inputArr[indices[i]];
		
		return output;
	}
	
	
	// List reduction
	/**
	 * Reduces a list using an array of integers.
	 */
	public static List<Double> reduceList(List<Double> inputList, int[] indices) {
		// Verify that reduction is necessary / possible
		if (indices[indices.length - 1] >= inputList.size())
			return inputList;
		
		List<Double> output = new ArrayList<>();
		
		// Fill list
		for (int i = 0; i < indices.length; i++)
				output.add(inputList.get(indices[i]));
		
		return output;
	}
	
	
	// Node distribution
	/**
	 * Returns n uniformy distributed indices from input array.
	 */
	public static int[] uniformIndices(double[] inputArr, int n) {
		int[] indices = new int[n];
		
		// Calculate step size
		double step = ((double) inputArr.length - 1d)  /  ((double) n - 1d);
		
		// Fill list
		for (int i = 0; i < n; i++)
				indices[i] = (int) Math.round(step * (double) i);
		
		return indices;
	}
	
	/**
	 * Returns the indices for n equidistant elements from input array.
	 */
	public static int[] equidistantIndices(double[] inputArr, int n) {
		int[] indices = new int[n];
		
		// Endpoints
		double a = inputArr[0];
		double b = inputArr[inputArr.length - 1];
		double step = (b - a) / ((double) n - 1d);
		
		// Create array of equidistant values
		double[] equidistantValues = new double[n];
		for (int k = 0; k < n; k++)
			equidistantValues[k] = a + k * step;
		
		// Loop through input array to find index
		int currentIndex = 0;
		double currentValue = equidistantValues[currentIndex];

		for (int i = 0; i < inputArr.length; i++) {
			if (inputArr[i] >= currentValue) {
				indices[currentIndex++] = i;
				
				// Break if finished
				if (currentIndex == n) break;
				
				// Update search value
				currentValue = equidistantValues[currentIndex];
			}
		}
		
		// Return reduced list
		return indices;
	}
	
	/**
	 * Returns the indices for n Chebyshev nodes from input array. Recommended for Polynomial interpolation.
	 * Elements must be in strictly increasing order.
	 */
	public static int[] chebyshevIndices(double[] inputArr, int n) {
		int[] indices = new int[n];
		
		// Endpoints
		double a = inputArr[0];
		double b = inputArr[inputArr.length - 1];
		
		// Create list of actual chebyshev values
		double[] chebyshevValues = new double[n];
		for (int k = 1; k <= n; k++)
			chebyshevValues[k - 1] = (1d/2d)*(a+b) + (1d/2d)*(a-b) * Math.cos((((double)(2*k - 1d)) / (2*n)) * Math.PI);
		
		// Loop through input array to find index
		int currentIndex = 0;
		double currentValue = chebyshevValues[currentIndex];

		for (int i = 0; i < inputArr.length; i++) {
			if (inputArr[i] >= currentValue) {
				indices[currentIndex++] = i;
				
				// Break if finished
				if (currentIndex == n) break;
				
				// Update search value
				currentValue = chebyshevValues[currentIndex];
			}
		}
		
		return indices;
	}
}


/**
 * Enumeration for available node distributions.
 */
enum Nodes {
	UNIFORM, EQUIDISTANT, CHEBYSHEV
}