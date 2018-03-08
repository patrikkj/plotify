package app;

import com.jfoenix.controls.JFXListCell;

public class CustomJFXListCell extends JFXListCell<Trace> {
	public CustomJFXListCell() {
		
		textProperty().bind(itemProperty().get().getNameProperty());
	}
	
}
