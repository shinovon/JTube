package ui.nokia_extensions;

import cc.nnproject.utils.PlatformUtils;

public class TextEditorUtil {
	
	private static boolean supported;
	
	public static void init() {
		if((PlatformUtils.isSymbian3Based() || PlatformUtils.isAshaFullTouch()) && !PlatformUtils.isKemulator) {
			try {
				Class.forName("com.nokia.mid.ui.TextEditor");
				supported = true;
			} catch (Exception e) {
			} catch (Error e) {
			}
		}
	}
	
	public static TextEditorInst createTextEditor(String text, int maxSize, int constraints, int width, int height) {
		if(!supported) return null;
		try {
			return TextEditorInvoker.createTextEditorInst(text, maxSize, constraints, width, height);
		} catch (Throwable e) {
		}
		return null;
	}
	
	public static boolean isSupported() {
		return supported;
	}

}
