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
	private double xOffset, yOffset;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		//Load FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		Image titleIcon = new Image(getClass().getResourceAsStream("../resources/physics_main.png"));
		Parent root = loader.load();
		
		//Controller used for later reference
		MainController controller = loader.getController();

		//Create scene based on FXML file
		Scene scene = new Scene(root);
		
		//Transparency settings
		scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		
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
		primaryStage.setScene(scene);
		primaryStage.setTitle("Wolfram Beta");
		primaryStage.getIcons().add(titleIcon);
		primaryStage.show();
		
		//Set key listener
		controller.postInitialize();
	}
	
	public static void main(String[] args) throws IOException {
		// DPI issues ?? :)
//		Runtime.getRuntime().exec("REG ADD \"HKCU\\Software\\Microsoft\\Windows NT\\CurrentVersion\\AppCompatFlags\\Layers\" /V %cd%\\WorkstationInstaller.exe /T REG_SZ /D HIGHDPIAWARE /F");
		
		// Run application
		launch(args);
	}
}
