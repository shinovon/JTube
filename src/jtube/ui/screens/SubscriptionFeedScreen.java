package jtube.ui.screens;

import java.util.Vector;

import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import jtube.App;
import jtube.Constants;
import jtube.Loader;
import jtube.LocalStorage;
import jtube.Settings;
import jtube.models.ILoader;
import jtube.models.VideoModel;
import jtube.ui.items.VideoItem;

public class SubscriptionFeedScreen extends NavigationScreen implements Runnable, Constants, ILoader {

	private int lastCount;
	private Vector allVideos;
	private String[] subscriptions;
	private int idx;
	private Object lock = new Object();

	public SubscriptionFeedScreen() {
		super("субскрибтионс");
	}
	
	protected void show() { 
		super.show();
		if(lastCount != LocalStorage.getSubsciptions().length && !busy) {
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
		busy = true;
		subscriptions = LocalStorage.getSubsciptions();
		idx = 0;
		try {
			allVideos = new Vector();
			Loader.stop();
			for(int i = 0; i < subscriptions.length; i+=2) {
				Loader.add(this);
			}
			Loader.start();
			// wait till all channels load
			synchronized(lock) {
				lock.wait();
			}
			System.out.println("done " + allVideos.size());
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
		busy = false;
	}

	public void load() {
		if(idx*2 >= subscriptions.length) return;
		try {
			String id = subscriptions[(idx++) * 2];
			JSONObject r = (JSONObject) App.invApi("channels/" + id + "/latest?", "videos(" + VIDEO_FIELDS + ",published)");
			JSONArray j = r.getArray("videos");
			for(int k = 0; k < j.size(); k++) {
				if(k > 10) break;
				allVideos.addElement(j.get(k));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(idx*2 >= subscriptions.length) {
			synchronized(lock) {
				lock.notify();
			}
		}
	}

}
