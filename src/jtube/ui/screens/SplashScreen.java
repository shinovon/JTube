/*
Copyright (c) 2023 Arman Jussupgaliyev

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
