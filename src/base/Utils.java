package base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

import base.obj.Particle;
import base.obj.Word;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
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
	public static void createScoreboard() {
		new File(SAVE_DIR).mkdir();
		try {
			/* copy template data to byte array */
			InputStream privateData = Window.class.getResourceAsStream(PATH_SCORE_TEMPLATE);				
			Files.copy(privateData, Paths.get(PATH_SCORE_PUBLIC), StandardCopyOption.REPLACE_EXISTING);
			privateData.close();
				
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
			createScoreboard();
		} else {
			if(isCorrectHash()) {
				Log.success("Hash is correct");
			} else {
				Log.warning("Hash is incorrect, reseting...");
				createScoreboard();
			}
		}
		
		if(save_retry > 2) return false;
		
		final File scoreboard = new File(PATH_SCORE_PUBLIC);
		final long modTime = scoreboard.lastModified();
		final String sql = "INSERT INTO scoreboard (Score, AvgCPM, Difficulty, Language, Gamemode, DatePlayed, TimePlayed) VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		final int score = Integer.valueOf(scoreStr);
		final String diff = String.valueOf(Window.gameDifficulty);
		final String language = (Select.selected_lng_names.size() > 1) ? "MIXED" : Select.selected_lng_names.get(0);
		final String now = String.valueOf(new Date().getTime());
		
		try {
			Statement st = getDataConnection(PATH_SCORE_PUBLIC).createStatement();
			PreparedStatement ps = st.getConnection().prepareStatement(sql);
				ps.setInt(1, score);
				ps.setInt(2, Window.avgCPM);
				ps.setString(3, diff);
				ps.setString(4, language);
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
	
	public static AnimationTimer getBackgroundTimer(int xRange, int yRange, Pane root, Node... toFront) {
		return getBackgroundTimer(xRange, yRange, root, 70, 50_000_000, 5_000_000, toFront);
	}
	
	public static AnimationTimer getBackgroundTimer(int xRange, int yRange, Pane root) {
		return getBackgroundTimer(xRange, yRange, root, 70, 50_000_000, 5_000_000);
	}
	
	public static AnimationTimer getBackgroundTimer(int xRange, int yRange, Pane root, int initialAmount, long creationDelay, long speed, Node... toFront) {
		List<Particle> particles = new ArrayList<Particle>();	// list of all particles
		Random random = new Random();
		
		int[] particleY = new int[yRange];
		for(int i=0; i<yRange; i++) {
			particleY[i] = i+2;	// array of predefined Y values
		}
		
		/* generate particle with its trail */
		for(int i=0; i<initialAmount; i++) {
			Particle p = new Particle(random.nextInt(xRange)+10, particleY[random.nextInt(yRange)], random.nextDouble());
				particles.add(p); root.getChildren().add(p);
			
			Particle trail_1 = new Particle(p.getTranslateX()-1, p.getTranslateY(), p.getAlpha()*0.66);
				particles.add(trail_1); root.getChildren().add(trail_1);
				
			Particle trail_2 = new Particle(p.getTranslateX()-2, p.getTranslateY(), p.getAlpha()*0.33);
				particles.add(trail_2); root.getChildren().add(trail_2);
		}
		
		/* animating particles */
		AnimationTimer animation_background = new AnimationTimer() {

			private long particle_create = 0;
			private long particle_move = 0;
			
			@Override
			public void handle(long now) { 
				
				if(now - particle_move >= speed) {
					List<Particle> toRemove = new ArrayList<Particle>();
					
					for(Particle p : particles) {						
						if(p.getTranslateX()>800) {
							toRemove.add(p);
						} else {
							p.moveForward();
						}
					} 
					
					particles.removeAll(toRemove);
					root.getChildren().removeAll(toRemove);
					toRemove.clear();
					
					particle_move = now;
				}
				
				if(now - particle_create >= creationDelay) {
					Particle p = new Particle(-2, particleY[random.nextInt(yRange)], random.nextDouble());
						particles.add(p); root.getChildren().add(p);
						
					Particle trail_1 = new Particle(-3, p.getTranslateY(), p.getAlpha()*0.66);
						particles.add(trail_1); root.getChildren().add(trail_1);
						
					Particle trail_2 = new Particle(-4, p.getTranslateY(), p.getAlpha()*0.33);
						particles.add(trail_2); root.getChildren().add(trail_2);
						
					for(Node n : toFront) {
						n.toFront();
					}
					particle_create = now;	
				}
			}
			
		};
		
		return animation_background;
	}
	
	public static boolean fileExists(String name) {
		return new File(name).exists();
	}
}
