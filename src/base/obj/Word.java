package base.obj;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Word extends Text {
	
	final private String value;
	
	public Word(int x, int y, String value) {
		
		super(value);
		this.value = value;
	
		setTranslateX(x);
		setTranslateY(y);
		
		setFont(Font.font("Courier New Bold", 14.49));	// for some reason 'm' bugs when bigger size is set?
				
		setFill(Color.WHITE);
	}
	
	public int getLength() {
		return this.value.length();
	}
	
	public void setValue(String s) {
		this.setValue(s);
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void moveForward() {
		this.setTranslateX(this.getTranslateX() + 15);
	}
}
