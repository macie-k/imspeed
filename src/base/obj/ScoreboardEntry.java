package base.obj;

import java.util.ArrayList;
import java.util.Arrays;

import base.Colors;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ScoreboardEntry extends StackPane {
	
	static boolean colorSwitch = false;			// flag for changing color
	public static ScoreboardEntry activeEntry;	// storing active entry object
	
	final static int COL_WIDTHS[] = {30, 90, 70, 90, 110, 110, 110, 140};	// storing widths of scoreboard columns + index (first one)
	
	final private boolean active;
	final private String date;
	final private int nr;
	
	/* constructor for table headers */
	public ScoreboardEntry(int y, String... headers) {
		this(y, 0, false, null, headers);
	}
		
	public ScoreboardEntry(int y, int nr, boolean active, String date, String... valuesArr) {
		this.active = active;
		this.date = date;
		this.nr = nr;
		
		/* save active entry */
		if(active) {
			activeEntry = this;
		}

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
				
				/* add style to it */
				String style = "-fx-border-color: white;"
						+ "-fx-padding: 0 10 0 0;"
						+ "-fx-border-style: hidden solid hidden hidden;"
						+ "-fx-background-color: ";
					style += (colorSwitch ? Colors.LIGHT_GREY : Colors.DARK_GREY) + ";";	// switching grey colors for rows
					
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
						+ "-fx-background-color: #FFF;";
				l.setTextFill(Color.BLACK);
				l.setAlignment(Pos.CENTER);
			}
			
			l.setStyle(style);	// apply all styles
			currentX += width;	// shift X for next column
			index++;			// increase index
			
			getChildren().add(l);	// add to stackPane
		}
		colorSwitch = (nr%15 == 0) ? false : !colorSwitch;	// reset color switching for each page
	}
	
	/* returns page where entry is */
	public int getEntryPage() {
		return (int) Math.ceil(this.nr/15.0);
	}
	
	/* sets visible name value */
	public void setName(String name) {
		Label nameField = (Label) getChildren().get(getChildren().size()-1);	// get the last node of stackpane
		nameField.setText(name);
	}
	
	/* returns numeral date */
	public String getDate() {
		return date;
	}
	
	/* returns information if entry is active */
	public boolean isActive() {
		return active;
	}
}
