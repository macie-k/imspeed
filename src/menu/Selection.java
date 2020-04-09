package menu;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import base.Window;

public class Selection {
	
	private static int x=0;
	static List<File> selected = new ArrayList<File>();
	private static Stage window = Window.window;
	
	public static void selectDifficulty(Pane root) {
		
		x=0;
		Scene scene = new Scene(root);
		root.setStyle("-fx-background-color: rgb(15,15,15)");
		
		Text header = new Text("DIFFICULTY");
		header.setTranslateX(143); header.setTranslateY(130); header.setFill(Color.WHITE);
			
		Font.loadFont("https://kazmierczyk.me/styles/fonts/Kyrou%207%20Wide%20Bold.ttf", 14);
		header.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 50;");
		
		MenuOption[] diff = new MenuOption[5];		
		for(int i=0; i<5; i++) {
			int calcY = 220 + 25*i;
			diff[i] = new MenuOption(calcY, MenuWords.loadDifficulties(x)[i], "diff", x==i);
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

	public static void selectLanguage(Pane root) {
		
		x=0; selected.clear();
		Scene scene = new Scene(root);
		root.setStyle("-fx-background-color: rgb(15,15,15)");
		
		Text header = new Text("LANGUAGES");
		header.setTranslateX(157); header.setTranslateY(130);header.setFill(Color.WHITE);
		
		Font.loadFont("https://kazmierczyk.me/styles/fonts/Kyrou%207%20Wide%20Bold.ttf", 14);		
		header.setStyle("-fx-font-family: 'Grixel Kyrou 7 Wide Bold'; -fx-font-size: 50;");
		
		/* load all available languages */
		MenuOption[] lngs = new MenuOption[5];
		for(int i=0; i<5; i++) {
			int calcY = 220 + 25*i;
			lngs[i] = new MenuOption(calcY, MenuWords.loadLanguages(x)[i], "lng", x==i);
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
					if(selected.contains(MenuWords.listOfFiles[x]))	selected.remove(MenuWords.listOfFiles[x]);
					else selected.add(MenuWords.listOfFiles[x]);
					setScene(root, scene, "lng", lngs);
					break;
					
				case ENTER:
					if(!selected.contains(MenuWords.listOfFiles[x])) selected.add(MenuWords.listOfFiles[x]);
					setScene(root, scene, "lng", lngs);
					if(!MenuWords.loadWords(selected).isEmpty()) Window.startGame(selected);
					break;
					
				default: break;			
			}
			
		});
	}
	
	public static void setScene(Pane root, Scene scene, String type, MenuOption[] option) {
		
		root.getChildren().removeAll(option);
		
		if(type.equals("lng")) {
			for(int i=0; i<5; i++) {
				int calcY = 220 + 25*i;
				option[i] = new MenuOption(calcY, MenuWords.loadLanguages(x)[i], "lng", i==x);
			}
		} if(type.equals("diff")) {
			for(int i=0; i<5; i++) {
				int calcY = 220 + 25*i;
				option[i] = new MenuOption(calcY, MenuWords.loadDifficulties(x)[i], "diff", i==x);
			}
		}
		
		root.getChildren().addAll(option);
		window.setScene(scene);
	}
}