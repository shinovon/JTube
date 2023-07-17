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
package jtube.ui.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import jtube.Util;
import jtube.ui.AppUI;
import jtube.ui.UIConstants;
import jtube.ui.UIItem;

public class Label extends UIItem implements UIConstants {

	private String[] textArr;
	private int maxLines;
	private String text;
	private Font font;
	private int color;
	
	private int h;
	private int lineSpaces = 2;
	private int marginLeft;
	private int marginRight;
	private int marginTop = 2;
	private int marginBottom = 2;
	private boolean colorSet;
	
	public Label(String s) {
		this(s, mediumfont);
	}
	
	public Label(String s, Font f) {
		this.font = f;
		this.text = s;
	}

	public Label(String s, Font f, int c) {
		this.font = f;
		this.text = s;
		this.color = c;
		this.colorSet = true;
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setFont(font);
		if(textArr == null) return;
		y+=marginTop;
		int fh = lineSpaces+font.getHeight();
		g.setColor(colorSet ? color : AppUI.getColor(COLOR_MAINFG));
		for(int i = 0; i < textArr.length; i++) {
			g.drawString(textArr[i], x + marginLeft, y, 0);
			y+=fh;
		}
	}
	
	public void setMaxLines(int i) {
		this.maxLines = i;
	}
	
	public void setColor(int c) {
		this.color = c;
		this.colorSet = true;
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = marginTop + marginBottom;
		String[] arr = Util.getStringArray(text, w - 4 - (marginLeft + marginRight), font);
		textArr = new String[arr.length > maxLines && maxLines > 0 ? maxLines : arr.length];
		for(int i = 0; i < textArr.length; i++) {
			textArr[i] = arr[i];
			if(i == textArr.length-1 && arr.length > textArr.length) {
				textArr[i] = textArr[i].trim().concat("...");
			}
		}
		h += textArr.length * (font.getHeight() + lineSpaces);
	}
	
	public void setFont(Font f) {
		this.font = f;
	}

	public void setLineSpaces(int i) {
		lineSpaces = i;
		relayout();
	}

	public void setMarginLeft(int i) {
		marginLeft = i;
		relayout();
	}

	public void setMarginRight(int i) {
		marginRight = i;
		relayout();
	}

	public void setMarginWidth(int i) {
		marginRight = marginLeft = i;
		relayout();
	}

	public void setMarginTop(int i) {
		marginTop = i;
		relayout();
	}

	public void setMarginBottom(int i) {
		marginBottom = i;
		relayout();
	}

	public void setMarginHeight(int i) {
		marginBottom = marginTop = i;
		relayout();
	}

	public boolean canBeFocused() {
		return false;
	}

}
