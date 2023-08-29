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

import jtube.Settings;
import jtube.ui.Locale;
import jtube.ui.items.VideoItem;

public class SearchScreen extends NavigationScreen {
	
	private String query = "";

	public SearchScreen(String q) {
		super(Locale.s(TITLE_SearchQuery));
		setSearchText(query = q);
		menuOptions = !topBar ? new String[] {
				Locale.s(CMD_Search),
				Locale.s(CMD_Settings),
				Locale.s(CMD_FuncMenu)
		} : new String[] {
				Locale.s(CMD_Settings),
				Locale.s(CMD_FuncMenu)
		};
	}
	
	public String getQuery() {
		return query;
	}
	
	protected void menuAction(int action) {
		if(!topBar) action--;
		switch(action) {
		case -1:
			openSearchTextBox();
			break;
		default:
			super.menuAction(action);
		}
	}

}
