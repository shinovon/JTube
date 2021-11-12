package ui;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import App;
import Constants;
import models.ChannelModel;

public class ChannelItem extends CustomItem {

	private ChannelModel channel;

	public ChannelItem(ChannelModel c) {
		super("");
		this.channel = c;
	}

	protected int getMinContentHeight() {
		// TODO Auto-generated method stub
		return Constants.AUTHORITEM_IMAGE_HEIGHT;
	}

	protected int getMinContentWidth() {
		return App.width - 32;
	}

	protected int getPrefContentHeight(int arg0) {
		// TODO Auto-generated method stub
		return getMinContentHeight();
	}

	protected int getPrefContentWidth(int arg0) {
		// TODO Auto-generated method stub
		return getMinContentWidth();
	}

	protected void paint(Graphics arg0, int w, int h) {
		// TODO Auto-generated method stub
		
	}
	
	public ChannelModel getChannel() {
		return channel;
	}

	public void setImage(Image img) {
		
	}

}
