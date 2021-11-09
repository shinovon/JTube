import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;

import cc.nnproject.json.AbstractJSON;
import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONException;
import cc.nnproject.json.JSONObject;
import models.ILoader;
import models.VideoModel;

public class MIDlet666 extends MIDlet implements CommandListener, ItemCommandListener {

	private static final String getlinksphp = "http://nnproject.cc/getlinks.php";
	private static final String hproxy = "http://nnproject.cc/hproxy.php?";
	private static final String inv = "http://iteroni.com/";
	
	private static final String CONFIG_RECORD_NAME = "ytconfig";
	
	private static final Command searchOkCmd = new Command("Search", Command.OK, 1);
	private static final Command goCmd = new Command("Go", Command.OK, 1);
	private static final Command settingsCmd = new Command("Settings", Command.SCREEN, 1);
	private static final Command searchCmd = new Command("Search", Command.SCREEN, 2);
	private static final Command idCmd = new Command("Open by ID", Command.SCREEN, 3);
	private static final Command cancelCmd = new Command("Cancel", Command.CANCEL, 2);
	private static final Command backCmd = new Command("Back", Command.BACK, 1);
	private static final Command watchCmd = new Command("Watch", Command.OK, 1);
	private static final Command downloadCmd = new Command("Download", Command.SCREEN, 2);
	private static final Command exitCmd = new Command("Exit", Command.EXIT, 2);
	
	private static final int TRENDS_LIMIT = 20;
	private static final int SEARCH_LIMIT = 20; 
	
	private static final String NAME = "Some";
	private static final String[] VIDEO_QUALITIES = new String[] { "144p", "360p", "720p" };

	private static boolean started;
	
	public static int width;
	public static int height;
	
	// Settings
	public String videoRes;
	
	public static MIDlet666 midlet;
	
	private static String region;
	
	public Vector v0;
	
	public VideoModel[] models;
	
	public Form mainForm;
	public Form searchForm;
	public Form settingsForm;
	//private TextField searchText;
	//private StringItem searchBtn;
	
	
	private Object lazyLoadLock = new Object();
	private LoaderThread t0;
	private LoaderThread t1;
	private LoaderThread t2;
	
	private boolean asyncLoading = true;
	private Form videoForm;
	private VideoModel video;
	private Vector v1;
	private Vector v2;
	private ChoiceGroup videoResChoice;
	private PlayerCanvas playerCanv;

	protected void destroyApp(boolean b) {}

	protected void pauseApp() {}

	protected void startApp() {
		if(started) return;
		/*
		 * форматы команд:
		 * обязательный аргумент: url или vid или cid
		 * url - ссылка на канал или видео
		 * vid - ид видео
		 * cid - ид канала
		 * method - show / player / showvideos
		 * - show - если видео то показать страницу видео, если канал то показать страницу канала
		 * - player - если видео то открыть плеер видео, если канал то ничего не делать
		 * - showvideos - если видео то показать список рекомендаванных к видео, если канал то показать видео канала
		 */
		midlet = this;
		started = true;
		region = System.getProperty("user.country");
		if(region == null) {
			region = System.getProperty("microedition.locale");
			if(region == null) {
				region = "US";
			}
		}
		if(region.length() > 2) {
			region = region.substring(0, 2);
		}
		region = region.toUpperCase();
		v0 = new Vector();
		if(Runtime.getRuntime().totalMemory() != 2048 * 1024 * 1024 && asyncLoading) {
			v1 = new Vector();
			v2 = new Vector();
			t0 = new LoaderThread(5, lazyLoadLock, v0);
			t1 = new LoaderThread(5, lazyLoadLock, v1);
			t2 = new LoaderThread(5, lazyLoadLock, v2);
			t0.start();
			t1.start();
			t2.start();
		} else {
			t0 = new LoaderThread(5, lazyLoadLock, v0);
			t0.start();
		}
		models = new VideoModel[TRENDS_LIMIT + SEARCH_LIMIT + 2];
		testCanvas();
		initForm();
		loadConfig();
		loadTrends();
	}

	private void testCanvas() {
		Canvas c = new TestCanvas();
		display(c);
		width = c.getWidth();
		height = c.getHeight();
	}

	private void initForm() {
		mainForm = new Form(NAME + " - Trends");
		/*
		searchText = new TextField("", "", 256, TextField.ANY);
		searchText.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_2);
		mainForm.append(searchText);
		searchBtn = new StringItem(null, "Поиск", StringItem.BUTTON);
		searchBtn.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_RIGHT | Item.LAYOUT_2);
		searchBtn.addCommand(searchCmd);
		searchBtn.setDefaultCommand(searchCmd);
		mainForm.append(searchBtn);
		*/
		mainForm.setCommandListener(this);
		mainForm.addCommand(searchCmd);
		mainForm.addCommand(idCmd);
		mainForm.addCommand(settingsCmd);
		mainForm.addCommand(exitCmd);
		display(mainForm);
	}
	
	public static byte[] hproxy(String s) throws IOException {
		if(s.startsWith("/")) return Util.get(inv + s.substring(1));
		return Util.get(hproxy + Util.url(s));
	}

	public static AbstractJSON invApi(String s) throws InvidiousException, IOException {
		if(!s.endsWith("?")) s = s.concat("&");
		s = s.concat("region=" + region);
		s = Util.getUtf(inv + "api/" + s);
		AbstractJSON res;
		try {
			res = JSON.getObject(s);
			if(((JSONObject) res).has("code")) {
				throw new InvidiousException((JSONObject) res, ((JSONObject) res).getString("code") + ": " + ((JSONObject) res).getNullableString("message"));
			}
			if(((JSONObject) res).has("error")) {
				throw new InvidiousException((JSONObject) res);
			}
		} catch (JSONException e) {
			if(!e.getMessage().equals("Not JSON object")) throw e;
			try {
				res = JSON.getArray(s);
			} catch (JSONException e2) {
				e2.printStackTrace();
				throw e;
			}
		}
		return res;
	}

	private void loadTrends() {
		try {
			JSONArray j = (JSONArray) invApi("v1/trending?");
			for(int i = 0; i < j.size(); i++) {
				VideoModel v = new VideoModel(j.getObject(i));
				addAsyncLoad(v);
				models[i] = v;
				mainForm.append(v.makeImageItemForList());
				if(i >= TRENDS_LIMIT) break;
			}
			notifyAsyncTasks();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void search(String q) {
		searchForm = new Form(NAME + " - Search query");
		searchForm.setCommandListener(this);
		searchForm.addCommand(backCmd);
		display(searchForm);
		try {
			JSONArray j = (JSONArray) invApi("v1/search?q=" + Util.url(q));
			for(int i = 0; i < j.size(); i++) {
				VideoModel v = new VideoModel(j.getObject(i));
				v.setFromSearch();
				models[TRENDS_LIMIT + i] = v;
				addAsyncLoad(v);
				searchForm.append(v.makeImageItemForList());
				if(i >= SEARCH_LIMIT) break;
			}
			notifyAsyncTasks();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openVideo(String id) {
		final String https = "https://";
		final String ytshort = "youtu.be/";
		final String www = "www.";
		final String watch = "youtube.com/watch?v=";
		if(id.startsWith(https)) id = id.substring(https.length());
		if(id.startsWith(ytshort)) id = id.substring(ytshort.length());
		if(id.startsWith(www)) id = id.substring(www.length());
		if(id.startsWith(watch)) id = id.substring(watch.length());
		try {
			openVideo(new VideoModel(id).extend());
		} catch (Exception e) {
			msg(e.toString());
		}
	}

	private void openVideo(VideoModel v) {
		video = v;
		videoForm = new Form(v.getTitle());
		videoForm.setCommandListener(this);
		videoForm.addCommand(backCmd);
		videoForm.addCommand(downloadCmd);
		display(videoForm);
		try {
			v.extend();
		} catch (Exception e) {
			msg(e.toString());
		}
		ImageItem img = v.makeImageItemForPage();
		img.addCommand(watchCmd);
		img.setDefaultCommand(watchCmd);
		img.setItemCommandListener(this);
		videoForm.append(img);
		Item t = new StringItem(null, v.getTitle());
		t.setLayout(Item.LAYOUT_LEFT);
		videoForm.append(t);
		videoForm.append(v.makeAuthorItem());
		stopDoingAsyncTasks();
		addAsyncLoad(v);
		Item vi = new StringItem("Views", "" + v.getViewCount());
		vi.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		videoForm.append(vi);
		Item ld = new StringItem("Likes / Dislikes", "" + v.getLikeCount() + " / " + v.getDislikeCount());
		ld.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		videoForm.append(ld);
		videoForm.append(new StringItem("Description", v.getDescription()));
		notifyAsyncTasks();
	}
	
	private void download(String id) {

		FileConnection fc = null;
		OutputStream out = null;
		HttpConnection hc = null;
		InputStream in = null;
		try {
		Alert alert = new Alert("", "", null, AlertType.INFO);
		//alert.setCommandListener(this);
		alert.setTimeout(-2);
		alert.setTitle("Downloading");
		alert.setIndicator(new Gauge("", false, -1, Gauge.CONTINUOUS_RUNNING));
		display(alert);
		String file = System.getProperty("fileconn.dir.videos");
		if(file == null)
			file = System.getProperty("fileconn.dir.photos");
		file = file + id + ".mp4";
		alert.setString(id + ".mp4");
		System.out.println("Download: " + id);
			JSONArray j = JSON.getArray(Util.getUtf(getlinksphp + "?url=" + Util.url("https://www.youtube.com/watch?v="+id)));
			if(j.size() == 0) {
				msg("Failed!");
				return;
			}
			System.out.println(j);
			JSONObject _144 = null;
			JSONObject _360 = null;
			JSONObject _720 = null;
			JSONObject other = null;
			for(int i = 0; i < j.size(); i++) {
				JSONObject o = j.getObject(i);
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
			System.out.println("preferred video res: " + videoRes);
			JSONObject o = null;
			if(videoRes == null) {
				if(_360 != null) {
					o = _360;
				} else if(other != null) {
					o = other;
				} else if(_144 != null) {
					o = _144;
				} 
			} else if(videoRes.equals("144p")) {
				if(_144 != null) {
					o = _144;
				} else if(_360 != null) {
					o = _360;
				} else if(other != null) {
					o = other;
				}
			} else if(videoRes.equals("360p")) {
				if(_360 != null) {
					o = _360;
				} else if(other != null) {
					o = other;
				} else if(_144 != null) {
					o = _144;
				} 
			} else if(videoRes.equals("720p")) {
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
			String url = o.getString("url");
			alert.setString("Downloading 0%");
			fc = (FileConnection) Connector.open(file, Connector.READ_WRITE);

			if (fc.exists()) {
				try {
					fc.delete();
				} catch (IOException e) {
				}
			}
			fc.create();
			out = fc.openOutputStream();
			hc = (HttpConnection) Connector.open(url, Connector.READ);
			hc.setRequestProperty("User-Agent", "User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");

			int r = hc.getResponseCode();
			while (r == 301 || r == 302) {
				String redir = hc.getHeaderField("Location");
				if (redir.startsWith("/")) {
					String tmp = url.substring(url.indexOf("//") + 2);
					String host = url.substring(0, url.indexOf("//")) + "//" + tmp.substring(0, tmp.indexOf("/"));
					redir = host + redir;
				}
				hc.close();
				hc = (HttpConnection) Connector.open(redir);
				hc.setRequestMethod("GET");
				hc.setRequestProperty("User-Agent", "User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
				r = hc.getResponseCode();
			}
			in = hc.openInputStream();
			int i = 0;
			byte[] buf = new byte[128 * 1024];
			int read = 0;
			int downloaded = 0;
			int l = (int) hc.getLength();
			while((read = in.read(buf)) != -1) {
				out.write(buf, 0, read);
				downloaded += read;
				i = (int)(((double)downloaded / (double)l) * 100d);
				alert.setString("Downloading " + i + "%");
			}
			alert.setString("Done");
		} catch (Exception e) {
			e.printStackTrace();
			msg(e.toString());
		} finally {
			try {
				if(out != null) out.close();
			} catch (Exception e) {
			} 
			try {
				if(fc != null) fc.close();
			} catch (Exception e) {
			} 
			try {
				if(in != null) in.close();
			} catch (Exception e) {
			} 
			try {
				if(hc != null) hc.close();
			} catch (Exception e) {
			} 
		}
	}
	
	private void watch(String id) {
		// TODO
		System.out.println("Watch: " + id);
		try {
			JSONArray j = JSON.getArray(Util.getUtf(getlinksphp + "?url=" + Util.url("https://www.youtube.com/watch?v="+id)));
			if(j.size() == 0) {
				msg("Failed!");
				return;
			}
			System.out.println(j);
			JSONObject _144 = null;
			JSONObject _360 = null;
			JSONObject _720 = null;
			JSONObject other = null;
			for(int i = 0; i < j.size(); i++) {
				JSONObject o = j.getObject(i);
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
			System.out.println("preferred video res: " + videoRes);
			JSONObject o = null;
			if(videoRes == null) {
				if(_360 != null) {
					o = _360;
				} else if(other != null) {
					o = other;
				} else if(_144 != null) {
					o = _144;
				} 
			} else if(videoRes.equals("144p")) {
				if(_144 != null) {
					o = _144;
				} else if(_360 != null) {
					o = _360;
				} else if(other != null) {
					o = other;
				}
			} else if(videoRes.equals("360p")) {
				if(_360 != null) {
					o = _360;
				} else if(other != null) {
					o = other;
				} else if(_144 != null) {
					o = _144;
				} 
			} else if(videoRes.equals("720p")) {
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
			String url = o.getString("url");
			final String type = o.getString("cleanMimeType");
			int length = o.getInt("contentLength");
			System.out.println("Url: " + url);
			DataSource src = new AsyncLoadDataSource(url, type, length);
			Player p = Manager.createPlayer(src);
			playerCanv = new PlayerCanvas(p);
			display(playerCanv);
			playerCanv.init();
		} catch (Exception e) {
			e.printStackTrace();
			msg(e.toString());
		}
	}
	
	private void addAsyncLoad(ILoader v) {
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
	
	private void notifyAsyncTasks() {
		synchronized(lazyLoadLock) {
			lazyLoadLock.notifyAll();
		}
	}

	private void stopDoingAsyncTasks() {
		synchronized(lazyLoadLock) {
			if(t0 != null) t0.pleaseInterrupt();
			if(t1 != null) t1.pleaseInterrupt();
			if(t2 != null) t2.pleaseInterrupt();
			v0.removeAllElements();
		}
	}

	public static void msg(String s) {
		Alert a = new Alert("", s, null, null);
		a.setTimeout(-2);
		display(a);
	}
	
	public static void display(Displayable d) {
		Display.getDisplay(midlet).setCurrent(d);
	}

	public static void open(VideoModel v) {
		midlet.openVideo(v);
	}

	public void commandAction(Command c, Displayable d) {
		if(c == exitCmd) {
			notifyDestroyed();
		}
		if(c == settingsCmd) {
			if(settingsForm == null) {
				settingsForm = new Form("Settings");
				settingsForm.addCommand(backCmd);
				settingsForm.setCommandListener(this);
				videoResChoice = new ChoiceGroup("Preferred video quality", ChoiceGroup.EXCLUSIVE, VIDEO_QUALITIES, null);
				settingsForm.append(videoResChoice);
			}
			display(settingsForm);
			if(videoRes == null) {
				videoResChoice.setSelectedIndex(1, true);
			} else if(videoRes.equals("144p")) {
				videoResChoice.setSelectedIndex(0, true);
			} else if(videoRes.equals("360p")) {
				videoResChoice.setSelectedIndex(1, true);
			} else if(videoRes.equals("720p")) {
				videoResChoice.setSelectedIndex(2, true);
			}
		}
		if(c == backCmd && d == settingsForm) {
			saveConfig();
			display(mainForm);
		}
		if(c == watchCmd) {
			watch(video.getVideoId());
		}
		if(c == searchCmd && d instanceof Form) {
			TextBox t = new TextBox("", "", 256, TextField.ANY);
			t.setCommandListener(this);
			t.setTitle("Search");
			t.addCommand(searchOkCmd);
			t.addCommand(cancelCmd);
			display(t);
		}
		if(c == idCmd && d instanceof Form) {
			TextBox t = new TextBox("", "", 256, TextField.ANY);
			t.setCommandListener(this);
			t.setTitle("Video URL or ID");
			t.addCommand(goCmd);
			t.addCommand(cancelCmd);
			display(t);
		}
		if(c == downloadCmd) {
			download(video.getVideoId());
		}
		if(c == cancelCmd && d instanceof TextBox) {
			display(mainForm);
		}
		if(c == backCmd && d == searchForm) {
			display(mainForm);
			disposeSearchForm();
		}
		if(c == backCmd && d == videoForm) {
			if(video.isFromSearch()) {
				display(searchForm);
			} else {
				display(mainForm);
			}
			disposeVideoForm();
		}
		if(c == goCmd && d instanceof TextBox) {
			openVideo(((TextBox) d).getString());
		}
		if(c == searchOkCmd && d instanceof TextBox) {
			search(((TextBox) d).getString());
		}
	}

	private void loadConfig() {
		try {
			RecordStore r = RecordStore.openRecordStore(CONFIG_RECORD_NAME, false);
			JSONObject j = JSON.getObject(new String(r.getRecord(1), "UTF-8"));
			r.closeRecordStore();
			videoRes = j.getString("videoRes");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		videoRes = "360p";
	}
	private void saveConfig() {
		if(videoResChoice.getSelectedIndex() == 0) {
			videoRes = "144p";
		} else if(videoResChoice.getSelectedIndex() == 1) {
			videoRes = "360p";
		} else if(videoResChoice.getSelectedIndex() == 2) {
			videoRes = "720p";
		}
		try {
			RecordStore.deleteRecordStore(CONFIG_RECORD_NAME);
		} catch (Exception e) {
		}
		try {
			RecordStore r = RecordStore.openRecordStore(CONFIG_RECORD_NAME, true);
			JSONObject j = new JSONObject();
			j.put("v", "v1");
			j.put("videoRes", videoRes);
			byte[] b = j.build().getBytes("UTF-8");
			
			r.addRecord(b, 0, b.length);
			r.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void disposeVideoForm() {
		video.disposeExtendedVars();
		video = null;
		videoForm = null;
	}

	private void disposeSearchForm() {
		searchForm = null;
	}

	public void commandAction(Command c, Item item) {
		if(c == watchCmd) {
			watch(video.getVideoId());
		}
	}

}
