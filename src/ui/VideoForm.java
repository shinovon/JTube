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
import Errors;
import Locale;
import Constants;
import models.AbstractModel;
import models.VideoModel;

public class VideoForm extends ModelForm implements CommandListener, ItemCommandListener, Constants {

	private static App app = App.inst;
	
	private VideoModel video;

	private StringItem loadingItem;

	private Form formContainer;

	public VideoForm(VideoModel v) {
		super(v.getTitle());
		this.video = v;
		setCommandListener(this);
		addCommand(backCmd);
		addCommand(downloadCmd);
		addCommand(settingsCmd);
		addCommand(watchCmd);
		loadingItem = new StringItem(null, Locale.s(TITLE_Loading));
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
			removeCommand(watchCmd);
			ImageItem img = video.makeImageItemForPage();
			img.addCommand(watchCmd);
			img.setDefaultCommand(watchCmd);
			img.setItemCommandListener(this);
			append(img);
		}
		Item t = new StringItem(null, video.getTitle());
		t.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_2);
		append(t);
		if(App.videoPreviews) {
			append(video.makeAuthorItem());
		}
		Item author = new StringItem(null, video.getAuthor());
		author.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(author);
		Item vi = new StringItem(Locale.s(TXT_Views), "" + video.getViewCount());
		vi.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(vi);
		Item ld = new StringItem(Locale.s(TXT_LikesDislikes), "" + video.getLikeCount() + " / " + video.getDislikeCount());
		ld.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(ld);
		Item date = new StringItem(Locale.s(TXT_Published), video.getPublishedText());
		date.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(date);
		append(new StringItem(Locale.s(TXT_Description), video.getDescription()));
	}

	public void load() {
		try {
			if(!video.isExtended()) {
				video.extend();
				init();
			}
			if(App.videoPreviews) video.load();
		} catch (Exception e) {
			App.error(this, Errors.VideoForm_load, e.toString());
		}
	}

	public void commandAction(Command c, Displayable d) {
		if(c == watchCmd) {
			App.watch(video.getVideoId());
			return;
		}
		if(c == downloadCmd) {
			App.download(video.getVideoId());
			return;
		}
		if(c == backCmd) {
			if(formContainer != null) {
				App.display(formContainer);
			} else {
				App.back(this);
			}
			app.disposeVideoForm();
			return;
		}
		App.inst.commandAction(c, d);
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

	public AbstractModel getModel() {
		return getVideo();
	}

	public void setFormContainer(Form form) {
		formContainer = form;
	}

}
