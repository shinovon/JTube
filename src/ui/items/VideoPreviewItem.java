package ui.items;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import ui.UIConstants;

public class VideoPreviewItem extends AbstractButtonItem implements UIConstants {

	private String videoId;
	
	private Image img;
	private int h;
	
	public VideoPreviewItem(String id, Image img) {
		this.videoId = id;
		this.img = img;
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
