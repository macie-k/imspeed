package base;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import menu.Selection;


public class Window extends Application {
		
	private Pane root = new Pane();
	
	@Override
	public void start(Stage stage) throws Exception {	
		root.setPrefSize(800, 600);
		Scene scene = new Scene(root, Color.web("#0f0f0f"));
		
		Selection.select(scene, root, stage);	// run menu scene
		
		stage.setTitle("I'm speed");
		stage.setResizable(false);
		stage.show();
	}
		
	public static void main (String[] args) {
		launch(args);
	}

}
