package ui.screens;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import App;
import Errors;
import InvidiousException;
import Locale;
import Settings;
import Util;
import ui.Commands;
import ui.AbstractListScreen;

public class MainScreen extends AbstractListScreen implements Commands, CommandListener {
	
	private List options;

	public MainScreen() {
		super("", null);
		clearCommands();
		addCommand(optsCmd);
		addCommand(exitCmd);
		options = new List("JTube Menu", List.IMPLICIT);
		options.append(Locale.s(CMD_Search), null);
		options.append(Locale.s(CMD_Refresh), null);
		options.append(Locale.s(Settings.startScreen == 0 ? CMD_SwitchToPopular : CMD_SwitchToTrends), null);
		options.append(Locale.s(CMD_OpenByID), null);
		options.append(Locale.s(CMD_Settings), null);
		options.append(Locale.s(CMD_About), null);
		options.append(Locale.s(CMD_Exit), null);
		options.addCommand(List.SELECT_COMMAND);
		options.setSelectCommand(List.SELECT_COMMAND);
		options.addCommand(backCmd);
		options.setCommandListener(this);
	}

	public boolean supportCommands() {
		return true;
	}

	public void commandAction(Command c, Displayable d) {
		if(c == List.SELECT_COMMAND) {
			switch(options.getSelectedIndex()) {
			case 0:
				// Search
				
				break;
			case 1:
				// Refresh
				try {
					App.inst.stopDoingAsyncTasks();
					clear();
					ui.display(null);
					if(Settings.startScreen == 0) {
						ui.loadTrends();
					} else {
						ui.loadPopular();
					}
				} catch (InvidiousException e) {
					App.error(this, Errors.AppUI_loadForm, e);
				} catch (OutOfMemoryError e) {
					Util.gc();
					App.error(this, Errors.AppUI_loadForm, "Out of memory!");
				} catch (Throwable e) {
					e.printStackTrace();
					App.error(this, Errors.AppUI_loadForm, e);
				}
				break;
			case 2:
				// Switch
				try {
					App.inst.stopDoingAsyncTasks();
					clear();
					ui.display(null);
					if(Settings.startScreen == 0) {
						Settings.startScreen = 1;
						ui.loadPopular();
					} else {
						Settings.startScreen = 0;
						ui.loadTrends();
					}
					Settings.saveConfig();
				} catch (Exception e) {
					App.error(this, Errors.App_commandAction_switchCmd, e);
					e.printStackTrace();
				}
				break;
			case 3:
				// Open by ID
				
				break;
			case 4:
				//Settings
				ui.showSettings();
				break;
			case 5:
				// About
				ui.showAbout(this);
				break;
			case 6:
				// Exit
				ui.exit();
				break;
			}
			return;
		}
		if(d instanceof Alert) {
			ui.display(options);
			return;
		}
		if(c == backCmd) {
			ui.display(null);
			return;
		}
		if(c == optsCmd) {
			ui.display(options);
			return;
		}
	}

}
