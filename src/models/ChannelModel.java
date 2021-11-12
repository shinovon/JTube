package models;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

import App;
import Constants;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;

// TODO
public class ChannelModel implements ILoader, Constants, ItemCommandListener {
	
	private static final Command openCmd = new Command("Open channel", Command.OK, 3);
	
	private String author;
	private String authorId;
	private int subCount;
	private int videoCount;
	private JSONArray authorThumbnails;

	private ImageItem item;
	private Image img;

	private boolean fromSearch;

	public ChannelModel(JSONObject o) {
		parse(o);
	}
	
	private void parse(JSONObject o) {
		author = o.getString("author");
		authorId = o.getString("authorId");
		authorThumbnails = o.getNullableArray("authorThumbnails");
	}

	public Item makeItemForList() {
		item = new ImageItem(author, img, Item.LAYOUT_CENTER, null);
		item.addCommand(openCmd);
		item.setDefaultCommand(openCmd);
		item.setItemCommandListener(this);
		return item;
	}

	public String getAuthor() {
		return author;
	}

	public String getAuthorId() {
		return authorId;
	}

	public int getSubCount() {
		return subCount;
	}

	public int getVideoCount() {
		return videoCount;
	}

	public Image getImg() {
		return img;
	}

	public void setFromSearch() {
		fromSearch = true;
	}
	
	public boolean isFromSearch() {
		return fromSearch;
	}

	private String getAuthorThumbUrl() {
		int s = 0;
		int ld = 16384;
		for(int i = 0; i < authorThumbnails.size(); i++) {
			JSONObject j = authorThumbnails.getObject(i);
			int d = Math.abs(AUTHORITEM_IMAGE_HEIGHT - j.getInt("width"));
			if (d < ld) {
				ld = d;
				s = i;
			}
		}
		return authorThumbnails.getObject(s).getString("url");
	}

	public void load() {
		if(img != null) return;
		if(authorThumbnails == null) return;
			try {
			byte[] b = App.hproxy(getAuthorThumbUrl());
			img = Image.createImage(b, 0, b.length);
			item.setImage(img);
			authorThumbnails = null;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		
	}

	public void commandAction(Command c, Item arg1) {
		if(c == openCmd) {
			App.openChannel(this);
		}
	}

}
