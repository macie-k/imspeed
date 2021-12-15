package app.obj;

import java.util.ArrayList;
import java.util.Arrays;

import app.Colors;
import app.Scenes;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ScoreboardEntry extends StackPane {
	
	public static boolean colorSwitch = false;			// flag for changing color
	public static ScoreboardEntry activeEntry = null;	// storing active entry object
	public static ScoreboardEntry toDeleteEntry = null;
	
	final static int COL_WIDTHS[] = {40, 100, 60, 90, 100, 90, 130, 140};	// storing widths of scoreboard columns + index (first one)
	
	final private boolean header;
	private boolean active;
	final private String date;
	final private int nr;
	private String baseColor;
	private String baseStyle = "-fx-border-color: white;"
								+ "-fx-padding: 0 10 0 0;"
								+ "-fx-border-style: hidden solid hidden hidden;";
	
	/* constructor for table headers */
	public ScoreboardEntry(int y, String... headers) {
		this(y, 0, false, null, headers);
	}
		
	public ScoreboardEntry(int y, int nr, boolean active, String date, String... valuesArr) {
		this.active = active;
		this.date = date;
		this.nr = nr;
		this.header = nr == 0;
		
		setPrefSize(750, 22);			// set size
		setTranslateY(y);				// set Y
		setAlignment(Pos.CENTER_LEFT);	// set alignment
		
		ArrayList<String> values = new ArrayList<>(Arrays.asList(valuesArr)); 	// list of all values of the entry
			values.add(0, String.valueOf(nr) + ".");							// add dot to entry index 
			
		int index = 0;		// order index of entry
		int currentX = 1;	// x position for each value
		
		for(String entry : values) {
			final int width = COL_WIDTHS[index];	// get width from declated sizes
			
			/* create label */
			Label l = new Label(entry);
				l.setPrefHeight(22);
				l.setMaxWidth(width);
				l.setTextFill(Color.WHITE);
				l.setAlignment(Pos.CENTER_RIGHT);
				l.setTranslateX(currentX);
				l.setFont(Font.font(Scenes.FONT_TEXT));
				
			baseColor = (colorSwitch ? Colors.MID_GREY : Colors.DARK_GREY);
			
			/* add style to it */
			String style = baseStyle + "-fx-background-color: " + baseColor + ";"; // switching grey colors for rows
							
			/* set different colors for active entry */
			if(active) {
				style += "-fx-background-color: #00AAAA;";
				l.setTextFill(Color.web("#ECF95B"));
			}
			
			if(index == 0) style += "-fx-padding: 0 5 0 0;";					// different padding for index value
			if(index == values.size()-1) style += "-fx-border-style: hidden;";	// hide border of the last value to not overlap with container border
										
			/* headers row styling */
			if(nr == 0) {
				if(index == 0) {
					l.setText("#");
				}
				style += "-fx-font-weight: bold;"
						+ "-fx-background-color: #FFF;"
						+ "-fx-padding: 0;";
				l.setTextFill(Color.BLACK);
				l.setAlignment(Pos.CENTER);
			}
						
			l.setStyle(style);	// apply all styles
			currentX += width;	// shift X for next column
			index++;			// increase index
			
			getChildren().add(l);	// add to stackPane
		}
		
		/* save active entry */
		if(active) {
			if(activeEntry == null) {
				activeEntry = this;
			} else {
				ScoreboardEntry entryToDisable = null;
				if(activeEntry.getDateNum() < this.getDateNum()) {
					entryToDisable = activeEntry;
					activeEntry = this;
				} else {
					entryToDisable = this;
				}
				setInactive(entryToDisable);
				entryToDisable.active = false;
			}
		}
		colorSwitch = (nr%15 == 0) ? false : !colorSwitch;	// reset color switching for each page
	}
	
	private void setInactive(ScoreboardEntry entry) {
		entry.getNameLabel().setText("- unfinished -");
		entry.getChildren().forEach(child -> {
			final Label label = (Label) child;
			final Color baseColor = Color.web(entry.baseColor);
			final String baseColorRGB = String.format("rgba(%d, %d, %d, %f)",
					(int) baseColor.getRed(),
					(int) baseColor.getGreen(),
					(int) baseColor.getBlue(),
					0.3);
				label.setStyle(label.getStyle() + ";-fx-background-color: " + baseColorRGB + ";");
				label.setTextFill(Color.rgb(255, 255, 255, 0.3));
		});
	}
	
	public void setToDelete(boolean toDelete) {
		if(toDelete) {
			toDeleteEntry = this;
			getChildren().forEach(e -> e.setStyle(e.getStyle() + "-fx-background-color: #fc3908;-fx-border-color: #EBB796;"));
		} else {
			getChildren().forEach(e -> e.setStyle(e.getStyle().replace("-fx-background-color: #fc3908;-fx-border-color: #EBB796;", "")));
		}
	}
		
	public long getDateNum() {
		return Long.valueOf(date);
	}
	
	public boolean isHeader() {
		return header;
	}
	
	/* returns page where entry is */
	public int getEntryPage() {
		return (int) Math.ceil(this.nr/15.0);
	}
	
	public String getName() {
		return getNameLabel().getText();
	}
	
	/* sets visible name value */
	public void setName(String name) {
		getNameLabel().setText(name);
	}
	
	/* returns numeral date */
	public String getDate() {
		return date;
	}
	
	/* returns information if entry is active */
	public boolean isActive() {
		return active;
	}
	
	private Label getNameLabel() {
		return (Label) getChildren().get(getChildren().size()-1);
	}
}
