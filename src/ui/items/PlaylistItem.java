package ui.items;

import javax.microedition.lcdui.Graphics;

import Util;
import Locale;
import ui.AppUI;
import ui.UIConstants;
import models.PlaylistModel;

public class PlaylistItem extends AbstractButtonItem implements UIConstants {

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
		this.title = p.getTitle();
		this.author = p.getAuthor();
		this.videosStr = Locale.videos(p.getVideoCount());
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setColor(-1);
		g.fillRect(x, y, w, h);
		g.setColor(0);
		g.setFont(mediumfont);
		int yy = y;
		y += 2;
		if(titleArr != null) {
			if(titleArr[0] != null) g.drawString(titleArr[0], x+2, y, 0);
			if(titleArr[1] != null) g.drawString(titleArr[1], x+2, y += mediumfontheight + 2, 0);
		}
		g.setColor(COLOR_GRAYTEXT);
		g.setFont(smallfont);
		if(videosStr != null) {
			g.drawString(videosStr, x+2, y += mediumfontheight + 4, 0);
		}
		if(author != null) {
			g.drawString(author, x+2, y + smallfontheight + 2, 0);
		}
		g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
		g.drawLine(x, y+h-1, w, y+h-1);
		if(isInFocus() && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, yy, w-1, h-1);
			g.drawRect(x+1, yy+1, w-3, h-3);
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
		return textWidth = w;
	}
	
	public PlaylistModel getPlaylist() {
		return playlist;
	}

	protected void action() {
		ui.open(playlist, playlist.getContainerScreen() != null ? playlist.getContainerScreen() : getScreen());
	}

}
