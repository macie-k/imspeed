package menu;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import menu.obj.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import base.Scenes;
import base.Window;

public class Select {
	
	public static final List<File> selected_lng_files = new ArrayList<File>();
	public static final List<String> selected_lng_names = new ArrayList<String>();
	
	public static int[] x = {4, 4, 4, 4};
	
	private static int row;
	private static int how_many_lngs;
	
	private final static Stage window = Window.window;

	
	public static void selectGamemode() {
		row = 0; setHighlight(Scenes.gamemodes);
		
		Pane root = Scenes.selectMenu("GAMEMODE");
		Scene scene = new Scene(root);		

		window.setScene(scene);
		
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
				case UP:
					row = (row > 0) ? row-1 : 1;
					setHighlight(Scenes.gamemodes);
					break;
					
				case DOWN:
					row = (row < 1) ? row+1 : 0;
					setHighlight(Scenes.gamemodes);
					break;
					
				case ENTER:
					Window.gameMode = row;
					System.out.println("[OK] Gamemode: " + Scenes.gamemodes[row].getValue());
					Select.selectDifficulty();
					break;
					
				default: break;			
			}
		});
	}
	
	/* set difficulty */
	public static void selectDifficulty() {
		
		row=0;
		
		Pane root = Scenes.selectMenu("DIFFICULTY");
		Scene scene = new Scene(root);		

		window.setScene(scene);
		
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
				case UP:
					row = (row > 0) ? row-1 : 4;
					setHighlight(Scenes.difficulties);
					break;
					
				case DOWN:
					row = (row < 4) ? row+1 : 0;
					setHighlight(Scenes.difficulties);
					break;
					
				case ENTER:
					Window.gameDifficulty = row+1;
					if(row == 4) {
						customDifficulty(false);
					} else {
						System.out.println("[OK] Difficulty: " + Scenes.loadedDifficulties[row]);
						selectLanguage(false);
					}
					break;
					
				case ESCAPE:
					selectGamemode();
					break;
					
				default: break;			
			}
			
		});
	}
	
	/* custom difficulty selection */
	public static void customDifficulty(boolean infinite) {
		
		row=0; 
		Scenes.pointer.setTranslateY(0);
		
		Pane root = Scenes.selectMenu("CUSTOM");
		Scene scene = new Scene(root);		
		
		int amount = Window.gameMode == 0 ? 3 : 4;
		for(int i=0; i<amount; i++) {
			for(int j=0; j<=x[i]; j++) {
				Scenes.scales[i][j].setFilled(true);
			}
		}
				
		window.setScene(scene);
		
		scene.setOnKeyPressed(e -> {
						
			double arrowY = Scenes.pointer.getTranslateY();
			
			switch (e.getCode()) {
				case UP:
					if(row-->0) {
						Scenes.pointer.setTranslateY(arrowY - 30);
					} else {
						row = amount-1;
						Scenes.pointer.setTranslateY(arrowY + (amount-1)*30);
					}
					break;
					
				case DOWN:
					if(row++<amount-1) {
						Scenes.pointer.setTranslateY(arrowY + 30);
					} else {
						row = 0;
						Scenes.pointer.setTranslateY(arrowY - (amount-1)*30);
					}
					break;
					
				case RIGHT:
					if((x[row]+1)<10) {
						Scenes.scales[row][++x[row]].setFilled(true);
					}
					break;
				
				case LEFT:
					if(x[row]>0) {
						Scenes.scales[row][x[row]--].setFilled(false);
					}
					break;
					
				case ESCAPE:
					selectDifficulty();
					break;
					
				case ENTER:
					Window.gameDifficulty = 5;
					
					int hF = x[0]+1; int hO = x[1]+1; int hM = x[2]+1; int tL = x[3]+1;
					System.out.println("[OK] Difficulty: Custom ["
																+ hF + ":"
																+ hO + ":"
																+ hM
																+ (Window.gameMode == 0 ? "" : ":"+tL)
																+ "]"
																+ ((infinite) ? " :: Infinite" : ""));
					
					parseCustom(hF, hO, hM, tL);
					selectLanguage(true);
					break;
					
				default: break;			
			}
			
		});		
	}
	
	static void parseCustom(int howFast, int howOften, int howMany, int timeLeft) {
		Window.howMany = howMany;
		Window.howFast = 1_650_000_000 - 150_000_000*howFast;
		Window.howOften = 8_000_000_000l - 500_000_000*howOften;
		Window.multiplierAdd = 0.05 + ((howFast-5) + (howOften-5) + (howMany-5))/100;
		Window.timeLeft = 5*timeLeft;
	}
	
	/* select languages */
	public static void selectLanguage(boolean custom) {
		
		// reset stuff
		selected_lng_files.clear();
		selected_lng_names.clear();
		row=0;
					
		if(Words.loadLanguages() == null) {
			Window.error("MISSING_WORDS");
			return;
		}
		
		Pane root = Scenes.selectMenu("LANGUAGES");		
		Scene scene = new Scene(root);
		
		window.setScene(scene);
		how_many_lngs = Words.how_many_lngs;	// redefine variable to avoid `0`
				
		/* menu movement key listener */
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {		
				case UP:
					row = (row > 0) ? row-1 : how_many_lngs-1;
					setHighlight(Scenes.lngs);
					break;
					
				case DOWN:
					row = (row < how_many_lngs-1) ? row+1 : 0;
					setHighlight(Scenes.lngs);
					break;
					
				case SPACE:
					if(selected_lng_files.contains(Words.listOfFiles[row])) {
						selected_lng_files.remove(Words.listOfFiles[row]);
						selected_lng_names.remove(Words.lngsNames.get(row));
					} else {
						if(!isEmpty(row)) {
							selected_lng_names.add(Words.lngsNames.get(row));
							selected_lng_files.add(Words.listOfFiles[row]);
						}
					}
					setCheck();
					break;
					
				case ENTER:
					if(!selected_lng_files.contains(Words.listOfFiles[row]) && selected_lng_files.size() == 0) {
						if(!isEmpty(row)) {
							selected_lng_names.add(Words.lngsNames.get(row));
							selected_lng_files.add(Words.listOfFiles[row]);
						}
					}
									
					if(!Words.loadWords(selected_lng_files).isEmpty()) {
							System.out.print("[OK] Languages: ");
							selected_lng_names.forEach(sln -> System.out.print("{" + sln + "} ")); System.out.println();
						Window.startGame(selected_lng_files);
					}
					break;
					
				case ESCAPE:
					if(custom) {
						customDifficulty(Window.infinite);
					} else {
						selectDifficulty();
					}
					break;
				default: break;			
			}
			
		});
	}
	
	static void setCheck() {
		Scenes.lngs[row].setChecked(!Scenes.lngs[row].getChecked());
	}
	
	static void setHighlight(Option[] tab) {
		int index = 0;
		for (Option o : tab) {
			o.setHighlighted(index++ == row);
		}
	}
		
	static boolean isEmpty(int y) {
		try {
			return !(Files.lines(Paths.get(Words.listOfFiles[y].toString())).count() > 1);
		} catch (IOException e) {
			System.err.println("[ERROR] Could not read file: " + e);
			return true;
		}
	}
}