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
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;

import App;
import Util;
import Errors;
import Locale;
import Settings;
import Constants;
import LocaleConstants;
import InvidiousException;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import cc.nnproject.utils.PlatformUtils;
import cc.nnproject.json.AbstractJSON;
import models.VideoModel;
import ui.screens.MainScreen;
import ui.screens.SearchScreen;
import ui.screens.VideoScreen;
import models.AbstractModel;
import models.ChannelModel;
import models.PlaylistModel;

public class AppUI implements CommandListener, Constants, UIConstants, LocaleConstants, Commands {
	
	public static final Display display = Display.getDisplay(App.midlet);

	public static AppUI inst;
	
	public AppUI() {
		if(inst != null) throw new Error();
		inst = this;
	}

	private App app = App.inst;
	
	public MainScreen mainScr;
	public SearchScreen searchScr;
	public VideoScreen videoScr;
	
	private SettingsForm settingsForm;

	private static MyCanvas canv;
	private static int scrollBarWidth;
	private UIScreen current;

	private Object repaintLock = new Object();
	private Object repaintResLock = new Object();

	public boolean scrolling;
	
	public int repaintTime;
	
	public boolean oddFrame;
	
	private List optionsForm;

	private Thread repaintThread = new Thread() {
		public void run() {
			boolean wasScrolling = false;
			while(App.midlet.running) {
				try {
					if(!scrolling) { 
						if(wasScrolling) {
							_repaint();
							wasScrolling = false;
						}
						synchronized (repaintLock) {
							repaintLock.wait(1000);
						}
					}
					_repaint();
					if(scrolling) {
						wasScrolling = true;
					} else {
						synchronized (repaintResLock) {
							repaintResLock.notify();
						}
					}
					waitRepaint();
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	};
	
	private Vector commands = new Vector();
	
	private boolean keyInput = false;
	
	private void waitRepaint() throws InterruptedException {
		int i = 1000 / 24;
		i -= repaintTime;
		if(i > 0) Thread.sleep(i);
		Thread.yield();
	}

	private void _repaint() {
		oddFrame = !oddFrame;
		long time = System.currentTimeMillis();
		if(canv != null) {
			canv.repaint();
			canv.serviceRepaints();
		}
		repaintTime = (int) (System.currentTimeMillis() - time);
	}

	public void repaint(boolean wait) {
		if(display.getCurrent() != canv) return;
		if(scrolling) return;
		synchronized (repaintLock) {
			repaintLock.notify();
		}
		if(wait) {
			try {
				synchronized (repaintResLock) {
					repaintResLock.wait(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int getColor(int i) {
		switch(i) {
		case COLOR_MAINBACKGROUND:
			return -1;
		case COLOR_MAINFOREGROUND:
		case COLOR_MAINBORDER:
			return 0;
		case COLOR_ITEMBORDER:
			return 0x5D5D5D;
		case COLOR_DARK_ALPHA:
			return 0x7fffffff;
		case COLOR_GRAYTEXT:
			return 0x5D5D5D;
		case COLOR_SCROLLBAR_BG:
			return 0x555555;
		case COLOR_SCROLLBAR_FG:
			return 0xAAAAAA;
		case COLOR_ITEM_HIGHLIGHT:
			return 0xAAAAAA;
		case COLOR_BUTTON_HOVER_BG:
			return 0xCCCCCC;
		default:
			return 0;
		}
	}

	public static Font getFont(int i) {
		switch(i) {
		case FONT_DEBUG:
		default:
			return Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		}
	}
	
	public void setScreen(UIScreen s) {
		current = s;
		canv.resetScreen();
		repaint(true);
		s.show();
	}
	
	public int getWidth() {
		return canv.width;
	}
	
	public int getHeight() {
		return canv.height;
	}

	public UIScreen getCurrentScreen() {
		return current;
	}
	
	public void loadMain() {
		try {
			if(Settings.startScreen == 0) {
				loadTrends();
			} else {
				loadPopular();
			}
			app.setLoadingState("Start page loaded");
			display(canv);
			Util.gc();
			setScreen(mainScr);
		} catch (InvidiousException e) {
			App.error(this, Errors.AppUI_loadForm, e);
		} catch (OutOfMemoryError e) {
			Util.gc();
			App.error(this, Errors.AppUI_loadForm, "Out of memory!");
		} catch (Throwable e) {
			e.printStackTrace();
			App.error(this, Errors.AppUI_loadForm, e);
		}
	}

	public void init() {
		inst = this;
		canv = new MyCanvas(this);
		canv.setCommandListener(this);
		repaintThread.setPriority(6);
		repaintThread.start();
		mainScr = new MainScreen();
	}
	
	public void load(String s) throws IOException {
		app.setLoadingState("Loading (0)");
		boolean b = App.needsCheckMemory();
		app.setLoadingState("Loading (1)");
		try {
			app.setLoadingState("Loading (2)");
			AbstractJSON r = App.invApi("v1/"+s+"?", VIDEO_FIELDS + (Settings.videoPreviews ? ",videoThumbnails" : ""));
			app.setLoadingState("Loading (3)");
			if(r instanceof JSONObject) {
				App.error(this, Errors.AppUI_load, "Wrong response", r.toString());
				return;
			}
			app.setLoadingState("Loading (4)");
			JSONArray j = (JSONArray) r;
			try {
			} catch (Exception e) {
			}
			app.setLoadingState("Parsing");
			int l = j.size();
			for(int i = 0; i < l; i++) {
				app.setLoadingState("Parsing (" + i + "/" + l + ")");
				UIItem item = parseAndMakeItem(j.getObject(i), false, i);
				if(item == null) continue;
				mainScr.add(item);
				if(i >= TRENDS_LIMIT) break;
				if(b) App.checkMemoryAndGc();
			}
			repaint(false);
			app.setLoadingState("Loading (6)");
			Thread.sleep(150);
			j = null;
			app.notifyAsyncTasks();
			Thread.sleep(150);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.AppUI_load, e);
		}
		Util.gc();
	}

	public void loadTrends() throws IOException {
		load("trending");
	}

	public void loadPopular() throws IOException {
		load("popular");
	}

	public void search(String q) {
		boolean b = App.needsCheckMemory();
		searchScr = new SearchScreen(mainScr);
		display(null);
		setScreen(searchScr);
		if(Settings.isLowEndDevice() || !Settings.rememberSearch) {
			disposeMainForm();
		}
		app.stopDoingAsyncTasks();
		try {
			JSONArray j = (JSONArray) App.invApi("v1/search?q=" + Util.url(q) + "&type=all", SEARCH_FIELDS + ",type" + (Settings.videoPreviews ? ",videoThumbnails" : "") + (Settings.searchChannels || Settings.searchPlaylists ? ",authorThumbnails,playlistId,videoCount" : ""));
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = parseAndMakeItem(j.getObject(i), true, i);
				if(item == null) continue;
				searchScr.add(item);
				if(i >= SEARCH_LIMIT) break;
				if(b) App.checkMemoryAndGc();
			}
			j = null;
			app.notifyAsyncTasks();
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.AppUI_search, e);
		}
		Util.gc();
	}
	
	private UIItem parseAndMakeItem(JSONObject j, boolean search, int i) {
		String type = j.getNullableString("type");
		if(type == null) {
			// video
			VideoModel v = new VideoModel(j);
			v.setIndex(i);
			if(search) v.setFromSearch();
			if(Settings.videoPreviews) app.addAsyncLoad(v);
			return v.makeListItem();
		}
		if(type.equals("video")) {
			VideoModel v = new VideoModel(j);
			v.setIndex(i);
			if(search) v.setFromSearch();
			if(Settings.videoPreviews) app.addAsyncLoad(v);
			return v.makeListItem();
		}
		if(Settings.searchChannels && type.equals("channel")) {
			ChannelModel c = new ChannelModel(j);
			if(search) c.setFromSearch();
			if(Settings.videoPreviews) app.addAsyncLoad(c);
			return c.makeItemForList();
		}
		if(Settings.searchPlaylists && type.equals("playlist")) {
			PlaylistModel p = new PlaylistModel(j);
			if(search) p.setFromSearch();
			//if(videoPreviews) addAsyncLoad(p);
			return p.makeItemForList();
		}
		return null;
	}

	public void openVideo(String id) {
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
			if(Settings.isLowEndDevice() || !Settings.rememberSearch) {
				disposeMainForm();
			}
		} catch (Exception e) {
			App.error(this, Errors.App_openVideo, e);
		}
	}

	void disposeMainForm() {
		if(mainScr == null) return;
		mainScr.clear();
		Util.gc();
	}

	public void disposeVideoForm() {
		if(videoScr == null) return;
		videoScr.dispose();
		videoScr = null;
		Util.gc();
	}

	public void disposeChannelForm() {
		Util.gc();
	}

	public void disposeSearchForm() {
		if(searchScr == null) return;
		searchScr.clear();
		searchScr = null;
		Util.gc();
	}

	public void commandAction(Command c, Displayable d) {
		if(optionsForm != null && d == optionsForm) {
			if(c == List.SELECT_COMMAND) {
				switch(optionsForm.getSelectedIndex()) {
				case 0:
					// Search
					App.inst.stopDoingAsyncTasks();
					if(searchScr != null) {
						disposeSearchForm();
					}
					TextBox t = new TextBox("", "", 256, TextField.ANY);
					t.setCommandListener(this);
					t.setTitle(Locale.s(CMD_Search));
					t.addCommand(searchOkCmd);
					t.addCommand(cancelCmd);
					display(t);
					break;
				case 1:
					// Refresh
					if(current == mainScr) {
						try {
							App.inst.stopDoingAsyncTasks();
							mainScr.clear();
							display(null);
							if(Settings.startScreen == 0) {
								loadTrends();
							} else {
								loadPopular();
							}
							mainScr.repaint();
						} catch (InvidiousException e) {
							App.error(this, Errors.AppUI_loadForm, e);
						} catch (OutOfMemoryError e) {
							Util.gc();
							App.error(this, Errors.AppUI_loadForm, "Out of memory!");
						} catch (Throwable e) {
							e.printStackTrace();
							App.error(this, Errors.AppUI_loadForm, e);
						}
					}
					break;
				case 2:
					// Switch
					try {
						App.inst.stopDoingAsyncTasks();
						mainScr.scroll = 0;
						mainScr.clear();
						display(null);
						setScreen(mainScr);
						if(Settings.startScreen == 0) {
							Settings.startScreen = 1;
							loadPopular();
						} else {
							Settings.startScreen = 0;
							loadTrends();
						}
						Settings.saveConfig();
						mainScr.repaint();
					} catch (Exception e) {
						App.error(this, Errors.App_commandAction_switchCmd, e);
						e.printStackTrace();
					}
					optionsForm.set(2, Locale.s(Settings.startScreen == 0 ? CMD_SwitchToPopular : CMD_SwitchToTrends), null);
					break;
				case 3:
					// Open by ID
					
					break;
				case 4:
					//Settings
					showSettings();
					break;
				case 5:
					// About
					showAbout(this);
					break;
				case 6:
					// Exit
					exit();
					break;
				}
				return;
			}
		}
		if(c == cancelCmd && d instanceof TextBox) {
			display(optionsForm);
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
		if(d instanceof Alert) {
			display(optionsForm);
			return;
		}
		if(c == settingsCmd) {
			app.stopDoingAsyncTasks();
			showSettings();
			return;
		}
		if(c == exitCmd) {
			exit();
			return;
		}
		if(c == aboutCmd) {
			showAbout(this);
			return;
		}
		if(this.current != null && current.supportCommands()) {
			((CommandListener)current).commandAction(c, d);
			return;
		}
		/*
		if(d instanceof Alert) {
			display(mainForm);
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
			showSettings();
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
				Settings.startScreen = 1;
				app.stopDoingAsyncTasks();
				if(mainForm != null) {
					mainForm.deleteAll();
				} else {
					init();
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
				Settings.startScreen = 0;
				app.stopDoingAsyncTasks();
				if(mainForm != null) {
					mainForm.deleteAll();
				} else {
					init();
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
		*/
	}
	
	public void exit() {
		try {
			String[] a = RecordStore.listRecordStores();
			for(int i = 0; i < a.length; i++) {
				if(a[i].equals(CONFIG_RECORD_NAME) || !a[i].startsWith("jС")) continue;
				RecordStore.deleteRecordStore(a[i]);
			}
		} catch (Exception e) {
		}
		App.midlet.notifyDestroyed();
	}

	public void display(Displayable d) {
		if(d == null) {
			display.setCurrent(canv);
			return;
		}
		if(!(d instanceof Alert)) {
			display.setCurrent(d);
		} else {
			display.setCurrent((Alert) d, canv);
		}
	}

	public void msg(String s) {
		Alert a = new Alert("", s, null, null);
		a.setTimeout(-2);
		display(a);
	}

	public void open(AbstractModel model) {
		open(model, null);
	}

	public void open(AbstractModel model, UIScreen formContainer) {
		App app = App.inst;
		AppUI ui = inst;
		// check if already loading
		if(formContainer == null && model instanceof VideoModel && current instanceof VideoScreen && ui.videoScr != null) {
			return;
		}
		if(model instanceof PlaylistModel) {
			if(((PlaylistModel) model).getVideoCount() > 100) {
				msg(">100 videos!!!");
				return;
			}
		}
		if(!Settings.rememberSearch) {
			if(model.isFromSearch()) {
				ui.disposeSearchForm();
			}
		}
		app.stopDoingAsyncTasks();
		ModelScreen scr = model.makeScreen();
		display(null);
		setScreen(scr);
		if(scr instanceof VideoScreen) {
			ui.videoScr = (VideoScreen) scr;
		}
		Util.gc();
		
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

	public void showSettings() {
		if(settingsForm == null) {
			settingsForm = new SettingsForm();
		}
		display(settingsForm);
		settingsForm.show();
	}
	
	public void showAbout(CommandListener l) {
		//boolean samsung = App.midlet.getAppProperty("JTube-Samsung-Build") != null;
		Alert a = new Alert("", "", null, null);
		a.setTimeout(-2);
		a.setString("JTube v" + App.midlet.getAppProperty("MIDlet-Version") + " \n"
				+ "By Shinovon (nnp.nnchan.ru) \n"
				+ "t.me/nnmidlets\n"
				+ "vk.com/nnprojectcc \n\n"
				+ "Special thanks to ales_alte, Jazmin Rocio, Feodor0090" + (Locale.loaded ? " \n\nCustom localization author (" + Locale.l +"): " + Locale.s(0) : ""));
		a.setCommandListener(l == null ? this : l);
		a.addCommand(new Command("OK", Command.OK, 1));
		display(a);
	}

	public int getItemWidth() {
		return getWidth() - getScrollBarWidth();
	}

	public static int getScrollBarWidth() {
		if(scrollBarWidth == 0) {
			scrollBarWidth = 6;
			if(PlatformUtils.isSymbian94()
					|| PlatformUtils.isAshaTouchAndType()
					 || PlatformUtils.isAshaFullTouch()) scrollBarWidth = 10;
		}
		return scrollBarWidth;
	}
	
	public void addCommand(Command c) {
		canv.addCommand(c);
		commands.addElement(c);
	}
	
	public void removeCommands() {
		for(int i = 0; i < commands.size(); i++) {
			Command c = (Command) commands.elementAt(i);
			canv.removeCommand(c);
		}
		commands.removeAllElements();
	}

	public void setKeyInputMode() {
		keyInput = true;
	}

	public void setTouchInputMode() {
		keyInput = false;
	}
	
	public boolean isKeyInputMode() {
		return keyInput;
	}

	public void showMain() {
		if(mainScr == null) {
			mainScr = new MainScreen();
			loadMain();
		} else {
			setScreen(mainScr);
		}
	}

	public void showOptions() {
		if(optionsForm == null) {
			optionsForm = new List("JTube Menu", List.IMPLICIT);
			optionsForm.append(Locale.s(CMD_Search), null);
			optionsForm.append(Locale.s(CMD_Refresh), null);
			optionsForm.append(Locale.s(Settings.startScreen == 0 ? CMD_SwitchToPopular : CMD_SwitchToTrends), null);
			optionsForm.append(Locale.s(CMD_OpenByID), null);
			optionsForm.append(Locale.s(CMD_Settings), null);
			optionsForm.append(Locale.s(CMD_About), null);
			optionsForm.append(Locale.s(CMD_Exit), null);
			optionsForm.addCommand(List.SELECT_COMMAND);
			optionsForm.setSelectCommand(List.SELECT_COMMAND);
			optionsForm.addCommand(backCmd);
			optionsForm.setCommandListener(this);
		}
		display(optionsForm);
	}

	public void back(UIScreen s) {
		if(s instanceof ModelScreen && ((ModelScreen)s).getModel().isFromSearch() && searchScr != null) {
			setScreen(searchScr);
		} else {
			showMain();
		}
	}

}
