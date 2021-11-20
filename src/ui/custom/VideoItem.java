package ui.custom;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import cc.nnproject.utils.PlatformUtils;
import models.VideoModel;

public class VideoItem extends CustomButtonItem {
	
	private VideoModel video;
	
	private String lengthStr;
	private String title;
	private String author;
	
	private String[] titleArr;

	private Image img;
	
	// cached values
	private int imgHeight;
	private int textWidth;

	// XXX: DEBUG
	private static int count;
	private int id;

	public VideoItem(VideoModel v) {
		super(v);
		this.video = v;
		this.img = v.getCachedImage();
		this.lengthStr = timeStr(v.getLengthSeconds());
		this.title = v.getTitle();
		this.author = v.getAuthor();
		id = count++;
	}

	protected void paint(Graphics g, int w, int h) {
		width = w;
		height = h;
		int ih = getImgHeight();
		g.setColor(-1);
		g.fillRect(0, 0, w, h);
		g.setColor(0);
		if(img != null) {
			g.drawImage(img, 0, 0, 0);
		} else {
			g.fillRect(0, 0, w, ih);
		}
		ih += 4;
		g.setFont(mediumfont);
		int tfh = mediumfontheight;
		int t = ih;
		if(title != null && titleArr == null) {
			makeTitleArr();
		}
		if(titleArr != null) {
			if(titleArr[0] != null) g.drawString(titleArr[0], 4, ih, 0);
			if(titleArr[1] != null) g.drawString(titleArr[1], 4, t += tfh, 0);
		}
		g.setColor(GRAYTEXT_COLOR);
		g.setFont(smallfont);
		if(author != null) {
			g.drawString(author, 4, t + tfh, 0);
		}
		if(lengthStr != null) {
			int sw = smallfont.stringWidth(lengthStr) + 4;
			g.drawString(lengthStr, w - sw, ih, 0);
		}
	}
	
	private void makeTitleArr() {
		int w = getTextMaxWidth();
		String[] arr = getStringArray(title, w, mediumfont);
		titleArr = new String[2];
		if(arr.length > 0) {
			titleArr[0] = arr[0];
			if(arr.length > 1) {
				titleArr[1] = arr[1];
			}
		}
		title = null;
	}

	private int getImgHeight() {
		if(imgHeight > 0) return imgHeight;
		int ih = App.width * 9 / 16;
		if(img != null) {
			ih = img.getHeight();
			imgHeight = ih;
		}
		return ih;
	}
	
	private int getTextMaxWidth() {
		if(textWidth > 0) return textWidth;
		int w = width;
		if(w <= 0) {
			w = App.width - 4;
		}
		int i;
		if(lengthStr != null) {
			i = smallfont.stringWidth(lengthStr) + 8;
		} else {
			i = smallfont.stringWidth(" AA:AA:AA") + 8;
		}
		return textWidth = w - i;
	}
	
	private int getTextHeight() {
		return (mediumfontheight + 4) * 2 + 4 + smallfontheight;
	}

	protected int getMinContentHeight() {
		return getImgHeight() + getTextHeight();
	}

	protected int getMinContentWidth() {
		return App.width - 4;
	}

	protected int getPrefContentHeight(int i) {
		//if(w > 1) return (w * 9 / 16) + getTextHeight();
		return getMinContentHeight();
	}

	protected int getPrefContentWidth(int i) {
		if(PlatformUtils.isKemulator) return i;
		//if(h > 1) return (h * 16 / 9) + getTextHeight();
		return getMinContentWidth();
	}

	public void setImage(Image img) {
		this.img = img;
		invalidate();
		repaint();
	}

	private static String timeStr(int i) {
		if(i <= 0) return null;
		String s = "" + i % 60;
		if(s.length() < 2) s = "0" + s;
		String m = "" + (i % 3600) / 60;
		if(m.length() < 2) m = "0" + m;
		int h = i / 3600;
		if(h > 0) {
			if(h >= 10) {
				return "0" + h + ":" + m + ":" + s;
			} else {
				return h + ":" + m + ":" + s;
			}
		} else {
			return m + ":" + s;
		}
	}
	
	protected void showNotify() {
	}
	
	protected void hideNotify() {
	}

	// XXX: DEBUG
	public String toString() {
		return "VideoModel" + id;
	}
	
	public VideoModel getVideo() {
		return video;
	}
	
	private static String[] getStringArray(String text, int maxWidth, Font font) {
		if (text == null || text.length() == 0 || text.equals(" ")) {
			return new String[0];
		}
		Vector v = new Vector(3);
		v.addElement(text);
		for (int i = 0; i < v.size(); i++) {
			String s = (String) v.elementAt(i);
			if(font.stringWidth(s) >= maxWidth) {
				int i1 = 0;
				for (int i2 = 0; i2 < s.length(); i2++) {
					if (font.stringWidth(s.substring(i1, i2)) >= maxWidth) {
						boolean space = false;
						for (int j = i2; j > i1; j--) {
							char c = s.charAt(j);
							if (c == ' ' || (c >= ',' && c <= '/')) {
								space = true;
								v.setElementAt(s.substring(i1, j + 1), i);
								v.insertElementAt(s.substring(j + 1), i + 1);
								i += 1;
								i2 = i1 = j + 1;
								break;
							}
						}
						if (!space) {
							i2 = i2 - 2;
							v.setElementAt(s.substring(i1, i2), i);
							v.insertElementAt(s.substring(i2), i +1);
							i2 = i1 = i2 + 1;
							i += 1;
						}
					}
				}
			}
		}
		String[] arr = new String[v.size()];
		v.copyInto(arr);
		return arr;
	}

}
