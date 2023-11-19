/*
Copyright (c) 2023 Arman Jussupgaliyev

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

import jtube.LocalStorage;
import jtube.models.ChannelModel;
import jtube.ui.Locale;

public class SubscriptionsListScreen extends NavigationScreen implements Runnable {

	private boolean shown;

	public SubscriptionsListScreen() {
		super(Locale.s(TITLE_Subscriptions));
		menuOptions = null;
		hasSearch = false;
	}
	
	public void show() {
		super.show();
		if(!shown) {
			new Thread(this).start();
			shown = true;
		}
	}
	
	public void run() {
		clear();
		String[] subscriptions = LocalStorage.getSubsciptions();
		for(int i = 0; i < subscriptions.length; i += 2) {
			add(new ChannelModel(subscriptions[i], subscriptions[i + 1], null).makeListItem());
		}
	}

}
