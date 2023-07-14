package jtube.ui.screens;

import jtube.Settings;
import jtube.ui.Locale;
import jtube.ui.items.VideoItem;

public class RecommendationsScreen extends NavigationScreen implements Runnable {

	private boolean shown;

	public RecommendationsScreen() {
		super(Locale.s(TITLE_Recommendations));
		menuOptions = null;
		hasSearch = false;
	}
	
	public void show() {
		super.show();
		if(!shown) {
			new Thread(this).start();
			shown = true;
		}
	}
	
	public void hide() {
		super.hide();
		if(!Settings.videoPreviews) return;
		for(int i = 0; i < items.size(); i++) {
			Object o = items.elementAt(i);
			if(o instanceof VideoItem) {
				((VideoItem)o).unload();
			}
		}
	}
	
	public void run() {
		// TODO
	}
	
}
