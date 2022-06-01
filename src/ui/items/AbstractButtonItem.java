package ui.items;

import javax.microedition.lcdui.Canvas;

import ui.UIItem;

public abstract class AbstractButtonItem extends UIItem {
	
	protected abstract void action();
	
	protected void tap(int x, int y, int time) {
		if(time <= 275 && time >= 10) action();
	}

	public void keyPress(int i) {
		if(i == Canvas.FIRE || i == -5) action();
	}

}
