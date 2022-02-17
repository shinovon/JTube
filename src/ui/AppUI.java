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
package ui;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;

import App;
import Constants;
import Errors;
import InvidiousException;
import Locale;
import Settings;
import Util;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import cc.nnproject.utils.PlatformUtils;
import cc.nnproject.json.AbstractJSON;
import models.VideoModel;
import models.AbstractModel;
import models.ChannelModel;
import models.PlaylistModel;

public class AppUI implements CommandListener, Commands, Constants, ItemCommandListener {
	
	public static final Display display = Display.getDisplay(App.midlet);

	public static AppUI inst;
	
	public AppUI() {
		if(inst != null) throw new Error();
		inst = this;
	}

	private App app = App.inst;
	
	private Form mainForm;
	private Form searchForm;
	private SettingsForm settingsForm;
	private TextField searchText;
	private StringItem searchBtn;
	public VideoForm videoForm;
	public ChannelForm channelForm;
	private Item loadingItem;

	private static Displayable lastd;
	
	public void loadForm() {
		try {
			loadingItem = new StringItem(null, Locale.s(TITLE_Loading));
			loadingItem.setLayout(Item.LAYOUT_CENTER);
			mainForm.append(loadingItem);
			if(App.startScreen == 0) {
				loadTrends();
			} else {
				loadPopular();
			}
			app.setLoadingState("Start page loaded");
			display(mainForm);
			App.gc();
		} catch (InvidiousException e) {
			App.error(this, Errors.App_loadForm, e);
		} catch (OutOfMemoryError e) {
			App.gc();
			App.error(this, Errors.App_loadForm, "Out of memory!");
		} catch (Throwable e) {
			e.printStackTrace();
			App.error(this, Errors.App_loadForm, e);
		}
	}

	public void initForm() {
		mainForm = new Form(NAME);
		searchText = new TextField("", "", 100, TextField.ANY);
		searchText.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_2);
		mainForm.append(searchText);
		searchBtn = new StringItem(null, Locale.s(CMD_Search), StringItem.BUTTON);
		searchBtn.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_RIGHT | Item.LAYOUT_2);
		searchBtn.addCommand(searchCmd);
		searchBtn.setItemCommandListener(this);
		searchBtn.setDefaultCommand(searchCmd);
		mainForm.append(searchBtn);
		mainForm.setCommandListener(this);
		//mainForm.addCommand(searchCmd);
		mainForm.addCommand(idCmd);
		mainForm.addCommand(settingsCmd);
		mainForm.addCommand(aboutCmd);
		mainForm.addCommand(exitCmd);
	}

	public void loadTrends() throws IOException {
		app.setLoadingState("Loading trends (0)");
		boolean b = App.needsCheckMemory();
		mainForm.addCommand(switchToPopularCmd);
		app.setLoadingState("Loading trends (1)");
		try {
			mainForm.setTitle(NAME + " - " + Locale.s(TITLE_Trends));
			app.setLoadingState("Loading trends (2)");
			AbstractJSON r = App.invApi("v1/trending?", VIDEO_FIELDS + (App.videoPreviews ? ",videoThumbnails" : ""));
			app.setLoadingState("Loading trends (3)");
			if(r instanceof JSONObject) {
				App.error(this, Errors.App_loadTrends, "Wrong response", r.toString());
				return;
			}
			app.setLoadingState("Loading trends (4)");
			JSONArray j = (JSONArray) r;
			try {
				if(mainForm.size() > 2 && mainForm.get(2) == loadingItem) {
					mainForm.delete(2);
				}
			} catch (Exception e) {
			}
			app.setLoadingState("Loading trends (5)");
			int l = j.size();
			for(int i = 0; i < l; i++) {
				app.setLoadingState("Parsing trends (" + i + "/" + l + ")");
				Item item = parseAndMakeItem(j.getObject(i), false, i);
				if(item == null) continue;
				mainForm.append(item);
				if(i >= TRENDS_LIMIT) break;
				if(b) App.checkMemoryAndGc();
			}
			app.setLoadingState("Loading trends (6)");
			j = null;
			app.notifyAsyncTasks();
			app.setLoadingState("Loading trends (7)");
		} catch (RuntimeException e) {
			if(!e.getClass().equals(RuntimeException.class)) {
				e.printStackTrace();
				App.error(this, Errors.App_loadPopular, e);
				return;
			}
			throw e;
		}/* catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.App_loadTrends, e);
		}*/
		App.gc();
	}

	void loadPopular() throws IOException {
		boolean b = App.needsCheckMemory();
		mainForm.addCommand(switchToTrendsCmd);
		try {
			mainForm.setTitle(NAME + " - " + Locale.s(TITLE_Popular));
			JSONArray j = (JSONArray) App.invApi("v1/popular?", VIDEO_FIELDS + (App.videoPreviews ? ",videoThumbnails" : ""));
			try {
				if(mainForm.size() > 0 && mainForm.get(0) == loadingItem) {
					mainForm.delete(0);
				}
			} catch (Exception e) {
			}
			int l = j.size();
			for(int i = 0; i < l; i++) {
				Item item = parseAndMakeItem(j.getObject(i), false, i);
				if(item == null) continue;
				mainForm.append(item);
				if(i >= TRENDS_LIMIT) break;
				if(b) App.checkMemoryAndGc();
			}
			j = null;
			app.notifyAsyncTasks();
		} catch (RuntimeException e) {
			if(!e.getClass().equals(RuntimeException.class)) {
				e.printStackTrace();
				App.error(this, Errors.App_loadPopular, e);
				return;
			}
			throw e;
		}/* catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.App_loadPopular, e);
		}*/
		App.gc();
	}

	void search(String q) {
		boolean b = App.needsCheckMemory();
		searchForm = new Form(NAME + " - " + Locale.s(TITLE_SearchQuery));
		searchForm.setCommandListener(this);
		searchForm.addCommand(settingsCmd);
		searchForm.addCommand(searchCmd);
		display(searchForm);
		if(Settings.isLowEndDevice() || !App.rememberSearch) {
			disposeMainForm();
		}
		if(mainForm != null) {
			searchForm.addCommand(backCmd);
		} else {
			searchForm.addCommand(switchToTrendsCmd);
			searchForm.addCommand(switchToPopularCmd);
		}
		app.stopDoingAsyncTasks();
		try {
			JSONArray j = (JSONArray) App.invApi("v1/search?q=" + Util.url(q) + "&type=all", SEARCH_FIELDS + ",type" + (App.videoPreviews ? ",videoThumbnails" : "") + (App.searchChannels || App.searchPlaylists ? ",authorThumbnails,playlistId,videoCount" : ""));
			int l = j.size();
			for(int i = 0; i < l; i++) {
				Item item = parseAndMakeItem(j.getObject(i), true, i);
				if(item == null) continue;
				
				searchForm.append(item);
				if(i >= SEARCH_LIMIT) break;
				if(b) App.checkMemoryAndGc();
			}
			j = null;
			app.notifyAsyncTasks();
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.App_search, e);
		}
		App.gc();
	}
	
	private Item parseAndMakeItem(JSONObject j, boolean search, int i) {
		String type = j.getNullableString("type");
		if(type == null) {
			// video
			VideoModel v = new VideoModel(j);
			v.setIndex(i);
			if(search) v.setFromSearch();
			if(App.videoPreviews) app.addAsyncLoad(v);
			return v.makeItemForList();
		}
		if(type.equals("video")) {
			VideoModel v = new VideoModel(j);
			v.setIndex(i);
			if(search) v.setFromSearch();
			if(App.videoPreviews) app.addAsyncLoad(v);
			return v.makeItemForList();
		}
		if(App.searchChannels && type.equals("channel")) {
			ChannelModel c = new ChannelModel(j);
			if(search) c.setFromSearch();
			if(App.videoPreviews) app.addAsyncLoad(c);
			return c.makeItemForList();
		}
		if(App.searchPlaylists && type.equals("playlist")) {
			PlaylistModel p = new PlaylistModel(j);
			if(search) p.setFromSearch();
			//if(videoPreviews) addAsyncLoad(p);
			return p.makeItemForList();
		}
		return null;
	}

	private void openVideo(String id) {
		final String https = "https://";
		final String ytshort = "youtu.be/";
		final String www = "www.";
		final String watch = "youtube.com/watch?v=";
		if(id.startsWith(https)) id = id.substring(https.length());
		if(id.startsWith(ytshort)) id = id.substring(ytshort.length());
		if(id.startsWith(www)) id = id.substring(www.length());
		if(id.startsWith(watch)) id = id.substring(watch.length());
		try {
			open(new VideoModel(id).extend());
			if(Settings.isLowEndDevice() || !App.rememberSearch) {
				disposeMainForm();
			}
		} catch (Exception e) {
			App.error(this, Errors.App_openVideo, e);
		}
	}

	void disposeMainForm() {
		if(mainForm == null) return;
		mainForm.deleteAll();
		mainForm = null;
		App.gc();
	}

	public void disposeVideoForm() {
		videoForm.dispose();
		videoForm = null;
		App.gc();
	}

	public void disposeChannelForm() {
		channelForm.dispose();
		channelForm = null;
		App.gc();
	}

	public void disposeSearchForm() {
		searchForm.deleteAll();
		searchForm = null;
		App.gc();
	}

	public void commandAction(Command c, Displayable d) {
		if(d instanceof Alert) {
			display(mainForm);
			return;
		}
		if(c == exitCmd) {
			try {
				String[] a = RecordStore.listRecordStores();
				for(int i = 0; i < a.length; i++) {
					if(a[i].equals(CONFIG_RECORD_NAME)) continue;
					RecordStore.deleteRecordStore(a[i]);
				}
			} catch (Exception e) {
			}
			App.midlet.notifyDestroyed();
			return;
		}
		if(c == aboutCmd) {
			Alert a = new Alert("", "", null, null);
			a.setTimeout(-2);
			a.setString("JTube v" + App.midlet.getAppProperty("MIDlet-Version") + "(" + App.ver + ") \n"
					+ "By Shinovon (nnproject.cc) \n"
					+ "t.me/nnmidletschat \n\n"
					+ "Thanks to ales_alte, Jazmin Rocio, Feodor0090" + (Locale.loaded ? " \n\nCustom localization author (" + Locale.l +"): " + Locale.s(0) : ""));
			a.setCommandListener(this);
			a.addCommand(new Command("OK", Command.OK, 1));
			display(a);
			return;
		}

		if(c == goCmd && d instanceof TextBox) {
			openVideo(((TextBox) d).getString());
			return;
		}
		if(c == searchOkCmd && d instanceof TextBox) {
			search(((TextBox) d).getString());
			return;
		}
		if(c == settingsCmd) {
			app.stopDoingAsyncTasks();
			if(settingsForm == null) {
				settingsForm = new SettingsForm();
			}
			display(settingsForm);
			settingsForm.show();
			return;
		}
		if(c == searchCmd && d instanceof Form) {
			app.stopDoingAsyncTasks();
			if(searchForm != null) {
				disposeSearchForm();
			}
			TextBox t = new TextBox("", "", 256, TextField.ANY);
			t.setCommandListener(this);
			t.setTitle(Locale.s(CMD_Search));
			t.addCommand(searchOkCmd);
			t.addCommand(cancelCmd);
			display(t);
			return;
		}
		if(c == idCmd && d instanceof Form) {
			app.stopDoingAsyncTasks();
			TextBox t = new TextBox("", "", 256, TextField.ANY);
			t.setCommandListener(this);
			t.setTitle("Video URL or ID");
			t.addCommand(goCmd);
			t.addCommand(cancelCmd);
			display(t);
			return;
		}
		/*if(c == browserCmd) {
			try {
				platReq(getVideoInfo(video.getVideoId(), videoRes).getString("url"));
			} catch (Exception e) {
				e.printStackTrace();
				msg(e.toString());
			}
		}*/
		if(c == cancelCmd && d instanceof TextBox) {
			display(mainForm);
			return;
		}
		if(c == backCmd && d == searchForm) {
			if(mainForm == null) return;
			app.stopDoingAsyncTasks();
			display(mainForm);
			disposeSearchForm();
			return;
		}
		try {
			if(c == switchToPopularCmd) {
				App.startScreen = 1;
				app.stopDoingAsyncTasks();
				if(mainForm != null) {
					mainForm.deleteAll();
				} else {
					initForm();
				}
				if(searchForm != null) {
					disposeSearchForm();
				} else {
					d.removeCommand(c);
				}
				loadPopular();
				Settings.saveConfig();
				return;
			}
			if(c == switchToTrendsCmd) {
				App.startScreen = 0;
				app.stopDoingAsyncTasks();
				if(mainForm != null) {
					mainForm.deleteAll();
				} else {
					initForm();
				}
				if(searchForm != null) {
					disposeSearchForm();
				} else {
					d.removeCommand(c);
				}
				loadTrends();
				Settings.saveConfig();
			}
		} catch (Exception e) {
			App.error(this, Errors.App_commandAction_switchCmd, e);
			e.printStackTrace();
		}
	}
	
	public static void display(Displayable d) {
		AppUI ui = inst;
		if(d == null) {
			if(ui.videoForm != null) {
				d = ui.videoForm;
			} else if(ui.channelForm != null) {
				d = ui.channelForm;
			} else if(ui.searchForm != null) {
				d = ui.searchForm;
			} else if(ui.mainForm != null) {
				d = ui.mainForm;
			} else {
				ui.initForm();
				ui.loadForm();
				return;
			}
		}
		if(!(d instanceof Alert)) {
			lastd = d;
			display.setCurrent(d);
		} else {
			display.setCurrent((Alert) d, lastd == null ? ui.mainForm : lastd);
		}
	}

	public static void back(Form f) {
		AppUI ui = inst;
		if(f instanceof ModelForm && ((ModelForm)f).getModel().isFromSearch() && ui.searchForm != null) {
			AppUI.display(ui.searchForm);
		} else if(ui.mainForm != null) {
			AppUI.display(ui.mainForm);
		} else {
			ui.initForm();
			//AppUI.display(ui.mainForm);
			ui.loadForm();
		}
	}

	public static void msg(String s) {
		Alert a = new Alert("", s, null, null);
		a.setTimeout(-2);
		AppUI.display(a);
	}

	public static void open(AbstractModel model) {
		open(model, null);
	}

	public static void open(AbstractModel model, Form formContainer) {
		App app = App.inst;
		AppUI ui = inst;
		// check if already loading
		if(formContainer == null && ui.videoForm != null && model instanceof VideoModel) {
			return;
		}
		if(model instanceof PlaylistModel) {
			if(((PlaylistModel) model).getVideoCount() > 100) {
				AppUI.msg(">100 videos!!!");
				return;
			}
		}
		if(!App.rememberSearch) {
			if(model.isFromSearch()) {
				ui.disposeSearchForm();
			} else if(ui.mainForm != null) {
				ui.disposeMainForm();
			}
		}
		app.stopDoingAsyncTasks();
		ModelForm form = model.makeForm();
		AppUI.display(form);
		if(form instanceof VideoForm) {
			ui.videoForm = (VideoForm) form;
		} else if(form instanceof ChannelForm) {
			ui.channelForm = (ChannelForm) form;
		}
		if(formContainer != null) {
			form.setFormContainer(formContainer);
		}
		App.gc();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
		app.addAsyncLoad(form);
		app.notifyAsyncTasks();
	}

	public static int getPlatformWidthOffset() {
		if(PlatformUtils.isKemulator) return 5;
		if(PlatformUtils.isJ2ML()) return 0;
		if(PlatformUtils.isAshaFullTouch()) return 24;
		if(PlatformUtils.isS40()) return 12;
		if(PlatformUtils.isSymbianAnna()) return 38;
		if(PlatformUtils.isSymbian94()) return 32;
		if(PlatformUtils.isSymbian3Based()) return 20;
		return 4;
	}

	public void commandAction(Command c, Item item) {
		if(c == searchCmd) {
			app.stopDoingAsyncTasks();
			search(searchText.getString());
		}
	}

}
