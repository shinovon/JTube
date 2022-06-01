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
import java.util.Vector;

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

import ui.AppUI;
import models.ILoader;
import cc.nnproject.json.JSON;
import cc.nnproject.ytapp.App2;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import cc.nnproject.json.AbstractJSON;
import cc.nnproject.json.JSONException;
import cc.nnproject.utils.PlatformUtils;

public class App implements Constants {
	
	public static App inst;
	public static App2 midlet;
	private AppUI ui;
	
	//private static PlayerCanvas playerCanv;
	
	private Object lazyLoadLock = new Object();
	private LoaderThread t0;
	private LoaderThread t1;
	private LoaderThread t2;
	private Vector v0;
	private Vector v1;
	private Vector v2;
	private Object addLock = new Object();
	
	private Vector queuedTasks = new Vector();
	private Object tasksLock = new Object();
	private Thread tasksThread = new Thread() {
		public void run() {
			while(midlet.running) {
				try {
					synchronized (tasksLock) {
						tasksLock.wait();
					}
					while(queuedTasks.size() > 0) {
						Object o = queuedTasks.elementAt(0);
						queuedTasks.removeElementAt(0);
						try {
							if(o instanceof Runnable) ((Runnable)o).run();
						} catch (Exception e) {
						}
						Thread.yield();
					}
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	};

	private int startSys;

	public static Form loadingForm;
	private StringItem loadingItem;
	private Command loadingExitCmd;
	private Command loadingSetsCmd;

	public void schedule(Object o) {
		if(queuedTasks.contains(o)) return;
		queuedTasks.addElement(o);
		synchronized(tasksLock) {
			tasksLock.notify();
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
		v0 = new Vector();
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
		setLoadingState("Initializing loader thread");
		if(!Settings.isLowEndDevice() && Settings.asyncLoading) {
			v1 = new Vector();
			v2 = new Vector();
			t0 = new LoaderThread(5, lazyLoadLock, v0, addLock, 0);
			t1 = new LoaderThread(5, lazyLoadLock, v1, addLock, 1);
			t2 = new LoaderThread(5, lazyLoadLock, v2, addLock, 2);
			t0.start();
			t1.start();
			t2.start();
		} else {
			t0 = new LoaderThread(5, lazyLoadLock, v0, addLock, 0);
			t0.start();
		}
		setLoadingState("Loading start page");
		ui.loadForm();
		checkUpdate();
		if(Settings.debugMemory) {
			Thread t = new Thread() {
				public void run() {
					try {
						while(true) {
							Displayable d = AppUI.display.getCurrent();
							if(d != null && d instanceof Form) {
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
								((Form)d).setTitle(s);
							}
							Thread.sleep(1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
		}
	}
	
	private void checkUpdate() {
		try {
			String s = Util.getUtf(updateurl+
					"?v="+App.midlet.getAppProperty("MIDlet-Version")+
					"&l="+Locale.l+
					"&s="+(App.midlet.getAppProperty("JTube-Samsung-Build") != null ? "1" : "0")+
					"&p="+Util.url(PlatformUtils.platform)
					);
			JSONObject j = JSON.getObject(s);
			if(j.getBoolean("update_available", false) && Settings.checkUpdates) {
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
			e.printStackTrace();
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

	public static byte[] hproxy(String s) throws IOException {
		if(s.startsWith("/")) return Util.get(Settings.inv + s.substring(1));
		if(Settings.imgproxy == null || Settings.imgproxy.length() <= 1) return Util.get(s);
		//if(s.indexOf("ggpht.com") != -1) return Util.get(Util.replace(s, "https:", "http:"));
		return Util.get(Settings.imgproxy + Util.url(s));
	}
	
	public static AbstractJSON invApi(String s) throws InvidiousException, IOException {
		return invApi(s, null);
	}

	public static AbstractJSON invApi(String s, String fields) throws InvidiousException, IOException {
		String url = s;
		if(!s.endsWith("?")) s = s.concat("&");
		s = s.concat("Settings.region=" + Settings.region);
		if(fields != null) {
			s = s.concat("&fields=" + fields + ",error,errorBacktrace,code");
		}
		String dbg = "Settings.region=" + Settings.region + " Fields=" + fields;
		try {
			s = Util.getUtf(Settings.inv + "api/" + s);
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
				throw new InvidiousException((JSONObject) res, null, url, dbg);
			}
		} else {
			res = JSON.getArray(s);
		}
		return res;
	}
	
	public static boolean needsCheckMemory() {
		return Settings.isLowEndDevice() && !Settings.videoPreviews;
	}
	
	public static void checkMemoryAndGc() {
		Runtime r = Runtime.getRuntime();
		if(r.freeMemory() > r.totalMemory() - 500 * 1024) {
			Util.gc();
		}
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
		JSONObject j = (JSONObject) invApi("v1/videos/"  + id + "?", (combined ? "formatStreams" : "adaptiveFormats"));
		JSONArray arr = j.getArray(combined ? "formatStreams" : "adaptiveFormats");
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
				s = "http://iteroni.com" + s.substring(i);
			} else {
				s = Settings.serverstream + Util.url(s);
			}
		}
		return s;
	}

	public static String getVideoLink(String id, String res) throws JSONException, IOException {
		return getVideoLink(id, res, false);
	}
	
	public static void download(final String id) {
		Downloader d = new Downloader(id, Settings.videoRes, Settings.downloadDir);
		d.start();
	}
	public static void download(String[] vids) {
		Downloader d = new Downloader(vids, Settings.videoRes, Settings.downloadDir);
		d.start();
	}
	
	public static void watch(final String id) {
		/*
		try {
			String url = getVideoLink(id, videoRes);
			platReq(url);
		} catch (Exception e) {
			e.printStackTrace();
			error(null, Errors.App_watch, e);
		}
		*/
		inst.stopDoingAsyncTasks();
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
			/*
			case 1: {
				Player p = Manager.createPlayer(url);
				playerCanv = new PlayerCanvas(p);
				AppUI.display(playerCanv);
				playerCanv.init();
				break;
			}
			*/
			case 1: {
				String url = getVideoLink(id, Settings.videoRes, true);
				String file = "file:///" + Settings.downloadDir;
				if (!file.endsWith("/") && !file.endsWith("\\"))
					file += "/";
				if (PlatformUtils.isSymbianTouch() || PlatformUtils.isBada()) {
					file += "watch.ram";
				} else if (PlatformUtils.isS603rd()) {
					file += "watch.m3u";
				} else {
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
					o.write((url).getBytes("UTF-8"));
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
				//platReq("https://next.2yxa.mobi/mov.php?id=" + id + "&type=" + type + "&poisk=you&dw");
				Util.platReq("https://next.2yxa.mobi/mov.php?id=" + id + "&poisk=you" + (Locale.localei != 1 ? "&lang=en" : ""));
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(null, Errors.App_watch, e);
		}
	}
	
	public void addAsyncLoad(ILoader v) {
		if(v == null) throw new NullPointerException("l");
		synchronized(lazyLoadLock) {
			if(v1 == null) {
				v0.addElement(v);
			} else {
				int s0 = v0.size();
				int s1 = v1.size();
				int s2 = v2.size();
				if(s0 < s1) {
					v0.addElement(v);
				} else if(s1 < s2) {
					v1.addElement(v);
				} else {
					v2.addElement(v);
				}
			}
		}
	}
	
	public void notifyAsyncTasks() {
		synchronized(lazyLoadLock) {
			lazyLoadLock.notifyAll();
		}
	}
	
	void waitAsyncTasks() {
		synchronized(lazyLoadLock) {
			lazyLoadLock.notifyAll();
		}
		try {
			synchronized(addLock) {
				addLock.wait();
			}
		} catch (Exception e) {
		}
	}

	public void stopDoingAsyncTasks() {
		if(t0 != null) t0.pleaseInterrupt();
		if(t1 != null) t1.pleaseInterrupt();
		if(t2 != null) t2.pleaseInterrupt();
		if(v0 != null) v0.removeAllElements();
		if(v1 != null) v1.removeAllElements();
		if(v2 != null) v2.removeAllElements();
		waitAsyncTasks();
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
		String cls = "";
		if(o != null) cls = "at " + o.getClass().getName();
		String s = str + " \n\n" + cls + " \nt:" + Thread.currentThread().getName();
		Alert a = new Alert("", s, null, AlertType.WARNING);
		a.setTimeout(-2);
		inst.ui.display(a);
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
		String cls = "null";
		if(o != null) cls = o.getClass().getName();
		String s = str + " \n\ne: " + i + " \nat " + cls + " \nt: " + Thread.currentThread().getName() + (str2 != null ? " \n" + str2 : "");
		Alert a = new Alert("", s, null, AlertType.ERROR);
		a.setTimeout(-2);
		inst.ui.display(a);
	}


}
