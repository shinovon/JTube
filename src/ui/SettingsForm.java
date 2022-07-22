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

import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import App;
import Util;
import Errors;
import Locale;
import Settings;
import Constants;
import LocaleConstants;

public class SettingsForm extends Form implements CommandListener, ItemCommandListener, LocaleConstants, Constants, ItemStateListener {
	
	static final String[] VIDEO_QUALITIES = new String[] { 
			"144p", 
			"360p", 
			"720p", 
			Locale.s(SET_VQ_AudioOnly),
			"240p (" + Locale.s(SET_VQ_NoAudio) + ")" };
	static final String[] NET_CHECKS = new String[] { 
			Locale.s(SET_HTTPProxy),
			Locale.s(SET_IteroniProxy)
			};
	static final String[] APPEARANCE_CHECKS = new String[] { 
			Locale.s(SET_VideoPreviews), 
			Locale.s(SET_SearchChannels), 
			Locale.s(SET_SearchPlaylists),
			Locale.s(SET_Amoled),
			Locale.s(SET_SmallPreviews)
			};
	static final String[] MISC_CHECKS = new String[] { 
			Locale.s(SET_RememberSearch),
			Locale.s(SET_PreLoadRMS)
			};
	static final String[] DEBUG_CHECKS = new String[] { 
			"Debug memory",
			"Debug render",
			"Async loading",
			"Fast scrolling"
			};
	static final String[] PLAYBACK_METHODS = new String[] { 
			Locale.s(SET_Browser),
			Locale.s(SET_SymbianOnline),
			Locale.s(SET_Via2yxa)
			};
	static final String[] ON_OFF = new String[] { 
			Locale.s(SET_On),
			Locale.s(SET_Off)
			};
	
	static final Command backCmd = new Command(Locale.s(CMD_Back), Command.BACK, 1);
	static final Command applyCmd = new Command(Locale.s(CMD_Apply), Command.BACK, 1);
	static final Command resetCmd = new Command(Locale.s(SET_Reset), Command.ITEM, 3);
	static final Command langCmd = new Command(Locale.s(SET_ChooseLanguage), Command.ITEM, 1);
	
	private ChoiceGroup videoResChoice;
	private TextField regionText;
	private TextField downloadDirText;
	private TextField httpProxyText;
	private ChoiceGroup netChoice;
	private TextField invidiousText;
	private TextField imgProxyText;
	private ChoiceGroup uiChoice;
	private StringItem dirBtn;
	private ChoiceGroup debugChoice;
	private ChoiceGroup playMethodChoice;
	private TextField downloadBufferText;
	private ChoiceGroup checkUpdatesChoice;
	private ChoiceGroup miscChoice;
	private ChoiceGroup autoStartChoice;

	private List dirList;
	private String curDir;
	private int proxyTextIdx;
	
	private List langsList;

	private static final Command dirCmd = new Command("...", Command.ITEM, 1);

	private final static Command dirOpenCmd = new Command(Locale.s(CMD_Open), Command.ITEM, 1);
	private final static Command dirSelectCmd = new Command(Locale.s(CMD_Apply), Command.OK, 2);

	public SettingsForm() {
		super(Locale.s(TITLE_Settings));
		setItemStateListener(this);
		setCommandListener(this);
		addCommand(applyCmd);
		Font titleFont = Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL);
		int titleLayout = Item.LAYOUT_2 | Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER;
		StringItem videoLabel = new StringItem(null, " " + Locale.s(SET_Video));
		videoLabel.setFont(titleFont);
		videoLabel.setLayout(Item.LAYOUT_2 | Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER);
		StringItem uiLabel = new StringItem(null, " " + Locale.s(SET_Appearance));
		uiLabel.setFont(titleFont);
		uiLabel.setLayout(titleLayout);
		StringItem netLabel = new StringItem(null, " " + Locale.s(SET_Network));
		netLabel.setFont(titleFont);
		netLabel.setLayout(titleLayout);
		StringItem miscLabel = new StringItem(null, " " + Locale.s(SET_OtherSettings));
		miscLabel.setFont(titleFont);
		miscLabel.setLayout(titleLayout);
		videoResChoice = new ChoiceGroup(Locale.s(SET_VideoRes), ChoiceGroup.POPUP, VIDEO_QUALITIES, null);
		regionText = new TextField(Locale.s(SET_CountryCode), Settings.region, 3, TextField.ANY);
		playMethodChoice = new ChoiceGroup(Locale.s(SET_PlaybackMethod), ChoiceGroup.POPUP, PLAYBACK_METHODS, null);
		checkUpdatesChoice = new ChoiceGroup(Locale.s(SET_CheckUpdates), ChoiceGroup.POPUP, ON_OFF, null);
		uiChoice = new ChoiceGroup(null, ChoiceGroup.MULTIPLE, APPEARANCE_CHECKS, null);
		netChoice = new ChoiceGroup(null, ChoiceGroup.MULTIPLE, NET_CHECKS, null);
		miscChoice = new ChoiceGroup(null, ChoiceGroup.MULTIPLE, MISC_CHECKS, null);
		downloadDirText = new TextField(Locale.s(SET_DownloadDir), Settings.downloadDir, 256, TextField.URL);
		dirBtn = new StringItem(null, "...", Item.BUTTON);
		dirBtn.setLayout(Item.LAYOUT_2 | Item.LAYOUT_RIGHT);
		dirBtn.setDefaultCommand(dirCmd);
		dirBtn.setItemCommandListener(this);
		invidiousText = new TextField(Locale.s(SET_InvAPI), Settings.inv, 256, TextField.URL);
		invidiousText.setLayout(Item.LAYOUT_2 | Item.LAYOUT_LEFT);
		httpProxyText = new TextField(Locale.s(SET_StreamProxy), Settings.serverstream, 256,
				Settings.iteroniPlaybackProxy ? TextField.URL | TextField.UNEDITABLE : TextField.URL);
		imgProxyText = new TextField(Locale.s(SET_ImagesProxy), Settings.imgproxy, 256, TextField.URL);
		downloadBufferText = new TextField(Locale.s(SET_DownloadBuffer), Integer.toString(Settings.downloadBuffer), 6, TextField.NUMERIC);
		debugChoice = new ChoiceGroup("Debug", ChoiceGroup.MULTIPLE, DEBUG_CHECKS, null);
		autoStartChoice = new ChoiceGroup(Locale.s(SET_AutoStart), ChoiceGroup.POPUP, ON_OFF, null);
		append(videoLabel);
		append(videoResChoice);
		append(playMethodChoice);
		append(downloadDirText);
		append(dirBtn);
		append(uiLabel);
		append(uiChoice);
		append(regionText);
		StringItem langBtn = new StringItem(null, Locale.s(SET_ChooseLanguage), StringItem.BUTTON);
		langBtn.addCommand(langCmd);
		langBtn.setDefaultCommand(langCmd);
		langBtn.setItemCommandListener(this);
		langBtn.setLayout(Item.LAYOUT_2 | Item.LAYOUT_EXPAND);
		append(langBtn);
		append(netLabel);
		append(netChoice);
		append(invidiousText);
		proxyTextIdx = append(httpProxyText);
		append(imgProxyText);
		append(downloadBufferText);
		append(miscLabel);
		append(checkUpdatesChoice);
		append(autoStartChoice);
		append(debugChoice);
		StringItem resetBtn = new StringItem(null, Locale.s(SET_Reset), StringItem.BUTTON);
		resetBtn.addCommand(resetCmd);
		resetBtn.setDefaultCommand(resetCmd);
		resetBtn.setItemCommandListener(this);
		resetBtn.setLayout(Item.LAYOUT_2 | Item.LAYOUT_EXPAND);
		append(resetBtn);
	}
	
	public void show() {
		uiChoice.setSelectedIndex(0, Settings.videoPreviews);
		uiChoice.setSelectedIndex(1, Settings.searchChannels);
		uiChoice.setSelectedIndex(2, Settings.searchPlaylists);
		uiChoice.setSelectedIndex(3, Settings.amoled);
		uiChoice.setSelectedIndex(4, Settings.smallPreviews);
		netChoice.setSelectedIndex(0, Settings.httpStream);
		netChoice.setSelectedIndex(1, Settings.iteroniPlaybackProxy);
		debugChoice.setSelectedIndex(0, Settings.debugMemory);
		debugChoice.setSelectedIndex(1, Settings.renderDebug);
		debugChoice.setSelectedIndex(2, Settings.asyncLoading);
		debugChoice.setSelectedIndex(3, Settings.fastScrolling);
		miscChoice.setSelectedIndex(0, Settings.rememberSearch);
		miscChoice.setSelectedIndex(1, Settings.rmsPreviews);
		try {
			playMethodChoice.setSelectedIndex(Settings.watchMethod, true);
		} catch (IndexOutOfBoundsException e) {
			playMethodChoice.setSelectedIndex(Settings.watchMethod = 0, true);
		}
		checkUpdatesChoice.setSelectedIndex(Settings.checkUpdates ? 0 : 1, true);
		autoStartChoice.setSelectedIndex(Settings.autoStart ? 0 : 1, true);
		setResolution();
	}
	
	private void setResolution() {
		if(Settings.videoRes == null) {
			videoResChoice.setSelectedIndex(1, true);
		} else if(Settings.videoRes.equals("144p")) {
			videoResChoice.setSelectedIndex(0, true);
		} else if(Settings.videoRes.equals("360p")) {
			videoResChoice.setSelectedIndex(1, true);
		} else if(Settings.videoRes.equals("720p")) {
			videoResChoice.setSelectedIndex(2, true);
		} else if(Settings.videoRes.equals("_audiohigh")) {
			videoResChoice.setSelectedIndex(3, true);
		} else if(Settings.videoRes.equals("_240p")) {
			videoResChoice.setSelectedIndex(4, true);
		}
	}
	
	private void applySettings() {
		try {
			int i = videoResChoice.getSelectedIndex();
			if(i == 0) {
				Settings.videoRes = "144p";
			} else if(i == 1) {
				Settings.videoRes = "360p";
			} else if(i == 2) {
				Settings.videoRes = "720p";
			} else if(i == 3) {
				Settings.videoRes = "_audiohigh";
			} else if(i == 4) {
				Settings.videoRes = "_240p";
			}
			Settings.region = regionText.getString().trim().toUpperCase();
			String dir = downloadDirText.getString();
			//dir = Util.replace(dir, "/", dirsep);
			dir = Util.replace(dir, "\\", Path_separator);
			while (dir.endsWith(Path_separator)) {
				dir = dir.substring(0, dir.length() - 1);
			}
			Settings.downloadDir = dir;
			boolean[] net = new boolean[netChoice.size()];
			netChoice.getSelectedFlags(net);
			boolean[] ui = new boolean[uiChoice.size()];
			uiChoice.getSelectedFlags(ui);
			boolean[] misc = new boolean[miscChoice.size()];
			miscChoice.getSelectedFlags(misc);
			Settings.videoPreviews = ui[0];
			Settings.searchChannels = ui[1];
			Settings.searchPlaylists = ui[2];
			Settings.amoled = ui[3];
			Settings.smallPreviews = ui[4];
			Settings.httpStream = net[0];
			Settings.iteroniPlaybackProxy = net[1];
			Settings.rememberSearch = misc[0];
			Settings.rmsPreviews = misc[1];
			Settings.serverstream = httpProxyText.getString();
			Settings.inv = invidiousText.getString();
			Settings.imgproxy = imgProxyText.getString();
			Settings.debugMemory = debugChoice.isSelected(0);
			Settings.watchMethod = playMethodChoice.getSelectedIndex();
			Settings.downloadBuffer = Integer.parseInt(downloadBufferText.getString());
			Settings.checkUpdates = checkUpdatesChoice.isSelected(0);
			Settings.renderDebug = debugChoice.isSelected(1);
			Settings.asyncLoading = debugChoice.isSelected(2);
			Settings.fastScrolling = debugChoice.isSelected(3);
			Settings.autoStart = autoStartChoice.isSelected(0);
			Settings.saveConfig();
		} catch (Exception e) {
			App.error(this, Errors.Settings_apply, e);
		}
		Settings.registerPush();
	}
	
	private void dirListOpen(String f, String title) {
		dirList = new List(title, List.IMPLICIT);
		dirList.setTitle(title);
		dirList.addCommand(backCmd);
		dirList.addCommand(List.SELECT_COMMAND);
		dirList.setSelectCommand(List.SELECT_COMMAND);
		dirList.setCommandListener(this);
		dirList.addCommand(dirSelectCmd);
		dirList.append("- " + Locale.s(CMD_Select), null);
		try {
			FileConnection fc = (FileConnection) Connector.open("file:///" + f);
			Enumeration list = fc.list();
			while(list.hasMoreElements()) {
				String s = (String) list.nextElement();
				if(s.endsWith("/")) {
					dirList.append(s.substring(0, s.length() - 1), null);
				}
			}
			fc.close();
		} catch (Exception e) {
		}
		AppUI.inst.display(dirList);
	}
	
	public void commandAction(Command c, Displayable d) {
		try {
			if(d == dirList) {
				if(c == backCmd) {
					if(curDir == null) {
						dirList = null;
						AppUI.inst.display(this);
					} else {
						if(curDir.indexOf("/") == -1) {
							dirList = new List("", List.IMPLICIT);
							dirList.addCommand(backCmd);
							dirList.setTitle("");
							dirList.addCommand(List.SELECT_COMMAND);
							dirList.setSelectCommand(List.SELECT_COMMAND);
							dirList.setCommandListener(this);
							for(int i = 0; i < Settings.rootsList.size(); i++) {
								String s = (String) Settings.rootsList.elementAt(i);
								if(s.startsWith("file:///")) s = s.substring("file:///".length());
								if(s.endsWith("/")) s = s.substring(0, s.length() - 1);
								dirList.append(s, null);
							}
							curDir = null;
							AppUI.inst.display(dirList);
							return;
						}
						String sub = curDir.substring(0, curDir.lastIndexOf('/'));
						String fn = "";
						if(sub.indexOf('/') != -1) {
							fn = sub.substring(sub.lastIndexOf('/') + 1);
						} else {
							fn = sub;
						}
						curDir = sub;
						dirListOpen(sub + "/", fn);
					}
				}
				if(c == dirOpenCmd || c == List.SELECT_COMMAND) {
					String fs = curDir;
					String f = "";
					if(fs != null) f += curDir + "/";
					String is = dirList.getString(dirList.getSelectedIndex());
					if(is.equals("- " + Locale.s(CMD_Select))) {
						dirList = null;
						downloadDirText.setString(f);
						curDir = null;
						AppUI.inst.display(this);
						return;
					}
					f += is;
					curDir = f;
					dirListOpen(f + "/", is);
					return;
				}
				if(c == dirSelectCmd) {
					dirList = null;
					downloadDirText.setString(curDir + "/");
					curDir = null;
					AppUI.inst.display(this);
				}
				return;
			}
			if(d == langsList) {
				if(c == List.SELECT_COMMAND) {
					Settings.customLocale = ((String[])Settings.langsList.elementAt(langsList.getSelectedIndex()))[0];
				} else {
					AppUI.inst.display(this);
				}
				return;
			}
			applySettings();
			AppUI.inst.display(null);
			UIScreen screen = AppUI.inst.getCurrentScreen();
			if(screen != null) {
				screen.relayout();
			}
		} catch (Exception e) {
		}
	}

	public void commandAction(Command c, Item item) {
		if(c == resetCmd) {
			Settings.removeConfig();
			App.midlet.notifyDestroyed();
			return;
		}
		if(c == langCmd) {
			if(langsList == null) {
				langsList = new List("", List.EXCLUSIVE);
				langsList.setFitPolicy(List.TEXT_WRAP_ON);
				langsList.addCommand(backCmd);
				langsList.addCommand(List.SELECT_COMMAND);
				langsList.setSelectCommand(List.SELECT_COMMAND);
				langsList.setCommandListener(this);
				int i = 0;
				while(i < Settings.langsList.size()) {
					String[] a = (String[]) Settings.langsList.elementAt(i++);
					int x = langsList.append((a[2].length() > 0 ? a[2] + (a[1].equalsIgnoreCase(a[2]) ? "" : " (" + a[1] + ")") : a[1]) + "\n" + a[3], null);
					if(a[0].equals(Settings.customLocale)) {
						langsList.setSelectedIndex(x, true);
					}
				}
			}
			AppUI.inst.display(langsList);
			return;
		}
		if(c == dirCmd) {
			dirList = new List("", List.IMPLICIT);
			Settings.getRoots();
			for(int i = 0; i < Settings.rootsList.size(); i++) {
				String s = (String) Settings.rootsList.elementAt(i);
				if(s.startsWith("file:///")) s = s.substring("file:///".length());
				if(s.endsWith("/")) s = s.substring(0, s.length() - 1);
				dirList.append(s, null);
			}
			dirList.addCommand(List.SELECT_COMMAND);
			dirList.setSelectCommand(List.SELECT_COMMAND);
			dirList.addCommand(backCmd);
			dirList.setCommandListener(this);
			AppUI.inst.display(dirList);
		}
	}

	public void itemStateChanged(Item item) {
		if(item == netChoice) {
			boolean b = Settings.iteroniPlaybackProxy;
			Settings.iteroniPlaybackProxy = netChoice.isSelected(3);
			if(Settings.iteroniPlaybackProxy != b) {
				httpProxyText = new TextField(Locale.s(SET_StreamProxy), Settings.serverstream, 256,
						Settings.iteroniPlaybackProxy ? TextField.URL | TextField.UNEDITABLE : TextField.URL);
				set(proxyTextIdx, httpProxyText);
			}
		}
		if(item == playMethodChoice) {
			Settings.watchMethod = playMethodChoice.getSelectedIndex();
		}
	}

}
