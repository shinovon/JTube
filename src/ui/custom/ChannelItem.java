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
package ui.custom;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import Locale;
import cc.nnproject.utils.PlatformUtils;
import models.ChannelModel;

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
		boolean i = false;
		if(img != null) {
			i = true;
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
		int x = i ? 56 : 2;
		g.drawString(author, x, ty, 0);
		g.setColor(GRAYTEXT_COLOR);
		g.setFont(smallfont);
		if(subsStr != null) {
			g.drawString(subsStr, x, ty + fh + 4, 0);
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
