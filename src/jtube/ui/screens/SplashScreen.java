package jtube.ui.screens;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import jtube.App;

public class SplashScreen extends Canvas {
	
	private Image splash;

	public SplashScreen() {
		setFullScreenMode(true);
		try {
			splash = Image.createImage("/splash.png");
		} catch (Exception e) {
		}
	}

	public void paint(Graphics g) {
		g.setColor(-1);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (splash == null)
			return;
		g.drawImage(splash, getWidth() >> 1, getHeight() >> 1, Graphics.VCENTER | Graphics.HCENTER);
		g.setColor(0);
	}
	
	public void keyPressed(int key) {
		if (key == -7) {
			App.midlet.notifyDestroyed();
		}
	}

}
