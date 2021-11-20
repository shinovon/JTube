package ui.custom;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import Locale;
import cc.nnproject.utils.PlatformUtils;
import models.ChannelModel;
import models.VideoModel;

public class ChannelItem extends CustomButtonItem {

	private ChannelModel channel;

	private String author;

	private Image img;

	private String subsStr;

	public ChannelItem(ChannelModel c) {
		super(c);
		this.channel = c;
		this.img = c.getImg();
		this.author = c.getAuthor();
		subsStr = Locale.subscribers(c.getSubCount());
	}

	protected void paint(Graphics g, int w, int h) {
		width = w;
		height = h;
		g.setColor(-1);
		g.fillRect(0, 0, w, h);
		if(img != null) {
			g.drawImage(img, 2, 2, 0);
		}
		g.setColor(0);
		g.setFont(mediumfont);
		int fh = mediumfontheight;
		int sfh = smallfontheight;
		int ty = (52 - fh) / 2;
		if(subsStr != null) {
			ty -= (sfh + 4) / 2;
		}
		g.drawString(author, 56, ty, 0);
		g.setColor(GRAYTEXT_COLOR);
		g.setFont(smallfont);
		if(subsStr != null) {
			g.drawString(subsStr, 56, ty + fh + 4, 0);
		}
	}

	protected int getMinContentHeight() {
		return 48 + 4;
	}

	protected int getMinContentWidth() {
		return App.width - 4;
	}

	protected int getPrefContentHeight(int i) {
		return 48 + 4;
	}

	protected int getPrefContentWidth(int i) {
		if(PlatformUtils.isKemulator) return i;
		return getMinContentWidth();
	}

	public void setImage(Image img) {
		this.img = img;
		repaint();
	}
	
	public ChannelModel getChannel() {
		return channel;
	}
	
	protected void showNotify() {
	}
	
	protected void hideNotify() {
	}

}
