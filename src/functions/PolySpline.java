package functions;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import parsers.Polynomial;


public class PolySpline extends AbstractDifferentiable {
	private PolynomialSplineFunction polySpline;
	private double[] domain;
	
	
	/**
	 * Constructs a PolySpline representing the given Polynomial Spline Function.
	 * @see analysis.Interpolation
	 */
	public PolySpline(PolynomialSplineFunction polySpline) {
		this.polySpline = polySpline;
		
		// Set domain restrictions
		double[] xValues = polySpline.getKnots();
		domain = new double[] { xValues[0], xValues[xValues.length - 1] };
		
	}
	
	
	/*
	 * Returns the function value of the spline polynomial at given point
	 */
	@Override
	public double eval(double x) {		
		return polySpline.value(x);
	}

	/*
	 * Returns a PolySpline object representing the first derivative of this function
	 */
	@Override
	public PolySpline derivative() {
		return new PolySpline(polySpline.polynomialSplineDerivative());
	}

	/**Returns the domain of this polynomial spline function*/
	@Override
	public double[] getDomain() {
		return domain;
	}
	
	/*
	 * Returns a string representing the spline polynomial described by this object
	 */
	@Override
	public String toString() {
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
			stringBuilder.append(Polynomial.toString(correctArray, true, true) + "\n");
		}
		
		//Return concatinated StringBuilder
		return stringBuilder.toString();
	}
}
