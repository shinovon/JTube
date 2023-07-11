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
package jtube.ui.items;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import jtube.RunnableTask;
import jtube.Util;
import jtube.models.VideoModel;
import jtube.ui.LocaleConstants;
import jtube.ui.UIConstants;

public class VideoPreview extends AbstractButton implements UIConstants {

	private VideoModel video;

	private String length;
	private Image img;
	private int h;

	private int lastW;
	
	public VideoPreview(VideoModel v, Image img) {
		this.video = v;
		this.img = img;
		this.length = Util.timeStr(v.lengthSeconds);
	}

	protected void action() {
		new Thread(new RunnableTask(video.videoId, RunnableTask.WATCH)).start();
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setColor(0);
		g.fillRect(x, y, w, h);
		int iw = w;
		if(img != null) {
			iw = img.getWidth();
			g.drawImage(img, x+(w-iw) >> 1, y, 0);
		}
		g.setFont(smallfont);
		if(length != null) {
			int xx = iw+((w-iw) >> 1)+x-(smallfont.stringWidth(length))-4;
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
		int sw = w;
		if(w > sh) {
			h = (int) (sh * 0.9f);
			w = (int) (h * 16F / 9F);
		}
		if(w != lastW) {
			if(img != null) img = video.previewResize(Math.min(w, sw), img);
		}
		if(img != null) {
			h = img.getHeight();
		}
	}

	public void setImage(Image img) {
		this.img = img;
		relayout();
	}
	
	public int getOKLabel() {
		return LocaleConstants.CMD_Watch;
	}

}
