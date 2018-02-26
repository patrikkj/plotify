package functions;

public interface Differentiable {
	//Evaluation
	public double eval(double x);
	public double evalDerivative(double x);
	public double evalDerivativeII(double x);
	
	//Differentiation
	public Differentiable derivative();
	public Differentiable derivativeII();
	
	//Slope angle and radius of curvature
	public double slopeAngle(double x);
	public double slopeAngleDegrees(double x);
	public double radiusOfCurvature(double x);
	
	//Other
	public double[] getDomain();
	public String toString(boolean includeZeroCoeffs, boolean addPadding);
}
