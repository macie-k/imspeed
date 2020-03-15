package base;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import menu.*;

public class Window extends Application {
		
	private static int x=0;
	
	private Pane root = new Pane();
	
	private Languages[] lngs = new Languages[5];
		
	@Override
	public void start(Stage stage) throws Exception {	
		root.setPrefSize(800, 600);
		Scene scene = new Scene(root, Color.web("#0f0f0f"));
		
		for(int i=0; i<5; i++) {
			int calcY = 200 + 20*i;
			lngs[i] = new Languages(calcY, Words.loadWords(x)[i], x==i);
		}
		root.getChildren().addAll(lngs);
		stage.setScene(scene);
		
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
				case UP: if(x>0) x--;
					root.getChildren().removeAll(lngs);
					break;
				case DOWN: if(x<4) x++;
					root.getChildren().removeAll(lngs);
					break;
				default: break;
				
			}
			for(int i=0; i<5; i++) {
				int calcY = 200 + 20*i;
				lngs[i] = new Languages(calcY, Words.loadWords(x)[i], x==i);
			}
			
			root.getChildren().addAll(lngs);
			stage.setScene(scene);
		});
		
		stage.setTitle("I'm speed");
		stage.setResizable(false);
		stage.show();
	}
		
	public static void main (String[] args) {
		launch(args);
	}

}
