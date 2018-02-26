package functions;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import parsers.Polynomial;


public class PolySpline implements Differentiable {
	//Instance variables
	private PolynomialSplineFunction polySpline;
	private double[] domain;
	
	
	//Constructor
	public PolySpline(PolynomialSplineFunction polySpline) {
		this.polySpline = polySpline;
		
		// Set domain restrictions
		double[] xValues = polySpline.getKnots();
		domain = new double[] { xValues[0], xValues[xValues.length - 1] };
		
	}
	
	
	//Evaluations
	/*
	 * Returns the function value of the spline polynomial at given point
	 */
	public double eval(double x) {		
		return polySpline.value(x);
	}
	
	/*
	 * Returns the function value of the spline polynomials' first derivative at given point
	 */
	public double evalDerivative(double x) {
		return polySpline.polynomialSplineDerivative().value(x);
	}
	
	/*
	 * Returns the function value of the spline polynomials' second derivative at given point
	 */
	public double evalDerivativeII(double x) {
		return polySpline.polynomialSplineDerivative().polynomialSplineDerivative().value(x);
	}
	
	
	//Derivatives
	/*
	 * Returns a PolySpline object representing the first derivative of this function
	 */
	public PolySpline derivative() {
		return new PolySpline(polySpline.polynomialSplineDerivative());
	}
	
	/*
	 * Returns a PolySpline object representing the second derivative of this function
	 */
	public PolySpline derivativeII() {
		return derivative().derivative();
	}

	
	//Slope angle and curvature
	/*
	 * Returns the slope angle at given point, in radians
	 * Slope angle is positive for a curve with a negative derivative
	 */
	public double slopeAngle(double x) {
		return Math.atan(-evalDerivative(x));
	}
	
	/*
	 * Returns the slope angle at given point, in degrees
	 * Slope angle is positive for a curve with a negative derivative
	 */
	public double slopeAngleDegrees(double x) {
		//Compute angle in radians
		double radians = slopeAngle(x);
		
		//Return converted angle
		return Math.toDegrees(radians);
	}
	
	/*
	 * Returns the radius of the osculating circle describing the curvature at a given point
	 * The sign of the radius of the osculating circle, is the same as that of the second derivative
	 * https://en.wikipedia.org/wiki/Radius_of_curvature
	 */
	public double radiusOfCurvature(double x) {
		double dy_dx_1 = evalDerivative(x);
		double dy_dx_2 = evalDerivativeII(x);
		
		//Prevent division by zero (y'' = 0)
		if (dy_dx_2 == 0)
			throw new ArithmeticException("Radius of curvature is only defined for polynomials of degree 2 and higher.");
		
		return Math.pow((1 + Math.pow(dy_dx_1, 2)), 3/2) / dy_dx_2;
	}
	
	
	//Others
	/**Returns the domain of this polynomial spline function*/
	public double[] getDomain() {
		return domain;
	}
	
	/*
	 * Returns a string representing the spline polynomial described by this object
	 */
	public String toString(boolean includeZeroCoeffs, boolean addPadding) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (PolynomialFunction polyFunc : polySpline.getPolynomials()) {
			double[] coeffArray = polyFunc.getCoefficients();
			
			//Convert coefficient array to list in order to reverse
			ArrayList<Double> coeffList = new ArrayList<Double>();
			for (double doub : coeffArray) coeffList.add(doub);
			
			//Reverse list order
			Collections.reverse(coeffList);
			
			//Coefficients as a primitive array
			double[] correctArray = coeffList.stream().mapToDouble(doub -> doub.doubleValue()).toArray();
			
			//Append parsed string to StringBuilder object
			stringBuilder.append(Polynomial.toString(correctArray, includeZeroCoeffs, addPadding) + "\n");
		}
		
		//Return concatinated StringBuilder
		return stringBuilder.toString();
	}
}
