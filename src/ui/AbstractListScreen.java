package ui;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import Util;

public abstract class AbstractListScreen extends UIScreen implements UIConstants {
	
	protected Vector items;
	private int screenHeight;
	private UIItem cItem;
	private boolean needLayout;
	private int scrollTarget = 0;

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
		w -= AppUI.getScrollBarWidth();
		width = w;
		screenHeight = h;
		if(height == 0 || needLayout) {
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
			if(-scroll > height) {
				scroll = -height;
			}
		}
		if(Math.abs(scroll) > 65535) return;
		int y = 0;
		int s = scroll;
		try {
			for (int i = 0; i < items.size(); i++) {
				UIItem it = (UIItem) items.elementAt(i);
				if (it != null) {
					int ih = it.getHeight();
					int sy = s + y;
					if(sy + ih > 0 && sy < h) {
						it.paint(g, w, 0, s+y, scroll);
					}
					y += ih;
				}
			}
			height = y;
		} catch (Exception e) {
			e.printStackTrace();
		}
		g.setColor(AppUI.getColor(COLOR_SCROLLBAR_BG));
		g.fillRect(w, 0, AppUI.getScrollBarWidth(), h);
		g.setColor(AppUI.getColor(COLOR_SCROLLBAR_FG));
		int hh = height;
		if(hh <= 0) hh = 1;
		int sby = (int)(((double)-scroll / (double)hh) * h);
		int sbh = (int)(((double)h / (double)hh) * h);
		g.fillRect(w, sby, AppUI.getScrollBarWidth(), sbh);
		if(scrollTarget <= 0) {
			ui.scrolling = true;
			if (Math.abs(scroll - scrollTarget) < 8) {
				scroll = scrollTarget;
				scrollTarget = 1;
				ui.scrolling = false;
			} else {
				scroll = Util.lerp(scroll, scrollTarget, 15, 100);
			}
			if(scroll > 0) {
				scroll = 0;
			}
			if(scroll < -height + screenHeight) {
				scroll = -height + screenHeight;
			}
		}
	}
	
	public boolean scroll(int units) {
		if(scroll + units < -height + screenHeight) {
			scroll = -height + screenHeight;
			return false;
		}
		if(units == 0) return false;
		if(height == 0 || (height <= screenHeight && (units < 0 ? scroll == 0 : true))) {
			return false;
		}
		scroll += units;
		if (scroll > 0) {
			scroll = 0;
		}
		scrollTarget = 1;
		return true;
	}
	
	public void repaint(UIItem i) {
		if(isItemOnScreen(i)) {
			super.repaint(i);
		}
	}
	
	protected void tap(int x, int y, int time) {
		int yy = 0;
		int sy = scroll + y;
		for (int i = 0; i < items.size(); i++) {
			UIItem it = (UIItem) items.elementAt(i);
			if (it != null) {
				int ih = it.getHeight();
				if(sy > yy && sy < yy + ih) {
					it.tap(x, y, time);
				}
				yy += ih;
			}
		}
	}
	
	public void keyRepeat(int i) {
		if(i == -1 || i == -2) keyPress(i);
	}
	
	public void keyPress(int i) {
		if((!ui.isKeyInputMode() || cItem == null) && ((i >= -7 && i <= -1) || (i >= 1 && i <= 57))) {
			ui.setKeyInputMode();
			selectItem();
		}
		if(i == -1) {
			if(cItem.getY() < -scroll) {
				smoothlyScrollTo(scroll+(screenHeight/3));
			} else {
				if(cItem.getIndex() == 0) {
					if(-scroll < screenHeight/4) {
						smoothlyScrollTo(0);
					}
					return;
				}
				cItem.defocus();
				cItem = (UIItem) items.elementAt(cItem.getIndex()-1);
				cItem.focus();
				if(!isItemOnScreen(cItem, screenHeight/4)) {
					smoothlyScrollTo(-cItem.getY());
				}
			}
		} else if(i == -2) {
			if(cItem.getY()+cItem.getHeight() > -(scroll-screenHeight)) {
				smoothlyScrollTo(scroll-(screenHeight/3));
			} else {
				if(cItem.getIndex() == items.size()-1) return;
				cItem.defocus();
				cItem = (UIItem) items.elementAt(cItem.getIndex()+1);
				cItem.focus();
				if(!isItemOnScreen(cItem, screenHeight/4)) {
					smoothlyScrollTo(-cItem.getY());
				}
			}
			if(scroll < -height + screenHeight) {
				scroll = -height + screenHeight;
			}
		}
		if(i == -5 || i == -6 || i == -7 || i == Canvas.FIRE) {
			if(selectItem()) {
				itemKeyPress(i);
			}
		}
	}
	
	private void itemKeyPress(int i) {
		cItem.keyPress(i);
	}

	// will return false if there is no items
	private boolean selectItem() {
		if(items.size() == 0) {
			cItem = null;
			return false;
		}
		if(cItem == null) {
			for (int i = 0; i < items.size(); i++) {
				UIItem it = (UIItem) items.elementAt(i);
				if (it != null) {
					if(isItemOnScreen(cItem)) {
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
		if(!isItemOnScreen(cItem)) {
			smoothlyScrollTo(-cItem.getY());
		}
		cItem.focus();
		return true;
	}

	private void smoothlyScrollTo(int i) {
		if(i >= 0) i = -1;
		scrollTarget = i;
		repaint();
	}

	private boolean isItemOnScreen(UIItem i) {
		return isItemOnScreen(i, 0);
	}

	private boolean isItemOnScreen(UIItem i, int offset) {
		if(!items.contains(i)) return false;
		int ih = i.getHeight();
		int y = i.getY();
		int sy = scroll + y;
		int h = screenHeight;
		return sy + ih - offset > 0 && sy < h - offset;
	}

	protected void add(UIItem i) {
		if(i == null) throw new NullPointerException("item");
		items.addElement(i);
		i.setScreen(this);
		relayout();
	}

	protected void remove(UIItem i) {
		if(i == null) throw new NullPointerException("item");
		items.removeElement(i);
		relayout();
	}
	
	protected void remove(int i) {
		items.removeElementAt(i);
		relayout();
	}
	
	protected void clear() {
		items.removeAllElements();
		height = 0;
		scroll = 0;
		relayout();
	}
	
	private void relayout() {
		needLayout = true;
		repaint();
	}
	
	protected UIItem get(int i) {
		return (UIItem) items.elementAt(i);
	}

	public boolean hasScrollBar() {
		return true;
	}

	public void setScrollBarY(int y) {
		int hh = height;
		int h = screenHeight;
		scroll = -(int) (((double)hh/(double)h)*y);
	}

}
