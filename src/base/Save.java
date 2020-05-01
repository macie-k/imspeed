package base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import menu.Selection;

public class Save {
	
	static String slash = Window.slash;
	
	static String path = new File("").getAbsolutePath();
	static String path_bak = Window.BAK_DIR;
	
	static File scoreboard = new File(path + slash + "score.board");
	static File scoreboard_bak = new File(path_bak + slash + "score.bak");
	
	static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	static void saveScore(String points) {
		
		if(isModified(scoreboard)) {
			restoreBackup(scoreboard_bak, scoreboard);
		} else {
			if(!scoreboard_bak.exists()) {
				System.out.print("[ERROR] Missing backup, trying to restore");
				restoreBackup(scoreboard, scoreboard_bak);
			}
		}
		
		PrintWriter saver = null; PrintWriter saver_bak = null;
		try {
			FileWriter FileW = new FileWriter(scoreboard, true);
			FileWriter FileW_bak = new FileWriter(scoreboard_bak, true);
			saver = new PrintWriter(FileW);
			saver_bak = new PrintWriter(FileW_bak);
			
			long now = new Date().getTime();
			long fileDate = scoreboard.lastModified();
			
			String language = "";
			if(Selection.selectedNames.size() > 1) {
				language = "MIXED";
			} else {
				language = Selection.selectedNames.get(0);
			}

			//saver.println("## " + sdf.format(now) + " ## " + MenuWords.loadDifficulties()[Window.DIFFICULTY-1] + " ## " + points);
			saver.println(now + "#" + Window.DIFFICULTY + "#" + language + "#" + points + "#" + Window.avgCPM);
				saver.close();
					scoreboard.setLastModified(fileDate);
				
			//saver_bak.println("## " + sdf.format(now) + " ## " + MenuWords.loadDifficulties()[Window.DIFFICULTY-1] + " ## " +points);
			saver.println(now + "#" + Window.DIFFICULTY + "#" + language + "#" + points + "#" + Window.avgCPM);	
				saver_bak.close();
					scoreboard_bak.setLastModified(fileDate);
					
			System.out.println("[OK] Score saved");
			
		} catch (Exception e) {
			saver.close(); saver_bak.close();
			System.out.println("[ERROR] Could not save score " + e);
		}
	}
	
	static void ifExists() {

		/* If scoreboard file doesn't exist */
		if(!scoreboard.exists()) {
			
			System.out.print("\n[!] Scoreboard doesn't exist, ");
			if(scoreboard_bak.exists()) {
				System.out.println("trying to restore . . .");
				restoreBackup(scoreboard_bak, scoreboard); return;
			} else {
				System.out.println("creating . . .");
				try (PrintWriter saver = new PrintWriter(scoreboard);
					 PrintWriter saver_bak = new PrintWriter(scoreboard_bak);){
									
					Random r = new Random();	// create file and Randomizer			
					
					long lastModified = scoreboard.lastModified();	// get file's modification time
					
					int x = r.nextInt(9)+1;
					
					double calculatedFlag = lastModified*Math.pow(Math.E, x);
					String encodedFlag = Base64.getEncoder().encodeToString(String.valueOf(calculatedFlag).getBytes());	// encode it in Base64
					
					lastModified -= 3;
					
					/* write information about used exponent (x) and flag itself & overwrite modification time after printing flag */
					saver.println("Control flag: #" + x + encodedFlag + "\n");
						saver.close();
							scoreboard.setLastModified(lastModified);
							
					saver_bak.println("Control flag: #" + x + encodedFlag + "\n");
						saver_bak.close();
							scoreboard_bak.setLastModified(lastModified);
										
//					System.out.println("ifExists()# calculated:\t" + calculatedFlag);
//					System.out.println("ifExists()# encoded:\t" + encodedFlag);
					
				} catch (IOException e) {
					System.err.println("[ERROR] Could not create scoreboard file, check your permissions " + e);
				}
			}
		} else {
			System.out.println("\n[OK] Scoreboard exists");
		}
	}
	
	static boolean isModified(File file) {
		
		String fileName = file.getName();
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

//				System.out.println("isModified("+file.getName()+")# encoded: \t" + encodedFlag);
//				System.out.println("isModified("+file.getName()+")# decoded: \t" + decodedFlag);
//				System.out.println("isModified("+file.getName()+")# calculated: \t" + calculatedFlag);
				
				if(Math.abs(calculatedFlag - decodedFlag) < 30) {	// check if the flags match with small error margin
					System.out.println("\n[OK] Flag is correct (" + fileName + ")");
					read.close();
					return false;
				} else {
					System.out.println("\n[ERROR] Flag is incorrect (" + fileName + "), trying to restore . . ."); 
					read.close();
					return true;
				}
				
			} catch (Exception e) {
				System.out.println("\n[ERROR] Flag is incorrect (" + fileName + "), trying to restore . . .");
				read.close();
				return true;
			}
		} else {
			System.out.println("[ERROR] File " + fileName + " doesn't exist");
			return true;
		}
	}
	
	static void restoreBackup(File from, File to) {
		

		if(!isModified(from)) {
			
			Path original = Paths.get(from.getPath());
			Path backup = Paths.get(to.getPath());
			
			try {
				Files.copy(original, backup, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("\n[OK] Restore complete");
			} catch (IOException e1) {
				System.err.println("\n[ERROR] Could not get access to the scoreboard file, may be used by other program");
				return;
			}
			
		} else {
			System.out.println("[ERROR] Scoreboard and backup file modified, deleting both . . .");
			scoreboard_bak.delete(); scoreboard.delete();
			ifExists();
		}
				
	}

}

