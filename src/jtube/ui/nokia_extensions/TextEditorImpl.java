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
package jtube.ui.nokia_extensions;

import javax.microedition.lcdui.Font;

import com.nokia.mid.ui.TextEditor;

public class TextEditorImpl extends TextEditorInst {
	
	protected TextEditor _editor;

	public boolean setEditor(Object editor) {
		if(editor instanceof TextEditor) {
			_editor = (TextEditor) editor;
			return true;
		}
		return false;
	}

	public void setBackgroundColor(int color) {
		_editor.setBackgroundColor(color);
	}

	public void setForegroundColor(int color) {
		_editor.setForegroundColor(color);
	}

	public void setContent(String content) {
		_editor.setContent(content);
	}

	public String getContent() {
		return _editor.getContent();
	}

	public void setVisible(boolean b) {
		_editor.setVisible(b);
	}
	
	public void setSize(int w, int h) {
		_editor.setSize(w, h);
	}
	
	public void setParent(Object parent) {
		_editor.setParent(parent);
	}
	
	public void setMultiline(boolean b) {
		_editor.setMultiline(b);
	}
	
	public Font getFont() {
		return _editor.getFont();
	}
	
	public void setFont(Font f) {
		_editor.setFont(f);
	}
	
	public void setFocus(boolean b) {
		_editor.setFocus(b);
	}
	
	public void setPosition(int x, int y) {
		_editor.setPosition(x, y);
	}

	public boolean isVisible() {
		return _editor.isVisible();
	}

	public void setCaretXY(int x, int y) {
	}

	public void setTouchEnabled(boolean enabled) {
	}

	public void setPreferredTouchMode(int mode) {
	}

	public void setIndicatorVisibility(boolean b) {
	}

	public void setTextEditorListener(final TextEditorListener l) {
		_editor.setTextEditorListener(new com.nokia.mid.ui.TextEditorListener() {
			public void inputAction(TextEditor textEditor, int actions) {
				l.inputAction(TextEditorImpl.this, actions);
			}
		});
	}

}
