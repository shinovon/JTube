package ui.custom;

import javax.microedition.lcdui.Font;

public interface UIConstants {

	static final Font smallfont = Font.getFont(0, 0, Font.SIZE_SMALL);
	static final Font mediumfont = Font.getFont(0, 0, Font.SIZE_MEDIUM);
	
	static final int smallfontheight = smallfont.getHeight();
	static final int mediumfontheight = mediumfont.getHeight();
	
	static final int GRAYTEXT_COLOR = 0x5D5D5D;
}
