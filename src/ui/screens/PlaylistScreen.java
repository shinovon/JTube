package ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import App;
import Util;
import Errors;
import Settings;
import Constants;
import ui.Commands;
import ui.UIScreen;
import ui.ModelScreen;
import ui.UIItem;
import models.VideoModel;
import models.AbstractModel;
import models.PlaylistModel;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;

public class PlaylistScreen extends ModelScreen implements Commands, Constants {

	private PlaylistModel playlist;
	
	private UIScreen containerScreen;

	private JSONArray json;

	private VideoModel[] videos;

	private boolean shown;

	public PlaylistScreen(PlaylistModel p) {
		super(p.getTitle());
		this.playlist = p;
		containerScreen = p.getContainerScreen();
	}
	
	public void show() {
		clearCommands();
		addCommand(backCmd);
		if(!shown) {
			shown = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			App.inst.addAsyncLoad(this);
			App.inst.startAsyncTasks();
		}
	}
	
	public boolean supportCommands() {
		return true;
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
			try {
				if(Settings.videoPreviews) {
					for(int i = 0; i < l && i < 20; i++) {
						if(videos[i] == null) continue;
						videos[i].loadImage();
					}
				}
			} catch (RuntimeException e) {
				throw e;
			} catch (Throwable e) {
				e.printStackTrace();
				App.error(this, Errors.PlaylistForm_init_previews, e);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			App.error(this, Errors.PlaylistForm_init, e);
		}
	}

	private UIItem item(JSONObject j, int i) {
		VideoModel v = new VideoModel(j);
		v.setIndex(i);
		v.setContainerScreen(this);
		videos[i] = v;
		return v.makeListItem();
	}

	public void load() {
		try {
			json = ((JSONObject) App.invApi("v1/playlists/" + playlist.getPlaylistId() + "?",
					PLAYLIST_EXTENDED_FIELDS +
					(getWidth() >= 320 ? ",publishedText,viewCount" : "")
					)).getArray("videos");
			init();
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			App.error(this, Errors.PlaylistForm_load, e);
		}
	}

	public void commandAction(Command c, Displayable d) {
		if(c == backCmd) {
			if(containerScreen != null) {
				ui.setScreen(containerScreen);
			} else {
				ui.back(this);
			}
			dispose();
			return;
		}
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
		this.containerScreen = s;
	}

	public int getLength() {
		return videos.length;
	}
	
	public VideoModel getVideo(int i) {
		return videos[i];
	}

}
