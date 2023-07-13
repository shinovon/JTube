package jtube;
import jtube.ui.AppUI;

public class RunnableTask implements Runnable {

	public static final int ID = 1;
	public static final int SEARCH = 2;
	public static final int REFRESH = 3;
	public static final int SWITCH = 4;
	public static final int WATCH = 5;
	public static final int MAIN = 6;
	public static final int SUBS = 7;
	public static final int LIB = 8;
	
	int type;
	String arg;
	
	public RunnableTask(int i) {
		this.type = i;
	}
	
	public RunnableTask(String s, int i) {
		this.arg = s;
		this.type = i;
	}

	public void run() {
		switch(type) {
		case ID:
			try {
				App.openURL(arg);
			} catch (IllegalArgumentException e) {
				AppUI.inst.openVideo(arg);
			}
			break;
		case SEARCH:
			AppUI.inst.search(arg);
			break;
		case REFRESH:
			AppUI.inst.refresh();
			break;
		case SWITCH:
			AppUI.inst.switchMain();
			break;
		case WATCH:
			App.watch(arg);
			break;
		case MAIN:
			AppUI.inst.loadMain();
			break;
		case SUBS:
			AppUI.inst.loadSubs();
			break;
		case LIB:
			AppUI.inst.loadLib();
			break;
		}
	}

}
