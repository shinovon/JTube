/*
Copyright (c) 2023 Arman Jussupgaliyev

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

import jtube.App;
import jtube.ui.AppUI;
import jtube.ui.Locale;
import jtube.ui.LocaleConstants;
import jtube.ui.UIConstants;
import jtube.ui.screens.ChannelScreen;

public class SubscribeButton extends AbstractButton implements UIConstants, Runnable {
	
	private ChannelScreen scr;
	private int h;
	
	private long lastTime;

	public SubscribeButton(ChannelScreen scr) {
		this.scr = scr;
	}

	protected void action() {
		if(System.currentTimeMillis()-lastTime < 500) return;
		App.inst.schedule(this);
		lastTime = System.currentTimeMillis();
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setFont(mediumfont);
		g.setColor(AppUI.getColor(COLOR_CHANNELPAGE_BG));
		g.fillRect(x, y, w, h);
		g.setColor(scr.subscribed ? AppUI.getColor(COLOR_SUBSCRIBED_BG) : AppUI.getColor(COLOR_SUBSCRIBE_BG));
		g.fillRoundRect(x + 8, y + 8, w - 16, 36, 18, 18);
		g.setColor(scr.subscribed ? AppUI.getColor(COLOR_SUBSCRIBED_FG) : AppUI.getColor(COLOR_SUBSCRIBE_FG));
		String s = Locale.s(scr.subscribed ? LocaleConstants.BTN_Unsubscribe : LocaleConstants.BTN_Subscribe);
		g.drawString(s, x + (w >> 1), y + ((h - mediumfont.getHeight()) >> 1), Graphics.HCENTER | Graphics.TOP);
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, y, w-1, h-1); 
		}
	}
 
	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = 52;
	}

	public void run() {
		scr.subscribe();
	}
	
	public int getOKLabel() {
		return scr.subscribed ? LocaleConstants.BTN_Unsubscribe : LocaleConstants.BTN_Subscribe;
	}

}