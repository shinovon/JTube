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
package jtube.ui;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

import jtube.Util;

public abstract class AbstractListScreen extends UIScreen implements UIConstants {
	
	protected Vector items;
	private UIItem cItem;
	
	private int screenHeight;
	private int scrollTarget = 0;
	
	private boolean needLayout;
	
	private long lastRepeat;
	
	private int scrollTimer;

	protected AbstractListScreen(String label, UIScreen parent, Vector v) {
		super(label, parent);
		items = v;
	}

	protected AbstractListScreen(String label, UIScreen parent) {
		this(label, parent, new Vector());
	}

	protected AbstractListScreen(Vector v) {
		this(null, null, new Vector());
	}

	protected void paint(Graphics g, int w, int h) {
		boolean sizeChanged = width != w;
		width = w;
		screenHeight = h;
		if(height == 0 || needLayout || sizeChanged) {
			needLayout = false;
			int y = 0;
			for (int i = 0; i < items.size(); i++) {
				UIItem it = (UIItem) items.elementAt(i);
				if (it != null) {
					it.layout(w);
					it.setY(y);
					it.setIndex(i);
					y += it.getHeight();
				}
			}
			height = y;
		}
		if(sizeChanged) {
			scrollToFocusedItem();
		}
		if(cItem == null && size() > 0 && ui.keyInput) {
			selectItem();
		}
		if(Math.abs(scroll) > 65535) return;
		if(scroll < -height + screenHeight && scroll != 0 && !ui.scrolling) {
			scroll = -height + screenHeight;
		}
		if(scroll > 0) {
			scroll = 0;
		}
		int y = 0;
		int s = (int)scroll;
		//int j = 0;
		try {
			for (int i = 0; i < items.size(); i++) {
				UIItem it = (UIItem) items.elementAt(i);
				if (it != null) {
					int ih = it.getHeight();
					int sy = s + y;
					if(sy + ih > 0 && sy < h) {
						if(it.hidden) it.onShow();
						it.paint(g, w, 0, s+y, (int)scroll);
						//j = i;
					} else if(!it.hidden) {
						it.onHide();
					}/* else if(j+1 == i) {
						it.onShow();
					}*/
					y += ih;
				}
			}
			height = y;
		} catch (Exception e) {
		}
		if(scrollTimer < 1 && height > 0 && height > screenHeight) {
			scrollTimer++;
			g.setColor(AppUI.getColor(COLOR_SCROLLBAR_FG));
			int sw = 4;
			int hh = height;
			if(hh <= 0) hh = 1;
			int sby = (int)(((float)-scroll / (float)hh) * h);
			int sbh = (int)(((float)h / (float)hh) * h);
			g.fillRect(w-sw, sby, sw-2, sbh);
		}
		if(scrollTarget <= 0) {
			scrollTimer = 0;
			ui.scrolling = true;
			if (Math.abs(scroll - scrollTarget) < 1) {
				scroll = scrollTarget;
				scrollTarget = 1;
				ui.scrolling = false;
			} else {
				scroll = Util.lerp(scroll, scrollTarget, ui.repaintTime > 33 ? 4 * ui.repaintTime / 33f : 4, 20);
			}
			if(scroll > 0) {
				scroll = 0;
				scrollTarget = 1;
				ui.scrolling = false;
			}
			if(scroll < -height + screenHeight) {
				scroll = -height + screenHeight;
				scrollTarget = 1;
				ui.scrolling = false;
			}
		}
		/*
		g.setColor(AppUI.getColor(COLOR_SCROLLBAR_BORDER));
		g.drawLine(w, 0, w+sw, 0);
		g.drawLine(w, 0, w, h);
		g.drawLine(w+sw-1, 0, w+sw-1, h);
		g.drawLine(w, h-1, w+sw, h-1);
		*/
	}
	
	protected boolean scroll(int units) {
		if(busy) return false;
		if(height == 0 || height <= screenHeight) {
			scroll = 0;
			return false;
		}
		if(scroll + units < -height + screenHeight) {
			scroll = -height + screenHeight;
			return false;
		}
		if(units == 0) return false;
		scroll += units;
		if (scroll > 0) {
			scroll = 0;
			return false;
		}
		scrollTimer = 0;
		scrollTarget = 1;
		return true;
	}
	
	public void repaint(UIItem i) {
		if(isItemSeenOnScreen(i)) {
			ui.repaint();
		}
	}
	
	protected void press(int x, int y) {
		if(busy) return;
		int yy = 0;
		int sy = (int)scroll;
		for (int i = 0; i < items.size(); i++) {
			UIItem it = (UIItem) items.elementAt(i);
			if (it != null) {
				int ih = it.getHeight();
				if(y > sy + yy && y < sy + yy + ih) {
					focusItem(it);
					it.press(x, (int)scroll + y - yy);
				}
				yy += ih;
			}
		}
	}
	
	protected void release(int x, int y) {
		if(busy) return;
		int yy = 0;
		int sy = (int)scroll;
		for (int i = 0; i < items.size(); i++) {
			UIItem it = (UIItem) items.elementAt(i);
			if (it != null) {
				int ih = it.getHeight();
				if(y > sy + yy && y < sy + yy + ih) {
					focusItem(it);
					it.release(x, (int)scroll + y - yy);
				}
				yy += ih;
			}
		}
	}
	
	protected void tap(int x, int y, int time) {
		int yy = 0;
		int sy = (int)scroll;
		for (int i = 0; i < items.size(); i++) {
			UIItem it = (UIItem) items.elementAt(i);
			if (it != null) {
				int ih = it.getHeight();
				if(y > sy + yy && y < sy + yy + ih) {
					focusItem(it);
					it.tap(x, (int)scroll + y - yy, time);
				}
				yy += ih;
			}
		}
	}
	
	protected void keyPress(int i) {
		if(busy) return;
		if(!ui.isKeyInputMode() && ((i >= -7 && i <= -1) || (i >= 1 && i <= 57))) {
			ui.setKeyInputMode();
		}
		if(cItem == null) selectItem();
		if(i == -1) {
			if(cItem == null) return;
			if(cItem.getY() < -scroll) {
				smoothlyScrollTo((int)scroll+(screenHeight/3));
			} else {
				if(cItem.getListPosition() == 0) {
					if(-scroll < screenHeight/4) {
						smoothlyScrollTo(0);
					}
					return;
				}
				if(cItem != getFirstFocusableItem()) {
					UIItem item = (UIItem) items.elementAt(cItem.getListPosition()-1);
					while(!item.canBeFocused() && item.getListPosition() != 0) {
						item = (UIItem) items.elementAt(item.getListPosition()-1);
					}
					focusItem(item);
				}
				if(!isItemSeenOnScreen(cItem, 24)) {
					smoothlyScrollTo(-cItem.getY());
				}
			}
		} else if(i == -2) {
			if(cItem == null) return;
			if(cItem.getY()+cItem.getHeight() > -(scroll-screenHeight)) {
				smoothlyScrollTo((int)scroll-(screenHeight/3));
			} else {
				if(cItem.getListPosition() == items.size()-1) return;
				UIItem item = (UIItem) items.elementAt(cItem.getListPosition()+1);
				while(!item.canBeFocused() && item.getListPosition() != items.size()-1) {
					item = (UIItem) items.elementAt(item.getListPosition()+1);
				}
				focusItem(item);
				if(!isItemSeenOnScreen(cItem, 24)) {
					smoothlyScrollTo(-cItem.getY());
				}
			}
			if(scroll < -height + screenHeight) {
				scroll = -height + screenHeight;
			}
		}
		if(i <= -3 && i >= -7 && cItem != null) {
			cItem.keyPress(i);
		}
	}
	
	protected void keyRelease(int i) {
		if(busy) return;
		if(i <= -3 && i >= -7 && cItem != null) {
			cItem.keyRelease(i);
		}
	}
	
	protected void keyRepeat(int i) {
		if(busy) return;
		if(System.currentTimeMillis()-lastRepeat < 100) return;
		if(cItem != null && (i == -1 || i == -2)) {
			if(i == -1) {
				if(cItem.getListPosition() == 0 || cItem == getFirstFocusableItem()) {
					if(-scroll < screenHeight/4) {
						smoothlyScrollTo(0);
					}
					return;
				}
				focusItem((UIItem) items.elementAt(cItem.getListPosition()-1));
			} else if(i == -2) {
				if(cItem.getListPosition() == items.size() - 1) {
					if(cItem.getY()+cItem.getHeight() > -(scroll-screenHeight)) {
						smoothlyScrollTo((int)scroll-(screenHeight/3));
					}
					return;
				}
				focusItem((UIItem) items.elementAt(cItem.getListPosition()+1));
			}
			smoothlyScrollTo(-cItem.getY());
			if(scroll < -height + screenHeight) {
				scroll = -height + screenHeight;
			}
		}
		lastRepeat = System.currentTimeMillis();
	}

	// will return false if there is no items
	private boolean selectItem() {
		if(items.size() == 0) {
			cItem = null;
			return false;
		}
		try {
			if(cItem == null) {
				for (int i = 0; i < items.size(); i++) {
					UIItem it = (UIItem) items.elementAt(i);
					if (it != null) {
						if(isItemSeenOnScreen(cItem)) {
							cItem = it;
							cItem.focus();
							return true;
						}
					}
				}
				if(cItem == null) {
					cItem = (UIItem) items.elementAt(0);
					scroll = 0;
					cItem.focus();
					return true;
				}
			}
			if(!isItemSeenOnScreen(cItem)) {
				smoothlyScrollTo(-cItem.getY());
			}
			cItem.focus();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private UIItem getFirstFocusableItem() {
		int size = items.size();
		if(size == 0) {
			return null;
		}
		for(int i = 0; i < size; i++) {
			UIItem item = (UIItem) items.elementAt(i);
			if(item != null && item.canBeFocused()) return item;
		}
		return null;
	}
	
	private void scrollToFocusedItem() {
		if(cItem == null || items.size() == 0) return;
		if(!isItemSeenOnScreen(cItem)) {
			smoothlyScrollTo(-cItem.getY());
		}
	}

	private void smoothlyScrollTo(int i) {
		if(i > 0) i = 0;
		if(ui.fastScrolling()) {
			scroll = i;
		} else {
			scrollTarget = i;
		}
	}

	protected boolean isItemSeenOnScreen(UIItem i) {
		return isItemSeenOnScreen(i, 0);
	}

	protected boolean isItemSeenOnScreen(UIItem i, int offset) {
		if(!items.contains(i)) return false;
		int ih = i.getHeight();
		int y = i.getY();
		int sy = (int)scroll + y;
		int h = screenHeight;
		return sy + ih - offset > 0 && sy < h - offset;
	}

	public void add(UIItem i) {
		if(i == null) throw new NullPointerException("item");
		items.addElement(i);
		i.setScreen(this);
		relayout();
	}

	public void remove(UIItem i) {
		if(i == null) throw new NullPointerException("item");
		items.removeElement(i);
		i.setScreen(null);
		relayout();
	}
	
	public void remove(int i) {
		UIItem it = (UIItem) items.elementAt(i);
		it.setScreen(null);
		items.removeElementAt(i);
		relayout();
	}
	
	protected void clear() {
		for(int i = 0; i < items.size(); i++) {
			UIItem it = (UIItem) items.elementAt(i);
			it.setScreen(null);
		}
		items.removeAllElements();
		height = 0;
		scroll = 0;
	}
	
	protected void relayout() {
		needLayout = true;
		if(width != 0) repaint();
	}
	
	protected UIItem get(int i) {
		return (UIItem) items.elementAt(i);
	}

	public boolean hasScrollBar() {
		return false;
	}

	public void setScrollBarY(int y) {
		int hh = height;
		int h = screenHeight;
		float sbh = (((float)h / (float)hh) * h);
		scroll = -(int) (((float)hh/(float)h) * (y - (sbh / 2)));
		if(scroll > 0) scroll = 0;
		if(scroll < -height + screenHeight) {
			scroll = -height + screenHeight;
		}
	}

	private void focusItem(UIItem it) {
		if(cItem == it) return;
		if(cItem != null) {
			cItem.defocus();
		}
		cItem = it;
		it.focus();
	}
	
	public int size() {
		return items.size();
	}

	public UIItem getCurrentItem() {
		return cItem;
	}
}
