package app.obj;

import app.Colors;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class CurtainBlock extends Rectangle {

	private final double speed;
		
	public CurtainBlock(int y, double speed, String LR) {
		super(0, 50, Colors.RED_C);
		
		this.speed = speed;
		if(LR.equals("R")) {
			getTransforms().add(new Rotate(180));
			setTranslateY(y+50);
			setTranslateX(800);
		} else {
			setTranslateX(0);
			setTranslateY(y);
		}
			
	}
	
	public void moveToMiddle() {
		this.setWidth(this.getWidth()+this.speed*0.7);
	}
	
	public void moveOutside() {
		this.setWidth(this.getWidth()-this.speed);
	}
	
}
