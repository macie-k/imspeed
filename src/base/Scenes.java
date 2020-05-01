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
	
	static Text pointsVal = new Text("0");
	static Text missedVal = new Text("0");
	static Text CPM = new Text("0");
		
	static String Color_RED = "#FF554D";
	static String Color_ORANGE = "#FCA103";
	static String Color_YELLOW = "#F0FC03";
	static String Color_GREEN = "#7FFC03";
	static String Color_GAY_GRADIENT = "-fx-fill: linear-gradient(to right, #FF3030, #FF6A00, #FFF200, #4AFF59, #00FF7B, #34ABEB, #A200FF, #FF36AB);";
	static String Color_GOLD_GRADIENT = "-fx-fill: linear-gradient(#FFA200, #FFD500);";
	
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
			Window.BAK_DIR = fontsPath; fontsPath += "fonts" + Window.slash;
			
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
		Text pkts = new Text(pointsVal.getText()); pkts.setFill(Color.web("#FF554D")); /*pkts.setFill(Color.RED);*/pkts.setTranslateX(t.getTranslateX() + (305+pointslen*36)/2); pkts.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 32;");
		
		stack.getChildren().addAll(t, pkts);
		stack.setTranslateY(200);
		root.getChildren().addAll(background, stack);
				
		return scene;
	}
	
	public static Scene game(Pane root) {
		
		Scene scene = new Scene(root, Window.BACKGROUND);
		
		StackPane topStack = new StackPane();
		StackPane bottomStack = new StackPane();
				
		Rectangle bottomLine = new Rectangle();
		bottomLine.setWidth(800); bottomLine.setHeight(5);
		bottomLine.setTranslateX(0); bottomLine.setTranslateY(400);
		bottomLine.setFill(Color.web("#131313"));
		
		Rectangle background = new Rectangle();
		background.setWidth(800); background.setHeight(500);
		background.setTranslateX(0); background.setTranslateY(0);
		background.setFill(Window.BACKGROUND);
				
		int pointsLen = String.valueOf(Math.round(Window.points)).length()+5;
		pointsVal.setFill(Color.web(Color_GREEN)); pointsVal.setTranslateX(50+10*pointsLen); pointsVal.setFont(Font.font("Courier new", 17));
		
		Text pointsText = new Text("Points: "); pointsText.setFill(Color.WHITE); pointsText.setTranslateX(30); pointsText.setFont(Font.font("Courier new", 17));
		Text signL = new Text("["); signL.setFill(Color.WHITE); signL.setTranslateX(27); signL.setFont(Font.font("Courier new", 17));
		Text signR = new Text("]"); signR.setFill(Color.WHITE); signR.setTranslateX(27+120+5); signR.setFont(Font.font("Courier new", 17));
		
		Text missedText = new Text("Missed: "); missedText.setFill(Color.WHITE); missedText.setTranslateX(230); missedText.setFont(Font.font("Courier new", 17));
		missedVal.setFill(Color.web(Color_RED)); missedVal.setTranslateX(310); missedVal.setFont(Font.font("Courier new", 17));
		
		Text CPMText = new Text("CPM: "); CPMText.setFill(Color.WHITE); CPMText.setTranslateX(230); CPMText.setFont(Font.font("Courier new", 17));
		CPM.setFill(Color.web(Color_YELLOW)); CPM.setTranslateX(280); CPM.setFont(Font.font("Courier new", 17));
		
		input.setPrefWidth(120); input.setPrefHeight(20); input.setMaxWidth(120); input.setMaxHeight(20); input.setTranslateX(35);
		input.setStyle("-fx-faint-focus-color: transparent;"
				+ "-fx-focus-color: transparent;"
				+ "-fx-text-box-border: transparent;"
				+ "-fx-background-color: #0e0e0e;"
				+ "-fx-text-fill: #FFF;"
				+ "-fx-highlight-fill: #FFF;"
				+ "-fx-highlight-text-fill: #000;"
				+ "-fx-cursor: block;"
				+ "-fx-font-family: 'Courier new', monospace;");
		
		topStack.setTranslateX(0); topStack.setTranslateY(425);
		topStack.setMaxWidth(800); topStack.setPrefWidth(800);
		topStack.getChildren().addAll(pointsText, pointsVal, missedText, missedVal);
		
//		List<Node> topNodes = topStack.getChildren();
//		alignNodes(topNodes, Pos.CENTER_LEFT);
		
		bottomStack.setTranslateX(0); bottomStack.setTranslateY(455);
		bottomStack.setMaxWidth(800); bottomStack.setPrefWidth(800);
		bottomStack.getChildren().addAll(input, signL, signR, CPMText, CPM);
	
//		List<Node> bottomNodes = bottomStack.getChildren();
//		alignNodes(bottomNodes, Pos.CENTER_LEFT);
		
		bottomStack.setAlignment(Pos.CENTER_LEFT);
		topStack.setAlignment(Pos.CENTER_LEFT);
				
		root.getChildren().addAll(background, bottomLine, topStack, bottomStack);
	
		return scene;
	}
		
}