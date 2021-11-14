package cc.nnproject.ytapp;

import javax.microedition.midlet.MIDlet;

import App;

public class App2 extends MIDlet {

	private static boolean started;

	protected void destroyApp(boolean b) {}

	protected void pauseApp() {}

	protected void startApp() {
		if(started) return;
		App.midlet = this;
		started = true;
		App.inst = new App();
		App.inst.startApp();
	}

}
