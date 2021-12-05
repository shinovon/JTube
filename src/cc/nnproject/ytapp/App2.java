package cc.nnproject.ytapp;

import javax.microedition.midlet.MIDlet;

import App;

public class App2 extends MIDlet {

	private static boolean started;
	public boolean running;

	protected void destroyApp(boolean b) {
		running = false;
	}

	protected void pauseApp() {}

	protected void startApp() {
		if(started) return;
		App.midlet = this;
		started = true;
		running = true;
		App.inst = new App();
		App.inst.startApp();
	}

}
