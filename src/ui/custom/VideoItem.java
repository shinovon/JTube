package ui.custom;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import models.VideoModel;

//TODO
public class VideoItem extends CustomItem {

	private static Font titlefont = Font.getFont(0, 0, Font.SIZE_MEDIUM);
	private static Font timefont = Font.getFont(0, 0, Font.SIZE_SMALL);
	private static Font authorfont = Font.getFont(0, 0, Font.SIZE_SMALL);
	
	private VideoModel video;
	
	private Image img;
	
	private String lengthStr;
	
	// cached image height
	private int imgHeight;

	public VideoItem(VideoModel v) {
		super(v.getTitle());
		this.video = v;
		this.img = v.getCachedImage();
		this.lengthStr = timeStr(v.getLengthSeconds());
	}

	protected void paint(Graphics g, int w, int h) {
		g.setColor(-1);
		g.fillRect(0, 0, w, h);
		g.setColor(0);
		g.fillRect(0, 0, w, getImgHeight());
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

	protected int getMinContentHeight() {
		int ih = getImgHeight();
		int th = titlefont.getHeight() + 4;
		return ih + th;
	}

	protected int getMinContentWidth() {
		return App.width;
	}

	protected int getPrefContentHeight(int w) {
		return getMinContentHeight();
	}

	protected int getPrefContentWidth(int h) {
		return getMinContentWidth();
	}

	public void setImage(Image img) {
		this.img = img;
		invalidate();
		repaint();
	}

	private static String timeStr(int i) {
		String res = "" + i % 60;
		
		return res;
	}

}
