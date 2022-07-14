package ui.nokia_extensions;

import javax.microedition.lcdui.Font;

import cc.nnproject.utils.PlatformUtils;

public class DirectFontUtil {
	
	private static boolean supported;
	
	public static void init() {
		if((PlatformUtils.isSymbian3Based() || PlatformUtils.isAshaFullTouch()) && !PlatformUtils.isKemulator) {
			try {
				DirectUtilsInvoker.init();
				supported = true;
			} catch (Exception e) {
			} catch (Error e) {
			}
		}
	}
	
	public static Font getFont(int face, int style, int height, int size) {
		if(supported) {
			try {
				Font f = DirectUtilsInvoker.getFont(face, style, height);
				if(f != null) return f;
			} catch (Throwable e) {
			}
		}
		return Font.getFont(face, style, size);
	}
	
}

