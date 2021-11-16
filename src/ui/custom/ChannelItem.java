package ui.custom;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;

import models.ChannelModel;

//TODO
public class ChannelItem extends CustomItem {

	private ChannelModel channel;

	protected ChannelItem(ChannelModel c) {
		super(c.getAuthor());
		this.channel = c;
	}

	protected int getMinContentHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected int getMinContentWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected int getPrefContentHeight(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	protected int getPrefContentWidth(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	protected void paint(Graphics arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	protected void showNotify() {
		
	}
	
	protected void hideNotify() {
		
	}

}
