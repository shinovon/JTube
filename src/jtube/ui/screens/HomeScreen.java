/*
 Copyright (c) 2022 Arman Jussupgaliyev

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

import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import jtube.Loader;
import jtube.RunnableTask;
import jtube.Settings;
import jtube.ui.Locale;
import jtube.ui.items.VideoItem;

public class HomeScreen extends NavigationScreen {

	public HomeScreen() {
		super("");
		menuOptions = !topBar ? new String[] {
				Locale.s(CMD_Search),
				Locale.s(CMD_SubscriptionsFeed),
				Locale.s(CMD_Refresh),
				Locale.s(Settings.startScreen == 0 ? CMD_SwitchToPopular : CMD_SwitchToTrends),
				Locale.s(CMD_OpenByID),
				Locale.s(CMD_Settings),
				Locale.s(CMD_About),
				Locale.s(CMD_Exit)
		} : new String[] {
				Locale.s(CMD_Refresh),
				Locale.s(Settings.startScreen == 0 ? CMD_SwitchToPopular : CMD_SwitchToTrends),
				Locale.s(CMD_OpenByID),
				Locale.s(CMD_Settings),
				Locale.s(CMD_About),
				Locale.s(CMD_Exit)
		};
	}
	
	protected void hide() {
		super.hide();
		if(!Settings.videoPreviews) return;
		for(int i = 0; i < items.size(); i++) {
			Object o = items.elementAt(i);
			if(o instanceof VideoItem) {
				((VideoItem)o).unload();
			}
		}
	}
	
	protected void menuAction(int action) {
		if(!topBar) action-=2;
		switch(action) {
		case -2:
			openSearchTextBox();
			break;
		case -1:
			ui.loadSubs();
			ui.nextScreen(ui.subsScr);
			break;
		case 0:
			new Thread(new RunnableTask(RunnableTask.REFRESH)).start();
			break;
		case 1:
			menuOptions[topBar ? 1 : 2] = Locale.s(Settings.startScreen == 0 ? CMD_SwitchToTrends : CMD_SwitchToPopular);
			new Thread(new RunnableTask(RunnableTask.SWITCH)).start();
			break;
		case 2:
			Loader.stop();
			TextBox t = new TextBox("", "", 256, TextField.ANY);
			t.setCommandListener(this);
			t.setTitle("Video URL or ID");
			t.addCommand(goCmd);
			t.addCommand(cancelCmd);
			ui.display(t);
			break;
		case 3:
			ui.showSettings();
			break;
		case 4:
			ui.showAbout(this);
			break;
		case 5:
			if(this instanceof HomeScreen) {
				ui.exit();
			}
			break;
		}
	}
}
