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
import RunnableTask;
import InvidiousException;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import cc.nnproject.utils.PlatformUtils;
import cc.nnproject.json.AbstractJSON;
import models.VideoModel;
import ui.screens.ChannelScreen;
import ui.screens.MainScreen;
import ui.screens.SearchScreen;
import ui.screens.VideoScreen;
import models.AbstractModel;
import models.ChannelModel;
import models.PlaylistModel;

public class AppUI implements CommandListener, Constants, UIConstants, LocaleConstants, Commands, Runnable {
	
	public static final Display display = Display.getDisplay(App.midlet);

	public static AppUI inst;
	
	public AppUI() {
		if(inst != null) throw new Error();
		inst = this;
		UIScreen.ui = this;
		UIItem.ui = this;
	}

	private App app = App.inst;
	
	public MainScreen mainScr;
	public SearchScreen searchScr;
	public VideoScreen videoScr;
	public ChannelScreen channelScr;
	
	private SettingsForm settingsForm;

	private static JTubeCanvas canv;
	private static int scrollBarWidth;
	private UIScreen current;

	private Object repaintLock = new Object();
	private Object repaintResLock = new Object();

	public boolean scrolling;
	
	public int repaintTime;
	
	public boolean oddFrame;
	
	private List optionsForm;
	
	private Vector commands = new Vector();
	
	protected boolean keyInput = false;
	
	public static boolean loadingState;

	private Thread repaintThread = new Thread(this);
	
	public void run() {
		boolean wasScrolling = false;
		while(App.midlet.running) {
			try {
				if(display.getCurrent() != canv) {
					Thread.sleep(1);
					continue;
				}
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

	private void waitRepaint() throws InterruptedException {
		int i = 30;
		i -= repaintTime;
		if(i > 0) Thread.sleep(i);
	}

	private void _repaint() {
		oddFrame = !oddFrame;
		long time = System.currentTimeMillis();
		canv.updateScreen();
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
			}
		}
	}
	
	public static int getColor(int i) {
		switch(i) {
		case COLOR_MAINBG:
			return Settings.amoled ? 0 : -1;
		case COLOR_MAINFG:
		case COLOR_MAINBORDER:
			return Settings.amoled ? -1 : 0;
		case COLOR_ITEMBORDER:
			return Settings.amoled ? 0x5D5D5D : 0x212121;
		case COLOR_DARK_ALPHA:
			return 0x7fffffff;
		case COLOR_GRAYTEXT:
			return Settings.amoled ? 0x7F7F7F : 0x5D5D5D;
		case COLOR_SCROLLBAR_BG:
			return 0x555555;
		case COLOR_SCROLLBAR_FG:
			return 0xAAAAAA;
		case COLOR_ITEM_HIGHLIGHT:
			return Settings.amoled ? 0x5D5D5D : 0x9A9A9A;
		case COLOR_BUTTON_HOVER_BG:
			return 0xCCCCCC;
		case COLOR_TIMETEXT:
			return 0xDDDDDD;
		case COLOR_SCROLLBAR_BORDER:
			return Settings.amoled ? 0 : -1;
		default:
			return 0;
		}
	}

	public static Font getFont(int i) {
		switch(i) {
		default:
			return Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		}
	}
	
	public void setScreen(UIScreen s) {
		removeCommands();
		display(null);
		if(current != null) {
			current.hide();
		}
		current = s;
		canv.resetScreen();
		repaint(true);
		s.show();
		String t = s.getTitle();
		if(t != null && t.length() > 0) {
			canv.setTitle(t);
		} else {
			canv.setTitle(null);
		}
	}
	
	public void updateScreenTitle(UIScreen s) {
		if(this.current == s) {
			String t = s.getTitle();
			if(t != null && t.length() > 0) {
				canv.setTitle(t);
			} else {
				canv.setTitle(null);
			}
		}
	}

	public UIScreen getCurrentScreen() {
		return current;
	}
	
	public int getWidth() {
		return canv.width;
	}
	
	public int getHeight() {
		return canv.height;
	}
	
	public void loadMain() {
		try {
			loadingState = true;
			if(mainScr == null) {
				mainScr = new MainScreen();
			}
			setScreen(mainScr);
			if(Settings.startScreen == 0) {
				loadTrends();
			} else {
				loadPopular();
			}
			loadingState = false;
			Util.gc();
		} catch (InvidiousException e) {
			App.error(this, Errors.AppUI_loadForm, e);
		} catch (OutOfMemoryError e) {
			Util.gc();
			App.error(this, Errors.AppUI_loadForm, "Out of memory!");
		} catch (Throwable e) {
			App.error(this, Errors.AppUI_loadForm, e);
		}
	}

	public void init() {
		inst = this;
		canv = new JTubeCanvas(this);
		canv.setCommandListener(this);
		repaintThread.start();
		try {
			DirectFontUtil.init();
		} catch (Throwable e) {
		}
	}
	
	public void load(String s) throws IOException {
		loadingState = true;
		boolean b = App.needsCheckMemory();
		try {
			AbstractJSON r = App.invApi("v1/"+s+"?",
					VIDEO_FIELDS +
					(getWidth() >= 320 ? ",publishedText,viewCount" : "")
					);
			if(r instanceof JSONObject) {
				App.error(this, Errors.AppUI_load, "Wrong response", r.toString());
				return;
			}
			JSONArray j = (JSONArray) r;
			try {
			} catch (Exception e) {
			}
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = parseAndMakeItem(j.getObject(i), false, i);
				if(item == null) continue;
				mainScr.add(item);
				if(i >= TRENDS_LIMIT) break;
				if(b) App.checkMemoryAndGc();
			}
			Thread.sleep(150);
			j = null;
			app.startAsyncTasks();
			Thread.sleep(150);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.AppUI_load, e);
		}
		Util.gc();
		loadingState = false;
		repaint(false);
	}

	public void loadTrends() throws IOException {
		mainScr.setTitle("JTube - " + Locale.s(TITLE_Trends));
		load("trending");
	}

	public void loadPopular() throws IOException {
		mainScr.setTitle("JTube - " + Locale.s(TITLE_Popular));
		load("popular");
	}

	public void search(String q) {
		loadingState = true;
		boolean b = App.needsCheckMemory();
		searchScr = new SearchScreen(q, mainScr);
		display(null);
		setScreen(searchScr);
		if(Settings.isLowEndDevice() || !Settings.rememberSearch) {
			disposeMainPage();
		}
		app.stopAsyncTasks();
		try {
			JSONArray j = (JSONArray) App.invApi("v1/search?q=" + Util.url(q) + "&type=all",
					SEARCH_FIELDS + ",type" +
					(Settings.searchChannels || Settings.searchPlaylists ? ",authorThumbnails,subCount,playlistId,videoCount" : "") +
					(getWidth() >= 320 ? ",publishedText,viewCount" : "")
					);
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = parseAndMakeItem(j.getObject(i), true, i);
				if(item == null) continue;
				searchScr.add(item);
				if(i >= SEARCH_LIMIT) break;
				if(b) App.checkMemoryAndGc();
			}
			j = null;
			app.startAsyncTasks();
		} catch (Exception e) {
			App.error(this, Errors.AppUI_search, e);
		}
		Util.gc();
		loadingState = false;
		repaint(false);
	}
	
	private UIItem parseAndMakeItem(JSONObject j, boolean search, int i) {
		String type = j.getNullableString("type");
		if(type == null) {
			// video
			VideoModel v = new VideoModel(j);
			v.setIndex(i);
			if(search) v.setFromSearch();
			if(Settings.videoPreviews) app.addAsyncTask(v);
			return v.makeListItem();
		}
		if(type.equals("video")) {
			VideoModel v = new VideoModel(j);
			v.setIndex(i);
			if(search) v.setFromSearch();
			if(Settings.videoPreviews) app.addAsyncTask(v);
			return v.makeListItem();
		}
		if(Settings.searchChannels && type.equals("channel")) {
			ChannelModel c = new ChannelModel(j);
			if(search) c.setFromSearch();
			if(Settings.videoPreviews) app.addAsyncTask(c);
			return c.makeListItem();
		}
		if(Settings.searchPlaylists && type.equals("playlist")) {
			PlaylistModel p = new PlaylistModel(j);
			if(search) p.setFromSearch();
			//if(videoPreviews) addAsyncLoad(p);
			return p.makeListItem();
		}
		return null;
	}

	void disposeMainPage() {
		if(mainScr == null) return;
		mainScr.clear();
		mainScr = null;
		Util.gc();
	}

	public void disposeVideoPage() {
		if(videoScr == null) return;
		videoScr.dispose();
		videoScr = null;
		Util.gc();
	}

	public void disposeChannelPage() {
		if(channelScr == null) return;
		channelScr.dispose();
		channelScr = null;
		Util.gc();
	}

	public void disposeSearchPage() {
		if(searchScr == null) return;
		searchScr.clear();
		searchScr = null;
		Util.gc();
	}
	
	public void refresh() {
		try {
			App.inst.stopAsyncTasks();
			display(null);
			if(current == searchScr) {
				searchScr.clear();
				search(searchScr.getTitle());
			} else {
				mainScr.clear();
				if(Settings.startScreen == 0) {
					loadTrends();
				} else {
					loadPopular();
				}
			}
			current.repaint();
		} catch (InvidiousException e) {
			App.error(this, Errors.AppUI_loadForm, e);
		} catch (OutOfMemoryError e) {
			Util.gc();
			App.error(this, Errors.AppUI_loadForm, "Out of memory!");
		} catch (Throwable e) {
			App.error(this, Errors.AppUI_loadForm, e);
		}
	}
	
	public void switchMain() {
		try {
			App.inst.stopAsyncTasks();
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
		}
		optionsForm.set(2, Locale.s(Settings.startScreen == 0 ? CMD_SwitchToPopular : CMD_SwitchToTrends), null);
		
	}

	public void commandAction(Command c, Displayable d) {
		if(optionsForm != null && d == optionsForm) {
			if(c == List.SELECT_COMMAND) {
				switch(optionsForm.getSelectedIndex()) {
				case 0:
				{
					// Search
					App.inst.stopAsyncTasks();
					if(searchScr != null) {
						disposeSearchPage();
					}
					TextBox t = new TextBox("", "", 256, TextField.ANY);
					t.setCommandListener(this);
					t.setTitle(Locale.s(CMD_Search));
					t.addCommand(searchOkCmd);
					t.addCommand(cancelCmd);
					display(t);
					break;
				}
				case 1:
				{
					// Refresh
					app.schedule(new RunnableTask(3));
					break;
				}
				case 2:
				{
					// Switch
					app.schedule(new RunnableTask(4));
					break;
				}
				case 3:
				{
					// Open by ID
					app.stopAsyncTasks();
					TextBox t = new TextBox("", "", 256, TextField.ANY);
					t.setCommandListener(this);
					t.setTitle("Video URL or ID");
					t.addCommand(goCmd);
					t.addCommand(cancelCmd);
					display(t);
					break;
				}
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
			if(c == backCmd) {
				display(null);
				return;
			}
		}
		if(c == cancelCmd && d instanceof TextBox) {
			display(optionsForm);
			return;
		}
		if(c == goCmd && d instanceof TextBox) {
			app.schedule(new RunnableTask(((TextBox) d).getString(), 1));
			return;
		}
		if(c == searchOkCmd && d instanceof TextBox) {
			app.schedule(new RunnableTask(((TextBox) d).getString(), 2));
			return;
		}
		if(d instanceof Alert) {
			display(optionsForm);
			return;
		}
		if(c == settingsCmd) {
			app.stopAsyncTasks();
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
		if(c == switchToPopularCmd || c == switchToTrendsCmd) {
			app.schedule(new RunnableTask(4));
			return;
		}
		if(c == searchCmd) {
			App.inst.stopAsyncTasks();
			if(searchScr != null) {
				disposeSearchPage();
			}
			TextBox t = new TextBox("", "", 256, TextField.ANY);
			t.setCommandListener(this);
			t.setTitle(Locale.s(CMD_Search));
			t.addCommand(searchOkCmd);
			t.addCommand(cancelCmd);
			display(t);
			return;
		}
		if(c == refreshCmd) {
			app.schedule(new RunnableTask(3));
			return;
		}
		if(c == openByIdCmd) {
			app.stopAsyncTasks();
			TextBox t = new TextBox("", "", 256, TextField.ANY);
			t.setCommandListener(this);
			t.setTitle("Video URL or ID");
			t.addCommand(goCmd);
			t.addCommand(cancelCmd);
			display(t);
			return;
		}
		if(this.current != null && current.supportCommands()) {
			((CommandListener)current).commandAction(c, d);
			return;
		}
		if(c == backCmd) {
			if(current != null && current.getParent() != null) {
				setScreen(current.getParent());
			}
			return;
		}
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
		if(current instanceof ChannelScreen && model instanceof ChannelModel
			&& ((ChannelModel)model).getAuthorId().equals(((ChannelScreen)current).getChannel().getAuthorId())) {
			return;
		}
		// check if already loading
		if(model instanceof VideoModel && current instanceof VideoScreen && ui.videoScr != null
			&& ((VideoModel)model).getVideoId().equals(((VideoModel)((VideoScreen)current).getModel()).getVideoId())) {
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
				ui.disposeSearchPage();
			}
		}
		if(ui.videoScr != null) {
			disposeVideoPage();
		}
		app.stopAsyncTasks();
		ModelScreen scr = model.makeScreen();
		scr.setContainerScreen(formContainer);
		display(null);
		setScreen(scr);
		if(scr instanceof VideoScreen) {
			ui.videoScr = (VideoScreen) scr;
		} else if(scr instanceof ChannelScreen) {
			ui.channelScr = (ChannelScreen) scr;
		}
		Util.gc();
		
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
			scrollBarWidth = PlatformUtils.isS603rd() || !canv.hasPointerEvents() ? 4 : 6;
			if(PlatformUtils.isSymbian94()) scrollBarWidth = 10;
		}
		return scrollBarWidth;
	}
	
	public void addCommand(Command c) {
		canv.addCommand(c);
		commands.addElement(c);
	}
	
	public void removeCommand(Command c) {
		canv.removeCommand(c);
		commands.removeElement(c);
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
		if(channelScr != null) {
			disposeChannelPage();
		}
		if(videoScr != null) {
			disposeVideoPage();
		}
		if(searchScr != null) {
			disposeSearchPage();
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

	public boolean fastScrolling() {
		return Settings.fastScrolling;
	}

	public void addOptionCommands() {
		if(PlatformUtils.isBelle()) {
			addCommand(searchCmd);
			addCommand(refreshCmd);
			addCommand(Settings.startScreen == 0 ? switchToPopularCmd : switchToTrendsCmd);
			addCommand(openByIdCmd);
			addCommand(settingsCmd);
			addCommand(aboutCmd);
			return;
		}
		addCommand(optsCmd);
	}

	public void openVideo(String id) {
		try {
			open(new VideoModel(id));
			if(Settings.isLowEndDevice() || !Settings.rememberSearch) {
				disposeMainPage();
			}
		} catch (Exception e) {
			App.error(this, Errors.AppUI_openVideo, e);
		}
	}

	public void openChannel(String id) {
		try {
			open(new ChannelModel(id));
			if(Settings.isLowEndDevice() || !Settings.rememberSearch) {
				disposeMainPage();
			}
		} catch (Exception e) {
			App.error(this, Errors.AppUI_openVideo, e);
		}
	}

	public void openPlaylist(String id) {
		try {
			open(new PlaylistModel(id));
			if(Settings.isLowEndDevice() || !Settings.rememberSearch) {
				disposeMainPage();
			}
		} catch (Exception e) {
			App.error(this, Errors.AppUI_openVideo, e);
		}
	}

}
