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
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;

import ui.AppUI;
import models.ILoader;
import cc.nnproject.json.JSON;
import cc.nnproject.ytapp.App2;
import midletintegration.MIDletIntegration;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import cc.nnproject.json.AbstractJSON;
import cc.nnproject.json.JSONException;
import cc.nnproject.utils.PlatformUtils;

public class App implements Constants {
	
	public static App inst;
	public static App2 midlet;
	private AppUI ui;
	
	private LoaderThread t0;
	private LoaderThread t1;
	private LoaderThread t2;
	Object loadLock1 = new Object();
	Object loadLock2 = new Object();
	ILoader[] loadTasks = new ILoader[30];
	int loadTasksIdx;
	
	
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
						Thread.yield();
					}
				} catch (InterruptedException e) {
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	private int startSys;

	public static Form loadingForm;
	private StringItem loadingItem;
	private Command loadingExitCmd;
	private Command loadingSetsCmd;

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
				System.arraycopy(queuedTasks, idx+1, queuedTasks, idx, queuedTasks.length - idx);
				queuedTasks[queuedTasks.length - 1] = null;
			}
		}
	}
	
	public static int width;
	public static int height;

	public void startApp() {
		loadingForm = new Form("Loading");
		loadingItem = new StringItem("", "");
		loadingItem.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_VCENTER);
		loadingForm.append(loadingItem);
		loadingForm.addCommand(loadingExitCmd = new Command("Exit", Command.EXIT, 1));
		loadingForm.setCommandListener(new CommandListener() {

			public void commandAction(Command c, Displayable d) {
				if(c == loadingExitCmd) {
					midlet.notifyDestroyed();
				}
				if(c == loadingSetsCmd) {
					ui.showSettings();
				}
			}
			
		});
		Display.getDisplay(midlet).setCurrent(loadingForm);
		try {
			String p = System.getProperty("com.nokia.memoryramfree");
			if(p != null) {
				startSys = Integer.parseInt(p)/1024;
			}
		} catch (Exception e) {
		}
		setLoadingState("Obtaining device Settings.region");
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
		Settings.region = Settings.region.toUpperCase();
		setLoadingState("Testing screen size");
		Util.testCanvas();
		setLoadingState("Initializing tasks thread");
		tasksThread.setPriority(4);
		tasksThread.start();
		setLoadingState("Loading config");
		Settings.loadConfig();
		setLoadingState("Initializing locales");
		Locale.init();
		setLoadingState("Initializing UI");
		initUI();
		loadingForm.addCommand(loadingSetsCmd = new Command("Settings", Command.SCREEN, 1));
		if(Settings.region.toLowerCase().equals("en")) {
			Settings.region = "US";
		}
		if(!Settings.isLowEndDevice() && Settings.asyncLoading) {
			t0 = new LoaderThread(5, 0, this);
			t1 = new LoaderThread(5, 1, this);
			t2 = new LoaderThread(5, 2, this);
			t0.start();
			t1.start();
			t2.start();
		} else {
			t0 = new LoaderThread(5, 0, this);
			t0.start();
		}
		if(!checkStartArguments()) {
			ui.loadMain();
		}
		checkUpdate();
		if(Settings.debugMemory) {
			Thread t = new Thread() {
				public void run() {
					try {
						while(true) {
							Displayable d = AppUI.display.getCurrent();
							if(d != null && !(d instanceof Alert || d instanceof TextBox)) {
								Runtime r = Runtime.getRuntime();
								int t = (int) (r.totalMemory() / 1024);
								int f = (int) (r.freeMemory() / 1024);
								int m = t - f;
								String p = System.getProperty("com.nokia.memoryramfree");
								String sys = "";
								if(p != null) {
									int sy = Integer.parseInt(p)/1024;
									String sysfree = "" + (int)((sy/1024D)*10)/10D;
									String syst = "" + (startSys/1024);
									String sysalloc = "" + (startSys - sy)/1024;
									sys = sysalloc + "/" + syst+ "-" + sysfree;
								}
								//long gt = System.currentTimeMillis();
								Util.gc();
								//gt = System.currentTimeMillis() - gt;
								String s = ((int)((m/1024D)*10)/10D) + "/" + ((int)((t/1024D)*10)/10D) + "-" + ((int)((f/1024D)*10)/10D) + " s:" + sys/* + " gc:" + gt*/;
								d.setTitle(s);
							}
							Thread.sleep(1000);
						}
					} catch (Exception e) {
					}
				}
			};
			t.start();
		}
	}
	
	private void checkUpdate() {
		boolean b = false;
		try {
			// temporary solution
			JSONObject video = (JSONObject) App.invApi("videos/iTwHY7v9M8c?", "description,title");
			if(Settings.checkUpdates) {
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
				String msg = j.getString("message", Locale.s(LocaleConstants.TXT_NewUpdateAvailable));
				Alert a = new Alert("", "", null, AlertType.INFO);
				a.setString(msg);
				final Command ignoreCmd = new Command(Locale.s(LocaleConstants.CMD_Ignore), Command.EXIT, 1);
				final Command okCmd = new Command(Locale.s(LocaleConstants.CMD_Download), Command.OK, 1);
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

	public void setLoadingState(String s) {
		if(loadingItem != null) {
			loadingItem.setText(s);
		}
	}
	
	private void initUI() {
		ui = new AppUI();
		ui.init();
	}

	public static byte[] getImageBytes(String s) throws IOException {
		if(s.startsWith("//")) s = "http:" + s;
		if(s.indexOf("ggpht.com") != -1) {
			s = "/ggpht" + s.substring(s.indexOf("/ytc"));
		}
		if(s.startsWith("/")) return Util.get(Settings.inv + s.substring(1));
		return Util.get(s);
	}
	
	public static AbstractJSON invApi(String s) throws InvidiousException, IOException {
		return invApi(s, null);
	}

	public static AbstractJSON invApi(String s, String fields) throws InvidiousException, IOException {
		String url = s;
		if(!s.endsWith("?")) s = s.concat("&");
		s = s.concat("region=" + (Settings.region != null ? Settings.region.toUpperCase() : "US"));
		if(fields != null) {
			s = s.concat("&fields=".concat(fields).concat(",error,errorBacktrace,code"));
		}
		String dbg = "Region=".concat(Settings.region).concat(" Fields=").concat(fields);
		try {
			s = Util.getUtf(Settings.inv.concat("api/v1/").concat(s));
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetRequestException(e, s);
		}
		AbstractJSON res;
		if(s.charAt(0) == '{') {
			res = JSON.getObject(s);
			if(((JSONObject) res).has("code")) {
				throw new InvidiousException((JSONObject) res, ((JSONObject) res).getString("code") + ": " + ((JSONObject) res).getNullableString("message"), url, dbg);
			}
			if(((JSONObject) res).has("error")) {
				throw new InvidiousException((JSONObject) res, ((JSONObject) res).getNullableString("error"), url, dbg);
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
			int k = 0;
			if(res.equals("_audiolow"))
				k = Integer.MAX_VALUE;
			for(int i = 0; i < l; i++) {
				JSONObject o = arr.getObject(i);
				String t = o.getNullableString("type");
				if(t != null && t.startsWith("audio")) {
					if(res.equals("_audiolow")) {
						int n = o.getInt("bitrate", 0);
						if(n < k) r = o;
					}
					if(res.equals("_audiohigh")) {
						int n = o.getInt("bitrate", 0);
						if(n > k) r = o;
					}
				}
				if(res.equals("_240p")) {
					String q = o.getNullableString("qualityLabel");
					String c = o.getNullableString("container");
					if(q != null && q.startsWith("240p") && c != null && c.startsWith("mp4")) {
						r = o;
					}
				}
			}
			return r;
		}
	}

	public static String getVideoLink(String id, String res, boolean forceProxy) throws JSONException, IOException {
		JSONObject o = getVideoInfo(id, res);
		String s = o.getString("url");
		if(Settings.httpStream || forceProxy) {
			if(Settings.iteroniPlaybackProxy) {
				int i = s.indexOf("/videoplayback");
				s = Settings.inv + s.substring(i+1);
			} else {
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
		inst.stopLoadTasks();
		try {
			switch (Settings.watchMethod) {
			case 0: {
				String url = getVideoLink(id, Settings.videoRes);
				try {
					Util.platReq(url);
				} catch (Exception e) {
					e.printStackTrace();
					error(null, Errors.App_watch, e);
				}
				break;
			}
			case 1: {
				String url = getVideoLink(id, Settings.videoRes, true);
				String file = "file:///" + Settings.downloadDir;
				if (!file.endsWith("/") && !file.endsWith("\\"))
					file += "/";
				if (PlatformUtils.isSymbian3Based() || PlatformUtils.isBada()) {
					file += "watch.ram";
				} else /*if (PlatformUtils.isS603rd()) {
					file += "watch.m3u";
				} else */{
					Settings.watchMethod = 0;
					Util.platReq(url);
					break;
				}
				FileConnection fc = null;
				OutputStream o = null;
				try {
					fc = (FileConnection) Connector.open(file);
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
				Util.platReq(file);
				break;
			}
			case 2: {
				Util.platReq("https://next.2yxa.mobi/mov.php?id=" + id + "&poisk=you" + (Locale.localei != 1 ? "&lang=en" : ""));
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(null, Errors.App_watch, e);
		}
	}
	
	public void addLoadTask(ILoader v) {
		try {
			if(v == null) throw new NullPointerException("l");
			synchronized(loadLock2) {
				loadTasks[loadTasksIdx++] = v;
				if(loadTasksIdx >= loadTasks.length - 1) {
					ILoader[] tmp = loadTasks;
					loadTasks = new ILoader[loadTasks.length + 16];
					System.arraycopy(tmp, 0, loadTasks, 0, tmp.length);
				}
			}
			synchronized(loadLock1) {
				loadLock1.notifyAll();
			}
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	/** @deprecated */
	public void startAsyncTasks() {
	}

	/** @deprecated */
	void waitAsyncTasks() {
	}

	public void stopLoadTasks() {
		//System.out.println("STOP");
		if(t0 != null) t0.doInterrupt();
		if(t1 != null) t1.doInterrupt();
		if(t2 != null) t2.doInterrupt();
		synchronized(loadLock2) {
			loadTasks = new ILoader[loadTasks.length];
			loadTasksIdx = 0;
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
			inst.ui.openChannel(s);
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
