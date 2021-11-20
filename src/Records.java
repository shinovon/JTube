import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

public class Records {
	
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
				System.out.println(id + " " + rs + " " + rs.getNumRecords());
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
