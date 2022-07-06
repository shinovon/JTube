package ui.items;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import Util;
import models.VideoModel;
import ui.UIConstants;

public class VideoPreviewItem extends AbstractButtonItem implements UIConstants {

	private VideoModel video;
	
	private String videoId;

	private String length;
	private Image img;
	private int h;

	private int lastW;
	
	public VideoPreviewItem(VideoModel v, Image img) {
		this.video = v;
		this.videoId = v.getVideoId();
		this.img = img;
		this.length = Util.timeStr(v.getLengthSeconds());
	}

	protected void action() {
		App.watch(videoId);
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setColor(0);
		g.fillRect(x, y, w, h);
		int iw = w;
		if(img != null) {
			iw = img.getWidth();
			g.drawImage(img, x+(w-iw)/2, y, 0);
		}
		g.setFont(smallfont);
		if(length != null) {
			int xx = iw+(w-iw)/2+x-(smallfont.stringWidth(length))-4;
			int yy = y+h-smallfontheight-2;
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
		int sh = ui.getHeight();
		if(w > sh) {
			h = (int) (sh * 0.9f);
			w = (int) (h * 16F / 9F);
		}
		if(w != lastW) {
			if(img != null) img = video.previewResize(w, img);
		}
		if(img != null) {
			h = img.getHeight();
		}
	}

	public void setImage(Image img) {
		this.img = img;
		relayout();
	}

}
