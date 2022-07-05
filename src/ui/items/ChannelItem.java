package ui.items;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import Locale;
import ui.AppUI;
import ui.UIScreen;
import ui.UIConstants;
import models.VideoModel;
import models.ChannelModel;
import ui.screens.ChannelScreen;
import ui.screens.VideoScreen;

public class ChannelItem extends AbstractButtonItem implements UIConstants {

	private static Image defaultImg;

	private ChannelModel channel;

	private String author;

	private Image img;

	private String subsStr;
	
	private int h;
	
	static {
		try {
			defaultImg = roundImage(Image.createImage("".getClass().getResourceAsStream("/user.png")));
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
				this.img = c.getImg();
			}
		}
		this.author = c.getAuthor();
		subsStr = Locale.subscribers(c.getSubCount());
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.drawImage(img != null ? img : defaultImg, x+2, y+2, 0);
		g.setColor(AppUI.getColor(COLOR_MAINFG));
		g.setFont(smallfont);
		int fh = smallfontheight;
		int sfh = smallfontheight;
		int ty = y+((52 - fh) / 2);
		if(subsStr != null) {
			ty -= (sfh + 4) / 2;
		}
		int xx = x + 56;
		g.drawString(author, xx, ty, 0);
		g.setColor(AppUI.getColor(COLOR_GRAYTEXT));
		g.setFont(smallfont);
		if(subsStr != null) {
			g.drawString(subsStr, xx, ty + fh + 4, 0);
		}
		if(!(getScreen() instanceof VideoScreen)) {
			g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
			g.drawLine(x, y+h-1, w, y+h-1);
		}
		if(inFocus && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, y, w-1, h-1);
			g.drawRect(x+1, y+1, w-3, h-3);
		}
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = 53;
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

}
