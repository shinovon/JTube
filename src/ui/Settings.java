package ui;

import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;

import App;
import Util;
import Errors;
import Locale;
import Constants;
import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONObject;
import cc.nnproject.utils.PlatformUtils;

public class Settings extends Form implements Constants, CommandListener, ItemCommandListener {

	//private static final Command dirCmd = new Command("...", Command.ITEM, 1);
	
	private ChoiceGroup videoResChoice;
	private TextField regionText;
	private TextField downloadDirText;
	private TextField httpProxyText;
	private ChoiceGroup checksChoice;
	private TextField invidiousText;
	private TextField imgProxyText;
	private ChoiceGroup uiChoice;
	//private StringItem dirBtn;

	public Settings() {
		super(Locale.s(TITLE_Settings));
		setCommandListener(this);
		addCommand(applyCmd);
		videoResChoice = new ChoiceGroup(Locale.s(SET_VideoRes), ChoiceGroup.EXCLUSIVE, VIDEO_QUALITIES, null);
		append(videoResChoice);
		regionText = new TextField(Locale.s(SET_CountryCode), App.region, 3, TextField.ANY);
		append(regionText);
		uiChoice = new ChoiceGroup(Locale.s(SET_Appearance), ChoiceGroup.MULTIPLE, APPEARANCE_CHECKS, null);
		append(uiChoice);
		checksChoice = new ChoiceGroup(Locale.s(SET_OtherSettings), ChoiceGroup.MULTIPLE, SETTINGS_CHECKS, null);
		append(checksChoice);
		downloadDirText = new TextField(Locale.s(SET_DownloadDir), App.downloadDir, 256, TextField.URL);
		append(downloadDirText);
		//dirBtn = new StringItem(null, "...", Item.BUTTON);
		//dirBtn.setLayout(Item.LAYOUT_2 | Item.LAYOUT_RIGHT);
		//dirBtn.setDefaultCommand(dirCmd);
		//dirBtn.setItemCommandListener(this);
		//append(dirBtn);
		invidiousText = new TextField(Locale.s(SET_InvAPI), App.inv, 256, TextField.URL);
		append(invidiousText);
		httpProxyText = new TextField(Locale.s(SET_StreamProxy), App.serverstream, 256, TextField.URL);
		append(httpProxyText);
		append("(Used only if http streaming is on)\n");
		imgProxyText = new TextField(Locale.s(SET_ImagesProxy), App.imgproxy, 256, TextField.URL);
		append(imgProxyText);
		append("(Leave images proxy empty if HTTPS is supported)\n");
	}
	
	public void show() {
		uiChoice.setSelectedIndex(0, App.customItems);
		uiChoice.setSelectedIndex(1, App.videoPreviews);
		uiChoice.setSelectedIndex(2, App.searchChannels);
		checksChoice.setSelectedIndex(0, App.rememberSearch);
		checksChoice.setSelectedIndex(1, App.httpStream);
		checksChoice.setSelectedIndex(2, App.rmsPreviews);
		//checksChoice.setSelectedIndex(4, App.apiProxy);
		if(App.videoRes == null) {
			videoResChoice.setSelectedIndex(1, true);
		} else if(App.videoRes.equals("144p")) {
			videoResChoice.setSelectedIndex(0, true);
		} else if(App.videoRes.equals("360p")) {
			videoResChoice.setSelectedIndex(1, true);
		} else if(App.videoRes.equals("720p")) {
			videoResChoice.setSelectedIndex(2, true);
		}
	}
	

	public static void loadConfig() {
		/*
		String s = System.getProperty("kemulator.libvlc.supported");
		if(s != null && s.equals("true")) {
			App.watchMethod = 1;
		}
		*/
		RecordStore r = null;
		try {
			r = RecordStore.openRecordStore(CONFIG_RECORD_NAME, false);
		} catch (Exception e) {
		}
		if(r == null) {
			// Defaults

			if(PlatformUtils.isJ2ML()) {
				App.videoPreviews = true;
				App.customItems = true;
				App.httpStream = false;
				App.videoRes = "360p";
				App.downloadDir = "C:/";
			} else {
				if(!PlatformUtils.isS40()) {
					Enumeration roots = FileSystemRegistry.listRoots();
					String root = "";
					while(roots.hasMoreElements()) {
						String s = (String) roots.nextElement();
						if(s.startsWith("file:///")) s = s.substring("file:///".length());
						if(s.startsWith("SDCard")) {
							root = s;
							break;
						}
						if(s.startsWith("F:")) {
							root = s;
							break;
						}
						if(s.startsWith("E:")) {
							root = s;
						}
					}
					if(!root.endsWith("/")) root += "/";
					App.downloadDir = root;
					try {
						FileConnection fc = (FileConnection) Connector.open("file:///" + root + "videos/");
						if(fc.exists()) {
							App.downloadDir = root + "videos/";
						}
						fc.close();
					} catch (Exception e) {
					}
				} else {
					String downloadDir = System.getProperty("fileconn.dir.videos");
					if(downloadDir == null)
						downloadDir = System.getProperty("fileconn.dir.photos");
					if(downloadDir == null)
						downloadDir = "C:/";
					else if(downloadDir.startsWith("file:///"))
						downloadDir = downloadDir.substring("file:///".length());
					App.downloadDir = downloadDir;
				}
				boolean lowEnd = isLowEndDevice();
				if(lowEnd) {
					App.httpStream = true;
					App.rememberSearch = false;
					App.searchChannels = true;
					App.asyncLoading = false;
					App.videoPreviews = false;
				} else {
					if(PlatformUtils.isNotS60() && !PlatformUtils.isS603rd()) {
						App.httpStream = true;
						App.asyncLoading = false;
					} else {
						App.asyncLoading = true;
					}
					if(PlatformUtils.isSymbianTouch()) {
						App.customItems = true;
					}
					App.rememberSearch = true;
					App.searchChannels = true;
					App.videoPreviews = true;
				}
				if(PlatformUtils.isAsha()) {
					App.videoPreviews = true;
					App.customItems = true;
				} else if(PlatformUtils.isS40() || (PlatformUtils.isNotS60() && !PlatformUtils.isS603rd() && PlatformUtils.startMemory > 512 * 1024 && PlatformUtils.startMemory < 2024 * 1024)) {
					App.videoPreviews = true;
					App.customItems = true;
					App.rmsPreviews = true;
				}
				int min = Math.min(App.width, App.height);
				// Symbian 9.4 can't handle H.264/AVC
				if(min < 360 || PlatformUtils.isSymbian94()) {
					App.videoRes = "144p";
				} else {
					App.videoRes = "360p";
				}
			}
		} else {
			try {
				JSONObject j = JSON.getObject(new String(r.getRecord(1), "UTF-8"));
				r.closeRecordStore();
				if(j.has("videoRes"))
					App.videoRes = j.getString("videoRes");
				if(j.has("region"))
					App.region = j.getString("region");
				if(j.has("downloadDir"))
					App.downloadDir = j.getString("downloadDir");
				if(j.has("videoPreviews"))
					App.videoPreviews = j.getBoolean("videoPreviews");
				if(j.has("searchChannels"))
					App.searchChannels = j.getBoolean("searchChannels");
				if(j.has("rememberSearch"))
					App.rememberSearch = j.getBoolean("rememberSearch");
				if(j.has("httpStream"))
					App.httpStream = j.getBoolean("httpStream");
				if(j.has("serverstream"))
					App.serverstream = j.getString("serverstream");
				if(j.has("inv"))
					App.inv = j.getString("inv");
				if(j.has("customItems"))
					App.customItems = j.getBoolean("customItems");
				if(j.has("imgProxy"))
					App.imgproxy = j.getString("imgProxy");
				if(j.has("startScreen"))
					App.startScreen = j.getInt("startScreen");
				if(j.has("rmsPreviews"))
					App.rmsPreviews = j.getBoolean("rmsPreviews");
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void applySettings() {
		try {
			if(videoResChoice.getSelectedIndex() == 0) {
				App.videoRes = "144p";
			} else if(videoResChoice.getSelectedIndex() == 1) {
				App.videoRes = "360p";
			} else if(videoResChoice.getSelectedIndex() == 2) {
				App.videoRes = "720p";
			}
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
			App.rememberSearch = s[0];
			App.httpStream = s[1];
			App.rmsPreviews = s[2];
			App.serverstream = httpProxyText.getString();
			App.inv = invidiousText.getString();
			App.imgproxy = imgProxyText.getString();
			saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
			App.error(this, Errors.Settings_apply, e.toString());
		}
	}
	
	public static void saveConfig() {
		try {
			RecordStore.deleteRecordStore(CONFIG_RECORD_NAME);
		} catch (Throwable e) {
		}
		try {
			RecordStore r = RecordStore.openRecordStore(CONFIG_RECORD_NAME, true);
			JSONObject j = new JSONObject();
			j.put("v", "\"v1\"");
			j.put("videoRes", "\"" + App.videoRes + "\"");
			j.put("region", "\"" + App.region + "\"");
			j.put("downloadDir", "\"" + App.downloadDir + "\"");
			j.put("videoPreviews", new Boolean(App.videoPreviews));
			j.put("searchChannels", new Boolean(App.searchChannels));
			j.put("rememberSearch", new Boolean(App.rememberSearch));
			j.put("httpStream", new Boolean(App.httpStream));
			j.put("serverstream", "\"" + App.serverstream + "\"");
			j.put("inv", "\"" + App.inv + "\"");
			j.put("imgProxy", "\"" + App.imgproxy + "\"");
			j.put("startScreen", new Integer(App.startScreen));
			j.put("customItems", new Boolean(App.customItems));
			j.put("rmsPreviews", new Boolean(App.rmsPreviews));
			byte[] b = j.build().getBytes("UTF-8");
			
			r.addRecord(b, 0, b.length);
			r.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commandAction(Command c, Displayable arg1) {
		applySettings();
		App.display(null);
	}
	
	public static boolean isLowEndDevice() {
		return PlatformUtils.isNotS60() && !PlatformUtils.isS603rd() && (PlatformUtils.isS40() || App.width < 240 || PlatformUtils.startMemory < 2048 * 1024);
	}

	public void commandAction(Command c, Item item) {
		
	}

}
