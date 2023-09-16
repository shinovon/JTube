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
package jtube.ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import jtube.App;
import jtube.Constants;
import jtube.Errors;
import jtube.InvidiousException;
import jtube.Loader;
import jtube.LocalStorage;
import jtube.Settings;
import jtube.Util;
import jtube.models.AbstractModel;
import jtube.models.ChannelModel;
import jtube.models.PlaylistModel;
import jtube.models.VideoModel;
import jtube.ui.IModelScreen;
import jtube.ui.Locale;
import jtube.ui.UIItem;
import jtube.ui.UIScreen;
import jtube.ui.items.ChannelTabs;
import jtube.ui.items.ContinueButton;
import jtube.ui.items.SubscribeButton;

public class ChannelScreen extends NavigationScreen implements IModelScreen, Constants, Runnable {

	private ChannelModel channel;

	private boolean shown;

	private UIItem item;
	private SubscribeButton subscribe;
	private ChannelTabs tabs;
	private UIItem continueBtn;

	public int state;
	private String q;
	private boolean next;

	public boolean subscribed;

	private int totalVideos;
	private int shownVideos;
	private String continuation;
	private VideoModel[] videoModels;
	private boolean continuing;

	public ChannelScreen(ChannelModel c) {
		super(c.author);
		this.channel = c;
		menuOptions = new String[] {
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
			JSONObject r = (JSONObject) App.invApi("channels/" + channel.authorId + "/videos?" + (next && continuation != null ? "continuation=" + Util.url(continuation) + "&" : ""), "videos(" + VIDEO_FIELDS +
					(getWidth() >= 320 ? ",viewCount" : "") + "),continuation"
					);
			continuation = r.getNullableString("continuation");
			JSONArray j = r.getArray("videos");
			int l = totalVideos = j.size();
			shownVideos = totalVideos >= LATESTVIDEOS_LIMIT ? LATESTVIDEOS_LIMIT : totalVideos;
			videoModels = new VideoModel[totalVideos];
			for(int i = 0; i < l; i++) {
				VideoModel v = videoModels[i] = new VideoModel(j.getObject(i), this);
				if(i >= LATESTVIDEOS_LIMIT) continue;
				if(Settings.videoPreviews && !Settings.lazyLoad) Loader.add(v);
				UIItem item = v.makeListItem();
				if(item != null) add(item);
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
		if(item == null) item = channel.makePageItem();
		add(item);
		add(subscribe);
		add(tabs);
		try {
			JSONArray j = ((JSONObject) App.invApi("channels/playlists/" + channel.authorId + "?", "playlists(title,playlistId,videoCount)")).getArray("playlists");
			int l = j.size();
			for(int i = 0; i < l && i < PLAYLISTS_LIMIT; i++) {
				addPlaylist(j.getObject(i));
			}
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_search, e);
		}
	}

	protected void show() {
		super.show();
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
		continueBtn = new ContinueButton(this);
		add(item = channel.makePageItem());
		add(subscribe = new SubscribeButton(this));
		add(tabs = new ChannelTabs(this));
	}

	private void addPlaylist(JSONObject j) {
		PlaylistModel p = new PlaylistModel(j, this, channel);
		add(p.makeListItem());
	}

	private void channelSearch(String q) {
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
			for(int i = 0; i < l && i < SEARCH_LIMIT; i++) {
				addVideo(j.getObject(i));
			}
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_search, e);
		}
		Loader.start();
	}

	private void addVideo(JSONObject j) {
		VideoModel v = new VideoModel(j, this);
		if(Settings.videoPreviews && !Settings.lazyLoad) Loader.add(v);
		add(v.makeListItem());
	}

	public void run() {
		if(q != null) {
			channelSearch(q);
			q = null;
			return;
		}
		if(next) {
			state = 0;
			latestVideos();
			next = false;
			return;
		}
		busy = true;
		try {
			if(!channel.extended) {
				channel.extend();
			}
			init();
			if(Settings.videoPreviews) {
				Loader.add(channel);
				Loader.start();
			}
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
		videoModels = null;
		continuation = null;
	}
	
	protected void back() {
		super.back();
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

	// TODO: onUpdate?
	protected void paint(Graphics g, int w, int h) {
		super.paint(g, w, h);
		// подгрузка элементов
		if(state != 1 || continuing || shownVideos < 5 /* || videoModels == null */ || h+(h>>1)-scroll < height) {
			return;
		}
		if(totalVideos > shownVideos) {
			continuing = true;
			int start = shownVideos;
			// remove continue button
			// TODO: выгружать превью?
			// TODO: async
			for(int i = start; i < totalVideos && i < start + LATESTVIDEOS_LIMIT; i++) {
				VideoModel v = videoModels[i];
				shownVideos++;
				if(Settings.videoPreviews && !Settings.lazyLoad) Loader.add(v);
				UIItem item = v.makeListItem();
				if(item != null) add(item);
			}
			if(shownVideos == totalVideos && continuation != null) add(continueBtn);
			continuing = false;
			return;
		}
	}

	public void older() {
		next = true;
		new Thread(this).start();
	}
}
