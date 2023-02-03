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
	public static String inv = altinv;
	public static boolean rmsPreviews;
	public static boolean searchPlaylists;
	public static String customLocale;
	public static boolean debugMemory;
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
	public static int renderPriority = 0;
	public static String[] inputLanguages = new String[] {"en", "ru"};
	public static String[] supportedInputLanguages = new String[0];
	public static int keyboard = 0;
	
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
		fullScreen = Util.testCanvas().hasPointerEvents();
		/*
		String s = System.getProperty("kemulator.libvlc.supported");
		if(s != null && s.equals("true")) {
			watchMethod = 1;
		}
		*/
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
							String[] arr = new String[4];
							arr[0] = line.substring(6, idx);
							line = line.substring(idx+1);
							JSONArray j = JSON.getArray("[".concat(line).concat("]"));
							j.copyInto(arr, 1, 3);
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
				boolean s40 = PlatformUtils.isS40();
				if(!s40) {
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
				} else {
					String downloadDir = System.getProperty("fileconn.dir.videos");
					if(downloadDir == null)
						downloadDir = System.getProperty("fileconn.dir.photos");
					if(downloadDir == null)
						downloadDir = "C:/";
					else if(downloadDir.startsWith("file:///"))
						downloadDir = downloadDir.substring("file:///".length());
					Settings.downloadDir = downloadDir;
				}
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
					if((PlatformUtils.isNotS60() && !PlatformUtils.isS603rd()) || PlatformUtils.isBada()) {
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
					if(PlatformUtils.isS603rd()) {
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
					//char c = PlatformUtils.platform.charAt(5);
					//customItems = c != '5' && c != '2' && !PlatformUtils.isAshaTouchAndType() && !PlatformUtils.isAshaNoTouch();
				} else if(s40 /*|| (PlatformUtils.isNotS60() && !PlatformUtils.isS603rd() && PlatformUtils.startMemory > 512 * 1024 && PlatformUtils.startMemory < 2024 * 1024)*/) {
					serverstream = stream;
					videoPreviews = true;
					rmsPreviews = true;
				} else {
					serverstream = glype;
				}
				int min = Math.min(App.width, App.height);
				// Symbian 9.4 can't handle H.264/AVC
				if(min < 360 || (PlatformUtils.isSymbian94() && PlatformUtils.platform.indexOf("SonyEricssonU5i") == -1 && PlatformUtils.platform.indexOf("Samsung") == -1)) {
					videoRes = "144p";
				} else {
					videoRes = "360p";
				}
			}
		} else {
			try {
				JSONObject j = JSON.getObject(new String(r.getRecord(1), "UTF-8"));
				r.closeRecordStore();
				if(j.has("videoRes"))
					videoRes = j.getString("videoRes");
				if(j.has("region"))
					region = j.getString("region");
				if(j.has("downloadDir"))
					downloadDir = j.getString("downloadDir");
				if(j.has("videoPreviews"))
					videoPreviews = j.getBoolean("videoPreviews");
				if(j.has("searchChannels"))
					searchChannels = j.getBoolean("searchChannels");
				if(j.has("rememberSearch"))
					rememberSearch = j.getBoolean("rememberSearch");
				if(j.has("httpStream"))
					httpStream = j.getBoolean("httpStream");
				if(j.has("serverstream")) {
					serverstream = j.getString("serverstream");
					// replace old proxy
					if(serverstream.endsWith("/stream.php")) {
						serverstream = glype;
					}
				}
				if(j.has("inv")) {
					inv = j.getString("inv");
					if(inv != null && inv.indexOf("iteroni.com") != -1) {
						inv = altinv;
					}
				}
				if(j.has("startScreen"))
					startScreen = j.getInt("startScreen");
				if(j.has("rmsPreviews"))
					rmsPreviews = j.getBoolean("rmsPreviews");
				if(j.has("customLocale"))
					customLocale = j.getString("customLocale");
				if(j.has("searchPlaylists"))
					searchPlaylists = j.getBoolean("searchPlaylists");
				if(j.has("debugMemory"))
					debugMemory = j.getBoolean("debugMemory");
				if(j.has("watchMethod"))
					watchMethod = j.getInt("watchMethod");
				if(j.has("asyncLoading"))
					asyncLoading = j.getBoolean("asyncLoading");
				if(j.has("downloadBuffer"))
					downloadBuffer = j.getInt("downloadBuffer");
				if(serverstream != null && serverstream.indexOf("nnproject.cc") != -1) {
					serverstream = Util.replace(serverstream, "nnproject.cc", "nnp.nnchan.ru");
				}
				if(j.has("checkUpdates"))
					checkUpdates = j.getBoolean("checkUpdates");
				if(j.has("iteroniPlaybackProxy"))
					iteroniPlaybackProxy = j.getBoolean("iteroniPlaybackProxy");
				if(j.has("renderDebug"))
					renderDebug = j.getBoolean("renderDebug");
				if(j.has("amoled"))
					amoled = j.getBoolean("amoled");
				if(j.has("smallPreviews"))
					smallPreviews = j.getBoolean("smallPreviews");
				if(j.has("fastScrolling"))
					fastScrolling = j.getBoolean("fastScrolling");
				if(j.has("searchBar"))
					searchBar = j.getBoolean("searchBar");
				if(j.has("autoStart"))
					autoStart = j.getBoolean("autoStart");
				if(j.has("fullScreen"))
					fullScreen = j.getBoolean("fullScreen");
				if(j.has("renderPriority"))
					renderPriority = j.getInt("renderPriority", 0);
				if(j.has("keyboard"))
					keyboard = j.getInt("keyboard");
				if(j.has("inputLanguages")) {
					JSONArray a = j.getArray("inputLanguages");
					inputLanguages = new String[a.size()];
					for(int i = 0; i < a.size(); i++) {
						inputLanguages[i] = a.getString(i);
					}
				}
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
			j.put("v", "v1");
			j.put("videoRes", videoRes);
			j.put("region", region);
			j.put("downloadDir", downloadDir);
			j.put("videoPreviews", new Boolean(videoPreviews));
			j.put("searchChannels", new Boolean(searchChannels));
			j.put("rememberSearch", new Boolean(rememberSearch));
			j.put("httpStream", new Boolean(httpStream));
			j.put("serverstream", serverstream);
			j.put("inv", inv);
			j.put("startScreen", new Integer(startScreen));
			j.put("rmsPreviews", new Boolean(rmsPreviews));
			j.put("customLocale", customLocale);
			j.put("searchPlaylists", new Boolean(searchPlaylists));
			j.put("debugMemory", new Boolean(debugMemory));
			j.put("watchMethod", new Integer(watchMethod));
			j.put("asyncLoading", new Boolean(asyncLoading));
			j.put("downloadBuffer", new Integer(downloadBuffer));
			j.put("checkUpdates", new Boolean(checkUpdates));
			j.put("iteroniPlaybackProxy", new Boolean(iteroniPlaybackProxy));
			j.put("renderDebug", new Boolean(renderDebug));
			j.put("amoled", new Boolean(amoled));
			j.put("smallPreviews", new Boolean(smallPreviews));
			j.put("fastScrolling", new Boolean(fastScrolling));
			j.put("searchBar", new Boolean(searchBar));
			j.put("autoStart", new Boolean(autoStart));
			j.put("fullScreen", new Boolean(fullScreen));
			j.put("renderPriority", new Integer(renderPriority));
			j.put("keyboard", new Integer(keyboard));
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
		return PlatformUtils.isNotS60() &&
				!PlatformUtils.isS603rd() && 
				(PlatformUtils.isS30() || 
						App.width < 176 || 
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
