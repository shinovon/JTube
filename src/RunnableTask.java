import ui.AppUI;

public class RunnableTask implements Runnable {
	String s;
	int i;
	
	public RunnableTask(int i) {
		this.i = i;
	}
	
	public RunnableTask(String s, int i) {
		this.s = s;
		this.i = i;
	}

	public void run() {
		switch(i) {
		case 1:
			try {
				App.openURL(s);
			} catch (IllegalArgumentException e) {
			}
			break;
		case 2:
			AppUI.inst.search(s);
			break;
		case 3:
			AppUI.inst.refresh();
			break;
		case 4:
			AppUI.inst.switchMain();
			break;
		}
	}

}
