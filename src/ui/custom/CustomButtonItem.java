package ui.custom;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

public abstract class CustomButtonItem extends CustomItem implements UIConstants {
	
	private ItemCommandListener l;
	private boolean pressed;
	private long pressTime;
	private int pressX;
	private int pressY;

	protected int width;
	protected int height;
	

	protected CustomButtonItem(ItemCommandListener l) {
		super(null);
		setLayout(Item.LAYOUT_EXPAND);
		this.l = l;
	}
	
	public void keyPressed(int i) {
		if(i == Canvas.FIRE || i == -5) {
			callCommandOK();
		}
	}
	
	public void pointerPressed(int x, int y) {
		pressed = true;
		pressTime = System.currentTimeMillis();
		pressX = x;
		pressY = y;
	}
	
	public void pointerDragged(int x, int y) {
		//pressed = false;
	}
	
	public void pointerReleased(int x, int y) {
		long l = System.currentTimeMillis() - pressTime;
		if(pressed && l <= 275) {
			// Symbian^3 does not support CustomItem drag events
			int ax = Math.abs(x - pressX);
			int ay = Math.abs(y - pressY);
			if(ax <= 6 && ay <= 6) {
				callCommandOK();
			}
		}
		pressed = false;
	}

	private void callCommandOK() {
		if(l != null) {
			l.commandAction(null, this);
		}
	}

}
