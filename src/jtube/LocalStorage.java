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
package jtube;

import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONArray;

public class LocalStorage {
	
	// constants
	private static final String SUBSCRIPTIONS = "jtsubscriptions";
	private static final String AVATARS_INDEX = "jtavaindex";
	private static final String THUMBNAIL_PREFIX = "jC";
	private static final String AVATAR_PREFIX = "jtava.";
	
	private static JSONArray subscriptions;
	private static JSONArray avatars;
	private static int avatarsLastIndex;

	public static void cacheThumbnail(String id, byte[] b) {
		try {
			RecordStore rs = RecordStore.openRecordStore(THUMBNAIL_PREFIX + id, true);
			setOrAdd(rs, 1, b);
			rs.closeRecordStore();
		} catch (Exception e) {
		}
	}
	
	public static void cacheThumbnail(String id, String url) {
		try {
			cacheThumbnail(id, App.getImageBytes(url));
		} catch (Exception e) {
		}
	}
	
	public static Image loadAndCacheThumnail(String id, String url) throws IOException {
		try {
			byte[] b = null;
			RecordStore rs = null;
			try {
				rs = RecordStore.openRecordStore(THUMBNAIL_PREFIX + id, true);
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
						setOrAdd(rs, 1, b);
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
	
	public static Image getAvatar(String id) {
		int i = avatars.indexOf(id);
		if(i != -1) {
			try {
				RecordStore rs = RecordStore.openRecordStore(AVATAR_PREFIX.concat(Integer.toHexString(avatars.getInt(i + 1))), false);
				byte[] b = rs.getRecord(1);
				rs.closeRecordStore();
				return Image.createImage(b, 0, b.length);
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	public static void saveAvatar(String id, byte[] b) {
		if(avatars.has(id)) return;
		avatars.add(id);
		int i;
		avatars.add(i = ++avatarsLastIndex);
		try {
			RecordStore rs = RecordStore.openRecordStore(AVATAR_PREFIX.concat(Integer.toHexString(i)), true);
			setOrAdd(rs, 1, b);
			rs.closeRecordStore();
		} catch (Exception e) {
		}
		saveAvatars();
	}
	
	public static void clearCache() {
		try {
			String[] a = RecordStore.listRecordStores();
			for(int i = 0; i < a.length; i++) {
				if(!a[i].startsWith(THUMBNAIL_PREFIX)) continue;
				RecordStore.deleteRecordStore(a[i]);
			}
		} catch (Exception e) {
		}
	}
	
	public static void init() {
		initSubscriptions();
	}
	
	public static void clearAllData() {
		try {
			RecordStore.deleteRecordStore(SUBSCRIPTIONS);
		} catch (Exception e) {
		}
		try {
			String[] a = RecordStore.listRecordStores();
			for(int i = 0; i < a.length; i++) {
				if(!a[i].startsWith(AVATAR_PREFIX)) continue;
				RecordStore.deleteRecordStore(a[i]);
			}
		} catch (Exception e) {
		}
		try {
			RecordStore.deleteRecordStore(AVATARS_INDEX);
		} catch (Exception e) {
		}
	}
	
	private static void initSubscriptions() {
		RecordStore rs;
		try {
			rs = RecordStore.openRecordStore(SUBSCRIPTIONS, false);
			subscriptions = JSON.getArray(new String(rs.getRecord(1), "UTF-8"));
			rs.closeRecordStore();
		} catch (Exception e) {
			subscriptions = new JSONArray();
		}
		try {
			rs = RecordStore.openRecordStore(AVATARS_INDEX, false);
			avatarsLastIndex = Integer.parseInt(new String(rs.getRecord(1)));
			avatars = JSON.getArray(new String(rs.getRecord(2), "UTF-8"));
			rs.closeRecordStore();
		} catch (Exception e) {
			avatars = new JSONArray();
		}
	}
	
	public static void importSubscriptions(byte[] b) throws Exception {
		subscriptions = JSON.getArray(new String(b, "UTF-8"));
		// remove repeats
		for(int i = subscriptions.size()-2; i > 0; i-=2) {
			int idx = subscriptions.indexOf(subscriptions.get(i));
			if(idx != i) {
				subscriptions.remove(i+1);
				subscriptions.remove(i);
			}
		}
		saveSubscriptions();
	}
	
	public static byte[] exportSubscriptionsBytes() throws Exception {
		return subscriptions.build().getBytes("UTF-8");
	}
	
	public static void saveSubscriptions() {
		try {
			RecordStore rs = RecordStore.openRecordStore(SUBSCRIPTIONS, true);
			setOrAdd(rs, 1, exportSubscriptionsBytes());
			rs.closeRecordStore();
		} catch (Exception e) {
		}
	}
	
	public static void saveAvatars() {
		try {
			RecordStore rs = RecordStore.openRecordStore(AVATARS_INDEX, true);
			setOrAdd(rs, 1, Integer.toString(avatarsLastIndex).getBytes());
			setOrAdd(rs, 2, avatars.build().getBytes("UTF-8"));
			rs.closeRecordStore();
		} catch (Exception e) {
		}
	}
	
	public static void addSubscription(String id, String name) {
		subscriptions.add(id);
		subscriptions.add(name);
		saveSubscriptions();
	}
	
	public static boolean isSubscribed(String id) {
		return subscriptions.has(id);
	}
	
	public static String[] getSubsciptions() {
		if(subscriptions == null) return new String[0];
		String[] arr = new String[subscriptions.size()];
		subscriptions.copyInto(arr, 0, arr.length);
		return arr;
	}
	
	public static void removeSubscription(String id) {
		int idx = subscriptions.indexOf(id);
		subscriptions.remove(idx+1);
		subscriptions.remove(idx);
		saveSubscriptions();
	}
	
	private static void setOrAdd(RecordStore rs, int n, byte[] b) throws Exception {
		try {
			rs.setRecord(n, b, 0, b.length);
		} catch (Exception e) {
			rs.addRecord(b, 0, b.length);
		}
	}
	
//	public static void addHistory(String id, String name) {
//	}
//	
//	public static boolean isWatched(String id) {
//		return false;
//	}
//	
//	public static String[] getHistory(int tab) {
//		return new String[0];
//	}
//	
//	public static void clearHistory() {
//	}
//	
//	public static void addLiked(String id, String name) {
//	}
//	
//	public static boolean isLiked(String id) {
//		return false;
//	}
//	
//	public static String[] getLiked(int tab) {
//		return new String[0];
//	}
//	
//	public static void removeLiked(String id) {
//	}

}
