package ui.nokia_extensions;

import javax.microedition.lcdui.Font;

public abstract class TextEditorInst {
	public static final int TOUCH_INPUT_ALL_AVAILABLE = 0;
	public static final int TOUCH_INPUT_HWR = 1;
	public static final int TOUCH_INPUT_VKB = 2;
	public static final int TOUCH_INPUT_FSQ = 4;
	public static final int TOUCH_INPUT_ITUT = 8;
	public static final int TOUCH_INPUT_FSC = 16;
	public static final int TOUCH_INPUT_MINI_ITUT = 32;

	protected abstract boolean setEditor(Object editor);
	
	public abstract void setBackgroundColor(int color);
	
	public abstract void setForegroundColor(int color);
	
	public abstract void setContent(String content);
	
	public abstract String getContent();
	
	public abstract void setVisible(boolean b);
	
	public abstract void setSize(int w, int h);
	
	public abstract void setParent(Object parent);
	
	public abstract void setMultiline(boolean b);
	
	public abstract Font getFont();
	
	public abstract void setFont(Font f);
	
	public abstract void setFocus(boolean b);

	public abstract boolean isVisible();
	
	public abstract void setPosition(int x, int y);
	
	public abstract void setCaretXY(int x, int y);
	
	public abstract void setTouchEnabled(boolean b);
	
	public abstract void setPreferredTouchMode(int mode);
	
	public abstract void setIndicatorVisibility(boolean b);
	
	public abstract void setTextEditorListener(TextEditorListener l);

}
