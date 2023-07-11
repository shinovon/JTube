package jtube;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.rms.RecordStore;

import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import cc.nnproject.keyboard.Keyboard;
import cc.nnproject.utils.PlatformUtils;
import jtube.ui.Locale;
import midletintegration.MIDletIntegration;

public class Settings implements Constants {
	
	// Settings
	public static String videoRes;
	public static String region;
	public static int watchMethod = 1;
	public static String downloadDir;
	public static String serverstream = glype;
	public static boolean videoPreviews;
	public static boolean searchChannels;
	public static boolean rememberSearch;
	public static boolean httpStream;
	public static int startScreen; // 0 - Trends 1 - Popular
	public static String inv = iteroni;
	public static boolean rmsPreviews;
	public static boolean searchPlaylists;
	public static String customLocale;
	public static int downloadBuffer = 1024;
	public static boolean asyncLoading;
	public static boolean checkUpdates = true;
	public static boolean iteroniPlaybackProxy = true;
	public static boolean renderDebug;
	public static boolean amoled;
	public static boolean fastScrolling;
	public static boolean smallPreviews = true;
	public static boolean searchBar = true;
	public static boolean autoStart;
	public static boolean fullScreen = true;
	public static boolean channelBanner = true;
	public static boolean searchSuggestions = true;
	public static String[] inputLanguages = new String[] {"en", "ru"};
	public static String[] supportedInputLanguages = new String[0];
	public static int keyboard = 0;
	public static String apiProxy = invproxy;
	public static boolean useApiProxy;
	
	public static Vector rootsList;
	public static Vector langsList;
	
	public static void getRoots() {
		if(rootsList != null) return;
		rootsList = new Vector();
		try {
			Enumeration roots = FileSystemRegistry.listRoots();
			while(roots.hasMoreElements()) {
				String s = (String) roots.nextElement();
				if(s.startsWith("file:///")) s = s.substring("file:///".length());
				rootsList.addElement(s);
			}
		} catch (Exception e) {
		}
	}
	

	public static void loadConfig() {
		customLocale = Locale.l;
		fullScreen = true;
		/*
		String s = System.getProperty("kemulator.libvlc.supported");
		if(s != null && s.equals("true")) {
			watchMethod = 1;
		}
		*/
		boolean ru = region.equalsIgnoreCase("RU") || customLocale.equalsIgnoreCase("ru");
		if(ru) {
			inv = iteroni;
			httpStream = true;
			iteroniPlaybackProxy = false;
			useApiProxy = true;
		}
		try {
			langsList = new Vector();
			langsList.addElement(new String[] { "en", "English", "", "Built-in"});
			langsList.addElement(new String[] { "ru", "Russian", "Русский", "Built-in"});
			InputStream is = "".getClass().getResourceAsStream("/jtindex");
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			char[] cbuf = new char[2048];
			isr.read(cbuf);
			isr.close();
			try {
				is.close();
			} catch (Exception e) {
			}
			int i = 0;
			char c;
			boolean skipLine = false;
			StringBuffer tmp = new StringBuffer();
			boolean b = true;
			while(b) {
				c = cbuf[i++];
				switch(c) {
				case '#':
					skipLine = true;
					break;
				case '\r':
					break;
				case 0:
					b = false;
				case '\n':
					if(!skipLine && tmp.length() > 6) {
						String line = tmp.toString();
						if(line.startsWith("jtlng_")) {
							int idx = line.indexOf('=');
							String[] arr = new String[5];
							arr[0] = line.substring(6, idx);
							line = line.substring(idx+1);
							JSONArray j = JSON.getArray("[".concat(line).concat("]"));
							j.copyInto(arr, 1, 4);
							langsList.addElement(arr);
						}
					}
					skipLine = false;
					tmp.setLength(0);
					break;
				default:
					tmp.append(c);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			supportedInputLanguages = Keyboard.getSupportedLanguages();
		} catch (Exception e) {
			e.printStackTrace();
		}
		RecordStore r = null;
		try {
			r = RecordStore.openRecordStore(CONFIG_RECORD_NAME, false);
		} catch (Exception e) {
		}
		
		if(r == null) {
			// Defaults
			if(PlatformUtils.isJ2ML()) {
				videoPreviews = true;
				httpStream = false;
				videoRes = "360p";
				downloadDir = "C:/";
			} else {
				String downloadDir = System.getProperty("fileconn.dir.videos");
				if(downloadDir == null)
					downloadDir = System.getProperty("fileconn.dir.photos");
				if(downloadDir == null) {
					getRoots();
					if(rootsList.size() > 0) {
						String root = "";
						for(int i = 0; i < rootsList.size(); i++) {
							String s = (String) rootsList.elementAt(i);
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
								break;
							}
							if(PlatformUtils.isPhoneme()) {
								if(s.startsWith("/Storage")) {
									root = s;
									break;
								}
								if(s.startsWith("/MyDocs")) {
									root = s;
								}
							}
						}
						if(!root.endsWith("/")) root += "/";
						downloadDir = root;
						try {
							FileConnection fc = (FileConnection) Connector.open("file:///" + root + "videos/");
							if(fc.exists()) {
								downloadDir = root + "videos/";
							}
							fc.close();
						} catch (Exception e) {
						}
					}
				} else if(downloadDir.startsWith("file:///")) {
					downloadDir = downloadDir.substring("file:///".length());
				}
				Settings.downloadDir = downloadDir;
				watchMethod = PlatformUtils.isSymbian3Based()/* || PlatformUtils.isBada() || PlatformUtils.isS603rd()*/ ? 1 : 0;
				searchBar = false;
				boolean lowEnd = isLowEndDevice();
				if(lowEnd) {
					httpStream = true;
					rememberSearch = false;
					searchChannels = true;
					asyncLoading = false;
					videoPreviews = false;
					serverstream = stream;
					fastScrolling = true;
				} else {
					if((!PlatformUtils.isSymbianJ9() && !PlatformUtils.isS60v3orLower()) || PlatformUtils.isBada()) {
						httpStream = true;
						asyncLoading = false;
					}
					if(PlatformUtils.isSymbian3Based() || PlatformUtils.isSymbian93() || (PlatformUtils.isSymbian94() && PlatformUtils.platform.indexOf("SonyEricssonU5i") != -1 && PlatformUtils.platform.indexOf("Samsung") != -1)) {
						asyncLoading = true;
						downloadBuffer = 4096;
						searchBar = true;
					}
					if(PlatformUtils.isPhoneme()) {
						asyncLoading = true;
						downloadBuffer = 4096;
						rmsPreviews = true;
					}
					if(PlatformUtils.isS60v3orLower()) {
						httpStream = true;
					}
					rememberSearch = true;
					searchChannels = true;
					searchPlaylists = true;
					videoPreviews = true;
				}
				if(PlatformUtils.isAsha()) {
					serverstream = stream;
					videoPreviews = true;
					if(PlatformUtils.isAshaFullTouch()) {
						searchBar = true;
					}
				} else if(PlatformUtils.isS40() /*|| (PlatformUtils.isNotS60() && !PlatformUtils.isS603rd() && PlatformUtils.startMemory > 512 * 1024 && PlatformUtils.startMemory < 2024 * 1024)*/) {
					serverstream = stream;
					videoPreviews = true;
					rmsPreviews = true;
				} else {
					serverstream = glype;
				}
				int min = Math.min(App.startWidth, App.startHeight);
				// Symbian 9.4 can't handle H.264/AVC
				if(min < 360 || (PlatformUtils.isSymbian94() && PlatformUtils.platform.indexOf("SonyEricssonU5i") == -1 && PlatformUtils.platform.indexOf("Samsung") == -1)) {
					videoRes = "144p";
				} else {
					videoRes = "360p";
				}
			}
			saveConfig();
		} else {
			try {
				JSONObject j = JSON.getObject(new String(r.getRecord(1), "UTF-8"));
				r.closeRecordStore();
				videoRes = j.getString("videoRes", videoRes);
				region = j.getString("region", region);
				downloadDir = j.getString("downloadDir", downloadDir);
				videoPreviews = j.getBoolean("videoPreviews", videoPreviews);
				searchChannels = j.getBoolean("searchChannels", searchChannels);
				rememberSearch = j.getBoolean("rememberSearch", rememberSearch);
				httpStream = j.getBoolean("httpStream", httpStream);
				serverstream = j.getString("serverstream", serverstream);
				startScreen = j.getInt("startScreen", startScreen);
				rmsPreviews = j.getBoolean("rmsPreviews", rmsPreviews);
				customLocale = j.getString("customLocale", customLocale);
				searchPlaylists = j.getBoolean("searchPlaylists", searchPlaylists);
				watchMethod = j.getInt("watchMethod", watchMethod);
				asyncLoading = j.getBoolean("asyncLoading", asyncLoading);
				downloadBuffer = j.getInt("downloadBuffer", downloadBuffer);
				checkUpdates = j.getBoolean("checkUpdates", true);
				iteroniPlaybackProxy = j.getBoolean("iteroniPlaybackProxy", iteroniPlaybackProxy);
				renderDebug = j.getBoolean("renderDebug", renderDebug);
				amoled = j.getBoolean("amoled", amoled);
				smallPreviews = j.getBoolean("smallPreviews", smallPreviews);
				fastScrolling = j.getBoolean("fastScrolling", fastScrolling);
				searchBar = j.getBoolean("searchBar", searchBar);
				autoStart = j.getBoolean("autoStart", autoStart);
				fullScreen = j.getBoolean("fullScreen", fullScreen);
				keyboard = j.getInt("keyboard", keyboard);
				if(j.has("inputLanguages")) {
					JSONArray a = j.getArray("inputLanguages");
					inputLanguages = new String[a.size()];
					for(int i = 0; i < a.size(); i++) {
						inputLanguages[i] = a.getString(i);
					}
				}
				inv = j.getString("inv", inv);
				apiProxy = j.getString("apiProxy", apiProxy);
				useApiProxy = j.getBoolean("useApiProxy", useApiProxy);
				String v = j.getString("v", "v1");
				int i = Integer.parseInt(v=v.substring(1));
				if(i < 2) {
					serverstream = glype;
					inv = iteroni;
					apiProxy = invproxy;
					if(ru) {
						httpStream = true;
						iteroniPlaybackProxy = false;
						useApiProxy = true;
					}
				}
				saveConfig();
				return;
			} catch (Exception e) {
			}
		}
		registerPush();
	}
	
	public static void saveConfig() {
		removeConfig();
		try {
			RecordStore r = RecordStore.openRecordStore(CONFIG_RECORD_NAME, true);
			JSONObject j = new JSONObject();
			j.put("v", "v2");
			j.put("videoRes", videoRes);
			j.put("region", region);
			j.put("downloadDir", downloadDir);
			j.put("videoPreviews", videoPreviews);
			j.put("searchChannels", searchChannels);
			j.put("rememberSearch", rememberSearch);
			j.put("httpStream", httpStream);
			j.put("serverstream", serverstream);
			j.put("inv", inv);
			j.put("startScreen", startScreen);
			j.put("rmsPreviews", rmsPreviews);
			j.put("customLocale", customLocale);
			j.put("searchPlaylists", searchPlaylists);
			j.put("watchMethod", watchMethod);
			j.put("asyncLoading", asyncLoading);
			j.put("downloadBuffer", downloadBuffer);
			j.put("checkUpdates", checkUpdates);
			j.put("iteroniPlaybackProxy", iteroniPlaybackProxy);
			j.put("renderDebug", renderDebug);
			j.put("amoled", amoled);
			j.put("smallPreviews", smallPreviews);
			j.put("fastScrolling", fastScrolling);
			j.put("searchBar", searchBar);
			j.put("autoStart", autoStart);
			j.put("fullScreen", fullScreen);
			j.put("keyboard", keyboard);
			j.put("apiProxy", apiProxy);
			j.put("useApiProxy", useApiProxy);
			JSONArray inputLanguagesJson = new JSONArray();
			for(int i = 0; i < inputLanguages.length; i++) {
				inputLanguagesJson.add(inputLanguages[i]);
			}
			j.put("inputLanguages", inputLanguagesJson);
			byte[] b = j.build().getBytes("UTF-8");
			r.addRecord(b, 0, b.length);
			r.closeRecordStore();
		} catch (Exception e) {
		}
	}
	
	public static void removeConfig() {
		try {
			RecordStore.deleteRecordStore(CONFIG_RECORD_NAME);
		} catch (Throwable e) {
		}
	}
	
	public static boolean isLowEndDevice() {
		return !PlatformUtils.isSymbianJ9() &&
				!(PlatformUtils.isSymbian() && PlatformUtils.getS60().startsWith("3")) && 
				(PlatformUtils.isS30Plus() || 
						App.startWidth < 176 || 
						PlatformUtils.startMemory < 1024 * 1024 ||
						(!PlatformUtils.isBada() && PlatformUtils.isSamsung()));
	}
	
	public static void registerPush() {
		try {
			if(App.midlet.getAppProperty("MIDlet-Push-1") != null) return;
		} catch (Exception e) {
		}
		try {
			int port = DEFAULT_PUSH_PORT;
			try {
				port = Integer.parseInt(App.midlet.getAppProperty("MIDletIntegration-Port"));
			} catch (Exception e) {
			}
			if(autoStart) {
				MIDletIntegration.registerPush(App.midlet, port);
			} else {
				MIDletIntegration.unregisterPush(port);
			}
		} catch (Exception e) {
		}
	}

}
