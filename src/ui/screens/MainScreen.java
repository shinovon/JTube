package ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import App;
import Locale;
import Settings;
import ui.Commands;
import ui.AbstractListScreen;
import ui.AppUI;
import ui.items.VideoItem;

public class MainScreen extends AbstractListScreen implements Commands, CommandListener {
	
	private Command okCmd = new Command("OK", Command.OK, 5);
	
	private boolean okAdded;

	public MainScreen() {
		super("", null);
	}
	
	protected void show() {
		clearCommands();
		addCommand(optsCmd);
		addCommand(exitCmd);
		if(okAdded || ui.isKeyInputMode()) {
			okAdded = true;
			addCommand(okCmd);
		}
		if(Settings.videoPreviews) {
			// resume loading previews
			App.inst.stopAsyncTasks();
			for(int i = 0; i < items.size(); i++) {
				Object o = items.elementAt(i);
				if(o instanceof VideoItem) {
					App.inst.addAsyncLoad(((VideoItem)o).getVideo());
				}
			}
			App.inst.startAsyncTasks();
		}
	}
	
	public void paint(Graphics g, int w, int h) {
		if(AppUI.loadingState) {
			g.setColor(AppUI.getColor(COLOR_MAINBG));
			g.fillRect(0, 0, w, h);
			g.setColor(AppUI.getColor(COLOR_MAINFG));
			String s = Locale.s(TITLE_Loading) + "...";
			g.setFont(smallfont);
			g.drawString(s, (w-smallfont.stringWidth(s))/2, smallfontheight*2, 0);
			return;
		}
		super.paint(g, w, h);
	}
	
	public void keyPress(int i) {
		if(!okAdded && ((i >= -7 && i <= -1) || (i >= 1 && i <= 57))) {
			okAdded = true;
			addCommand(okCmd);
		}
		super.keyPress(i);
	}

	public boolean supportCommands() {
		return true;
	}

	public void commandAction(Command c, Displayable d) {
		if(c == okCmd) {
			keyPress(-5);
			return;
		}
		if(c == backCmd) {
			ui.display(null);
			return;
		}
		if(c == optsCmd) {
			ui.showOptions();
			return;
		}
	}

}
