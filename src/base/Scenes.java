package base;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Scenes {
	
	static Text points = new Text();
	public static TextField input = new TextField();
	
	public static Scene game(Pane root) {
		
		Scene scene = new Scene(root, Color.web("#0f0f0f"));
		StackPane inputArea = new StackPane();
				
		Rectangle bottom = new Rectangle();
		bottom.setWidth(800); bottom.setHeight(10);
		bottom.setTranslateX(0); bottom.setTranslateY(400);
		bottom.setFill(Color.web("#151515"));
		
		Rectangle background = new Rectangle();
		background.setWidth(800); background.setHeight(500);
		background.setTranslateX(0); background.setTranslateY(0);
		background.setFill(Color.web("#0f0f0f"));
		
		points.setFill(Color.web("#ff4a80")); points.setTranslateX(450); points.setFont(Font.font("Courier new")); points.setStyle("-fx-font-size: 17;");
		
		Text pkts = new Text("Points: "); pkts.setFill(Color.WHITE); pkts.setTranslateX(400); pkts.setFont(Font.font("Courier new")); pkts.setStyle("-fx-font-size: 17;");
		Text l = new Text(">"); l.setFill(Color.WHITE); l.setTranslateX(-70); l.setFont(Font.font("Courier new"));
		//Label r = new Label("<"); r.setTextFill(Color.WHITE); r.setTranslateX(70);

		input.setPrefWidth(120); input.setPrefHeight(20); input.setMaxWidth(120); input.setMaxHeight(20);
		input.setStyle("-fx-faint-focus-color: transparent;"
				+ "-fx-focus-color: transparent;"
				+ "-fx-text-box-border: transparent;"
				+ "-fx-background-color: #151515;"
				+ "-fx-text-fill: #FFF;"
				+ "-fx-highlight-fill: #FFF;"
				+ "-fx-highlight-text-fill: #000;"
				+ "-fx-cursor: block;"
				+ "-fx-font-family: 'Courier new', monospace;");
		
		inputArea.setTranslateX(20); inputArea.setTranslateY(440);
		inputArea.setMaxWidth(140); inputArea.setPrefWidth(140);
		inputArea.getChildren().addAll(input, l, pkts, points);
				
		root.getChildren().addAll(background, bottom, inputArea);
	
		return scene;
	}

/*	public static Scene gameOver(Pane root) {
		
	}*/
}
