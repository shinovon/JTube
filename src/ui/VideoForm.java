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
import Locale;
import Errors;
import models.VideoModel;
import models.AbstractModel;

public class VideoForm extends ModelForm implements CommandListener, ItemCommandListener, Commands {
	
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
		if(v.isFromPlaylist()) {
			addCommand(openPlaylistCmd);
			addCommand(prevCmd);
			addCommand(nextCmd);
		}
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
		if(video == null) return;
		if(App.videoPreviews) {
			removeCommand(watchCmd);
			if(video == null) return;
			ImageItem img = video.makeImageItemForPage();
			img.addCommand(watchCmd);
			img.setDefaultCommand(watchCmd);
			img.setItemCommandListener(this);
			append(img);
		}
		if(video == null) return;
		Item t = new StringItem(null, video.getTitle());
		t.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_2);
		append(t);
		if(App.videoPreviews) {
			append(video.makeAuthorItem());
		}
		if(video == null) return;
		Item author = new StringItem(null, video.getAuthor());
		author.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(author);
		if(video == null) return;
		Item vi = new StringItem(Locale.s(TXT_Views), Locale.views(video.getViewCount()));
		vi.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(vi);
		//Item ld = new StringItem(Locale.s(TXT_LikesDislikes), "" + video.getLikeCount() + " / " + video.getDislikeCount());
		//ld.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		//append(ld);
		if(video == null) return;
		Item date = new StringItem(Locale.s(TXT_Published), video.getPublishedText());
		date.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(date);
		if(video == null) return;
		append(new StringItem(Locale.s(TXT_Description), video.getDescription()));
	}

	public void load() {
		try {
			if(!video.isExtended()) {
				video.extend();
				init();
			}
			if(App.videoPreviews) video.load();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.VideoForm_load, e);
		}
	}

	public void commandAction(Command c, Displayable d) {
		if(formContainer != null && video.isFromPlaylist()) {
			if(c == openPlaylistCmd) {
				AppUI.display(formContainer);
				return;
			}
			if(c == nextCmd || c == prevCmd) {
				boolean next = c == nextCmd;
				PlaylistForm p = (PlaylistForm) formContainer;
				int cur = video.getIndex();
				int l = p.getLength();
				int i = 0;
				if(next) {
					if(cur + 1 < l) {
						i = cur + 1;
					} else {
						i = 0;
					}
				} else {
					if(cur - 1 > 0) {
						i = cur - 1;
					} else {
						i = l - 1;
					}
				}
				VideoModel nv = p.getVideo(i);
				AppUI.open(nv, formContainer);
				dispose();
				return;
			}
		}
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
				AppUI.display(formContainer);
			} else {
				AppUI.back(this);
			}
			AppUI.inst.disposeVideoForm();
			return;
		}
		AppUI.inst.commandAction(c, d);
	}

	public void commandAction(Command c, Item i) {
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
