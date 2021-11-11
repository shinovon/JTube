import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;

import cc.nnproject.json.AbstractJSON;
import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONException;
import cc.nnproject.json.JSONObject;
import models.ILoader;
import models.VideoModel;

public class App extends MIDlet implements CommandListener, ItemCommandListener, Constants {

	private static boolean started;
	
	public static int width;
	public static int height;
	
	// Settings
	private static String videoRes;
	private static String region;
	private static int watchMethod; // 0 - platform request 1 - mmapi player
	private static String downloadDir;
	private static String servergetlinks = getlinksphp;
	
	public static App midlet;
	
	
	public VideoModel[] models;
	
	public Form mainForm;
	public Form searchForm;
	public Form settingsForm;
	private ChoiceGroup videoResChoice;
	private TextField regionText;
	private TextField downloadDirText;
	//private TextField searchText;
	//private StringItem searchBtn;
	private Form videoForm;
	private VideoModel video;
	private PlayerCanvas playerCanv;
	
	private Object lazyLoadLock = new Object();
	private LoaderThread t0;
	private LoaderThread t1;
	private LoaderThread t2;
	private boolean asyncLoading = true;
	public Vector v0;
	private Vector v1;
	private Vector v2;

	private TextField serverText;


	protected void destroyApp(boolean b) {}

	protected void pauseApp() {}

	protected void startApp() {
		if(started) return;
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

	static JSONObject getVideoInfo(String id, String res) throws JSONException, IOException {
		JSONArray j = JSON.getArray(Util.getUtf(servergetlinks + "?url=" + Util.url("https://www.youtube.com/watch?v="+id)));
		if(j.size() == 0) {
			throw new RuntimeException("failed to get link for video: " + id);
		}
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
		System.out.println("preferred video res: " + res);
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
	}

	private void openVideo(VideoModel v) {
		video = v;
		videoForm = new Form(v.getTitle());
		videoForm.setCommandListener(this);
		videoForm.addCommand(backCmd);
		videoForm.addCommand(downloadCmd);
		//videoForm.addCommand(browserCmd);
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
	
	private void download(final String id) {
		Downloader d = new Downloader(id, videoRes, videoForm, downloadDir);
		d.start();
	}
	
	public void watch(String id) {
		// TODO
		try {
			JSONObject o = getVideoInfo(id, videoRes);
			String url = o.getString("url");
			switch(watchMethod) {
			case 0: {
				platReq(url);
				break;
			}
			case 1: {
				Player p = Manager.createPlayer(url);
				playerCanv = new PlayerCanvas(p);
				display(playerCanv);
				playerCanv.init();
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg(e.toString());
		}
	}
	
	public static void platReq(String s) throws ConnectionNotFoundException {
		midlet.platformRequest(s);
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
				regionText = new TextField("Country code (ISO 3166)", region, 3, TextField.ANY);
				settingsForm.append(regionText);
				downloadDirText = new TextField("Download directory", downloadDir, 256, TextField.ANY);
				settingsForm.append(downloadDirText);
				serverText = new TextField("Get Links PHP server", servergetlinks, 256, TextField.URL);
				settingsForm.append(serverText);
				StringItem s = new StringItem("See:", "github.com/shinovon/название", StringItem.HYPERLINK);
				settingsForm.append(s);
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
		/*if(c == browserCmd) {
			try {
				platReq(getVideoInfo(video.getVideoId(), videoRes).getString("url"));
			} catch (Exception e) {
				e.printStackTrace();
				msg(e.toString());
			}
		}*/
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
		// Defaults
		videoRes = "360p";
		downloadDir = System.getProperty("fileconn.dir.video");
		if(downloadDir == null)
			downloadDir = System.getProperty("fileconn.dir.photo");
		if(downloadDir.startsWith("file:///"))
			downloadDir = downloadDir.substring("file:///".length());
		try {
			RecordStore r = RecordStore.openRecordStore(CONFIG_RECORD_NAME, false);
			JSONObject j = JSON.getObject(new String(r.getRecord(1), "UTF-8"));
			r.closeRecordStore();
			if(j.has("videoRes"))
				videoRes = j.getString("videoRes");
			if(j.has("region"))
				region = j.getString("region");
			if(j.has("downloadDir"))
				downloadDir = j.getString("downloadDir");
			if(j.has("servergetlinks"))
				servergetlinks = j.getString("servergetlinks");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			j.put("region", region);
			j.put("downloadDir", downloadDir);
			j.put("servergetlinks", servergetlinks);
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
