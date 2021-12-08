package app.obj;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Particle extends Rectangle {
	
	private final double alpha;
	private final int distance;
	private final int velocity;
	
	public Particle(int distance, double x, double y, double alpha) {
		super(distance, distance, Color.rgb(255, 255, 255, alpha));
		
		this.alpha = alpha;
		this.distance = distance;
		this.velocity = 4-distance;
		setTranslateX(x);
		setTranslateY(y);
	}
		
	public void moveForward() {
		this.setTranslateX(this.getTranslateX() + this.velocity);
	}
	
	public int getDistance() {
		return this.distance;
	}
	
	public double getAlpha() {
		return this.alpha;
	}
	
}
