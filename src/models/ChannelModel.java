/*
Copyright (c) 2022 Arman Jussupgaliyev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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
import Locale;
import ui.AppUI;
import Constants;
import ui.ModelForm;
import ui.ChannelForm;
import InvidiousException;
import ui.custom.ChannelItem;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;

public class ChannelModel extends AbstractModel implements ILoader, ItemCommandListener, Constants {
	
	private static final Command cOpenCmd = new Command(Locale.s(CMD_Open), Command.ITEM, 3);
	
	private String author;
	private String authorId;
	private int subCount;
	private long totalViews;
	private String description;

	private ImageItem item;
	private Image img;
	private ChannelItem customItem;

	private boolean extended;
	private boolean fromSearch;

	private String imageUrl;

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
		if(App.videoPreviews || App.customItems) {
			JSONArray authorThumbnails = o.getNullableArray("authorThumbnails");
			if(authorThumbnails != null) {
				String u = App.getThumbUrl(authorThumbnails, AUTHORITEM_IMAGE_HEIGHT);
				//
				if(u.startsWith("//")) {
					u = "https:" + Util.replace(u, "s88", "s" + AUTHORITEM_IMAGE_HEIGHT);
				}
				imageUrl = u;
			}
		}
		subCount = o.getInt("subCount", -1);
		if(extended) {
			totalViews = o.getLong("totalViews", 0);
			description = o.getNullableString("description");
		}
	}
	
	public ChannelModel extend() throws InvidiousException, IOException {
		if(!extended) {
			parse((JSONObject) App.invApi("v1/channels/" + authorId + "?", CHANNEL_EXTENDED_FIELDS + (App.videoPreviews ? "authorThumbnails" : "")), true);
		}
		return this;
	}

	private Item makeItem() {
		if(App.customItems) {
			return customItem = new ChannelItem(this);
		}
		if(!App.videoPreviews) {
			return new StringItem(null, author);
		}
		return item = new ImageItem(author, img, Item.LAYOUT_CENTER, null);
	}

	public Item makeItemForList() {
		Item i = makeItem();
		i.addCommand(cOpenCmd);
		i.setDefaultCommand(cOpenCmd);
		i.setItemCommandListener(this);
		return i;
	}

	public Item makeItemForPage() {
		return makeItem();
	}

	public void load() {
		if(img != null) return;
		if(item == null && customItem == null) return;
		if(imageUrl == null) return;
		try {
			byte[] b = App.hproxy(imageUrl);
			img = Image.createImage(b, 0, b.length);
			if(customItem != null) {
				customItem.setImage(img);
			} else {
				item.setImage(img);
			}
			imageUrl = null;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		
	}

	public void commandAction(Command c, Item arg1) {
		if(c == cOpenCmd || c == null) {
			AppUI.open(this);
		}
	}

	public void dispose() {
		imageUrl = null;
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
