package jtube.ui;

import jtube.ui.screens.NavigationScreen;

public class SearchSuggestionsThread extends Thread {
	
	private int i;
	private boolean b;

	public final void run() {
		try {
			while(true) {
				if(b) {
					if(i > 0) i--;
					else {
						UIScreen screen = AppUI.inst.getCurrentScreen();
						if(screen instanceof NavigationScreen) {
							((NavigationScreen) screen).loadSuggestions();
						}
						b = false;
					}
				}
				Thread.sleep(500L);
				Thread.yield();
			}
		} catch (InterruptedException e) {
		}
	}
	
	public void schedule() {
		b = true;
		i = 2;
	}

}
