package ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import ui.Commands;
import ui.AbstractListScreen;

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
