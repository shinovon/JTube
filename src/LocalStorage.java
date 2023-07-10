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

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

public class LocalStorage {
	
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
	
	// TODO
	
	public static void addSubscription(String id, String name) {
	}
	
	public static boolean isSubscribed(String id) {
		return false;
	}
	
	public static String[] getSubsciptions(int tab) {
		return new String[0];
	}
	
	public static void removeSubscription(String id) {
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
		
	}

}
