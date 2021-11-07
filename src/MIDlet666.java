import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import cc.nnproject.json.AbstractJSON;
import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONException;
import cc.nnproject.json.JSONObject;
import models.VideoModel;

public class MIDlet666 extends MIDlet {

	private static final String proxy = "http://nnproject.cc/proxy.php?";
	private static final String hproxy = "http://nnproject.cc/hproxy.php?";
	private static final String inv = "https://invidious.snopyta.org/";

	private static boolean started;
	private static boolean destroyed;
	
	public static int width;
	public static int height;
	private static boolean httpsNotSupported;
	
	public static MIDlet666 midlet;
	
	public Vector models;
	
	public Form mainForm;

	private TextField searchText;

	private StringItem searchBtn;
	
	private String region;
	
	private Object lazyLoadLock = new Object();
	
	private Thread lazyLoadThread = new Thread() {
		public void run() {
			try {
				while(!destroyed) { 
					synchronized(lazyLoadLock) {
						lazyLoadLock.wait();
					}
					int len = models.size();
					for(int i = 0; i < len; i++) {
						VideoModel v = (VideoModel) models.elementAt(i);
						v.loadImage();
					}
				}
			} catch (Exception e) {
			}
		}
	};

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
		lazyLoadThread.start();
		testCanvas();
		initForm();
		loadTrends();
	}

	private void testCanvas() {
		Canvas c = new Canvas() {
			protected void paint(Graphics g) { }
			
			public void pointerPressed(int x, int y) {
				// для того чтобы симбиан поняла что виртуальные кнопки пихать не надо
			}
		};
		c.setFullScreenMode(true);
		display(c);
		width = c.getWidth();
		height = c.getHeight();
	}

	private void initForm() {
		mainForm = new Form("test");
		searchText = new TextField("", "", 256, TextField.ANY);
		searchText.setLayout(Item.LAYOUT_SHRINK | Item.LAYOUT_LEFT | Item.LAYOUT_2);
		mainForm.append(searchText);
		searchBtn = new StringItem("", "Поиск", StringItem.BUTTON);
		searchBtn.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_RIGHT | Item.LAYOUT_2);
		mainForm.append(searchBtn);
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

	private AbstractJSON invApi(String s) throws InvidiousException, IOException {
		s = hproxyUtf(inv + "api/" + s);
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
		models = new Vector();
		try {
			JSONArray j = (JSONArray) invApi("v1/trending?region=" + region);
			for(int i = 0; i < j.size(); i++) {
				VideoModel v = new VideoModel(j.getObject(i));
				models.addElement(v);
				mainForm.append(v.makeImageItem());
			}
			synchronized(lazyLoadLock) {
				lazyLoadLock.notifyAll();
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openVideo(String id) {
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
		midlet.openVideo(v.getVideoId());
	}

}
