package ui.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ui.AppUI;
import ui.UIConstants;
import ui.screens.ChannelScreen;
import Locale;
import LocaleConstants;

public class SubscribeButton extends AbstractButtonItem implements UIConstants {
	
	private ChannelScreen scr;
	private int h;
	
	private long lastTime;

	public SubscribeButton(ChannelScreen scr) {
		this.scr = scr;
	}

	protected void action() {
		if(System.currentTimeMillis()-lastTime < 500) return;
		scr.subscribe();
		lastTime = System.currentTimeMillis();
		//if(action != null) action.run();
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setFont(mediumfont);
		if(hover)
			g.setColor(AppUI.getColor(COLOR_BUTTON_HOVER_BG));
		else
			g.setColor(AppUI.getColor(COLOR_MAINBG));
		g.fillRect(x, y, w, h);
		g.setColor(AppUI.getColor(COLOR_MAINFG));
		Font f = g.getFont();
		String s = scr.subscribed ? Locale.s(LocaleConstants.BTN_Unsubscribe) : Locale.s(LocaleConstants.BTN_Subscribe);
		g.drawString(s, x + (w >> 1), y + ((h - f.getHeight()) >> 1), Graphics.HCENTER);
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