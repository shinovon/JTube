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
import jtube.models.ChannelModel;
import jtube.models.VideoModel;
import jtube.ui.AppUI;
import jtube.ui.IModelScreen;
import jtube.ui.Locale;
import jtube.ui.LocaleConstants;
import jtube.ui.UIConstants;
import jtube.ui.UIScreen;
import jtube.ui.nokia_extensions.DirectFontUtil;
import jtube.ui.screens.ChannelScreen;
import jtube.ui.screens.VideoScreen;
import tube42.lib.imagelib.ImageUtils;

public class ChannelItem extends AbstractButton implements UIConstants {

	private static Image defaultImg;
	private static Image defaultImg36;

	private ChannelModel channel;

	private String author;

	private Image img;
	private Image bannerImg;
	private int bannerWidth;

	private String subsStr;
	
	private int h;
	
	private static Font titleFont;
	private static Font titleSmallFont;
	private static Font subsCountFont;
	private static Font subsCountSmallFont;

	static {
		try {
			defaultImg = roundImage(Image.createImage("".getClass().getResourceAsStream("/user48.png")));
			defaultImg36 = roundImage(Image.createImage("".getClass().getResourceAsStream("/user36.png")));
		} catch (Exception e) {
		}
	}

	public ChannelItem(ChannelModel c) {
		super();
		this.channel = c;
		if(c.img != null) {
			if(!c.rounded) {
				this.img = roundImage(c.img);
				c.setImage(img, true);
			} else {
				int s = c.hasSmallImage ? 36 : 48;
				this.img = ImageUtils.resize(c.img, s, s);
				c.setImage(img, true);
			}
		}
		this.author = c.author;
		subsStr = Locale.subscribers(c.subCount);
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		boolean page = getScreen() instanceof ChannelScreen;
		if(page) {
			g.setColor(AppUI.getColor(COLOR_CHANNELPAGE_BG));
			g.fillRect(x, y, w, h);
			g.setColor(AppUI.getColor(COLOR_MAINFG));
			if(channel.bannerImg != null) {
				if(bannerImg == null) {
					bannerWidth = (bannerImg = channel.bannerImg).getWidth();
					relayout();
				}
				if(bannerWidth != w && (bannerImg = channel.bannerImg).getWidth() != w) {
					bannerImg = ImageUtils.resize(bannerImg, w, (int)(w*bannerImg.getHeight()/(float)bannerImg.getWidth()));
				}
				bannerWidth = w;
				g.drawImage(bannerImg, w >> 1, y, Graphics.HCENTER | Graphics.TOP);
				y += bannerImg.getHeight();
			}
			g.drawImage(img != null ? img : defaultImg, x + (w >> 1), y + (56 >> 1), Graphics.HCENTER | Graphics.VCENTER);
			y += 56;
			g.setFont(Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_LARGE));
			g.drawString(author, x + (w >> 1), y + 8, Graphics.HCENTER | Graphics.TOP);
			y += 16 + g.getFont().getHeight();
			g.setFont(subsCountFont);
			g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
			g.drawString(subsStr, x + (w >> 1), y, Graphics.HCENTER | Graphics.TOP);
			return;
		}
		boolean small = channel.hasSmallImage && getScreen() instanceof IModelScreen;
		int iw = small ? 36 : 48;
		g.drawImage(img != null ? img : small ? defaultImg36 : defaultImg, x + 4, y + (h >> 1), Graphics.LEFT | Graphics.VCENTER);
		g.setColor(AppUI.getColor(COLOR_MAINFG));
		Font f = small ? titleSmallFont : titleFont;
		g.setFont(f);
		int fh = f.getHeight();
		f = small ? subsCountSmallFont : subsCountFont;
		int sfh = f.getHeight();
		int ty = y + ((52 - fh) >> 1);
		if(subsStr != null) {
			ty -= (sfh + 4) >> 1;
		}
		int xx = x + 8 + iw;
		g.drawString(author, xx, ty, 0);
		g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
		g.setFont(f);
		if(subsStr != null) {
			g.drawString(subsStr, xx, ty + fh + 4, 0);
		}
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, y, w-1, h-1);
			//g.drawRect(x+1, y+1, w-3, h-3);
		}
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = 52;
		if(getScreen() instanceof ChannelScreen) {
			h = 0;
			if(channel.bannerImg != null) {
				h += (int)(w*channel.bannerImg.getHeight()/(float)channel.bannerImg.getWidth());
			}
			h += 56;
			h += 8 + 16 + Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_LARGE).getHeight();
			h += 8 + subsCountFont.getHeight();
		}
		if(titleFont == null) {
			titleFont = App.startWidth >= 360 ? DirectFontUtil.getFont(0, 0, 24, Font.SIZE_SMALL) : smallfont;
			titleSmallFont = App.startWidth >= 360 ? DirectFontUtil.getFont(0, 0, 19, Font.SIZE_SMALL) : smallfont;
			subsCountFont = App.startWidth >= 360 ? DirectFontUtil.getFont(0, 0, 21, Font.SIZE_SMALL) : smallfont;
			subsCountSmallFont = App.startWidth >= 360 ? DirectFontUtil.getFont(0, 0, 18, Font.SIZE_SMALL) : smallfont;
		}
	}
	
	public void onShow() {
		if(!channel.loaded) {
			channel.load();
		}
	}

	public void setImage(Image img) {
		if(img != null) {
			this.img = roundImage(img);
			channel.setImage(this.img, true);
			repaint();
		}
	}
	
	public ChannelModel getChannel() {
		return channel;
	}

	public static Image roundImage(Image img) {
		if(img == null) return null;
		try {
			int w = img.getWidth();
			int h = img.getHeight();
			int[] c = new int[w * h];
			img.getRGB(c, 0, w, 0, 0, w, h);
			for (int i = 0; i < h; i++) {
				float y = (float) (h / 2 - i) / (h - 1);
				y = y * 2;
				float xf = (float) Math.sqrt(1 - y * y);
				int x = (int) (xf * (w - 1));
				x = (w - x) / 2;
				for (int j = 0; j < x; j++) {
					c[i * w + j] = 0x00FFFFFF;
					c[i * w + w - j - 1] = 0x00FFFFFF;
				}
			}
			return Image.createRGBImage(c, w, h, true);
		} catch (Exception e) {
			return img;
		}
	}

	protected void action() {
		UIScreen s;
		if(getScreen() instanceof VideoScreen && (s = ((VideoModel) ((VideoScreen)getScreen()).getModel()).getContainerScreen()) instanceof ChannelScreen) {
			ui.nextScreen(s);
		} else {
			ui.open(channel);
		}
	}
	
	public int getOKLabel() {
		if(getScreen() instanceof VideoScreen) {
			return LocaleConstants.CMD_ViewChannel;
		}
		 return -1;
	}

}
