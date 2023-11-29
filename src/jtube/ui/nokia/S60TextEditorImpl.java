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
package jtube.ui.nokia;

import com.nokia.mid.ui.S60TextEditor;
import com.nokia.mid.ui.TextEditor;

public class S60TextEditorImpl extends TextEditorImpl {

	private S60TextEditor _S60editor;

	public boolean setEditor(Object editor) {
		if(editor instanceof S60TextEditor) {
			_S60editor = (S60TextEditor) editor;
			_editor = (TextEditor) editor;
			return true;
		}
		return false;
	}

	public void setCaretXY(int x, int y) {
		_S60editor.setCaretXY(x, y);
	}

	public void setTouchEnabled(boolean enabled) {
		_S60editor.setTouchEnabled(enabled);
	}

	public void setIndicatorVisibility(boolean b) {
		_S60editor.setIndicatorVisibility(b);
	}

	public void setPreferredTouchMode(int mode) {
		_S60editor.setPreferredTouchMode(mode);
	}

}
