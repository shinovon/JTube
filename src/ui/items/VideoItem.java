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

	private boolean isSmall;

	private static Font titleFont;
	private static Font bottomFont;
	
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
		if(Settings.smallPreviews) {
			boolean b = ui.getWidth() == 320;
			boolean b2 = ui.getWidth() >= 480;
			boolean b3 = App.width <= 240;
			boolean b4 = ui.getWidth() >= 320;
			boolean b5 = ui.getWidth() > 320;
			int xx = x + 4;
			int yy = y + 4;
			if(Settings.videoPreviews) {
				g.setColor(0);
				int iw = getImgWidth(w);
				g.fillRect(xx, yy, iw, ih);
				if(img != null) {
					g.drawImage(img, xx + (iw - img.getWidth()) / 2, yy + (ih - img.getHeight()) / 2, 0);
					//if(Settings.rmsPreviews)
					//	img = null;
				}
				if(lengthStr != null) {
					g.setColor(0);
					g.setFont(smallfont);
					int sw = smallfont.stringWidth(lengthStr);
					g.fillRect(xx + iw - sw - 4, yy + ih - smallfontheight - 4, sw + 2, smallfontheight + 2);
					g.setColor(AppUI.getColor(COLOR_TIMETEXT));
					g.drawString(lengthStr, xx + iw - sw - 3, yy + ih - smallfontheight - 3, 0);
				}
				xx += iw+4;
			}
			g.setColor(AppUI.getColor(COLOR_MAINFG));
			g.setFont(titleFont);
			int tfh = titleFont.getHeight();
			if(b4) yy += 4;
			if(b2) xx += 2;
			if(titleArr != null) {
				if(titleArr[0] != null)
					g.drawString(titleArr[0], xx, yy, 0);
				if(titleArr[1] != null)
					g.drawString(titleArr[1], xx, yy += tfh, 0);
			}
			g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
			g.setFont(bottomFont);
			if(b3 || b5) yy += 4;
			if(author != null) {
				g.drawString(author, xx, yy += tfh, 0);
			}
			if(bottomFont != null && bottomText != null && (b || b2)) {
				g.drawString(bottomText, xx, yy += bottomFont.getHeight(), 0);
			}
		} else {
			if(Settings.videoPreviews) {
				if(img != null) {
					g.drawImage(img, x, y, 0);
					//if(Settings.rmsPreviews)
					//	img = null;
				} else if(ih != 0) {
					g.fillRect(x, y, w, ih);
				}
			}
			g.setColor(AppUI.getColor(COLOR_MAINFG));
			ih += 4;
			g.setFont(titleFont);
			int tfh = titleFont.getHeight();
			int t = ih+y;
			if(titleArr != null) {
				if(titleArr[0] != null)
					g.drawString(titleArr[0], x + 4, t, 0);
				if(titleArr.length > 1 && titleArr[1] != null)
					g.drawString(titleArr[1], x + 4, t += tfh, 0);
			}
			g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
			g.setFont(smallfont);
			if(lengthStr != null) {
				int sw = smallfont.stringWidth(lengthStr) + 4;
				g.drawString(lengthStr, x + w - sw, y + ih, 0);
			}
			g.setFont(bottomFont);
			if(author != null) {
				g.drawString(author, x + 4, t += tfh + 2, 0);
			}
			if(bottomFont != null && bottomText != null) {
				g.drawString(bottomText, x + 4, t += bottomFont.getHeight() + 2, 0);
			}
		}
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, y, w-1, h-2);
			g.drawRect(x+1, y+1, w-3, h-4);
		}
		g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
		g.drawLine(x, y+h-1, w, y+h-1);
	}

	private void makeTitleArr(int sw) {
		int w = getTextMaxWidth(sw);
		String[] arr = Util.getStringArray(title, w, titleFont);
		titleArr = new String[2];
		if(arr.length > 0) {
			titleArr[0] = arr[0];
			if(arr.length > 1) {
				if(titleArr.length == 1) {
					titleArr[0] = arr[0].trim().concat("..");
				} else if(arr.length > 1) {
					titleArr[1] = arr[1].trim().concat("..");
				} else {
					titleArr[1] = arr[1];
				}
			}
		}
	}
	
	private int getImgWidth(int w) {
		if(Settings.smallPreviews) {
			return w / 3;
		} else {
			return w;
		}
	}

	private int getImgHeight(int w) {
		if(imgHeight > 0) return imgHeight;
		if(Settings.smallPreviews) {
			w = getImgWidth(w);
			if(ui.getWidth() > 480 && w > h)
				w = w * 9 / 16;
			else
				w = w * 3 / 4;
			return imgHeight = w;
		}
		int ih = w * 9 / 16;
		if(img != null) {
			ih = img.getHeight();
			imgHeight = ih;
		}
		return ih;
	}
	
	private int getTextMaxWidth(int w) {
		if(Settings.smallPreviews) {
			return w - getImgWidth(w) - 24;
		}
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
		return (titleFont.getHeight()) * 2 + 8 + (2 + bottomFont.getHeight()) * 2;
	}

	public int getHeight() {
		return h;
	}

	public void setImage(Image img) {
		this.img = img;
		repaint();
	}

	protected void layout(int w) {
		if(Settings.smallPreviews) {
			if(ui.getWidth() >= 360)
				titleFont = DirectFontUtil.getFont(0, Font.STYLE_BOLD, 25, Font.SIZE_SMALL);
			else titleFont = Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL);
		} else {
			titleFont = mediumfont;
			if(ui.getWidth() >= 360)
				titleFont = DirectFontUtil.getFont(0, 0, 25, Font.SIZE_MEDIUM);
		}
		bottomFont = smallfont;
		if(ui.getWidth() >= 360)
			bottomFont = DirectFontUtil.getFont(0, 0, 21, Font.SIZE_SMALL);
		if(w != lastW || Settings.smallPreviews != isSmall) {
			makeTitleArr(w);
			imgHeight = 0;
			video.setImageWidth(getImgWidth(w));
			if(img != null) {
				if(Settings.rmsPreviews) {
					try {
						Image i = Records.saveOrGetImage(video.getVideoId(), null);
						if(i != null) img = video.customResize(i);
					} catch (Exception e) {
					}
				} else {
					img = video.customResize(img);
				}
			}
		}
		if(ui.getWidth() >= 320 && bottomText == null && (views > 0 || published != null)) {
			String s = Locale.views(views);
			if(published != null) {
				s = s.concat(" • ").concat(Locale.date(published));
				published = null;
			}
			bottomText = s;
		}
		lastW = w;
		if(Settings.smallPreviews) {
			h = getImgHeight(w) + 8;
		} else {
			h = (Settings.videoPreviews ? getImgHeight(w) : 0) + getTextHeight();
		}
		isSmall = Settings.smallPreviews;
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
	
	public VideoModel getVideo() {
		return video;
	}

}
