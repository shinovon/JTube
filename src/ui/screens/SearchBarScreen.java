package ui.screens;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import App;
import Locale;
import Settings;
import RunnableTask;
import cc.nnproject.utils.PlatformUtils;
import ui.AbstractListScreen;
import ui.AppUI;
import ui.Commands;
import ui.UIScreen;
import ui.nokia_extensions.TextEditorInst;
import ui.nokia_extensions.TextEditorListener;
import ui.nokia_extensions.TextEditorUtil;

public abstract class SearchBarScreen extends AbstractListScreen implements TextEditorListener, CommandListener, Commands {
	
	protected SearchBarScreen(String label, UIScreen parent) {
		super(label, parent);
	}

	protected boolean wasHidden;

	private int searchHeight;
	private int searchButtonWidth;
	private int searchFieldWidth;
	protected String searchText = "";

	protected static TextEditorInst editor;
	private static Image searchImg;

	private int lastW;

	private boolean editorHidden = true;

	private static final Command textOkCmd = new Command(Locale.s(CMD_Search), Command.OK, 1);

	private static final Font searchFont = Font.getDefaultFont();
	
	protected void hide() {
		wasHidden = true;
		if(editor != null && !editorHidden && editor.isVisible()) {
			editorHidden = true;
			searchText = "";
			editor.setFocus(false);
			editor.setVisible(false);
			editor.setParent(null);
		}
	}
	
	protected void show() {
		if(wasHidden && editor != null && !editorHidden) {
			editor.setForegroundColor(AppUI.getColor(COLOR_MAINFG));
			editor.setBackgroundColor(AppUI.getColor(COLOR_MAINBG));
			showEditor();
		}
	}
	
	public void paint(Graphics g, int w, int h) {
		if(Settings.searchBar && lastW != w) {
			lastW = w;
			if(!ui.isKeyInputMode()) {
				searchHeight = h >= 360 && h > w ? 48 : 32;
				if(searchImg == null || searchImg.getHeight() != searchHeight) {
					try {
						searchImg = Image.createImage("/search" + searchHeight + ".png");
					} catch (IOException e) {
					}
				}
				if(searchImg != null) {
					searchButtonWidth = searchImg.getWidth();
					searchFieldWidth = w - searchButtonWidth;
				}
				if(TextEditorUtil.isSupported()) {
					if(editor == null) {
						editor = TextEditorUtil.createTextEditor("", 100, TextField.ANY, searchFieldWidth, searchHeight);
						editor.setForegroundColor(AppUI.getColor(COLOR_MAINFG));
						editor.setBackgroundColor(AppUI.getColor(COLOR_MAINBG));
					}
					if(!editorHidden) {
						showEditor();
						if(PlatformUtils.isSymbian3Based()) {
							editor.setFocus(true);
						}
					}
				}
			} else {
				searchHeight = 0;
				if(editor != null) {
					editor.setParent(null);
					editor.setVisible(false);
					editor.setTouchEnabled(false);
				}
			}
		}
		if(Settings.searchBar && searchHeight > 0) {
			g.translate(0, searchHeight);
			super.paint(g, w, h-searchHeight);
			g.translate(0, -searchHeight);
			g.setColor(AppUI.getColor(COLOR_MAINBG));
			g.fillRect(0, 0, w, searchHeight);
			if((editor == null || editorHidden)) {
				g.setFont(searchFont);
				int y = (searchHeight - searchFont.getHeight()) / 2;
				String s;
				if(searchText.length() > 0) {
					s = searchText;
					g.setColor(AppUI.getColor(COLOR_MAINFG));
					while(searchFont.stringWidth(s) >= searchFieldWidth-8) {
						s = s.substring(1);
					}
				} else {
					g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
					s = Locale.s(TXT_SearchHint);
				}
				g.drawString(s, 4, y, 0);
			}
			if(searchImg != null && searchText.length() > 0) {
				g.drawImage(searchImg, searchFieldWidth, 0, 0);
			}
			g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
			g.drawLine(0, searchHeight, w, searchHeight);
			return;
		}
		super.paint(g, w, h);
	}
	
	private void showEditor() {
		editor.setParent(ui.getCanvas());
		editor.setVisible(true);
		editor.setMultiline(true);
		editor.setTouchEnabled(true);
		editor.setTextEditorListener(this);
		int y = (searchHeight - editor.getFont().getHeight()) / 2;
		editor.setPosition(4, y);
		editor.setSize(searchFieldWidth-4, searchHeight-y-1);
	}
	
	protected void press(int x, int y) {
		if(Settings.searchBar && y < searchHeight) return;
		super.press(x, y - searchHeight);
	}
	
	protected void release(int x, int y) {
		if(Settings.searchBar && y < searchHeight) return;
		super.release(x, y - searchHeight);
	}
	
	protected void tap(int x, int y, int time) {
		System.out.println("tap " + x + " " + y + " " + searchHeight);
		if(Settings.searchBar && y < searchHeight) {
			if(time > 5 && time < 200) {
				if(x < searchFieldWidth) {
					if(editor != null) {
						if(editorHidden) {
							editorHidden = false;
							showEditor();
						}
						editor.setFocus(true);
					} else {
						openTextBox();
					}
				} else {
					if(searchText.length() > 0) {
						search(searchText);
					}
				}
			}
			return;
		}
		super.tap(x, y - searchHeight, time);
	}

	protected void search(String s) {
		App.inst.schedule(new RunnableTask(s, 2));
	}

	private void openTextBox() {
		TextBox t = new TextBox("", "", 256, TextField.ANY);
		t.setCommandListener(this);
		t.setTitle(Locale.s(CMD_Search));
		t.addCommand(textOkCmd);
		t.addCommand(cancelCmd);
		ui.display(t);
	}
	
	protected void setSearchText(String s) {
		searchText = s;
		if(editor != null) {
			editor.setContent(s);
		}
	}

	public void inputAction(TextEditorInst editor, int event) {
		System.out.println("event: " + event);
		if((event & ACTION_CONTENT_CHANGE) > 0) {
			searchText = editor.getContent();
			System.out.println(searchText);
			if(searchText.endsWith("\n")) {
				searchText = searchText.trim();
				editor.setContent(searchText);
				if(searchText.length() > 0) {
					editor.setFocus(false);
					editor.setVisible(false);
					search(searchText);
				}
			}
		}
		if((event & ACTION_PAINT_REQUEST) > 0) {
			repaint();
		}
	}

	public boolean supportCommands() {
		return true;
	}

	public void commandAction(Command c, Displayable d) {
		if(c == textOkCmd && d instanceof TextBox) {
			searchText = ((TextBox) d).getString();
			search(searchText);
			return;
		}
		if(c == cancelCmd && d instanceof TextBox) {
			searchText = ((TextBox) d).getString();
			ui.display(null);
			return;
		}
		super.commandAction(c, d);
	}
	
}
