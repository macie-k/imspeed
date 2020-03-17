package menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Words {
	
	static File[] listOfFiles;
	
	public static String[] loadLanguages(int x) {
		
		String path = new File("").getAbsolutePath() + "\\words\\";		// get path of "words" directory
		//System.out.println(path);
		File folder = new File(path);
		listOfFiles = folder.listFiles();	// get all available language files
		
		Arrays.sort(listOfFiles, Collections.reverseOrder());	// reverse them
		
		String[] lngs = new String[5];

		for(int i=0; i<listOfFiles.length; i++) {
			try (BufferedReader b = new BufferedReader(new FileReader(path + listOfFiles[i].getName()));){
				
				if(Selection.selected.contains(Words.listOfFiles[i]))
					lngs[i] = " [ x ] "; else lngs[i] = " [   ] ";		// if selected add "x" in box else empty box
				
				lngs[i] += b.readLine();	// add language name from file's first line
					
			} catch (IOException e) {}
		}
		return lngs;		
	}
	
	public static List<String> loadWords(List<File> selected) {
		List<String> allWords = new ArrayList<String>();
		
		for(File f : selected) {
			try (Scanner read = new Scanner(f);) {
						
				read.nextLine();
				while(read.hasNext()) {
					allWords.add(read.nextLine());
				}
				
			} catch (FileNotFoundException e) {
				System.out.println("File error");
			}
		}
		//System.out.println(allWords);
		return allWords;
	}
	
	
}
