package base;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Word extends Text {
	
	boolean typed = false;
	final String value;
	
	Word(int x, int y, String value) {
		
		super(value);
		this.value = value;
	
		setTranslateX(x);
		setTranslateY(y);
		setStyle("-fx-font-size: 15;");
		setFont(Font.font("Courier new"));
		setFill(Color.WHITE);

	}
	
	public String getValue() {
		return this.value;
	}
	
	void moveForward() {
		this.setTranslateX(this.getTranslateX() + 15);
	}
}
