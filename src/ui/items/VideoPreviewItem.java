package ui.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import Util;
import ui.AppUI;
import ui.UIConstants;

public class VideoPreviewItem extends AbstractButtonItem implements UIConstants {

	private String videoId;

	private String length;
	private Image img;
	private int h;

	
	public VideoPreviewItem(String id, Image img, int length) {
		this.videoId = id;
		this.img = img;
		this.length = Util.timeStr(length);
	}

	protected void action() {
		App.watch(videoId);
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		layout(w);
		g.setColor(-1);
		g.fillRect(x, y, w, h);
		g.setColor(0);
		if(img != null) {
			g.drawImage(img, x, y, 0);
		} else {
			g.fillRect(x, y, w, h);
		}
		g.setFont(smallfont);
		if(length != null) {
			Font f = g.getFont();
			int xx = w+x-(f.stringWidth(length))-4;
			int yy = y+h-f.getHeight()-2;
			g.setColor(0);
			g.drawString(length, xx+2, yy, 0);
			g.drawString(length, xx, yy, 0);
			g.drawString(length, xx+1, yy - 1, 0);
			g.drawString(length, xx+1, yy + 1, 0);
			g.setColor(-1);
			g.drawString(length, xx+1, yy, 0);
		}
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = w * 9 / 16;
		if(img != null) {
			h = img.getHeight();
		}
	}

	public void setImage(Image img) {
		this.img = img;
		repaint();
	}

}
