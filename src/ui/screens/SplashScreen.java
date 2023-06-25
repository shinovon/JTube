package ui.screens;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import App;

public class SplashScreen extends Canvas {
	
	public SplashScreen() {
		setFullScreenMode(true);
	}

	public void paint(Graphics g) {
		g.setColor(-1);
		g.fillRect(0, 0, getWidth(), getHeight());
		// TODO: иконку
	}
	
	public void keyPressed(int key) {
		if (key == -7) {
			App.midlet.notifyDestroyed();
		}
	}

}
