package ui.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import App;
import ui.AppUI;
import ui.UIConstants;

public class ButtonItem extends AbstractButtonItem implements UIConstants {
	
	private String text;
	private Runnable action;
	private int h;
	
	private long lastTime;

	public ButtonItem(String s, Runnable r) {
		this.text = s;
		this.action = r;
	}

	protected void action() {
		if(System.currentTimeMillis()-lastTime < 500) return;
		if(action != null) App.inst.schedule(action);
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
		g.drawString(text, x + (w - f.stringWidth(text)) / 2, y + (h - f.getHeight()) / 2, 0);
		g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
		g.drawLine(x, y+h-1, w, y+h-1);
		if(isInFocus() && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, y, w-1, h-1);
			g.drawRect(x+1, y+1, w-3, h-3);
		}
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = Math.max(36, mediumfontheight + 8);
	}

}
