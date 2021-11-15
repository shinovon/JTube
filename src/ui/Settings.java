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
import cc.nnproject.utils.PlatformUtils;

public class Settings extends Form implements Constants, CommandListener {
	
	private ChoiceGroup videoResChoice;
	private TextField regionText;
	private TextField downloadDirText;
	private TextField httpProxyText;
	private ChoiceGroup checksChoice;
	private TextField invidiousText;

	public Settings() {
		super("Settings");
		setCommandListener(this);
		addCommand(applyCmd);
		videoResChoice = new ChoiceGroup("Preferred video quality", ChoiceGroup.EXCLUSIVE, VIDEO_QUALITIES, null);
		append(videoResChoice);
		regionText = new TextField("Country code (ISO 3166)", App.region, 3, TextField.ANY);
		append(regionText);
		checksChoice = new ChoiceGroup("", ChoiceGroup.MULTIPLE, SETTINGS_CHECKS, null);
		append(checksChoice);
		invidiousText = new TextField("Invidious server", App.inv, 256, TextField.URL);
		append(invidiousText);
		downloadDirText = new TextField("Download directory", App.downloadDir, 256, TextField.ANY);
		append(downloadDirText);
		httpProxyText = new TextField("Stream proxy server", App.serverstream, 256, TextField.URL);
		append(httpProxyText);
	}
	
	public void show() {
		checksChoice.setSelectedIndex(0, App.videoPreviews);
		checksChoice.setSelectedIndex(1, App.searchChannels);
		checksChoice.setSelectedIndex(2, App.rememberSearch);
		checksChoice.setSelectedIndex(3, App.httpStream);
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
		// Defaults
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
			App.searchChannels = true;
			App.videoPreviews = false;
			//if(isS40()) App.apiProxy = true;
		} else {
			if(PlatformUtils.isNotS60() && !PlatformUtils.isS603rd()) {
				App.httpStream = true;
				App.asyncLoading = true;
			}
			App.rememberSearch = true;
			App.searchChannels = true;
			App.videoPreviews = true;
		}

		int min = Math.min(App.width, App.height);
		if(min < 360) {
			App.videoRes = "144p";
		} else {
			App.videoRes = "360p";
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
			//if(j.has("apiProxy"))
			//	App.apiProxy = j.getBoolean("apiProxy");
			//if(j.has("serverproxy"))
			//	App.serverproxy = j.getString("serverproxy");
			return;
		} catch (Exception e) {
			e.printStackTrace();
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
				App.downloadDir = downloadDirText.getString();
				boolean[] s = new boolean[checksChoice.size()];
				checksChoice.getSelectedFlags(s);
				App.videoPreviews = s[0];
				App.searchChannels = s[1];
				App.rememberSearch = s[2];
				App.httpStream = s[3];
				//App.apiProxy = s[4];
				App.serverstream = httpProxyText.getString();
				App.inv = invidiousText.getString();
				saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
			App.msg(e.toString());
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
		return PlatformUtils.isNotS60() && (PlatformUtils.isS40() || App.width < 240);
	}

}
