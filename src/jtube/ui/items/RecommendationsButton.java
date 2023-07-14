package jtube.ui.items;

import javax.microedition.lcdui.Graphics;

import jtube.App;
import jtube.ui.AppUI;
import jtube.ui.Locale;
import jtube.ui.LocaleConstants;
import jtube.ui.UIConstants;
import jtube.ui.screens.VideoScreen;

public class RecommendationsButton extends AbstractButton implements UIConstants, Runnable {
	
	private VideoScreen scr;
	private int h;
	
	private long lastTime;

	public RecommendationsButton(VideoScreen scr) {
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
		g.drawString(Locale.s(LocaleConstants.TITLE_Recommendations), x + (w >> 1), y + ((h - mediumfont.getHeight()) >> 1), Graphics.HCENTER | Graphics.TOP);
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
		
	}
	
	public int getOKLabel() {
		return LocaleConstants.TITLE_Recommendations;
	}

}