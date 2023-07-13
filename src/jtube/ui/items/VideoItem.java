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
package jtube.ui.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import jtube.App;
import jtube.Loader;
import jtube.LocalStorage;
import jtube.Settings;
import jtube.Util;
import jtube.models.VideoModel;
import jtube.ui.AppUI;
import jtube.ui.Locale;
import jtube.ui.UIConstants;
import jtube.ui.nokia_extensions.DirectFontUtil;
import jtube.ui.screens.SearchScreen;

public class VideoItem extends AbstractButton implements UIConstants, Runnable {
	
	private VideoModel video;
	
	private String title;
	private String author;
	private int views;

	private String[] titleArr;
	private String lengthStr;
	private String bottomText;

	private Image img;
	private int h;
	
	private static int imgHeight;
	private int textWidth;

	private int lastW;

	private boolean isSmall;

	private static Font titleFont;
	private static Font bottomFont;
	
	private static int titleFontHeight;
	private static int bottomFontHeight;
	
	public VideoItem(VideoModel v) {
		super();
		this.video = v;
		this.lengthStr = Util.timeStr(v.lengthSeconds);
		this.title = v.title;
		this.author = v.author;
		this.views = v.viewCount;
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		int ih = Settings.videoPreviews ? imgHeight > 0 ? imgHeight : getImgHeight(w) : 0;
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
			boolean b = w == 320;
			boolean b2 = w >= 480;
			boolean b3 = App.startWidth <= 240;
			boolean b4 = w >= 320;
			boolean b5 = w > 320;
			int xx = x + 4;
			int yy = y + 4;
			if(Settings.videoPreviews) {
				int iw = getImgWidth(w);
				if(img != null) {
					g.setColor(0);
					g.fillRect(xx, yy, iw, ih);
					g.drawImage(img, xx + ((iw - img.getWidth()) >> 1), yy + ((ih - img.getHeight()) >> 1), 0);
					//if(Settings.rmsPreviews)
					//	img = null;
				} else {
					g.setColor(0xE5E5E5);
					g.fillRect(xx, yy, iw, ih);
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
			if(b4) yy += 4;
			if(b2) xx += 2;
			if(titleArr != null) {
				if(titleArr[0] != null)
					g.drawString(titleArr[0], xx, yy, 0);
				if(titleArr[1] != null)
					g.drawString(titleArr[1], xx, yy += titleFontHeight, 0);
			}
			g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
			g.setFont(bottomFont);
			if(b3 || b5) yy += 4;
			if(author != null) {
				g.drawString(author, xx, yy += titleFontHeight, 0);
			}
			if(bottomFont != null && bottomText != null && (b || b2)) {
				g.drawString(bottomText, xx, yy += bottomFontHeight, 0);
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
			int t = ih+y;
			if(titleArr != null) {
				if(titleArr[0] != null)
					g.drawString(titleArr[0], x + 4, t, 0);
				if(titleArr.length > 1 && titleArr[1] != null)
					g.drawString(titleArr[1], x + 4, t += titleFontHeight, 0);
			}
			g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
			g.setFont(smallfont);
			if(lengthStr != null) {
				int sw = smallfont.stringWidth(lengthStr) + 4;
				g.drawString(lengthStr, x + w - sw, y + ih, 0);
			}
			g.setFont(bottomFont);
			if(author != null) {
				g.drawString(author, x + 4, t += titleFontHeight + 2, 0);
			}
			if(bottomFont != null && bottomText != null) {
				g.drawString(bottomText, x + 4, t += bottomFontHeight + 2, 0);
			}
		}
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, y, w-1, h-2);
			//g.drawRect(x+1, y+1, w-3, h-4);
		}
	}

	private void makeTitleArr(int sw) {
		int w = getTextMaxWidth(sw);
		String[] arr = Util.getStringArray(title, w, titleFont);
		titleArr = new String[2];
		if(arr.length > 0) {
			titleArr[0] = arr[0];
			if(arr.length > 1) {
				if(titleArr.length == 1) {
					titleArr[0] = arr[0].trim();
				} else if(arr.length > 1) {
					titleArr[1] = arr[1].trim();
				} else {
					titleArr[1] = arr[1];
				}
			}
		}
		if(Settings.smallPreviews && bottomFont.stringWidth(author) > w) {
			arr = Util.getStringArray(author, w, bottomFont);
			author = arr[0];
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
		return (titleFontHeight) * 2 + 8 + (2 + bottomFontHeight) * 2;
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
			else titleFont = smallfont;
		} else {
			titleFont = mediumfont;
			if(ui.getWidth() >= 360)
				titleFont = DirectFontUtil.getFont(0, 0, 25, Font.SIZE_MEDIUM);
		}
		titleFontHeight = titleFont.getHeight();
		bottomFont = smallfont;
		if(ui.getWidth() >= 360)
			bottomFont = DirectFontUtil.getFont(0, 0, 21, Font.SIZE_SMALL);
		bottomFontHeight = bottomFont.getHeight();
		if(w != lastW || Settings.smallPreviews != isSmall) {
			makeTitleArr(w);
			imgHeight = 0;
			if(Settings.videoPreviews) {
				video.imageWidth = getImgWidth(w);
				if(img != null) {
					if(Settings.rmsPreviews) {
						try {
							Image i = LocalStorage.loadAndCacheThumnail(video.videoId, null);
							if(i != null) img = video.customResize(i);
						} catch (Exception e) {
						}
					} else {
						img = video.customResize(img);
					}
				}
			}
		}
		if(ui.getWidth() >= 320 && bottomText == null && views > 0) {
			bottomText = Locale.views(views);
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
		if(!video.loaded) {
			Loader.add(video);
			Loader.start();
		}
		if(Settings.videoPreviews && Settings.rmsPreviews) {
			App.inst.schedule(this);
		}
	}
	
	public void onHide() {
		super.onHide();
		if(Settings.videoPreviews && !video.loaded) {
			Loader.cancel(video);
		}
		if(Settings.videoPreviews && Settings.rmsPreviews) {
			img = null;
			App.inst.cancel(this);
		}
	}

	public void run() {
		if(img == null && Settings.videoPreviews && Settings.rmsPreviews) {
			try {
				Image i = LocalStorage.loadAndCacheThumnail(video.videoId, null);
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
