import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
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
import javax.microedition.midlet.MIDlet;

import cc.nnproject.json.AbstractJSON;
import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONException;
import cc.nnproject.json.JSONObject;
import models.ILoader;
import models.VideoModel;

public class MIDlet666 extends MIDlet implements CommandListener, ItemCommandListener {

	private static final String proxy = "http://nnproject.cc/proxy.php?";
	private static final String hproxy = "http://nnproject.cc/hproxy.php?";
	private static final String getlinksphp = "http://nnproject.cc/getlinks.php";
	private static final String inv = "https://invidious.snopyta.org/";
	
	private static final Command searchCmd = new Command("Search", Command.SCREEN, 1);
	private static final Command searchOkCmd = new Command("Search", Command.OK, 1);
	private static final Command goCmd = new Command("Go", Command.OK, 1);
	private static final Command idCmd = new Command("Open by ID", Command.SCREEN, 2);
	private static final Command cancelCmd = new Command("Cancel", Command.CANCEL, 2);
	private static final Command backCmd = new Command("Back", Command.BACK, 1);
	private static final Command watchCmd = new Command("Watch", Command.OK, 1);
	private static final Command exitCmd = new Command("Exit", Command.EXIT, 2);
	
	private static final int TRENDS_LIMIT = 20;
	private static final int SEARCH_LIMIT = 20; 
	
	private static final String NAME = "Some";

	private static boolean started;
	private static boolean destroyed;
	
	public static int width;
	public static int height;
	private static boolean httpsNotSupported;
	
	public static MIDlet666 midlet;
	
	private static String region;
	
	public Vector v0;
	
	public VideoModel[] models;
	
	public Form mainForm;
	//private TextField searchText;
	//private StringItem searchBtn;
	
	public Form searchForm;
	
	private Object lazyLoadLock = new Object();
	private LoaderThread t0;
	private LoaderThread t1;
	private LoaderThread t2;
	
	private boolean asyncLoading = true;
	private Form videoForm;
	private VideoModel video;
	private Vector v1;
	private Vector v2;

	protected void destroyApp(boolean b) {
		destroyed = true;
	}

	protected void pauseApp() {

	}

	protected void startApp() {
		if(started) return;
		midlet = this;
		started = true;
		region = System.getProperty("user.country");
		if(region == null) {
			region = System.getProperty("microedition.locale");;
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
		mainForm.addCommand(exitCmd);
		display(mainForm);
	}
	
	public static String proxy(String s) throws IOException {
		if(!httpsNotSupported) {
			try {
				return Util.getUtf(s);
			} catch (IOException e) {
				e.printStackTrace();
				httpsNotSupported = true;
			}
		}
		return Util.getUtf(proxy + Util.url(s));
	}
	
	public static byte[] hproxy(String s) throws IOException {
		return Util.get(hproxy + Util.url(s));
	}
	
	public static String hproxyUtf(String s) throws IOException {
		return Util.getUtf(hproxy + Util.url(s));
	}

	public static AbstractJSON invApi(String s) throws InvidiousException, IOException {
		if(!s.endsWith("?")) s = s.concat("&");
		s = s.concat("region=" + region);
		s = proxy(inv + "api/" + s);
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
		videoForm.addCommand(watchCmd);
		display(videoForm);
		try {
			v.extend();
		} catch (Exception e) {
			msg(e.toString());
		}
		ImageItem img = v.makeImageItemForPage();
		img.addCommand(watchCmd);
		img.addCommand(backCmd);
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
	
	private void watch(String id) {
		// TODO
		System.out.println("Watch: " + id);
		try {
			JSONArray j = JSON.getArray(Util.getUtf(getlinksphp + "?url=" + Util.url("https://www.youtube.com/watch?v="+id)));
			if(j.size() == 0) msg("Failed!");
			System.out.println(j);
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
