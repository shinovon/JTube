/*
Copyright (c) 2022 Arman Jussupgaliyev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package ui;

import javax.microedition.lcdui.Font;

public interface UIConstants {

	public static final Font smallfont = Font.getFont(0, 0, Font.SIZE_SMALL);
	public static final Font mediumfont = Font.getFont(0, 0, Font.SIZE_MEDIUM);
	
	public static final int smallfontheight = smallfont.getHeight();
	public static final int mediumfontheight = mediumfont.getHeight();
	
	public static final int COLOR_MAINBG = 1;
	public static final int COLOR_MAINFG = 2;
	public static final int COLOR_MAINBORDER = 3;
	public static final int COLOR_SCREEN_DARK_ALPHA = 4;
	public static final int COLOR_ITEMBORDER = 5;
	public static final int COLOR_GRAYTEXT = 6;
	public static final int COLOR_SCROLLBAR_BG = 7;
	public static final int COLOR_SCROLLBAR_FG = 8;
	public static final int COLOR_ITEM_HIGHLIGHT = 9;
	public static final int COLOR_BUTTON_HOVER_BG = 10;
	public static final int COLOR_TIMETEXT = 11;
	public static final int COLOR_SCROLLBAR_BORDER = 12;
	public static final int COLOR_TOPBAR_BORDER = 13;
	public static final int COLOR_TOPBAR_BG = 14;
	public static final int COLOR_SOFTBAR_BG = 15;
	public static final int COLOR_SOFTBAR_FG = 16;
	public static final int COLOR_ICON = 17;

}
