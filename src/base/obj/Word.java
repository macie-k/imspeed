package base.obj;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Word extends Text {
	
	private String value;

	public Word(int x, int y, String value) {
		this(x, y, value, "#FFF");
	}
	
	public Word(int x, int y, String value, String color) {
		super(value);
		
		this.value = value;
	
		setTranslateX(x);
		setTranslateY(y);
		
		setFill(Color.WHITE);
		setFont(Font.font("Courier New Bold", 14.49));	// for some reason 'm' bugs when bigger size is set?
	}
	
	public void setColor(String color) {
		setFill(Color.web(color));
	}
	
	public int getLength() {
		return value.length();
	}
	
	public void setValue(String s) {
		setText(s);
		value = s;
	}
	
	public String getValue() {
		return value;
	}
	
	public void moveForward() {
		setTranslateX(getTranslateX() + 15);
	}
}
