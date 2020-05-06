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

import base.Window;

public class Words {
	
	public static List<String> lngsNames = new ArrayList<String>();
	
	static File[] listOfFiles;
	static String slash = Window.slash;
	
	public static String[] loadDifficulties() {
		//String[] diffs = {"Masturbating with one hand", "I have iOS autocorrection", "Kinda normal", "I wear fast glasses", "Asian"};
		String[] diffs = {"Very easy", "Easy", "Normal", "Hard", "Asian"};
		return diffs;		
	}
	
	public static String[] loadLanguages() {
		
		String path = new File("").getAbsolutePath() + slash + "words" + slash;
		String splitslash = slash;
		
		if(Window.OS.equals("windows")) {
			splitslash = "\\\\";
		}
		if(!new File(path).exists()) {
			String d[] = path.split(splitslash);
			path="";
			for(int i=0; i<d.length-2; i++) {
				path += d[i] + slash;
			}
			path += "words" + slash;
		}
		
		//System.out.println(path);
		File folder = new File(path);
		listOfFiles = folder.listFiles();	// get all available language files
		
		Arrays.sort(listOfFiles, Collections.reverseOrder());	// reverse them
		
		String[] lngs = new String[5];

		for(int i=0; i<listOfFiles.length; i++) {
			try (BufferedReader b = new BufferedReader(new FileReader(path + listOfFiles[i].getName()))){
				
				String s = b.readLine();
				
				if(Select.selected_lng_files.contains(listOfFiles[i])) {
					lngs[i] = " [ x ] "; 	// if selected add "x" in box else empty box
				} else {
					lngs[i] = " [   ] ";		
				}
				
				lngsNames.add(s);
				lngs[i] += s;	// add language name from file's first line
					
			} catch (IOException e) {
				System.out.println("\n[ERROR] " + e);
			}
		}
		return lngs;		
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
				System.out.println("\n[Error] " + e);
			}
		}
		return allWords;
	}
}