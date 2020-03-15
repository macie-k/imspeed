package menu;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Selection {
	
	private static int x=0;
	private static Languages[] lngs = new Languages[5];
	static List<File> selected = new ArrayList<File>();

	public static void select(Scene scene, Pane root, Stage stage) {
		
		/* load all available languages */
		for(int i=0; i<5; i++) {
			int calcY = 240 + 20*i;
			lngs[i] = new Languages(calcY, Words.loadLanguages(x)[i], x==i);
		}
		
		root.getChildren().addAll(lngs);
		stage.setScene(scene);
		
		/* menu movement key listener */
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			
				case UP: if(x>0) x--;
					root.getChildren().removeAll(lngs);
					break;
					
				case DOWN: if(x<4) x++;
					root.getChildren().removeAll(lngs);
					break;
					
				case SPACE:
					if(selected.contains(Words.listOfFiles[x]))	selected.remove(Words.listOfFiles[x]);
					else selected.add(Words.listOfFiles[x]);
					break;
					
				case ENTER:
					root.getChildren().removeAll(lngs);
					Words.loadWords(selected);
					break;
					
				default: break;			
			}
			
			for(int i=0; i<5; i++) {
				int calcY = 240 + 20*i;
				lngs[i] = new Languages(calcY, Words.loadLanguages(x)[i], i==x);
			}
			
			root.getChildren().addAll(lngs);
			stage.setScene(scene);
		});
	}
}
