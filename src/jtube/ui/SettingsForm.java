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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

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
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import jtube.App;
import jtube.Constants;
import jtube.Errors;
import jtube.LocalStorage;
import jtube.Settings;
import jtube.Util;

public class SettingsForm extends Form implements CommandListener, ItemCommandListener, LocaleConstants, Constants, ItemStateListener {
	
//	static final String[] VIDEO_QUALITIES = new String[] { 
//			"144p", 
//			"360p", 
//			"720p", 
//			Locale.s(SET_VQ_AudioOnly),
//			"240p (" + Locale.s(SET_VQ_NoAudio) + ")"
//			};
	static final String[] PROXY_CHECKS = new String[] { 
			Locale.s(SET_UseApiProxy),
			Locale.s(SET_HTTPProxy),
			};
	static final String[] APPEARANCE_CHECKS = new String[] { 
			Locale.s(SET_VideoPreviews), 
			Locale.s(SET_Amoled),
			Locale.s(SET_SmallPreviews),
			Locale.s(SET_FullScreenMode),
			Locale.s(SET_ChannelBanners),
			Locale.s(SET_SearchSuggestions)
			};
	static final String[] MISC_CHECKS = new String[] { 
			Locale.s(SET_PreLoadRMS),
			Locale.s(SET_PowerSaving)
			};
	static final String[] DEBUG_CHECKS = new String[] { 
			"Debug render",
			"Async loading",
			"Fast scrolling",
			"Lazy load"
			};
	static final String[] PLAYBACK_METHODS = new String[] { 
			Locale.s(SET_Browser),
			Locale.s(SET_SymbianOnline),
			Locale.s(SET_Via2yxa),
			"JTDL"
			};
	static final String[] ON_OFF = new String[] { 
			Locale.s(SET_On),
			Locale.s(SET_Off)
			};
	static final String[] VIRTUAL_KEYBOARDS = new String[] { 
			Locale.s(SET_NokiaUI),
			"j2mekeyboard",
			Locale.s(SET_FullScreenInput)
			};
	static final String[] VPB_PROXY_VARIANTS = new String[] {
			Locale.s(SET_Auto),
			"Invidious",
			"nnchan",
			Locale.s(SET_UrlPrefix)
			};
	
	static final Command backCmd = new Command(Locale.s(CMD_Back), Command.BACK, 1);
	static final Command applyCmd = new Command(Locale.s(CMD_Apply), Command.BACK, 1);
	static final Command resetCmd = new Command(Locale.s(SET_Reset), Command.ITEM, 3);
	static final Command langCmd = new Command(Locale.s(SET_ChooseLanguage), Command.ITEM, 1);
	static final Command inputLangsCmd = new Command(Locale.s(SET_InputLanguages), Command.ITEM, 1);
	static final Command subsImportCmd = new Command(Locale.s(SET_ImportSubscriptions), Command.ITEM, 1);
	static final Command subsExportCmd = new Command(Locale.s(SET_ExportSubscriptions), Command.ITEM, 1);
	
//	private ChoiceGroup videoResChoice;
	private TextField regionText;
	private TextField downloadDirText;
	private TextField httpProxyText;
	private TextField invidiousText;
	private ChoiceGroup uiChoice;
	private StringItem dirBtn;
	private ChoiceGroup debugChoice;
	private ChoiceGroup playMethodChoice;
	private TextField downloadBufferText;
	private ChoiceGroup checkUpdatesChoice;
	private ChoiceGroup miscChoice;
	private ChoiceGroup autoStartChoice;
	private ChoiceGroup keyboardChoice;
	private TextField apiProxyText;
	private ChoiceGroup proxyChoice;
	private ChoiceGroup vpbProxyChoice;
	
	private TextField jtdlUrlField;
	private TextField jtdlFormatField;
	private TextField jtdlPasswordField;

	private List dirList;
	private String curDir;
	private int apiProxyIdx;
	
	private List langsList;
	private List inputLangsList;
	private int dir;

	private static final Command dirCmd = new Command("...", Command.ITEM, 1);

	private final static Command dirOpenCmd = new Command(Locale.s(CMD_Open), Command.ITEM, 1);
	private final static Command dirSelectCmd = new Command(Locale.s(CMD_Apply), Command.SCREEN, 2);

	public SettingsForm() {
		super(Locale.s(TITLE_Settings));
		setItemStateListener(this);
		setCommandListener(this);
		addCommand(applyCmd);
		Font titleFont = Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL);
		final int titleLayout = Item.LAYOUT_LEFT;
		StringItem videoLabel = new StringItem(null, " " + Locale.s(SET_Video) + EOL);
		videoLabel.setFont(titleFont);
		StringItem uiLabel = new StringItem(null, " " + Locale.s(SET_Appearance) + EOL);
		uiLabel.setFont(titleFont);
		StringItem netLabel = new StringItem(null, EOL + " " + Locale.s(SET_Network) + EOL);
		netLabel.setFont(titleFont);
		StringItem miscLabel = new StringItem(null, " " + Locale.s(SET_OtherSettings) + EOL);
		miscLabel.setFont(titleFont);
		StringItem inputLabel = new StringItem(null, " " + Locale.s(SET_Input) + EOL);
		inputLabel.setFont(titleFont);
		StringItem jtdlLabel;
		try {
			videoLabel.setLayout(titleLayout);
			uiLabel.setLayout(titleLayout);
			netLabel.setLayout(titleLayout);
			miscLabel.setLayout(titleLayout);
			inputLabel.setLayout(titleLayout);
		} catch (Exception e) {}
//		videoResChoice = new ChoiceGroup(Locale.s(SET_VideoRes), ChoiceGroup.POPUP, VIDEO_QUALITIES, null);
		regionText = new TextField(Locale.s(SET_CountryCode), Settings.region, 3, TextField.ANY);
		playMethodChoice = new ChoiceGroup(Locale.s(SET_PlaybackMethod), ChoiceGroup.POPUP, PLAYBACK_METHODS, null);
		checkUpdatesChoice = new ChoiceGroup(Locale.s(SET_CheckUpdates), ChoiceGroup.POPUP, ON_OFF, null);
		uiChoice = new ChoiceGroup(null, ChoiceGroup.MULTIPLE, APPEARANCE_CHECKS, null);
		proxyChoice = new ChoiceGroup(Locale.s(SET_Proxy), ChoiceGroup.MULTIPLE, PROXY_CHECKS, null);
		miscChoice = new ChoiceGroup(null, ChoiceGroup.MULTIPLE, MISC_CHECKS, null);
		downloadDirText = new TextField(Locale.s(SET_DownloadDir), Settings.downloadDir, 256, TextField.URL);
		dirBtn = new StringItem(null, "...", Item.BUTTON);
		dirBtn.setLayout(Item.LAYOUT_RIGHT);
		dirBtn.setDefaultCommand(dirCmd);
		dirBtn.setItemCommandListener(this);
		invidiousText = new TextField(Locale.s(SET_InvAPI), Settings.inv, 256, TextField.URL);
		httpProxyText = new TextField(Locale.s(SET_StreamProxy), Settings.serverstream, 256, TextField.URL);
		downloadBufferText = new TextField(Locale.s(SET_DownloadBuffer), Integer.toString(Settings.downloadBuffer), 6, TextField.NUMERIC);
		debugChoice = new ChoiceGroup("", ChoiceGroup.MULTIPLE, DEBUG_CHECKS, null);
		autoStartChoice = new ChoiceGroup(Locale.s(SET_AutoStart), ChoiceGroup.POPUP, ON_OFF, null);
		keyboardChoice = new ChoiceGroup(Locale.s(SET_VirtualKeyboard), ChoiceGroup.POPUP, VIRTUAL_KEYBOARDS, null);
		apiProxyText = new TextField(Locale.s(SET_ApiProxy), Settings.apiProxy, 256,
				Settings.useApiProxy ? TextField.URL : TextField.URL | TextField.UNEDITABLE);
		vpbProxyChoice = new ChoiceGroup(Locale.s(SET_PlaybackProxy), ChoiceGroup.POPUP, VPB_PROXY_VARIANTS, null);
		
		if (JTDL) {
			jtdlUrlField = new TextField("JTDL URL", Settings.jtdlUrl != null ? Settings.jtdlUrl : "", 256, TextField.URL);
			jtdlFormatField = new TextField("Format", Settings.jtdlFormat, 32, TextField.ANY);
			jtdlPasswordField = new TextField("Password", Settings.jtdlPassword != null ? Settings.jtdlPassword : "", 64, TextField.ANY);

			jtdlLabel = new StringItem(null, " JTDL " + EOL);
			jtdlLabel.setFont(titleFont);
			try {
				jtdlLabel.setLayout(titleLayout);
			} catch (Exception e) {}
		}
		
		append(videoLabel);
//		append(videoResChoice);
		append(playMethodChoice);
		append(downloadDirText);
		append(dirBtn);
		
		if (JTDL) {
			append(jtdlLabel);
			append(jtdlUrlField);
			append(jtdlFormatField);
			append(jtdlPasswordField);
		}
		
		append(uiLabel);
		append(uiChoice);
		append(regionText);
		
		StringItem langBtn = new StringItem(null, Locale.s(SET_ChooseLanguage), StringItem.BUTTON);
		langBtn.addCommand(langCmd);
		langBtn.setDefaultCommand(langCmd);
		langBtn.setItemCommandListener(this);
		langBtn.setLayout(Item.LAYOUT_EXPAND);
		
		append(langBtn);
		append(spacer());
		
		append(netLabel);
		append(invidiousText);
		append(proxyChoice);
		append(vpbProxyChoice);
		apiProxyIdx = append(apiProxyText);
		append(httpProxyText);
		append(spacer());
		
		append(miscLabel);
		append(miscChoice);
		append(checkUpdatesChoice);
		append(autoStartChoice);
		append(downloadBufferText);
		append(spacer());
		
		StringItem s = new StringItem(null, Locale.s(SET_j2mekeyboardSettings)+"\n");
		s.setFont(Font.getFont(0, 0, 8));
		StringItem inputLangsBtn = new StringItem(null, Locale.s(SET_InputLanguages), StringItem.BUTTON);
		inputLangsBtn.addCommand(inputLangsCmd);
		inputLangsBtn.setDefaultCommand(inputLangsCmd);
		inputLangsBtn.setItemCommandListener(this);
		inputLangsBtn.setLayout(Item.LAYOUT_EXPAND);
		StringItem subsImportBtn = new StringItem(null, Locale.s(SET_ImportSubscriptions), StringItem.BUTTON);
		subsImportBtn.addCommand(subsImportCmd);
		subsImportBtn.setDefaultCommand(subsImportCmd);
		subsImportBtn.setItemCommandListener(this);
		subsImportBtn.setLayout(Item.LAYOUT_EXPAND);
		StringItem subsExportBtn = new StringItem(null, Locale.s(SET_ExportSubscriptions), StringItem.BUTTON);
		subsExportBtn.addCommand(subsExportCmd);
		subsExportBtn.setDefaultCommand(subsExportCmd);
		subsExportBtn.setItemCommandListener(this);
		subsExportBtn.setLayout(Item.LAYOUT_EXPAND);
		StringItem resetBtn = new StringItem(null, Locale.s(SET_Reset), StringItem.BUTTON);
		resetBtn.addCommand(resetCmd);
		resetBtn.setDefaultCommand(resetCmd);
		resetBtn.setItemCommandListener(this);
		resetBtn.setLayout(Item.LAYOUT_EXPAND);
		
		append(inputLabel);
		append(keyboardChoice);
		append(s);
		append(inputLangsBtn);
		append(spacer());
		
		append(subsImportBtn);
		append(subsExportBtn);
		append(debugChoice);
		append(resetBtn);
	}
	
	private Item spacer() {
		Spacer spacer = new Spacer(10, 10);
		spacer.setLayout(Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER);
		return spacer;
	}

	public void show() {
		uiChoice.setSelectedIndex(0, Settings.videoPreviews);
		uiChoice.setSelectedIndex(1, Settings.amoled);
		uiChoice.setSelectedIndex(2, Settings.smallPreviews);
		uiChoice.setSelectedIndex(3, Settings.fullScreen);
		uiChoice.setSelectedIndex(4, Settings.channelBanner);
		uiChoice.setSelectedIndex(5, Settings.searchSuggestions);
		proxyChoice.setSelectedIndex(0, Settings.useApiProxy);
		proxyChoice.setSelectedIndex(1, Settings.httpStream);
		debugChoice.setSelectedIndex(0, Settings.renderDebug);
		debugChoice.setSelectedIndex(1, Settings.asyncLoading);
		debugChoice.setSelectedIndex(2, Settings.fastScrolling);
		debugChoice.setSelectedIndex(3, Settings.lazyLoad);
		miscChoice.setSelectedIndex(0, Settings.rmsPreviews);
		miscChoice.setSelectedIndex(1, Settings.powerSaving);
		try {
			playMethodChoice.setSelectedIndex(Settings.watchMethod, true);
		} catch (IndexOutOfBoundsException e) {
			playMethodChoice.setSelectedIndex(Settings.watchMethod = 0, true);
		}
		checkUpdatesChoice.setSelectedIndex(Settings.checkUpdates ? 0 : 1, true);
		autoStartChoice.setSelectedIndex(Settings.autoStart ? 0 : 1, true);
		keyboardChoice.setSelectedIndex(Settings.keyboard, true);
		try {
			vpbProxyChoice.setSelectedIndex(Settings.playbackProxyVariant, true);
		} catch (IndexOutOfBoundsException e) {
			playMethodChoice.setSelectedIndex(Settings.playbackProxyVariant = 0, true);
		}
		setResolution();
	}
	
	private void setResolution() {
//		videoResChoice.setSelectedIndex(Settings.videoRes != null && Settings.videoRes.equals("720p") ? 1 : 0, true);
//		else if(Settings.videoRes.equals("_audiohigh")) {
//			videoResChoice.setSelectedIndex(3, true);
//		} else if(Settings.videoRes.equals("_240p")) {
//			videoResChoice.setSelectedIndex(4, true);
//		}
	}
	
	private void applySettings() {
		try {
//			Settings.videoRes = videoResChoice.getSelectedIndex() == 1 ? "720p" : "360p";
			Settings.region = regionText.getString().trim().toUpperCase();
			String dir = downloadDirText.getString();
			//dir = Util.replace(dir, "/", dirsep);
			dir = Util.replace(dir, "\\", PATH_SEPARATOR);
			while (dir.endsWith(PATH_SEPARATOR)) {
				dir = dir.substring(0, dir.length() - 1);
			}
			Settings.downloadDir = dir;
			boolean[] b = new boolean[uiChoice.size()];
			uiChoice.getSelectedFlags(b);
			Settings.videoPreviews = b[0];
			Settings.amoled = b[1];
			Settings.smallPreviews = b[2];
			Settings.fullScreen = b[3];
			Settings.channelBanner = b[4];
			Settings.searchSuggestions = b[5];
			Settings.useApiProxy = proxyChoice.isSelected(0);
			Settings.httpStream = proxyChoice.isSelected(1);
			miscChoice.getSelectedFlags(b = new boolean[miscChoice.size()]);
			Settings.rmsPreviews = b[0];
			Settings.powerSaving = b[1];
			Settings.serverstream = httpProxyText.getString();
			String inv = invidiousText.getString();
			if(inv.length() <= 2) {
				inv = iteroni;
			} else {
				if(inv.indexOf(':') == -1) {
					inv = "http://" + inv;
				}
				if(!inv.endsWith("/")) {
					inv += "/";
				}
			}
			Settings.inv = inv;
			Settings.watchMethod = playMethodChoice.getSelectedIndex();
			Settings.downloadBuffer = Integer.parseInt(downloadBufferText.getString());
			Settings.checkUpdates = checkUpdatesChoice.isSelected(0);
			debugChoice.getSelectedFlags(b = new boolean[debugChoice.size()]);
			Settings.renderDebug = b[0];
			Settings.asyncLoading = b[1];
			Settings.fastScrolling = b[2];
			Settings.lazyLoad = b[3];
			Settings.autoStart = autoStartChoice.isSelected(0);
			Settings.keyboard = keyboardChoice.getSelectedIndex();
			String apiProxy = apiProxyText.getString();
			if(apiProxy.length() <= 2) {
				apiProxy = invproxy;
			} else if(apiProxy.indexOf(':') == -1) {
				apiProxy = "http://" + inv;
			}
			Settings.apiProxy = apiProxy;
			Settings.playbackProxyVariant = vpbProxyChoice.getSelectedIndex();
			
			Settings.jtdlUrl = jtdlUrlField.getString();
			Settings.jtdlFormat = jtdlFormatField.getString();
			Settings.jtdlPassword = jtdlPasswordField.getString();
			
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
		if(dir != 1) {
			dirList.addCommand(dirSelectCmd);
			dirList.append("- " + Locale.s(CMD_Select), null);
		}
		try {
			FileConnection fc = (FileConnection) Connector.open("file:///" + f);
			Enumeration list = fc.list();
			while(list.hasMoreElements()) {
				String s = (String) list.nextElement();
				if(s.endsWith("/")) {
					dirList.append(s.substring(0, s.length() - 1), null);
				} else if(s.equalsIgnoreCase("jtsubscriptions.json")) {
					dirList.append(s, null);
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
						if(dir == 0) {
							downloadDirText.setString(f);
						} else if(dir == 2) {
							FileConnection fc = null;
							OutputStream o = null;
							try {
								fc = (FileConnection) Connector.open("file:///" + f + "jtsubscriptions.json", 3);
								if (fc.exists())
									fc.delete();
								fc.create();
								o = fc.openDataOutputStream();
								o.write(LocalStorage.exportSubscriptionsBytes());
								o.flush();
							} catch (Exception e) {
							} finally {
								try {
									if (o != null)
										o.close();
									if (fc != null)
										fc.close();
								} catch (Exception e) {
								}
							}
						}
						curDir = null;
						AppUI.inst.display(this);
						return;
					}
					f += is;
					if(dir == 1 && is.equalsIgnoreCase("jtsubscriptions.json")) {
						FileConnection fc = null;
						InputStream in = null;
						try {
							fc = (FileConnection) Connector.open("file:///" + f);
							in = fc.openInputStream();
					        LocalStorage.importSubscriptions(Util.readBytes(in, (int) fc.fileSize(), 1024, 2048));
						} catch (Exception e) {
						} finally {
							try {
								if (in != null)
									in.close();
								if (fc != null)
									fc.close();
							} catch (Exception e) {
							}
						}
						curDir = null;
						AppUI.inst.display(this);
				        return;
					}
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
				if(langsList.getSelectedIndex() == -1) {
					Settings.customLocale = Locale.lang;
				} else {
					Settings.customLocale = ((String[])Settings.langsList.elementAt(langsList.getSelectedIndex()))[0];
				}
				if(c != List.SELECT_COMMAND) {
					AppUI.inst.display(this);
				}
				return;
			}
			if(d == inputLangsList) {
				if(c == backCmd) {
					Vector v = new Vector();
					for(int i = 0; i < Settings.supportedInputLanguages.length; i++) {
						String s = Settings.supportedInputLanguages[i];
						String l = s.substring(s.lastIndexOf('[')+1,s.lastIndexOf(']'));
						if(inputLangsList.isSelected(i)) {
							v.addElement(l);
						}
					}
					String[] res = new String[v.size()];
					for(int i = 0; i < v.size(); i++) {
						res[i] = (String) v.elementAt(i);
					}
					Settings.inputLanguages = res;
					AppUI.inst.display(this);
				}
				return;
			}
			applySettings();
			AppUI.inst.display(null);
			UIScreen screen = AppUI.inst.current;
			if(screen != null) {
				screen.relayout();
			}
			try {
				AppUI.inst.resetFullScreenMode();
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}
	}

	public void commandAction(Command c, Item item) {
		if(c == resetCmd) {
			LocalStorage.clearCache();
			LocalStorage.clearAllData();
			Settings.removeConfig();
			App.midlet.notifyDestroyed();
			return;
		}
		if(c == subsImportCmd) {
			dir = 1;
			dir();
			return;
		}
		if(c == subsExportCmd) {
			dir = 2;
			dir();
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
				int s = 0;
				while(i < Settings.langsList.size()) {
					String[] a = (String[]) Settings.langsList.elementAt(i++);
					int x = langsList.append((a[2].length() > 0 ? a[2] + (a[1].equalsIgnoreCase(a[2]) ? "" : ("ar".equals(a[0]) ? " " + a[1] : " (" + a[1] + ")")) : a[1]) + "\n" + a[3], null);
					if(a[0].equalsIgnoreCase(Settings.customLocale)) {
						s = x;
					}
				}
				langsList.setSelectedIndex(s, true);
			}
			AppUI.inst.display(langsList);
			return;
		}
		if(c == dirCmd) {
			dir = 0;
			dir();
			return;
		}
		if(c == inputLangsCmd) {
			if(inputLangsList == null) {
				inputLangsList = new List("", List.MULTIPLE);
				inputLangsList.addCommand(backCmd);
				inputLangsList.addCommand(List.SELECT_COMMAND);
				inputLangsList.setSelectCommand(List.SELECT_COMMAND);
				inputLangsList.setCommandListener(this);
				for(int i = 0; i < Settings.supportedInputLanguages.length; i++) {
					String s = Settings.supportedInputLanguages[i];
					String l = s.substring(s.lastIndexOf('[')+1,s.lastIndexOf(']'));
					int x = inputLangsList.append(s, null);
					for(int j = 0; j < Settings.inputLanguages.length; j++) {
						if(Settings.inputLanguages[j].equals(l)) {
							inputLangsList.setSelectedIndex(x, true);
							break;
						}
					}
				}
			}
			AppUI.inst.display(inputLangsList);
		}
	}
	
	private void dir() {
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

	public void itemStateChanged(Item item) {
		if(item == proxyChoice) {
			boolean tmp = Settings.useApiProxy;
			Settings.useApiProxy = proxyChoice.isSelected(0);
			Settings.httpStream = proxyChoice.isSelected(1);
			if(Settings.useApiProxy != tmp) {
				apiProxyText = new TextField(Locale.s(SET_ApiProxy), Settings.apiProxy, 256,
						Settings.useApiProxy ? TextField.URL : TextField.URL | TextField.UNEDITABLE);
				set(apiProxyIdx, apiProxyText);
			}
		}
		if(item == playMethodChoice) {
			Settings.watchMethod = playMethodChoice.getSelectedIndex();
		}
		if(item == keyboardChoice) {
			Settings.keyboard = keyboardChoice.getSelectedIndex();
		}
	}

}
