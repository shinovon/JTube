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
import jtube.LoaderThread;
import jtube.LocalStorage;
import jtube.Settings;
import jtube.models.ILoader;
import jtube.models.VideoModel;
import jtube.ui.Locale;
import jtube.ui.items.Label;

public class SubscriptionsFeedScreen extends NavigationScreen implements Runnable, Constants, ILoader {

	private int lastCount;
	private Vector allVideos = new Vector();
	private String[] subscriptions;
	private int idx;
	private Object lock = new Object();
	private Thread thread;
	private boolean loaded;
	private boolean interrupt;

	public SubscriptionsFeedScreen() {
		super(Locale.s(TITLE_Subscriptions));
		menuOptions = new String[] {
				Locale.s(CMD_ChannelsList)
		};
	}
	
	protected void show() { 
		super.show();
		if((lastCount != LocalStorage.getSubsciptions().length || !loaded) && !busy) {
			Loader.stop();
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void hide() {
		interrupt = true;
		busy = false;
		Loader.stop();
		if(thread != null) {
			thread.interrupt();
		}
		allVideos.removeAllElements();
		super.hide();
	}

	public void run() {
		lastCount = 0;
		busy = true;
		loaded = false;
		idx = 0;
		subscriptions = LocalStorage.getSubsciptions();
		if(subscriptions == null || subscriptions.length == 0) {
			busy = false;
			return;
		}
		try {
			allVideos.removeAllElements();
			Loader.synchronize();
			Thread.sleep(200);
			if(interrupt) return;
			for(int i = 0; i < subscriptions.length; i+=2) {
				Loader.add(this);
			}
			Loader.start();
			// wait till all channels load
			synchronized(lock) {
				lock.wait();
			}
			if(interrupt) return;
			// sorting
			int l = allVideos.size();
			for (int i = 0; i < l; i++) {
				for (int j = i + 1; j < l; j++) {
					JSONObject t1 = (JSONObject) allVideos.elementAt(i);
					JSONObject t2 = (JSONObject) allVideos.elementAt(j);
					if (t1.getLong("published") < t2.getLong("published")) {
						allVideos.setElementAt(t2, i);
						allVideos.setElementAt(t1, j);
					}
				}
			}
			Date lastDate = null;
			for(int i = 0; i < allVideos.size(); i++) {
				if(i > 100) break;
				JSONObject j = (JSONObject) allVideos.elementAt(i);
				VideoModel v = new VideoModel(j);
				Date date = new Date(j.getLong("published") * 1000L);
				if(lastDate == null || !dateEqual(date, lastDate)) {
					lastDate = date;
					add(new Label(dateStr(date), smallfont));
				}
				if(Settings.videoPreviews && !Settings.lazyLoad) Loader.add(v);
				add(v.makeListItem());
			}
			allVideos.removeAllElements();
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

	public void load() {
		try {
			if(idx*2 >= subscriptions.length) return;
			int i;
			synchronized(subscriptions) {
				i = idx++;
			}
			JSONObject r = (JSONObject) App.invApi("channels/" + subscriptions[i * 2] + "/latest?", "videos(" + VIDEO_FIELDS + ",published)");
			JSONArray j = r.getArray("videos");
			if(((LoaderThread) Thread.currentThread()).checkInterrupted()) {
				interrupt = true;
				synchronized(lock) {
					lock.notify();
				}
				throw new RuntimeException("loader interrupt");
			}
			for(int k = 0; k < j.size() && k < 10; k++) {
				allVideos.addElement(j.get(k));
			}
			if((++i)*2 >= subscriptions.length) {
				synchronized(lock) {
					lock.notify();
				}
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
		}
	}
	
	protected void menuAction(int action) {
		switch(action) {
		case 0:
			ui.nextScreen(new SubscriptionsListScreen());
			break;
		}
	}

}
