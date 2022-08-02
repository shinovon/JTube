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

import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;


import App;
import Util;
import Errors;
import Locale;
import Settings;
import Constants;
import ui.UIScreen;
import ui.IModelScreen;
import ui.UIItem;
import models.ChannelModel;
import models.PlaylistModel;
import models.VideoModel;
import ui.items.ButtonItem;
import models.AbstractModel;
import ui.AppUI;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;

public class ChannelScreen extends NavigationScreen implements IModelScreen, Constants, Runnable {

	private ChannelModel channel;

	private boolean shown;

	private Runnable playlistsRun = new Runnable() {
		public void run() {
			playlists();
		}
	};
	private Runnable latestRun = new Runnable() {
		public void run() {
			latestVideos();
		}
	};

	private UIItem channelItem;

	public ChannelScreen(ChannelModel c) {
		super(c.getAuthor(), null);
		this.channel = c;
		setSearchText("");
		menuOptions = !topBar ? new String[] {
				Locale.s(CMD_Search),
				Locale.s(CMD_Settings)
		} : new String[] {
				Locale.s(CMD_Settings)
		};
	}
	
	protected void latestVideos() {
		clear();
		add(channelItem);
		add(new ButtonItem(Locale.s(BTN_Playlists), playlistsRun));
		try {
			App.inst.stopAsyncTasks();
			JSONArray j = (JSONArray) App.invApi("v1/channels/" + channel.getAuthorId() + "/latest?", VIDEO_FIELDS +
					(getWidth() >= 320 ? ",publishedText,viewCount" : "")
					);
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = parseAndMakeItem(j.getObject(i), false);
				if(item == null) continue;
				add(item);
				if(i >= LATESTVIDEOS_LIMIT) break;
			}
			App.inst.startAsyncTasks();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_latestVideos, e);
		}
	}

	protected void search() {
		disposeSearchForm();
		App.inst.stopAsyncTasks();
		TextBox t = new TextBox("", "", 256, TextField.ANY);
		t.setCommandListener(this);
		t.setTitle(Locale.s(CMD_Search));
		t.addCommand(searchOkCmd);
		t.addCommand(cancelCmd);
		ui.display(t);
	}

	protected void playlists() {
		clear();
		add(channelItem);
		add(new ButtonItem(Locale.s(BTN_LatestVideos), latestRun));
		App.inst.stopAsyncTasks();
		try {
			JSONArray j = ((JSONObject) App.invApi("v1/channels/playlists/" + channel.getAuthorId() + "?", "playlists,title,playlistId,videoCount")).getArray("playlists");
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = playlist(j.getObject(i));
				if(item == null) continue;
				add(item);
				if(i >= PLAYLISTS_LIMIT) break;
			}
			new Thread() {
				public void run() {
					App.inst.startAsyncTasks();
				}
			}.start();
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_search, e);
		}
	}

	protected void show() {
		super.show();
		if(wasHidden) {
			wasHidden = false;
		}
		if(!shown) {
			shown = true;
			new Thread(this).start();
		}
	}
	
	private void init() {
		scroll = 0;
		AppUI.loadingState = false;
		if(channel == null) return;
		add(channelItem = channel.makePageItem());
		add(new ButtonItem(Locale.s(BTN_Playlists), playlistsRun));
	}

	protected UIItem playlist(JSONObject j) {
		PlaylistModel p = new PlaylistModel(j, this, channel);
		return p.makeListItem();
	}

	protected void search(String q) {
		clear();
		add(channelItem);
		add(new ButtonItem(Locale.s(BTN_LatestVideos), latestRun));
		App.inst.stopAsyncTasks();
		try {
			JSONArray j = (JSONArray) App.invApi("v1/channels/search/" + channel.getAuthorId() + "?q=" + Util.url(q), VIDEO_FIELDS +
					(getWidth() >= 320 ? ",publishedText,viewCount" : "")
					);
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = parseAndMakeItem(j.getObject(i), true);
				if(item == null) continue;
				add(item);
				if(i >= SEARCH_LIMIT) break;
			}
			new Thread() {
				public void run() {
					App.inst.startAsyncTasks();
				}
			}.start();
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_search, e);
		}
	}

	protected UIItem parseAndMakeItem(JSONObject j, boolean search) {
		VideoModel v = new VideoModel(j, this);
		if(Settings.videoPreviews) App.inst.addAsyncTask(v);
		return v.makeListItem();
	}

	public void load() {
		AppUI.loadingState = true;
		try {
			if(!channel.isExtended()) {
				channel.extend();
				init();
			}
			if(Settings.videoPreviews) channel.load();
			latestVideos();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_load, e);
		}
		AppUI.loadingState = false;
	}

	public void run() {
		try {
			load();
		} catch (Exception e) {
		}
	}

	/*public void commandAction(Command c, Displayable d) {
		if(c == okCmd) {
			keyPress(-5);
			return;
		}
		if(c == searchCmd) {
			search();
			return;
		}
		if(c == searchOkCmd && d instanceof TextBox) {
			search(((TextBox) d).getString());
			return;
		}
		if(c == cancelCmd && d instanceof TextBox) {
			ui.display(null);
			return;
		}
		if(c == backCmd) {
			AppUI.loadingState = false;
			App.inst.stopAsyncTasks();
			if(parent != null) {
				ui.setScreen(parent);
			} else {
				ui.back(this);
			}
			ui.disposeChannelPage();
			return;
		}
		super.commandAction(c, d);
	}*/

	private void disposeSearchForm() {
		Util.gc();
	}

	public ChannelModel getChannel() {
		return channel;
	}

	public AbstractModel getModel() {
		return channel;
	}

	public void setContainerScreen(UIScreen s) {
		this.parent = s;
	}

	public void dispose() {
		clear();
		channel.disposeExtendedVars();
		channel = null;
		parent = null;
	}
	
	protected void menuAction(int action) {
		if(!topBar) action--;
		switch(action) {
		case -1:
			openSearchTextBox();
			break;
		case 0:
			ui.showSettings();
			break;
		}
	}
	
	protected void back() {
		super.back();
		ui.disposeChannelPage();
	}
}
