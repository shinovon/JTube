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
package ui.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import Util;
import ui.AppUI;
import ui.UIConstants;
import ui.UIItem;

public class Description extends UIItem implements UIConstants {
	
	private String[] textArr;
	private String text;
	private Font font;
	
	private int h;
	private int lastW;
	
	public Description(String s, Font f) {
		this.font = f;
		this.text = s;
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		if(textArr == null) return;
		g.setFont(font);
		y+=8;
		int fh = 4+font.getHeight();
		g.setColor(AppUI.getColor(COLOR_MAINFG));
		for(int i = 0; i < textArr.length; i++) {
			if(y+fh > 0 && y < ui.getHeight()) {
				g.drawString(textArr[i], x + 8, y, 0);
			}
			y+=fh;
		}
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		if(w == lastW) return;
		lastW = w;
		h = 8;
		textArr = Util.getStringArray(text, w - 20, font);
		h += textArr.length * (font.getHeight() + 4);
	}

}
