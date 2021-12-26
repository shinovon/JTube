package ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import App;
import Util;
import Errors;
import Locale;
import Constants;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import models.AbstractModel;
import models.ChannelModel;
import models.VideoModel;

// TODO
public class ChannelForm extends ModelForm implements CommandListener, Commands, ItemCommandListener, Constants {

	private static final Command lastVideosCmd = new Command(Locale.s(BTN_LatestVideos), Command.ITEM, 2);
	private static final Command searchVideosCmd = new Command(Locale.s(BTN_SearchVideos), Command.ITEM, 3);
	private static final Command playlistsCmd = new Command(Locale.s(BTN_Playlists), Command.ITEM, 4);

	private ChannelModel channel;
	
	private StringItem loadingItem;
	private StringItem lastVideosBtn;
	private StringItem searchVideosBtn;
	private StringItem playlistsBtn;
	
	private Form lastVideosForm;
	private Form searchForm;

	public ChannelForm(ChannelModel c) {
		super(c.getAuthor());
		loadingItem = new StringItem(null, Locale.s(TITLE_Loading));
		loadingItem.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_VCENTER | Item.LAYOUT_2);
		setCommandListener(this);
		addCommand(backCmd);
		this.channel = c;
	}
	
	private void init() {
		try {
			if(get(0) == loadingItem) {
				delete(0);
			}
		} catch (Exception e) {
		}
		Item img = channel.makeItemForPage();
		append(img);
		lastVideosBtn = new StringItem(null, Locale.s(BTN_LatestVideos), Item.BUTTON);
		lastVideosBtn.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_NEWLINE_AFTER);
		lastVideosBtn.addCommand(lastVideosCmd);
		lastVideosBtn.setDefaultCommand(lastVideosCmd);
		lastVideosBtn.setItemCommandListener(this);
		append(lastVideosBtn);
		searchVideosBtn = new StringItem(null, Locale.s(BTN_SearchVideos), Item.BUTTON);
		searchVideosBtn.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_NEWLINE_AFTER);
		searchVideosBtn.addCommand(searchVideosCmd);
		searchVideosBtn.setDefaultCommand(searchVideosCmd);
		searchVideosBtn.setItemCommandListener(this);
		append(searchVideosBtn);
		playlistsBtn = new StringItem(null, Locale.s(BTN_Playlists), Item.BUTTON);
		playlistsBtn.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_NEWLINE_AFTER);
		playlistsBtn.addCommand(playlistsCmd);
		playlistsBtn.setDefaultCommand(playlistsCmd);
		playlistsBtn.setItemCommandListener(this);
		append(playlistsBtn);
	}

	public void load() {
		try {
			if(!channel.isExtended()) {
				channel.extend();
				init();
			}
			if(App.videoPreviews) channel.load();
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_load, e);
		}
	}

	private void latestVideos() {
		lastVideosForm = new Form(channel.getAuthor() + " - " + Locale.s(BTN_LatestVideos));
		lastVideosForm.setCommandListener(this);
		lastVideosForm.addCommand(backCmd);
		lastVideosForm.addCommand(settingsCmd);
		lastVideosForm.addCommand(searchCmd);
		AppUI.display(lastVideosForm);
		App.inst.stopDoingAsyncTasks();
		try {
			JSONArray j = (JSONArray) App.invApi("v1/channels/" + channel.getAuthorId() + "/latest?", VIDEO_FIELDS + (App.videoPreviews ? ",videoThumbnails" : ""));
			int l = j.size();
			for(int i = 0; i < l; i++) {
				Item item = parseAndMakeItem(j.getObject(i), false);
				if(item == null) continue;
				lastVideosForm.append(item);
				if(i >= LATESTVIDEOS_LIMIT) break;
			}
			App.inst.notifyAsyncTasks();
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.ChannelForm_latestVideos, e);
		}
	}

	private void search(String q) {
		searchForm = new Form(NAME + " - " + Locale.s(TITLE_SearchQuery));
		searchForm.setCommandListener(this);
		searchForm.addCommand(backCmd);
		searchForm.addCommand(settingsCmd);
		searchForm.addCommand(searchCmd);
		AppUI.display(searchForm);
		App.inst.stopDoingAsyncTasks();
		try {
			JSONArray j = (JSONArray) App.invApi("v1/channels/search/" + channel.getAuthorId() + "?q=" + Util.url(q), VIDEO_FIELDS + (App.videoPreviews ? ",videoThumbnails" : ""));
			int l = j.size();
			for(int i = 0; i < l; i++) {
				Item item = parseAndMakeItem(j.getObject(i), true);
				if(item == null) continue;
				searchForm.append(item);
				if(i >= SEARCH_LIMIT) break;
			}
			App.inst.notifyAsyncTasks();
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.ChannelForm_search, e);
		}
	}

	private Item parseAndMakeItem(JSONObject j, boolean search) {
		VideoModel v = new VideoModel(j, search ? searchForm : lastVideosForm);
		if(search) v.setFromSearch();
		if(App.videoPreviews) App.inst.addAsyncLoad(v);
		return v.makeItemForList();
	}

	public void commandAction(Command c, Displayable d) {
		if(c == lastVideosCmd) {
			latestVideos();
			return;
		}
		if(c == searchVideosCmd && d instanceof Form) {
			App.inst.stopDoingAsyncTasks();
			if(searchForm != null) {
				disposeSearchForm();
			}
			TextBox t = new TextBox("", "", 256, TextField.ANY);
			t.setCommandListener(this);
			t.setTitle(Locale.s(CMD_Search));
			t.addCommand(searchOkCmd);
			t.addCommand(cancelCmd);
			AppUI.display(t);
			return;
		}
		if(c == searchOkCmd && d instanceof TextBox) {
			search(((TextBox) d).getString());
			return;
		}
		if(c == cancelCmd && d instanceof TextBox) {
			AppUI.display(this);
			return;
		}
		if(d == searchForm && c == backCmd) {
			AppUI.display(this);
			disposeSearchForm();
			return;
		}
		if(d == lastVideosForm && c == backCmd) {
			AppUI.display(this);
			disposeLastVideosForm();
			return;
		}
		if(d == this && c == backCmd) {
			AppUI.back(this);
			AppUI.inst.disposeChannelForm();
			return;
		}
		AppUI.inst.commandAction(c, d);
	}

	private void playlists() {
		// TODO Auto-generated method stub
		
	}

	private void disposeLastVideosForm() {
		if(lastVideosForm == null) return;
		lastVideosForm.deleteAll();
		lastVideosForm = null;
		App.gc();
	}

	private void disposeSearchForm() {
		if(searchForm == null) return;
		searchForm.deleteAll();
		searchForm = null;
		App.gc();
	}

	public void dispose() {
		disposeLastVideosForm();
		disposeSearchForm();
		channel.disposeExtendedVars();
		channel = null;
	}

	public ChannelModel getChannel() {
		return channel;
	}

	public AbstractModel getModel() {
		return getChannel();
	}

	public void commandAction(Command c, Item i) {
		if(c == searchVideosCmd) {
			App.inst.stopDoingAsyncTasks();
			if(searchForm != null) {
				disposeSearchForm();
			}
			TextBox t = new TextBox("", "", 256, TextField.ANY);
			t.setCommandListener(this);
			t.setTitle(Locale.s(CMD_Search));
			t.addCommand(searchOkCmd);
			t.addCommand(cancelCmd);
			AppUI.display(t);
			return;
		}
		if(c == playlistsCmd) {
			playlists();
			return;
		}
		if(c == lastVideosCmd) {
			latestVideos();
		}
	}

	// ignore
	public void setFormContainer(Form form) { }
}
