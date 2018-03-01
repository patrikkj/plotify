package enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Integration {
	// Integration types
	EULER_METHOD			(0, "Eulers method"),
	EULER_IMPROVED_METHOD	(1, "Eulers improved method"),
	RUNGE_KUTTA_METHOD		(2, "Runge-Kutta method");
	
	// Constants
	public final int ID;
	public final String TEXT;
	
	// Constructor
	private Integration(int ID, String TEXT) {
		this.ID = ID;
		this.TEXT = TEXT;
	}
	
	// ID Getters
	public static int toID(String TEXT) {
		for (Integration integration : Integration.values())
			if (integration.TEXT == TEXT) return integration.ID;
		
		throw new IllegalArgumentException(String.format("Integration enum with TEXT: \"%s\" does not exist.", TEXT));
	}
	
	// TEXT Getters
	public static String toText(int ID) {
		for (Integration integration : Integration.values())
			if (integration.ID == ID) return integration.TEXT;
		
		throw new IllegalArgumentException(String.format("Integration enum with ID: \"%s\" does not exist.", ID));
	}

	// List getters
	public static List<String> getTextValues() {
		return Arrays.stream(values()).map(integration -> integration.TEXT).collect(Collectors.toList());
	}
	public static List<Integration> getElements() {
		return Arrays.stream(values()).collect(Collectors.toList());
	}
	
	// toString - Values to be displayed in ComboBox
	public String toString() {
		return TEXT;
	}
}