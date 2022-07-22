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
package ui.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import Settings;
import Locale;
import LocaleConstants;
import Util;
import ui.AppUI;
import ui.UIConstants;
import ui.UIItem;
import ui.nokia_extensions.DirectFontUtil;
import ui.screens.VideoScreen;

public class VideoExtrasItem extends UIItem implements UIConstants {
	
	private static boolean amoledImgs;
	private static Image likeImg;
	private static Image shareImg;
	private static Image saveImg;
	private static Font font;
	
	private static void init() {
		try {
			if(font == null) {
				font = DirectFontUtil.getFont(0, 0, 18, 8);
			}
			likeImg = Image.createImage("/like24.png");
			shareImg = Image.createImage("/share24.png");
			saveImg = Image.createImage("/save24.png");
			if(Settings.amoled) {
				likeImg = Util.invert(likeImg);
				shareImg = Util.invert(shareImg);
				saveImg = Util.invert(saveImg);
			}
			amoledImgs = Settings.amoled;
		} catch (Exception e) {
		}
	}
	
	static {
		init();
	}

	private int w;
	private int h;
	
	private VideoScreen scr;
	private int likes;
	
	private int selectedIndex;

	public VideoExtrasItem(VideoScreen scr, int likes) {
		this.scr = scr;
		this.likes = likes;
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		float f = w / 3F;
		g.drawImage(likeImg, (int) ((f-24)/2), y+6, 0);
		g.drawImage(shareImg, (int) ((f-24)/2+f), y+6, 0);
		g.drawImage(saveImg, (int) ((f-24)/2+f*2), y+6, 0);
		g.setFont(font);
		g.setColor(AppUI.getColor(COLOR_MAINFG));
		String s = "" + likes;
		g.drawString(s, ((int)f - font.stringWidth(s))/2, y+32, 0);
		s = Locale.s(LocaleConstants.BTN_Share);
		g.drawString(s, ((int)f - font.stringWidth(s))/2+(int)f, y+32, 0);
		s = Locale.s(LocaleConstants.CMD_Download);
		g.drawString(s, ((int)f - font.stringWidth(s))/2+(int)f*2, y+32, 0);
		g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
		g.drawLine(x, y, w-x, y);
		g.drawLine(x, y+h, w-x, y+h);
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x+(int)(f * selectedIndex), y, (int)f, h-1);
		}
	}
	
	protected void tap(int x, int y, int time) {
		if(time > 5 && time < 200) {
			int f = (int) (w / 3F);
			if(x > f && x < f*2) {
				scr.showLink();
			} else if(x > f*2 && x < w) {
				scr.download();
			}
		}
	}
	
	protected void keyPress(int key) {
		if(key == -5) {
			if(selectedIndex == 1) {
				scr.showLink();
			} else if(selectedIndex == 2) {
				scr.download();
			}
			return;
		}
		if(key == -3) {
			if(selectedIndex == 0) return;
			selectedIndex--;
			return;
		}
		if(key == -4) {
			if(selectedIndex == 2) return;
			selectedIndex++;
			return;
		}
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		this.w = w;
		h = Math.max(52, 36 + font.getHeight());
		if(Settings.amoled != amoledImgs) {
			init();
		}
	}
	
	public int getOKLabel() {
		switch(selectedIndex) {
		case 1:
			return LocaleConstants.BTN_Share;
		case 2:
			return LocaleConstants.CMD_Download;
		default:
			return -1;
		}
	}

}
