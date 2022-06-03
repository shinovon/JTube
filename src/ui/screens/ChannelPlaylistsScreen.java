package ui.screens;

import Locale;
import Constants;
import ui.Commands;
import ui.AbstractListScreen;

class ChannelPlaylistsScreen extends AbstractListScreen implements Commands, Constants {
	
	ChannelScreen s;
	
	ChannelPlaylistsScreen(ChannelScreen s) {
		super(s.getChannel().getAuthor() + " - " + Locale.s(BTN_Playlists), s);
		this.s = s;
	}
	
	protected void show() {
		clearCommands();
		addCommand(backCmd);
	}
}