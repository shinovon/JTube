package models;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;

import App;
import InvidiousException;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import tube42.lib.imagelib.ImageUtils;
import ui.ModelForm;
import ui.VideoForm;
import ui.custom.VideoItem;

public class VideoModel extends AbstractModel implements ItemCommandListener, ILoader {

	private String title;
	private String videoId;
	private String author;
	private String authorId;
	private String description;
	private int viewCount;
	//private long published;
	private String publishedText;
	private int lengthSeconds;
	private int likeCount;
	private int dislikeCount;

	private String thumbnailUrl;
	private int imageWidth;
	private ImageItem imageItem;
	private Image img;
	private JSONArray authorThumbnails;

	private boolean extended;
	private boolean fromSearch;

	private ImageItem authorItem;
	
	private VideoItem customItem;

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
			//published = j.getLong("published", 0);
			publishedText = j.getNullableString("publishedText");
			likeCount = j.getInt("likeCount", -1);
			dislikeCount = j.getInt("dislikeCount", -1);
			if(App.videoPreviews) authorThumbnails = j.getNullableArray("authorThumbnails");
		}
		// это сделает парс дольше но сэкономит память
		if(videoThumbnails != null) {
			if(App.customItems) {
				imageWidth = App.width;
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
			parse((JSONObject) App.invApi("v1/videos/" + videoId + "?fields=" + VIDEO_EXTENDED_FIELDS), true);
		}
		return this;
	}
	private Item makeItem() {
		//if(imageItem != null) return imageItem;
		if(App.customItems) {
			return customItem = new VideoItem(this);
		}
		if(!App.videoPreviews) {
			return new StringItem(author, title);
		}
		return imageItem = new ImageItem(title, img, Item.LAYOUT_CENTER, null, ImageItem.BUTTON);
	}

	public Item makeItemForList() {
		Item i = makeItem();
		i.addCommand(vOpenCmd);
		i.setDefaultCommand(vOpenCmd);
		i.setItemCommandListener(this);
		return i;
	}

	public ImageItem makeImageItemForPage() {
		return imageItem = new ImageItem(null, img, Item.LAYOUT_CENTER, null);
	}

	public void loadImage() {
		if(img != null) return;
		if(thumbnailUrl == null) return;
		if(imageItem == null && customItem == null) return;
		try {
			byte[] b = App.hproxy(thumbnailUrl);
			img = Image.createImage(b, 0, b.length);
			float iw = img.getWidth();
			float ih = img.getHeight();
			float nw = (float) imageWidth;
			int nh = (int) (nw * (ih / iw));
			img = ImageUtils.resize(img, imageWidth, nh);
			if(!App.customItems) {
				imageItem.setImage(img);
			} else if(customItem != null) {
				float f = iw / ih;
				if(f == 4F / 3F) {
					// cropping to 16:9
					float ch = nw * (9F / 16F);
					int chh = (int) ((nh - ch) / 2F);
					img = ImageUtils.crop(img, 0, chh, img.getWidth(), (int) (ch + chh));
				}
				customItem.setImage(img);
			}
			thumbnailUrl = null;
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
		authorItem = new ImageItem(null, null, Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_BEFORE, null, Item.BUTTON);
		authorItem.addCommand(vOpenChannelCmd);
		authorItem.setItemCommandListener(this);
		return authorItem;
	}

	public void commandAction(Command c, Item item) {
		if(c == vOpenCmd || c == null) {
			App.open(this);
		}
		if(c == vOpenChannelCmd) {
			Image img = null;
			if(authorItem != null) img = authorItem.getImage();
			App.open(new ChannelModel(authorId, author, img));
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

	//public long getPublished() {
	//	return published;
	//}

	public String getPublishedText() {
		return publishedText;
	}

	public int getLengthSeconds() {
		return lengthSeconds;
	}

	public void load() {
		loadImage();
		if(extended) {
			loadAuthorImg();
		}
	}

	public void setFromSearch() {
		fromSearch = true;
	}
	
	public boolean isFromSearch() {
		return fromSearch;
	}

	public void dispose() {
		thumbnailUrl = null;
		img = null;
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
	
	public Image getCachedImage() {
		return img;
	}

	public ModelForm makeForm() {
		return new VideoForm(this);
	}

}
