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
package jtube.models;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import jtube.App;
import jtube.Constants;
import jtube.InvidiousException;
import jtube.LocalStorage;
import jtube.Settings;
import jtube.ui.IModelScreen;
import jtube.ui.UIItem;
import jtube.ui.items.ChannelItem;
import jtube.ui.screens.ChannelScreen;
import tube42.lib.imagelib.ImageUtils;

public class ChannelModel extends AbstractModel implements ILoader, Constants {
	
	public String author;
	public String authorId;
	public int subCount;
	public long totalViews;

	private ChannelItem item;
	public Image img;
	public Image bannerImg;

	public boolean extended;
	public boolean fromSearch;

	private String imageUrl;
	public boolean hasSmallImage;
	private String bannerUrl;
	public boolean loaded;
	private boolean page;
	
	public ChannelModel(String id) {
		this.authorId = id;
	}

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
	
	public ChannelModel(String id, String name, Image img, int subs) {
		this.author = name;
		this.authorId = id;
		this.img = img;
		this.subCount = subs;
	}

	private void parse(JSONObject o, boolean extended) {
		this.extended = extended;
		author = o.getString("author");
		authorId = o.getString("authorId");
		if(Settings.videoPreviews) {
			JSONArray authorThumbnails = o.getNullableArray("authorThumbnails");
			if(authorThumbnails != null) {
				imageUrl = App.getThumbUrl(authorThumbnails, hasSmallImage ? 36 : 48);
			}
			try {
				JSONArray authorBanners = o.getNullableArray("authorBanners");
				if(authorBanners != null) {
					bannerUrl = App.getSmallestThumbUrl(authorBanners);
				}
			} catch (Exception e) {
			}
		}
		subCount = o.getInt("subCount", -1);
		totalViews = o.getLong("totalViews", 0);
	}
	
	public ChannelModel extend() throws InvidiousException, IOException {
		if(!extended) {
			parse((JSONObject) App.invApi("channels/" + authorId + "?",
					CHANNEL_EXTENDED_FIELDS +
					(Settings.videoPreviews ? ",authorThumbnails" + (Settings.channelBanner ? ",authorBanners" : "") : "")), true);
		}
		return this;
	}

	public void load() {
		if(item == null) return;
		loaded = true;
		if(img != null && !extended) {
			item.setImage(img);
			return;
		}
		int size = hasSmallImage ? 36 : 48;
		if((img = LocalStorage.getAvatar(authorId)) == null) {
			if(imageUrl == null) return;
			try {
				byte[] b = App.getImageBytes(imageUrl);
				if(page) LocalStorage.saveAvatar(authorId, b);
				item.setImage(img = ChannelItem.roundImage(ImageUtils.resize(Image.createImage(b, 0, b.length), size, size)));
			} catch (Throwable e) {
			}
		} else {
			item.setImage(img = ChannelItem.roundImage(ImageUtils.resize(img, size, size)));
		}
		imageUrl = null;
		if(bannerUrl == null) return;
		try {
			byte[] b = App.getImageBytes(bannerUrl);
			bannerImg = Image.createImage(b, 0, b.length);
			b = null;
			int w = Math.max(App.startWidth, App.startHeight);
			bannerImg = ImageUtils.resize(bannerImg, w, (int)(w*bannerImg.getHeight()/(float)bannerImg.getWidth()));
			bannerUrl = null;
		} catch (Throwable e) {
		}
	}

	public void dispose() {
		bannerUrl = imageUrl = null;
		bannerImg = img = null;
		if(item != null) item.img = null;
	}
	
	public void disposeExtendedVars() {
		bannerImg = null;
		extended = false;
		hasSmallImage = false;
	}

	public UIItem makeListItem() {
		hasSmallImage = false;
		return item = new ChannelItem(this);
	}

	public UIItem makeVideoItem() {
		hasSmallImage = true;
		return item = new ChannelItem(this);
	}

	public UIItem makePageItem() {
		hasSmallImage = false;
		page = true;
		return item = new ChannelItem(this);
	}

	public IModelScreen makeScreen() {
		return new ChannelScreen(this);
	}

}
