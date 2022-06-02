package ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import ui.AbstractListScreen;
import ui.Commands;
import ui.UIScreen;

public class SearchScreen extends AbstractListScreen implements Commands, CommandListener {

	private boolean okAdded;
	
	private Command okCmd = new Command("OK", Command.OK, 5);

	public SearchScreen(UIScreen parent) {
		super("", parent);
	}
	
	public void show() {
		clearCommands();
		addCommand(optsCmd);
		addCommand(backCmd);
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
		if(c == backCmd) {
			ui.showMain();
			ui.disposeSearchForm();
			return;
		}
		if(c == optsCmd) {
			ui.showOptions();
			return;
		}
	}

}
