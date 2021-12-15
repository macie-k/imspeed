package app;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;

import app.obj.Particle;
import app.obj.ScoreboardEntry;
import app.obj.Word;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import menu.Select;


public class Utils {
	
	public static final String OS = System.getProperty("os.name").toLowerCase();	// get current operating system
	public static final boolean WINDOWS = !OS.equals("linux");
	
	public static final String SAVE_DIR = WINDOWS ? (System.getenv("APPDATA") + "\\imspeed\\") : (System.getenv("HOME") + "/.local/share/imspeed/");
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
		
	public static ResultSet getScores(String sortBy, String order) throws SQLException {
		final File scoreboard = new File(PATH_SCORE_PUBLIC);	// get data file
		final long modTime = scoreboard.lastModified();		// get modification time
		final String sql = String.format("SELECT * FROM scoreboard ORDER BY %s %s, Score DESC", sortBy, order);
		
		/* execute query to set the level value */
		Statement st = getDataConnection(PATH_SCORE_PUBLIC).createStatement();
		PreparedStatement ps = st.getConnection().prepareStatement(sql);
		ResultSet results = ps.executeQuery();
		
		ps.close();
		st.close();
		scoreboard.setLastModified(modTime);	// restore  modification time
		
		return results;
	}
		
	public static ArrayList<ScoreboardEntry> getRows(String sortBy, String order) {
		final ArrayList<ScoreboardEntry> entries = new ArrayList<>();	// list of all returned scores
		final ScoreboardEntry activeEntry = ScoreboardEntry.activeEntry;
		try {
			final ResultSet results = Utils.getScores(sortBy, order);

			int i = 0;
			while(results.next()) {
				boolean active = false;
				final String score = String.valueOf(results.getInt("Score"));
				final String cpm = String.valueOf(results.getInt("CPM"));
				final String diff = results.getString("Difficulty");
				final String lang = results.getString("Language");
				final String gm = results.getString("Gamemode");
				final String dateNum = results.getString("DateTime");
				final String date = Utils.formatDate(Long.valueOf(dateNum), "dd.MM.yy HH:mm");
				String username = results.getString("Name");
					if(username.equals("NULL")) {
						if(activeEntry == null) {
							active = true;
						} 
					} else if((activeEntry != null && activeEntry.getDate().equals(dateNum))) {
						username = activeEntry.getName();
						active = true;
					}
				entries.add(new ScoreboardEntry(23+(i%15)*22, ++i, active, dateNum, score, cpm, diff, lang, gm, date, username.equals("NULL") ? "" : username));
			}
		} catch (SQLException e) {
			Log.error("Could not retrieve scoreboard information: " + e);
		}
		return entries;
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
		final String sql = "INSERT INTO scoreboard (Score, CPM, Difficulty, Language, Gamemode, DateTime, Name, TimePlayed) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		final int score = Integer.valueOf(scoreStr);
		final String diff = parseDifficulty(Window.gameDifficulty);
		final String language = (Select.selected_lng_names.size() > 1) ? "MIXED" : Select.selected_lng_names.get(0);
		final String now = String.valueOf(new Date().getTime());
		final String gm = Scenes.gamemodes[Window.gameMode].getValue();
		
		try {
			Statement st = getDataConnection(PATH_SCORE_PUBLIC).createStatement();
			PreparedStatement ps = st.getConnection().prepareStatement(sql);
				ps.setInt(1, score);
				ps.setInt(2, Window.avgCPM);
				ps.setString(3, diff);
				ps.setString(4, language);
				ps.setString(5, gm);
				ps.setString(6, now);
				ps.setString(7, "NULL");
				ps.setInt(8, (int) Window.totalSeconds);
				
				
			ps.executeUpdate();
				ps.close();
				st.close();
				
			scoreboard.setLastModified(modTime);
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
	
	/* returns formatted date from given time in milis */
	public static String formatDate(long milis, String format) {
		return new SimpleDateFormat(format).format(new Date(milis));
	}
	
	/* fallback for default formatting */
	public static String formatDate(long milis) {
		return formatDate(milis, "dd.MM.yyyy HH:mm:ss");
	}
	
	/* returns string name of the selected difficulty */
	public static String parseDifficulty(int nr) {
		return Scenes.loadedDifficulties[nr-1];
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
	
	
	public static void showScoreboardPage(int page, Pane resultsContainer, ArrayList<ScoreboardEntry> entries, Text pageNumNode) {
		pageNumNode.setText(String.valueOf(page));
		
		ArrayList<ScoreboardEntry> toRemove = new ArrayList<>();
		resultsContainer.getChildren().forEach(node -> {
			ScoreboardEntry e = (ScoreboardEntry) node;
			if(!e.isHeader()) {
				toRemove.add(e);
			}
		});
		resultsContainer.getChildren().removeAll(toRemove);
		
		final int starting = (page-1)*15;
		/* add max 15 new results per page */
		for(int i=starting; i<starting+15 && i<entries.size(); i++) {
			resultsContainer.getChildren().add(entries.get(i));
		}
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
			w.setStyle(Colors.GAY_GRADIENT);
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
		
	public static AnimationTimer blinkingNodeTimer(Node node) {
		return blinkingNodeTimer(node, 500_000_000);
	}
	
	public static AnimationTimer blinkingNodeTimer(Node node, long speed) {
		 return new AnimationTimer() {
			
			private long lastUpdate = 0;
						
			@Override
			public void handle(long now) {		
				
				if(now - lastUpdate >= speed) {
					node.setVisible(!node.isVisible());
					lastUpdate = now;
				}
			}
		};		
	}
	
	/* removes all not saved scores / all with NULL as name */
	public static boolean removeRecord(String date) {
		final File scoreboard = new File(PATH_SCORE_PUBLIC);
		final long modTime = scoreboard.lastModified();
		final String sql = String.format("DELETE FROM scoreboard WHERE DateTime='%s'", date);
				
		try {
			Statement st = getDataConnection(PATH_SCORE_PUBLIC).createStatement();
			final int rows = st.executeUpdate(sql);
				st.close();
				
			scoreboard.setLastModified(modTime);
			
			ScoreboardEntry activeEntry = ScoreboardEntry.activeEntry;
			if(activeEntry != null && activeEntry.getDate().equals(date)) {
				ScoreboardEntry.activeEntry = null;
			}
			if(rows > 0) {
				Log.success("Removed " + rows + " scores");
				return true;
			} else {
				Log.success("No scores were removed");
				return false;
			}
		} catch (Exception e) {
			Log.error("Could not save score: " + e);
			return false;
		}
	}
	
	public static boolean setScoreName(String name, String date) {
		final File scoreboard = new File(PATH_SCORE_PUBLIC);
		final long modTime = scoreboard.lastModified();
		final String sql = "UPDATE scoreboard SET name=? WHERE DateTime='"+date+"'";
		
		try {
			Statement st = getDataConnection(PATH_SCORE_PUBLIC).createStatement();
			PreparedStatement ps = st.getConnection().prepareStatement(sql);
				
			ps.setString(1, name);
			ps.executeUpdate();
				ps.close();
				st.close();
				
			scoreboard.setLastModified(modTime);
			Log.success("Updated score name");
			return true;
		} catch (Exception e) {
			Log.error("Could not update name: " + e);
			return false;
		}
	}
	
	public static Text createText(String value, Color fill, String fontName, int fontSize) {
		Text t = new Text(value);
		t.setFont(Font.font(fontName, fontSize));
		t.setFill(fill);
		
		return t;
	}
	
	public static Node[] inputBox(String messageStr) {
		return inputBox(messageStr, 15);
	}
	
	public static Node[] inputBox(String messageStr, int maxChars) {
		final StackPane stack = new StackPane();
			stack.setAlignment(Pos.CENTER_LEFT);
					
		final TextField input = new TextField();
			input.setMaxWidth(120);
			input.setMaxHeight(20);
			input.setAlignment(Pos.CENTER);
			input.setStyle("-fx-faint-focus-color: transparent;"
					+ "-fx-focus-color: transparent;"
					+ "-fx-text-box-border: transparent;"
					+ "-fx-background-color: transparent;"
					+ "-fx-text-fill: #FFF;"
					+ "-fx-highlight-fill: #FFF;"
					+ "-fx-highlight-text-fill: #0E0E0E;"
					+ "-fx-cursor: block;"
					+ "-fx-padding: 0 2 0 2;"
					+ "-fx-font-family: 'Courier new', monospace;");
			input.setOnKeyTyped(e -> {
				final int maxCharacters = maxChars;
		        if(input.getText().length() > maxCharacters) {
		        	e.consume();
		        }
			});
			
		int shiftX = 20;
			
		Text message = createText(messageStr, Color.WHITE, Scenes.FONT_TEXT, 17);
			message.setTranslateX(shiftX);
			
		Text signL = createText("[", Color.WHITE, Scenes.FONT_TEXT, 17);
			shiftX += message.getLayoutBounds().getWidth() + 20;
			signL.setTranslateX(shiftX);
			
		shiftX += 10;
		input.setTranslateX(shiftX);
		
		Text signR = createText("]", Color.WHITE, Scenes.FONT_TEXT, 17);
			shiftX += 120;
			signR.setTranslateX(shiftX);
		
		final Rectangle bg = new Rectangle(shiftX+20, 100);
			bg.setFill(Colors.BACKGROUND_C);
		
		final int borderWidth = 5;
		stack.setPrefSize(shiftX+20+2*borderWidth, 100+2*borderWidth);
		stack.setStyle("-fx-border-color: #FFF; -fx-border-width: " + borderWidth);
		stack.getChildren().addAll(bg, message, signL, signR, input);
		
		return new Node[] {stack, input};
	}
	
	public static AnimationTimer getBackgroundTimer(int xRange, int yRange, Pane root, Node... toFront) {
		return getBackgroundTimer(xRange, yRange, root, 30, 200_000_000, 5_000_000, toFront);
	}
	
	public static AnimationTimer getBackgroundTimer(int xRange, int yRange, Pane root) {
		return getBackgroundTimer(xRange, yRange, root, 30, 200_000_000, 5_000_000);
	}
	
	public static AnimationTimer getBackgroundTimer(int xRange, int yRange, Pane root, int initialAmount, long creationDelay, long speed, Node... toFront) {		
		Random random = new Random();
		
		int[] particleY = new int[yRange];
		for(int i=0; i<yRange; i++) {
			particleY[i] = i+2;	// array of predefined Y values
		}
		
		/* generate particle with its trail */
		for(int i=0; i<initialAmount; i++) {
			final int x = random.nextInt(xRange)+10;
			final int y = particleY[random.nextInt(yRange)];
			final double alpha = 0.1 + 0.70 * random.nextDouble();
			final int distance = getRandomDistance(alpha);
			
			Particle p = new Particle(distance, x, y, alpha, root);
				root.getChildren().add(p);
		}
				
		/* animating particles */
		return new AnimationTimer() {

			private long particle_create = 0;
			
			@Override
			public void handle(long now) { 				
				if(now - particle_create >= creationDelay) {
					final int y = particleY[random.nextInt(yRange)];
					final double alpha = 0.1 + 0.70 * random.nextDouble();
					final int distance = getRandomDistance(alpha);
					
					Particle p = new Particle(distance, -2, y, alpha, root);
						root.getChildren().add(p);
						
					for(Node n : toFront) {
						n.toFront();
					}
					
					particle_create = now;	
				}
			}
			
		};		
		
	}
	
	/* Get random distance based on alpha */
	private static int getRandomDistance(double alpha) {
		if(alpha <= 0.45) return 1;
		if(alpha <= 0.70) return 2;
		if(alpha <= 1.00) return 3;
		
		return 0;
	}
	
	public static boolean fileExists(String name) {
		return new File(name).exists();
	}
}
