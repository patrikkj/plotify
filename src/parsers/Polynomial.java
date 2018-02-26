package parsers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Polynomial {
	private static double[] samplePoly1 = new double[] {1, 2, 3, 4};
	private static double[] samplePoly2 = new double[] {-1, -2, -3, -4};
	private static double[] samplePoly3 = new double[] {-1, 2, -3, 4};
	private static double[] samplePoly4 = new double[] {11, 0, 22, 44};
	private static double[] samplePoly5 = new double[] {22, 0, 22, 44, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	
	/*
	 * Evaluates polyonmial represented by an array of coefficients, using Horner's method
	 * NOTE: Coefficcents must be given in descending order of degrees
	 */
	public static double eval(double[] coeffArray, double x) {
		double result = coeffArray[0];
		
		for (int i = 0; i < coeffArray.length - 1; i++)
			result = (result * x) + coeffArray[i + 1];
		
		return result;
	}
	
	/*
	 * Evaluates derivative of polynomial, represented by an array of coefficients
	 * NOTE: Coefficcents must be given in descending order of degrees
	 */
	public static double evalDerivative(double[] coeffArray, double x) {
		//Derivative of given polynomial
		double[] derivativeArray = derivative(coeffArray);
		
		//Derivative evaulated at x
		return eval(derivativeArray, x);
	}
	
	/*
	 * Evaluates nth-derivative of polynomial, represented by an array of coefficients
	 * NOTE: Coefficcents must be given in descending order of degrees
	 */
	public static double evalNthDerivative(double[] coeffArray, double x, int n) {
		//Nth-derivative of given polynomial
		double[] derivativeArray = nthDerivative(coeffArray, n);
		
		//Nth-derivative evaulated at x
		return eval(derivativeArray, x);
	}
	
	/*
	 * Returns an array of coefficients, representing the derivative of given polynomial
	 * NOTE: Coefficcents must be given in descending order of degrees
	 */
	public static double[] derivative(double[] coeffArray) {
		double[] outputArray = new double[coeffArray.length - 1];
		
		//Multiply coefficients with corresponding degree
		for (int i = 0; i < outputArray.length; i++)
			outputArray[i] = coeffArray[i] * (coeffArray.length - i - 1);
		
		return outputArray;
	}
	
	/*
	 * Returns an array of coefficients, representing the nth-derivative of given polynomial
	 * NOTE: Coefficcents must be given in descending order of degrees
	 */
	public static double[] nthDerivative(double[] coeffArray, int n) {
		//Derivative of higher degree than polynomial will always return 0.
		if (n >= coeffArray.length)
			return new double[1];
		
		//Temporary array for storing current polynomial
		double[] outputArray = coeffArray;
		
		//Perform n derivations
		for (int i = 0; i < n; i++)
			outputArray = derivative(outputArray);
		
		return outputArray;
	}
	
	/*
	 * Returns the slope angle at given point, in radians
	 * Slope angle is positive for a curve with a negative derivative
	 */
	public static double slopeAngle(double[] coeffArray, double x) {
		return Math.atan(-evalDerivative(coeffArray, x));
	}
	
	/*
	 * Returns the slope angle at given point, in degrees
	 * Slope angle is positive for a curve with a negative derivative
	 */
	public static double slopeAngleDegrees(double[] coeffArray, double x) {
		//Compute angle in radians
		double radians = slopeAngle(coeffArray, x);
		
		//Return converted angle
		return Math.toDegrees(radians);
	}
	
	/*
	 * Returns the radius of the osculating circle describing the curvature at a given point
	 * The sign of the radius of the osculating circle, is the same as that of the second derivative
	 * https://en.wikipedia.org/wiki/Radius_of_curvature
	 */
	public static double radiusOfCurvature(double[] coeffArray, double x) {
		double dy_dx_1 = evalDerivative(coeffArray, x);
		double dy_dx_2 = evalNthDerivative(coeffArray, x, 2);
		
		//Prevent division by zero (y'' = 0)
		if (dy_dx_2 == 0)
			throw new ArithmeticException("Radius of curvature is only defined for polynomials of degree 2 and higher.");
		
		return Math.pow((1 + Math.pow(dy_dx_1, 2)), 3/2) / dy_dx_2;
	}
	
	
	//Other
	/*
	 * Returns a string representing the polynomial described by the coefficient array
	 */
	public static String toString(double[] coeffArray, boolean includeZeroCoeffs, boolean addPadding) {
		HashMap<Character, String> unicodeMap = new HashMap<Character, String>(buildMap());
		//Degree of polynomial
		int degree = coeffArray.length - 1;
		
		//Polynomial string
		StringBuilder stringBuilder = new StringBuilder();
		
		//Iterate through coefficient array, adding substrings to polynomial string
		for (int i = 0; i < degree; i++) {
			double value = coeffArray[i];
			//Break if element is zero and zero coefficients should be excluded
			if (value == 0  &&  !includeZeroCoeffs) continue;
			
			if (value >= 0) stringBuilder.append('+');
			Locale defaultFormat = Locale.getDefault();
			DecimalFormatSymbols decFormatter = new DecimalFormatSymbols(defaultFormat);
			decFormatter.setDecimalSeparator('.');
			stringBuilder.append(String.format("%.7f", value).replace(',', '.'));
			stringBuilder.append('x'); 
			
			
			//Handle exponents
			for (char c : String.valueOf(degree - i).toCharArray())
				stringBuilder.append(unicodeMap.get(c));
			
			stringBuilder.append('\t'); 

		}
		
		//Handle constant if any
		if (coeffArray[degree] != 0) {
			if (coeffArray[degree] >= 0) 
				stringBuilder.append('+');
//			stringBuilder.append(coeffArray[degree]);
			stringBuilder.append(String.format("%.7f", coeffArray[degree]).replace(',', '.'));
		}
		
		//Remove leading + if first element is positive
		if (stringBuilder.charAt(0) == '+')
			stringBuilder.deleteCharAt(0);
		
		//Convert StringBuilder object to String
		String polyString = stringBuilder.toString();
		
		//Handle coefficient of power 1
		polyString = polyString.replace("x\u00B9 ", "x ")
							   .replace("x\u00B9+", "x+")
							   .replace("x\u00B9-", "x-")
							   .replace("x\u00B9", "x");
		
		//Handle addPadding
		if (addPadding) {
			polyString = polyString.replace("+", " + ");
			polyString = polyString.replace("-", " - ");
			//Fix leading minus spacing
			if (polyString.startsWith(" - "))
				polyString = '-' + polyString.substring(3);

		}
		
		return polyString;
	}

	//Maps characters representing numeric values to unicode superscript characters
	private static Map<Character, String> buildMap() {
		Map<Character,String> map = new HashMap<Character, String>();
		
		map.put('0', "\u2070");
		map.put('1', "\u00B9");
		map.put('2', "\u00B2");
		map.put('3', "\u00B3");
		map.put('4', "\u2074");
		map.put('5', "\u2075");
		map.put('6', "\u2076");
		map.put('7', "\u2077");
		map.put('8', "\u2078");
		map.put('9', "\u2079");
		
		return map;
	}
	
	public static void main(String[] args) {
		System.out.println(toString(samplePoly1, true, true));
		System.out.println(toString(samplePoly2, true, true));
		System.out.println(toString(samplePoly3, true, true));
		System.out.println(toString(samplePoly4, true, true));
		System.out.println(toString(samplePoly5, false, true));
	}
}
