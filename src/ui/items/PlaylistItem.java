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

import javax.microedition.lcdui.Graphics;

import Util;
import Locale;
import ui.AppUI;
import ui.UIConstants;
import models.PlaylistModel;

public class PlaylistItem extends AbstractButton implements UIConstants {

	private PlaylistModel playlist;
	
	private String title;
	private String author;

	private String[] titleArr;
	private String videosStr;

	private int textWidth;

	private int h;

	public PlaylistItem(PlaylistModel p) {
		super();
		this.playlist = p;
		this.title = p.title;
		this.author = p.author;
		this.videosStr = Locale.videos(p.videoCount);
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		//g.setColor(COLOR_MAINBG);
		//g.fillRect(x, y, w, h);
		g.setColor(AppUI.getColor(COLOR_MAINFG));
		g.setFont(mediumfont);
		int yy = y;
		y += 2;
		if(titleArr != null) {
			if(titleArr[0] != null) g.drawString(titleArr[0], x+2, y, 0);
			if(titleArr[1] != null) g.drawString(titleArr[1], x+2, y += mediumfontheight + 2, 0);
		}
		g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
		g.setFont(smallfont);
		if(videosStr != null) {
			g.drawString(videosStr, x+2, y += mediumfontheight + 4, 0);
		}
		if(author != null) {
			g.drawString(author, x+2, y + smallfontheight + 2, 0);
		}
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, yy, w-1, h-1);
			//g.drawRect(x+1, yy+1, w-3, h-3);
		}
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		if(title != null && titleArr == null) {
			makeTitleArr(w);
		}
		h = 12 + mediumfontheight + mediumfontheight + smallfontheight + smallfontheight;
	}
	
	private void makeTitleArr(int w) {
		w = getTextMaxWidth(w);
		String[] arr = Util.getStringArray(title, w, mediumfont);
		titleArr = new String[2];
		if(arr.length > 0) {
			titleArr[0] = arr[0];
			if(arr.length > 1) {
				if(arr.length > 2) {
					titleArr[1] = arr[1].concat("...");
				} else {
					titleArr[1] = arr[1];
				}
			}
		}
		title = null;
	}
	
	private int getTextMaxWidth(int w) {
		if(textWidth > 0) return textWidth;
		return textWidth = w-4;
	}
	
	public PlaylistModel getPlaylist() {
		return playlist;
	}

	protected void action() {
		ui.open(playlist, playlist.getContainerScreen() != null ? playlist.getContainerScreen() : getScreen());
	}

}
