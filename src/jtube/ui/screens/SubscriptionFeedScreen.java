package jtube.ui.screens;

import java.util.Vector;

import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import jtube.App;
import jtube.Constants;
import jtube.Loader;
import jtube.LocalStorage;
import jtube.Settings;
import jtube.models.VideoModel;
import jtube.ui.items.VideoItem;

public class SubscriptionFeedScreen extends NavigationScreen implements Runnable, Constants {

	private int lastCount;

	public SubscriptionFeedScreen() {
		super("субскрибтионс");
	}
	
	protected void show() { 
		super.show();
		if(lastCount != LocalStorage.getSubsciptions().length) {
			new Thread(this).start();
			lastCount = LocalStorage.getSubsciptions().length;
		}
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
		String[] subscriptions = LocalStorage.getSubsciptions();
		try {
			Vector allVideos = new Vector();
			for(int i = 0; i < subscriptions.length; i += 2) {
				String id = subscriptions[i];
				JSONObject r = (JSONObject) App.invApi("channels/" + id + "/latest?", "videos(" + VIDEO_FIELDS + ",published)");
				JSONArray j = r.getArray("videos");
				for(int k = 0; k < j.size(); k++) {
					if(k > 10) break;
					allVideos.addElement(j.get(k));
				}
			}
			// sorting
			for (int i = 0; i < allVideos.size() - 1; i++) {
				for (int j = 0; j < i; j++) {
					if (((JSONObject) allVideos.elementAt(j)).getLong("published") < ((JSONObject) allVideos.elementAt(j + 1)).getLong("published")) {
						Object t = allVideos.elementAt(j);
						allVideos.setElementAt(allVideos.elementAt(j + 1), j);
						allVideos.setElementAt(t, j + 1);
					}
				}
			}
			for(int i = 0; i < allVideos.size(); i++) {
				if(i > 100) break;
				VideoModel v = new VideoModel((JSONObject) allVideos.elementAt(i));
				if(Settings.videoPreviews && !Settings.lazyLoad) Loader.add(v);
				add(v.makeListItem());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
