package jtube.ui.screens;

import jtube.LocalStorage;
import jtube.models.ChannelModel;
import jtube.ui.Locale;

public class SubscriptionsListScreen extends NavigationScreen implements Runnable {

	protected SubscriptionsListScreen() {
		super(Locale.s(TITLE_Subscriptions));
		new Thread(this).start();
		menuOptions = null;
		hasSearch = false;
	}
	
	public void run() {
		clear();
		String[] subscriptions = LocalStorage.getSubsciptions();
		for(int i = 0; i < subscriptions.length; i += 2) {
			add(new ChannelModel(subscriptions[i], subscriptions[i + 1], null).makeListItem());
		}
	}

}
