package menu;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import menu.obj.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import base.Window;

public class Select {
	
	public static List<File> selected_lng_files= new ArrayList<File>();
	public static List<String> selected_lng_names = new ArrayList<String>();
	
	private static int x=0;
	private static Stage window = Window.window;
	private static int how_many_lngs;
	
	private static String[][] loadedLanguages = Words.loadLanguages();
	private static String[] loadedDifficulties= Words.loadDifficulties();
	
	
	/* set difficulty */
	public static void selectDifficulty(Pane root) {
		
		x=0;
		root.setStyle("-fx-background-color: rgb(14, 14, 14)");
		Scene scene = new Scene(root);
	
		Text header = new Text("DIFFICULTY");
			header.setTranslateX(143);
			header.setTranslateY(130);
			header.setFill(Color.WHITE);
			header.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 50;");
		
		Option[] diff = new Option[5];		
		for(int i=0; i<5; i++) {
			int calcY = 220 + 25*i;
			diff[i] = new Option(calcY, loadedDifficulties[i], x==i);
		}
		
		root.getChildren().add(header);
		root.getChildren().addAll(diff);
		window.setScene(scene);
		
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			
				case UP:
					if(x>0) {
						x--;
					} else {
						x = 4;
					}
					setScene(root, scene, "diff", diff);
					break;
					
				case DOWN:
					if(x<4) {
						x++;
					} else {
						x = 0;
					}
					setScene(root, scene, "diff", diff);
					break;
					
				case ENTER:
					Window.DIFFICULTY = x+1;
					System.out.println("[OK] Difficulty: " + loadedDifficulties[x]);
					Window.setLang();
					break;
					
				default: break;			
			}
			
		});
	}

	/* select languages */
	public static void selectLanguage(Pane root) {
		
		// reset stuff
		selected_lng_files.clear();
		selected_lng_names.clear();
		x=0;
				
		Scene scene = new Scene(root);
			root.setStyle("-fx-background-color: rgb(14, 14, 14)");
		
		Text header = new Text("LANGUAGES");
			header.setTranslateX(157); header.setTranslateY(130);header.setFill(Color.WHITE);
			header.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 50;");

		how_many_lngs = Words.how_many_lngs;	// redefine variable to avoid `0`
		
		/* load all available languages */
		Option[] lngs = new Option[how_many_lngs];
		for(int i=0; i<how_many_lngs; i++) {
			int calcY = 220 + 25*i;
			lngs[i] = new Option(calcY, loadedLanguages[0][i], loadedLanguages[1][i], x==i);
		}
		
		root.getChildren().add(header);
		root.getChildren().addAll(lngs);
		window.setScene(scene);
		
		/* menu movement key listener */
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			
				case ESCAPE: x=0;
					Window.setDiff();
					break;
			
				case UP:
					if(x>0) {
						x--;
					} else {
						x = how_many_lngs-1;
					}
					setScene(root, scene, "lng", lngs);
					break;
					
				case DOWN:
					if(x<how_many_lngs-1) {
						x++;
					} else {
						x = 0;
					}
					setScene(root, scene, "lng", lngs);
					break;
					
				case SPACE:
					if(selected_lng_files.contains(Words.listOfFiles[x])) {
						selected_lng_files.remove(Words.listOfFiles[x]);
						selected_lng_names.remove(Words.lngsNames.get(x));
					} else {
						if(!isEmpty(x)) {
							selected_lng_names.add(Words.lngsNames.get(x));
							selected_lng_files.add(Words.listOfFiles[x]);
						}
					}
					setScene(root, scene, "lng", lngs);
					break;
					
				case ENTER:
					if(!selected_lng_files.contains(Words.listOfFiles[x]) && selected_lng_files.size() == 0) {
						if(!isEmpty(x)) {
							selected_lng_names.add(Words.lngsNames.get(x));
							selected_lng_files.add(Words.listOfFiles[x]);
						}
					}
					
					setScene(root, scene, "lng", lngs);
					
					if(!Words.loadWords(selected_lng_files).isEmpty()) {
							System.out.print("[OK] Languages: ");
							selected_lng_names.forEach(slf -> System.out.print("{" + slf + "} ")); System.out.println("\n");
						Window.startGame(selected_lng_files);
					}
					break;
					
				default: break;			
			}
		});
	}
	
	/* method for refreshing the view */
	static void setScene(Pane root, Scene scene, String type, Option[] option) {
		
		root.getChildren().removeAll(option);
		
		if(type.equals("lng")) {
			for(int i=0; i<how_many_lngs; i++) {
				int calcY = 220 + 25*i;
				option[i] = new Option(calcY, Words.loadLanguages()[0][i], Words.loadLanguages()[1][i], i==x);
			}
		}
		
		if(type.equals("diff")) {
			for(int i=0; i<5; i++) {
				int calcY = 220 + 25*i;
				option[i] = new Option(calcY, loadedDifficulties[i], i==x);
			}
		}
		
		root.getChildren().addAll(option);
		window.setScene(scene);
	}
	
	static boolean isEmpty(int x) {
		try {
			if(Files.lines(Paths.get(Words.listOfFiles[x].toString())).count() > 1) {
				return false;				
			} else {
				return true;
			}
		} catch (IOException er) {
			System.out.println("[ERROR] Could not read file: " + er);
			return true;
		}
	}
}