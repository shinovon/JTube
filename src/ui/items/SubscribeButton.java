package ui.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ui.AppUI;
import ui.UIConstants;
import ui.screens.ChannelScreen;
import Locale;
import LocaleConstants;

public class SubscribeButton extends AbstractButton implements UIConstants {
	
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
		g.setColor(AppUI.getColor(COLOR_CHANNELPAGE_BG));
		g.fillRect(x, y, w, h);
		g.setColor(scr.subscribed ? AppUI.getColor(COLOR_SUBSCRIBED_BG) : AppUI.getColor(COLOR_SUBSCRIBE_BG));
		g.fillRoundRect(x + 8, y + 8, w - 16, 36, 18, 18);
		g.setColor(scr.subscribed ? AppUI.getColor(COLOR_SUBSCRIBED_FG) : AppUI.getColor(COLOR_SUBSCRIBE_FG));
		Font f = g.getFont();
		String s = scr.subscribed ? Locale.s(LocaleConstants.BTN_Unsubscribe) : Locale.s(LocaleConstants.BTN_Subscribe);
		g.drawString(s, x + (w >> 1), y + ((h - f.getHeight()) >> 1), Graphics.HCENTER | Graphics.TOP);
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
		h = 36 + 16;
	}

}