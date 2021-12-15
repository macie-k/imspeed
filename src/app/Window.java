package app;

import static app.Utils.blinkingNodeTimer;
import static app.Utils.createWord;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.obj.CurtainBlock;
import app.obj.ScoreboardEntry;
import app.obj.Word;
import javafx.scene.shape.Rectangle;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import menu.Select;
import menu.Words;

public class Window extends Application {
	
	public static Stage window;
	public static long howOften, howFast;
	public static int maxWords, howMany;
	public static int timeLeft;
	public static double multiplierAdd;
	public static List<Integer> xVal, yVal;
	
	static double points;
	static final List<Integer> CPMs = new ArrayList<Integer>();	// list of all registered CPMs [for average calculating]
	static int avgCPM;	// average CPM (for saving)
	static double totalSeconds;
	
	private static AnimationTimer animation_words, animation_background, animation_gameover, animation_curtain, game_timer;
	private static boolean curtain, pause = false;
	private static int typedWords, typedChars, maxWordLen = 0;
	private static double multiplier;
	private static long startTime, pauseTime;
	
	public static int gameDifficulty;
	public static int gameMode;
	public static boolean infinite = false;
	
	public static void launcher(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {	
		window = primaryStage;
		window.getIcons().add(new Image("/resources/img/icon.jpg"));

		Scenes.fontSetup();
		Select.selectGamemode();
						
		window.setTitle("I'm speed");
		window.setResizable(false);
		window.show();
		
		/* if user didn't confirm the score remove it from scoreboard file */
		window.setOnCloseRequest(e -> {
			Log.success("Exiting ...");
			if(!Scenes.saved) {
				final ScoreboardEntry entry = ScoreboardEntry.activeEntry;
				if(entry != null) {
					Utils.removeRecord(entry.getDate()); 
				}
			}
		});	
	}
		
	public static void error(String err) {
		Scene error = new Scene(Scenes.error(err));
		window.setScene(error);
		
		error.setOnKeyPressed(e -> {
			switch (e.getCode()) {
				case ESCAPE:
					Select.selectDifficulty();
					break;
			default:
				break;
			}
		});
	}
		
	public static void curtain(Scene scene, Pane root) {
		
		game_timer.stop();	// stop the CPM timer
		
		curtain = true;
		
		Rectangle cover = new Rectangle(800, 500, Colors.BACKGROUND_C);
			cover.setVisible(false);
			
		root.getChildren().add(cover);
		
		List<CurtainBlock> blocks = new ArrayList<>();
		
		/* generate 10 curtain blocks for each site */
		for(int i=0; i<10; i++) {
			CurtainBlock left_block = new CurtainBlock(i*50, Math.log(20 - Math.abs(4.5-i))*13, "L");
				blocks.add(left_block);
			CurtainBlock right_block = new CurtainBlock(i*50, Math.log(20 - Math.abs(4.5-i))*13, "R");
				blocks.add(right_block);
								
			root.getChildren().addAll(left_block, right_block);		
		}
		
		blocks.forEach(block -> block.toFront());
		
		animation_curtain = new AnimationTimer() {

			private long lastUpdate = 0;
			boolean reverse = false;

			@Override
			public void handle(long now) {
								
				if(now - lastUpdate >= 35_000_000) {
										
					if(!reverse) {
						if(blocks.get(0).getWidth() > 450) {
							animation_background.stop();
							animation_words.stop();
							
							reverse = true;
							cover.setVisible(true);
							cover.toFront();
							blocks.forEach(block -> block.toFront());
						} else {
							blocks.forEach(block -> block.moveToMiddle());
						}
					} else {
						if(blocks.get(8).getWidth() < 1) {
							animation_curtain.stop();
							gameOver();
							return;
						} else {
							blocks.forEach(block -> block.moveOutside());
						}
					}
					lastUpdate = now;
				}
			}
			
		}; animation_curtain.start();
	}
	
	/* fallback function */
	public static void gameOver() {
		gameOver(false);
	}
	
	public static void gameOver(boolean closed) {
		
		if(CPMs.size() > 0) {
			for(int c : CPMs) {
				avgCPM += c;
			}
			avgCPM /= CPMs.size();
		} else {
			avgCPM = 0;
		}
		
		/* dont log on close */
		if(!closed) {
			System.out.println();
			Log.warning("[GAME OVER]");
			final String[] t = Utils.formatTimePlayed(totalSeconds);
			Log.success(String.format("Total game time: %s:%s:%s", t[0], t[1], t[2]));
		}
		
		if(points > 0) {
			Utils.saveScore(Scenes.pointsVal.getText());
		}		
		
		Pane root = Scenes.gameOver();
			root.setPrefSize(800, 500);
		
		Text retry = new Text("> Press enter to try again <");
			retry.setFill(Color.WHITE);
			retry.setTranslateX(308);
			retry.setTranslateY(370);
			retry.setFont(Font.font("Courier new", 11));
				
		root.getChildren().add(retry);
			root.setOpacity(0);
		
		Scene scene = new Scene(root);
			scene.setFill(Colors.BACKGROUND_C);
			
		window.setScene(scene);
		Utils.fadeIn(root, 300);
		
		animation_gameover = blinkingNodeTimer(retry);
			animation_gameover.start();
		
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
            	animation_gameover.stop();	System.out.println(); 
            	window.setScene(Scenes.scoreboard());
            } e.consume();
        });
	}
	
	public static void startGame(List<File> selected) {
		
		Pane root = Scenes.game(); 
		Scene scene = new Scene(root);
		
		
		/* RESET EVERYTHING */
		curtain = false;
		typedWords = 0;
		typedChars = 0;
		multiplier = 0.98;
		points = -7;		
		CPMs.clear();
		
		Scenes.CPM.setText("0");
		Scenes.pointsVal.setText("0");	

		List<String> strings = Words.loadWords(selected);	// list of all word-strings combined
		List<Word> words = new ArrayList<Word>();	// list of active words
		List<Word> fresh = new ArrayList<Word>();	// list of new words [for placement optimization]
		
		// get longest word's length
		for(String s : strings) {
			if(s.length()>maxWordLen) {
				maxWordLen = s.length();
			}
		}
		maxWordLen *= 9; // multiply by pixels of 1 letter with space
		
		// list for predefined x & y coordinates
		final List<Integer> xVal_final = new ArrayList<Integer>();
		final List<Integer> yVal_final = new ArrayList<Integer>();
		
		// predefined values
		for(int i=-10; i<10; i+=5) xVal_final.add(i);
		for(int i=20; i<400; i+=20) yVal_final.add(i);
		
		// temporary sublists
		xVal = new ArrayList<Integer>(xVal_final);
		yVal = new ArrayList<Integer>(yVal_final);
		
		Word first = new Word(0, 195, "type-me");		// first word
		words.add(first);
						
		root.getChildren().add(first);
		window.setScene(scene);	// render scene
				
		/* set difficulty variables */ 
		switch(gameDifficulty) {		
		case 1:
			maxWords = 16;
			multiplierAdd = 0.01;
			howOften = 7_000_000_000l;
			howFast = 1_650_000_000;
			howMany = 3;
			timeLeft = 25;
			break;
		
		case 2:
			maxWords = 17;
			multiplierAdd = 0.03;
			howOften = 6_000_000_000l;
			howFast = 750_000_000;
			howMany = 5;
			timeLeft = 15;
			break;
		
		case 3:
			maxWords = 17;
			multiplierAdd = 0.05;
			howOften = 5_500_000_000l;
			howFast = 650_000_000;
			howMany = 6;
			timeLeft = 10;
			break;
		
		case 4:
			maxWords = 18;
			multiplierAdd = 0.1;
			howOften = 4_500_000_000l;
			howFast = 550_000_000;
			howMany = 6;
			timeLeft = 5;
			break;
		
		case 5:
			maxWords = 100;
			break;
					
		}
		
		Scenes.conditionVal.setText(Window.gameMode == 0 ? "0" : String.valueOf(timeLeft));
		
		/* timer for calculating CPM */
		game_timer = new AnimationTimer() {
						
			private long lastUpdate = 0;

			@Override
			public void handle(long now) {
				
				if(pause) {
					lastUpdate = now;
				}
				
				/* every 1s */
				if(now - lastUpdate >= 1_000_000_000) {
					
					if(gameMode == 1) {
						Scenes.conditionVal.setText(String.valueOf(--timeLeft));
						if(timeLeft <= 0) {
							root.getChildren().removeAll(words);	// remove all objects
							curtain(scene, root);
						}
					}
					
					/* calculating CPM */
					totalSeconds = (now - startTime) / 1_000_000_000l;
					double minutes = totalSeconds/60;
					int calc = (int) Math.round(typedChars/minutes);
					
					/* skip the 1st word*/
					if(calc > -1 && typedWords > 1) {
						CPMs.add(calc);
						Scenes.CPM.setText(String.valueOf(calc));

						if(calc == 69) {
							Scenes.CPM.setStyle(Colors.GAY_GRADIENT);  
						} else {
							/* ranges for color change */
					 		if(calc > 350) Scenes.CPM.setStyle(Colors.GOLD_GRADIENT);  /* >350 */
					 		else Scenes.CPM.setFill(
								(calc > 250) ? Colors.GREEN_C :		/* 250-350 */
								(calc > 200) ? Colors.YELLOW_C :		/* 200-250 */
								(calc > 150) ? Colors.ORANGE_C :		/* 150-200 */
								Colors.RED_C							/* <150 */
							);						 		
						}
					}
					lastUpdate = now;
				}
			}
			
		};
		
		animation_background = Utils.getBackgroundTimer(790, 398, root);
			animation_background.start();
		
		/* animating words */
		animation_words = new AnimationTimer() {
						
			private long lastUpdate = 0;
			private long lastUpdate2 = 0;
			
			int strike = 0;
						
			@Override
			public void handle(long now) {
				 
				if(now - lastUpdate >= howFast) {
					List<Word> del = new ArrayList<Word>();		// list of words to deletion after loop
					boolean gameOver = false;
					
					outerloop:
					for(Word w : words) {
						
						if(curtain) {
							break;
						}
						w.moveForward(); 	// move all words forward
						w.toFront();
						
						double xPos = w.getTranslateX();
						if(xPos > 805) {	// if word leaves beyond the window
							
							multiplier = 1;	// reset multiplier
							root.getChildren().remove(w);	// remove word from the pane
							del.add(w);	// add word to deletion list
							
							switch(gameMode) {
								case 0:
									Scenes.conditionVal.setText(String.valueOf(++strike));	// update missed and increase strikes
									if(typedWords != 0) {
										Log.warning("[STRIKE]: " +  strike);
									}
									if(!infinite && strike >= 10) {
										gameOver = true;
										break outerloop;
									}
									break;
								case 1:
									timeLeft -= 10;
									if(timeLeft <= 0) {
										gameOver = true;
										break outerloop;
									} else {
										Scenes.conditionVal.setText(String.valueOf(timeLeft));
									}
									break;
							}
						}
						
						if(!w.getValue().equals("I'm gay")) {	// gay will remain rainbowish
							if(xPos > 370) {
								w.setColor(Colors.YELLOW);
							}
							if(xPos > 500) {
								w.setColor(Colors.ORANGE);
							}
							if(xPos > 630) {
								w.setColor(Colors.RED);
							}
						}
						
						if(xPos > maxWordLen) {
							fresh.remove(w);	// if word is further than longest word remove it from list of new words
						}						
					}
					
					if(gameOver) {
						root.getChildren().removeAll(words);	// remove all objects
						curtain(scene, root);
					} else {
						words.removeAll(del);
					}
					
					if(words.isEmpty()) { // if no words are on the screen
						if(!curtain) {	// and it's not the end of the game
							if(typedWords == 0) {	// and the first word wasn't typed end the game
								curtain(scene, root);
							} else {	// else generate new words
								for(int i=0; i<howMany; i++) {		
									Word word = createWord(strings, xVal_final, yVal_final, fresh);
										fresh.add(word);
										words.add(word);
										root.getChildren().add(word);
								}
							}
						}
					}
					lastUpdate = now;
				}
				
				/* every [n] seconds add [m] new words if less than [x] are displayed */
				if(now - lastUpdate2 >= howOften && typedWords > 4) {
					for(int i=0; i<howMany; i++) {		
						if(words.size() < maxWords) {
							Word word = createWord(strings, xVal_final, yVal_final, fresh);
							fresh.add(word);
							words.add(word);
							root.getChildren().add(word);
						}
					}
					lastUpdate2 = now;
				}
			}
			
		}; animation_words.start();

		Scenes.input.setOnKeyPressed(e -> {
			
			switch (e.getCode()) {
			case ESCAPE:

				if(!pause) {
					animation_words.stop();
					pauseTime = System.nanoTime();

					Log.success("Suspended");
					Scenes.input.setEditable(false);
					Scenes.pauseBox.setVisible(true);
					Scenes.pauseBox.toFront();
				} else {
					animation_words.start();
					startTime = System.nanoTime() - (pauseTime - startTime);

					Scenes.input.setEditable(true);
					Scenes.pauseBox.setVisible(false);
				}
				
				pause = !pause;			
				break;
			
			case ENTER:
				
				if(Scenes.input.getText().equals("killmenow")) {	// special word to end the game
					curtain(scene, root);
				}
																							
				List<Word> del = new ArrayList<Word>();		// list for words to be deleted from "words" list
				List<Word> add = new ArrayList<Word>();		// list for words to be added to "words" list
				
				for(Word w : words) {
					
					if(w.getValue().equals(Scenes.input.getText())) {	// if typed word is equal to eny currently displayed
						
						timeLeft += (typedWords > 0) ? w.getLength()/3*multiplier : 0;	// for marathon add time for typed word
						points += w.getLength()*multiplier; // add points accordingly to multiplier
						multiplier += multiplierAdd;	// increase multiplier
						typedWords++; typedChars += w.getValue().length();	// increase the amount of typed words and characters
							
						if(w.getValue().equals("I'm gay")) {
							w.setValue("lmao");
							w.setColor("#FFF");
							continue;
						}
						fresh.remove(w); del.add(w);	// remove from new words and add to deletion from main list
						root.getChildren().remove(w);	// remove from pane
						
						Scenes.pointsVal.setText(String.valueOf(Math.round(points)));	// update the points
						
						switch (typedWords) {
						case 1:
							startTime = System.nanoTime(); game_timer.start();	// after typing first word start timer for CPM
							
							/* add [m] new words */
							for(int i=0; i<howMany; i++) {					
								Word word = createWord(strings, xVal_final, yVal_final, fresh);
									fresh.add(word);
									add.add(word);
									root.getChildren().add(word);
							}
							break;
						
						case 3:
							for(int i=0; i<3; i++) {
								Word word = createWord(strings, xVal_final, yVal_final, fresh);
									fresh.add(word);
									add.add(word);
									root.getChildren().add(word);
							}
							break;
						
						default:
							/* every 6th typed word add [m] new words */
							if(typedWords%6==0) {
								for(int i=0; i<howMany; i++) {
									Word word = createWord(strings, xVal_final, yVal_final, fresh);
										fresh.add(word);
										add.add(word);
										root.getChildren().add(word);
								}
							}
							break;
						}
					}
				}
				words.addAll(add);	// add words
				words.removeAll(del);	// delete words
				Scenes.input.clear(); // clear text field
				break ;	
				
			default: break;
			}
		});
	}
	

}
