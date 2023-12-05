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

import com.nokia.mid.ui.TextEditor;

public class TextEditorInvoker {
	
	static Object createTextEditor(String text, int maxSize, int constraints, int width, int height) {
		return TextEditor.createTextEditor(text, maxSize, constraints, width, height);
	}
	
	static TextEditorInst createTextEditorInst(String text, int maxSize, int constraints, int width, int height) {
		Object editor = createTextEditor(text, maxSize, constraints, width, height);
		TextEditorInst inst = null;
		try {
			Class.forName("com.nokia.mid.ui.S60TextEditor");
			inst = new S60TextEditorImpl();
			if(inst.setEditor(editor)) {
				return inst;
			}
		} catch (Throwable e) {
		}
		inst = new TextEditorImpl();
		if(inst.setEditor(editor)) {
			return inst;
		}
		return null;
	}

}
