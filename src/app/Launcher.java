package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Launcher {
	public static void main (String[] args) throws FileNotFoundException {
		
		if(args.length>0) {
			for(String arg : args) {
				switch(arg) {
					case "--log":
						Log.success("Logging enabled");
						
						PrintStream outputLog = new PrintStream(new FileOutputStream(new File("log.txt")));
							System.setOut(outputLog);
							System.setErr(outputLog);
					break;
					
					default: break;
				}
			}
		}
		try {
			Window.launcher(args);
		} catch (Exception e) {
			Log.error(e.toString());
		}
	}
}
