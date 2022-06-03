package ui.screens;

import Locale;
import Constants;
import ui.Commands;
import ui.AbstractListScreen;

class ChannelVideosScreen extends AbstractListScreen implements Commands, Constants {
	
	ChannelScreen s;
	
	ChannelVideosScreen(ChannelScreen s) {
		super(s.getChannel().getAuthor() + " - " + Locale.s(BTN_LatestVideos), s);
		this.s = s;
	}
	
	protected void show() {
		clearCommands();
		addCommand(backCmd);
	}
}