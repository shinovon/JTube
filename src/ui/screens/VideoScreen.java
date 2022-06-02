package ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import App;
import Errors;
import Locale;
import Settings;
import ui.AppUI;
import ui.Commands;
import ui.UIScreen;
import ui.ModelScreen;
import models.VideoModel;
import ui.items.LabelItem;
import models.AbstractModel;

public class VideoScreen extends ModelScreen implements CommandListener, Commands {

	private VideoModel video;
	
	private UIScreen containerScreen;
	
	private boolean shown;

	public VideoScreen(VideoModel v) {
		super(v.getTitle());
		this.video = v;
		add(new LabelItem(Locale.s(TITLE_Loading)));
	}

	private void init() {
		scroll = 0;
		if(video == null) return;
		VideoModel video = this.video;
		add(video.makePreviewItem());
		LabelItem title = new LabelItem(video.getTitle(), mediumfont);
		title.setMaxLines(2);
		add(title);
		add(video.makeChannelItem());
		add(new LabelItem(Locale.s(TXT_Views) + ": " + Locale.views(video.getViewCount()), smallfont));
		add(new LabelItem(Locale.s(TXT_Published) + ": " + video.getPublishedText(), smallfont));
		add(new LabelItem(Locale.s(TXT_Description), mediumfont, AppUI.getColor(COLOR_GRAYTEXT)));
		add(new LabelItem(video.getDescription(), smallfont));
		relayout();
	}
	
	public void show() {
		clearCommands();
		addCommand(backCmd);
		addCommand(settingsCmd);
		addCommand(showLinkCmd);
		addCommand(downloadCmd);
		addCommand(watchCmd);
		if(!shown) {
			shown = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			App.inst.addAsyncLoad(this);
			App.inst.notifyAsyncTasks();
		}
	}
	
	public boolean supportCommands() {
		return true;
	}

	public void load() {
		try {
			if(!video.isExtended()) {
				video.extend();
				scroll = 0;
				clear();
				init();
			}
			if(Settings.videoPreviews) video.load();
		} catch (NullPointerException e) {
			// ignore
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.VideoForm_load, e);
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
/*
		if(containerScreen != null && video.isFromPlaylist()) {
			if(c == openPlaylistCmd) {
				ui.setCurrent(containerScreen);
				return;
			}
			if(c == nextCmd || c == prevCmd) {
				boolean next = c == nextCmd;
				PlaylistScreen p = (PlaylistScreen) containerScreen;
				int cur = video.getIndex();
				int l = p.getLength();
				int i = 0;
				if(next) {
					if(cur + 1 < l) {
						i = cur + 1;
					} else {
						i = 0;
						return;
					}
				} else {
					if(cur - 1 > 0) {
						i = cur - 1;
					} else {
						i = l - 1;
						return;
					}
				}
				VideoModel nv = p.getVideo(i);
				ui.open(nv, formContainer);
				dispose();
				return;
			}
		}
*/
		if(c == showLinkCmd) {
			TextBox t = new TextBox("", "", 64, TextField.URL);
			t.setString("https://www.youtube.com/watch?v=" + video.getVideoId() + (video.isFromPlaylist() ? "&list=" + video.getPlaylistId() : ""));
			t.addCommand(backCmd);
			t.setCommandListener(this);
			ui.display(t);
			return;
		}
		if(c == backCmd) {
			if(containerScreen != null) {
				ui.setScreen(containerScreen);
			} else {
				ui.back(this);
			}
			ui.disposeVideoForm();
			return;
		}
	}

	public AbstractModel getModel() {
		return video;
	}

	public void setFormContainer(UIScreen s) {
		containerScreen = s;
	}

	public void dispose() {
		clear();
		video.disposeExtendedVars();
		video = null;
		containerScreen = null;
	}

}
