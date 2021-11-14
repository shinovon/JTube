package models;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;

import App;
import Util;
import InvidiousException;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import ui.ChannelForm;
import ui.ModelForm;

// TODO
public class ChannelModel extends AbstractModel implements ILoader, ItemCommandListener {
	
	private static final Command cOpenCmd = new Command("Open channel", Command.OK, 3);
	
	private String author;
	private String authorId;
	private int subCount;
	private long totalViews;
	private JSONArray authorThumbnails;
	private String description;

	private ImageItem item;
	private Image img;

	private boolean extended;
	private boolean fromSearch;

	public ChannelModel(JSONObject o) {
		parse(o, false);
	}
	
	public ChannelModel(JSONObject o, boolean extended) {
		parse(o, extended);
	}
	
	public ChannelModel(String id, String name, Image img) {
		this.author = name;
		this.authorId = id;
		this.img = img;
	}

	private void parse(JSONObject o, boolean extended) {
		this.extended = extended;
		author = o.getString("author");
		authorId = o.getString("authorId");
		authorThumbnails = o.getNullableArray("authorThumbnails");
		if(extended) {
			subCount = o.getInt("subCount", 0);
			totalViews = o.getLong("totalViews", 0);
			description = o.getNullableString("description");
		}
	}
	
	public ChannelModel extend() throws InvidiousException, IOException {
		if(!extended) {
			parse((JSONObject) App.invApi("v1/channels/" + authorId + "?fields=" + CHANNEL_EXTENDED_FIELDS), true);
		}
		return this;
	}

	public Item makeItemForList() {
		if(!App.videoPreviews) {
			StringItem i = new StringItem(null, author);
			i.addCommand(cOpenCmd);
			i.setDefaultCommand(cOpenCmd);
			i.setItemCommandListener(this);
			return i;
		}
		item = new ImageItem(author, img, Item.LAYOUT_CENTER, null);
		item.addCommand(cOpenCmd);
		item.setDefaultCommand(cOpenCmd);
		item.setItemCommandListener(this);
		return item;
	}

	private String getAuthorThumbUrl() {
		String u = App.getThumbUrl(authorThumbnails, AUTHORITEM_IMAGE_HEIGHT);
		//
		if(u.startsWith("//")) {
			u = "https:" + Util.replace(u, "s88", "s" + AUTHORITEM_IMAGE_HEIGHT);
		}
		return u;
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
		if(c == cOpenCmd) {
			App.open(this);
		}
	}

	public void dispose() {
		authorThumbnails = null;
		img = null;
		if(item != null) item.setImage(null);
	}
	
	public void disposeExtendedVars() {
		extended = false;
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

	public long getTotalViews() {
		return totalViews;
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

	public boolean isExtended() {
		return extended;
	}
	
	public String getDescription() {
		return description;
	}

	public ModelForm makeForm() {
		return new ChannelForm(this);
	}

}
