package base.obj;

import base.Scenes;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class CurtainBlock extends Rectangle {

	final private double speed;
		
	public CurtainBlock(int y, double speed, String LR) {
		super(0, 50, Color.web(Scenes.Color_RED));
		
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