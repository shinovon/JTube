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
package ui.screens;

import Loader;
import Locale;
import Settings;
import ui.UIScreen;
import ui.items.VideoItem;

public class SearchScreen extends NavigationScreen {
	
	private String query = "";

	public SearchScreen(String q, UIScreen parent) {
		super(Locale.s(TITLE_SearchQuery), parent);
		setSearchText(query = q);
		menuOptions = !topBar ? new String[] {
				Locale.s(CMD_Search),
				Locale.s(CMD_Settings)
		} : new String[] {
				Locale.s(CMD_Settings)
		};
	}
	
	protected void show() {
		super.show();
		if(wasHidden) {
			wasHidden = false;
			if(Settings.videoPreviews) {
				// resume loading previews
				Loader.stop();
				for(int i = 0; i < items.size(); i++) {
					Object o = items.elementAt(i);
					if(o instanceof VideoItem) {
						Loader.add(((VideoItem)o).getVideo());
					}
				}
				Loader.start();
			}
		}
	}
	
	protected void hide() {
		super.hide();
		if(Settings.rmsPreviews) {
			for(int i = 0; i < items.size(); i++) {
				Object o = items.elementAt(i);
				if(o instanceof VideoItem) {
					((VideoItem)o).onHide();
				}
			}
		}
	}
/*
	public void commandAction(Command c, Displayable d) {
		if(c == backCmd) {
			AppUI.loadingState = false;
			ui.showMain();
			ui.disposeSearchPage();
			return;
		}
		if(c == searchOkCmd && d instanceof TextBox) {
			App.inst.schedule(new RunnableTask(((TextBox) d).getString(), 2));
			return;
		}
		if(c == optsCmd) {
			ui.showOptions();
			return;
		}
		super.commandAction(c, d);
	}
*/
	public String getQuery() {
		return query;
	}
	
	protected void menuAction(int action) {
		if(!topBar) action--;
		switch(action) {
		case -1:
			openSearchTextBox();
			break;
		case 0:
			ui.showSettings();
			break;
		}
	}

}
