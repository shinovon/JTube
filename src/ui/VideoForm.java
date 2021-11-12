package ui;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;

import App;
import Constants;
import models.ILoader;
import models.VideoModel;

public class VideoForm extends Form implements CommandListener, ItemCommandListener, ILoader, Constants {

	private static App app = App.midlet;
	
	private VideoModel video;

	private StringItem loadingItem;

	public VideoForm(VideoModel v) {
		super(v.getTitle());
		this.video = v;
		setCommandListener(this);
		addCommand(backCmd);
		addCommand(downloadCmd);
		loadingItem = new StringItem("", "Loading");
		loadingItem.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_VCENTER | Item.LAYOUT_2);
		//addCommand(browserCmd);
		if(v.isExtended()) {
			init();
		} else {
			append(loadingItem);
		}
	}

	private void init() {
		try {
			if(get(0) == loadingItem) {
				delete(0);
			}
		} catch (Exception e) {
		}
		if(App.videoPreviews) {
			ImageItem img = video.makeImageItemForPage();
			img.addCommand(watchCmd);
			img.setDefaultCommand(watchCmd);
			img.setItemCommandListener(this);
			append(img);
		} else {
			addCommand(watchCmd);
		}
		Item t = new StringItem(null, video.getTitle());
		t.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_2);
		append(t);
		append(video.makeAuthorItem());
		Item author = new StringItem(null, video.getAuthor());
		author.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(author);
		Item vi = new StringItem("Views", "" + video.getViewCount());
		vi.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(vi);
		Item ld = new StringItem("Likes / Dislikes", "" + video.getLikeCount() + " / " + video.getDislikeCount());
		ld.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(ld);
		Item date = new StringItem("Published", video.getPublishedText());
		date.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(date);
		append(new StringItem("Description", video.getDescription()));
	}

	public void queueLoad() {
		App.pageOpen(this);
	}

	public void load() {
		try {
			if(!video.isExtended()) {
				video.extend();
				init();
			}
			if(App.videoPreviews) video.load();
		} catch (Exception e) {
			App.msg(e.toString());
		}
	}

	public void commandAction(Command c, Displayable arg1) {
		if(c == watchCmd) {
			App.watch(video.getVideoId());
		}
		if(c == downloadCmd) {
			App.download(video.getVideoId());
		}
		if(c == backCmd) {
			App.back(this);
			app.disposeVideoForm();
		}
	}

	public void commandAction(Command c, Item arg1) {
		if(c == watchCmd) {
			App.watch(video.getVideoId());
		}
	}

	public void dispose() {
		video.disposeExtendedVars();
		video = null;
	}

	public VideoModel getVideo() {
		return video;
	}

}
