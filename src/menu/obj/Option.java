package menu.obj;

import base.Window;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Option extends StackPane {
	
	private final Text val;
	private final Text check;
	private final Rectangle background;
	
	public Option(int y, String value, boolean highlighted) {
		this(y, "", value, highlighted);
	}
	
	public Option(int y, String checked, String value, boolean highlighted) {	
						
		int textX = 240;
		int bgX = 240;
		int bgWidth = 320;
		
		check = new Text(checked);
		check.setTranslateX(250);
		check.setTranslateY(y);
		check.setFont(Font.font("Courier new", 14));
		
		val = new Text(value);
		val.setTranslateX(textX);
		val.setTranslateY(y);
		val.setFont(Font.font("Courier new", 14));
		
		background = new Rectangle(bgWidth, 25, Color.WHITE);
		background.setTranslateX(bgX);
		background.setTranslateY(y);
		
		if(highlighted) {
			background.setFill(Color.WHITE);
			val.setFill(Color.BLACK);
			check.setFill(Color.BLACK);
		} else {
			background.setFill(Window.BACKGROUND);
			val.setFill(Color.WHITE);
			check.setFill(Color.WHITE);
		}
		
		setAlignment(val, Pos.CENTER);
		setAlignment(check, Pos.CENTER_LEFT);
		getChildren().addAll(background, val, check);	
	}
}
