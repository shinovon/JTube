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
