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
import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONArray;

public class LocalStorage {
	
	private static Vector subscriptions;

	public static void cacheThumbnail(String id, byte[] b) {
		try {
			RecordStore.deleteRecordStore("jС"+id);
		} catch (Exception e) {
		}
		try {
			RecordStore rs = RecordStore.openRecordStore("jС"+id, true);
			rs.addRecord(b, 0, b.length);
			rs.closeRecordStore();
		} catch (Exception e) {
		}
	}
	
	public static void cacheThumbnail(String id, String url) {
		try {
			RecordStore.deleteRecordStore("jС"+id);
		} catch (Exception e) {
		}
		try {
			RecordStore rs = RecordStore.openRecordStore("jС"+id, true);
			byte[] b = App.getImageBytes(url);
			rs.addRecord(b, 0, b.length);
			b = null;
			rs.closeRecordStore();
		} catch (Exception e) {
		}
	}
	
	public static Image loadAndCacheThumnail(String id, String url) throws IOException {
		try {
			byte[] b = null;
			RecordStore rs = null;
			try {
				rs = RecordStore.openRecordStore("jС"+id, true);
				if(rs.getNumRecords() > 0) {
					b = rs.getRecord(1);
				}
			} catch (Exception e) {
			}
			if(b == null) {
				if(url == null) {
					return null;
				}
				b = App.getImageBytes(url);
				if(rs != null) {
					try {
						if(rs.getNumRecords() <= 0) {
							rs.addRecord(b, 0, b.length);
						}
					} catch (Exception e) {
					}
				}
			}
			if(rs != null) {
				try {
					rs.closeRecordStore();
				} catch (Exception e) {
				}
			}
			return Image.createImage(b, 0, b.length);
		} catch (IOException e) {
			throw e;
		}
	}
	
	public static void init() {
		initSubscriptions();
	}
	
	private static void initSubscriptions() {
		try {
			RecordStore subsRS = RecordStore.openRecordStore("jtsubscriptions", true);
			if(subsRS.getNumRecords() > 0) {
				String s = new String(subsRS.getRecord(1), "UTF-8");
				subscriptions = JSON.getArray(s).getVector();
				// remove repeats
				for(int i = subscriptions.size()-2; i > 0; i-=2) {
					int idx = subscriptions.indexOf(subscriptions.elementAt(i));
					if(idx != i) {
						subscriptions.removeElementAt(i+1);
						subscriptions.removeElementAt(i);
					}
				}
				byte[] b = new JSONArray(subscriptions).build().getBytes("UTF-8");
				subsRS.setRecord(1, b, 0, b.length);
			} else {
				subscriptions = new Vector();
				subsRS.addRecord("[]".getBytes(), 0, 2);
			}
			subsRS.closeRecordStore();
		} catch (Exception e) {
			subscriptions = new Vector();
			e.printStackTrace();
		}
	}
	
	public static void saveSubscriptions() {
		long l = System.currentTimeMillis();
		try {
			RecordStore subsRS = RecordStore.openRecordStore("jtsubscriptions", true);
			byte[] b = new JSONArray(subscriptions).build().getBytes("UTF-8");
			subsRS.setRecord(1, b, 0, b.length);
			subsRS.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("save took " + (System.currentTimeMillis() - l));
	}
	
	// TODO
	
	public static void addSubscription(String id, String name) {
		subscriptions.addElement(id);
		subscriptions.addElement(name);
		saveSubscriptions();
	}
	
	public static boolean isSubscribed(String id) {
		return subscriptions.contains(id);
	}
	
	public static String[] getSubsciptions() {
		String[] arr = new String[subscriptions.size()];
		subscriptions.copyInto(arr);
		return arr;
	}
	
	public static String[] getSubsciptionIds() {
		String[] arr = new String[subscriptions.size() >> 1];
		for(int i = 0; i < arr.length; i++) {
			arr[i] = (String) subscriptions.elementAt(i * 2);
		}
		return arr;
	}
	
	public static String[] getSubsciptionNames() {
		String[] arr = new String[subscriptions.size() >> 1];
		for(int i = 0; i < arr.length; i++) {
			arr[i] = (String) subscriptions.elementAt((i * 2) + 1);
		}
		return arr;
	}
	
	public static void removeSubscription(String id) {
		int idx = subscriptions.indexOf(id);
		subscriptions.removeElementAt(idx+1);
		subscriptions.removeElementAt(idx);
		saveSubscriptions();
	}
	
	public static void addHistory(String id, String name) {
	}
	
	public static boolean isWatched(String id) {
		return false;
	}
	
	public static String[] getHistory(int tab) {
		return new String[0];
	}
	
	public static void clearHistory() {
	}
	
	public static void addLiked(String id, String name) {
	}
	
	public static boolean isLiked(String id) {
		return false;
	}
	
	public static String[] getLiked(int tab) {
		return new String[0];
	}
	
	public static void removeLiked(String id) {
	}
	
	public static void clearAllData() {
		try {
			RecordStore.deleteRecordStore("jtsubscriptions");
		} catch (Exception e) {
		}
	}

}
