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

import javax.microedition.lcdui.Image;

import App;
import Util;
import Loader;
import LocalStorage;
import Settings;
import Constants;
import InvidiousException;
import ui.AppUI;
import ui.IModelScreen;
import ui.UIItem;
import ui.UIScreen;
import ui.items.ChannelItem;
import ui.items.VideoItem;
import ui.items.VideoPreviewItem;
import ui.screens.PlaylistScreen;
import ui.screens.VideoScreen;
import cc.nnproject.json.JSONObject;
import tube42.lib.imagelib.ImageUtils;

public class VideoModel extends AbstractModel implements ILoader, Constants, Runnable {

	public String title;
	public String videoId;
	public String author;
	public String authorId;
	public String description;
	public int viewCount;
	public String publishedText;
	public int lengthSeconds;
	public int likeCount;
	public String playlistId;
	private int subCount;

	private String thumbnailUrl;
	public int imageWidth;
	private String authorThumbnailUrl;

	public boolean extended;
	public boolean fromSearch;
	public boolean fromPlaylist;
	
	private VideoItem item;
	private VideoPreviewItem prevItem;
	private ChannelItem channelItem;
	
	private UIScreen containerScreen;
	
	public int index = -1;
	private boolean imgLoaded;
	
	private byte[] tempImgBytes;
	
	private boolean loadDone;

	public VideoModel(String id) {
		videoId = id;
	}

	public VideoModel(JSONObject j) {
		this(j, false);
	}

	public VideoModel(JSONObject j, boolean extended) {
		parse(j, extended);
	}

	public VideoModel(JSONObject j, UIScreen s) {
		this(j, false);
		this.containerScreen = s;
		this.fromPlaylist = s instanceof PlaylistScreen;
	}

	private void parse(JSONObject j, boolean extended) {
		this.extended = extended;
		videoId = j.getString("videoId");
		title = j.getNullableString("title");
		author = j.getNullableString("author");
		authorId = j.getNullableString("authorId");
		lengthSeconds = j.getInt("lengthSeconds", 0);
		viewCount = j.getInt("viewCount", 0);
		publishedText = j.getNullableString("publishedText");
		if(extended) {
			subCount = j.getInt("subCount", 0);
			description = j.getNullableString("description");
			likeCount = j.getInt("likeCount", -1);
			if(Settings.videoPreviews && j.has("authorThumbnails")) {
				authorThumbnailUrl = App.getThumbUrl(j.getArray("authorThumbnails"), 36);
			}
		}
		if(Settings.videoPreviews) {
			int w = AppUI.inst.getItemWidth();
			if(!extended && Settings.smallPreviews) {
				w /= 3;
			}
			if(imageWidth == 0) imageWidth = w;
			thumbnailUrl = App.getThumbUrl(videoId, w);
		}
		j = null;
	}
	
	public VideoModel extend() throws InvidiousException, IOException {
		if(!extended) {
			parse((JSONObject) App.invApi("videos/" + videoId + "?", VIDEO_EXTENDED_FIELDS + (Settings.videoPreviews ? ",authorThumbnails" : "")), true);
			try {
				JSONObject j = (JSONObject) App.invApi("channels/" + authorId + "?", "subCount");
				if(j.has("subCount"))
					subCount = j.getInt("subCount", 0);
			} catch (Exception e) {
			}
		}
		return this;
	}
	
	public Image customResize(Image img) {
		return resize(img, false, 0);
	}
	
	public Image resize(Image img, boolean prev, int w) {
		float iw = img.getWidth();
		float ih = img.getHeight();
		Util.gc();
		float f = iw / ih;
		int sw = AppUI.inst.getWidth();
		if(f == 4F / 3F && (sw > 480 && sw > AppUI.inst.getHeight())) {
			// cropping to 16:9
			float ch = iw * (9F / 16F);
			int chh = (int) ((ih - ch) / 2F);
			img = ImageUtils.crop(img, 0, chh, img.getWidth(), (int) (ch + chh));
			iw = img.getWidth();
			ih = img.getHeight();
		}
		float nw = (float) (prev ? w : imageWidth);
		int nh = (int) (nw * (ih / iw));
		img = ImageUtils.resize(img, (int)nw, nh);
		return img;
	}
	
	public Image previewResize(int w, Image img) {
		return resize(img, true, w);
	}

	public void loadImage() {
		if(imgLoaded) return;
		imgLoaded = true;
		if(thumbnailUrl == null) return;
		if(item == null && prevItem == null && !extended) return;
		try {
			byte[] b = App.getImageBytes(thumbnailUrl);
			if(prevItem != null && extended) {
				Image img = Image.createImage(b, 0, b.length);
				b = null;
				Util.gc();
				prevItem.setImage(img);
			} else if(Settings.rmsPreviews) {
				if(Settings.isLowEndDevice()) {
					LocalStorage.cacheThumbnail(videoId, b);
					if(item != null && index <= 1 && index != -1) {
						Image img = Image.createImage(b, 0, b.length);
						b = null;
						Util.gc();
						item.setImage(customResize(img));
					}
					b = null;
				} else {
					tempImgBytes = b;
					App.inst.schedule(this);
					if(item != null && index <= 2 && index != -1) {
						Image img = Image.createImage(b, 0, b.length);
						Util.gc();
						item.setImage(customResize(img));
					}
				}
			} else {
				Image img = Image.createImage(b, 0, b.length);
				b = null;
				Util.gc();
				if(item != null) {
					item.setImage(customResize(img));
				}
			}
			thumbnailUrl = null;
			Util.gc();
		} catch (NullPointerException e) {
		} catch (IllegalArgumentException e) {
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
			Util.gc();
			Loader.stop();
			App.warn(this, "Not enough memory to load video previews!");
		}
	}

	private void loadAuthorImg() {
		if(authorThumbnailUrl == null) return;
		try {
			_loadAuthorImg();
		} catch (IllegalArgumentException e) {
			if(e.toString().indexOf("format") != -1) {
				try {
					_loadAuthorImg();
				} catch (Exception e1) {
				} catch (Error e1) {
				}
			}
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}
	}

	private void _loadAuthorImg() throws Exception {
		byte[] b = App.getImageBytes(authorThumbnailUrl);
		authorThumbnailUrl = null;
		channelItem.setImage(ImageUtils.resize(Image.createImage(b, 0, b.length), 36, 36));
	}
	
	public void setIndex(int i) {
		this.index = i;
	}

	public void load() {
		if(loadDone) return;
		try {
			loadImage();
			if(extended) {
				loadAuthorImg();
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
		}
		loadDone = true;
	}

	public void dispose() {
		thumbnailUrl = null;
	}

	public void disposeExtendedVars() {
		extended = false;
		authorId = null;
		description = null;
		publishedText = null;
		authorThumbnailUrl = null;
		channelItem = null;
		prevItem = null;
	}

	// Cache image to RMS
	public void run() {
		if(tempImgBytes != null) {
			LocalStorage.cacheThumbnail(videoId, tempImgBytes);
			tempImgBytes = null;
		}
	}

	public UIItem makeListItem() {
		return item = new VideoItem(this);
	}

	public UIItem makePreviewItem() {
		Image img = null;
		loadDone = false;
		imgLoaded = false;
		/*if(item != null) {
			img = item.getImage();
		}*/
		if(Settings.rmsPreviews && Settings.videoPreviews) {
			try {
				img = LocalStorage.loadAndCacheThumnail(videoId, thumbnailUrl);
			} catch (IOException e) {
			}
		}
		return prevItem = new VideoPreviewItem(this, img);
	}

	public IModelScreen makeScreen() {
		return new VideoScreen(this);
	}
	
	public ChannelItem makeChannelItem() {
		return channelItem = (ChannelItem) new ChannelModel(authorId, author, null, subCount).makePageItem();
	}

	public void setContainerScreen(UIScreen s) {
		this.containerScreen = s;
		this.fromPlaylist = s instanceof PlaylistScreen;
		if(fromPlaylist) {
			this.playlistId = ((PlaylistModel)((PlaylistScreen)s).getModel()).playlistId;
		}
	}

	public UIScreen getContainerScreen() {
		return containerScreen;
	}

}
