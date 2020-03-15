package menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class Words {
	
	public static String[] loadWords(int x) {
		
		String path = new File("").getAbsolutePath() + "\\words\\";
		File folder = new File(path);
		
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles, Collections.reverseOrder());
		
		String[] lngs = new String[5];

		for(int i=0; i<listOfFiles.length; i++) {
			try (BufferedReader b = new BufferedReader(new FileReader(path + listOfFiles[i].getName()));){
				
				if(i == x) {
					lngs[i] = " [ x ] " +  b.readLine();
				} else {
					lngs[i] = " [   ] " + b.readLine();;
				}
			} catch (IOException e) {}
		}
		return lngs;		
	}
}
