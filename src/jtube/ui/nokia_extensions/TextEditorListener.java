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

public interface TextEditorListener {
	public static final int ACTION_CONTENT_CHANGE = 1;
	public static final int ACTION_OPTIONS_CHANGE = 2;
	public static final int ACTION_CARET_MOVE = 4;
	public static final int ACTION_TRAVERSE_PREVIOUS = 8;
	public static final int ACTION_TRAVERSE_NEXT = 16;
	public static final int ACTION_PAINT_REQUEST = 32;
	public static final int ACTION_DIRECTION_CHANGE = 64;
	public static final int ACTION_INPUT_MODE_CHANGE = 128;
	public static final int ACTION_LANGUAGE_CHANGE = 256;
	public static final int ACTION_TRAVERSE_OUT_SCROLL_UP = 512;
	public static final int ACTION_TRAVERSE_OUT_SCROLL_DOWN = 1024;
	public static final int ACTION_SCROLLBAR_CHANGED = 2048;
	public static final int ACTION_VIRTUAL_KEYBOARD_OPEN = 4096;
	public static final int ACTION_VIRTUAL_KEYBOARD_CLOSE = 8192;

	public void inputAction(TextEditorInst editor, int event);
	
}
