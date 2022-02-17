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

public class Records {
	
	public static void save(String id, byte[] b) {
		try {
			RecordStore rs = RecordStore.openRecordStore(id, true);
			if(rs.getNumRecords() <= 0) {
				rs.addRecord(b, 0, b.length);
			}
			rs.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void save(String id, String url) {
		try {
			RecordStore rs = RecordStore.openRecordStore(id, true);
			if(rs.getNumRecords() <= 0) {
				byte[] b = App.hproxy(url);
				rs.addRecord(b, 0, b.length);
				b = null;
			}
			rs.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Image saveOrGetImage(String id, String url) throws IOException {
		try {
			byte[] b = null;
			RecordStore rs = null;
			try {
				rs = RecordStore.openRecordStore(id, true);
				//System.out.println(id + " " + rs + " " + rs.getNumRecords());
				if(rs.getNumRecords() > 0) {
					b = rs.getRecord(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(b == null) {
				if(url == null) {
					return null;
				}
				b = App.hproxy(url);
				if(rs != null) {
					try {
						if(rs.getNumRecords() <= 0) {
							rs.addRecord(b, 0, b.length);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if(rs != null) {
				try {
					rs.closeRecordStore();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return Image.createImage(b, 0, b.length);
		} catch (IOException e) {
			throw e;
		}
	}

}
