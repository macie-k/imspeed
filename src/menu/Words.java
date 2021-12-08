package menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import app.Log;

public class Words {
	
	public static final List<String> lngsNames = new ArrayList<String>();
	
	static File[] listOfFiles;
	public static int how_many_lngs;
	
	public static String[] loadDifficulties() {
		return new String[] {"Easy", "Normal", "Hard", "Asian", "Custom"};
	}
	
	public static String[][] loadLanguages() {
		
		String path = new File("").getAbsolutePath() + "/words/";		
		File folder = new File(path);	
			
		if(folder.listFiles() != null) {
			
			listOfFiles = folder.listFiles();	// get all available language files
			Arrays.sort(listOfFiles, Collections.reverseOrder());	// reverse them
			
			if(listOfFiles.length > 8) {
				how_many_lngs = 8;
			} else if(listOfFiles.length < 1) {
				how_many_lngs = 1;
			} else {
				how_many_lngs = listOfFiles.length;
			}
			
			String[][] lngs = new String[how_many_lngs][2];

			for(int i=0; i<how_many_lngs; i++) {
				try (BufferedReader b = new BufferedReader(new FileReader(path + listOfFiles[i].getName()))){
					
					String s = b.readLine();
					lngsNames.add(s);
					
					lngs[i][0] = Select.selected_lng_files.contains(listOfFiles[i]) ? "[ x ]" : "[   ]";	// if selected add "x" in box else empty box
					lngs[i][1] = s;	// add language name from file's first line					
						
				} catch (IOException e) {
					Log.error("\n" + e);
				}
			}
			return lngs;	
		} else {
			return null;
		}
	
	}
	
	public static List<String> loadWords(List<File> selected) {
		List<String> allWords = new ArrayList<String>();
		
		for(File f : selected) {
			try (BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"))) {
				read.readLine();
				
				String s;
				while((s = read.readLine()) != null) {
					allWords.add(s);
				}
				
			} catch (IOException e) {
				Log.error("\n" + e);
			}
		}
		return allWords;
	}
}