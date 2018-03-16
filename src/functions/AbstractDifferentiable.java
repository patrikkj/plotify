package functions;

public abstract class AbstractDifferentiable {
	//Evaluation
	/**
	 * Evaluates function at given x-value.
	 */
	public abstract double eval(double x);
	
	/**
	 * Evaluates first derivative of function at given x-value
	 */
	public double evalDerivative(double x) {
		return derivative().eval(x);
	}
	
	/**
	 * Evaluates second derivative of function at given x-value
	 */
	public double evalDerivativeII(double x) {
		return derivativeII().eval(x);
	}
	
	
	//Differentiation
	/**
	 * Returns an instance of object class, representing the first derivative of this function.
	 */
	public abstract AbstractDifferentiable derivative();
	
	/**
	 * Returns an instance of object class, representing the second derivative of this function.
	 */
	public AbstractDifferentiable derivativeII() {
		return derivative().derivative();
	}
	
	
	//Slope angle and radius of curvature
	/**
	 * Returns the slope angle at given point, in radians
	 * Slope angle is positive for a curve with a negative derivative
	 */
	public double slopeAngle(double x) {
		return Math.atan(-evalDerivative(x));
	}
	
	/**
	 * Returns the slope angle at given point, in degrees
	 * Slope angle is positive for a curve with a negative derivative
	 */
	public double slopeAngleDegrees(double x) {
		//Compute angle in radians
		double radians = slopeAngle(x);
		
		//Return converted angle
		return Math.toDegrees(radians);
	}
	
	/**
	 * Returns the radius of the osculating circle describing the curvature at a given point
	 * The sign of the radius of the osculating circle, is the same as that of the second derivative
	 * https://en.wikipedia.org/wiki/Radius_of_curvature
	 */
	public double radiusOfCurvature(double x) {
		double dy_dx_1 = evalDerivative(x);
		double dy_dx_2 = evalDerivativeII(x);
//		
//		//Prevent division by zero (y'' = 0)
//		if (dy_dx_2 == 0) {
//			System.out.println("Division by zero for x = " + x);
//			return Double.POSITIVE_INFINITY;
//		}
//			throw new ArithmeticException("Radius of curvature is only defined for polynomials of degree 2 and higher.");
		
		return Math.pow((1 + Math.pow(dy_dx_1, 2)), 3/2) / dy_dx_2;
	}
	
	
	//Other
	/**
	 * Returns an array on the form [minX, maxX] representing the domain of this function.
	 */
	public abstract double[] getDomain();
	
	/**
	 * Returns a String representation of this function.
	 */
	public abstract String toString();
}
