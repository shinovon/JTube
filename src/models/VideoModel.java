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
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;

import App;
import Records;
import Settings;
import Constants;
import ui.AppUI;
import ui.ModelForm;
import ui.VideoForm;
import ui.PlaylistForm;
import InvidiousException;
import ui.custom.VideoItem;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import tube42.lib.imagelib.ImageUtils;

public class VideoModel extends AbstractModel implements ItemCommandListener, ILoader, Constants, Runnable {

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

	private String thumbnailUrl;
	private int imageWidth;
	private ImageItem imageItem;
	private JSONArray authorThumbnails;

	private boolean extended;
	private boolean fromSearch;
	private boolean fromPlaylist;

	private ImageItem authorItem;
	
	private VideoItem customItem;
	
	private Form formContainer;
	
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

	public VideoModel(JSONObject j, Form form) {
		this(j, false);
		this.formContainer = form;
		this.fromPlaylist = form instanceof PlaylistForm;
	}

	private void parse(JSONObject j, boolean extended) {
		this.extended = extended;
		videoId = j.getString("videoId");
		title = j.getNullableString("title");
		JSONArray videoThumbnails = null;
		if(App.videoPreviews || App.customItems) {
			videoThumbnails = j.getNullableArray("videoThumbnails");
		}
		author = j.getNullableString("author");
		authorId = j.getNullableString("authorId");
		lengthSeconds = j.getInt("lengthSeconds", 0);
		if(extended) {
			viewCount = j.getInt("viewCount", 0);
			
			description = j.getNullableString("description");
			publishedText = j.getNullableString("publishedText");
			likeCount = j.getInt("likeCount", -1);
			dislikeCount = j.getInt("dislikeCount", -1);
			if(App.videoPreviews) authorThumbnails = j.getNullableArray("authorThumbnails");
		}
		if(videoThumbnails != null) {
			if(App.customItems) {
				imageWidth = VideoItem.getImageWidth();
			} else {
				imageWidth = getImgItemWidth();
				if (imageWidth <= 0) imageWidth = 220;
			}
			thumbnailUrl = App.getThumbUrl(videoThumbnails, imageWidth);
			videoThumbnails = null;	
		}
		j = null;
	}
	
	public VideoModel extend() throws InvidiousException, IOException {
		if(!extended) {
			parse((JSONObject) App.invApi("v1/videos/" + videoId + "?", VIDEO_EXTENDED_FIELDS + (App.videoPreviews ? ",videoThumbnails,authorThumbnails" : "")), true);
		}
		return this;
	}
	private Item makeItem() {
		if(App.customItems) {
			customItem = new VideoItem(this);
			imageWidth = VideoItem.getImageWidth();
			return customItem;
		}
		if(!App.videoPreviews) {
			return new StringItem(author, title);
		}
		return imageItem = new ImageItem(title, null, Item.LAYOUT_CENTER, null, ImageItem.BUTTON);
	}

	public Item makeItemForList() {
		Item i = makeItem();
		i.addCommand(vOpenCmd);
		i.setDefaultCommand(vOpenCmd);
		i.setItemCommandListener(this);
		return i;
	}

	public ImageItem makeImageItemForPage() {
		Image img = null;
		if(customItem != null) {
			img = customItem.getImage();
		}
		if(img == null && App.rmsPreviews) {
			try {
				img = Records.saveOrGetImage(videoId, thumbnailUrl);
			} catch (IOException e) {
			}
		}
		if(img == null && imageItem != null) {
			img = imageItem.getImage();
		}
		if(img == null) {
			try {
				byte[] b = App.hproxy(thumbnailUrl);
				img = Image.createImage(b, 0, b.length);
				b = null;
				App.gc();
				float iw = img.getWidth();
				float ih = img.getHeight();
				float nw = (float) imageWidth;
				int nh = (int) (nw * (ih / iw));
				img = ImageUtils.resize(img, imageWidth, nh);
			} catch (IOException e) {
			}
		}
		return imageItem = new ImageItem(null, img, Item.LAYOUT_CENTER, null);
	}
	
	public Image customResize(Image img) {
		float iw = img.getWidth();
		float ih = img.getHeight();
		float nw = (float) imageWidth;
		int nh = (int) (nw * (ih / iw));
		img = ImageUtils.resize(img, imageWidth, nh);
		App.gc();
		float f = iw / ih;
		if(f == 4F / 3F) {
			// cropping to 16:9
			float ch = nw * (9F / 16F);
			int chh = (int) ((nh - ch) / 2F);
			return ImageUtils.crop(img, 0, chh, img.getWidth(), (int) (ch + chh));
		}
		return img;
	}

	public void loadImage() {
		if(imgLoaded) return;
		imgLoaded = true;
		if(thumbnailUrl == null) return;
		if(imageItem == null && customItem == null && !extended) return;
		try {
			byte[] b = App.hproxy(thumbnailUrl);
			if(App.rmsPreviews && App.customItems) {
				if(Settings.isLowEndDevice()) {
					Records.save(videoId, b);
					if(index <= 1 && index != -1) {
						Image img = Image.createImage(b, 0, b.length);
						b = null;
						App.gc();
						customItem.setImage(customResize(img));
					}
					b = null;
				} else {
					tempImgBytes = b;
					
					App.inst.schedule(this);
					if(index <= 2 && index != -1) {
						Image img = Image.createImage(b, 0, b.length);
						App.gc();
						customItem.setImage(customResize(img));
					}
				}
			} else {
				Image img = Image.createImage(b, 0, b.length);
				b = null;
				App.gc();
				if(imageItem != null) {
					if(App.width >= 480) {
						img = customResize(img);
					} else {
						float iw = img.getWidth();
						float ih = img.getHeight();
						float nw = (float) imageWidth;
						int nh = (int) (nw * (ih / iw));
						img = ImageUtils.resize(img, imageWidth, nh);
					}
					imageItem.setImage(img);
				} else if(customItem != null) {
					customItem.setImage(customResize(img));
				}
			}
			thumbnailUrl = null;
			App.gc();
		} catch (NullPointerException e) {
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			App.gc();
			App.inst.stopDoingAsyncTasks();
			App.warn(this, "Not enough memory to load video previews!");
		}
	}

	private void loadAuthorImg() {
		if(authorThumbnails == null) return;
		if(authorItem == null || authorItem.getImage() != null) return;
		try {
			byte[] b = App.hproxy(getAuthorThumbUrl());
			authorItem.setImage(Image.createImage(b, 0, b.length));
			authorThumbnails = null;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}
	
	private int getImgItemWidth() {
		return (int) (App.width * 2F / 3F);
	}
	
	public String getAuthorThumbUrl() {
		return App.getThumbUrl(authorThumbnails, VIDEOFORM_AUTHOR_IMAGE_HEIGHT);
	}

	public Item makeAuthorItem() {
		/*if(!App.videoPreviews) {
			Item i = new StringItem(null, getAuthor());
			i.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_2);
			return i;
		}*/
		authorItem = new ImageItem(null, null, Item.LAYOUT_LEFT, null, Item.BUTTON);
		authorItem.addCommand(vOpenChannelCmd);
		authorItem.setDefaultCommand(vOpenChannelCmd);
		authorItem.setItemCommandListener(this);
		return authorItem;
	}

	public void commandAction(Command c, Item item) {
		if(c == vOpenCmd || c == null) {
			AppUI.open(this, formContainer);
		}
		if(c == vOpenChannelCmd) {
			if(formContainer != null && !fromPlaylist) {
				AppUI.display(formContainer);
				return;
			}
			Image img = null;
			//if(authorItem != null) img = authorItem.getImage();
			AppUI.open(new ChannelModel(authorId, author, img));
		}
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
		if(imageItem != null) imageItem.setImage(null);
	}

	public void disposeExtendedVars() {
		extended = false;
		authorId = null;
		description = null;
		publishedText = null;
		authorThumbnails = null;
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

	public ModelForm makeForm() {
		return new VideoForm(this);
	}

	public void setFormContainer(Form form) {
		this.formContainer = form;
		this.fromPlaylist = form instanceof PlaylistForm;
	}

	public int getIndex() {
		return index;
	}

	// Cache image to RMS
	public void run() {
		if(tempImgBytes != null) {
			Records.save(videoId, tempImgBytes);
			tempImgBytes = null;
		}
	}

}
