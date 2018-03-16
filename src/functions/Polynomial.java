package functions;

public class Polynomial extends AbstractDifferentiable {
	private double[] coeffArray;
	private double[] domain;
	
	
	/**
	 * Creates a Polynomial object described by the input coefficient array.
	 * NOTE: Coefficcents must be given in descending order of degrees
	 */
	public Polynomial(double[] coeffArray, double[] domain) {
		this.coeffArray = coeffArray;
		this.domain = domain;
	}
	
	/**
	 * Evaluates polyonmial represented by an array of coefficients, using Horner's method
	 */
	@Override
	public double eval(double x) {
		double result = coeffArray[0];
		
		for (int i = 0; i < coeffArray.length - 1; i++)
			result = (result * x) + coeffArray[i + 1];
		
		return result;
	}
	
	/**
	 * Returns a Polynomial object, representing the first derivative of given polynomial
	 */
	@Override
	public Polynomial derivative() {
		double[] derivativeArray = new double[coeffArray.length - 1];
		
		//Multiply coefficients with corresponding degree
		for (int i = 0; i < derivativeArray.length; i++)
			derivativeArray[i] = coeffArray[i] * (coeffArray.length - i - 1);
		
		return new Polynomial(derivativeArray, domain);
	}

	/**
	 * Returns the domain of this polynomial
	 */
	@Override
	public double[] getDomain() {
		return domain;
	}
	
	/**
	 * Returns a string representation of Polynomial
	 */
	@Override
	public String toString() {
		return parsers.Polynomial.toString(coeffArray, true, true);
	}
}
