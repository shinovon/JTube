package ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import App;
import Errors;
import Locale;
import Settings;
import ui.AppUI;
import ui.Commands;
import ui.DirectFontUtil;
import ui.UIScreen;
import ui.ModelScreen;
import models.VideoModel;
import ui.items.LabelItem;
import ui.items.LineSplitItem;
import models.AbstractModel;

public class VideoScreen extends ModelScreen implements CommandListener, Commands, Runnable {

	private VideoModel video;
	
	private UIScreen containerScreen;
	
	private boolean shown;

	private Object loadingLock = new Object();
	private boolean loaded;

	public VideoScreen(VideoModel v) {
		super(v.getTitle());
		this.video = v;
		add(new LabelItem(Locale.s(TITLE_Loading)));
	}

	private void init() {
		loaded = true;
		synchronized(loadingLock) {
			loadingLock.notify();
		}
		scroll = 0;
		if(video == null) return;
		VideoModel video = this.video;
		add(video.makePreviewItem());
		LabelItem title = new LabelItem(video.getTitle(), mediumfont);
		title.setMarginWidth(6);
		title.setMaxLines(2);
		add(title);
		add(video.makeChannelItem());
		Font f = smallfont;
		try {
			if(ui.getWidth() >= 360) {
				f = DirectFontUtil.getFont(0, 0, 21, 8);
			} else {
				f = DirectFontUtil.getFont(0, 0, 14, 8);
			}
		} catch (Throwable e) {
		}
		LabelItem i = new LabelItem(Locale.views(video.getViewCount()).concat(" • ").concat(Locale.date(video.getPublishedText())), f, AppUI.getColor(COLOR_GRAYTEXT));
		i.setMarginLeft(8);
		//i.setCentered(true);
		i.setMaxLines(2);
		add(i);
		add(new LineSplitItem());
		LabelItem d = new LabelItem(video.getDescription(), smallfont);
		d.setLineSpaces(4);
		d.setMarginTop(8);
		d.setMarginWidth(8);
		add(d);
		relayout();
	}
	
	protected void show() {
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
		new Thread(this).run();
	}
	
	public boolean supportCommands() {
		return true;
	}

	public void load() {
		try {
			if(!video.isExtended()) {
				video.extend();
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
		if(containerScreen != null && video.isFromPlaylist()) {
			if(c == openPlaylistCmd) {
				ui.setScreen(containerScreen);
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
				ui.open(nv, containerScreen);
				dispose();
				return;
			}
		}
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
			ui.disposeVideoPage();
			return;
		}
	}

	public AbstractModel getModel() {
		return video;
	}

	public void setContainerScreen(UIScreen s) {
		containerScreen = s;
	}

	public void dispose() {
		clear();
		video.disposeExtendedVars();
		video = null;
		containerScreen = null;
	}

	public void run() {
		try {
			synchronized(loadingLock) {
				loadingLock.wait(5000);
			}
			if(!loaded) {
				App.inst.stopDoingAsyncTasks();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
				App.inst.addAsyncLoad(this);
				App.inst.notifyAsyncTasks();
			}
		} catch (Exception e) {
		}
	}

}
