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
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

public class PlayerCanvas extends Canvas {

	private Player p;
	private VideoControl videoControl;
	private boolean started;

	public PlayerCanvas(Player p) {
		setFullScreenMode(true);
		this.p = p;
	}

	protected void paint(Graphics g) {
		g.drawString("player", 10, 10, 0);
	}

	public void init() throws MediaException {
		p.realize();
		videoControl = (VideoControl) p.getControl("javax.microedition.media.control.VideoControl");
		videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, this);
		p.prefetch();
		p.start();
		started = true;
	}
	
	public void close() {
		try {
			p.stop();
		} catch (MediaException e) {
		}
		p.deallocate();
		p.close();
	}
	
	public void playpause() throws MediaException {
		if(started) {
			p.stop();
		} else {
			p.start();
		}
	}

	public void keyPressed(int i) {
		try {
			if (i == -5) playpause();
			if (i == -7) close();
		} catch (MediaException e) {
			e.printStackTrace();
		}
	}

}
