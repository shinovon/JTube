package models;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

import MIDlet666;
import Util;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import tube42.lib.imagelib.ImageUtils;

public class VideoModel implements ItemCommandListener {

	private static Command openCmd = new Command("Open", Command.ITEM, 1);
	
	private ImageItem imageItem;
	
	private String title;
	private String videoId;
	private String author;
	private String authorId;
	private String description;
	private int viewCount;
	private long published;
	private String publishedText;
	private int lengthSeconds;

	private JSONArray videoThumbnails;

	public VideoModel(JSONObject j) {
		this(j, false);
	}

	public VideoModel(JSONObject j, boolean extended) {
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
		}
	}

	public ImageItem makeImageItem() {
		if(imageItem != null) return imageItem;
		imageItem = new ImageItem(title, null, Item.LAYOUT_CENTER, null);
		imageItem.addCommand(openCmd);
		imageItem.setDefaultCommand(openCmd);
		imageItem.setItemCommandListener(this);
		return imageItem;
	}

	public void loadImage() {
		if(videoThumbnails != null) {
			try {
				int w = getPreferredWidth();
				String url = getThumbUrl(w);
				byte[] b = MIDlet666.hproxy(url);
				Image img = Image.createImage(b, 0, b.length);
				int h = (int) ((float) w * ((float) img.getHeight() / (float) img.getWidth()));
				img = ImageUtils.resize(img, w, h, true, true);
				imageItem.setImage(img);
			} catch (IOException e) {
				e.printStackTrace();
			}
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

}
