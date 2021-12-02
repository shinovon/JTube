package ui.custom;

import javax.microedition.lcdui.Graphics;

import App;
import Locale;
import models.PlaylistModel;
import cc.nnproject.utils.PlatformUtils;

public class PlaylistItem extends CustomButtonItem implements UIConstants {

	private PlaylistModel playlist;
	
	private String title;
	private String author;
	private String videosStr;

	private int textWidth;

	private String[] titleArr;

	public PlaylistItem(PlaylistModel p) {
		super(p);
		this.playlist = p;
		this.title = p.getTitle();
		this.author = p.getAuthor();
		this.videosStr = Locale.videos(p.getVideoCount());
	}

	protected int getMinContentHeight() {
		return 10 + mediumfontheight + mediumfontheight + smallfontheight + smallfontheight;
	}

	protected int getMinContentWidth() {
		return App.width - 4;
	}

	protected int getPrefContentHeight(int i) {
		return getMinContentHeight();
	}

	protected int getPrefContentWidth(int i) {
		if(PlatformUtils.isKemulator) return i;
		return getMinContentWidth();
	}

	protected void paint(Graphics g, int w, int h) {
		width = w;
		height = h;
		g.setColor(-1);
		g.fillRect(0, 0, w, h);
		g.setColor(0);
		g.setFont(mediumfont);
		if(title != null && titleArr == null) {
			makeTitleArr();
		}
		int y = 2;
		if(titleArr != null) {
			if(titleArr[0] != null) g.drawString(titleArr[0], 2, y, 0);
			if(titleArr[1] != null) g.drawString(titleArr[1], 2, y += mediumfontheight, 0);
		}
		g.setColor(GRAYTEXT_COLOR);
		g.setFont(smallfont);
		if(videosStr != null) {
			g.drawString(videosStr, 2, y += mediumfontheight + 4, 0);
		}
		if(author != null) {
			g.drawString(author, 2, y + smallfontheight + 2, 0);
		}
	}
	
	private void makeTitleArr() {
		int w = getTextMaxWidth();
		String[] arr = VideoItem.getStringArray(title, w, mediumfont);
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
	
	private int getTextMaxWidth() {
		if(textWidth > 0) return textWidth;
		return textWidth = width;
	}
	
	public PlaylistModel getPlaylist() {
		return playlist;
	}

}
