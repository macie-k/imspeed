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
	
	public static List<File> selected_lng_files= new ArrayList<File>();
	public static List<String> selected_lng_names = new ArrayList<String>();
	
	public static boolean disable_move = false;
	public static int[] x = {4, 4, 4};
	
	private static int row=0;
	private static Stage window = Window.window;
	private static int how_many_lngs;
	
	private static String[] loadedDifficulties;

	
	/* set difficulty */
	public static void selectDifficulty() {
		
		row=0;
		
		Pane root = Scenes.selectMenu("DIFFICULTY");
		Scene scene = new Scene(root);
		
		loadedDifficulties = Words.loadDifficulties();
		
		Option[] diff = new Option[5];		
		for(int i=0; i<4; i++) {
			int calcY = 220 + 25*i;
			diff[i] = new Option(calcY, loadedDifficulties[i], row==i);
		} diff[4] = new Option(345, loadedDifficulties[4], row==4);
		
		root.getChildren().addAll(diff);			
		window.setScene(scene);
		
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case UP:
				if(row>0) {
					row--;
				} else {
					row = 4;
				}
				setScene(root, scene, "diff", diff);
				break;
				
			case DOWN:
				if(row<4) {
					row++;
				} else {
					row = 0;
				}
				setScene(root, scene, "diff", diff);
				break;
				
			case ENTER:
				Window.DIFFICULTY = row+1;
				System.out.println("[OK] Difficulty: " + loadedDifficulties[row]);
				if(row==4) {
					customDifficulty();
				} else {
					selectLanguage(false);
				}
				break;
				
			default: break;			
			}
			
		});
	}

	/* custom difficulty selection */
	public static void customDifficulty() {
		
		row=0; x[0] = 4; x[1]=4; x[2]=4;
		Scenes.pointer.setTranslateY(0);
		
		Pane root = Scenes.selectMenu("CUSTOM");
		Scene scene = new Scene(root);							
				
		window.setScene(scene);
		
		scene.setOnKeyPressed(e -> {
			
			double arrowY = Scenes.pointer.getTranslateY();
			
			switch (e.getCode()) {
			case UP:
				if(row-->0) {
					Scenes.pointer.setTranslateY(arrowY - 30);
				} else {
					row = 2;
					Scenes.pointer.setTranslateY(arrowY + 60);
				}
				break;
				
			case DOWN:
				if(row++<2) {
					Scenes.pointer.setTranslateY(arrowY + 30);
				} else {
					row = 0;
					Scenes.pointer.setTranslateY(arrowY - 60);
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
				Window.DIFFICULTY = 5;
				int hF = x[0]+1; int hO = x[1]+1; int hM = x[2]+1;
				System.out.println("[OK] Difficulty: Custom [" + hF + ":" + hO + ":" + hM + "]");
				parseCustom(hF, hO, hM);
				selectLanguage(true);
				break;
				
			default: break;			
			}
			
		});		
	}
	
	static void parseCustom(int howFast, int howOften, int howMany) {
		Window.howMany = howMany;
		Window.howFast = 1_650_000_000 - 150_000_000*howFast;
		Window.howOften = 8_000_000_000l - 500_000_000*howOften;
		Window.multiplierAdd = 0.05 + ((howFast-5) + (howOften-5) + (howMany-5))/100;
		
//		alternative version 
//		if(howFast >= 5) {
//			Window.howFast = 1_125_000_000l - 75_000_000*howFast;
//		}
	}
	
	/* select languages */
	public static void selectLanguage(boolean custom) {
		
		// reset stuff
		selected_lng_files.clear();
		selected_lng_names.clear();
		row=0;
					
		String[][] loadedLanguages = Words.loadLanguages();
		
		if(loadedLanguages == null) {
			Window.error("MISSING_WORDS");
			return;
		}
		
		Pane root = Scenes.selectMenu("LANGUAGES");		
		Scene scene = new Scene(root);
		
		how_many_lngs = Words.how_many_lngs;	// redefine variable to avoid `0`
		
		/* load all available languages */
		Option[] lngs = new Option[how_many_lngs];
		for(int i=0; i<how_many_lngs; i++) {
			int calcY = 220 + 25*i;
			lngs[i] = new Option(calcY, loadedLanguages[i][0], loadedLanguages[i][1], row==i);
		}
		
		root.getChildren().addAll(lngs);
		window.setScene(scene);
				
		/* menu movement key listener */
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {		
			case UP:
				if(row>0) {
					row--;
				} else {
					row = how_many_lngs-1;
				}
				setScene(root, scene, "lng", lngs);
				break;
				
			case DOWN:
				if(row<how_many_lngs-1) {
					row++;
				} else {
					row = 0;
				}
				setScene(root, scene, "lng", lngs);
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
				setScene(root, scene, "lng", lngs);
				break;
				
			case ENTER:
				if(!selected_lng_files.contains(Words.listOfFiles[row]) && selected_lng_files.size() == 0) {
					if(!isEmpty(row)) {
						selected_lng_names.add(Words.lngsNames.get(row));
						selected_lng_files.add(Words.listOfFiles[row]);
					}
				}
				
				setScene(root, scene, "lng", lngs);
				
				if(!Words.loadWords(selected_lng_files).isEmpty()) {
						System.out.print("[OK] Languages: ");
						selected_lng_names.forEach(sln -> System.out.print("{" + sln + "} ")); System.out.println("\n");
					Window.startGame(selected_lng_files);
				}
				break;
				
			case ESCAPE: 
				if(custom) {
					customDifficulty();
				} else {
					selectDifficulty();
				}
				break;
			default: break;			
			}
		});
	}
	
	/* method for refreshing the view // kind of uneccesary */
	static void setScene(Pane root, Scene scene, String type, Option[] option) {
		
		root.getChildren().removeAll(option);
		
		switch(type) {
		case "diff":
			for(int i=0; i<4; i++) {
				int calcY = 220 + 25*i;
				option[i] = new Option(calcY, loadedDifficulties[i], i==row);
			}
			
			option[4] = new Option(345, loadedDifficulties[4], 4==row);
		break;
		
		case "lng":
			for(int i=0; i<how_many_lngs; i++) {
				int calcY = 220 + 25*i;
				option[i] = new Option(calcY, Words.loadLanguages()[i][0], Words.loadLanguages()[i][1], i==row);
			}
		break;
		}
				
		root.getChildren().addAll(option);
		window.setScene(scene);
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