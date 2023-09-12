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
package jtube.ui;

import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import cc.nnproject.json.AbstractJSON;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import jtube.App;
import jtube.Constants;
import jtube.Errors;
import jtube.InvidiousException;
import jtube.Loader;
import jtube.LocalStorage;
import jtube.RunnableTask;
import jtube.Settings;
import jtube.Util;
import jtube.models.AbstractModel;
import jtube.models.ChannelModel;
import jtube.models.PlaylistModel;
import jtube.models.VideoModel;
import jtube.ui.nokia_extensions.DirectFontUtil;
import jtube.ui.nokia_extensions.TextEditorUtil;
import jtube.ui.screens.ChannelScreen;
import jtube.ui.screens.HomeScreen;
import jtube.ui.screens.NavigationScreen;
import jtube.ui.screens.SearchScreen;
import jtube.ui.screens.SubscriptionsFeedScreen;
import jtube.ui.screens.VideoScreen;

public class AppUI implements CommandListener, Constants, UIConstants, LocaleConstants, Commands, Runnable {
	
	public static final Display display = Display.getDisplay(App.midlet);

	public static AppUI inst;
	
	public AppUI() {
		if(inst != null) throw new Error();
		inst = this;
		UIScreen.ui = this;
		UIItem.ui = this;
	}
	
	public HomeScreen mainScr;
	public SearchScreen searchScr;
	public VideoScreen videoScr;
	public SubscriptionsFeedScreen subsScr;
	
	public int currentTab;
	public Stack[] screenStacks = new Stack[] { new Stack(), new Stack() };
	
	private SettingsForm settingsForm;

	private static JTubeCanvas canv;
	public UIScreen current;

	private Object repaintLock = new Object();

	public boolean scrolling;
	
	public int repaintTime;
	
	private Vector commands = new Vector();
	
	protected boolean keyInput = false;

	private List optionsList;

	private boolean repaint;
	
	
	public void run() {
		try {
			while(App.midlet.running) {
				if(display.getCurrent() != canv) {
					synchronized (repaintLock) {
						repaintLock.wait();
					}
				}
				if(!scrolling) {
					repaint = false;
					_repaint();
					if(repaint) continue;
					synchronized (repaintLock) {
						repaintLock.wait(Settings.powerSaving ? 5000 : 2000);
					}
					continue;
				}
				_repaint();
				limitFramerate();
			}
		} catch (InterruptedException e) {
		}
	}

	private void limitFramerate() throws InterruptedException {
		int i = Settings.powerSaving ? 67 : 33;
		i -= repaintTime;
		if(i > 0) Thread.sleep(i);
	}

	private void _repaint() {
		long time = System.currentTimeMillis();
		canv.updateScreen();
		repaintTime = (int) (System.currentTimeMillis() - time);
	}

	public void repaint() {
		repaint = true;
		synchronized (repaintLock) {
			repaintLock.notify();
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
			return Settings.amoled ? 0x383838 : 0xE5E5E5;
		case COLOR_SCREEN_DARK_ALPHA:
			return 0x7f000000;
		case COLOR_GRAYTEXT:
			return Settings.amoled ? 0x7F7F7F : 0x5D5D5D;
		case COLOR_SCROLLBAR_BG:
			return 0x555555;
		case COLOR_SCROLLBAR_FG:
			return 0xAAAAAA;
		case COLOR_ITEM_HIGHLIGHT:
			return Settings.amoled ? 0x5D5D5D : 0x9A9A9A;
		case COLOR_BUTTON_HOVER_BG:
			return Settings.amoled ? 0x111111 : 0xCCCCCC;
		case COLOR_TIMETEXT:
			return 0xDDDDDD;
		case COLOR_SCROLLBAR_BORDER:
			return Settings.amoled ? 0 : -1;
		case COLOR_TOPBAR_BORDER:
			return Settings.amoled ? 0x2D2D2D : 0xBABABA;
		case COLOR_TOPBAR_BG:
			return Settings.amoled ? 0 : 0xFAFAFA;
		case COLOR_SOFTBAR_BG:
			return Settings.amoled ? 0 : 0xFAFAFA;
		case COLOR_SOFTBAR_FG:
			return Settings.amoled ? -1 : 0;
		case COLOR_ICON:
			return Settings.amoled ? -1 : 0;
		case COLOR_SUBSCRIBE_BG:
			return Settings.amoled ? 0xF1F1F1 : 0x0F0F0F;
		case COLOR_SUBSCRIBE_FG:
			return Settings.amoled ? 0x0F0F0F : 0xF1F1F1;
		case COLOR_SUBSCRIBED_BG:
			return Settings.amoled ? 0x383838 : 0xF2F2F2;
		case COLOR_SUBSCRIBED_FG:
			return Settings.amoled ? 0xF1F1F1 : 0x0F0F0F;
		case COLOR_CHANNELPAGE_BG:
			return Settings.amoled ? 0x0F0F0F : 0xFFFFFF;
		case COLOR_CHANNELTAB_SELECT:
			return Settings.amoled ? 0xAAAAAA : 0x606060;
		default:
			return 0;
		}
	}
	
	public void setScreen(UIScreen s) {
		removeCommands();
		if(current != null) {
			current.hide();
		}
		current = s;
		canv.resetScreen();
		repaint();
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
	
	public int getWidth() {
		return canv.width;
	}
	
	public int getHeight() {
		return canv.height;
	}
	
	public void loadMain() {
		try {
			if(mainScr == null) {
				mainScr = new HomeScreen();
			}
			mainScr.busy = true;
			setScreen(mainScr);
			display(null);
			if(Settings.startScreen == 0) {
				loadTrends();
			} else {
				loadPopular();
			}
			Util.gc();
		} catch (InvidiousException e) {
			if(e.toString().indexOf("key: \"content\"") != -1) {
				Settings.region = "US";
				loadMain();
				return;
			}
			App.error(this, Errors.AppUI_loadForm, e);
		} catch (OutOfMemoryError e) {
			Util.gc();
			App.error(this, Errors.AppUI_loadForm, "Out of memory!");
		} catch (Throwable e) {
			App.error(this, Errors.AppUI_loadForm, e);
		}
		mainScr.busy = false;
	}

	public void init() {
		inst = this;
		canv = new JTubeCanvas(this);
		resetFullScreenMode();
		try {
			DirectFontUtil.init();
		} catch (Throwable e) {
		}
		try {
			TextEditorUtil.init();
		} catch (Throwable e) {
		}
	}
	
	public void resetFullScreenMode() {
		try {
			if(Settings.fullScreen) {
				canv.setFullScreenMode(true);
				canv.setCommandListener(null);
			} else {
				canv.setFullScreenMode(false);
				canv.setCommandListener(this);
			}
		} catch (Exception e) {
		}
	}
	
	public void load(String s) throws IOException {
		mainScr.busy = true;
		try {
			AbstractJSON r = App.invApi(s+"?",
					VIDEO_FIELDS +
					(getWidth() >= 320 ? ",viewCount" : "")
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
			}
			j = null;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.AppUI_load, e);
		}
		Loader.start();
		Util.gc();
		mainScr.busy = false;
		repaint();
	}

	public void loadTrends() throws IOException {
		mainScr.setTitle(Locale.s(TITLE_Trends));
		load("trending");
	}

	public void loadPopular() throws IOException {
		mainScr.setTitle(Locale.s(TITLE_Popular));
		load("popular");
	}

	public void search(String q) {
		Loader.stop();
		searchScr = new SearchScreen(q);
		searchScr.busy = true;
		display(null);
		nextScreen(searchScr);
		if(Settings.isLowEndDevice()) {
			disposeMainPage();
		}
		try {
			JSONArray j = (JSONArray) App.invApi("search?q=" + Util.url(q) + "&type=all",
					SEARCH_FIELDS + ",type" +
					(Settings.searchChannels || Settings.searchPlaylists ? ",authorThumbnails,subCount,playlistId,videoCount" : "") +
					(getWidth() >= 320 ? ",viewCount" : "")
					);
			int l = j.size();
			for(int i = 0; i < l; i++) {
				UIItem item = parseAndMakeItem(j.getObject(i), true, i);
				if(item == null) continue;
				searchScr.add(item);
				if(i >= SEARCH_LIMIT) break;
			}
			j = null;
		} catch (Exception e) {
			App.error(this, Errors.AppUI_search, e);
		}
		Loader.start();
		Util.gc();
		searchScr.busy = false;
		repaint();
	}
	
	private UIItem parseAndMakeItem(JSONObject j, boolean search, int i) {
		String type = j.getNullableString("type");
		if(type == null) {
			// video
			VideoModel v = new VideoModel(j);
			v.setIndex(i);
			v.fromSearch = search;
			if(Settings.videoPreviews && !Settings.lazyLoad) Loader.add(v);
			return v.makeListItem();
		}
		if(type.equals("video")) {
			VideoModel v = new VideoModel(j);
			v.setIndex(i);
			v.fromSearch = search;
			if(Settings.videoPreviews && !Settings.lazyLoad) Loader.add(v);
			return v.makeListItem();
		}
		if(Settings.searchChannels && type.equals("channel")) {
			ChannelModel c = new ChannelModel(j);
			c.fromSearch = search;
			if(Settings.videoPreviews && !Settings.lazyLoad) Loader.add(c);
			return c.makeListItem();
		}
		if(Settings.searchPlaylists && type.equals("playlist")) {
			PlaylistModel p = new PlaylistModel(j);
			p.fromSearch = search;
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

	public void disposeSearchPage() {
		if(searchScr == null) return;
		searchScr.clear();
		searchScr = null;
		Util.gc();
	}
	
	public void refresh() {
		try {
			Loader.stop();
			display(null);
			mainScr.clear();
			if(Settings.startScreen == 0) {
				loadTrends();
			} else {
				loadPopular();
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
			Loader.stop();
			if(mainScr != null) {
				mainScr.scroll = 0;
				mainScr.clear();
			} else {
				mainScr = new HomeScreen();
			}
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
	}

	public void commandAction(Command c, Displayable d) {
		if(optionsList != null && d == optionsList) {
			if(c == List.SELECT_COMMAND) {
				switch(optionsList.getSelectedIndex()) {
				case 0:
				{
					// Search
					Loader.stop();
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
					Loader.stop();
					new Thread(new RunnableTask(RunnableTask.REFRESH)).start();
					break;
				}
				case 2:
				{
					// Switch
					Loader.stop();
					new Thread(new RunnableTask(RunnableTask.SWITCH)).start();
					break;
				}
				case 3:
				{
					// Open by ID
					Loader.stop();
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
		if(c == exitCmd) {
			exit();
			return;
		}
		if(c == searchOkCmd && d instanceof TextBox) {
			Loader.stop();
			new Thread(new RunnableTask(((TextBox) d).getString(), RunnableTask.SEARCH)).start();
			return;
		}
		if(c == goCmd && d instanceof TextBox) {
			Loader.stop();
			new Thread(new RunnableTask(((TextBox) d).getString(), RunnableTask.ID)).start();
			return;
		}
		if(this.current != null && current instanceof CommandListener) {
			((CommandListener)current).commandAction(c, d);
			return;
		}
		if(d instanceof Alert || d instanceof TextBox) {
			display(null);
			return;
		}
	}
	
	public void exit() {
		LocalStorage.clearCache();
		App.midlet.notifyDestroyed();
	}

	public void display(Displayable d) {
		if(d == null) {
			if(display.getCurrent() == canv) return;
			if(current != null && current instanceof NavigationScreen) {
				((NavigationScreen) current).canvasFocused();
			}
			display.setCurrent(canv);
			repaint();
			if(current != null) {
				current.show();
			}
			return;
		}
		if(d instanceof Alert) {
			display.setCurrent((Alert) d, canv);
			return;
		}
		display.setCurrent(d);
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
		AppUI ui = inst;
		if(current instanceof ChannelScreen && model instanceof ChannelModel
			&& ((ChannelModel)model).authorId.equals(((ChannelScreen)current).getChannel().authorId)) {
			return;
		}
		// check if already loading
		if(model instanceof VideoModel && current instanceof VideoScreen && ui.videoScr != null
			&& ((VideoModel)model).videoId.equals(((VideoModel)((VideoScreen)current).getModel()).videoId)) {
			return;
		}
//		if(model instanceof VideoModel) {
//			LocalStorage.addHistory(((VideoModel)model).videoId, ((VideoModel)model).title);
//		}
		if(model instanceof PlaylistModel) {
			if(((PlaylistModel) model).videoCount > PLAYLIST_VIDEOS_LIMIT) {
				msg(">" + PLAYLIST_VIDEOS_LIMIT + " videos!!!");
				return;
			}
		}
		if(ui.videoScr != null) {
			disposeVideoPage();
		}
		Loader.stop();
		IModelScreen scr = model.makeScreen();
		scr.setContainerScreen(formContainer);
		nextScreen((UIScreen) scr);
		display(null);
		if(scr instanceof VideoScreen) {
			ui.videoScr = (VideoScreen) scr;
		}
		Util.gc();
	}

	public void showSettings() {
		try {
			if(settingsForm == null) {
				settingsForm = new SettingsForm();
			}
			display(settingsForm);
			settingsForm.show();
		} catch (Exception e) {
			App.error(this, "Could not open settings! \n" + e.toString());
		}
	}
	
	public void showAbout(CommandListener l) {
		//boolean samsung = App.midlet.getAppProperty("JTube-Samsung-Build") != null;
		TextBox t = new TextBox("", "", 200, TextField.ANY | TextField.UNEDITABLE);
		t.setTitle("JTube v" + App.midlet.getAppProperty("MIDlet-Version"));
		t.setString("By Shinovon (nnp.nnchan.ru)" + EOL
				+ "t.me/nnmidlets" + EOL
				+ "vk.com/nnprojectcc" + EOL + EOL
				+ "Special thanks to ales_alte, Jazmin Rocio, Feodor0090, musecat77, curoviyxru"
				+ (Locale.loaded ? EOL + EOL + "Custom localization author (" + Locale.l +"): " + Locale.s(0) : ""));
		t.setCommandListener(l == null ? this : l);
		t.addCommand(new Command("OK", Command.OK, 1));
		display(t);
	}

	public void addCommand(Command c) {
		if(!Settings.fullScreen)
		try {
			canv.addCommand(c);
		} catch (Exception e) {
		}
		commands.addElement(c);
	}
	
	public void removeCommand(Command c) {
		try {
			canv.removeCommand(c);
		} catch (Exception e) {
		}
		commands.removeElement(c);
	}
	
	public void removeCommands() {
		try {
			for(int i = 0; i < commands.size(); i++) {
				Command c = (Command) commands.elementAt(i);
				canv.removeCommand(c);
			}
		} catch (Exception e) {
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
			mainScr = new HomeScreen();
			loadMain();
		} else {
			setScreen(mainScr);
		}
		if(videoScr != null) {
			disposeVideoPage();
		}
		if(searchScr != null) {
			disposeSearchPage();
		}
	}

	public void showOptions() {
		if(optionsList == null) {
			optionsList = new List("JTube Menu", List.IMPLICIT);
			optionsList.append(Locale.s(CMD_Search), null);
			optionsList.append(Locale.s(CMD_Refresh), null);
			optionsList.append(Locale.s(Settings.startScreen == 0 ? CMD_SwitchToPopular : CMD_SwitchToTrends), null);
			optionsList.append(Locale.s(CMD_OpenByID), null);
			optionsList.append(Locale.s(CMD_Settings), null);
			optionsList.append(Locale.s(CMD_About), null);
			optionsList.append(Locale.s(CMD_Exit), null);
			optionsList.addCommand(List.SELECT_COMMAND);
			optionsList.setSelectCommand(List.SELECT_COMMAND);
			optionsList.addCommand(backCmd);
			optionsList.setCommandListener(this);
		}
		display(optionsList);
	}

	public void nextScreen(UIScreen s) {
		if(screenStacks[currentTab].contains(s)) {
			screenStacks[currentTab].removeElement(s);
		}
		if(current instanceof VideoScreen) {
			disposeVideoPage();
		}
		if(!(current instanceof VideoScreen || (current instanceof SearchScreen && s instanceof SearchScreen))) {
			screenStacks[currentTab].push(current);
		}
		setScreen(s);
	}

	public void back(UIScreen s) {
		if(!screenStacks[currentTab].empty()) {
			if(s instanceof VideoScreen) {
				disposeVideoPage();
			}
			setScreen((UIScreen) screenStacks[currentTab].pop());
		} else {
			NavigationScreen.selectTab(currentTab);
		}
	}

	public boolean fastScrolling() {
		return Settings.fastScrolling;
	}

	public void openVideo(String id) {
		try {
			open(new VideoModel(id));
			if(Settings.isLowEndDevice()) {
				disposeMainPage();
			}
		} catch (Exception e) {
			App.error(this, Errors.AppUI_openVideo, e);
		}
	}

	public void openChannel(String id) {
		try {
			open(new ChannelModel(id));
			if(Settings.isLowEndDevice()) {
				disposeMainPage();
			}
		} catch (Exception e) {
			App.error(this, Errors.AppUI_openVideo, e);
		}
	}

	public void openPlaylist(String id) {
		try {
			open(new PlaylistModel(id));
			if(Settings.isLowEndDevice()) {
				disposeMainPage();
			}
		} catch (Exception e) {
			App.error(this, Errors.AppUI_openVideo, e);
		}
	}

	public JTubeCanvas getCanvas() {
		return canv;
	}

	public void loadSubs() {
		if(subsScr != null) return;
		subsScr = new SubscriptionsFeedScreen();
	}

}
