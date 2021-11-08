package models;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

import MIDlet666;
import InvidiousException;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import tube42.lib.imagelib.ImageUtils;

public class VideoModel implements ItemCommandListener, ILoader {

	private static final String EXTENDED_FIELDS = "title,videoId,videoThumbnails,author,authorId,description,videoCount,published,publishedText,lengthSeconds,likeCount,dislikeCount,authorThumbnails,viewCount";

	public static Command openCmd = new Command("Open video", Command.ITEM, 1);
	
	private String title;
	private String videoId;
	private String author;
	private String authorId;
	private String description;
	private int viewCount;
	private long published;
	private String publishedText;
	private int lengthSeconds;
	private int likeCount;
	private int dislikeCount;

	private JSONArray videoThumbnails;
	private ImageItem imageItem;
	private Image img;
	private JSONArray authorThumbnails;

	private boolean extended;

	private boolean fromSearch;

	private ImageItem authorItem;

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
		title = j.getNullableString("title");
		videoId = j.getString("videoId");
		author = j.getNullableString("author");
		viewCount = j.getInt("viewCount", 0);
		lengthSeconds = j.getInt("lengthSeconds", 0);
		videoThumbnails = j.getNullableArray("videoThumbnails");
		if(extended) {
			description = j.getNullableString("description");
			authorId = j.getNullableString("authorId");
			published = j.getLong("published", 0);
			publishedText = j.getNullableString("publishedText");
			likeCount = j.getInt("likeCount", -1);
			dislikeCount = j.getInt("dislikeCount", -1);
			authorThumbnails = j.getNullableArray("authorThumbnails");
		}
	}
	
	public VideoModel extend() throws InvidiousException, IOException {
		if(!extended) {
			parse((JSONObject) MIDlet666.invApi("v1/videos/" + videoId + "?fields=" + EXTENDED_FIELDS), true);
		}
		return this;
	}

	public ImageItem makeImageItemForList() {
		//if(imageItem != null) return imageItem;
		imageItem = new ImageItem(title, img, Item.LAYOUT_CENTER, null);
		imageItem.addCommand(openCmd);
		imageItem.setDefaultCommand(openCmd);
		imageItem.setItemCommandListener(this);
		return imageItem;
	}

	public ImageItem makeImageItemForPage() {
		imageItem = new ImageItem(null, img, Item.LAYOUT_CENTER, null);
		return imageItem;
	}

	public void loadImage() {
		if(img != null) return;
		if(videoThumbnails == null) return;
		try {
			int w = getPreferredWidth();
			if (w <= 0) w = 220;
			String url = getThumbUrl(w);
			byte[] b = MIDlet666.hproxy(url);
			img = Image.createImage(b, 0, b.length);
			int h = (int) ((float) w * ((float) img.getHeight() / (float) img.getWidth()));
			img = ImageUtils.resize(img, w, h);
			imageItem.setImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadAuthorImg() {
		if(authorItem.getImage() != null) return;
		if(authorThumbnails == null) return;
			try {
			byte[] b = MIDlet666.hproxy(getAuthorThumbUrl());
			authorItem.setImage(Image.createImage(b, 0, b.length));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int getPreferredWidth() {
		return (int) (MIDlet666.width * 2F / 3F);
	}
	
	private String getThumbUrl(int tw) {
		int s = 0;
		int ld = 16384;
		for(int i = 0; i < videoThumbnails.size(); i++) {
			JSONObject j = videoThumbnails.getObject(i);
			int d = Math.abs(tw - j.getInt("width"));
			if (d < ld) {
				ld = d;
				s = i;
			}
		}
		return videoThumbnails.getObject(s).getString("url");
	}
	
	private String getAuthorThumbUrl() {
		if(!extended) return null;
		int s = 0;
		int ld = 16384;
		for(int i = 0; i < authorThumbnails.size(); i++) {
			JSONObject j = authorThumbnails.getObject(i);
			int d = Math.abs(32 - j.getInt("width"));
			if (d < ld) {
				ld = d;
				s = i;
			}
		}
		return authorThumbnails.getObject(s).getString("url");
	}

	public void commandAction(Command c, Item item) {
		if(c == openCmd) {
			MIDlet666.open(this);
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

	public long getPublished() {
		return published;
	}

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

	public void dispose() {
		videoThumbnails = null;
		img = null;
		if(imageItem != null) imageItem.setImage(null);
	}

	public void setFromSearch() {
		fromSearch = true;
	}
	
	public boolean isFromSearch() {
		return fromSearch;
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

	public Item makeAuthorItem() {
		authorItem = new ImageItem(getAuthor(), null, Item.LAYOUT_LEFT, null);
		return authorItem;;
	}

}
