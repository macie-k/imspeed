package menu.obj;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ScaleBox extends Rectangle {

	private boolean filled;
	
	public ScaleBox(int x, int y, boolean filled){
		super(10, 15, Color.TRANSPARENT);
		this.filled = filled;
		
		setTranslateX(x);
		setTranslateY(y);
		setStyle("-fx-stroke: white; fx-stroke-width: 1");
		
		if(filled) {
			setFill(Color.WHITE);
		}
	}
	
	public boolean isFilled() {
		return filled;
	}
	
	public void setFilled(boolean value) {
		filled = value;
		if(filled) {
			setFill(Color.WHITE);
		} else {
			setFill(Color.TRANSPARENT);
		}
	}
}
