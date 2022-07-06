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
	public static String imgproxy = hproxy;
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
		customLocale = Locale.l;
		/*
		String s = System.getProperty("kemulator.libvlc.supported");
		if(s != null && s.equals("true")) {
			watchMethod = 1;
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
				videoPreviews = true;
				httpStream = false;
				videoRes = "360p";
				downloadDir = "C:/";
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
				watchMethod = PlatformUtils.isSymbian3Based() || PlatformUtils.isBada()/*|| PlatformUtils.isS603rd()*/ ? 1 : 0;
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
					if(PlatformUtils.isSymbian3Based() || (PlatformUtils.isSymbian94() && PlatformUtils.platform.indexOf("SonyEricssonU5i") != -1 && PlatformUtils.platform.indexOf("Samsung") != -1)) {
						asyncLoading = true;
						downloadBuffer = 4096;
					}
					if(PlatformUtils.isPhoneme()) {
						asyncLoading = true;
						downloadBuffer = 4096;
						rmsPreviews = true;
					}
					rememberSearch = true;
					searchChannels = true;
					searchPlaylists = true;
					videoPreviews = true;
				}
				if(PlatformUtils.isAsha()) {
					serverstream = stream;
					videoPreviews = true;
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
				if(j.has("inv"))
					inv = j.getString("inv");
				if(j.has("imgProxy"))
					imgproxy = j.getString("imgProxy");
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
				if((serverstream != null && serverstream.indexOf("nnproject.cc") != -1)
						|| (imgproxy != null && imgproxy.indexOf("nnproject.cc") != -1)) {
					if(serverstream != null)
						serverstream = Util.replace(serverstream, "nnproject.cc", "nnp.nnchan.ru");
					if(imgproxy != null)
						imgproxy = Util.replace(imgproxy, "nnproject.cc", "nnp.nnchan.ru");
				}
				if(imgproxy != null) {
					imgproxy = Util.replace(imgproxy, "nnp.nnchan.ru/hproxy.php", "nnp.nnchan.ru/proxy.php");
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
				return;
			} catch (Exception e) {
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
			j.put("imgProxy", imgproxy);
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
			byte[] b = j.build().getBytes("UTF-8");
			r.addRecord(b, 0, b.length);
			r.closeRecordStore();
		} catch (Exception e) {
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

}
