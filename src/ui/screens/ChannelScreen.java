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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;


import App;
import Util;
import Loader;
import Errors;
import Locale;
import Settings;
import Constants;
import InvidiousException;
import ui.UIScreen;
import ui.IModelScreen;
import ui.UIItem;
import models.ChannelModel;
import models.PlaylistModel;
import models.VideoModel;
import ui.items.ChannelTabs;
import ui.items.SubscribeButton;
import models.AbstractModel;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import LocalStorage;

public class ChannelScreen extends NavigationScreen implements IModelScreen, Constants, Runnable {

	private ChannelModel channel;

	private boolean shown;

	private UIItem item;

	public int state;

	public boolean subscribed;

	private SubscribeButton subscribe;

	private ChannelTabs tabs;

	private String q;

	public ChannelScreen(ChannelModel c) {
		super(c.author, null);
		this.channel = c;
		menuOptions = !topBar ? new String[] {
				Locale.s(CMD_Search),
				Locale.s(CMD_Settings),
				Locale.s(CMD_FuncMenu)
		} : new String[] {
				Locale.s(CMD_Settings),
				Locale.s(CMD_FuncMenu)
		};
	}
	
	public void latestVideos() {
		if(state == 1) return;
		state = 1;
		clear();
		add(item);
		add(subscribe);
		add(tabs);
		try {
			Loader.stop();
			JSONObject r = (JSONObject) App.invApi("channels/" + channel.authorId + "/latest?", VIDEO_FIELDS +
					(getWidth() >= 320 ? ",publishedText,viewCount" : "") + ",videos"
					);
			JSONArray j = r.getArray("videos");
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = parseAndMakeItem(j.getObject(i), false);
				if(item == null) continue;
				add(item);
				if(i >= LATESTVIDEOS_LIMIT) break;
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_latestVideos, e);
		}
		Loader.start();
	}

	public void channelSearch() {
		disposeSearchForm();
		Loader.stop();
		TextBox t = new TextBox("", "", 256, TextField.ANY);
		t.setCommandListener(this);
		t.setTitle(Locale.s(CMD_Search));
		t.addCommand(searchOkCmd);
		t.addCommand(backCmd);
		ui.display(t);
	}

	public void playlists() {
		if(state == 2) return;
		state = 2;
		clear();
		add(item);
		add(subscribe);
		add(tabs);
		Loader.stop();
		try {
			JSONArray j = ((JSONObject) App.invApi("channels/playlists/" + channel.authorId + "?", "playlists,title,playlistId,videoCount")).getArray("playlists");
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = playlist(j.getObject(i));
				if(item == null) continue;
				add(item);
				if(i >= PLAYLISTS_LIMIT) break;
			}
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_search, e);
		}
		Loader.start();
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
		busy = false;
		if(channel == null) return;
		subscribed = LocalStorage.isSubscribed(channel.authorId);
		add(item = channel.makePageItem());
		add(subscribe = new SubscribeButton(this));
		add(tabs = new ChannelTabs(this));
	}

	protected UIItem playlist(JSONObject j) {
		PlaylistModel p = new PlaylistModel(j, this, channel);
		return p.makeListItem();
	}

	private void channelSearch(String q) {
		q = null;
		state = 3;
		clear();
		add(item);
		add(tabs);
		Loader.stop();
		try {
			JSONArray j = (JSONArray) App.invApi("channels/search/" + channel.authorId + "?q=" + Util.url(q), VIDEO_FIELDS +
					(getWidth() >= 320 ? ",publishedText,viewCount" : "")
					);
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = parseAndMakeItem(j.getObject(i), true);
				if(item == null) continue;
				add(item);
				if(i >= SEARCH_LIMIT) break;
			}
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_search, e);
		}
		Loader.start();
	}

	protected UIItem parseAndMakeItem(JSONObject j, boolean search) {
		VideoModel v = new VideoModel(j, this);
		if(Settings.videoPreviews) Loader.add(v);
		return v.makeListItem();
	}

	public void run() {
		if(q != null) {
			channelSearch(q);
			return;
		}
		/*if(state > 1) {
			latestVideos();
			return;
		}
		if(state == 1) {
			playlists();
			return;
		}*/
		busy = true;
		try {
			if(!channel.extended) {
				channel.extend();
				init();
			}
			if(Settings.videoPreviews) channel.load();
			latestVideos();
		} catch (InvidiousException e) {
			App.error(this, Errors.ChannelForm_load, e);
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_load, e);
		}
		busy = false;
	}

	public void commandAction(Command c, Displayable d) {
		if(c == searchOkCmd && d instanceof TextBox) {
			q = ((TextBox) d).getString();
			new Thread(this).start();
			return;
		}
		if(c == backCmd && d instanceof TextBox) {
			ui.display(null);
			return;
		}
		super.commandAction(c, d);
	}

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
		default:
			super.menuAction(action);
		}
	}
	
	protected void back() {
		super.back();
		ui.disposeChannelPage();
	}

	public void subscribe() {
		if(busy) return;
		if(subscribed) {
			LocalStorage.removeSubscription(channel.authorId);
		} else {
			LocalStorage.addSubscription(channel.authorId, channel.author);
		}
		subscribed = !subscribed;
	}
}
