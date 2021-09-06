package clipping.application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Aplicación creada para crear una imagen con clipping
 * 
 * @author Xeronulis
 *
 */

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/clipping/views/MainScene.fxml"));
			Scene scene = new Scene(root);
			
			primaryStage.getIcons().add(new Image("/clipping/icon/icon.png"));
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		launch(args);
	}
}
