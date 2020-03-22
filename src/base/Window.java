package base;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import menu.Selection;
import menu.MenuWords;


public class Window extends Application {
		
	public static int DIFFICULTY;
	public static Stage window;
	
	static double points=-7;
	private static int typed=0;
	private static int max_word_len = 0;
	private static double multiplier = 0.98;
	
	private static long howOften;
	private static long howFast;
	private static int howMuch;
	private static double multiplierAdd;
	
	private static List<Integer> xVal;
	private static List<Integer> yVal;
	
	static AnimationTimer timer;
		
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
		Pane root = new Pane(); root.setPrefSize(800, 500);
		Scene scene = Scenes.gameOver(root);
		window.setScene(scene);
		
	}
	
	public static void startGame(List<File> selected) {
		
		Pane root = new Pane(); root.setPrefSize(800, 500);
		Scene scene = Scenes.game(root);
		
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
				
		timer = new AnimationTimer() {
			
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
							if(w.getTranslateX()>800) {		// if word leaves beyond the window
								strike++; multiplier = 1;	// add strikes, reset multiplier
								del.add(w);	root.getChildren().remove(w);	// add word to deletion, and remove it from pane
								System.out.println("Strike: " +  strike);
							}	
						} words.removeAll(del); lastUpdate = now;
						
					} else {	// if game is over
						root.getChildren().removeAll(words);	// remove all objects
						timer.stop(); gameOver();	// stop the timer and change the scene
						System.out.println("rip");
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
			
		}; timer.start();

		Scenes.input.setOnKeyPressed(e -> {
			
			switch (e.getCode()) {
				
				case ENTER:	// every ENTER pressed:
					
					if(Scenes.input.getText().equals("killmenow")) {	// special word to end the game
						timer.stop();
						gameOver();
					}
														
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
