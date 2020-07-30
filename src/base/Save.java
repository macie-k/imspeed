package base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import menu.Select;

public class Save {
	
	static String slash =  Window.slash;
	static String path = Window.SCORE_DIR;
	static File scoreboard = new File(path + slash + "score.board");
	
	static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	static void saveScore(String points) {
		
		if(isModified(scoreboard)) {
			scoreboard.delete();
			ifExists();
		}
		
		PrintWriter saver = null;	// pre-declare PrintWriter so it can be closed in {catch}
		try {
			saver = new PrintWriter(new FileWriter(scoreboard, true));
			
			long now = new Date().getTime();
			long fileDate = scoreboard.lastModified();
			
			/* get info about used languages */
			String language = "";
			if(Select.selected_lng_names.size() > 1) {
				language = "MIXED";
			} else {
				language = Select.selected_lng_names.get(0);
			}

			saver.println(now + "#" + Window.DIFFICULTY + "#" + language + "#" + points + "#" + Window.avgCPM);
				saver.close();
					scoreboard.setLastModified(fileDate);
									
			System.out.println("[OK] Score saved");
			
		} catch (Exception e) {
			saver.close();
			System.err.println("[ERROR] Could not save score: " + e);
			Window.error("Could not save score: " + e);
		}
	}
	
	static void ifExists() {

		/* If scoreboard file doesn't exist */
		if(!scoreboard.exists()) {
			
			System.out.print("[!] Scoreboard doesn't exist, creating . . .");
			try (PrintWriter saver = new PrintWriter(scoreboard);){
								
				Random r = new Random();	// create file and Randomizer			
				
				long lastModified = scoreboard.lastModified();	// get file's modification time
				
				int x = r.nextInt(9)+1;	// get random exponent
				
				double calculatedFlag = lastModified*Math.pow(Math.E, x);
				String encodedFlag = Base64.getEncoder().encodeToString(String.valueOf(calculatedFlag).getBytes());	// encode it in Base64
				
				lastModified -= 3;	// correct milliseconds by the time it takes to make changes by Java
				
				/* write information about used exponent (x) and flag itself & overwrite modification time after printing flag */
				saver.println("Control flag: #" + x + encodedFlag + "\n");
					saver.close();
						scoreboard.setLastModified(lastModified);
										
					/* debugging data logging */ 
//					System.out.println("ifExists()# calculated:\t" + calculatedFlag);
//					System.out.println("ifExists()# encoded:\t" + encodedFlag);
				
			} catch (IOException e) {
				System.err.println("[ERROR] Could not create scoreboard file, check your permissions: " + e);
			}
		} else {
			System.out.println("[OK] Scoreboard exists");
		}
	}
	
	static boolean isModified(File file) {
		
		if(file.exists()) {
			
			Scanner read = null;
			
			try {
				read = new Scanner(file);
				String s = read.nextLine().split("#")[1];	// read first line and get the flag

				int x = Character.getNumericValue(s.charAt(0));

				String encodedFlag = s.substring(1, s.length());
				String savedFlag = new String(Base64.getDecoder().decode(encodedFlag)); 	// decode the flag
				
				double calculatedFlag = file.lastModified();	// get current file's modification time
				double decodedFlag = Double.valueOf(savedFlag)/Math.pow(Math.E, x);	// read first flagged line and get saved date number

				/* debugging data logging */ 
//				System.out.println("isModified("+file.getName()+")# encoded: \t" + encodedFlag);
//				System.out.println("isModified("+file.getName()+")# decoded: \t" + decodedFlag);
//				System.out.println("isModified("+file.getName()+")# calculated: \t" + calculatedFlag);
				
				if(Math.abs(calculatedFlag - decodedFlag) < 30) {	// check if the flags match with small error margin
					System.out.println("[OK] Flag is correct");
					read.close();
					return false;
				} else {
					System.err.println("[ERROR] Flag is incorrect, deleting . . ."); 
					read.close();
					return true;
				}
				
			} catch (Exception e) {
				System.err.println("[ERROR] Flag is incorrect, deleting . . .");
				read.close();
				return true;
			}
		} else {
			return true;
		}
	}
	
	
/* saved for future reference? idk */
	
//	static void restoreBackup(File from, File to) {
//
//		if(!isModified(from)) {
//			
//			Path original = Paths.get(from.getPath());
//			Path backup = Paths.get(to.getPath());
//			
//			try {
//				Files.copy(original, backup, StandardCopyOption.REPLACE_EXISTING);
//				System.out.println("[OK] Restore complete\n");
//			} catch (IOException e) {
//				System.err.println("[ERROR] Could not get access to the scoreboard file: " + e);
//				return;
//			}
//			
//		} else {
//			System.out.println("[ERROR] Scoreboard and backup file modified, deleting both . . .");
//			scoreboard_bak.delete(); scoreboard.delete();
//			ifExists();
//		}
//				
//	}

}

