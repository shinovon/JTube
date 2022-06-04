package ui.items;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import Util;
import Records;
import Settings;
import ui.AppUI;
import ui.UIConstants;
import ui.screens.SearchScreen;
import models.VideoModel;

public class VideoItem extends AbstractButtonItem implements UIConstants, Runnable {
	
	private VideoModel video;
	
	private String lengthStr;
	private String title;
	private String author;
	
	private String[] titleArr;

	private Image img;
	private int h;
	
	private int imgHeight;
	private int textWidth;

	private int lastW;
	
	public VideoItem(VideoModel v) {
		super();
		this.video = v;
		this.lengthStr = Util.timeStr(v.getLengthSeconds());
		this.title = v.getTitle();
		this.author = v.getAuthor();
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		int ih = Settings.videoPreviews ? getImgHeight(w) : 0;
		g.setColor(AppUI.getColor(COLOR_MAINBACKGROUND));
		g.fillRect(x, y, w, h);
		g.setColor(AppUI.getColor(COLOR_MAINFOREGROUND));
		/*
		if(img == null && Settings.rmsPreviews) {
			try {
				img = Records.saveOrGetImage(video.getVideoId(), null);
				if(img != null)
					img = video.customResize(img);
			} catch (IOException e) {
			}
		}
		*/
		if(Settings.videoPreviews) {
			if(img != null) {
				g.drawImage(img, x, y, 0);
				//if(Settings.rmsPreviews)
				//	img = null;
			} else if(ih != 0) {
				g.fillRect(x, y, w, ih);
			}
		}
		ih += 4;
		g.setFont(mediumfont);
		int tfh = mediumfontheight;
		int t = ih;
		if(title != null && titleArr == null) {
			makeTitleArr(w);
		}
		if(titleArr != null) {
			if(titleArr[0] != null) g.drawString(titleArr[0], x + 4, y + ih, 0);
			if(titleArr[1] != null) g.drawString(titleArr[1], x+ 4, y + (t += tfh), 0);
		}
		g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
		g.setFont(smallfont);
		if(author != null) {
			g.drawString(author, x + 4, y + t + tfh, 0);
		}
		if(lengthStr != null) {
			int sw = smallfont.stringWidth(lengthStr) + 4;
			g.drawString(lengthStr, x + w - sw, y+ ih, 0);
		}
		g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
		g.drawRect(x, y+h-1, w, 1);
		if(isInFocus() && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, y, w-1, h-1);
			g.drawRect(x+1, y+1, w-3, h-3);
		}
	}

	private void makeTitleArr(int sw) {
		int w = getTextMaxWidth(sw);
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

	private int getImgHeight(int w) {
		if(imgHeight > 0) return imgHeight;
		int ih = w * 9 / 16;
		if(img != null) {
			ih = img.getHeight();
			imgHeight = ih;
		}
		return ih;
	}
	
	private int getTextMaxWidth(int w) {
		if(textWidth > 0) return textWidth;
		int i;
		if(lengthStr != null) {
			i = smallfont.stringWidth(lengthStr) + 10;
		} else {
			i = smallfont.stringWidth(" AA:AA:AA") + 10;
		}
		return textWidth = w - 6 - i;
	}
	
	private int getTextHeight() {
		return (mediumfontheight + 4) * 2 + 4 + smallfontheight;
	}

	public int getHeight() {
		return h;
	}

	public void setImage(Image img) {
		this.img = img;
		repaint();
	}

	protected void layout(int w) {
		if(w != lastW) {
			imgHeight = 0;
			video.setImageWidth(w);
			if(img != null)
			img = video.customResize(img);
		}
		lastW = w;
		h = (Settings.videoPreviews ? getImgHeight(w) : 0) + getTextHeight();
	}

	protected void action() {
		ui.open(video, video.getContainerScreen() != null ? video.getContainerScreen() : getScreen() instanceof SearchScreen ? null : getScreen());
	}

	public Image getImage() {
		return img;
	}
	
	public void onShow() {
		super.onShow();
		if(Settings.rmsPreviews) {
			App.inst.schedule(this);
		}
	}
	
	public void onHide() {
		super.onHide();
		if(Settings.rmsPreviews) {
			img = null;
			App.inst.cancel(this);
		}
	}

	public void run() {
		Util.gc();
		if(img == null && Settings.rmsPreviews) {
			try {
				Image i = Records.saveOrGetImage(video.getVideoId(), null);
				if(i != null)
					img = video.customResize(i);
				repaint();
			} catch (Exception e) {
			}
		}
	}

}
