package enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Style {
	// Object types
	FULL_LINE	("-fx-dot-style", "――――――――――", "Full line"),
	PADDED_LINE	("-fx-dot-style", "----------", "Dashed line"),
	DOTTED_LINE	("-fx-dot-style", "··········", "Dotted line");
	
	// Constants
	public final String STYLE;
	public final String DISPLAY;
	public final String TEXT;
	
	// Constructor
	private Style(String STYLE, String DISPLAY, String TEXT) {
		this.STYLE = STYLE;
		this.DISPLAY = DISPLAY;
		this.TEXT = TEXT;
	}

	// List getters
	public static List<Style> getElements() {
		return Arrays.stream(values()).collect(Collectors.toList());
	}
	
	// toString - Values to be displayed in ComboBox
	@Override
	public String toString() {
		return TEXT;
	}
}