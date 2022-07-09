package ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
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
import ui.items.ChannelItem;
import ui.items.LabelItem;
import ui.items.LineSplitItem;
import models.AbstractModel;

public class VideoScreen extends ModelScreen implements CommandListener, Commands, Runnable {

	private VideoModel video;
	
	private UIScreen containerScreen;
	
	private boolean shown;

	private Object loadingLock = new Object();
	private boolean loaded;

	private ChannelItem channelItem;

	public VideoScreen(VideoModel v) {
		super(v.getTitle());
		this.video = v;
	}
	
	public void paint(Graphics g, int w, int h) {
		if(AppUI.loadingState) {
			g.setColor(AppUI.getColor(COLOR_MAINBG));
			g.fillRect(0, 0, w, h);
			g.setColor(AppUI.getColor(COLOR_MAINFG));
			String s = Locale.s(TITLE_Loading) + "...";
			g.setFont(smallfont);
			g.drawString(s, (w-smallfont.stringWidth(s))/2, smallfontheight*2, 0);
			return;
		}
		super.paint(g, w, h);
	}

	private void init() {
		loaded = true;
		synchronized(loadingLock) {
			loadingLock.notify();
		}
		scroll = 0;
		if(video == null) return;
		VideoModel video = this.video;
		if(Settings.videoPreviews)
			add(video.makePreviewItem());
		LabelItem title = new LabelItem(video.getTitle(), mediumfont);
		title.setMarginWidth(6);
		title.setMaxLines(2);
		add(title);
		add(channelItem = video.makeChannelItem());
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
		repaint();
	}
	
	protected void show() {
		addCommand(backCmd);
		if(ui.isKeyInputMode())
			addCommand(watchOkCmd);
		else
			addCommand(watchScrCmd);
		addCommand(settingsCmd);
		addCommand(showLinkCmd);
		addCommand(downloadCmd);
		addCommand(vOpenChannelCmd);
		super.show();
		if(!shown) {
			shown = true;
			App.inst.stopAsyncTasks();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			App.inst.addAsyncTask(this);
			App.inst.startAsyncTasks();
		}
		new Thread(this).run();
	}
	
	public boolean supportCommands() {
		return true;
	}

	public void load() {
		AppUI.loadingState = true;
		try {
			if(!video.isExtended()) {
				video.extend();
				init();
				if(Settings.videoPreviews) video.load();
			}
		} catch (NullPointerException e) {
			// ignore
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.VideoForm_load, e);
		}
		AppUI.loadingState = false;
	}

	public void commandAction(Command c, Displayable d) {
		if(d instanceof TextBox && c == backCmd) {
			ui.display(null);
			return;
		}
		if(c == watchOkCmd || c == watchScrCmd) {
			App.watch(video.getVideoId());
			return;
		}
		if(c == vOpenChannelCmd) {
			ui.open(channelItem.getChannel());
			return;
		}
		if(c == downloadCmd) {
			App.download(video.getVideoId(), video.getTitle());
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
			AppUI.loadingState = false;
			if(containerScreen != null) {
				ui.setScreen(containerScreen);
			} else {
				ui.back(this);
			}
			ui.disposeVideoPage();
			return;
		}
		super.commandAction(c, d);
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
		channelItem = null;
		containerScreen = null;
	}

	public void run() {
		try {
			synchronized(loadingLock) {
				loadingLock.wait(1000);
			}
			if(!loaded) {
				App.inst.stopAsyncTasks();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
				App.inst.addAsyncTask(this);
				App.inst.startAsyncTasks();
			}
		} catch (Exception e) {
		}
	}

}
