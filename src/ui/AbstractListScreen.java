package ui;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import Util;

public abstract class AbstractListScreen extends UIScreen implements UIConstants {
	
	protected Vector items;
	private UIItem cItem;
	
	private int screenHeight;
	private int scrollTarget = 0;
	
	private boolean needLayout;
	
	private long lastRepeat;

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
			e.printStackTrace();
		}
		g.setColor(AppUI.getColor(COLOR_SCROLLBAR_BG));
		g.fillRect(w, 0, AppUI.getScrollBarWidth(), h);
		g.setColor(AppUI.getColor(COLOR_SCROLLBAR_FG));
		int hh = height;
		if(hh <= 0) hh = 1;
		int sby = (int)(((float)-scroll / (float)hh) * h);
		int sbh = (int)(((float)h / (float)hh) * h);
		g.fillRect(w, sby, AppUI.getScrollBarWidth(), sbh);
		if(scrollTarget <= 0) {
			ui.scrolling = true;
			if (Math.abs(scroll - scrollTarget) < 1) {
				scroll = scrollTarget;
				scrollTarget = 1;
				ui.scrolling = false;
			} else {
				scroll = Util.lerp(scroll, scrollTarget, 4, 20);
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
	}
	
	public boolean scroll(int units) {
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
		scrollTarget = 1;
		return true;
	}
	
	public void repaint(UIItem i) {
		if(isItemSeenOnScreen(i)) {
			super.repaint(i);
		}
	}
	
	protected void press(int x, int y) {
		int yy = 0;
		int sy = (int)scroll;
		for (int i = 0; i < items.size(); i++) {
			UIItem it = (UIItem) items.elementAt(i);
			if (it != null) {
				int ih = it.getHeight();
				if(y > sy + yy && y < sy + yy + ih) {
					if(cItem != null) cItem.defocus();
					cItem = it;
					it.focus();
					it.press(x, (int)scroll + y - yy);
				}
				yy += ih;
			}
		}
	}
	
	protected void release(int x, int y) {
		int yy = 0;
		int sy = (int)scroll;
		for (int i = 0; i < items.size(); i++) {
			UIItem it = (UIItem) items.elementAt(i);
			if (it != null) {
				int ih = it.getHeight();
				if(y > sy + yy && y < sy + yy + ih) {
					if(cItem != null) cItem.defocus();
					cItem = it;
					it.focus();
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
					if(cItem != null) cItem.defocus();
					cItem = it;
					it.focus();
					it.tap(x, (int)scroll + y - yy, time);
				}
				yy += ih;
			}
		}
	}
	
	protected void keyPress(int i) {
		if((!ui.isKeyInputMode() || cItem == null) && ((i >= -7 && i <= -1) || (i >= 1 && i <= 57))) {
			ui.setKeyInputMode();
			selectItem();
		}
		if(i == -1) {
			if(cItem.getY() < -scroll) {
				smoothlyScrollTo((int)scroll+(screenHeight/3));
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
				if(!isItemSeenOnScreen(cItem, screenHeight/4)) {
					smoothlyScrollTo(-cItem.getY());
				}
			}
		} else if(i == -2) {
			if(cItem.getY()+cItem.getHeight() > -(scroll-screenHeight)) {
				smoothlyScrollTo((int)scroll-(screenHeight/3));
			} else {
				if(cItem.getIndex() == items.size()-1) return;
				cItem.defocus();
				cItem = (UIItem) items.elementAt(cItem.getIndex()+1);
				cItem.focus();
				if(!isItemSeenOnScreen(cItem, screenHeight/4)) {
					smoothlyScrollTo(-cItem.getY());
				}
			}
			if(scroll < -height + screenHeight) {
				scroll = -height + screenHeight;
			}
		}
		if(i == -5 || i == -6 || i == -7 || i == Canvas.FIRE || i == Canvas.KEY_NUM5) {
			if(selectItem()) {
				cItem.keyPress(i);
			}
		}
	}
	
	protected void keyRelease(int i) {
		if(i == -5 || i == -6 || i == -7 || i == Canvas.FIRE || i == Canvas.KEY_NUM5) {
			if(selectItem()) {
				cItem.keyRelease(i);
			}
		}
	}
	
	protected void keyRepeat(int i) {
		if(System.currentTimeMillis()-lastRepeat < 200) return;
		if(i == -1 || i == -2) {
			if(i == -1) {
				if(cItem.getIndex() == 0) {
					if(-scroll < screenHeight/4) {
						smoothlyScrollTo(0);
					}
					return;
				}
				cItem.defocus();
				cItem = (UIItem) items.elementAt(cItem.getIndex()-1);
			} else if(i == -2) {
				if(cItem.getIndex() == items.size()-1) return;
				cItem.defocus();
				cItem = (UIItem) items.elementAt(cItem.getIndex()+1);
			}
			cItem.focus();
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
	}

	private void smoothlyScrollTo(int i) {
		if(i >= 0) i = -1;
		scrollTarget = i;
		repaint();
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
	
	protected void relayout() {
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
		scroll = -(int) (((float)hh/(float)h)*y);
		if(scroll > 0) scroll = 0;
		if(scroll < -height + screenHeight) {
			scroll = -height + screenHeight;
		}
	}

}
