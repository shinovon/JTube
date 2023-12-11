/*
Copyright (c) 2023 Arman Jussupgaliyev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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
			Loader.stop();
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
