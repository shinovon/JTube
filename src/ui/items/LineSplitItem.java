package ui.items;

import javax.microedition.lcdui.Graphics;

import ui.AppUI;
import ui.UIConstants;
import ui.UIItem;

public class LineSplitItem extends UIItem implements UIConstants {

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
		g.drawLine(x+16, y+4, w-x-16, y+4);
	}

	public int getHeight() {
		return 9;
	}

	protected void layout(int w) {

	}

}
