/*
Copyright (c) 2022 Arman Jussupgaliyev

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
package ui.screens;

import App;
import Util;
import Loader;
import Errors;
import Settings;
import Constants;
import ui.UIScreen;
import ui.IModelScreen;
import ui.UIItem;
import models.VideoModel;
import models.AbstractModel;
import models.PlaylistModel;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;

public class PlaylistScreen extends NavigationScreen implements IModelScreen, Constants, Runnable {

	private PlaylistModel playlist;

	private JSONArray json;

	private VideoModel[] videos;

	private boolean shown;

	public PlaylistScreen(PlaylistModel p) {
		super(p.getTitle(), p.getContainerScreen());
		this.playlist = p;
		menuOptions = null;
		hasSearch = false;
	}
	
	public void show() {
		super.show();
		if(!shown) {
			shown = true;
			Loader.stop();
			new Thread(this).start();
		}
	}
	
	private void init() {
		scroll = 0;
		if(playlist == null) return;
		try {
			int l = json.size();
			Util.gc();
			videos = new VideoModel[l];
			for(int i = 0; i < l; i++) {
				UIItem item = item(json.getObject(i), i);
				if(item == null) continue;
				add(item);
				Util.gc();
			}
			json = null;
			Util.gc();
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			App.error(this, Errors.PlaylistForm_init, e);
		}
		Loader.start();
	}

	private UIItem item(JSONObject j, int i) {
		VideoModel v = new VideoModel(j);
		v.setIndex(i);
		v.setContainerScreen(this);
		if(Settings.videoPreviews && i < 20) {
			Loader.add(v);
		}
		videos[i] = v;
		return v.makeListItem();
	}

	public void load() {
		try {
			json = ((JSONObject) App.invApi("playlists/" + playlist.getPlaylistId() + "?",
					PLAYLIST_EXTENDED_FIELDS +
					(getWidth() >= 320 ? ",publishedText,viewCount" : "")
					)).getArray("videos");
			init();
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			App.error(this, Errors.PlaylistForm_load, e);
		}
	}
	
	public void run() {
		load();
	}
	
	protected void back() {
		super.back();
		dispose();
	}

	private void dispose() {
		clear();
		playlist.disposeExtendedVars();
		playlist = null;
		videos = null;
		Util.gc();
	}

	public AbstractModel getModel() {
		return playlist;
	}

	public void setContainerScreen(UIScreen s) {
		parent = s;
	}

	public int getLength() {
		return videos.length;
	}

	protected void menuAction(int action) {
	}
	
	public VideoModel getVideo(int i) {
		return videos[i];
	}

}
