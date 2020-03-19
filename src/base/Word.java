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
		setStyle("-fx-font-size: 15;");
		setFont(Font.font("Courier new"));
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
	
	public void setFresh(boolean b) {
		this.fresh = b;
	}
	
	void moveForward() {
		this.setTranslateX(this.getTranslateX() + 15);
	}
}
