package ui.nokia_extensions;

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
