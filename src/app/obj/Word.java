package app.obj;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Word extends Text {
	
	public Word(int x, int y, String value) {
		this(x, y, value, "#FFF");
	}
	
	public Word(int x, int y, String value, String color) {
		super(value);
	
		setTranslateX(x);
		setTranslateY(y);
		
		setFill(Color.WHITE);
		setFont(Font.font("Courier New Bold", 14.49));	// for some reason 'm' bugs when bigger size is set?
	}
	
	public void setColor(String color) {
		this.setFill(Color.web(color));
	}
	
	public int getLength() {
		return this.getText().length();
	}
	
	public void setValue(String s) {
		this.setText(s);
	}
	
	public String getValue() {
		return this.getText();
	}
	
	public void moveForward() {
		this.setTranslateX(this.getTranslateX() + 15);
	}
}
