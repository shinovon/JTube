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
import Records;
import Settings;
import Constants;
import InvidiousException;
import ui.AppUI;
import ui.ModelScreen;
import ui.UIItem;
import ui.UIScreen;
import ui.items.ChannelItem;
import ui.items.VideoItem;
import ui.items.VideoPreviewItem;
import ui.screens.PlaylistScreen;
import ui.screens.VideoScreen;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import tube42.lib.imagelib.ImageUtils;

public class VideoModel extends AbstractModel implements ILoader, Constants, Runnable {

	private String title;
	private String videoId;
	private String author;
	private String authorId;
	private String description;
	private int viewCount;
	private String publishedText;
	private int lengthSeconds;
	private int likeCount;
	private int dislikeCount;
	private String playlistId;
	private int subCount;

	private String thumbnailUrl;
	private int imageWidth;
	private String authorThumbnailUrl;

	private boolean extended;
	private boolean fromSearch;
	private boolean fromPlaylist;
	
	private VideoItem item;
	private VideoPreviewItem prevItem;
	private ChannelItem channelItem;
	
	private UIScreen containerScreen;
	
	private int index = -1;
	private boolean imgLoaded;
	
	private byte[] tempImgBytes;

	// create model without parsing
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
		JSONArray videoThumbnails = null;
		if(Settings.videoPreviews) {
			videoThumbnails = j.getNullableArray("videoThumbnails");
		}
		author = j.getNullableString("author");
		authorId = j.getNullableString("authorId");
		lengthSeconds = j.getInt("lengthSeconds", 0);
		viewCount = j.getInt("viewCount", 0);
		publishedText = j.getNullableString("publishedText");
		if(extended) {
			subCount = j.getInt("subCount", 0);
			description = j.getNullableString("description");
			likeCount = j.getInt("likeCount", -1);
			dislikeCount = j.getInt("dislikeCount", -1);
			if(Settings.videoPreviews) {
				authorThumbnailUrl = App.getThumbUrl(j.getNullableArray("authorThumbnails"), AUTHORITEM_IMAGE_HEIGHT);
			}
		}
		if(videoThumbnails != null) {
			imageWidth = AppUI.inst.getItemWidth();
			thumbnailUrl = App.getThumbUrl(videoThumbnails, imageWidth);
			videoThumbnails = null;	
		}
		j = null;
	}
	
	public VideoModel extend() throws InvidiousException, IOException {
		if(!extended) {
			parse((JSONObject) App.invApi("v1/videos/" + videoId + "?", VIDEO_EXTENDED_FIELDS + (Settings.videoPreviews ? ",videoThumbnails,authorThumbnails" : "")), true);
		}
		return this;
	}
	
	public Image customResize(Image img) {
		float iw = img.getWidth();
		float ih = img.getHeight();
		Util.gc();
		float f = iw / ih;
		if(f == 4F / 3F) {
			// cropping to 16:9
			float ch = iw * (9F / 16F);
			int chh = (int) ((ih - ch) / 2F);
			return ImageUtils.crop(img, 0, chh, img.getWidth(), (int) (ch + chh));
		}
		float nw = (float) imageWidth;
		int nh = (int) (nw * (ih / iw));
		img = ImageUtils.resize(img, imageWidth, nh);
		return img;
	}

	public void loadImage() {
		if(imgLoaded) return;
		imgLoaded = true;
		if(thumbnailUrl == null) return;
		if(item == null && prevItem == null && !extended) return;
		try {
			byte[] b = App.hproxy(thumbnailUrl);
			if(Settings.rmsPreviews && item != null) {
				if(Settings.isLowEndDevice()) {
					Records.save(videoId, b);
					if(index <= 1 && index != -1) {
						Image img = Image.createImage(b, 0, b.length);
						b = null;
						Util.gc();
						item.setImage(customResize(img));
					}
					b = null;
				} else {
					tempImgBytes = b;
					App.inst.schedule(this);
					if(index <= 2 && index != -1) {
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
				} else if(prevItem != null) {
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
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Util.gc();
			App.inst.stopDoingAsyncTasks();
			App.warn(this, "Not enough memory to load video previews!");
		}
	}

	private void loadAuthorImg() {
		if(authorThumbnailUrl == null) return;
		try {
			_loadAuthorImg();
		} catch (IllegalArgumentException e) {
			if(e.getMessage().indexOf("format") != -1) {
				try {
					_loadAuthorImg();
				} catch (Exception e1) {
				} catch (Error e1) {
				}
			}
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	private void _loadAuthorImg() throws Exception {
		byte[] b = App.hproxy(authorThumbnailUrl);
		authorThumbnailUrl = null;
		channelItem.setImage(ImageUtils.resize(Image.createImage(b, 0, b.length), AUTHORITEM_IMAGE_HEIGHT, AUTHORITEM_IMAGE_HEIGHT));
	}
	public String getTitle() {
		return title;
	}

	public String getVideoId() {
		return videoId;
	}

	public String getAuthor() {
		return author;
	}

	public String getAuthorId() {
		return authorId;
	}

	public String getDescription() {
		return description;
	}

	public int getViewCount() {
		return viewCount;
	}

	public String getPublishedText() {
		return publishedText;
	}

	public int getLengthSeconds() {
		return lengthSeconds;
	}
	
	public void setIndex(int i) {
		this.index = i;
	}

	public void load() {
		try {
			loadImage();
			if(extended) {
				loadAuthorImg();
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setFromSearch() {
		fromSearch = true;
	}
	
	public boolean isFromSearch() {
		return fromSearch;
	}
	
	public boolean isFromPlaylist() {
		return fromPlaylist;
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

	public int getLikeCount() {
		return likeCount;
	}

	public int getDislikeCount() {
		return dislikeCount;
	}

	public boolean isExtended() {
		return extended;
	}

	public int getIndex() {
		return index;
	}

	public String getPlaylistId() {
		return playlistId;
	}

	// Cache image to RMS
	public void run() {
		if(tempImgBytes != null) {
			Records.save(videoId, tempImgBytes);
			tempImgBytes = null;
		}
	}

	public UIItem makeListItem() {
		return item = new VideoItem(this);
	}

	public UIItem makePreviewItem() {
		Image img = null;
		if(item != null) {
			img = item.getImage();
		}
		if(img == null && Settings.rmsPreviews) {
			try {
				img = Records.saveOrGetImage(videoId, thumbnailUrl);
			} catch (IOException e) {
			}
		}
		return prevItem = new VideoPreviewItem(this, img);
	}

	public ModelScreen makeScreen() {
		return new VideoScreen(this);
	}
	
	public ChannelItem makeChannelItem() {
		return channelItem = (ChannelItem) new ChannelModel(getAuthorId(), getAuthor(), null, subCount).makeListItem();
	}

	public void setContainerScreen(UIScreen s) {
		this.containerScreen = s;
		this.fromPlaylist = s instanceof PlaylistScreen;
		if(fromPlaylist) {
			this.playlistId = ((PlaylistModel)((PlaylistScreen)s).getModel()).getPlaylistId();
		}
	}

	public UIScreen getContainerScreen() {
		return containerScreen;
	}

	public void setImageWidth(int i) {
		imageWidth = i;
	}

}
