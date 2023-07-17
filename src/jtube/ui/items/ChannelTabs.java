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

import javax.microedition.lcdui.Graphics;

import jtube.ui.AppUI;
import jtube.ui.Locale;
import jtube.ui.LocaleConstants;
import jtube.ui.UIConstants;
import jtube.ui.UIItem;
import jtube.ui.screens.ChannelScreen;

public class ChannelTabs extends UIItem implements UIConstants, LocaleConstants, Runnable {

	private int w;
	private int h;

	private ChannelScreen scr;
	private int select;

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
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x+(int)(tw * (scr.state - 1)), y, tw, h-1);
		}
		g.setColor(AppUI.getColor(COLOR_CHANNELTAB_SELECT));
		g.fillRect(x + (tw * (scr.state - 1)), y+h-2, tw, 2);
	}
	
	protected void tap(int x, int y, int time) {
		if(time > 5 && time < 250) {
			select(x / (w / 3) + 1);
		}
	}
	
	protected boolean keyPress(int key) {
		if(key == -3) {
			if(scr.state <= 1) return false;
			select(scr.state - 1);
			return true;
		}
		if(key == -4) {
			if(scr.state == 3) return false;
			select(scr.state + 1);
			return true;
		}
		if(key == -5 && scr.state == 3) {
			select(3);
			return true;
		}
		return false;
	}
	
	public int getOKLabel() {
		if(scr.state == 3) {
			return LocaleConstants.CMD_Search;
		}
		return -1;
	}
	
	private void select(int i) {
		if((select = i) == 3) {
			scr.channelSearch();
			return;
		}
		new Thread(this).start();
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		this.w = w;
		h = Math.max(36, mediumfontheight + 8);
	}

	public void run() {
		switch(select) {
		case 1:
			scr.latestVideos();
			break;
		case 2:
			scr.playlists();
			break;
		}
	}

}
