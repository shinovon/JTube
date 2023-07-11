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

import ui.AppUI;
import ui.UIConstants;
import ui.UIItem;
import ui.screens.ChannelScreen;

public class ChannelTabs extends UIItem implements UIConstants {
	
	private int h;

	private ChannelScreen scr;

	public ChannelTabs(ChannelScreen scr) {
		this.scr = scr;
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setFont(mediumfont);
		g.setColor(AppUI.getColor(COLOR_MAINFG));
		Font f = g.getFont();
		int halfWidth = w >> 1;
		String s = "Videos";
		g.drawString(s, x + (halfWidth >> 1), y + ((h - f.getHeight()) >> 1), Graphics.HCENTER | Graphics.TOP);

		s = "Playlists";
		g.drawString(s, x + (halfWidth >> 1) + halfWidth, y + ((h - f.getHeight()) >> 1), Graphics.HCENTER | Graphics.TOP);
		g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
		g.drawLine(x, y+h-1, w, y+h-1);
		g.drawLine(x, y, w, y);
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, y, w-1, h-1);
			//g.drawRect(x+1, y+1, w-3, h-3);
		}
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = Math.max(36, mediumfontheight + 8);
	}

}
