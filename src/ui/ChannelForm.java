package ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;

import App;
import Errors;
import Constants;
import models.AbstractModel;
import models.ChannelModel;

// TODO
public class ChannelForm extends ModelForm implements CommandListener, Constants, ItemCommandListener {

	private static final Command videosCmd = new Command("Videos", Command.ITEM, 2);

	private ChannelModel channel;
	
	private StringItem loadingItem;
	private StringItem videosBtn;
	
	private int state = 0; // 0 - info 1 - latest videos
	private int page = 1;

	public ChannelForm(ChannelModel c) {
		super(c.getAuthor());
		loadingItem = new StringItem(null, "Loading");
		loadingItem.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_VCENTER | Item.LAYOUT_2);
		setCommandListener(this);
		addCommand(backCmd);
		this.channel = c;
	}
	
	private void init() {
		try {
			if(get(0) == loadingItem) {
				delete(0);
			}
		} catch (Exception e) {
		}
		Item img = channel.makeItemForPage();
		append(img);
		videosBtn = new StringItem(null, "Videos", Item.BUTTON);
		videosBtn.addCommand(videosCmd);
		videosBtn.setDefaultCommand(videosCmd);
		videosBtn.setItemCommandListener(this);
	}

	public void load() {
		try {
			if(!channel.isExtended()) {
				channel.extend();
				init();
			}
			if(App.videoPreviews) channel.load();
		} catch (Exception e) {
			App.error(this, Errors.ChannelForm_load, e.toString());
		}
	}

	public void commandAction(Command c, Displayable d) {
		if(c == backCmd) {
			App.back(this);
			return;
		}
		App.inst.commandAction(c, d);
	}

	public void dispose() {
		channel.disposeExtendedVars();
		channel = null;
	}

	public ChannelModel getChannel() {
		return channel;
	}

	public AbstractModel getModel() {
		return getChannel();
	}

	public void commandAction(Command c, Item i) {
		
	}
}
