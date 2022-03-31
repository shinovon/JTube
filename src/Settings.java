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
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.rms.RecordStore;

import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONObject;
import cc.nnproject.utils.PlatformUtils;

public class Settings implements Constants {

	public static Vector rootsVector;
	
	public static void getRoots() {
		if(rootsVector != null) return;
		rootsVector = new Vector();
		Enumeration roots = FileSystemRegistry.listRoots();
		while(roots.hasMoreElements()) {
			String s = (String) roots.nextElement();
			if(s.startsWith("file:///")) s = s.substring("file:///".length());
			rootsVector.addElement(s);
		}
	}
	

	public static void loadConfig() {
		App.customLocale = Locale.l;
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
				boolean s40 = PlatformUtils.isS40();
				if(!s40) {
					getRoots();
					String root = "";
					for(int i = 0; i < rootsVector.size(); i++) {
						String s = (String) rootsVector.elementAt(i);
						if(s.startsWith("file:///")) s = s.substring("file:///".length());
						if(s.startsWith("Video")) {
							root = s;
							break;
						}
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
				App.watchMethod = PlatformUtils.isSymbianTouch() || PlatformUtils.isBada() || PlatformUtils.isS603rd() ? 1 : 0;
				boolean lowEnd = isLowEndDevice();
				if(lowEnd) {
					App.httpStream = true;
					App.rememberSearch = false;
					App.searchChannels = true;
					App.asyncLoading = false;
					App.videoPreviews = false;
				} else {
					if((PlatformUtils.isNotS60() && !PlatformUtils.isS603rd()) || PlatformUtils.isBada()) {
						App.httpStream = true;
						App.asyncLoading = false;
						App.downloadBuffer = 4 * 1024;
					} else {
						App.downloadBuffer = 32 * 1024;
						App.asyncLoading = true;
					}
					if(PlatformUtils.isSymbian3Based() || PlatformUtils.isBada()) {
						App.customItems = true;
					}
					App.rememberSearch = true;
					App.searchChannels = true;
					App.searchPlaylists = true;
					App.videoPreviews = true;
				}
				if(PlatformUtils.isAsha()) {
					App.videoPreviews = true;
					App.customItems = true;
				} else if(s40 /*|| (PlatformUtils.isNotS60() && !PlatformUtils.isS603rd() && PlatformUtils.startMemory > 512 * 1024 && PlatformUtils.startMemory < 2024 * 1024)*/) {
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
				if(j.has("serverstream")) {
					App.serverstream = j.getString("serverstream");
					// replace old proxy
					if(App.serverstream.endsWith("/stream.php")) {
						App.serverstream = glype;
					}
				}
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
				if(j.has("customLocale"))
					App.customLocale = j.getString("customLocale");
				if(j.has("searchPlaylists"))
					App.searchPlaylists = j.getBoolean("searchPlaylists");
				if(j.has("debugMemory"))
					App.debugMemory = j.getBoolean("debugMemory");
				if(j.has("watchMethod"))
					App.watchMethod = j.getInt("watchMethod");
				if(j.has("asyncLoading"))
					App.asyncLoading = j.getBoolean("asyncLoading");
				if(j.has("downloadBuffer"))
					App.downloadBuffer = j.getInt("downloadBuffer");
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			j.put("customLocale", "\"" + App.customLocale + "\"");
			j.put("searchPlaylists", new Boolean(App.searchPlaylists));
			j.put("debugMemory", new Boolean(App.debugMemory));
			j.put("watchMethod", new Integer(App.watchMethod));
			j.put("asyncLoading", new Boolean(App.asyncLoading));
			j.put("downloadBuffer", new Integer(App.downloadBuffer));
			byte[] b = j.build().getBytes("UTF-8");
			
			r.addRecord(b, 0, b.length);
			r.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isLowEndDevice() {
		return PlatformUtils.isNotS60() && 
				!PlatformUtils.isS603rd() && 
				!PlatformUtils.isS603rd() && 
				(PlatformUtils.isS40() || 
						App.width < 240 || 
						PlatformUtils.startMemory < 2048 * 1024 ||
						(!PlatformUtils.isBada() && PlatformUtils.isSamsung()));
	}

}
