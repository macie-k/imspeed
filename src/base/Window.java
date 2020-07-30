package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import base.obj.CurtainBlock;
import base.obj.Particle;
import base.obj.Word;
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
		
	public static int DIFFICULTY;
	public static Stage window;
	public static String OS, slash;
	public static Color BACKGROUND = Color.web("#0e0e0e");
	
	public static List<Scene> SCENES = new ArrayList<Scene>();
	public static int SCENE_INDEX = -1;
		
	static String SCORE_DIR;	// directory to save backup and fonts
	
	static double points;
	static List<Integer> CPMs = new ArrayList<Integer>();	// list of all registered CPMs [for average calculating]
	static int avgCPM;	// average CPM [for saving]
	
	private static List<Integer> xVal, yVal;
	private static boolean curtain;
	private static boolean pause = false;
	
	private static AnimationTimer WORDS_ANIMATION, BACKGROUND_ANIMATION, GAMEOVER_ANIMATION, CURTAIN_ANIMATION, TIMER;
	
	private static int typedWords, typedChars;
	private static int max_word_len = 0;
	private static double multiplier;
	private static long startTime;
	private static long pauseTime;
	
	public static long howOften;
	public static long howFast;
	public static int maxWords;
	public static int howMany;
	public static double multiplierAdd;

	
	@Override
	public void start(Stage primaryStage) throws Exception {
				
		/* Detect OS for file manipulation */
		if(System.getProperty("os.name").toLowerCase().equals("linux")) {
			OS = "linux";
			slash = "/";
			SCORE_DIR = System.getenv("HOME") + "/.imspeed/";
		} else {
			OS = "windows";
			slash = "\\";
			SCORE_DIR = System.getenv("appdata") + "\\imspeed\\";
		}
		
		window = primaryStage;
		window.getIcons().add(new Image("/icon.jpg"));

		Scenes.fontSetup();		System.out.println();
		setDiff();
		
		window.setTitle("I'm speed");
		window.setResizable(false);
		window.show();
	}
		
	public static void error(String err) {
		
		Scene error = new Scene(Scenes.error(err));
		window.setScene(error);
		
		error.setOnKeyPressed(e -> {
			switch (e.getCode()) {
				case ESCAPE:
					setDiff();
					break;
			default:
				break;
			}
		});
	}
	
	public static void setDiff() {		
		Select.selectDifficulty();
	}
	
	public static void setLang(boolean custom) {			
		Select.selectLanguage(custom);
	}
	
	public static void curtain(Scene scene, Pane root) {
		
		TIMER.stop();	// stop the CPM timer
		
		curtain = true;
		
		Rectangle cover = new Rectangle(800, 500, BACKGROUND);
			cover.setVisible(false);
			
		root.getChildren().add(new Rectangle(800, 500, BACKGROUND));
		
		List<CurtainBlock> blocks = new ArrayList<>();
		
		/* generate 10 curtain blocks for each site */
		for(int i=0; i<10; i++) {
			CurtainBlock left_block = new CurtainBlock(i*50, Math.log(20 - Math.abs(4.5-i))*13, "L");
				blocks.add(left_block);
			CurtainBlock right_block = new CurtainBlock(i*50, Math.log(20 - Math.abs(4.5-i))*13, "R");
				blocks.add(right_block);
								
			root.getChildren().addAll(left_block, right_block);		
		}
		
		window.setScene(scene);
		
		CURTAIN_ANIMATION = new AnimationTimer() {

			private long lastUpdate = 0;
			boolean reverse = false;

			@Override
			public void handle(long now) {
								
				if(now - lastUpdate >= 35_000_000) {
										
					if(!reverse) {
						if(blocks.get(0).getWidth() > 450) {
							BACKGROUND_ANIMATION.stop();
							WORDS_ANIMATION.stop();
							
							reverse = true;
							cover.setVisible(true);
						} else {
							blocks.forEach(block -> block.moveToMiddle());
						}
					} else {
						if(blocks.get(8).getWidth() < 1) {
							CURTAIN_ANIMATION.stop();
							gameOver();
							return;
						} else {
							blocks.forEach(block -> block.moveOutside());
						}
					}
					lastUpdate = now;
				}
			}
			
		}; CURTAIN_ANIMATION.start();
	}
	
	public static void gameOver() {
		
		System.out.println("[GAME OVER]\n");
			
		if(CPMs.size() > 0) {
			for(int c : CPMs) {
				avgCPM += c;
			}
			avgCPM /= CPMs.size();
		} else {
			avgCPM = 0;
		}

		Save.saveScore(Scenes.pointsVal.getText());
		
		Pane root = Scenes.gameOver();
			root.setPrefSize(800, 500);
		
		Text retry = new Text("> Press enter to try again <");
			retry.setFill(Color.WHITE);
			retry.setTranslateX(308);
			retry.setTranslateY(370);
			retry.setFont(Font.font("Courier new", 11));
				
		Scene scene = new Scene(root);
		root.getChildren().add(retry);
		
		window.setScene(scene);
		
		GAMEOVER_ANIMATION = new AnimationTimer() {
			
			private long lastUpdate = 0;
						
			@Override
			public void handle(long now) {		
				
				if(now - lastUpdate >= 500_000_000) {
					retry.setVisible(!retry.isVisible());
					lastUpdate = now;
				}
			}
		}; GAMEOVER_ANIMATION.start();
		
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
            	GAMEOVER_ANIMATION.stop();	System.out.println(); 
                setDiff(); 
            } e.consume();
        });
	}
	
	public static void startGame(List<File> selected) {
		
		Pane root = Scenes.game(); 
		Scene scene = new Scene(root);
		
		List<Particle> particles = new ArrayList<Particle>();	// list of all particles
		Random random = new Random();
		
		int[] particleY = new int[398];
		for(int i=0; i<398; i++) {
			particleY[i] = i+2;	// array of predefined Y values
		}
		
		/* generate particle with its trail */
		for(int i=0; i<70; i++) {
			Particle p = new Particle(random.nextInt(790)+10, particleY[random.nextInt(398)], random.nextDouble());
				particles.add(p); root.getChildren().add(p);
			
			Particle trail_1 = new Particle(p.getTranslateX()-1, p.getTranslateY(), p.getAlpha()*0.66);
				particles.add(trail_1); root.getChildren().add(trail_1);
				
			Particle trail_2 = new Particle(p.getTranslateX()-2, p.getTranslateY(), p.getAlpha()*0.33);
				particles.add(trail_2); root.getChildren().add(trail_2);
		}
		
		/* RESET EVERYTHING */
		curtain = false;
		typedWords = 0;
		typedChars = 0;
		multiplier = 0.98;
		points = -7;		
		CPMs.clear();
		
		Scenes.missedVal.setText("0");
		Scenes.CPM.setText("0");
		Scenes.pointsVal.setText("0");	

		List<String> strings = Words.loadWords(selected);	// list of all word-strings combined
		List<Word> words = new ArrayList<Word>();	// list of active words
		List<Word> fresh = new ArrayList<Word>();	// list of new words [for placement optimization]
		
		// get longest word's length
		for(String s : strings) {
			if(s.length()>max_word_len) {
				max_word_len = s.length();
			}
		}
		max_word_len *= 9; // multiply by pixels of 1 letter with space
		
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
		switch(DIFFICULTY) {		
		case 1:
			maxWords = 16;
			multiplierAdd = 0.01;
			howOften = 7_000_000_000l;
			howFast = 1_650_000_000;
			howMany = 3;
		break;
		
		case 2:
			maxWords = 17;
			multiplierAdd = 0.03;
			howOften = 6_000_000_000l;
			howFast = 750_000_000;
			howMany = 5;
		break;
		
		case 3:
			maxWords = 17;
			multiplierAdd = 0.05;
			howOften = 5_500_000_000l;
			howFast = 650_000_000;
			howMany = 6;
		break;
		
		case 4:
			maxWords = 18;
			multiplierAdd = 0.1;
			howOften = 4_500_000_000l;
			howFast = 550_000_000;
			howMany = 6;
		break;
		
		case 5:
			maxWords = 100;
		break;
		}
		
		/* timer for calculating CPM */
		TIMER = new AnimationTimer() {
						
			private long lastUpdate = 0;

			@Override
			public void handle(long now) {
				
				if(pause) {
					lastUpdate = now;
				}
				
				/* every 1s */
				if(now - lastUpdate >= 1_000_000_000) {
					
					/* calculating CPM */
					double seconds = (now - startTime) / 1_000_000_000l;
					double minutes = seconds/60;
					int calc = (int) Math.round(typedChars/minutes);
					
					/* skip the 1st word*/
					if(calc > -1 && typedWords > 1) {
						CPMs.add(calc);
						Scenes.CPM.setText(String.valueOf(calc));

						if(calc == 69) {
							Scenes.CPM.setStyle(Scenes.Color_GAY_GRADIENT);  
						} else {
							/* ranges for color change */
							/* >350 */ 		if(calc > 350) Scenes.CPM.setStyle(Scenes.Color_GOLD_GRADIENT); else 
							/* 250-350 */ 	if(calc > 250) Scenes.CPM.setFill(Color.web(Scenes.Color_GREEN)); else
							/* 200-250 */ 	if(calc > 200) Scenes.CPM.setFill(Color.web(Scenes.Color_YELLOW)); else
							/* 150-200 */	if(calc > 150) Scenes.CPM.setFill(Color.web(Scenes.Color_ORANGE));
							/* <150 */ 		else Scenes.CPM.setFill(Color.web(Scenes.Color_RED));
						}
					}
					lastUpdate = now;
				}
			}
			
		};
		
		/* animating particles */
		BACKGROUND_ANIMATION = new AnimationTimer() {

			private long particle_create = 0;
			private long particle_move = 0;
			
			@Override
			public void handle(long now) { 
				
				if(now - particle_move >= 5_000_000) {
					List<Particle> toRemove = new ArrayList<Particle>();
					
					for(Particle p : particles) {						
						if(p.getTranslateX()>800) {
							toRemove.add(p);
						} else {
							p.moveForward();
						}
					} 
					
					particles.removeAll(toRemove);
					root.getChildren().removeAll(toRemove);
					toRemove.clear();
					
					particle_move = now;
				}
				
				if(!curtain) {
					if(now - particle_create >= 50_000_000) {
						Particle p = new Particle(-2, particleY[random.nextInt(398)], random.nextDouble());
							particles.add(p); root.getChildren().add(p);
							
						Particle trail_1 = new Particle(-3, p.getTranslateY(), p.getAlpha()*0.66);
							particles.add(trail_1); root.getChildren().add(trail_1);
							
						Particle trail_2 = new Particle(-4, p.getTranslateY(), p.getAlpha()*0.33);
							particles.add(trail_2); root.getChildren().add(trail_2);
							
						particle_create = now;	
					}
				}
			}
			
		}; BACKGROUND_ANIMATION.start();
						
		/* animating words */
		WORDS_ANIMATION = new AnimationTimer() {
						
			private long lastUpdate = 0;
			private long lastUpdate2 = 0;
			
			int strike = 0;
						
			@Override
			public void handle(long now) {
				 
				if(now - lastUpdate >= howFast) {
					List<Word> del = new ArrayList<Word>();		// list of words to deletion after loop
					
					for(Word w : words) {
						
						if(curtain) break;
						w.moveForward(); 	// move all words forward
						
						double xPos = w.getTranslateX();
						if(xPos > 805) {	// if word leaves beyond the window
							
							Scenes.missedVal.setText(String.valueOf(++strike));	// update missed and increase strikes
							if(typedWords != 0) {
								System.out.println("[STRIKE]: " +  strike);
							}
							
							if(strike < 10) {
								multiplier = 1;	// reset multiplier
								
								root.getChildren().remove(w);	// remove word from the pane
								del.add(w);	// add word to deletion list
							} else {
								root.getChildren().removeAll(words);	// remove all objects
								System.out.println();
								curtain(scene, root);
								break;
							}
						}
						
						/* lets leave the gays be */
						if(!w.getValue().equals("I'm gay")) {
							if(xPos > 370) {
								w.setColor(Scenes.Color_YELLOW);
							}
							if(xPos > 500) {
								w.setColor(Scenes.Color_ORANGE);
							}
							if(xPos > 630) {
								w.setColor(Scenes.Color_RED);
							}
						}
						
						if(xPos > max_word_len) {
							fresh.remove(w);	// if word is further than longest word remove it from list of new words
						}						
					}
					
					words.removeAll(del);
					
					if(words.isEmpty()) { // if no words are on the screen
						if(!curtain) {
							if(typedWords == 0) {	// if fist word wasn't typed end the game
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
				
				/* every [n] seconds add [m] new words if less than 17 are displayed */
				if(now - lastUpdate2 >= howOften && typedWords > 4) {
					for(int i=0; i<howMany; i++) {		
						if(words.size() < maxWords) {
							Word word = createWord(strings, xVal_final, yVal_final, fresh);
							fresh.add(word);
							words.add(word);
							root.getChildren().add(word);
						}
					} lastUpdate2 = now;
				}
			}
			
		}; WORDS_ANIMATION.start();

		Scenes.input.setOnKeyPressed(e -> {
			
			switch (e.getCode()) {
			case ESCAPE:

				if(!pause) {
					WORDS_ANIMATION.stop();
					BACKGROUND_ANIMATION.stop();
					pauseTime = System.nanoTime();
					System.out.println("Pause: " + pauseTime);
					Scenes.input.setEditable(false);
					Scenes.pauseBox.setVisible(true);
					Scenes.pauseBox.toFront();
				} else {
					WORDS_ANIMATION.start();
					BACKGROUND_ANIMATION.start();
					startTime = System.nanoTime() - (pauseTime - startTime);
					System.err.println(startTime);
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
										
						points += w.getLength()*multiplier; // add points accordingly to multiplier,
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
							startTime = System.nanoTime(); TIMER.start();	// after typing first word start timer for CPM
							System.out.println(startTime);
							
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
	
	/* function to calculate new word's position and value */
	public static Word createWord(List<String> strings, List<Integer> xVal_final, List<Integer> yVal_final, List<Word> fresh) {
		
		if(xVal.size() < 1) {
			xVal = new ArrayList<>(xVal_final);
		}
		if(yVal.size() < 1) {
			yVal = new ArrayList<>(yVal_final);
		}
		
		Random random = new Random();

		/* get random indexes */
		int rndmx = random.nextInt(xVal.size());	
		int rndmy = random.nextInt(yVal.size());
	
		String value = strings.get(random.nextInt(strings.size()));		// get random text from all words
		
		/* set x,y and remove them from lists */
		int y = yVal.get(rndmy);
			yVal.remove(rndmy);
		int x = xVal.get(rndmx);
			xVal.remove(rndmx);
		
		/* check for word collision in the same row and calculate the final x coordinate */
		for(Word w : fresh) {
			if(w.getTranslateY() == y) {
				while(w.getTranslateX() <= (value.length()*9)+x+20) {
					x -= 5;
				}
			}
		}	
		
		Word w = new Word(x, y, value);
		
		/* there is 0.00043687199 chance at most, that it will happen, pls don't get mad */
		if(value.equals("I'm gay")) {
			w.setStyle(Scenes.Color_GAY_GRADIENT);
		}
		
		return w;
	}
	
	public static void main (String[] args) throws FileNotFoundException {
				
		if(args.length>0) {
			for(String arg : args) {
				
				switch(arg) {
					case "--log":
						System.out.println("[OK] Logging enabled");
						
						PrintStream outputLog = new PrintStream(new FileOutputStream(new File("log.txt")));
							System.setOut(outputLog);
							System.setErr(outputLog);
					break;
					
					default: break;
				}
			}
		}
		launch(args);
	}
}
