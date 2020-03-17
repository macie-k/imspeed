package base;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import menu.Selection;
import menu.Words;


public class Window extends Application {
		
	public static Stage window;
	
	static int points=-1;
	private static List<Integer> xVal;
	private static List<Integer> yVal;
		
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		window = primaryStage;
		//window.getIcons().add(new Image("/path/to/stackoverflow.jpg"));
		
		Pane menu = new Pane(); menu.setPrefSize(800, 500);
		Scene scene = new Scene(menu, Color.web("#0f0f0f"));
		
		window.setScene(scene);
		window.setTitle("I'm speed");
		window.setResizable(false);
		window.show();
		
		Selection.select(scene, menu, window);	// run menu scene
	}
	
	public static void startGame(List<File> selected) {
		
		Pane root = new Pane(); root.setPrefSize(800, 500);
		Scene scene = Scenes.game(root);
		Random random = new Random();
		
		List<String> strings = Words.loadWords(selected);	// list of all word-strings combined
		
		Word first = new Word(0, 195, "type-me");		// first trial word
		List<Word> words = new ArrayList<Word>();	// list of avctive words
		words.add(first);
						
		root.getChildren().add(first);
		window.setScene(scene);
		
		final List<Integer> xVal_final = new ArrayList<Integer>();
		final List<Integer> yVal_final = new ArrayList<Integer>();
		
		for(int i=-15; i<20; i+=5) { xVal_final.add(i); }
		for(int i=20; i<400; i+=20) { yVal_final.add(i); }
		
		xVal = new ArrayList<Integer>(xVal_final);
		yVal = new ArrayList<Integer>(yVal_final);
		
		System.out.println(xVal.size() + " :: " + xVal);
		System.out.println(yVal.size() + " :: " + yVal);
		
		AnimationTimer timer = new AnimationTimer() {
			
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
							if(w.getTranslateX()>800) {
								strike++;
								del.add(w);	root.getChildren().remove(w);
								System.out.println("Strike: " +  strike);
							}
						} words.removeAll(del);
						
						lastUpdate = now;
					} else {
						System.out.println("rip");
					}
				}
				if(now - lastUpdate2 >= 10_000_000_000l && points > 1) {
					checkIfEnough(4, xVal_final, yVal_final);
					
					for(int i=0; i<4; i++) {		
						int rndmx = random.nextInt(xVal.size());
						int rndmy = random.nextInt(yVal.size());
						
						int x = xVal.get(rndmx); xVal.remove(rndmx);
						int y = yVal.get(rndmy); yVal.remove(rndmy);
						String value = strings.get(random.nextInt(strings.size()));
						
						Word word = new Word(x, y, value);
						
						words.add(word);
						root.getChildren().add(word);
					}
					lastUpdate2 = now;
				}
			}
			
		}; timer.start();

		Scenes.input.setOnKeyPressed(e -> {
			
			switch (e.getCode()) {
				
				case ENTER:
															
					List<Word> del = new ArrayList<Word>();
					List<Word> add = new ArrayList<Word>();
					
					for(Word w : words) {
						if(w.getValue().equals(Scenes.input.getText())) {
							points++;
							Scenes.points.setText(String.valueOf(points));
							del.add(w);		// add to list to remove after iteration
							root.getChildren().remove(w);	// remove from scene
							window.setScene(scene);		// reload the scene
							
							switch (points) {
							
								case 0:
									for(int i=0; i<5; i++) {									
										int rndmx = random.nextInt(xVal.size());
										int rndmy = random.nextInt(yVal.size());
										
										int x = xVal.get(rndmx); xVal.remove(rndmx);
										int y = yVal.get(rndmy); yVal.remove(rndmy);
										String value = strings.get(random.nextInt(strings.size()));
										
										Word word = new Word(x, y, value);
										
										add.add(word);
										root.getChildren().add(word);
									}
									
								break;
								
								case 1:
									for(int i=0; i<3; i++) {
										checkIfEnough(3, xVal_final, yVal_final);
										
										int rndmx = random.nextInt(xVal.size());
										int rndmy = random.nextInt(yVal.size());
										
										int x = xVal.get(rndmx); xVal.remove(rndmx);
										int y = yVal.get(rndmy); yVal.remove(rndmy);
										String value = strings.get(random.nextInt(strings.size()));
										
										Word word = new Word(x, y, value);

										add.add(word);
										root.getChildren().add(word);
									}
								break;
								
								default:
									if(points%6==0) {
										checkIfEnough(3, xVal_final, yVal_final);
										
										for(int i=0; i<3; i++) {
											
											int rndmx = random.nextInt(xVal.size());
											int rndmy = random.nextInt(yVal.size());
											
											int x = xVal.get(rndmx); xVal.remove(rndmx);
											int y = yVal.get(rndmy); yVal.remove(rndmy);
											
											String value = strings.get(random.nextInt(strings.size()));
											
											Word word = new Word(x, y, value);
											
											
											
											add.add(word);
											root.getChildren().add(word);
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
		
	public static void checkIfEnough(int x, List<Integer> xVal_final, List<Integer> yVal_final) {
		if(xVal.size() < x) xVal = new ArrayList<>(xVal_final);
		if(yVal.size() < x) yVal = new ArrayList<>(yVal_final);
	}
	
	public static void main (String[] args) {
		launch(args);
	}

}
