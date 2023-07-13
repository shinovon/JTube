package jtube.ui.screens;

import jtube.Loader;
import jtube.LocalStorage;
import jtube.Settings;
import jtube.models.ChannelModel;
import jtube.models.VideoModel;
import jtube.ui.items.VideoItem;

public class SubscriptionFeedScreen extends NavigationScreen implements Runnable {

	public SubscriptionFeedScreen() {
		super("субскрибтионс");
	}
	
	protected void show() { 
		super.show();
		new Thread(this).start();
		if(wasHidden) {
			wasHidden = false;
			if(Settings.videoPreviews) {
				// resume loading previews
				Loader.stop();
				for(int i = 0; i < items.size(); i++) {
					Object o = items.elementAt(i);
					if(o instanceof VideoItem) {
						VideoModel v = ((VideoItem)o).getVideo();
						if(v.loaded) continue;
						Loader.add(v);
					}
				}
				Loader.start();
			}
		}
	}
	
	protected void hide() {
		super.hide();
		Loader.stop();
		if(Settings.rmsPreviews) {
			for(int i = 0; i < items.size(); i++) {
				Object o = items.elementAt(i);
				if(o instanceof VideoItem) {
					((VideoItem)o).onHide();
				}
			}
		}
	}

	public void run() {
		clear();
		String[] subscriptions = LocalStorage.getSubsciptions();
		for(int i = 0; i < subscriptions.length; i += 2) {
			add(new ChannelModel(subscriptions[i], subscriptions[i + 1], null).makeListItem());
		}
	}

}
