package base;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Word extends Text {
	
	final String value;
	boolean fresh = true;
	
	Word(int x, int y, String value) {
		
		super(value);
		this.value = value;
	
		setTranslateX(x);
		setTranslateY(y);
		setFont(Font.font("Courier new", 15));
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
	
	void moveForward() {
		this.setTranslateX(this.getTranslateX() + 15);
	}
}
