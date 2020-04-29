package menu;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import base.Window;

public class Selection {
	
	public static List<File> selected = new ArrayList<File>();
	public static List<String> selectedNames = new ArrayList<String>();
	
	private static int x=0;
	private static Stage window = Window.window;
	
	/* set difficulty */
	public static void selectDifficulty(Pane root) {
		
		x=0;
		Scene scene = new Scene(root);
		root.setStyle("-fx-background-color: rgb(14, 14, 14)");
		
		Text header = new Text("DIFFICULTY");
		header.setTranslateX(143); header.setTranslateY(130); header.setFill(Color.WHITE);
		header.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 50;");
		
		MenuOption[] diff = new MenuOption[5];		
		for(int i=0; i<5; i++) {
			int calcY = 220 + 25*i;
			diff[i] = new MenuOption(calcY, MenuWords.loadDifficulties()[i], "diff", x==i);
		}
		
		root.getChildren().add(header); root.getChildren().addAll(diff);
		window.setScene(scene);
		
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			
				case UP: if(x>0) x--;
					
					setScene(root, scene, "diff", diff);
					break;
					
				case DOWN: if(x<4) x++;
					setScene(root, scene, "diff", diff);
					break;
					
				case ENTER:
					Window.DIFFICULTY = x+1;
					Window.setLang();
					break;
					
				default: break;			
			}
			
		});
	}

	/* select languages */
	public static void selectLanguage(Pane root) {
		
		x=0; selected.clear(); selectedNames.clear();
		Scene scene = new Scene(root);
		root.setStyle("-fx-background-color: rgb(14, 14, 14)");
		
		Text header = new Text("LANGUAGES");
		header.setTranslateX(157); header.setTranslateY(130);header.setFill(Color.WHITE);
		header.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 50;");
		
		/* load all available languages */
		MenuOption[] lngs = new MenuOption[5];
		for(int i=0; i<5; i++) {
			int calcY = 220 + 25*i;
			lngs[i] = new MenuOption(calcY, MenuWords.loadLanguages()[i], "lng", x==i);
		}
		
		root.getChildren().add(header); root.getChildren().addAll(lngs);
		window.setScene(scene);
		
		/* menu movement key listener */
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			
				case ESCAPE: x=0;
					Window.setDiff();
					break;
			
				case UP: if(x>0) x--;
					setScene(root, scene, "lng", lngs);
					break;
					
				case DOWN: if(x<4) x++;
					setScene(root, scene, "lng", lngs);
					break;
					
				case SPACE:
					if(selected.contains(MenuWords.listOfFiles[x]))	{
						selected.remove(MenuWords.listOfFiles[x]);
						selectedNames.remove(MenuWords.lngsNames.get(x));
					} else {
						if(!isEmpty(x)) {
							selectedNames.add(MenuWords.lngsNames.get(x));
							selected.add(MenuWords.listOfFiles[x]);
						}
					}
					setScene(root, scene, "lng", lngs);
					break;
					
				case ENTER:
					if(!selected.contains(MenuWords.listOfFiles[x])) {
						if(!isEmpty(x)) {
							selectedNames.add(MenuWords.lngsNames.get(x));
							selected.add(MenuWords.listOfFiles[x]);
						}
					}
					
					setScene(root, scene, "lng", lngs);
					
					if(!MenuWords.loadWords(selected).isEmpty()) {
						Window.startGame(selected);
					}
					break;
					
				default: break;			
			}
			
		});
	}
	
	/* method for refreshing the view */
	static void setScene(Pane root, Scene scene, String type, MenuOption[] option) {
		
		root.getChildren().removeAll(option);
		
		if(type.equals("lng")) {
			for(int i=0; i<5; i++) {
				int calcY = 220 + 25*i;
				option[i] = new MenuOption(calcY, MenuWords.loadLanguages()[i], "lng", i==x);
			}
		}
		
		if(type.equals("diff")) {
			for(int i=0; i<5; i++) {
				int calcY = 220 + 25*i;
				option[i] = new MenuOption(calcY, MenuWords.loadDifficulties()[i], "diff", i==x);
			}
		}
		
		root.getChildren().addAll(option);
		window.setScene(scene);
	}
	
	static boolean isEmpty(int x) {
		try {
			if(Files.lines(Paths.get(MenuWords.listOfFiles[x].toString())).count() > 1) {
				return false;				
			} else {
				return true;
			}
		} catch (IOException er) {
			System.out.println(er);
			return true;
		}
	}
}