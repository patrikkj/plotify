package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
	private double xOffset;
	private double yOffset;
	
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		Scene scene = new Scene(root);
//		Image titleIcon = new Image(getClass().getResourceAsStream("../icons/main/main_dark.png"));

		//Transparency settings
		scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
		
		//Set mouse pressed
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               xOffset = event.getSceneX();
               yOffset = event.getSceneY();
           }});
        
        //Set mouse drag
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if (yOffset < 80) {
	                primaryStage.setX(event.getScreenX() - xOffset);
	                primaryStage.setY(event.getScreenY() - yOffset);
            	}
            }});
        
		// Initialize application window
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setScene(scene);
		primaryStage.setTitle("PattyPlot");
//		primaryStage.getIcons().add(titleIcon);
		primaryStage.show();
	}

	public static void main(String[] args) throws IOException {
		Runtime.getRuntime().exec("REG ADD \"HKCU\\Software\\Microsoft\\Windows NT\\CurrentVersion\\AppCompatFlags\\Layers\" /V %cd%\\WorkstationInstaller.exe /T REG_SZ /D HIGHDPIAWARE /F");
		launch(args);
	}
}
