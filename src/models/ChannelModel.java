package models;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import App;
import Constants;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import ui.ChannelItem;

// TODO
public class ChannelModel implements ILoader, Constants {
	
	private String author;
	private String authorId;
	private int subCount;
	private int videoCount;
	private JSONArray authorThumbnails;

	private ChannelItem item;
	private Image img;

	public ChannelModel(JSONObject o) {
		parse(o);
	}
	
	private void parse(JSONObject o) {
		
	}

	public ChannelItem makeItemForList() {
		item = new ChannelItem(this);
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
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		
	}

}
