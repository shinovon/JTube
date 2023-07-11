package jtube;
import jtube.ui.AppUI;

public class RunnableTask implements Runnable {

	public static final int ID = 1;
	public static final int SEARCH = 2;
	public static final int REFRESH = 3;
	public static final int SWITCH = 4;
	public static final int WATCH = 5;
	
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
		case 1:
			try {
				App.openURL(arg);
			} catch (IllegalArgumentException e) {
				AppUI.inst.openVideo(arg);
			}
			break;
		case 2:
			AppUI.inst.search(arg);
			break;
		case 3:
			AppUI.inst.refresh();
			break;
		case 4:
			AppUI.inst.switchMain();
			break;
		case 5:
			App.watch(arg);
			break;
		}
	}

}
