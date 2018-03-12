package enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Style {
	// Object types
	FULL_LINE		(10d, 0d), 	// ------------------------------------------- //
	PADDED_LINE1	(9d, 6d), 	// ----------     ----------     ----------    //
	PADDED_LINE2	(4d, 5d),	// -----    -----    -----    -----    -----   //
	DOTTED_LINE1	(1d, 8d),	// -    -    -    -    -    -    -    -    -   //
	DOTTED_LINE2	(1d, 5d);	// -  -  -  -  -  -  -  -  -  -  -  -  -  -  - //
	
	// Constants
	public final Double[] stroke;
	
	// Constructor
	private Style(Double... stroke) {
		this.stroke = stroke;
	}
	
	public Double[] getStroke() {
		return stroke;
	}
	
	// List getters
	public static List<Style> getElements() {
		return Arrays.stream(values()).collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", stroke[0].intValue(), stroke[1].intValue());
	}
}

