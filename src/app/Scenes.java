package app;

import static app.Utils.blinkingNodeTimer;
import static app.Utils.createText;
import static app.Utils.showScoreboardPage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import app.obj.KeyButton;
import app.obj.ScoreboardEntry;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import menu.Select;
import menu.Words;
import menu.obj.Option;
import menu.obj.ScaleBox;

public class Scenes {

	public final static String FONT_TITLE = "Grixel Kyrou 7 Wide Bold";
	public final static String FONT_TEXT = "Courier new";
	
	static Text pointsVal = new Text("0");
	static Text conditionVal = new Text();	// create empty text and assign value later based on gamemode
	static Text CPM = new Text("0");
	static final StackPane pauseBox = new StackPane();

	public static final TextField input = new TextField();
	public static final Text pointer = createText(">", Color.WHITE, FONT_TEXT, 15);
	public static final Option[] gamemodes = {
			new Option(250, "Normal", 40, true),
			new Option(290, "Marathon", 40, false)
	};
	
	public static ScaleBox[][] scales;
	public static Option[] difficulties = new Option[5];
	public static String[] loadedDifficulties = Words.loadDifficulties();
	public static Option[] lngs;
	public static String fontsPath;
	
	private static ArrayList<ScoreboardEntry> entries;
	private static int toDeleteIndex = 0;

	@SuppressWarnings("serial")
	private static Map<String, String> columns = new LinkedHashMap<String, String>() {{
		put("Score", "DESC");
		put("CPM", "DESC");
		put("Difficulty", "ASC");
		put("Language", "ASC");
		put("Gamemode", "ASC");
		put("DateTime", "DESC");
		put("Name", "ASC");
	}};
	
	private static String[] columnsKeys = {"Score", "CPM", "Difficulty", "Language", "Gamemode", "DateTime", "Name"};
	private static int columnsIndex = 0;
	
	private static int currentPage = 1;
	private static boolean deletable = false;
	static boolean saved;
	
	/* fallback for default call, if called from main menu 'saved' is set as true */
	public static Scene scoreboard() {
		return scoreboard(false);
	}
	
	public static Scene scoreboard(boolean _saved) {
		
		boolean newScore = false;	// flag for asking for user's name
		saved = _saved;
				
		Pane root = new Pane();
			root.setPrefSize(800, 500);
			root.setStyle("-fx-background-color: #0E0E0E");
		
		/* text with error information when SQLException occurs */
		Text errorMsg = createText("Could not get any scores", Colors.RED_C, FONT_TEXT, 15);
			errorMsg.setTranslateX(292);
			errorMsg.setTranslateY(80);
			errorMsg.setVisible(false);
			
		/* title text */
		Text title = createText("SCOREBOARD", Color.WHITE, FONT_TITLE, 26);
			title.setTranslateX(257);
			
		/* stackpane with input box asking for name */
		Node[] nameInputRes = Utils.inputBox("NAME");
		StackPane nameInput = (StackPane) nameInputRes [0];
				nameInput.setTranslateX((800 - nameInput.getPrefWidth())/2);
				nameInput.setTranslateY((500 - nameInput.getPrefHeight())/2);
				nameInput.setVisible(false);	
		final TextField input = (TextField) nameInputRes[1];
			
		Pane resultsContainer = new Pane();
			resultsContainer.setTranslateX(25);
			resultsContainer.setTranslateY(85);
			resultsContainer.setPrefSize(752, 354);
			resultsContainer.setStyle("-fx-border-color: white;");
			
		Text newScoreText = createText("New score - Press enter to save", Colors.GREEN_C, FONT_TEXT, 15);
			newScoreText.setTranslateX(261);
			newScoreText.setTranslateY(75);
			
		AnimationTimer newScoreTimer = blinkingNodeTimer(newScoreText, 750_000_000);
			
		StackPane pageStack = new StackPane();
			pageStack.setTranslateY(460);
			pageStack.setTranslateX(325);
			pageStack.setPrefSize(150, 25);
			
		Text pagePrev = createText("<", Color.WHITE, FONT_TEXT, 16);
			pagePrev.setTranslateX(-30);
			
		Text pageNext = createText(">", Color.WHITE, FONT_TEXT, 16);
			pageNext.setTranslateX(30);
			
		Text pageNumber = createText("1", Color.WHITE, FONT_TEXT, 16);

		pageStack.getChildren().addAll(pagePrev, pageNumber, pageNext);	
			
		entries = Utils.getRows("Score", "DESC");
		for(ScoreboardEntry e : entries) {
			if(e.isActive()) {
				newScore = true;
				break;
			}
		}
		
		/* create 1st entry as table headers */
		final ScoreboardEntry headers = new ScoreboardEntry(1, columnsKeys);
		resultsContainer.getChildren().add(headers);
	
		if(entries.size() == 0)  {
			errorMsg.setVisible(true);	// show error message
		}
		
		final ScoreboardEntry activeEntry = ScoreboardEntry.activeEntry;
		final int pages = (int) Math.ceil(entries.size()/15.0);
		
		currentPage = activeEntry != null ? ScoreboardEntry.activeEntry.getEntryPage() : 1;
		pagePrev.setVisible(currentPage != 1);
		pageNext.setVisible(currentPage != pages && pages != 1);
		
		newScoreText.setVisible(newScore);
		if(newScore) newScoreTimer.start();
		title.setTranslateY(newScore ? 49 : 54);
		
		showScoreboardPage(currentPage, resultsContainer, entries, pageNumber);
		
		root.getChildren().addAll(resultsContainer, title, errorMsg, pageStack, newScoreText, nameInput);
		root.setId(String.valueOf(newScore));	// store information if there is new score, so it can be accessed inside lambda

		Scene scene = new Scene(root);
						
		
		Text sortText = createText("SORT BY: " + columnsKeys[columnsIndex], Color.WHITE, FONT_TEXT, 14);
			sortText.setTranslateX(85);
			sortText.setTranslateY(460);
			
		Text deleteText = createText("DELETE", Color.WHITE, FONT_TEXT, 14);
			deleteText.setTranslateX(85);
			deleteText.setTranslateY(485);
			
		KeyButton sortButton = new KeyButton(scene, root, KeyCode.TAB, "TAB", 25, 447, 50, 20, 14, 8, new ButtonAction() {
			@Override
			public void callback(Pane root, boolean active) {
				columnsIndex = (columnsIndex + 1 == columnsKeys.length) ? 0 : columnsIndex+1;
				final String column = columnsKeys[columnsIndex];
				final String order = columns.get(column);
				entries = Utils.getRows(column, order);
				ScoreboardEntry.colorSwitch = false;
				
				showScoreboardPage(currentPage, resultsContainer, entries, pageNumber);
				sortText.setText("SORT BY: " + column);
			}
		});		
		
		KeyButton deleteButton = new KeyButton(scene, root, KeyCode.DELETE, "DEL", 25, 472, 50, 20, 14, 8, new ButtonAction() {
			@Override
			public void callback(Pane root, boolean active) {
				if(saved) {
					if(!deletable) {
						deletable = true;
						deleteText.setText("ESC TO CANCEL");
						setToDeleteEntry(0, resultsContainer, pageNumber);
					}
				} else {
					deleteText.setText("SAVE FIRST");
				}
			}
		});	
		root.getChildren().addAll(sortButton, sortText);
		root.getChildren().addAll(deleteButton, deleteText);
		
		/* animation timer with floating particles */
		AnimationTimer animation_bg = Utils.getBackgroundTimer(790, 590, root, resultsContainer, newScoreText, nameInput, sortButton, sortText);
			animation_bg.start();
			
			
		scene.setOnKeyPressed(e -> {
			
			switch(e.getCode()) {
				/* show next page if possible */
				case DOWN:
					if(deletable) {
						if(++toDeleteIndex == entries.size()) toDeleteIndex = 0;
						setToDeleteEntry(toDeleteIndex, resultsContainer, pageNumber);
					}
					
				break;
				
				case UP:
					if(deletable) {
						if(--toDeleteIndex < 0) toDeleteIndex = entries.size()-1;
						setToDeleteEntry(toDeleteIndex, resultsContainer, pageNumber);
					}
				break;
				
				case RIGHT:
					if(currentPage + 1 <= pages) {
						showScoreboardPage(++currentPage, resultsContainer, entries, pageNumber);
					}
				break;
				
				/* show previous page if possible */
				case LEFT:
					if(currentPage - 1 >= 1) {
						showScoreboardPage(--currentPage, resultsContainer, entries, pageNumber);
					}
				break;
				
				case ESCAPE:
					/* if input prompt is active, close it */
					if(deletable) {
						deletable = false;
						deleteText.setText("DELETE");
						setToDeleteEntry(-1, resultsContainer, pageNumber);
					} else {
						if(nameInput.isVisible()) {
							nameInput.setVisible(false);
							newScoreTimer.start();
							root.setId("true");
						} else {
							/* else check if record should be removed */
							if(Boolean.valueOf(root.getId())) {
								Utils.removeRecord(ScoreboardEntry.activeEntry.getDate());
							}
							/* and return to selection scene */
							ScoreboardEntry.activeEntry = null;
							Select.selectGamemode();
						}
					}					
					break;
					
				case ENTER:
					/* if there is a new score */
					if(Boolean.valueOf(root.getId())){
						newScoreTimer.stop();			// stop new score animation
						newScoreText.setVisible(false);	// and hide it
						
						nameInput.setVisible(true);		// show name prompt
						root.setId("false");			// update flag about new score
					} 
					
					else if (nameInput.isVisible() && input.getText().trim().length() > 0) { // if prompt is active and is not empty save the name
						final String name = input.getText();
						if(Utils.setScoreName(name, activeEntry.getDate())) {
							ScoreboardEntry.activeEntry.setName(name);		// update visible user's name
							saved = true;
							deleteText.setText("DELETE");
						} else {
							/* if error occurs display information */
							errorMsg.setText("Could not save name");
							errorMsg.setVisible(true);
						}
						nameInput.setVisible(false);	// hide name prompt
					}
					
					else if(deletable) {
						deletable = false;
						Utils.removeRecord(ScoreboardEntry.toDeleteEntry.getDate());
						deleteText.setText("DELETE");
						setToDeleteEntry(-1, resultsContainer, pageNumber);
						ScoreboardEntry.toDeleteEntry = null;
						entries = Utils.getRows("Score", "DESC");
						showScoreboardPage(currentPage, resultsContainer, entries, pageNumber);
					}
				break;
						
				default: break;
			}
			/* hide/show navigation arrows */
			pagePrev.setVisible(currentPage != 1);
			pageNext.setVisible(currentPage != pages);
		});
			
		return scene;
	}
	
	private static void setToDeleteEntry(int index, Pane resultsContainer, Text pageNumber) {
		entries.forEach(entry -> entry.setToDelete(false));
		if(index != -1) {
			entries.get(index).setToDelete(true);
		}
		showScoreboardPage(currentPage, resultsContainer, entries, pageNumber);
	}

			
	public static Pane selectMenu(String type) {
		Pane root = new Pane();
			root.setPrefSize(800, 500);
			root.setStyle("-fx-background-color: rgb(14, 14, 14)");
	
		Text header = createText(type, Color.WHITE, FONT_TITLE, 50);
			header.setTranslateX((800 - header.getLayoutBounds().getWidth())/2);
			header.setTranslateY(130);
		
		root.getChildren().add(header);
		
		switch(type) {
			case "GAMEMODE":
				root.getChildren().addAll(gamemodes);
				break;
				
			case "DIFFICULTY":
				for(int i=0; i<5; i++) {
					difficulties[i] = new Option((i==4) ? 345 : 220 + 25*i, loadedDifficulties[i], i==0);	// list all difficulties with one empty line for 'Custom'
				}
				root.getChildren().addAll(difficulties);
				break;
				
			case "CUSTOM":
				Text subHeader = createText("DIFFICULTY", Color.WHITE,FONT_TITLE, 18);
					subHeader.setTranslateX((800 - subHeader.getLayoutBounds().getWidth())/2);
					subHeader.setTranslateY(120 + subHeader.getLayoutBounds().getHeight());
					
				int startY = Window.gameMode == 0 ? 260 : 245;
				StackPane sPaneText = new StackPane();
					sPaneText.setTranslateX(245);
					sPaneText.setTranslateY(startY);
					sPaneText.setAlignment(Pos.CENTER_LEFT);
					
				StackPane sPaneScales = new StackPane();
					sPaneScales.setTranslateX(385);
					sPaneScales.setTranslateY(startY);
					sPaneScales.setAlignment(Pos.CENTER_LEFT);
					
				pointer.setTranslateX(-30);
				
				Text howFast = createText("Speed", Color.WHITE, FONT_TEXT, 16);
				
				Text howOften = createText("Frequency", Color.WHITE, FONT_TEXT, 16);
					howOften.setTranslateY(30);

				Text howMany = createText("Amount", Color.WHITE, FONT_TEXT, 16);
					howMany.setTranslateY(60);
					
				Text startTime = createText(Window.gameMode == 0 ? "" : "Start time", Color.WHITE, FONT_TEXT, 16);	
					startTime.setTranslateY(90);
										
				scales = new ScaleBox[Window.gameMode == 0 ? 3 : 4][10];
				for(int i=0; i<scales.length; i++) {
					for(int j=0; j<10; j++) {
						int x = j*17; int y = i*30;
						scales[i][j] = new ScaleBox(x, y);	// 
						sPaneScales.getChildren().add(scales[i][j]);
					}
				}
				
				
				sPaneText.getChildren().addAll(pointer, howFast, howOften, howMany, startTime);
				root.getChildren().addAll(subHeader, sPaneText, sPaneScales);
				break;
				
			case "LANGUAGES":
				String[][] loadedLanguages = Words.loadLanguages();
				lngs = new Option[Words.how_many_lngs];	// needs to be defined here because before 'Words.loadLanguages()' is called 'how_many_lngs' will be 0
				
				/* load all available languages */
				for(int i=0; i<Words.how_many_lngs; i++) {
					lngs[i] = new Option(220 + 25*i, loadedLanguages[i][0], loadedLanguages[i][1], i==0);
				}
				root.getChildren().addAll(lngs);
				break;
		}
		return root;	// return pane with selected elements
	}
	
	public static Pane gameOver() {
		
		Pane root = new Pane();
			root.setPrefSize(800, 500);
			root.setStyle("-fx-background: " + Colors.BACKGROUND);
			
		StackPane stack = new StackPane();
			stack.setTranslateY(200);
			
		Rectangle background = new Rectangle(800, 500);
			background.setTranslateX(0);
			background.setTranslateY(0);
			background.setFill(Colors.BACKGROUND_C);
				
		int pointsLen = String.valueOf(Math.round(Window.points)).length();		// calculation needed for good placement of the points
		Text pointsText = new Text("Your score: ");
			pointsText.setFont(Font.font(FONT_TITLE, 30));
			pointsText.setFill(Color.WHITE);
			pointsText.setTranslateX(400-(300+pointsLen*36)/2);
			
		Text pointsVal = new Text(Scenes.pointsVal.getText());
			pointsVal.setFont(Font.font(FONT_TITLE, 32));
			pointsVal.setFill(Color.web("#FF554D"));
			pointsVal.setTranslateX(pointsText.getTranslateX() + (305+pointsLen*36)/2);
		
		stack.getChildren().addAll(pointsText, pointsVal);
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
			background.setFill(Colors.BACKGROUND_C);	
			
		int pointsLen = String.valueOf(Math.round(Window.points)).length()+5;
				
		Text pointsText = new Text("Points: ");
			pointsText.setFill(Color.WHITE);
			pointsText.setTranslateX(30);
			pointsText.setFont(Font.font(FONT_TEXT, 17));
			
			pointsVal.setFill(Colors.GREEN_C);
			pointsVal.setTranslateX(50+10*pointsLen);
			pointsVal.setFont(Font.font(FONT_TEXT, 17));
		
		Text conditionText = new Text(Window.gameMode == 0 ? "Missed: " : "Time left: ");	// if default gamemode, set "Missed" else "Time left" for marathon mode
			conditionText.setFill(Color.WHITE);
			conditionText.setTranslateX(230);
			conditionText.setFont(Font.font(FONT_TEXT, 17));
			
			conditionVal.setFill(Colors.RED_C);
			conditionVal.setTranslateX(conditionText.getTranslateX() + conditionText.getLayoutBounds().getWidth());
			conditionVal.setFont(Font.font(FONT_TEXT, 17));
		
		Text CPMText = new Text("CPM: ");
			CPMText.setFill(Color.WHITE);
			CPMText.setTranslateX(230);
			CPMText.setFont(Font.font(FONT_TEXT, 17));
			
			CPM.setFill(Colors.YELLOW_C);
			CPM.setTranslateX(280);
			CPM.setFont(Font.font(FONT_TEXT, 17));
			
		Text signL = new Text("[");
			signL.setFill(Color.WHITE);
			signL.setTranslateX(27);
			signL.setFont(Font.font(FONT_TEXT, 17));
		
		Text signR = new Text("]");
			signR.setFill(Color.WHITE);
			signR.setTranslateX(27+120+5);
			signR.setFont(Font.font(FONT_TEXT, 17));
			
		Text pause = new Text("PAUSE");
			pause.setFill(Colors.RED_C);
			pause.setFont(Font.font(FONT_TEXT, 25));
			
		Rectangle pauseBg = new Rectangle(100, 40);
			pauseBg.setFill(Colors.BACKGROUND_C);
		
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
		input.setOnKeyTyped(e -> {
			final int maxCharacters = 30;
	        if(input.getText().length() > maxCharacters) {
	        	e.consume();
	        }
		});
		
		StackPane topStack = new StackPane();
			topStack.setTranslateX(0);
			topStack.setTranslateY(425);
			topStack.setMaxWidth(800);
			topStack.setPrefWidth(800);
		topStack.getChildren().addAll(pointsText, pointsVal, conditionText, conditionVal);
				
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
			error.setFont(Font.font(FONT_TEXT, 25));
			
		Text errorMsg = new Text();
			errorMsg.setWrappingWidth(350);
			errorMsg.setFill(Color.WHITE);
			errorMsg.setFont(Font.font(FONT_TEXT, 20));
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
		
	public static void fontSetup() {
		/* list of required font names */
		String[] fontNames = {
				"Grixel Kyrou 7 Wide Bold.ttf",
				"Courier New.ttf",
				"Courier New Bold.ttf"
		};
		
		/* load each font */
		for(String font : fontNames) {
			try {
				Font.loadFont(Scenes.class.getResourceAsStream("/resources/fonts/" + font), 20);
			} catch (Exception e) {
				Log.error(String.format("Unable to load font {%s}: {%s}", font, e));
			}
		}		
	}	
}