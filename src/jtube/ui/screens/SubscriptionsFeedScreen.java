/*
Copyright (c) 2023 Arman Jussupgaliyev

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
package jtube.ui.screens;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import jtube.App;
import jtube.Constants;
import jtube.Loader;
import jtube.LocalStorage;
import jtube.Settings;
import jtube.models.VideoModel;
import jtube.ui.Locale;
import jtube.ui.items.Label;

public class SubscriptionsFeedScreen extends NavigationScreen implements Runnable, Constants {

	private int lastCount;
	private String[] subscriptions;
	private Thread thread;
	private boolean loaded;

	public SubscriptionsFeedScreen() {
		super(Locale.s(TITLE_Subscriptions));
		menuOptions = new String[] { Locale.s(CMD_ChannelsList) };
	}
	
	protected void show() { 
		super.show();
		if(lastCount != LocalStorage.getSubsciptions().length || !loaded) {
			Loader.stop();
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void hide() {
		busy = false;
		Loader.stop();
		if(thread != null) {
			thread.interrupt();
		}
		super.hide();
	}

	public void run() {
		lastCount = 0;
		busy = true;
		loaded = false;
		subscriptions = LocalStorage.getSubsciptions();
		if(subscriptions == null || subscriptions.length == 0) {
			busy = false;
			return;
		}
		try {
			Vector videos = new Vector(subscriptions.length * 5);
			for(int i = 0; i < subscriptions.length; i+=2) {
				JSONArray j = ((JSONObject) App.invApi("channels/" + subscriptions[i] + "/latest?", "videos(" + VIDEO_FIELDS + ",published)")).getArray("videos");
				for(int k = 0; k < j.size() && k < 10; k++) {
					videos.addElement(j.get(k));
				}
				Thread.sleep(1);
			}
			// sorting
			int l = videos.size();
			for (int i = 0; i < l; i++) {
				for (int j = i + 1; j < l; j++) {
					JSONObject t1 = (JSONObject) videos.elementAt(i);
					JSONObject t2 = (JSONObject) videos.elementAt(j);
					if (t1.getLong("published") < t2.getLong("published")) {
						videos.setElementAt(t2, i);
						videos.setElementAt(t1, j);
					}
				}
			}
			Date lastDate = null;
			for(int i = 0; i < videos.size() && i < 100; i++) {
				JSONObject j = (JSONObject) videos.elementAt(i);
				VideoModel v = new VideoModel(j);
				Date date = new Date(j.getLong("published") * 1000L);
				if(lastDate == null || !dateEqual(date, lastDate)) {
					lastDate = date;
					add(new Label(dateStr(date), smallfont));
				}
				if(Settings.videoPreviews && !Settings.lazyLoad) Loader.add(v);
				add(v.makeListItem());
			}
			loaded = true;
			lastCount = subscriptions.length;
			subscriptions = null;
		} catch (Exception e) {
		}
		busy = false;
	}
	
	private static boolean dateEqual(Date a, Date b) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(a);
		Calendar cb = Calendar.getInstance();
		cb.setTime(b);
		return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) && 
				ca.get(Calendar.MONTH) == cb.get(Calendar.MONTH) && 
				ca.get(Calendar.DAY_OF_MONTH) == cb.get(Calendar.DAY_OF_MONTH);
	}
	
	private static String dateStr(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c(c.get(Calendar.DAY_OF_MONTH)) + "." + c(c.get(Calendar.MONTH)+1) + "." + c.get(Calendar.YEAR);
	}
	
	private static String c(int i) {
    	String s = String.valueOf(i);
    	if(s.length() < 2) s = "0" + s;
		return s;
	}
	
	protected void menuAction(int action) {
		switch(action) {
		case 0:
			ui.nextScreen(new SubscriptionsListScreen());
			break;
		}
	}

}
