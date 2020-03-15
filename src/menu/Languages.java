package menu;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Languages extends StackPane {
	
	private final Text text;
	private final Rectangle background;
	
	public Languages(int y, String value, boolean highlighted) {	
				
		text = new Text(value);
		text.setTranslateX(325);
		text.setTranslateY(y);
		text.setFont(Font.font("Courier new"));
		
			
		background = new Rectangle();
		background.setWidth(160);
		background.setHeight(20);
		background.setTranslateX(325);
		background.setTranslateY(y);
		background.setFill(Color.WHITE);
		
		if(highlighted) {
			background.setFill(Color.WHITE);
			text.setFill(Color.BLACK);
		} else {
			background.setFill(Color.web("#0f0f0f"));
			text.setFill(Color.WHITE);
		}
		setAlignment(text, Pos.CENTER_LEFT);
		getChildren().addAll(background, this.text);	
	}
}
