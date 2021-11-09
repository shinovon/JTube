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
