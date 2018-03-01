package enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Interpolation {
	// Interpolation types
	POLYNOMIAL_SPLINE	(0, "Polynomial Spline"),
	POLYNOMIAL			(1, "Polynomial");
	
	// Constants
	public final int ID;
	public final String TEXT;
	
	// Constructor
	private Interpolation(int ID, String TEXT) {
		this.ID = ID;
		this.TEXT = TEXT;
	}
	
	// ID Getters
	public static int toID(String TEXT) {
		for (Interpolation interpolation : Interpolation.values())
			if (interpolation.TEXT == TEXT) return interpolation.ID;
		
		throw new IllegalArgumentException(String.format("Interpolation enum with TEXT: \"%s\" does not exist.", TEXT));
	}
	
	// TEXT Getters
	public static String toText(int ID) {
		for (Interpolation interpolation : Interpolation.values())
			if (interpolation.ID == ID) return interpolation.TEXT;
		
		throw new IllegalArgumentException(String.format("Interpolation enum with ID: \"%s\" does not exist.", ID));
	}
	
	// List getters
	public static List<String> getTextValues() {
		return Arrays.stream(values()).map(interpolation -> interpolation.TEXT).collect(Collectors.toList());
	}
	public static List<Interpolation> getElements() {
		return Arrays.stream(values()).collect(Collectors.toList());
	}

	// toString - Values to be displayed in ComboBox
	public String toString() {
		return TEXT;
	}
}
