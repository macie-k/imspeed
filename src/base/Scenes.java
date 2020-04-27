package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

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
	public static String fontsPath;
	
	public static void fontSetup() {
		String OS = Window.OS;
		String[] fontNames = {"Kyrou 7 Wide Bold.ttf", "Courier new.ttf"};
		
		if(OS.equals("windows")) {
			fontsPath = System.getenv("appdata") + "\\imspeed\\";
		} else {
			fontsPath = System.getenv("HOME") + "/.imspeed/";
		}
		
		if(!new File(fontsPath).exists() && !new File(fontsPath).mkdir()) {
			System.out.println("Error creating imspeed directory");
		} else {
			Window.HOME_DIR = fontsPath; fontsPath += "fonts" + Window.slash;
			
			if(!new File(fontsPath).exists() && !new File(fontsPath).mkdir()) {
				System.out.println("Error creating font directory");
			} else {
				for(int i=0; i<fontNames.length; i++) {
					if(!new File(fontsPath + fontNames[i]).exists()) {
						try {
							URL font = new URL("https://kazmierczyk.me/--imspeed/" + URLEncoder.encode(fontNames[i], "UTF-8").replace("+", "%20"));
							InputStream url = font.openStream();
							Files.copy(url, Paths.get(fontsPath+fontNames[i]));
							url.close();
						} catch (Exception e) {
							System.out.println("Error downloading font " + e);
						}		
					}
				}
			}
		}
		
		for(int i=0; i<fontNames.length; i++) {
			try {
				InputStream font = new FileInputStream(fontsPath + fontNames[i]);
				Font.loadFont(font, 14);
			} catch (FileNotFoundException e) {
				System.out.println("Error loading font file " + e);
			}
		}
	}
		
	public static Scene gameOver(Pane root) {
		Scene scene = new Scene(root, Window.BACKGROUND);
		StackPane stack = new StackPane();
		
		Rectangle background = new Rectangle();
		background.setWidth(800); background.setHeight(500);
		background.setTranslateX(0); background.setTranslateY(0);
		background.setFill(Window.BACKGROUND);
				
		int pointslen = String.valueOf(Math.round(Window.points)).length();
		Text t = new Text("Your score: "); t.setFill(Color.WHITE); t.setTranslateX(400-(300+pointslen*36)/2); t.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 30;");
		Text pkts = new Text(points.getText()); pkts.setFill(Color.web("#FF554D")); /*pkts.setFill(Color.RED);*/pkts.setTranslateX(t.getTranslateX() + (305+pointslen*36)/2); pkts.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 32;");
		
		stack.getChildren().addAll(t, pkts);
		stack.setTranslateY(200);
		root.getChildren().addAll(background, stack);
				
		return scene;
	}
	
	public static Scene game(Pane root) {
		
		Scene scene = new Scene(root, Window.BACKGROUND);
		StackPane inputArea = new StackPane();
				
		Rectangle bottom = new Rectangle();
		bottom.setWidth(800); bottom.setHeight(5);
		bottom.setTranslateX(0); bottom.setTranslateY(400);
		bottom.setFill(Color.web("#131313"));
		
		Rectangle background = new Rectangle();
		background.setWidth(800); background.setHeight(500);
		background.setTranslateX(0); background.setTranslateY(0);
		background.setFill(Window.BACKGROUND);
		
		int pointslen = String.valueOf(Math.round(Window.points)).length()+5;
		points.setFill(Color.web("#FF554D")); points.setTranslateX(445+10*pointslen); points.setFont(Font.font("Courier new", 17));
		
		Text pkts = new Text("Points: "); pkts.setFill(Color.WHITE); pkts.setTranslateX(400); pkts.setFont(Font.font("Courier new", 17));
		Text l = new Text(">"); l.setFill(Color.WHITE); l.setTranslateX(-70); l.setFont(Font.font("Courier new"));

		input.setPrefWidth(120); input.setPrefHeight(20); input.setMaxWidth(120); input.setMaxHeight(20);
		input.setStyle("-fx-faint-focus-color: transparent;"
				+ "-fx-focus-color: transparent;"
				+ "-fx-text-box-border: transparent;"
				+ "-fx-background-color: #131313;"
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