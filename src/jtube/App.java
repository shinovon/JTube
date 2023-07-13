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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import cc.nnproject.json.AbstractJSON;
import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONException;
import cc.nnproject.json.JSONObject;
import cc.nnproject.utils.PlatformUtils;
import cc.nnproject.ytapp.App2;
import jtube.ui.AppUI;
import jtube.ui.Locale;
import jtube.ui.screens.SplashScreen;
import midletintegration.MIDletIntegration;

public class App implements Constants, Runnable {
	
	public static App inst;
	public static App2 midlet;
	private AppUI ui;
	
	public static int startWidth;
	public static int startHeight;
	
	private Runnable[] queuedTasks = new Runnable[30];
	private int queuedTasksIdx;
	private Object tasksLock = new Object();
	private Thread tasksThread = new Thread("Task Thread") {
		public void run() {
			while(midlet.running) {
				try {
					synchronized (tasksLock) {
						tasksLock.wait();
					}
					while(true) {
						Runnable r = queuedTasks[0];
						if(r == null) continue;
						System.arraycopy(queuedTasks, 1, queuedTasks, 0, queuedTasks.length - 1);
						queuedTasks[queuedTasks.length - 1] = null;
						queuedTasksIdx--;
						try {
							r.run();
						} catch (Exception e) {
						}
						Thread.sleep(1);
					}
				} catch (InterruptedException e) {
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	private Thread uiThread;

	public void schedule(Runnable r) {
		queuedTasks[queuedTasksIdx++] = r;
		if(queuedTasksIdx == queuedTasks.length) {
			Runnable[] tmp = queuedTasks;
			queuedTasks = new Runnable[queuedTasks.length + 16];
			System.arraycopy(tmp, 0, queuedTasks, 0, tmp.length);
		}
		synchronized(tasksLock) {
			tasksLock.notify();
		}
	}

	public void cancel(Runnable r) {
		synchronized(tasksLock) {
			int idx = -1;
			for(int i = 0; i < queuedTasks.length; i++) {
				if(queuedTasks[i] == r) {
					idx = i;
					break;
				}
			}
			if(idx != -1) {
				if(idx < queuedTasks.length - 1) {
					System.arraycopy(queuedTasks, idx+1, queuedTasks, idx, queuedTasks.length - idx);
				}
				queuedTasks[queuedTasks.length - 1] = null;
			}
		}
	}

	public void startApp() {
		SplashScreen splash = new SplashScreen();
		Display.getDisplay(midlet).setCurrent(splash);
		App.startWidth = splash.getWidth();
		App.startHeight = splash.getHeight();
		Settings.region = System.getProperty("user.country");
		if(Settings.region == null) {
			Settings.region = System.getProperty("microedition.locale");
			if(Settings.region == null) {
				Settings.region = "US";
			} else {
				if(Settings.region.length() == 5) {
					Settings.region = Settings.region.substring(3, 5);
				} else if(Settings.region.length() > 2) {
					Settings.region = "US";
				}
			}
		} else if(Settings.region.length() > 2) {
			Settings.region = Settings.region.substring(0, 2);
		}
		if(Settings.region.toLowerCase().equals("en")) {
			Settings.region = "US";
		}
		Settings.region = Settings.region.toUpperCase();
		tasksThread.start();
		Settings.loadConfig();
		Locale.init();
		startUIThread();
	}
	
	private void startUIThread() {
		uiThread = new Thread(this, "UI Thread");
		uiThread.start();
	}
	
	public void run() {
		LocalStorage.init();
		initUI();
		Loader.init();
		new Thread() {
			public void run() {
				if(!checkStartArguments()) {
					ui.loadMain();
				}
				try {
					checkUpdate();
				} catch (Throwable e) {
				}
			}
		}.start();
		ui.run();
	}
	
	private void checkUpdate() {
		boolean b = false;
		/*
		try {
			if(Settings.checkUpdates) {
				JSONObject video = (JSONObject) App.invApi("videos/iTwHY7v9M8c?", "description,title");
				String title = video.getNullableString("title");
				if(title != null && !title.equalsIgnoreCase("jtube") && !title.trim().equalsIgnoreCase(App.midlet.getAppProperty("MIDlet-Version"))) {
					b = true;
					Alert a = new Alert("", "", null, AlertType.INFO);
					a.setTimeout(-2);
					a.setString(video.getString("description", "Download from: t.me/nnmidlets"));
					a.setTitle(Locale.s(LocaleConstants.TXT_NewUpdateAvailable));
					final Command okCmd = new Command(Locale.s(LocaleConstants.CMD_OK), Command.OK, 1);
					a.addCommand(okCmd);
					a.setCommandListener(new CommandListener() {
						public void commandAction(Command c, Displayable d) {
							if(c == okCmd) ui.display(null);
						}
					});
					ui.display(a);
				}
			}
		} catch (Exception e) {
		}
		*/
		try {
			String s = Util.getUtf(updateurl+
					"?v="+App.midlet.getAppProperty("MIDlet-Version")+
					"&l="+Locale.l+
					"&s="+(App.midlet.getAppProperty("JTube-Samsung-Build") != null ? "1" : "0")+
					"&p="+Util.url(PlatformUtils.platform)
					);
			JSONObject j = JSON.getObject(s);
			if(j.getBoolean("update_available", false) && Settings.checkUpdates && !b) {
				final String url = j.getString("download_url");
				String msg = j.getString("message", Locale.s(Locale.TXT_NewUpdateAvailable));
				Alert a = new Alert("", "", null, AlertType.INFO);
				a.setString(msg);
				final Command ignoreCmd = new Command(Locale.s(Locale.CMD_Ignore), Command.EXIT, 1);
				final Command okCmd = new Command(Locale.s(Locale.CMD_Download), Command.OK, 1);
				a.addCommand(ignoreCmd);
				a.addCommand(okCmd);
				a.setCommandListener(new CommandListener() {
					public void commandAction(Command c, Displayable d) {
						if(c == ignoreCmd) {
							ui.display(null);
						}
						if(c == okCmd) {
							ui.display(null);
							try {
								App.midlet.platformRequest(url);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
				ui.display(a);
			}
		} catch (Exception e) {
		}
	}
	
	private void initUI() {
		ui = new AppUI();
		ui.init();
	}

	public static byte[] getImageBytes(String s) throws IOException {
		if(s.startsWith("//")) s = "http:" + s;
		if(s.indexOf("ggpht.com") != -1 || s.indexOf("googleusercontent.com") != -1) {
			if(s.indexOf("//") != -1) s = s.substring(s.indexOf("//") + 2);
			s = "/ggpht" + s.substring(s.indexOf("/"));
		}
		if(s.startsWith("/")) s = Settings.inv + s.substring(1);
		if(Settings.useApiProxy) {
			s = Settings.apiProxy.concat("?u=").concat(Util.url(s));
		}
		return Util.get(s);
	}
	
	public static AbstractJSON invApi(String s) throws InvidiousException, IOException {
		return invApi(s, null);
	}

	public static AbstractJSON invApi(String s, String fields) throws InvidiousException, IOException {
		String url = s;
		if(!s.endsWith("?")) s = s.concat("&");
		s += "region=" + (Settings.region != null ? Settings.region.toUpperCase() : "US");
		if(fields != null) {
			s = s + "&fields=" + fields + ",error,errorBacktrace,code";
		}
		s = Settings.inv + "api/v1/" + s;
		if(Settings.useApiProxy) {
			s = Settings.apiProxy + "?u=" + Util.url(s);
		}
		try {
			s = Util.getUtf(s);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetRequestException(e, s);
		}
		AbstractJSON res;
		if(s.charAt(0) == '{') {
			res = JSON.getObject(s);
			if(((JSONObject) res).has("code")) {
				throw new InvidiousException((JSONObject) res, ((JSONObject) res).getString("code") + ": " + ((JSONObject) res).getNullableString("message"), url, "");
			}
			if(((JSONObject) res).has("error")) {
				throw new InvidiousException((JSONObject) res, ((JSONObject) res).getNullableString("error"), url, "");
			}
		} else {
			res = JSON.getArray(s);
		}
		return res;
	}

	static JSONObject getVideoInfo(String id, String res) throws JSONException, IOException {
		boolean combined = res == null || res.charAt(0) != '_';
		/*
			if(piped) {
			String s = Util.getUtf(imgproxy + Util.url("https://pipedapi.kavin.rocks/streams/" + id));
			JSONObject j = JSON.getObject(s);
			JSONArray videoStreams = j.getArray("videoStreams");
			for(int i = videoStreams.size() - 1; i >= 0; i--) {
				JSONObject v = videoStreams.getObject(i);
				if(!v.getBoolean("videoOnly", false)) {
					if(v.getString("quality").equalsIgnoreCase(res)) {
						JSONObject r = new JSONObject();
						r.put("url", v.getString("url"));
						try {
							r.put("clen", new Integer(v.getInt("bitrate")*j.getInt("duration")));
						} catch (Exception e) {
						}
						return r;
					}
				}
			}
			return null;
		} else {
		*/
		String f = combined ? "formatStreams" : "adaptiveFormats";
		JSONObject j = (JSONObject) invApi("videos/"  + id + "?", f);
		JSONArray arr = j.getArray(f);
		if(j.size() == 0) {
			throw new RuntimeException("failed to get link for video: " + id);
		}
		int l = arr.size();
		if(combined) {
			JSONObject _144 = null;
			JSONObject _360 = null;
			JSONObject _720 = null;
			JSONObject other = null;
			for(int i = 0; i < l; i++) {
				JSONObject o = arr.getObject(i);
				String q = o.getString("qualityLabel");
				if(q.startsWith("720p")) {
					_720 = o;
				} else if(q.startsWith("360p")) {
					_360 = o;
				} else if(q.startsWith("144p")) {
					_144 = o;
				} else {
					other = o;
				}
			}
			JSONObject o = null;
			if(res == null) {
				if(_360 != null) {
					o = _360;
				} else if(other != null) {
					o = other;
				} else if(_144 != null) {
					o = _144;
				} 
			} else if(res.equals("144p")) {
				if(_144 != null) {
					o = _144;
				} else if(_360 != null) {
					o = _360;
				} else if(other != null) {
					o = other;
				}
			} else if(res.equals("360p")) {
				if(_360 != null) {
					o = _360;
				} else if(other != null) {
					o = other;
				} else if(_144 != null) {
					o = _144;
				} 
			} else if(res.equals("720p")) {
				if(_720 != null) {
					o = _720;
				} else if(_360 != null) {
					o = _360;
				} else if(other != null) {
					o = other;
				} else if(_144 != null) {
					o = _144;
				} 
			}
			return o;
		} else {
			JSONObject r = null;
			int k = -1;
			if(res.equals("_audiolow"))
				k = Integer.MAX_VALUE;
			for(int i = 0; i < l; i++) {
				JSONObject o = arr.getObject(i);
				if(o.getString("type", "").startsWith("audio/mp4")) {
					if(res.equals("_audiolow")) {
						int n = o.getInt("bitrate", 0);
						if(n < k || n < 0) r = o;
					}
					if(res.equals("_audiohigh")) {
						int n = o.getInt("bitrate", 0);
						if(n > k || n < 0) r = o;
					}
				}
				if(res.equals("_240p")) {
					if(o.getString("qualityLabel", "").startsWith("240p") && o.getString("type", "").startsWith("video/mp4")) {
						r = o;
					}
				}
			}
			return r;
		}
	}

	public static String getVideoLink(String id, String res, boolean forceProxy) throws JSONException, IOException {
		JSONObject o = getVideoInfo(id, res);
		if(o == null) throw new RuntimeException("not found");
		String s = o.getString("url");
		if(Settings.httpStream || forceProxy) {
			if(Settings.iteroniPlaybackProxy) {
				int i = s.indexOf("/videoplayback");
				s = Settings.inv + s.substring(i+1);
			}
			if(!Settings.iteroniPlaybackProxy || Settings.useApiProxy) {
				s = Settings.serverstream + Util.url(s);
			}
		}
		return s;
	}

	public static String getVideoLink(String id, String res) throws JSONException, IOException {
		return getVideoLink(id, res, false);
	}
	
	public static void download(final String id, String name) {
		Downloader d = new Downloader(id, Settings.videoRes, Settings.downloadDir, name);
		d.start();
	}
	
	public static void watch(final String id) {
		Loader.stop();
		try {
			switch (Settings.watchMethod) {
			case 0: {
				String url = getVideoLink(id, Settings.videoRes);
				try {
					Util.platReq(url);
				} catch (Exception e) {
					error(null, Errors.App_watch, e);
				}
				break;
			}
			case 1: {
				if(Settings.downloadDir == null || Settings.downloadDir.length() < 2) {
					Alert a = new Alert("");
					a.setString(Locale.s(Locale.TXT_DownloadDirWarning));
					Command c = new Command(Locale.s(Locale.CMD_Settings), Command.OK, 2);
					a.addCommand(c);
					a.setCommandListener(new CommandListener() {

						public void commandAction(Command c, Displayable arg1) {
							if(c.getPriority() == 2) {
								AppUI.inst.showSettings();
							}
						}
						
					});
					return;
				}
				String url = getVideoLink(id, Settings.videoRes, true);
				boolean bada = PlatformUtils.isBada();
				String file = bada ? System.getProperty("fileconn.dir.videos") : ("file:///" + Settings.downloadDir);
				if (!file.endsWith("/") && !file.endsWith("\\"))
					file += "/";
				if (PlatformUtils.isSymbian3Based() || PlatformUtils.isBada()) {
					file += "watch.ram";
				} else /*if (PlatformUtils.isSymbian93()) {
					file += "watch.ram";
				} else */{
					Settings.watchMethod = 0;
					Util.platReq(url);
					break;
				}
				FileConnection fc = null;
				OutputStream o = null;
				try {
					fc = (FileConnection) Connector.open(file, 3);
					if (fc.exists())
						fc.delete();
					fc.create();
					o = fc.openDataOutputStream();
					o.write(url.getBytes("UTF-8"));
					o.flush();
				} finally {
					try {
						if (o != null)
							o.close();
						if (fc != null)
							fc.close();
					} catch (Exception e) {
					}
				}
				if(bada) {
					try {
						Util.platReq("file:///Media/Videos/watch.ram");
						return;
					} catch (Exception e) {
					}
				}
				Util.platReq(file);
				break;
			}
			case 2: {
				Util.platReq("https://next.2yxa.mobi/mov.php?id=" + id + "&poisk=you" + (Locale.localei != 1 ? "&lang=en" : ""));
				break;
			}
			}
		} catch (Exception e) {
			if(e instanceof RuntimeException && "not found".equals(e.getMessage())) {
				inst.ui.msg("Selected quality is not available");
				return;
			}
			error(null, Errors.App_watch, e);
		}
	}
	
	public static String getThumbUrl(String id, int tw) {
		String s;
		if(tw < 120) {
			s = "default";
		} else if(tw < 480) {
			s = "mqdefault";
		} else if(tw < 640) {
			s = "hqdefault";
		} else if(tw < 720) {
			s = "sddefault";
		} else {
			s = "maxresdefault";
		}
		return "/vi/" + id + "/" + s + ".jpg";
	}

	public static String getThumbUrl(JSONArray arr, int tw) {
		JSONObject s = null;
		int ld = 16384;
		int l = arr.size();
		for(int i = 0; i < l; i++) {
			JSONObject j = arr.getObject(i);
			int d = Math.abs(tw - j.getInt("width"));
			if (d < ld) {
				ld = d;
				s = j;
			}
		}
		return s.getString("url");
	}

	public static String getSmallestThumbUrl(JSONArray arr) {
		JSONObject s = null;
		int ld = 16384;
		int l = arr.size();
		for(int i = 0; i < l; i++) {
			JSONObject j = arr.getObject(i);
			int d = j.getInt("width");
			if (d < ld) {
				ld = d;
				s = j;
			}
		}
		return s.getString("url");
	}

	public static void warn(Object o, String str) {
		String s = str + " \n\n" + getThreadInfo(o);
		Alert a = new Alert("", s, null, AlertType.WARNING);
		a.setTimeout(-2);
		Display.getDisplay(midlet).setCurrent(a);
	}

	public static void error(Object o, String str) {
		String s = str + " \n" + CONTANT_DEVELOPER_STRING + " \n\n" + 
				getThreadInfo(o);
		Alert a = new Alert("", s, null, AlertType.ERROR);
		a.setTimeout(-2);
		Display.getDisplay(midlet).setCurrent(a);
	}

	public static void error(Object o, int i, Throwable e) {
		e.printStackTrace();
		if(e instanceof InvidiousException) {
			error(o, i, e.toString(), ((InvidiousException)e).toErrMsg());
			return;
		}
		if(e instanceof NetRequestException) {
			NetRequestException e2 = (NetRequestException) e;
			error(o, i, e2.getTheCause().toString(), "URL: " + e2.getUrl());
			return;
		}
		error(o, i, e.toString(), null);
	}

	public static void error(Object o, int i, String str) {
		error(o, i, str, null);
	}

	public static void error(Object o, int i, String str, String str2) {
		String s = str + " \n" + CONTANT_DEVELOPER_STRING + " " + EOL + EOL + "e: " + i + " \n" + 
				getThreadInfo(o) + 
				(str2 != null ? " \n" + str2 : "");
		Alert a = new Alert("", s, null, AlertType.ERROR);
		a.setTimeout(-2);
		Display.getDisplay(midlet).setCurrent(a);
	}
	
	private static String getThreadInfo(Object o) {
		Thread t = Thread.currentThread();
		return (o != null ? "at " + o.getClass().getName() + " " + EOL : "") + 
				"t: " + t.getName() +
				" (p: " + t.getPriority() + 
				" c: " + t.getClass() + ") " + EOL +
				Thread.activeCount();
	}
	
	public static boolean checkStartArguments() {
		try {
			if(MIDletIntegration.checkLaunch()) {
				return parseStartArguments();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean parseStartArguments() {
		Hashtable args = MIDletIntegration.getArguments(MIDletIntegration.getLaunchCommand());
		String s;
		if((s = (String) args.get("url")) != null && s.length() > 0) {
			try {
				s = Util.decodeURL(s);
				openURL(s);
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		} else if((s = (String) args.get("videoId")) != null && s.length() > 0) {
			inst.ui.openVideo(s);
			return true;
		} else if((s = (String) args.get("channelId")) != null && s.length() > 0) {
			inst.ui.openChannel(s);
			return true;
		} else if((s = (String) args.get("playlistId")) != null && s.length() > 0) {
			inst.ui.openPlaylist(s);
			return true;
		}
		return false;
	}

	public static void openURL(String url) {
		if(inst == null || inst.ui == null) {
			return;
		}
		final String https = "https://";
		final String http = "http://";
		final String www = "www.";
		if(url.startsWith(https)) url = url.substring(https.length());
		else if(url.startsWith(http)) url = url.substring(http.length());
		if(url.startsWith(www)) url = url.substring(www.length());
		if(url.startsWith("youtu.be")) {
			int i = url.indexOf('/');
			if(i == -1)
				throw new IllegalArgumentException();
			url = url.substring(i + 1);
			if((i = url.indexOf('/')) != -1) {
				url = url.substring(0, i);
			}
			inst.ui.openVideo(url);
		} else if(url.startsWith("youtube.com") || 
				url.startsWith("iteroni.com") || 
				url.startsWith("invidious.snopyta.org")) {
			int i = url.indexOf('/');
			if(i == -1)
				throw new IllegalArgumentException();
			url = url.substring(i + 1);
			if(url.startsWith("watch")) {
				i = url.indexOf("?v=");
				if(i == -1)
					throw new IllegalArgumentException();
				url = url.substring(i + 3);
				if((i = url.indexOf('&')) != -1) {
					url = url.substring(0, i);
				}
				inst.ui.openVideo(url);
			} else if(url.startsWith("embed")) {
				i = url.indexOf('/');
				if(i == -1)
					throw new IllegalArgumentException();
				url = url.substring(i + 1);
				if((i = url.indexOf('/')) != -1) {
					url = url.substring(0, i);
				} else if((i = url.indexOf('?')) != -1) {
					url = url.substring(0, i);
				}
				inst.ui.openVideo(url);
			} else if(url.startsWith("@")) {
				url = url.substring(url.indexOf('@'));
				if((i = url.indexOf('/')) != -1) {
					url = url.substring(0, i);
				} else if((i = url.indexOf('?')) != -1) {
					url = url.substring(0, i);
				}
				inst.ui.openChannel(url);
			} else if(url.startsWith("c")) {
				i = url.indexOf('/');
				if(i == -1)
					throw new IllegalArgumentException();
				url = url.substring(i + 1);
				if((i = url.indexOf('/')) != -1) {
					url = url.substring(0, i);
				} else if((i = url.indexOf('?')) != -1) {
					url = url.substring(0, i);
				}
				inst.ui.openChannel(url);
			} else if(url.startsWith("playlist")) {
				i = url.indexOf("?list=");
				if(i == -1)
					throw new IllegalArgumentException();
				url = url.substring(i + 6);
				if((i = url.indexOf('&')) != -1) {
					url = url.substring(0, i);
				}
				inst.ui.openPlaylist(url);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

}
