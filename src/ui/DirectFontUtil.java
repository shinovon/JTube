package ui;

import javax.microedition.lcdui.Font;

public class DirectFontUtil {
	
	private static boolean supported;

	static {
		try {
			DirectUtilsInvoker.init();
			supported = true;
		} catch (NoClassDefFoundError e) {
			supported = false;
		} catch (Exception e) {
			supported = false;
		} catch (Error e) {
			supported = false;
		}
	}
	
	public static Font getFont(int face, int style, int height, int size) {
		//костыль
		if(supported)
			try {
				Font f = DirectUtilsInvoker.getFont(face, style, height);
				if(f != null)
					return f;
			} catch (Throwable e) {
			}
		return Font.getFont(face, style, size);
	}
	
}

