package ui.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import Util;
import Locale;
import Records;
import Settings;
import ui.AppUI;
import ui.DirectFontUtil;
import ui.UIConstants;
import ui.screens.SearchScreen;
import models.VideoModel;

public class VideoItem extends AbstractButtonItem implements UIConstants, Runnable {
	
	private VideoModel video;
	
	private String title;
	private String author;
	private int views;
	private String published;

	private String[] titleArr;
	private String lengthStr;
	private String bottomText;

	private Image img;
	private int h;
	
	private int imgHeight;
	private int textWidth;

	private int lastW;

	private static Font viewsFont;
	
	public VideoItem(VideoModel v) {
		super();
		this.video = v;
		this.lengthStr = Util.timeStr(v.getLengthSeconds());
		this.title = v.getTitle();
		this.author = v.getAuthor();
		this.views = v.getViewCount();
		this.published = v.getPublishedText();
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		int ih = Settings.videoPreviews ? getImgHeight(w) : 0;
		g.setColor(AppUI.getColor(COLOR_MAINBACKGROUND));
		g.fillRect(x, y, w, h);
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
		g.setColor(0);
		if(Settings.videoPreviews) {
			if(img != null) {
				g.drawImage(img, x, y, 0);
				//if(Settings.rmsPreviews)
				//	img = null;
			} else if(ih != 0) {
				g.fillRect(x, y, w, ih);
			}
		}
		g.setColor(AppUI.getColor(COLOR_MAINFOREGROUND));
		ih += 4;
		g.setFont(mediumfont);
		int tfh = mediumfontheight + 2;
		int t = ih+y;
		if(title != null && titleArr == null) {
			makeTitleArr(w);
		}
		if(titleArr != null) {
			if(titleArr[0] != null)
				g.drawString(titleArr[0], x + 4, t, 0);
			if(titleArr.length > 1 && titleArr[1] != null)
				g.drawString(titleArr[1], x + 4, t += tfh, 0);
		}
		g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
		g.setFont(smallfont);
		if(author != null) {
			g.drawString(author, x + 4, t += tfh, 0);
		}
		if(lengthStr != null) {
			int sw = smallfont.stringWidth(lengthStr) + 4;
			g.drawString(lengthStr, x + w - sw, y + ih, 0);
		}
		if(viewsFont != null && bottomText != null) {
			g.setFont(viewsFont);
			g.drawString(bottomText, x + 4, t += smallfontheight + 2, 0);
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
				if(titleArr.length == 1) {
					titleArr[0] = arr[0].trim().concat("...");
				} else if(arr.length > 1) {
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
		if(getScreen() != null && w > getScreen().getHeight()) {
			
		}
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
		int i = 0;
		if(bottomText != null) {
			if(viewsFont == null)
				viewsFont = DirectFontUtil.getFont(0, 0, 21, 8);;
			i = 2 + viewsFont.getHeight();
		}
		int h = (mediumfontheight + 2) * 2 + 8 + smallfontheight + i;
		return h;
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
		if(bottomText == null && (views > 0 || published != null) && ui.getWidth() >= 320) {
			String s = Locale.views(views);
			if(published != null) {
				s = s.concat(" • ").concat(Locale.date(published));
				published = null;
			}
			bottomText = s;
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
