package base;

import javafx.scene.paint.Color;

/*
	  Constants for all used colors
	  _C indicates a Color object
*/

public interface Colors {
	public static final String BACKGROUND = "#0E0E0E";
	public static final String RED = "#FF554D";
	public static final String ORANGE = "#FCA103";
	public static final String YELLOW = "#F0FC03";
	public static final String GREEN = "#7FFC03";
	public static final String LIGHT_GREY = "#373737";
	public static final String DARK_GREY = "#2F2F2F";
	public static final String GAY_GRADIENT = "-fx-fill: linear-gradient(to right, #FF3030, #FF6A00, #FFF200, #4AFF59, #00FF7B, #34ABEB, #A200FF, #FF36AB);";
	public static final String GOLD_GRADIENT = "-fx-fill: linear-gradient(#FFA200, #FFD500);";
	
	public static final Color BACKGROUND_C = Color.web(BACKGROUND);
	public static final Color RED_C = Color.web(RED);
	public static final Color ORANGE_C = Color.web(ORANGE);
	public static final Color YELLOW_C = Color.web(YELLOW);
	public static final Color GREEN_C = Color.web(GREEN);
	public static final Color LIGHT_GREY_C = Color.web(LIGHT_GREY);
	public static final Color DARK_GREY_C = Color.web(DARK_GREY);
}
