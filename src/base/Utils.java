package base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;

import base.obj.Word;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;
import menu.Select;


public class Utils {
	
	public static final String OS = System.getProperty("os.name").toLowerCase();	// get current operating system
	public static final boolean WINDOWS = !OS.equals("linux");
	
	public static final String SAVE_DIR = WINDOWS ? (System.getenv("APPDATA") + "/imspeed/") : (System.getenv("HOME") + "/.local/share/imspeed/");
	public static final String PATH_SCORE_TEMPLATE = "/resources/scoreboard";
	public static final String PATH_SCORE_PUBLIC = SAVE_DIR + "scoreboard";	
	
	private static int save_retry = 0;
	
	/* creates data file and sets new key */
	public static void createData() {		
		try {
			/* copy template data to byte array */
			InputStream privateData = Window.class.getResourceAsStream(PATH_SCORE_TEMPLATE);
				byte[] privateBytes = new byte[privateData.available()];
				privateData.read(privateBytes);
				privateData.close();

			/* write byte array to data file in game folder */
			FileOutputStream publicData = new FileOutputStream(PATH_SCORE_PUBLIC);
				publicData.write(privateBytes);
				publicData.close();
				
			/* set new key */
			if(setDataKey(getNewKey())) {
				Log.success("Successfully created data file");
			}
		} catch (IOException e) {
			Log.error("Could not create data file: " + e);
		}
	}
	
	/* returns true only if data key was read and is correct, false otherwise */
	public static boolean isCorrectHash() {
		try {
			final String key = getHash();						// get stored key
			final int x = Character.getNumericValue(key.charAt(0));		// extract prefix
			final String encodedKey = key.substring(1, key.length());	// extract encoded key
			
			final long modTime = new File(PATH_SCORE_PUBLIC).lastModified();					// get current modification time
			final String decodedKey = new String(Base64.getDecoder().decode(encodedKey));	// get currently saved key
			final double decodedTime = Double.valueOf(decodedKey)/Math.pow(Math.E, x);		// decode the key

			return modTime == decodedTime;	// check if flags are the same
		} catch (Exception e) {
			Log.error("Could not verify data key: " + e);
			return false;
		}
	}
	
	/* returns control hash from scoreboard file */
	public static String getHash() throws Exception {
		try {
			Statement st = getDataConnection(PATH_SCORE_PUBLIC).createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM hash");
				rs.next();

			final String hash = rs.getString("hash");
			
			st.close();
			return hash;
		} catch (SQLException e) {
			throw new Exception("Could not get results from data file: " + e.getMessage());
		}
	}
	
	/* saves score to scoreboard file */
	public static boolean saveScore(String scoreStr) {
		if(!fileExists(PATH_SCORE_PUBLIC)) {
			createData();
		} else {
			if(isCorrectHash()) {
				Log.success("Hash is correct");
			} else {
				Log.warning("Hash is incorrect, reseting...");
				createData();
			}
		}
		
		if(save_retry > 2) return false;
		
		final File scoreboard = new File(PATH_SCORE_PUBLIC);
		final long modTime = scoreboard.lastModified();
		final String sql = "INSERT INTO scoreboard (Score, Difficulty, Language, AvgCPM, Gamemode, DatePlayed, TimePlayed) VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		final int score = Integer.valueOf(scoreStr);
		final String diff = String.valueOf(Window.gameDifficulty);
		final String language = (Select.selected_lng_names.size() > 1) ? "MIXED" : Select.selected_lng_names.get(0);
		final String now = String.valueOf(new Date().getTime());
		
		try {
			Statement st = getDataConnection(PATH_SCORE_PUBLIC).createStatement();
			PreparedStatement ps = st.getConnection().prepareStatement(sql);
				ps.setInt(1, score);
				ps.setString(2, diff);
				ps.setString(3, language);
				ps.setInt(4, Window.avgCPM);
				ps.setString(5, String.valueOf(Window.gameMode));
				ps.setString(6, now);
				ps.setInt(7, (int) Window.totalSeconds);
				
			ps.executeUpdate();
				ps.close();
				st.close();
				
			scoreboard.setLastModified(modTime);
			Window.saved = true;
			Log.success("Saved score");
			save_retry = 0;
			return true;
		} catch (Exception e) {
			Log.error("Could not set key: " + e + ", retrying ...");
			save_retry++; saveScore(scoreStr);
			return false;
		}
	}
	
	/* sets data key value */
	public static boolean setDataKey(String value) {
		final File scoreboard = new File(PATH_SCORE_PUBLIC);	// get data file
		final long modTime = scoreboard.lastModified();		// get modification time
		final String sql = "UPDATE hash SET hash=?";
		
		try {
			/* execute query to set the level value */
			Statement st = getDataConnection(PATH_SCORE_PUBLIC).createStatement();
			PreparedStatement ps = st.getConnection().prepareStatement(sql);
			
			ps.setString(1, value);
			ps.executeUpdate();
				ps.close();
				st.close();
			
				scoreboard.setLastModified(modTime);	// restore  modification time
			return true;
		} catch (SQLException e) {
			Log.error("Could not set key: " + e);
			return false;
		}
	}
	
	/* formats seconds into readable time */
	public static String[] formatTimePlayed(double totalSeconds) {
		int total = (int)totalSeconds;
		
		int seconds = total % 60;
			String secondsPrint = (seconds > 9) ? String.valueOf(seconds) : "0"+String.valueOf(seconds);
		int minutes = (total % 3600) / 60;
			String minutesPrint = (minutes > 9) ? String.valueOf(minutes) : "0"+String.valueOf(minutes);
		int hours = total/3600;
			String hoursPrint = (hours > 9) ? String.valueOf(hours) : "0"+String.valueOf(hours);
		
		return new String[] {hoursPrint, minutesPrint, secondsPrint};
	}
		
	/* returns connection for data reading & writing */
	public static Connection getDataConnection(String path) throws SQLException {
		return DriverManager.getConnection("jdbc:ucanaccess://" + path);
	}
	
	/* generate new key for data file */
	public static String getNewKey() {
		Random r = new Random();
		
		final long modTime = new File(PATH_SCORE_PUBLIC).lastModified();		// get current modification time
		final int x = r.nextInt(9)+1;										// get random exponent
		final double calculatedKey = modTime*Math.pow(Math.E, x);			// calculate key
		
		return (x + Base64.getEncoder().encodeToString(String.valueOf(calculatedKey).getBytes()));	// return key in base64
	}
	
	/* function to calculate new word's position and value */
	public static Word createWord(List<String> strings, List<Integer> xVal_final, List<Integer> yVal_final, List<Word> fresh) {
		List<Integer> xVal = Window.xVal, yVal = Window.yVal;
		
		if(xVal.size() < 1) {
			xVal = new ArrayList<>(xVal_final);
		}
		if(yVal.size() < 1) {
			yVal = new ArrayList<>(yVal_final);
		}
		
		Random random = new Random();

		/* get random indexes */
		int rndmx = random.nextInt(xVal.size());	
		int rndmy = random.nextInt(yVal.size());
	
		String value = strings.get(random.nextInt(strings.size()));		// get random text from all words
		
		/* set x,y and remove them from lists */
		int y = yVal.get(rndmy);
			yVal.remove(rndmy);
		int x = xVal.get(rndmx);
			xVal.remove(rndmx);
		
		/* check for word collision in the same row and calculate the final x coordinate */
		for(Word w : fresh) {
			if(w.getTranslateY() == y) {
				while(w.getTranslateX() <= (value.length()*9)+x+20) {
					x -= 5;
				}
			}
		}	
		
		Word w = new Word(x, y, value);
		
		/* there is 0.00043687199 chance at most, that it will happen, pls don't get mad */
		if(value.equals("I'm gay")) {
			w.setStyle(Colors.COLOR_GAY_GRADIENT);
		}
		
		return w;
	}
	
	/* fades in given node */
	public static void fadeIn(Node node, int duration) {
		FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
			ft.setFromValue(node.getOpacity());
		    ft.setToValue(1);
		    ft.play();
	}
	
	public static boolean fileExists(String name) {
		return new File(name).exists();
	}
}
