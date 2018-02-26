package analysis;

import java.time.LocalTime;

public class Integration {
	//Performs one iteration of Eulers' Method
	public static double eulerMethod(double initValue, double expr, double step) {
		return initValue + expr * step;
	}
	
	public static void main(String[] args) {
		//Initial values
		double iter = 0;
		double x = 0;
		double a = 9.81;
		double v = 0;
		double dt = 0.000000001;
		
		System.out.println(LocalTime.now().toString());
		
		while (x < 100) {
			iter++;
			v = eulerMethod(v, a, dt);
			x = eulerMethod(x, v, dt);
		}
		
		System.out.println(LocalTime.now().toString());
		System.out.println(iter);
		System.out.println("Total time: " + dt*iter);
		
	}
}
