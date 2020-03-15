package menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Words {
	
	static File[] listOfFiles;
	
	public static String[] loadLanguages(int x) {
		
		String path = new File("").getAbsolutePath() + "\\words\\";		// get path of "words" directory
		File folder = new File(path);
		listOfFiles = folder.listFiles();	// get all available language files
		
		Arrays.sort(listOfFiles, Collections.reverseOrder());	// reverse them
		
		String[] lngs = new String[5];

		for(int i=0; i<listOfFiles.length; i++) {
			try (BufferedReader b = new BufferedReader(new FileReader(path + listOfFiles[i].getName()));){
				
				if(Selection.selected.contains(Words.listOfFiles[i]))
					lngs[i] = " [ x ] "; else lngs[i] = " [   ] ";		// if selected add "x" in box else box alone
				
				lngs[i] += b.readLine();	// add language name from file's first line
					
			} catch (IOException e) {}
		}
		return lngs;		
	}
	
	public static void loadWords(List<File> selected) {
		
	}
	
	
}
