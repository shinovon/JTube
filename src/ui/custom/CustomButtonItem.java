/*
Copyright (c) 2022 Arman Jussupgaliyev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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
