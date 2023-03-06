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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import Locale;
import LocaleConstants;
import ui.AppUI;
import ui.IModelScreen;
import ui.nokia_extensions.DirectFontUtil;
import ui.UIScreen;
import ui.UIConstants;
import models.VideoModel;
import tube42.lib.imagelib.ImageUtils;
import models.ChannelModel;
import ui.screens.ChannelScreen;
import ui.screens.VideoScreen;

public class ChannelItem extends AbstractButtonItem implements UIConstants {

	private static Image defaultImg;
	private static Image defaultImg36;

	private ChannelModel channel;

	private String author;

	private Image img;

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
		if(c.getImg() != null) {
			if(!c.isImageRounded()) {
				this.img = roundImage(c.getImg());
				c.setImage(img, true);
			} else {
				int s = c.hasSmallImage() ? 36 : 48;
				this.img = ImageUtils.resize(c.getImg(), s, s);
				c.setImage(img, true);
			}
		}
		this.author = c.getAuthor();
		subsStr = Locale.subscribers(c.getSubCount());
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		boolean small = channel.hasSmallImage() && getScreen() instanceof IModelScreen;
		int iw = small ? 36 : 48;
		g.drawImage(img != null ? img : small ? defaultImg36 : defaultImg, x + 4, y + ((h - iw) >> 1), 0);
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
		if(titleFont == null) {
			titleFont = App.width >= 360 ? DirectFontUtil.getFont(0, 0, 24, Font.SIZE_SMALL) : smallfont;
			titleSmallFont = App.width >= 360 ? DirectFontUtil.getFont(0, 0, 19, Font.SIZE_SMALL) : smallfont;
			subsCountFont = App.width >= 360 ? DirectFontUtil.getFont(0, 0, 21, Font.SIZE_SMALL) : smallfont;
			subsCountSmallFont = App.width >= 360 ? DirectFontUtil.getFont(0, 0, 18, Font.SIZE_SMALL) : smallfont;
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
			ui.setScreen(s);
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
