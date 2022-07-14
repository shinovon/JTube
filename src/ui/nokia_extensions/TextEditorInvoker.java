package ui.nokia_extensions;

import com.nokia.mid.ui.TextEditor;

public class TextEditorInvoker {
	
	static TextEditor createTextEditor(String text, int maxSize, int constraints, int width, int height) {
		return TextEditor.createTextEditor(text, maxSize, constraints, width, height);
	}
	
	static TextEditorInst createTextEditorInst(String text, int maxSize, int constraints, int width, int height) {
		TextEditor editor = createTextEditor(text, maxSize, constraints, width, height);
		TextEditorInst inst = null;
		try {
			Class.forName("com.nokia.mid.ui.S60TextEditor");
			inst = new S60TextEditorImpl();
			if(inst.setEditor(editor)) {
				return inst;
			}
		} catch (Exception e) {
		} catch (Error e) {
		}
		inst = new TextEditorImpl();
		if(inst.setEditor(editor)) {
			return inst;
		}
		return null;
	}

}
