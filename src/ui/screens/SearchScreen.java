package ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import Locale;
import ui.AppUI;
import ui.Commands;
import ui.UIScreen;
import cc.nnproject.utils.PlatformUtils;

public class SearchScreen extends SearchBarScreen implements Commands, CommandListener {

	private boolean okAdded;
	
	private Command okCmd = new Command("OK", Command.OK, 5);
	
	private String query = "";

	public SearchScreen(String q, UIScreen parent) {
		super(Locale.s(TITLE_SearchQuery) + " - " + q, parent);
		setSearchText(query = q);
	}
	
	public void paint(Graphics g, int w, int h) {
		if(AppUI.loadingState) {
			if(MainScreen.editor != null && MainScreen.editor.isVisible()) {
				MainScreen.editor.setVisible(false);
			}
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

	protected void show() {
		ui.addOptionCommands();
		addCommand(backCmd);
		super.show();
		if(wasHidden) {
			wasHidden = false;
		}
		if((PlatformUtils.isS603rd() && ui.getWidth() > ui.getHeight()) || PlatformUtils.isKemulator || PlatformUtils.isSonyEricsson()) {
			okAdded = true;
		} else if(okAdded || ui.isKeyInputMode()) {
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
			AppUI.loadingState = false;
			ui.showMain();
			ui.disposeSearchPage();
			return;
		}
		if(c == optsCmd) {
			ui.showOptions();
			return;
		}
		super.commandAction(c, d);
	}
	
	public String getQuery() {
		return query;
	}

}
