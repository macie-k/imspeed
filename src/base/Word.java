package base;

import javafx.scene.text.Text;

public class Word extends Text {
	boolean typed = false;
	final String value;
	
	Word(int x, int y, String value) {		
		this.value = value;
		setTranslateX(x);
		setTranslateY(y);
	}
	
	void moveForward() {
		setTranslateX(getTranslateX() - 5);
	}
}
