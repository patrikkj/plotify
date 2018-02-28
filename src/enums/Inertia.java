package enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Inertia {
	// Object types
	POINT_OF_MASS	(0, 0d, "Point of mass"),
	DISC_SOLID		(1, 1d/2d, "Disc (Solid)"),
	DISC_HOLLOW		(2, 1d/1d, "Disc (Hollow)"),
	SPHERE_SOLID	(3, 2d/5d, "Sphere (Solid)"),
	SPHERE_HOLLOW	(4, 2d/3d, "Sphere (Hollow)"),
	CYLINDER_SOLID	(5, 1d/2d, "Cylinder (Solid)"),
	CYLINDER_HOLLOW	(6, 1d/1d, "Cylinder (Hollow)");
	
	// Constants
	public final int ID;
	public final Double VALUE;
	public final String TEXT;
	
	// Constructor
	private Inertia(int ID, Double VALUE, String TEXT) {
		this.ID = ID;
		this.VALUE = VALUE;
		this.TEXT = TEXT;
	}

	// ID Getters
	public static int toID(String TEXT) {
		for (Inertia inertia : Inertia.values())
			if (inertia.TEXT == TEXT) return inertia.ID;
		
		throw new IllegalArgumentException(String.format("Inertia enum with TEXT: \"%s\" does not exist.", TEXT));
	}
	public static int toID(Double VALUE) {
		for (Inertia inertia : Inertia.values())
			if (inertia.VALUE == VALUE) return inertia.ID;
		
		throw new IllegalArgumentException(String.format("Inertia enum with VALUE: \"%s\" does not exist.", VALUE));
	}
	
	// VALUE Getters
	public static Double toValue(int ID) {
		for (Inertia inertia : Inertia.values())
			if (inertia.ID == ID) return inertia.VALUE;
		
		throw new IllegalArgumentException(String.format("Inertia enum with ID: \"%s\" does not exist.", ID));
	}
	public static Double toValue(String TEXT) {
		for (Inertia inertia : Inertia.values())
			if (inertia.TEXT == TEXT) return inertia.VALUE;
		
		throw new IllegalArgumentException(String.format("Inertia enum with TEXT: \"%s\" does not exist.", TEXT));
	}
	
	// TEXT Getters
	public static String toText(int ID) {
		for (Inertia inertia : Inertia.values())
			if (inertia.ID == ID) return inertia.TEXT;
		
		throw new IllegalArgumentException(String.format("Inertia enum with ID: \"%s\" does not exist.", ID));
	}
	public static String toText(Double VALUE) {
		for (Inertia inertia : Inertia.values())
			if (inertia.VALUE == VALUE) return inertia.TEXT;
		
		throw new IllegalArgumentException(String.format("Inertia enum with VALUE: \"%s\" does not exist.", VALUE));
	}

	// List getters
	public static List<Double> getValues() {
		return Arrays.stream(values()).map(inertia -> inertia.VALUE).collect(Collectors.toList());
	}
	public static List<String> getTextValues() {
		return Arrays.stream(values()).map(inertia -> inertia.TEXT).collect(Collectors.toList());
	}
}