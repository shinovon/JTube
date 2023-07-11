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

import javax.microedition.lcdui.Graphics;

import ui.AppUI;
import ui.UIConstants;
import ui.UIItem;
import ui.screens.ChannelScreen;
import Locale;
import LocaleConstants;

public class ChannelTabs extends UIItem implements UIConstants, LocaleConstants {

	private int w;
	private int h;

	private ChannelScreen scr;

	private int selected;

	public ChannelTabs(ChannelScreen scr) {
		this.scr = scr;
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setFont(smallfont);
		g.setColor(AppUI.getColor(COLOR_CHANNELPAGE_BG));
		g.fillRect(x, y, w, h);
		g.setColor(AppUI.getColor(COLOR_MAINFG));
		int tw = w / 3;
		String s = Locale.s(BTN_Videos);
		g.drawString(s, x + (tw >> 1), y + ((h - smallfontheight) >> 1), Graphics.HCENTER | Graphics.TOP);
		s = Locale.s(BTN_Playlists);
		g.drawString(s, x + (tw >> 1) + tw, y + ((h - smallfontheight) >> 1), Graphics.HCENTER | Graphics.TOP);
		s = Locale.s(CMD_Search);
		g.drawString(s, x + (tw >> 1) + tw + tw, y + ((h - smallfontheight) >> 1), Graphics.HCENTER | Graphics.TOP);
		g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
		g.drawLine(x, y+h-1, w, y+h-1);
		g.drawLine(x, y, w, y);
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x+(int)(tw * selected), y, tw, h-1);
		}
		g.setColor(AppUI.getColor(COLOR_CHANNELTAB_SELECT));
		g.fillRect(x + (tw * selected), y+h-2, tw, 2);
	}
	
	protected void tap(int x, int y, int time) {
		if(time > 5 && time < 250) {
			select(selected = x / (w / 3));
		}
	}
	
	protected void keyPress(int key) {
		if(key == -3) {
			if(selected == 0) return;
			select(selected--);
			return;
		}
		if(key == -4) {
			if(selected == 2) return;
			select(selected++);
			return;
		}
		if(key == -5 && selected == 2) {
			select(2);
		}
	}
	
	private void select(int i) {
		switch(i) {
		case 0:
			scr.latestVideos();
			break;
		case 1:
			scr.playlists();
			break;
		case 2:
			scr.channelSearch();
			break;
		}
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		this.w = w;
		h = Math.max(36, mediumfontheight + 8);
	}

}
