package ui;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

public abstract class AbstractListScreen extends UIScreen implements UIConstants {
	
	protected Vector items;
	private int layout = 0;
	private int screenHeight;
	private UIItem cItem;
	private boolean needLayout;

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
	
	/**
	 * @param i 0: сверху вниз 1: по центру 2: снизу вверх
	 */
	protected void setLayout(int i) {
		layout = i;
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
	}
	
	public boolean scroll(int units) {
		//System.out.print("scroll " + scroll + (units < 0 ? "" : "+") + units + " ");
		/*if(scroll <= -height + screenHeight && units <= 0) {
			scroll = -height + screenHeight;
			System.out.println("no3");
			return false;
		}*/
		if(scroll + units < -height + screenHeight) {
			scroll = -height + screenHeight;
			//System.out.println("no4");
			return false;
		}
		if(units == 0) return false;
		if(height == 0 || (height <= screenHeight && (units < 0 ? scroll == 0 : true))) {
			//System.out.println("no1");
			return false;
		}
		scroll += units;
		if (scroll > 0) {
			scroll = 0;
		}
		//System.out.println("");
		return true;
	}
	
	public void repaint(UIItem i) {
		int ih = i.getHeight();
		int y = i.getY();
		int sy = scroll + y;
		int h = screenHeight;
		if(sy + ih > 0 && sy < h) {
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
	
	public void keyPress(int i) {
		// TODO: Keys support
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
