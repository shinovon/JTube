package ui.items;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import Locale;
import ui.AppUI;
import ui.UIConstants;
import models.ChannelModel;
import ui.screens.VideoScreen;

public class ChannelItem extends AbstractButtonItem implements UIConstants {

	private ChannelModel channel;

	private String author;

	private Image img;

	private String subsStr;
	
	private int h;

	public ChannelItem(ChannelModel c) {
		super();
		this.channel = c;
		this.img = roundImage(c.getImg());
		this.author = c.getAuthor();
		subsStr = Locale.subscribers(c.getSubCount());
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setColor(AppUI.getColor(COLOR_MAINBACKGROUND));
		g.fillRect(x, y, w, h);
		boolean i = false;
		if (img != null) {
			i = true;
			g.drawImage(img, x+2, y+2, 0);
		}
		g.setColor(AppUI.getColor(COLOR_MAINFOREGROUND));
		g.setFont(smallfont);
		int fh = smallfontheight;
		int sfh = smallfontheight;
		int ty = y+((52 - fh) / 2);
		if(subsStr != null) {
			ty -= (sfh + 4) / 2;
		}
		int xx = x+(i ? 56 : 2);
		g.drawString(author, xx, ty, 0);
		g.setColor(COLOR_GRAYTEXT);
		g.setFont(smallfont);
		if(subsStr != null) {
			g.drawString(subsStr, xx, ty + fh + 4, 0);
		}
		if(!(getScreen() instanceof VideoScreen)) {
			g.setColor(AppUI.getColor(COLOR_ITEMBORDER));
			g.drawRect(x, y+h-1, w, 1);
		}
		if(isInFocus() && ui.isKeyInputMode()) {
			g.setColor(AppUI.getColor(COLOR_ITEM_HIGHLIGHT));
			g.drawRect(x, y, w-1, h-1);
			g.drawRect(x+1, y+1, w-3, h-3);
		}
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = 52;
	}

	public void setImage(Image img) {
		this.img = roundImage(img);
		repaint();
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
		
	}

}
