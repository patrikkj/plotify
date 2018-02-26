package functions;

public class Polynomial implements Differentiable {
	//Instaice variables
	private double[] coeffArray;
	private double[] domain;
	
	
	//Constructor
	/**
	 * Creates a Polynomial object described by the input coefficient array.
	 * NOTE: Coefficcents must be given in descending order of degrees
	 */
	public Polynomial(double[] coeffArray, double[] domain) {
		this.coeffArray = coeffArray;
		this.domain = domain;
	}
	
	/*
	 * Evaluates polyonmial represented by an array of coefficients, using Horner's method
	 */
	public double eval(double x) {
		double result = coeffArray[0];
		
		for (int i = 0; i < coeffArray.length - 1; i++)
			result = (result * x) + coeffArray[i + 1];
		
		return result;
	}
	
	/*
	 * Evaluates first derivative of polynomial at given x-value
	 */
	public double evalDerivative(double x) {
		return derivative().eval(x);
	}
	
	/*
	 * Evaluates second derivative of polynomial at given x-value
	 */
	public double evalDerivativeII(double x) {		
		return derivativeII().eval(x);
	}
	
	/*
	 * Evaluates nth-derivative of polynomial at given x-value
	 */
	public double evalNthDerivative(double x, int n) {
		return nthDerivative(n).eval(x);
	}
	
	
	/*
	 * Returns a Polynomial object, representing the first derivative of given polynomial
	 */
	public Polynomial derivative() {
		double[] derivativeArray = new double[coeffArray.length - 1];
		
		//Multiply coefficients with corresponding degree
		for (int i = 0; i < derivativeArray.length; i++)
			derivativeArray[i] = coeffArray[i] * (coeffArray.length - i - 1);
		
		return new Polynomial(derivativeArray, domain);
	}
	
	/*
	 * Returns a Polynomial object, representing the second derivative of given polynomial
	 */
	public Polynomial derivativeII() {
		return derivative().derivative();
	}
	
	/*
	 * Returns a Polynomial object, representing the nth-derivative of given polynomial
	 */
	public Polynomial nthDerivative(int n) {
		//Temporary object for storing current polynomial
		Polynomial outputPoly = this;
		
		//Perform n derivations
		for (int i = 0; i < n; i++)
			outputPoly = derivative();
		
		return outputPoly;
	}
	
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

	/**Returns the domain of this polynomial*/
	public double[] getDomain() {
		return domain;
	}
	
	/**Returns a string representation of Polynomial*/
	public String toString(boolean includeZeroCoeffs, boolean addPadding) {
		return parsers.Polynomial.toString(coeffArray, includeZeroCoeffs, addPadding);
	}
		
}
