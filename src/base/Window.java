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
import menu.Words;


public class Window extends Application {
		
	public static Stage window;
	
	static int points=-7;
	private static int typed=0;
	private static int max_word_len=0;
	
	private static List<Integer> xVal;
	private static List<Integer> yVal;
	
	static AnimationTimer timer;
		
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		window = primaryStage;
		
		Pane root = new Pane(); root.setPrefSize(800, 500);;
		
		//window.setScene(new Scene(root, Color.web("#0f0f0f")));
		Selection.select(window, root);	// run menu scene
		window.setTitle("I'm speed");
		window.setResizable(false);
		window.show();
		
	}
	
	public static void gameOver() {	
		Pane root = new Pane(); root.setPrefSize(800, 500);
		window.setScene(Scenes.gameOver(root));
	}
	
	public static void startGame(List<File> selected) {
		
		Pane root = new Pane(); root.setPrefSize(800, 500);
		Scene scene = Scenes.game(root);
		
		List<String> strings = Words.loadWords(selected);	// list of all word-strings combined
		List<Word> words = new ArrayList<Word>();	// list of avctive words
		List<Word> fresh = new ArrayList<Word>();
		
		for(String s : strings) {
			if(s.length()>max_word_len) max_word_len = s.length();
		}
		max_word_len *= 8;
		
		final List<Integer> xVal_final = new ArrayList<Integer>();
		final List<Integer> yVal_final = new ArrayList<Integer>();
		
		for(int i=-10; i<20; i+=5) { xVal_final.add(i); }
		for(int i=20; i<400; i+=20) { yVal_final.add(i); }
		
		xVal = new ArrayList<Integer>(xVal_final);
		yVal = new ArrayList<Integer>(yVal_final);
		
		Word first = new Word(0, 195, "type-me");		// first trial word
		words.add(first);
						
		root.getChildren().add(first);
		window.setScene(scene);
		
		System.out.println(xVal.size() + " :: " + xVal);
		System.out.println(yVal.size() + " :: " + yVal);
		
		timer = new AnimationTimer() {
			
			private long lastUpdate = 0;
			private long lastUpdate2 = 0;
			int strike = 0;
			
			@Override
			public void handle(long now) {			
				if(now - lastUpdate >= 1_000_000_000) {
					if(strike < 10) {
						List<Word> del = new ArrayList<Word>();
						
						
						
						for(Word w : words) {
							w.moveForward(); 
							
							if(w.getTranslateX() > 100) {
								w.setFresh(false);
								fresh.remove(w);
							}
							
							if(w.getTranslateX()>800) {
								strike++;
								del.add(w);	root.getChildren().remove(w);
								System.out.println("Strike: " +  strike);
							}
							
						} words.removeAll(del);
						
						lastUpdate = now;
					} else {
						root.getChildren().removeAll(words);
						timer.stop(); gameOver();
						System.out.println("rip");
					}
				}
				if(now - lastUpdate2 >= 10_000_000_000l && typed > 1) {

					for(int i=0; i<4; i++) {		
						if(fresh.size()+1 < 19) {
							Word word = createWord(strings, xVal_final, yVal_final, fresh);
							fresh.add(word); words.add(word);
							root.getChildren().add(word);
						}
					}
					lastUpdate2 = now;
				}
			}
			
		}; timer.start();

		Scenes.input.setOnKeyPressed(e -> {
			
			switch (e.getCode()) {
				
				case ENTER:
					
					if(Scenes.input.getText().equals("killmenow")) {
						timer.stop();
						gameOver();
					}
					
//					if(Scenes.input.getText().equals("add")) {
//						points += 100;
//						Scenes.points.setText(String.valueOf(points));
//						window.setScene(scene);
//					}
															
					List<Word> del = new ArrayList<Word>();
					List<Word> add = new ArrayList<Word>();
					
					for(Word w : words) {
						if(w.getValue().equals(Scenes.input.getText())) {
							
							typed++; points += w.getLength();
							
							fresh.remove(w); del.add(w); root.getChildren().remove(w);
							
							Scenes.points.setText(String.valueOf(points));
							window.setScene(scene);
							
							switch (typed) {
							
								case 1:
									for(int i=0; i<5; i++) {									
										if(fresh.size()+1 < 19) {
											Word word = createWord(strings, xVal_final, yVal_final, fresh);
											fresh.add(word); add.add(word);
											root.getChildren().add(word);
										}
									}
									
								break;
								
								case 2:
									for(int i=0; i<3; i++) {
										if(fresh.size()+1 < 19) {
											Word word = createWord(strings, xVal_final, yVal_final, fresh);
											fresh.add(word); add.add(word);
											root.getChildren().add(word);
										}
									}
								break;
								
								default:
									if(typed%6==0) {
										for(int i=0; i<4; i++) {											
											if(fresh.size()+1 < 19) {
												Word word = createWord(strings, xVal_final, yVal_final, fresh);
												fresh.add(word); add.add(word);
												root.getChildren().add(word);
											}
										}
									}
								break;
							}
						}
					}
					words.addAll(add); words.removeAll(del);
					Scenes.input.clear(); break;	// remove the typed word from active word list and clear text field
					
				default: break;
			}
		});
	}
	
	public static Word createWord(List<String> strings, List<Integer> xVal_final, List<Integer> yVal_final, List<Word> fresh) {
		
		if(xVal.size() < 1) xVal = new ArrayList<>(xVal_final);
		if(yVal.size() < 1) yVal = new ArrayList<>(yVal_final);
		
		Random random = new Random();

		int rndmx = random.nextInt(xVal.size());
		int rndmy = random.nextInt(yVal.size());
		
		String value = strings.get(random.nextInt(strings.size()));
		
		int y = yVal.get(rndmy); yVal.remove(rndmy);
		int x = xVal.get(rndmx); xVal.remove(rndmx);
		
		for(Word w : fresh)
			if(w.getTranslateY() == y)
				while(w.getTranslateX() <= (value.length()*8)+x+15)
					x -= 5;
		
		return new Word(x, y, value);
	}
	
	public static void main (String[] args) {
		launch(args);
	}

}
