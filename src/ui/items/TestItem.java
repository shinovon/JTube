package ui.items;

import javax.microedition.lcdui.Graphics;

import ui.UIItem;
import ui.UIScreen;

public class TestItem extends UIItem {
	
	private static int count = 0;
	private int id;
	
	public TestItem() {
		id = count++;
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setColor(-1);
		g.drawLine(x, y + 47, w, y + 47);
		g.drawString("" + id, x + 10, y + (48 - g.getFont().getHeight()) / 2, 0);
	}

	public int getHeight() {
		return 48;
	}

	protected void layout(int w) {
	}

}
