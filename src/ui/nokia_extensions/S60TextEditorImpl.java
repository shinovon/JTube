package ui.nokia_extensions;

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
