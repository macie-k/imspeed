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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import menu.Words;
import menu.obj.Option;
import menu.obj.ScaleBox;

public class Scenes {

	static AnimationTimer timer;
	
	static Text pointsVal = new Text("0");
	static Text missedVal = new Text("0");
	static Text CPM = new Text("0");
	static final StackPane pauseBox = new StackPane();
		
	public static final String COLOR_RED = "#FF554D";
	public static final String COLOR_ORANGE = "#FCA103";
	public static final String COLOR_YELLOW = "#F0FC03";
	public static final String COLOR_GREEN = "#7FFC03";
	public static final String COLOR_GAY_GRADIENT = "-fx-fill: linear-gradient(to right, #FF3030, #FF6A00, #FFF200, #4AFF59, #00FF7B, #34ABEB, #A200FF, #FF36AB);";
	public static final String COLOR_GOLD_GRADIENT = "-fx-fill: linear-gradient(#FFA200, #FFD500);";
	
	public static final TextField input = new TextField();
	public static final Text pointer = createText(">", Color.WHITE, "Courier new", 15);
	public static final ScaleBox[][] scales = new ScaleBox[3][10];
	
	public static String fontsPath;
	
	public static Option[] lngs;
	public static Option[] diffs = new Option[5];;
	public static String[] loadedDifficulties = Words.loadDifficulties();
	
		
	public static Pane selectMenu(String type) {
		Pane root = new Pane();
			root.setPrefSize(800, 500);
			root.setStyle("-fx-background-color: rgb(14, 14, 14)");
	
		Text header = createText(type, Color.WHITE,"Grixel Kyrou 7 Wide Bold", 50);
			header.setTranslateX((800 - header.getLayoutBounds().getWidth())/2);
			header.setTranslateY(130);
		
		root.getChildren().add(header);
		
		switch(type) {
			case "CUSTOM":
				Text subHeader = createText("DIFFICULTY", Color.WHITE,"Grixel Kyrou 7 Wide Bold", 18);
					subHeader.setTranslateX((800 - subHeader.getLayoutBounds().getWidth())/2);
					subHeader.setTranslateY(120 + subHeader.getLayoutBounds().getHeight());
			
				StackPane sPaneText = new StackPane();
					sPaneText.setTranslateX(245);
					sPaneText.setTranslateY(260);
					sPaneText.setAlignment(Pos.CENTER_LEFT);
					
				StackPane sPaneScales = new StackPane();
					sPaneScales.setTranslateX(385);
					sPaneScales.setTranslateY(260);
					sPaneScales.setAlignment(Pos.CENTER_LEFT);
					
				pointer.setTranslateX(-30);
				
				Text howFast = createText("Speed", Color.WHITE, "Courier new", 16);
				
				Text howOften = createText("Frequency", Color.WHITE, "Courier new", 16);
					howOften.setTranslateY(30);

				Text howMany = createText("Amount", Color.WHITE, "Courier new", 16);
					howMany.setTranslateY(60);
					
				for(int i=0; i<3; i++) {
					for(int j=0; j<10; j++) {
						int x = j*17; int y = i*30;
						scales[i][j] = new ScaleBox(x, y);	// 
						sPaneScales.getChildren().add(scales[i][j]);
					}
				}
					
				sPaneText.getChildren().addAll(pointer, howFast, howOften, howMany);
				root.getChildren().addAll(subHeader, sPaneText, sPaneScales);
				break;
				
			case "LANGUAGES":
				String[][] loadedLanguages = Words.loadLanguages();
				lngs = new Option[Words.how_many_lngs];	// needs to be defined later because before 'Words.loadLanguages()' is called 'how_many_lngs' will be 0
				
				/* load all available languages */
				for(int i=0; i<Words.how_many_lngs; i++) {
					lngs[i] = new Option(220 + 25*i, loadedLanguages[i][0], loadedLanguages[i][1], i==0);
				}
				root.getChildren().addAll(lngs);
				break;
				
			case "DIFFICULTY":
				for(int i=0; i<5; i++) {
					diffs[i] = new Option((i==4) ? 345 : 220 + 25*i, loadedDifficulties[i], i==0);	// list all difficulties with one empty line for 'Custom'
				}
				root.getChildren().addAll(diffs);
				break;
		}
			
		return root;
	}
	
	public static Pane gameOver() {
		
		Pane root = new Pane();
			root.setPrefSize(800, 500);
			
		StackPane stack = new StackPane();
		
		Rectangle background = new Rectangle(800, 500);
			background.setTranslateX(0);
			background.setTranslateY(0);
			background.setFill(Window.BACKGROUND);
				
		int pointsLen = String.valueOf(Math.round(Window.points)).length();		// calculation needed for good placement of the points
		Text pointsText = new Text("Your score: ");
			pointsText.setFont(Font.font("Grixel Kyrou 7 Wide Bold", 30));
			pointsText.setFill(Color.WHITE);
			pointsText.setTranslateX(400-(300+pointsLen*36)/2);
			
		Text pointsVal = new Text(Scenes.pointsVal.getText());
			pointsVal.setFont(Font.font("Grixel Kyrou 7 Wide Bold", 32));
			pointsVal.setFill(Color.web("#FF554D"));
			pointsVal.setTranslateX(pointsText.getTranslateX() + (305+pointsLen*36)/2);
		
		stack.getChildren().addAll(pointsText, pointsVal);
		stack.setTranslateY(200);
		root.getChildren().addAll(background, stack);
					
		return root;
	}
	
	public static Pane game() {
		
		Pane root = new Pane();
			root.setPrefSize(800, 500);
				
		Rectangle bottomLine = new Rectangle(800, 5);
			bottomLine.setTranslateX(0); bottomLine.setTranslateY(400);
			bottomLine.setFill(Color.web("#131313"));
		
		Rectangle background = new Rectangle(800, 500);
			background.setTranslateX(0); background.setTranslateY(0);
			background.setFill(Window.BACKGROUND);	
			
		int pointsLen = String.valueOf(Math.round(Window.points)).length()+5;
				
		Text pointsText = new Text("Points: ");
			pointsText.setFill(Color.WHITE);
			pointsText.setTranslateX(30);
			pointsText.setFont(Font.font("Courier new", 17));
			
			pointsVal.setFill(Color.web(COLOR_GREEN));
			pointsVal.setTranslateX(50+10*pointsLen);
			pointsVal.setFont(Font.font("Courier new", 17));
		
		Text missedText = new Text("Missed: ");
			missedText.setFill(Color.WHITE);
			missedText.setTranslateX(230);
			missedText.setFont(Font.font("Courier new", 17));
			
			missedVal.setFill(Color.web(COLOR_RED));
			missedVal.setTranslateX(310);
			missedVal.setFont(Font.font("Courier new", 17));
		
		Text CPMText = new Text("CPM: ");
			CPMText.setFill(Color.WHITE);
			CPMText.setTranslateX(230);
			CPMText.setFont(Font.font("Courier new", 17));
			
			CPM.setFill(Color.web(COLOR_YELLOW));
			CPM.setTranslateX(280);
			CPM.setFont(Font.font("Courier new", 17));
			
		Text signL = new Text("[");
			signL.setFill(Color.WHITE);
			signL.setTranslateX(27);
			signL.setFont(Font.font("Courier new", 17));
		
		Text signR = new Text("]");
			signR.setFill(Color.WHITE);
			signR.setTranslateX(27+120+5);
			signR.setFont(Font.font("Courier new", 17));
			
		Text pause = new Text("PAUSE");
			pause.setFill(Color.web(COLOR_RED));
			pause.setFont(Font.font("Courier new", 25));
			
		Rectangle pauseBg = new Rectangle(100, 40);
			pauseBg.setFill(Window.BACKGROUND);
		
		pauseBox.getChildren().addAll(pauseBg, pause);
			pauseBox.setTranslateX(350);
			pauseBox.setTranslateY(200);
			pauseBox.setVisible(false);
			
		input.setPrefWidth(120);
		input.setPrefHeight(20); 
		input.setMaxWidth(120);
		input.setMaxHeight(20);
		input.setTranslateX(35);
		input.setStyle("-fx-faint-focus-color: transparent;"
				+ "-fx-focus-color: transparent;"
				+ "-fx-text-box-border: transparent;"
				+ "-fx-background-color: #0e0e0e;"
				+ "-fx-text-fill: #FFF;"
				+ "-fx-highlight-fill: #FFF;"
				+ "-fx-highlight-text-fill: #000;"
				+ "-fx-cursor: block;"
				+ "-fx-font-family: 'Courier new', monospace;");
		
		StackPane topStack = new StackPane();
			topStack.setTranslateX(0);
			topStack.setTranslateY(425);
			topStack.setMaxWidth(800);
			topStack.setPrefWidth(800);
		topStack.getChildren().addAll(pointsText, pointsVal, missedText, missedVal);
				
		StackPane bottomStack = new StackPane();
			bottomStack.setTranslateX(0);
			bottomStack.setTranslateY(455);
			bottomStack.setMaxWidth(800);
			bottomStack.setPrefWidth(800);
		bottomStack.getChildren().addAll(input, signL, signR, CPMText, CPM);
		
		bottomStack.setAlignment(Pos.CENTER_LEFT);
		topStack.setAlignment(Pos.CENTER_LEFT);
				
		root.getChildren().addAll(background, bottomLine, topStack, bottomStack, pauseBox);
	
		return root;
	}
		
	public static StackPane error(String err) {
		
		StackPane root = new StackPane();
			root.setPrefSize(800, 500);
			root.setStyle("-fx-background-color: rgb(14, 14, 14)");
					
		Rectangle errorBox = new Rectangle(350, 130);
			errorBox.setFill(Color.web("#0e0e0e"));		
			errorBox.setStyle("-fx-stroke: white; fx-stroke-width: 2");
		
		Label error = new Label(" ERROR ");
			error.setTranslateY(-65);
			error.setStyle("-fx-background-color: #0e0e0e;");
			error.setTextFill(Color.WHITE);	
			error.setFont(Font.font("Courier new", 25));
			
		Text errorMsg = new Text();
			errorMsg.setWrappingWidth(350);
			errorMsg.setFill(Color.WHITE);
			errorMsg.setFont(Font.font("Courier new", 20));
			errorMsg.setTextAlignment(TextAlignment.CENTER);

		switch(err) {
			case "MISSING_WORDS": 
				errorMsg.setText("Missing folder with words");
			break;
			
			case "TEST":
				errorMsg.setText("LONG TEST TEXT TO SEE HOW IT WRAPS");
			break;
			
		}
		
		root.getChildren().addAll(errorBox, errorMsg, error);
		
		return root;
	}
	
	public static Text createText(String value, Color fill, String fontName, int fontSize) {
		Text t = new Text(value);
		t.setFont(Font.font(fontName, fontSize));
		t.setFill(fill);
		
		return t;
	}
	
	/* spaghetti for downloading and loading fonts */
	public static void fontSetup() {
		
		/* fonts to be loaded */
		String[] fontNames = {	"Kyrou 7 Wide Bold.ttf",
								"Courier New.ttf",
								"Courier New Bold.ttf" };	
				
		fontsPath = Window.saveDirectory;
		
		if(!new File(fontsPath).exists() && !new File(fontsPath).mkdir()) {		// if 'imspeed' folder doesn't exist and cannot be created throw an error
			System.err.println("[ERROR] Could not create 'imspeed' directory");
		} else {
			fontsPath += "fonts" + Window.slash;	// add 'fonts' to the path with OS-dependent slash
			
			if(!new File(fontsPath).exists() && !new File(fontsPath).mkdir()) {		// if 'fonts' folder doesn't exist and cannot be created throw an error
				System.err.println("[ERROR] Could not create 'fonts' directory");
			} else {
				
				for(int i=0; i<fontNames.length; i++) {		// iterate all fonts
					if(!new File(fontsPath + fontNames[i]).exists()) {
						try {
							URL font = new URL("https://kazmierczyk.me/--imspeed/" + URLEncoder.encode(fontNames[i], "UTF-8").replace("+", "%20"));		// change encoding for url
							InputStream url = font.openStream();
							Files.copy(url, Paths.get(fontsPath + fontNames[i]));	// download fonts from private hosting and save in dedicated folder
							url.close();
							System.out.println("[OK] Success downloading {" + fontNames[i] + "}");
						} catch (Exception e) {
							System.err.println("[ERROR] Could not download font: " + e);
						}		
					} else {
						System.out.println("[OK] {" + fontNames[i] + "} already downloaded");
					}
				}
			}
		}
		
		/* load every front */
		for(int i=0; i<fontNames.length; i++) {
			try {
				InputStream font = new FileInputStream(fontsPath + fontNames[i]);
				Font.loadFont(font, 15);
			} catch (FileNotFoundException e) {
				System.err.println("[Error] Could not load font file: " + e);
			}
		} System.out.println();
	}
	
}
