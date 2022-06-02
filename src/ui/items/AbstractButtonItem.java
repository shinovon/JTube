package ui.items;

import javax.microedition.lcdui.Canvas;

import ui.UIItem;

public abstract class AbstractButtonItem extends UIItem {
	
	protected boolean hover;

	protected abstract void action();
	
	protected void press(int x, int y) {
		hover();
	}
	
	protected void release(int x, int y) {
		unhover();
	}
	
	protected void tap(int x, int y, int time) {
		unhover();
		if(time <= 200 && time >= 5) {
			action();
		}
	}

	protected void keyPress(int i) {
		if(i == Canvas.FIRE || i == -5 || i == Canvas.KEY_NUM5) {
			hover();
			action();
		}
	}

	protected void keyRelease(int i) {
		if(i == Canvas.FIRE || i == -5 || i == Canvas.KEY_NUM5) {
			unhover();
		}
	}
	
	public void defocus() {
		super.defocus();
		hover = false;
	}

	protected void hover() {
		hover = true;
	}

	protected void unhover() {
		hover = false;
	}

}
