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

public class SettingsForm extends Form implements CommandListener, ItemCommandListener, Commands, Constants, ItemStateListener {
	
	static final String[] VIDEO_QUALITIES = new String[] { 
			"144p", 
			"360p", 
			"720p", 
			Locale.s(SET_VQ_AudioOnly), 
			"240p (" + Locale.s(SET_VQ_NoAudio) + ")" };
/*
	static final String[] VIDEO_QUALITIES_2YXA = new String[] {
			"RTSP",
			"360p",  
			"240p",
			"144p (MP4)", 
			"144p (3GP)",
			Locale.s(SET_VQ_AudioOnly)
			};
*/
	static final String[] SETTINGS_CHECKS = new String[] { 
			Locale.s(SET_RememberSearch), 
			Locale.s(SET_HTTPProxy), 
			Locale.s(SET_PreLoadRMS) };
	static final String[] APPEARANCE_CHECKS = new String[] { 
			Locale.s(SET_CustomItems), 
			Locale.s(SET_VideoPreviews), 
			Locale.s(SET_SearchChannels), 
			Locale.s(SET_SearchPlaylists) };
	static final String[] DEBUG_CHECKS = new String[] { 
			"Debug memory"
			};
	static final String[] PLAYBACK_METHODS = new String[] { 
			Locale.s(SET_Browser),
			Locale.s(SET_SymbianOnline),
			Locale.s(SET_Via2yxa)
			};
	
	private ChoiceGroup videoResChoice;
	//private ChoiceGroup res2yxaChoice;
	private TextField regionText;
	private TextField downloadDirText;
	private TextField httpProxyText;
	private ChoiceGroup checksChoice;
	private TextField invidiousText;
	private TextField imgProxyText;
	private ChoiceGroup uiChoice;
	private StringItem dirBtn;
	private TextField customLocaleText;
	private ChoiceGroup debugChoice;
	private ChoiceGroup playMethodChoice;
	private TextField downloadBufferText;

	private List dirList;
	private String curDir;

	//private int videoResChoiceIndex;


	private static final Command dirCmd = new Command("...", Command.ITEM, 1);

	private final static Command dirOpenCmd = new Command(Locale.s(CMD_Open), Command.ITEM, 1);
	private final static Command dirSelectCmd = new Command(Locale.s(CMD_Apply), Command.OK, 2);

	public SettingsForm() {
		super(Locale.s(TITLE_Settings));
		setItemStateListener(this);
		setCommandListener(this);
		addCommand(applyCmd);
		/*if(App.watchMethod == 2) {
			res2yxaChoice = new ChoiceGroup(Locale.s(SET_VideoRes), ChoiceGroup.EXCLUSIVE, VIDEO_QUALITIES_2YXA, null);
			videoResChoiceIndex = append(res2yxaChoice);
		} else {*/
		videoResChoice = new ChoiceGroup(Locale.s(SET_VideoRes), ChoiceGroup.EXCLUSIVE, VIDEO_QUALITIES, null);
		/*videoResChoiceIndex = */append(videoResChoice);
		//}
		regionText = new TextField(Locale.s(SET_CountryCode), App.region, 3, TextField.ANY);
		append(regionText);
		playMethodChoice = new ChoiceGroup(Locale.s(SET_PlaybackMethod), ChoiceGroup.EXCLUSIVE, PLAYBACK_METHODS, null);
		append(playMethodChoice);
		uiChoice = new ChoiceGroup(Locale.s(SET_Appearance), ChoiceGroup.MULTIPLE, APPEARANCE_CHECKS, null);
		append(uiChoice);
		checksChoice = new ChoiceGroup(Locale.s(SET_OtherSettings), ChoiceGroup.MULTIPLE, SETTINGS_CHECKS, null);
		append(checksChoice);
		downloadDirText = new TextField(Locale.s(SET_DownloadDir), App.downloadDir, 256, TextField.URL);
		append(downloadDirText);
		dirBtn = new StringItem(null, "...", Item.BUTTON);
		dirBtn.setLayout(Item.LAYOUT_2 | Item.LAYOUT_RIGHT);
		dirBtn.setDefaultCommand(dirCmd);
		dirBtn.setItemCommandListener(this);
		append(dirBtn);
		invidiousText = new TextField(Locale.s(SET_InvAPI), App.inv, 256, TextField.URL);
		append(invidiousText);
		httpProxyText = new TextField(Locale.s(SET_StreamProxy), App.serverstream, 256, TextField.URL);
		append(httpProxyText);
		append(Locale.s(SET_Tip3) + "\n");
		imgProxyText = new TextField(Locale.s(SET_ImagesProxy), App.imgproxy, 256, TextField.URL);
		append(imgProxyText);
		append(Locale.s(SET_Tip2) + "\n");
		customLocaleText = new TextField(Locale.s(SET_CustomLocaleId), App.customLocale, 8, TextField.ANY);
		append(customLocaleText);
		downloadBufferText = new TextField(Locale.s(SET_DownloadBuffer), Integer.toString(App.downloadBuffer), 6, TextField.NUMERIC);
		append(downloadBufferText);
		debugChoice = new ChoiceGroup("Debug", ChoiceGroup.MULTIPLE, DEBUG_CHECKS, null);
		//append(debugChoice);
	}
	
	public void show() {
		uiChoice.setSelectedIndex(0, App.customItems);
		uiChoice.setSelectedIndex(1, App.videoPreviews);
		uiChoice.setSelectedIndex(2, App.searchChannels);
		uiChoice.setSelectedIndex(3, App.searchPlaylists);
		checksChoice.setSelectedIndex(0, App.rememberSearch);
		checksChoice.setSelectedIndex(1, App.httpStream);
		checksChoice.setSelectedIndex(2, App.rmsPreviews);
		debugChoice.setSelectedIndex(0, App.debugMemory);
		playMethodChoice.setSelectedIndex(App.watchMethod, true);
		//checksChoice.setSelectedIndex(4, App.apiProxy);
		//itemStateChanged(playMethodChoice);
		setResolution();
	}
	
	private void setResolution() {
		/*
		if(App.watchMethod == 2) {
			switch(App.videoRes2yxa) {
			case -1:
				res2yxaChoice.setSelectedIndex(1, true);
				App.videoRes2yxa = 18;
				break;
			case 7:
				res2yxaChoice.setSelectedIndex(0, true);
				break;
			case 18:
				res2yxaChoice.setSelectedIndex(1, true);
				break;
			case 3:
				res2yxaChoice.setSelectedIndex(2, true);
				break;
			case 6:
				res2yxaChoice.setSelectedIndex(3, true);
				break;
			case 2:
				res2yxaChoice.setSelectedIndex(4, true);
				break;
			case 4:
				res2yxaChoice.setSelectedIndex(5, true);
				break;
			}
		} else {*/
		if(App.videoRes == null) {
			videoResChoice.setSelectedIndex(1, true);
		} else if(App.videoRes.equals("144p")) {
			videoResChoice.setSelectedIndex(0, true);
		} else if(App.videoRes.equals("360p")) {
			videoResChoice.setSelectedIndex(1, true);
		} else if(App.videoRes.equals("720p")) {
			videoResChoice.setSelectedIndex(2, true);
		} else if(App.videoRes.equals("_audiohigh")) {
			videoResChoice.setSelectedIndex(3, true);
		} else if(App.videoRes.equals("_240p")) {
			videoResChoice.setSelectedIndex(4, true);
		}
		//}
	}
	
	private void applySettings() {
		try {
			/*
			if((App.watchMethod = playMethodChoice.getSelectedIndex()) == 2) {
				if(res2yxaChoice != null) {
					switch(res2yxaChoice.getSelectedIndex()) {
					case 0:
						App.videoRes2yxa = 7;
						break;
					case 1:
						App.videoRes2yxa = 18;
						break;
					case 2:
						App.videoRes2yxa = 3;
						break;
					case 3:
						App.videoRes2yxa = 6;
						break;
					case 4:
						App.videoRes2yxa = 2;
						break;
					case 5:
						App.videoRes2yxa = 4;
						break;
					}
				}
			} else {*/
			int i = videoResChoice.getSelectedIndex();
			if(i == 0) {
				App.videoRes = "144p";
			} else if(i == 1) {
				App.videoRes = "360p";
			} else if(i == 2) {
				App.videoRes = "720p";
			} else if(i == 3) {
				App.videoRes = "_audiohigh";
			} else if(i == 4) {
				App.videoRes = "_240p";
			}
			//}
			App.region = regionText.getString();
			String dir = downloadDirText.getString();
			//dir = Util.replace(dir, "/", dirsep);
			dir = Util.replace(dir, "\\", Path_separator);
			while (dir.endsWith(Path_separator)) {
				dir = dir.substring(0, dir.length() - 1);
			}
			App.downloadDir = dir;
			boolean[] s = new boolean[checksChoice.size()];
			checksChoice.getSelectedFlags(s);
			boolean[] ui = new boolean[uiChoice.size()];
			uiChoice.getSelectedFlags(ui);
			App.customItems = ui[0];
			App.videoPreviews = ui[1];
			App.searchChannels = ui[2];
			App.searchPlaylists = ui[3];
			App.rememberSearch = s[0];
			App.httpStream = s[1];
			App.rmsPreviews = s[2];
			App.serverstream = httpProxyText.getString();
			App.inv = invidiousText.getString();
			App.imgproxy = imgProxyText.getString();
			App.customLocale = customLocaleText.getString().trim().toLowerCase();
			App.debugMemory = debugChoice.isSelected(0);
			App.watchMethod = playMethodChoice.getSelectedIndex();
			App.downloadBuffer = Integer.parseInt(downloadBufferText.getString());
			Settings.saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.Settings_apply, e);
		}
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
			e.printStackTrace();
		}
		AppUI.display(dirList);
	}
	
	public void commandAction(Command c, Displayable d) {
		try {
			if(d == dirList) {
				if(c == backCmd) {
					if(curDir == null) {
						dirList = null;
						AppUI.display(this);
					} else {
						if(curDir.indexOf("/") == -1) {
							dirList = new List("", List.IMPLICIT);
							dirList.addCommand(backCmd);
							dirList.setTitle("");
							dirList.addCommand(List.SELECT_COMMAND);
							dirList.setSelectCommand(List.SELECT_COMMAND);
							dirList.setCommandListener(this);
							for(int i = 0; i < Settings.rootsVector.size(); i++) {
								String s = (String) Settings.rootsVector.elementAt(i);
								if(s.startsWith("file:///")) s = s.substring("file:///".length());
								if(s.endsWith("/")) s = s.substring(0, s.length() - 1);
								dirList.append(s, null);
							}
							curDir = null;
							AppUI.display(dirList);
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
						AppUI.display(this);
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
					AppUI.display(this);
				}
				return;
			}
			applySettings();
			AppUI.display(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void commandAction(Command c, Item item) {
		if(c == dirCmd) {
			dirList = new List("", List.IMPLICIT);
			Settings.getRoots();
			for(int i = 0; i < Settings.rootsVector.size(); i++) {
				String s = (String) Settings.rootsVector.elementAt(i);
				if(s.startsWith("file:///")) s = s.substring("file:///".length());
				if(s.endsWith("/")) s = s.substring(0, s.length() - 1);
				dirList.append(s, null);
			}
			dirList.addCommand(List.SELECT_COMMAND);
			dirList.setSelectCommand(List.SELECT_COMMAND);
			dirList.addCommand(backCmd);
			dirList.setCommandListener(this);
			AppUI.display(dirList);
		}
	}

	public void itemStateChanged(Item item) {
		if(item == playMethodChoice) {
			App.watchMethod = playMethodChoice.getSelectedIndex();
			/*
			if(App.watchMethod == 2) {
				if(videoResChoice != null) {
					delete(videoResChoiceIndex);
					videoResChoice = null;
					res2yxaChoice = new ChoiceGroup(Locale.s(SET_VideoRes), ChoiceGroup.EXCLUSIVE, VIDEO_QUALITIES_2YXA, null);
					insert(videoResChoiceIndex, res2yxaChoice);
				}
			} else if(res2yxaChoice != null) {
				delete(videoResChoiceIndex);
				res2yxaChoice = null;
				videoResChoice = new ChoiceGroup(Locale.s(SET_VideoRes), ChoiceGroup.EXCLUSIVE, VIDEO_QUALITIES, null);
				insert(videoResChoiceIndex, videoResChoice);
			}
			setResolution();
			*/
		}
	}

}
