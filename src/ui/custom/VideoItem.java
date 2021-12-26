package ui.custom;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import Records;
import ui.AppUI;
import models.VideoModel;
import IScheduledShowHide;
import cc.nnproject.utils.PlatformUtils;

public class VideoItem extends CustomButtonItem implements IScheduledShowHide {
	
	private static int globalWidth;
	
	private VideoModel video;
	
	private String lengthStr;
	private String title;
	private String author;
	
	private String[] titleArr;

	private Image img;
	
	// cached values
	private int imgHeight;
	private int textWidth;

	private boolean drawn;
	
	private Object showmsg = new Object[] {this, new Boolean(true)};
	private Object hidemsg = new Object[] {this, new Boolean(false)};

	public VideoItem(VideoModel v) {
		super(v);
		this.video = v;
		this.lengthStr = timeStr(v.getLengthSeconds());
		this.title = v.getTitle();
		this.author = v.getAuthor();
	}

	protected void paint(Graphics g, int w, int h) {
		globalWidth = w;
		drawn = true;
		width = w;
		height = h;
		int ih = getImgHeight();
		g.setColor(-1);
		g.fillRect(0, 0, w, h);
		g.setColor(0);
		if(img != null) {
			g.drawImage(img, -2, 0, 0);
			if(PlatformUtils.isS40() || PlatformUtils.isS603rd() || !PlatformUtils.isNotS60()) disposeImage();
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
	
	private void disposeImage() {
		img = null;
	}

	private void makeTitleArr() {
		int w = getTextMaxWidth();
		String[] arr = getStringArray(title, w, mediumfont);
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
		int w = width - 6;
		if(w <= 0) {
			w = App.width - 10;
		}
		int i;
		if(lengthStr != null) {
			i = smallfont.stringWidth(lengthStr) + 10;
		} else {
			i = smallfont.stringWidth(" AA:AA:AA") + 10;
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
		return App.width - AppUI.getPlatformWidthOffset();
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
		//invalidate();
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
			return h + ":" + m + ":" + s;
		} else {
			return m + ":" + s;
		}
	}
	
	protected void showNotify() {
		//System.out.println("showNotify " + toString());
		if(App.rmsPreviews) {
			App.inst.schedule(showmsg);
		}
	}
	
	protected void hideNotify() {
		//System.out.println("hideNotify " + toString());
		if(App.rmsPreviews) {
			App.inst.schedule(hidemsg);
		}
	}
	
	public VideoModel getVideo() {
		return video;
	}
	
	static String[] getStringArray(String text, int maxWidth, Font font) {
		if (text == null || text.length() == 0 || text.equals(" ")) {
			return new String[0];
		}
		final int max = 3;
		Vector v = new Vector(max);
		v: {
			if (font.stringWidth(text) > maxWidth) {
				int i1 = 0;
				for (int i2 = 0; i2 < text.length(); i2++) {
					if(v.size() >= max) break v;
					if (text.charAt(i2) == '\n') {
						v.addElement(text.substring(i1, i2));
						i2 = i1 = i2 + 1;
					} else {
						if (text.length() - i2 <= 1) {
							v.addElement(text.substring(i1, text.length()));
							break;
						} else if (font.stringWidth(text.substring(i1, i2)) >= maxWidth) {
							boolean space = false;
							for (int j = i2; j > i1; j--) {
								char c = text.charAt(j);
								if (c == ' ' || (c >= ',' && c <= '/')) {
									String s = text.substring(i1, j + 1);
									if(font.stringWidth(s) >= maxWidth - 1) {
										continue;
									}
									space = true;
									v.addElement(s);
									i2 = i1 = j + 1;
									break;
								}
							}
							if (!space) {
								i2 = i2 - 2;
								v.addElement(text.substring(i1, i2));
								i2 = i1 = i2 + 1;
							}
						}
					}
				}
			} else {
				return new String[] { text };
			}
		}
		String[] arr = new String[v.size()];
		v.copyInto(arr);
		return arr;
	}

	public Image getImage() {
		return img;
	}

	public static int getImageWidth() {
		if(globalWidth > 0) return globalWidth;
		return App.width - 4;
	}

	public void show() {
		try {
			if(!drawn) return;
			Image img = Records.saveOrGetImage(video.getVideoId(), null);
			if(img == null) {
				//System.out.println("img null " + VideoItem.this.toString());
				return;
			}
			//System.out.println("img " + VideoItem.this.toString());
			setImage(video.customResize(img));
		} catch (Exception e) {
		}
		
	}

	public void hide() {
		try {
			if(!drawn) return;
			setImage(null);
		} catch (Exception e) {
		}
	}

}
