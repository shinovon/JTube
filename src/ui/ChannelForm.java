package ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import App;
import Constants;
import models.ChannelModel;

// TODO
public class ChannelForm extends Form implements Constants, CommandListener {

	private ChannelModel channel;

	public ChannelForm(ChannelModel c) {
		super(c.getAuthor());
		append("TODO");
		setCommandListener(this);
		addCommand(backCmd);
		this.channel = c;
	}

	public void commandAction(Command c, Displayable arg1) {
		if(c == backCmd) {
			App.back(this);
		}
	}

	public ChannelModel getChannel() {
		return channel;
	}
}
