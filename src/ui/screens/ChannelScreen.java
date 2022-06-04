package ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;


import App;
import Util;
import Errors;
import Locale;
import Settings;
import Constants;
import ui.Commands;
import ui.UIScreen;
import ui.ModelScreen;
import ui.UIItem;
import ui.items.LabelItem;
import models.ChannelModel;
import models.PlaylistModel;
import models.VideoModel;
import ui.items.ButtonItem;
import models.AbstractModel;
import ui.AbstractListScreen;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;

public class ChannelScreen extends ModelScreen implements Commands, CommandListener, Constants {

	private ChannelModel channel;
	
	private UIScreen containerScreen;

	private boolean shown;
	
	protected AbstractListScreen searchScr;
	protected AbstractListScreen playlistsScr;

	private Runnable playlistsRun = new Runnable() {
		public void run() {
			playlists();
		}
	};

	private boolean okAdded;
	
	private Command okCmd = new Command("OK", Command.OK, 5);

	public ChannelScreen(ChannelModel c) {
		super(c.getAuthor());
		this.channel = c;
		add(new LabelItem(Locale.s(TITLE_Loading)));
	}
	
	protected void latestVideos() {
		//App.inst.stopDoingAsyncTasks();
		try {
			Thread.sleep(100);
			JSONArray j = (JSONArray) App.invApi("v1/channels/" + channel.getAuthorId() + "/latest?", VIDEO_FIELDS + (Settings.videoPreviews ? ",videoThumbnails" : ""));
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = parseAndMakeItem(j.getObject(i), false);
				if(item == null) continue;
				add(item);
				if(i >= LATESTVIDEOS_LIMIT) break;
			}
			App.inst.notifyAsyncTasks();
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.ChannelForm_latestVideos, e);
		}
		//App.inst.notifyAsyncTasks();
	}

	protected void search() {
		disposeSearchForm();
		App.inst.stopDoingAsyncTasks();
		TextBox t = new TextBox("", "", 256, TextField.ANY);
		t.setCommandListener(this);
		t.setTitle(Locale.s(CMD_Search));
		t.addCommand(searchOkCmd);
		t.addCommand(cancelCmd);
		ui.display(t);
	}

	protected void playlists() {
		playlistsScr = new ChannelPlaylistsScreen(this);
		App.inst.stopDoingAsyncTasks();
		ui.setScreen(playlistsScr);
		try {
			JSONArray j = ((JSONObject) App.invApi("v1/channels/playlists/" + channel.getAuthorId() + "?", "playlists,title,playlistId,videoCount" + (Settings.videoPreviews ? ",videoThumbnails" : ""))).getArray("playlists");
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = playlist(j.getObject(i));
				if(item == null) continue;
				playlistsScr.add(item);
				if(i >= SEARCH_LIMIT) break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.ChannelForm_search, e);
		}
		App.inst.notifyAsyncTasks();
	}

	protected void show() {
		clearCommands();
		addCommand(backCmd);
		addCommand(searchCmd);
		if(!shown) {
			shown = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			App.inst.addAsyncLoad(this);
			App.inst.notifyAsyncTasks();
		}
		if(okAdded || ui.isKeyInputMode()) {
			okAdded = true;
			addCommand(okCmd);
		}
	}
	
	private void init() {
		scroll = 0;
		if(channel == null) return;
		add(channel.makeListItem());
		add(new ButtonItem(Locale.s(BTN_Playlists), playlistsRun ));
	}

	protected UIItem playlist(JSONObject j) {
		PlaylistModel p = new PlaylistModel(j, playlistsScr, channel);
		return p.makeListItem();
	}

	private void search(String q) {
		searchScr = new AbstractListScreen(channel.getAuthor() + " - " + Locale.s(TITLE_SearchQuery), ChannelScreen.this) {
			protected void show() {
				clearCommands();
				addCommand(backCmd);
			}
		};
		ui.setScreen(searchScr);
		App.inst.stopDoingAsyncTasks();
		try {
			JSONArray j = (JSONArray) App.invApi("v1/channels/search/" + channel.getAuthorId() + "?q=" + Util.url(q), VIDEO_FIELDS + (Settings.videoPreviews ? ",videoThumbnails" : ""));
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = parseAndMakeItem(j.getObject(i), true);
				if(item == null) continue;
				searchScr.add(item);
				if(i >= SEARCH_LIMIT) break;
			}
			App.inst.notifyAsyncTasks();
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.ChannelForm_search, e);
		}
	}
	
	public void keyPress(int i) {
		if(!okAdded && ((i >= -7 && i <= -1) || (i >= 1 && i <= 57))) {
			okAdded = true;
			addCommand(okCmd);
		}
		super.keyPress(i);
	}


	protected UIItem parseAndMakeItem(JSONObject j, boolean search) {
		VideoModel v = new VideoModel(j, search ? searchScr : this);
		if(search) v.setFromSearch();
		if(Settings.videoPreviews) App.inst.addAsyncLoad(v);
		return v.makeListItem();
	}
	
	public boolean supportCommands() {
		return true;
	}

	public void load() {
		try {
			if(!channel.isExtended()) {
				channel.extend();
				clear();
				init();
			}
			if(Settings.videoPreviews) channel.load();
			latestVideos();
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.ChannelForm_load, e);
		}
	}

	public void commandAction(Command c, Displayable d) {
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
		/*
		if((d == searchForm || d == lastVideosForm || d == playlistsForm) && c == backCmd) {
			ui.setScreen(this);
			disposeSearchForm();
			return;
		}
		*/
		if(c == backCmd) {
			App.inst.stopDoingAsyncTasks();
			if(containerScreen != null) {
				ui.setScreen(containerScreen);
			} else {
				ui.back(this);
			}
			ui.disposeChannelPage();
			return;
		}
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
		this.containerScreen = s;
	}

	public void dispose() {
		clear();
		channel.disposeExtendedVars();
		channel = null;
		containerScreen = null;
	}
}
