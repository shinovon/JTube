package ui;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;

import App;
import Constants;
import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONObject;

public class Settings extends Form implements Constants, CommandListener {
	
	private ChoiceGroup videoResChoice;
	private TextField regionText;
	private TextField downloadDirText;
	private TextField serverText;
	private TextField httpProxyText;
	private ChoiceGroup checksChoice;

	public Settings() {
		super("Settings");
		addCommand(backCmd);
		setCommandListener(this);
		videoResChoice = new ChoiceGroup("Preferred video quality", ChoiceGroup.EXCLUSIVE, VIDEO_QUALITIES, null);
		append(videoResChoice);
		regionText = new TextField("Country code (ISO 3166)", App.region, 3, TextField.ANY);
		append(regionText);
		checksChoice = new ChoiceGroup("", ChoiceGroup.MULTIPLE, SETTINGS_CHECKS, null);
		append(checksChoice);
		downloadDirText = new TextField("Download directory", App.downloadDir, 256, TextField.ANY);
		append(downloadDirText);
		serverText = new TextField("Get Links PHP server", App.servergetlinks, 256, TextField.URL);
		append(serverText);
		httpProxyText = new TextField("Stream proxy server", App.serverhttp, 256, TextField.URL);
		append(httpProxyText);
	}
	
	public void show() {
		checksChoice.setSelectedIndex(0, App.videoPreviews);
		checksChoice.setSelectedIndex(1, App.searchChannels);
		checksChoice.setSelectedIndex(2, App.rememberSearch);
		checksChoice.setSelectedIndex(3, App.httpStream);
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
		// Defaults
		App.videoRes = "360p";
		String downloadDir = System.getProperty("fileconn.dir.videos");
		if(downloadDir == null)
			downloadDir = System.getProperty("fileconn.dir.photos");
		if(downloadDir == null)
			downloadDir = "C:/";
		else if(downloadDir.startsWith("file:///"))
			downloadDir = downloadDir.substring("file:///".length());
		App.downloadDir = downloadDir;
		boolean lowEnd = isLowEndDevice();
		if(lowEnd) {
			App.httpStream = true;
			App.rememberSearch = false;
			App.searchChannels = false;
			App.videoPreviews = false;
		} else {
			if(!isSymbian3()) App.httpStream = false;
			else App.httpStream = true;
			App.rememberSearch = true;
			App.searchChannels = true;
			App.videoPreviews = true;
		}
		try {
			RecordStore r = RecordStore.openRecordStore(CONFIG_RECORD_NAME, false);
			JSONObject j = JSON.getObject(new String(r.getRecord(1), "UTF-8"));
			r.closeRecordStore();
			if(j.has("videoRes"))
				App.videoRes = j.getString("videoRes");
			if(j.has("region"))
				App.region = j.getString("region");
			if(j.has("downloadDir"))
				App.downloadDir = j.getString("downloadDir");
			if(j.has("servergetlinks"))
				App.servergetlinks = j.getString("servergetlinks");
			if(j.has("videoPreviews"))
				App.videoPreviews = j.getBoolean("videoPreviews");
			if(j.has("searchChannels"))
				App.searchChannels = j.getBoolean("searchChannels");
			if(j.has("rememberSearch"))
				App.rememberSearch = j.getBoolean("rememberSearch");
			if(j.has("httpStream"))
				App.httpStream = j.getBoolean("httpStream");
			if(j.has("serverhttp"))
				App.serverhttp = j.getString("serverhttp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveConfig() {
		if(videoResChoice.getSelectedIndex() == 0) {
			App.videoRes = "144p";
		} else if(videoResChoice.getSelectedIndex() == 1) {
			App.videoRes = "360p";
		} else if(videoResChoice.getSelectedIndex() == 2) {
			App.videoRes = "720p";
		}
		App.region = regionText.getString();
		App.downloadDir = downloadDirText.getString();
		App.servergetlinks = serverText.getString();
		App.videoPreviews = checksChoice.isSelected(0);
		App.searchChannels = checksChoice.isSelected(1);
		App.rememberSearch = checksChoice.isSelected(2);
		App.httpStream = checksChoice.isSelected(3);
		App.serverhttp = httpProxyText.getString();
		try {
			RecordStore.deleteRecordStore(CONFIG_RECORD_NAME);
		} catch (Exception e) {
		}
		try {
			RecordStore r = RecordStore.openRecordStore(CONFIG_RECORD_NAME, true);
			JSONObject j = new JSONObject();
			j.put("v", "v1");
			j.put("videoRes", App.videoRes);
			j.put("region", App.region);
			j.put("downloadDir", App.downloadDir);
			j.put("servergetlinks", App.servergetlinks);
			j.put("videoPreviews", new Boolean(App.videoPreviews));
			j.put("searchChannels", new Boolean(App.searchChannels));
			j.put("rememberSearch", new Boolean(App.rememberSearch));
			j.put("httpStream", new Boolean(App.httpStream));
			j.put("serverhttp", App.serverhttp);
			byte[] b = j.build().getBytes("UTF-8");
			
			r.addRecord(b, 0, b.length);
			r.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void commandAction(Command c, Displayable arg1) {
		if(c == backCmd) {
			saveConfig();
			App.display(null);
		}
	}
	
	private static boolean isS60PlatformVersion(String v) {
		return platform.indexOf("platform_version=" + v) > -1;
	}
	
	public static boolean isLowEndDevice() {
		return isNotS60() && (startMemory == S40_MEM || App.width < 240);
	}
	
	public static boolean isNotS60() {
		return platform.indexOf("S60") < 0;
	}
	
	public static boolean isSymbian3() {
		return !isNotS60() && (isS60PlatformVersion("5.1") || isS60PlatformVersion("5.2") || isS60PlatformVersion("5.3") || isS60PlatformVersion("5.4") || isS60PlatformVersion("5.5"));
	}
	
	public static boolean isSymbian94() {
		return !isNotS60() && isS60PlatformVersion("5.0");
	}
	
	public static boolean isS603rd() {
		return isS60PlatformVersion("3") || platform.startsWith("NokiaN73") || platform.startsWith("NokiaN95") || platform.startsWith("NokiaE90") || 
				platform.startsWith("NokiaN93") || platform.startsWith("NokiaN82") || platform.startsWith("NokiaE71") || 
				platform.startsWith("NokiaE70") || platform.startsWith("NokiaN80") || platform.startsWith("NokiaE63") || 
				platform.startsWith("NokiaE66") || platform.startsWith("NokiaE51") || platform.startsWith("NokiaE50") || 
				platform.startsWith("NokiaE65") || platform.startsWith("NokiaE61") || platform.startsWith("NokiaE60");
	}

}
