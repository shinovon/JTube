package ui;

import javax.microedition.lcdui.Font;

import com.nokia.mid.ui.DirectUtils;

class DirectUtilsInvoker {
	
	static Font getFont(int face, int style, int height) {
		try {
			return DirectUtils.getFont(face, style, height);
		} catch (Throwable e) {
		}
		return null;
	}
	
	static void init() {
		DirectUtils.getFont(0, 0, 12);
	}

}
