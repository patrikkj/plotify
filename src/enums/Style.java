package enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Style {
	// Object types
	FULL_LINE	("-fx-dot-style", "――――――――――"),
	PADDED_LINE	("-fx-dot-style", "----------"),
	DOTTED_LINE	("-fx-dot-style", "··········");
	
	// Constants
	public final String STYLE;
	public final String DISPLAY;
	
	// Constructor
	private Style(String STYLE, String DISPLAY) {
		this.STYLE = STYLE;
		this.DISPLAY = DISPLAY;
	}

	// List getters
	public static List<Style> getElements() {
		return Arrays.stream(values()).collect(Collectors.toList());
	}
	
	// toString - Values to be displayed in ComboBox
	@Override
	public String toString() {
		return DISPLAY;
	}
}