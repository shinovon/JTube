package ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;

public abstract class UIScreen {
	
	static {
		if(AppUI.inst == null) {
			System.out.println("UIScreen class initialized before AppUI?!?!");
		}
	}
	
	protected static AppUI ui = AppUI.inst;
	
	private String label;
	private UIScreen parent;
	
	protected float scroll;
	protected int width;
	protected int height;

	protected UIScreen(String label, UIScreen parent) {
		this.label = label;
		this.parent = parent;
	}
	
	protected abstract void paint(Graphics g, int w, int h);
	
	public String getTitle() {
		return label;
	}
	
	public UIScreen getParent() {
		return parent;
	}
	
	public boolean backCommand() {
		return parent != null;
	}
	
	public void repaint() {
		ui.repaint(false);
	}
	
	public void repaint(UIItem item) {
		ui.repaint(false);
	}

	public boolean hideBottomBar() {
		return false;
	}

	public boolean hideTopBar() {
		return false;
	}

	public int getHeight() {
		return height;
	}


	protected void press(int x, int y) {}
	protected void release(int x, int y) {}
	protected void tap(int x, int y, int time) {}
	protected void keyPress(int i) {}
	protected void keyRelease(int i) {}
	protected void keyRepeat(int i) {}
	
	/** -1: назад */
	public void screenCommand(int i) {}
	
	/**
	 * @return true если успешно, false если уже некуда скроллить
	 */
	public boolean scroll(int units) {
		return false;
	}

	public int getWidth() {
		return width;
	}

	public boolean hasScrollBar() {
		return false;
	}

	public void setScrollBarY(int y) {
	}
	
	protected final void clearCommands() {
		ui.removeCommands();
	}

	protected final void addCommand(Command c) {
		ui.addCommand(c);
	}

	public boolean supportCommands() {
		return false;
	}

	protected void relayout() {
	}

	protected boolean isItemSeenOnScreen(UIItem i) {
		return true;
	}

	protected void show() {}

}
