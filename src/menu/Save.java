 package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

public class Save {
	
	static String path = new File("").getAbsolutePath();
	static String path_bak = System.getenv("appData") + "\\imspeed";
	
	static File scoreboard = new File(path + "\\score.board");
	static File scoreboard_bak = new File(path_bak + "\\scores.bak");
	
	static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
	static void scoreboardSetup() {
		ifExists();
		if(isModified(scoreboard)) {
			restoreBackup(scoreboard_bak, scoreboard);
		} else {
			if(!scoreboard_bak.exists()) {
				System.out.print("Missing backup, ");
				restoreBackup(scoreboard, scoreboard_bak);
			}
		}
	}
	
	static void ifExists() {

		/* If scoreboard file doesn't exist */
		if(!scoreboard.exists()) {
			
			File bakDir = new File(path_bak);
			if(scoreboard_bak.exists()) {
				System.out.print("Scoreboard doesn't exist, ");
				restoreBackup(scoreboard_bak, scoreboard); return;
			} else {
				System.out.print("Scoreboard doesn't exist, ");
				if(!bakDir.exists()) {	
					if(!new File(path_bak).mkdir()) {
						System.out.println("## Error creating backup directory");
					}
				}
			}
			
			System.out.println("creating  . . .");
			try (PrintWriter saver = new PrintWriter(Save.scoreboard);
				 PrintWriter saver_bak = new PrintWriter(Save.scoreboard_bak);){
								
				Random r = new Random();	// create file and Randomizer			
				
				long lastModified = scoreboard.lastModified();	// get file's modification time
				
				int x = r.nextInt(9)+1;
				
				long calculatedFlag = (long)(lastModified*Math.pow(Math.E, x));
				String encodedFlag = Base64.getEncoder().encodeToString(String.valueOf(calculatedFlag).getBytes());	// encode it in Base64
				
				// write information about used exponent and flag itself & overwrite modification time after printing flag
				saver.println("Control flag: #" + x + encodedFlag); scoreboard.setLastModified(lastModified);
				saver_bak.println("Control flag: #" + x + encodedFlag); scoreboard_bak.setLastModified(lastModified); 
				
				//System.out.println("Actual date: \t" + sdf.format(lastModified) + "\t" + lastModified);
				
				System.out.println("ifExists()# calculated:\t" + calculatedFlag);
				System.out.println("ifExists()# encoded:\t" + encodedFlag);
				
			} catch (IOException e) {System.err.println("Error when creating scoreboard file, check your permissions");}
		} else {
			System.out.println("Scoreboard exists");
		}
	}
	
	static boolean isModified(File file) {
		
		Scanner read = null;
		
		try {
			read = new Scanner(file);
			System.out.println("");
			String s = read.nextLine().split("#")[1];	// read first line and get the flag

			int x = Character.getNumericValue(s.charAt(0)); System.out.println("x = " + x);

			String encodedFlag = s.substring(1, s.length());
			System.out.println("Encoded " + encodedFlag);
			String savedFlag = new String(Base64.getDecoder().decode(encodedFlag)); 	// decode the flag
			
			long lastModified = file.lastModified();	// get current file's modification time
			long decodedFlag = (long)(Long.parseLong(savedFlag)/Math.pow(Math.E, x));	// read first flagged line and get saved date number
								
			//System.out.println("Actual date: \t" + sdf.format(lastModified));
			//System.out.println("Actual date: \t" + sdf.format(savedFlag/Math.pow(b, e)));
			
			System.out.println("isModified("+file.getName()+")# encoded:\t" + encodedFlag);
			System.out.println("isModified("+file.getName()+")# decoded:\t" + decodedFlag);
			System.out.println("isModified("+file.getName()+")# calculated:\t" + lastModified);
			
			if(Math.abs(lastModified - decodedFlag) < 10) {	// check if the flags match with small error margin
				System.out.println("Flag is correct");
				read.close();
				return false;
			} else {
				System.out.print("1. ## Flag is incorrect, "); 
				read.close();
				return true;
			}
			
		} catch (IllegalArgumentException | FileNotFoundException e) {
			System.out.println(e);
			System.out.print("2. ## Flag is incorrect, ");
			read.close();
			return true;
		}
	}
	
	static void restoreBackup(File from, File to) {
		
		System.out.println("restoring " + to.getName() + " . . .");

		if(!isModified(from)) {
			
			Path original = Paths.get(from.getPath());
			Path backup = Paths.get(to.getPath());
			
			try {
				Files.copy(original, backup, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("\n# Restore completed #");
			} catch (IOException e1) {
				System.err.println("Can't get access to scoreboard, may be used by other program");
				return;
			}
			
		} else {
			System.out.println("Backup file modified, deleting both");
			scoreboard_bak.delete(); scoreboard.delete();
			ifExists();
		}
				
	}
	
	static void saveScore() {
		
	}

}
