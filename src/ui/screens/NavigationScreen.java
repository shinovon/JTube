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
package ui.screens;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import App;
import Util;
import cc.nnproject.utils.PlatformUtils;
import Locale;
import Settings;
import RunnableTask;
import ui.AbstractListScreen;
import ui.AppUI;
import ui.Commands;
import ui.UIItem;
import ui.UIScreen;
import ui.nokia_extensions.DirectFontUtil;
import ui.nokia_extensions.TextEditorInst;
import ui.nokia_extensions.TextEditorListener;
import ui.nokia_extensions.TextEditorUtil;

public abstract class NavigationScreen extends AbstractListScreen implements TextEditorListener, CommandListener, Commands {

	private static final Command textOkCmd = new Command(Locale.s(CMD_Search), Command.OK, 1);

	private static final Font searchFont = Font.getDefaultFont();

	private static final Font softFont = Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL);
	
	private static Image searchImg;
	private static Image backImg;
	private static Image menuImg;

	private static boolean init;
	protected static boolean topBar;
	private static boolean amoledImgs;
	private static boolean addOk;

	protected static TextEditorInst editor;

	protected boolean wasHidden;

	private int topBarHeight = 48;
	private int softBarHeight = 24;
	
	protected String searchText = "";

	private int lastW;
	private int lastH;

	private boolean editorHidden = true;

	private boolean search;
	private boolean menu;

	private int menuW;
	private int menuH;
	
	protected boolean hasSearch;
	protected String[] menuOptions;

	private int menuSelectedIndex;
	
	protected NavigationScreen(String label, UIScreen parent) {
		super(label, parent);
		init();
		hasSearch = true;
		if(this instanceof MainScreen) {
			menuOptions = !topBar ? new String[] {
					Locale.s(CMD_Search),
					Locale.s(CMD_Refresh),
					Locale.s(Settings.startScreen == 0 ? CMD_SwitchToPopular : CMD_SwitchToTrends),
					Locale.s(CMD_OpenByID),
					Locale.s(CMD_Settings),
					Locale.s(CMD_About),
					Locale.s(CMD_Exit)
			} : new String[] {
					Locale.s(CMD_Refresh),
					Locale.s(Settings.startScreen == 0 ? CMD_SwitchToPopular : CMD_SwitchToTrends),
					Locale.s(CMD_OpenByID),
					Locale.s(CMD_Settings),
					Locale.s(CMD_About),
					Locale.s(CMD_Exit)
			};
		}
	}
	
	private static void init() {
		if(init) return;
		init = true;
		topBar = ui.getCanvas().hasPointerEvents();
		try {
			if(topBar) {
				searchImg = Image.createImage("/search24.png");
				backImg = Image.createImage("/back24.png");
				menuImg = Image.createImage("/menu24.png");
				if(Settings.amoled) {
					amoledImgs = true;
					searchImg = Util.invert(searchImg);
					backImg = Util.invert(backImg);
					menuImg = Util.invert(menuImg);
				}
			}
			if(TextEditorUtil.isSupported()) {
				editor = TextEditorUtil.createTextEditor("", 100, TextField.ANY, 24, 24);
				editor.setForegroundColor(AppUI.getColor(COLOR_MAINFG) | 0xFF000000);
				editor.setBackgroundColor(AppUI.getColor(COLOR_MAINBG) | 0xFF000000);
				Font f = Font.getDefaultFont();
				if(DirectFontUtil.isSupported() && App.width >= 360) {
					f = DirectFontUtil.getFont(0, 0, 23, 0);
				}
				editor.setFont(f);
			}
		} catch (Exception e) {
		}
		addOk = !topBar && PlatformUtils.isNotS60() && !PlatformUtils.isS603rd() && !PlatformUtils.isSonyEricsson() && !PlatformUtils.isKemulator && !PlatformUtils.isJ2ML() && !PlatformUtils.isPhoneme();
	}

	protected void hide() {
		wasHidden = true;
		search = false;
		if(editor != null && !editorHidden && editor.isVisible()) {
			editorHidden = true;
			searchText = "";
			editor.setFocus(false);
			editor.setVisible(false);
			editor.setParent(null);
		}
	}
	
	protected void show() {
		if(Settings.amoled != amoledImgs) {
			try {
				searchImg = Image.createImage("/search24.png");
				backImg = Image.createImage("/back24.png");
				menuImg = Image.createImage("/menu24.png");
				if(Settings.amoled) {
					searchImg = Util.invert(searchImg);
					backImg = Util.invert(backImg);
					menuImg = Util.invert(menuImg);
				}
			} catch (Exception e) {
			}
			amoledImgs = Settings.amoled;
		}
		ui.removeCommands();
		if(!Settings.fullScreen) {
			ui.addOptionCommands();
			ui.addCommand(this instanceof MainScreen ? exitCmd : backCmd);
			if(addOk) {
				ui.addCommand(okCmd);
			}
		}
	}
	
	private void setEditorPositions() {
		if(editor != null && editor.isVisible()) {
			editor.setSize(lastW-98, 24);
			editor.setPosition(50, 12);
		}
	}
	
	public void paint(Graphics g, int w, int h) {
		if(lastW != w) {
			lastW = w;
			lastH = h;
			if(menuOptions != null) {
				menuW = w-w/4;
				menuH = menuOptions.length * (mediumfontheight+8);
			}
			setEditorPositions();
		}
		if(topBar) {
			g.translate(0, topBarHeight);
			_paint(g, w, h-topBarHeight);
			if(search) {
				int l = w * 30 + 1;
				int c = AppUI.getColor(COLOR_SCREEN_DARK_ALPHA);
				int[] rgb = new int[l];
				for(int i=0; i < l; i++) {
					rgb[i] = c;
				}
				for(int i = 0; i <= h; i+=30) {
					g.drawRGB(rgb, 0, w, 0, i, w, 30, true);
				}
			}
			g.translate(0, -topBarHeight);
			g.setColor(AppUI.getColor(COLOR_TOPBAR_BG));
			g.fillRect(0, 0, w, topBarHeight);
			g.setColor(AppUI.getColor(COLOR_TOPBAR_BORDER));
			g.drawLine(0, topBarHeight, w, topBarHeight);
			if(search) {
				g.drawImage(searchImg, w - 36, 12, 0);
				g.drawImage(backImg, 12, 12, 0);
				if(editor == null || editorHidden) {
					g.setFont(searchFont);
					String s;
					if(searchText.length() > 0) {
						s = searchText;
						g.setColor(AppUI.getColor(COLOR_MAINFG));
						while(searchFont.stringWidth(s) >= w-100) {
							s = s.substring(1);
						}
					} else {
						g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
						s = Locale.s(TXT_SearchHint);
					}
					g.drawString(s, 50, (topBarHeight - searchFont.getHeight()) / 2, 0);
				}
			} else {
				if(Settings.fullScreen) {
					int xx = 4;
					if(!(this instanceof MainScreen)) {
						g.drawImage(backImg, 12, 12, 0);
						xx += 48;
					}
					if(!(this instanceof VideoScreen)) {
						String s = getTitle();
						if(s != null && s.length() > 0) {
							int ww = w-96-xx - (smallfont.charWidth('.')*2);
							if(mediumfont.stringWidth(s) >= ww) {
								g.setFont(smallfont);
								while(smallfont.stringWidth(s) >= ww) {
									s = s.substring(0, s.length()-1);
								}
								s += "..";
							} else {
								g.setFont(mediumfont);
							}
							g.setColor(AppUI.getColor(COLOR_MAINFG));
							g.drawString(s, xx, (48-g.getFont().getHeight())/2, 0);
						}
					}
				}
				if(menuOptions != null) {
					g.drawImage(menuImg, w - 36, 12, 0);
				}
				if(hasSearch) {
					g.drawImage(searchImg, w - 84, 12, 0);
				}
			}
			if(menu) {
				int xx = (w-menuW)/2;
				int yy = (h-menuH)/2;
				g.setFont(mediumfont);
				g.setColor(AppUI.getColor(COLOR_MAINBG));
				g.fillRect(xx, yy, menuW, menuH);
				for(int i = 0; i < menuOptions.length; i++) {
					g.setColor(AppUI.getColor(COLOR_MAINFG));
					g.drawString(menuOptions[i], xx + 4, yy+4, 0);
					yy += 8 + mediumfontheight;
					/*if(i != menuOptions.length - 1) {
						g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
						g.drawLine(xx, yy, xx+menuW, yy);
					}*/
				}
			}
			return;
		}
		if(Settings.fullScreen) {
			_paint(g, w, h-softBarHeight);
			if(!AppUI.loadingState) {
				if(menu) {
					int xx = (w-menuW)/2;
					int yy = Math.max(0, (h-softBarHeight-menuH)/2);
					g.setFont(mediumfont);
					g.setColor(AppUI.getColor(COLOR_MAINBG));
					int ih = 8 + mediumfontheight;
					if(yy+ih*(menuSelectedIndex+1) > h-softBarHeight) {
						for(int i = 0; i < menuSelectedIndex+1; i++) {
							if(yy+ih*(i+1) > h-softBarHeight) {
								yy-=ih;
							}
						}
					}
					g.fillRect(xx, yy, menuW, menuH);
					for(int i = 0; i < menuOptions.length; i++) {
						g.setColor(AppUI.getColor(COLOR_MAINFG));
						if(menuSelectedIndex == i) {
							g.fillRect(xx, yy, 2, ih);
						}
						g.drawString(menuOptions[i], xx + 4, yy+4, 0);
						yy += ih;
						/*if(i != menuOptions.length - 1) {
							g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
							g.drawLine(xx, yy, xx+menuW, yy);
						}*/
					}
				}
				g.setColor(AppUI.getColor(COLOR_SOFTBAR_BG));
				g.fillRect(0, h-softBarHeight, w, softBarHeight);
				g.setColor(AppUI.getColor(COLOR_SOFTBAR_FG));
				g.setFont(softFont);
				if(!menu) {
					g.drawString(Locale.s(CMD_Func), 2, h-2, Graphics.BOTTOM | Graphics.LEFT);
				}
				if(menu || !(this instanceof MainScreen)) {
					String s = Locale.s(CMD_Back);
					g.drawString(s, w-2, h-2, Graphics.BOTTOM | Graphics.RIGHT);
				} else {
					String s = Locale.s(CMD_Exit);
					g.drawString(s, w-2, h-2, Graphics.BOTTOM | Graphics.RIGHT);
				}
				if(getCurrentItem() != null) {
					UIItem ci = getCurrentItem();
					if(ci.getOKLabel() > 0) {
						g.drawString(Locale.s(ci.getOKLabel()), w/2, h-2, Graphics.BOTTOM | Graphics.HCENTER);
					}
				}
			}
		} else {
			_paint(g, w, h);
		}
	}
	
	private void _paint(Graphics g, int w, int h) {
		if(AppUI.loadingState) {
			if(editor != null && editor.isVisible()) {
				editor.setVisible(false);
			}
			g.setColor(AppUI.getColor(COLOR_MAINBG));
			g.fillRect(0, 0, w, h);
			g.setColor(AppUI.getColor(COLOR_MAINFG));
			String s = Locale.s(TITLE_Loading) + "...";
			g.setFont(smallfont);
			g.drawString(s, (w-smallfont.stringWidth(s))/2, smallfontheight*2, 0);
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
		setEditorPositions();
	}
	
	protected void keyPress(int i) {
		if(menu) {
			if(i == Canvas.KEY_NUM1) {
				menuSelectedIndex = 0;
			}
			if(i == Canvas.KEY_NUM7) {
				menuSelectedIndex = menuOptions.length - 1;
			}
			if(i == -1 || i == Canvas.KEY_NUM2) {
				menuSelectedIndex--;
				if(menuSelectedIndex < 0) {
					menuSelectedIndex = menuOptions.length - 1;
				}
			}
			if(i == -2 || i == Canvas.KEY_NUM8) {
				menuSelectedIndex++;
				if(menuSelectedIndex > menuOptions.length - 1) {
					menuSelectedIndex = 0;
				}
			}
			if(i == -7) {
				menu = false;
			}
			if(i == -5 || i == Canvas.KEY_NUM5) {
				menuAction(menuSelectedIndex);
				menu = false;
			}
			repaint();
			return;
		}
		if(i == -6) {
			leftSoft();
			return;
		}
		if(i == -7) {
			if(this instanceof MainScreen) {
				ui.exit();
			} else {
				back();
			}
			repaint();
			return;
		}
		super.keyPress(i);
	}
	
	protected void leftSoft() {
		if(menuOptions != null) menu = true;
	}

	protected void keyRelease(int i) {
		if(menu) {
			return;
		}
		super.keyRelease(i);
	}
	
	protected void keyRepeat(int i) {
		if(menu) {
			return;
		}
		super.keyRepeat(i);
	}
	
	protected void press(int x, int y) {
		if(y < topBarHeight || search | menu) return;
		super.press(x, y - topBarHeight);
	}
	
	protected void release(int x, int y) {
		if(y < topBarHeight || search || menu) return;
		super.release(x, y - topBarHeight);
	}
	
	protected void tap(int x, int y, int time) {
		if(menu) {
			int xx = (lastW-menuW)/2;
			int yy = (lastH-menuH)/2;
			if(x < xx || y < yy || x > xx+menuW || y > yy+menuH) {
				menu = false;
				return;
			}
			for(int i = 0; i < menuOptions.length; i++) {
				if(y < yy+(i+1)*(mediumfontheight+8)) {
					menuAction(i);
					menu = false;
					break;
				}
			}
			return;
		}
		if(y < topBarHeight) {
			if(time > 5 && time < 200 && x > 0 && x < lastW) {
				if(x < 48) {
					if(search) {
						editorHidden = true;
						if(editor != null && editor.isVisible()) {
							editor.setFocus(false);
							editor.setVisible(false);
							editor.setParent(null);
						}
						search = false;
					} else if(!(this instanceof MainScreen)) {
						back();
					}
				} else if(x > lastW - 48) {
					if(search) {
						if(searchText.length() == 0) return;
						search = false;
						editorHidden = true;
						if(editor != null && editor.isVisible()) {
							editor.setFocus(false);
							editor.setVisible(false);
							editor.setParent(null);
						}
						search(searchText);
					} else {
						if(menuOptions != null) menu = true;
					}
				} else if(hasSearch) {
					if(x > lastW - 96 && !search) {
						if(editor != null) {
							editorHidden = false;
							showEditor();
							editor.setFocus(true);
						}
						search = true;
					} else if(search) {
						if(editor != null) {
							if(editorHidden) {
								editorHidden = false;
								showEditor();
							}
							editor.setFocus(true);
						} else {
							openSearchTextBox();
						}
					}
				}
				/*
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
				*/
			}
			return;
		}
		if(search) return;
		super.tap(x, y - topBarHeight, time);
	}

	protected void back() {
		AppUI.loadingState = false;
		App.inst.stopAsyncTasks();
		if(parent != null) {
			ui.setScreen(parent);
		} else {
			ui.back(this);
		}
	}

	protected void menuAction(int action) {
		if(!topBar) action--;
		switch(action) {
		case -1:
			openSearchTextBox();
			break;
		case 0:
			App.inst.schedule(new RunnableTask(3));
			break;
		case 1:
			menuOptions[1] = Locale.s(Settings.startScreen == 0 ? CMD_SwitchToTrends : CMD_SwitchToPopular);
			App.inst.schedule(new RunnableTask(4));
			break;
		case 2:
			App.inst.stopAsyncTasks();
			TextBox t = new TextBox("", "", 256, TextField.ANY);
			t.setCommandListener(this);
			t.setTitle("Video URL or ID");
			t.addCommand(goCmd);
			t.addCommand(cancelCmd);
			ui.display(t);
			break;
		case 3:
			ui.showSettings();
			break;
		case 4:
			ui.showAbout(this);
			break;
		case 5:
			if(this instanceof MainScreen) {
				ui.exit();
			}
			break;
		}
	}

	protected void search(String s) {
		App.inst.schedule(new RunnableTask(s, 2));
	}

	protected void openSearchTextBox() {
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
		if((event & ACTION_CONTENT_CHANGE) > 0) {
			searchText = editor.getContent();
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

	public void commandAction(Command c, Displayable d) {
		if(c == goCmd && d instanceof TextBox) {
			App.inst.schedule(new RunnableTask(((TextBox) d).getString(), 1));
			return;
		}
		if(c == textOkCmd && d instanceof TextBox) {
			search(searchText = ((TextBox) d).getString());
			return;
		}
		if(c == cancelCmd && d instanceof TextBox) {
			ui.display(null);
			return;
		}
		if(d instanceof Alert || d instanceof TextBox) {
			ui.display(null);
			return;
		}
		if(c == optsCmd) {
			ui.showOptions();
			return;
		}
		if(c == okCmd) {
			keyPress(-5);
			return;
		}
		if(c == exitCmd) {
			ui.exit();
			return;
		}
		if(c == backCmd) {
			back();
		}
	}
	
	public boolean blockScrolling() {
		return search || menu;
	}
	
}
