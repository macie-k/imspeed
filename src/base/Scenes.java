package base;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Scenes {
	
	static AnimationTimer timer;
	static Text points = new Text();
	public static TextField input = new TextField();
		
	public static Scene gameOver(Pane root) {
		
		Font.loadFont("https://kazmierczyk.me/styles/fonts/Kyrou%207%20Wide%20Bold.ttf", 14);
		Scene scene = new Scene(root, Window.BACKGROUND);
		StackPane stack = new StackPane();
		
		Rectangle background = new Rectangle();
		background.setWidth(800); background.setHeight(500);
		background.setTranslateX(0); background.setTranslateY(0);
		background.setFill(Window.BACKGROUND);
		
		int pointslen = String.valueOf(Math.round(Window.points)).length();
		Text t = new Text("Your score: "); t.setFill(Color.WHITE); t.setTranslateX(240-pointslen*12); t.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 30;");
		Text pkts = new Text(points.getText()); pkts.setFill(Color.web("#ff4a80")); /*pkts.setFill(Color.RED);*/pkts.setTranslateX(400+pointslen); pkts.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 32;");
		
		stack.getChildren().addAll(t, pkts);
		stack.setTranslateY(200);
		root.getChildren().addAll(background, stack);
				
		return scene;
	}
	
	public static Scene game(Pane root) {
		
		Scene scene = new Scene(root, Window.BACKGROUND);
		StackPane inputArea = new StackPane();
				
		Rectangle bottom = new Rectangle();
		bottom.setWidth(800); bottom.setHeight(10);
		bottom.setTranslateX(0); bottom.setTranslateY(400);
		bottom.setFill(Color.web("#151515"));
		
		Rectangle background = new Rectangle();
		background.setWidth(800); background.setHeight(500);
		background.setTranslateX(0); background.setTranslateY(0);
		background.setFill(Window.BACKGROUND);
		
		int pointslen = String.valueOf(Math.round(Window.points)).length()+5;
		points.setFill(Color.web("#ff4a80")); points.setTranslateX(442+10*pointslen); points.setFont(Font.font("Courier new", 17));
		
		Text pkts = new Text("Points: "); pkts.setFill(Color.WHITE); pkts.setTranslateX(400); pkts.setFont(Font.font("Courier new", 17));
		Text l = new Text(">"); l.setFill(Color.WHITE); l.setTranslateX(-70); l.setFont(Font.font("Courier new"));

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
		StackPane.setAlignment(points, Pos.CENTER_LEFT);
		inputArea.getChildren().addAll(input, l, pkts, points);
				
		root.getChildren().addAll(background, bottom, inputArea);
	
		return scene;
	}

}