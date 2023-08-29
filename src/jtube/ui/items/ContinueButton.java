package jtube.ui.items;

import javax.microedition.lcdui.Graphics;

import jtube.App;
import jtube.ui.AppUI;
import jtube.ui.Locale;
import jtube.ui.LocaleConstants;
import jtube.ui.UIConstants;
import jtube.ui.screens.ChannelScreen;

public class ContinueButton extends AbstractButton implements UIConstants, Runnable {
	
	private ChannelScreen scr;
	private long lastTime;

	public ContinueButton(ChannelScreen scr) {
		this.scr = scr;
	}

	protected void action() {
		if(System.currentTimeMillis()-lastTime < 500) return;
		App.inst.schedule(this);
		lastTime = System.currentTimeMillis();
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setFont(mediumfont);
		g.setColor(AppUI.getColor(COLOR_MAINFG));
		g.drawString(Locale.s(LocaleConstants.BTN_OlderVideos), x + (w >> 1), y + ((36 - mediumfont.getHeight()) >> 1), Graphics.HCENTER | Graphics.TOP);
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, y, w-1, 35); 
		}
	}

	public int getHeight() {
		return 36;
	}

	protected void layout(int w) {
	}

	public void run() {
		scr.older();
	}
	
	public int getOKLabel() {
		return LocaleConstants.BTN_OlderVideos;
	}

}
