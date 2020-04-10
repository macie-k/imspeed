package base;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Particle extends Rectangle {
	
	private double alpha;
	
	Particle(double e, double d, double alpha) {
		super();
		
		this.alpha = alpha;
		setTranslateX(e);
		setTranslateY(d);
		setHeight(1); setWidth(1);
		setFill(Color.rgb(255, 255, 255, alpha));
	}
	
	void moveForward() {
		this.setTranslateX(this.getTranslateX() + 2);
	}
	
	public double getAlpha() {
		return this.alpha;
	}
	
}
