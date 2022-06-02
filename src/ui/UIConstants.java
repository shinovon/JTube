package ui;

import javax.microedition.lcdui.Font;

public interface UIConstants {

	public static final Font smallfont = Font.getFont(0, 0, Font.SIZE_SMALL);
	public static final Font mediumfont = Font.getFont(0, 0, Font.SIZE_MEDIUM);
	
	public static final int smallfontheight = smallfont.getHeight();
	public static final int mediumfontheight = mediumfont.getHeight();
	
	
	public static final int COLOR_MAINBACKGROUND = 1;
	public static final int COLOR_MAINFOREGROUND = 2;
	public static final int COLOR_MAINBORDER = 3;
	public static final int COLOR_DARK_ALPHA = 4;
	public static final int COLOR_ITEMBORDER = 5;
	public static final int COLOR_GRAYTEXT = 6;
	public static final int COLOR_SCROLLBAR_BG = 7;
	public static final int COLOR_SCROLLBAR_FG = 8;
	public static final int COLOR_ITEM_HIGHLIGHT = 9;
	public static final int COLOR_BUTTON_HOVER_BG = 10;
	
	public static final int FONT_DEBUG = 1;

}
