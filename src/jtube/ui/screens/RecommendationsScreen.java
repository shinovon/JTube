package jtube.ui.screens;

import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import jtube.App;
import jtube.Constants;
import jtube.Errors;
import jtube.Loader;
import jtube.Settings;
import jtube.models.VideoModel;
import jtube.ui.Locale;
import jtube.ui.items.VideoItem;

public class RecommendationsScreen extends NavigationScreen implements Runnable, Constants {

	private boolean shown;
	private String videoId;

	public RecommendationsScreen(String videoId) {
		super(Locale.s(TITLE_Recommendations));
		this.videoId = videoId;
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
	
	public void run() {
		try {
			JSONObject r = (JSONObject) App.invApi("videos/" + videoId + "?", "recommendedVideos(" + VIDEO_FIELDS + ")");
			JSONArray j = r.getArray("recommendedVideos");
			int l = j.size();
			for(int i = 0; i < l; i++) {
				VideoModel v = new VideoModel(j.getObject(i));
				if(Settings.videoPreviews && !Settings.lazyLoad) Loader.add(v);
				add(v.makeListItem());
				if(i >= 20) break;
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_latestVideos, e);
		}
		Loader.start();
	}
	
}
