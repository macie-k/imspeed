package base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import menu.Selection;
import menu.MenuWords;

public class Window extends Application {
		
	public static int DIFFICULTY;
	public static Stage window;
	
	public static Color BACKGROUND = Color.web("#0e0e0e");
	
	static double points;
	private static int typed;
	private static int max_word_len = 0;
	private static double multiplier;
	
	private static long howOften;
	private static long howFast;
	private static int howMuch;
	private static double multiplierAdd;
	
	private static List<Integer> xVal;
	private static List<Integer> yVal;
	
	static AnimationTimer WORDS_ANIMATION;
	static AnimationTimer BACKGROUND_ANIMATION;
	static AnimationTimer GAMEOVER_ANIMATION;
		
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		
		setDiff();
		window.setTitle("I'm speed");
		window.setResizable(false);
		window.show();
	}
	
	public static void setDiff() {
		Pane root = new Pane(); root.setPrefSize(800, 500);
		Selection.selectDifficulty(root);
	}
	
	public static void setLang() {
		Pane lng = new Pane(); lng.setPrefSize(800, 500);
		Selection.selectLanguage(lng);	// run menu scene
	}
	
	public static void gameOver() {	
		WORDS_ANIMATION.stop();
		BACKGROUND_ANIMATION.stop(); 
		
		Pane root = new Pane(); root.setPrefSize(800, 500);
		Text retry = new Text("> Press enter to try again <"); retry.setFill(Color.WHITE); retry.setTranslateX(308);retry.setTranslateY(370); retry.setFont(Font.font("Courier new", 11));
		Scene scene = Scenes.gameOver(root);
		
		root.getChildren().add(retry);
		window.setScene(scene);
		
		GAMEOVER_ANIMATION = new AnimationTimer() {
			
			private long lastUpdate = 0;
						
			@Override
			public void handle(long now) {		
				
				if(now - lastUpdate >= 500_000_000) {
					retry.setVisible(!retry.isVisible());
					window.setScene(scene);
					lastUpdate = now;
				}
			}
		}; GAMEOVER_ANIMATION.start();
		
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
            	GAMEOVER_ANIMATION.stop();
                setDiff();
            } e.consume();
        });
	}
	
	public static void startGame(List<File> selected) {
		
		Pane root = new Pane(); root.setPrefSize(800, 500);
		Scene scene = Scenes.game(root);
		
		List<Particle> particles = new ArrayList<Particle>();
		int[] particleY = new int[398]; for(int i=0; i<398; i++) particleY[i] = i+2;
		Random random = new Random();
		
		for(int i=0; i<200; i++) {
			Particle p = new Particle(random.nextInt(790)+10, particleY[random.nextInt(398)], random.nextDouble());
				particles.add(p); root.getChildren().add(p);
			
			Particle trail_1 = new Particle(p.getTranslateX()-1, p.getTranslateY(), p.getAlpha()*0.66);
				particles.add(trail_1); root.getChildren().add(trail_1);
				
			Particle trail_2 = new Particle(p.getTranslateX()-2, p.getTranslateY(), p.getAlpha()*0.33);
				particles.add(trail_2); root.getChildren().add(trail_2);
		}
		
		multiplier = 0.98; points = -7; typed = 0; Scenes.points.setText("");
		window.setScene(scene);
		
		List<String> strings = MenuWords.loadWords(selected);	// list of all word-strings combined
		List<Word> words = new ArrayList<Word>();	// list of avctive words
		List<Word> fresh = new ArrayList<Word>();	// list of new words [for placement optimization]
		
		// get longest word's length
		for(String s : strings)
			if(s.length()>max_word_len) max_word_len = s.length();
		max_word_len *= 9;
		
		// list for predefined x & y coordinates
		final List<Integer> xVal_final = new ArrayList<Integer>();
		final List<Integer> yVal_final = new ArrayList<Integer>();
		
		// predefined values
		for(int i=-10; i<10; i+=5) { xVal_final.add(i); }
		for(int i=20; i<400; i+=20) { yVal_final.add(i); }
		
		// temporary sublists
		xVal = new ArrayList<Integer>(xVal_final);
		yVal = new ArrayList<Integer>(yVal_final);
		
		Word first = new Word(0, 195, "type-me");		// first word
		words.add(first);
						
		root.getChildren().add(first);
		window.setScene(scene);
		
		//System.out.println(xVal.size() + " :: " + xVal);
		//System.out.println(yVal.size() + " :: " + yVal);
		
		switch(DIFFICULTY) {
			case 1:
				multiplierAdd = 0;
				howOften = 7_000_000_000l;
				howFast = 2_000_000_000;
				howMuch = 3;
			break;
			
			case 2:
				multiplierAdd = 0.01;
				howOften = 7_000_000_000l;
				howFast = 1_000_000_000;
				howMuch = 5;
			break;
			
			case 3:
				multiplierAdd = 0.02;
				howOften = 6_000_000_000l;
				howFast = 700_000_000;
				howMuch = 5;
			break;
			
			case 4:
				multiplierAdd = 0.04;
				howOften = 6_000_000_000l;
				howFast = 650_000_000;
				howMuch = 6;
			break;
			
			case 5:
				multiplierAdd = 0.1;
				howOften = 4_500_000_000l;
				howFast = 600_000_000;
				howMuch = 6;
			break;
		}
		
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
							window.setScene(scene);				
						}
					} particles.removeAll(toRemove); root.getChildren().removeAll(toRemove); toRemove.clear();
					particle_move = now;
				}
				
				if(now - particle_create >= 7_000_000) {
					Particle p = new Particle(-2, particleY[random.nextInt(398)], random.nextDouble());
						particles.add(p); root.getChildren().add(p);
						
					Particle trail_1 = new Particle(-3, p.getTranslateY(), p.getAlpha()*0.66);
						particles.add(trail_1); root.getChildren().add(trail_1);
						
					Particle trail_2 = new Particle(-4, p.getTranslateY(), p.getAlpha()*0.33);
						particles.add(trail_2); root.getChildren().add(trail_2);
					particle_create = now;	
				}
				
			}
			
		}; BACKGROUND_ANIMATION.start();
						
		WORDS_ANIMATION = new AnimationTimer() {
						
			private long lastUpdate = 0;
			private long lastUpdate2 = 0;
			
			int strike = 0;
						
			@Override
			public void handle(long now) {
				 
				if(now - lastUpdate >= howFast) {	// do every 0.7s
					if(strike < 10) {
						List<Word> del = new ArrayList<Word>();		// list of words to deletion after loop
						
						for(Word w : words) {
							w.moveForward(); 	// move all words forward
							
							if(w.getTranslateX() > max_word_len) fresh.remove(w);	// if word is further than longest word remove it from list of new words
							if(w.getTranslateX()>805) {		// if word leaves beyond the window
								strike++; multiplier = 1;	// add strikes, reset multiplier
								del.add(w);	root.getChildren().remove(w);	// add word to deletion, and remove it from pane
								System.out.println("Strike: " +  strike);
							}	
						} words.removeAll(del); lastUpdate = now;
						
						if(words.isEmpty()) gameOver();
						
					} else {	// if game is over
						root.getChildren().removeAll(words);	// remove all objects
						System.out.println("rip");
						gameOver();	// stop the timer and change the scene
					}
				}
				
				if(now - lastUpdate2 >= howOften && typed > 4) {		// every 6 seconds try to add 5 new words if less than 20 are displayed
					if(words.size() < 20) {
						for(int i=0; i<howMuch; i++) {		
							Word word = createWord(strings, xVal_final, yVal_final, fresh);
							fresh.add(word); words.add(word);
							root.getChildren().add(word);
						}
					} lastUpdate2 = now;
				}
			}
			
		}; WORDS_ANIMATION.start();

		Scenes.input.setOnKeyPressed(e -> {
			
			switch (e.getCode()) {
				
				case ENTER:	// every ENTER pressed:
					
					if(Scenes.input.getText().equals("killmenow")) {	// special word to end the game
						gameOver();
					}
					
//					if(Scenes.input.getText().equals("add")) {
//						points += 10000;
//						Scenes.points.setText(String.valueOf(Math.round(points)));
//						window.setScene(scene);
//					}
																								
					List<Word> del = new ArrayList<Word>();		// list for words to be deleted from "words" list
					List<Word> add = new ArrayList<Word>();		// list for words to be added to "words" list
					
					for(Word w : words) {
						
						if(w.getValue().equals(Scenes.input.getText())) {	// if typed word is equal to eny currently displayed
													
							points+=w.getLength()*multiplier; // add points accordingly to multiplier,
							typed++; multiplier+=multiplierAdd;	// increase typed counter and multiplier
							
							fresh.remove(w); del.add(w);	// remove word from all lists
							root.getChildren().remove(w);	// and pane
							
							Scenes.points.setText(String.valueOf(Math.round(points)));	// update the points
							window.setScene(scene);		// update the scene
							
							switch (typed) {
							
								case 1:
									for(int i=0; i<howMuch; i++) {									
										Word word = createWord(strings, xVal_final, yVal_final, fresh);
										fresh.add(word); add.add(word);
										root.getChildren().add(word);
									}
									
								break;
								
								case 3:
									for(int i=0; i<3; i++) {
										Word word = createWord(strings, xVal_final, yVal_final, fresh);
										fresh.add(word); add.add(word);
										root.getChildren().add(word);
									}
								break;
								
								default:
									if(typed%6==0) {
										for(int i=0; i<howMuch; i++) {
											Word word = createWord(strings, xVal_final, yVal_final, fresh);
											fresh.add(word); add.add(word);
											root.getChildren().add(word);
										}
									}
								break;
							}
						}
					}
					words.addAll(add); words.removeAll(del);	// add and delete words
					Scenes.input.clear(); break;	// remove the typed word from active word list and clear text field
					
				default: break;
			}
		});
	}
	
	/* function to calculate new word's position and value */
	public static Word createWord(List<String> strings, List<Integer> xVal_final, List<Integer> yVal_final, List<Word> fresh) {
		
		if(xVal.size() < 1) xVal = new ArrayList<>(xVal_final);		// 
		if(yVal.size() < 1) yVal = new ArrayList<>(yVal_final);
		
		Random random = new Random();

		// get random indexes
		int rndmx = random.nextInt(xVal.size());	
		int rndmy = random.nextInt(yVal.size());
	
		String value = strings.get(random.nextInt(strings.size()));		// get random text from all words
		
		// set x,y and remove them from lists
		int y = yVal.get(rndmy); yVal.remove(rndmy);
		int x = xVal.get(rndmx); xVal.remove(rndmx);
		
		// check for word collision in the same row and calculate the final x coordinate
		for(Word w : fresh)
			if(w.getTranslateY() == y)
				while(w.getTranslateX() <= (value.length()*9)+x+20)
					x -= 5;
		
		return new Word(x, y, value);
	}
	
	public static void main (String[] args) {
		launch(args);
	}

}
