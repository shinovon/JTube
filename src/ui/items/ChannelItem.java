package ui.items;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import Locale;
import ui.UIItem;
import ui.UIConstants;
import models.ChannelModel;

public class ChannelItem extends UIItem implements UIConstants {

	private ChannelModel channel;

	private String author;

	private Image img;

	private String subsStr;
	
	private int h;

	public ChannelItem(ChannelModel c) {
		super(null);
		this.channel = c;
		this.img = c.getImg();
		this.author = c.getAuthor();
		subsStr = Locale.subscribers(c.getSubCount());
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setColor(-1);
		g.fillRect(x, y, w, h);
		boolean i = false;
		if (img != null) {
			i = true;
			g.drawImage(img, x+2, y+2, 0);
		}
		g.setColor(0);
		g.setFont(mediumfont);
		int fh = mediumfontheight;
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
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = 52;
	}

	public void setImage(Image img) {
		this.img = img;
		repaint();
	}
	
	public ChannelModel getChannel() {
		return channel;
	}

}
