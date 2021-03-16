package base.obj;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ScoreboardEntry extends StackPane {
	
	static boolean colorSwitch = false;
	public static ScoreboardEntry activeEntry;
	
	final static String COLOR_LIGHT = "#515658";
	final static String COLOR_DARKER = "#2F2F2F";
	final static int COL_WIDTHS[] = {30, 90, 70, 90, 110, 110, 110, 140};
	
	final private boolean active;
	private String date;
	
	public ScoreboardEntry(int y, String... headers) {
		this(y, 0, false, headers);
	}
	
	public ScoreboardEntry(int y, int nr, String... entries_arr) {
		this(y, nr, false, entries_arr);
	}
	
	public ScoreboardEntry(int y, int nr, boolean active, String... entries_arr) {
		this.active = active;
		if(active) {
			activeEntry = this;
		}

		setPrefSize(750, 22);
		setTranslateY(y);
		setAlignment(Pos.CENTER_LEFT);
		
		ArrayList<String> entries = new ArrayList<>(Arrays.asList(entries_arr)); 
			entries.add(0, String.valueOf(nr) + ".");
			
		int index = 0;
		int currentX = 1;
		
		for(String entry : entries) {
			final int width = COL_WIDTHS[index];
			
			Label l = new Label(entry);
				l.setPrefHeight(22);
				l.setMaxWidth(width);
				l.setTextFill(Color.WHITE);
				l.setAlignment(Pos.CENTER_RIGHT);
				l.setTranslateX(currentX);
				
				
				String style = "-fx-border-color: white;"
						+ "-fx-padding: 0 10 0 0;"
						+ "-fx-border-style: hidden solid hidden hidden;"
						+ "-fx-background-color: ";
					style += (nr == 0 ? "#FFFFFF" : colorSwitch ? COLOR_LIGHT : COLOR_DARKER) + ";";
					
					if(active) {
						style += "-fx-background-color: #00AAAA;";
						l.setTextFill(Color.web("#ECF95B"));
					}
					
					if(index == 0) style += "-fx-padding: 0 5 0 0;";
					if(index == entries.size()-1) style += "-fx-border-style: hidden;";		
				
				
			if(nr == 0) {
				if(index == 0) {
					l.setText("#");
				}
				style += ";-fx-font-weight: bold";
				l.setTextFill(Color.BLACK);
				l.setAlignment(Pos.CENTER);
			}
			
			l.setStyle(style);
			currentX += width;
			index++;
			
			getChildren().add(l);
			
		}
		colorSwitch = (nr%15 == 0) ? false : !colorSwitch;

	}
	
	public void setDate(String date) {
		this.date = date;
	}
		
	public void setName(String name) {
		Label nameField = (Label) getChildren().get(getChildren().size()-1);
		nameField.setText(name);
	}
	
	public String getDate() {
		return date;
	}
	
	public boolean isActive() {
		return active;
	}
}
