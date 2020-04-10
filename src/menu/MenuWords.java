package menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MenuWords {
	
	static File[] listOfFiles;
	
	public static String[] loadDifficulties() {
		String[] diffs = {"Masturbating with one hand", "I have iOS autocorrection", "Kinda normal", "I wear fast glasses", "Asian"};
		return diffs;		
	}
	
	public static String[] loadLanguages() {
		
		String path = new File("").getAbsolutePath() + "\\words\\";		// get path of "words" directory

		if(!new File(path).exists()) {
			String d[] = path.split("\\\\");
			path="";
			for(int i=0; i<d.length-2; i++) path += d[i] + "\\";
			path += "words\\";
		}
		
		//System.out.println(path);
		File folder = new File(path);
		listOfFiles = folder.listFiles();	// get all available language files
		
		Arrays.sort(listOfFiles, Collections.reverseOrder());	// reverse them
		
		String[] lngs = new String[5];

		for(int i=0; i<listOfFiles.length; i++) {
			try (BufferedReader b = new BufferedReader(new FileReader(path + listOfFiles[i].getName()));){
				
				if(Selection.selected.contains(listOfFiles[i]))
					lngs[i] = " [ x ] "; else lngs[i] = " [   ] ";		// if selected add "x" in box else empty box
				
				lngs[i] += b.readLine();	// add language name from file's first line
					
			} catch (IOException e) {}
		}
		return lngs;		
	}
	
	public static List<String> loadWords(List<File> selected) {
		List<String> allWords = new ArrayList<String>();
		
		for(File f : selected) {
			try (BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));) {
				read.readLine();
				
				String s;
				while((s = read.readLine()) != null) {
					allWords.add(s);
				}
				
			} catch (FileNotFoundException e) {
				System.out.println("File error");
			} catch (UnsupportedEncodingException e) {} catch (IOException e) {
			}
		}
		//System.out.println(allWords);
		return allWords;
	}
	
	
}