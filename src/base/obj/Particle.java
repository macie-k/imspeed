package base.obj;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Particle extends Rectangle {
	
	private final double alpha;
	
	public Particle(double x, double y, double alpha) {
		super(1, 1, Color.rgb(255, 255, 255, alpha));
		
		this.alpha = alpha;
		setTranslateX(x);
		setTranslateY(y);
	}
	
	public void moveForward() {
		this.setTranslateX(this.getTranslateX() + 2);
	}
	
	public double getAlpha() {
		return this.alpha;
	}
	
}
