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
package ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import App;
import Loader;
import Errors;
import Locale;
import Settings;
import ui.AppUI;
import ui.Commands;
import ui.UIScreen;
import ui.IModelScreen;
import models.VideoModel;
import ui.items.DescriptionItem;
import ui.items.LabelItem;
import models.AbstractModel;
import ui.items.LineSplitItem;
import ui.items.VideoExtrasItem;
import ui.nokia_extensions.DirectFontUtil;

public class VideoScreen extends NavigationScreen implements IModelScreen, Runnable, Commands {

	private VideoModel video;
	
	private boolean shown;

	public VideoScreen(VideoModel v) {
		super(v.getTitle(), null);
		this.video = v;
		menuOptions = new String[] {
				Locale.s(CMD_Download),
				Locale.s(CMD_ShowLink),
				Locale.s(CMD_Settings),
				Locale.s(CMD_OpenPlaylist),
				Locale.s(CMD_Next),
				Locale.s(CMD_Prev)
		};
	}

	private void init() {
		scroll = 0;
		if(video == null) return;
		VideoModel video = this.video;
		if(Settings.videoPreviews)
			add(video.makePreviewItem());
		Font f = App.width >= 360 ? DirectFontUtil.getFont(0, 0, 24, Font.SIZE_MEDIUM) : mediumfont;
		LabelItem title = new LabelItem(video.getTitle(), f);
		title.setMarginWidth(9);
		title.setMarginTop(12);
		title.setMaxLines(2);
		title.setSkipScrolling(true);
		add(title);
		f = App.width >= 360 ? DirectFontUtil.getFont(0, 0, 19, Font.SIZE_SMALL) : smallfont;
		LabelItem views = new LabelItem(Locale.views(video.getViewCount()).concat(" • ").concat(Locale.date(video.getPublishedText())), f, AppUI.getColor(COLOR_GRAYTEXT));
		views.setMarginTop(3);
		views.setMarginWidth(9);
		views.setMaxLines(2);
		views.setMarginBottom(9);
		views.setSkipScrolling(true);
		add(views);
		add(new LineSplitItem());
		add(video.makeChannelItem());
		add(new VideoExtrasItem(this, video.getLikeCount()));
		DescriptionItem d = new DescriptionItem(video.getDescription(), f);
		add(d);
		relayout();
		repaint();
	}
	
	protected void show() {
		super.show();
		if(!shown) {
			shown = true;
			Loader.stop();
			new Thread(this).start();
		}
		addCommand(backCmd);
		addCommand(watchCmd);
		addCommand(showLinkCmd);
		addCommand(downloadCmd);
		if(parent != null && video.isFromPlaylist()) {
			addCommand(openPlaylistCmd);
			addCommand(prevCmd);
			addCommand(nextCmd);
		}
	}

	public void load() {
		AppUI.loadingState = true;
		try {
			if(!video.isExtended()) {
				video.extend();
				init();
				AppUI.loadingState = false;
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
		if(c == downloadCmd) {
			App.download(video.getVideoId(), video.getTitle());
			return;
		}
		if(parent != null && video != null && video.isFromPlaylist()) {
			if(c == openPlaylistCmd) {
				ui.setScreen(parent);
				return;
			}
			if(c == nextCmd || c == prevCmd) {
				boolean next = c == nextCmd;
				PlaylistScreen p = (PlaylistScreen) parent;
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
				ui.open(nv, parent);
				dispose();
				return;
			}
		}
		if(c == showLinkCmd) {
			showLink();
			return;
		}
		if(c == backCmd) {
			back();
			return;
		}
		
		super.commandAction(c, d);
	}

	protected void menuAction(int action) {
		switch(action) {
		case 0:
			download();
			break;
		case 1:
			showLink();
			break;
		case 2:
			ui.showSettings();
			break;
		case 3:
			if(parent != null && video.isFromPlaylist()) {
				ui.setScreen(parent);
			}
			break;
		case 4:
			if(parent != null && video.isFromPlaylist()) {
				PlaylistScreen p = (PlaylistScreen) parent;
				int cur = video.getIndex();
				int l = p.getLength();
				int i = 0;
				if(cur + 1 < l) {
					i = cur + 1;
				} else {
					i = 0;
					return;
				}
				VideoModel nv = p.getVideo(i);
				ui.open(nv, parent);
				dispose();
			}
			break;
		case 5:
			if(parent != null && video.isFromPlaylist()) {
				PlaylistScreen p = (PlaylistScreen) parent;
				int cur = video.getIndex();
				int l = p.getLength();
				int i = 0;
				if(cur - 1 > 0) {
					i = cur - 1;
				} else {
					i = l - 1;
					return;
				}
				VideoModel nv = p.getVideo(i);
				ui.open(nv, parent);
				dispose();
			}
			break;
		}
	}

	public AbstractModel getModel() {
		return video;
	}

	public void setContainerScreen(UIScreen s) {
		parent = s;
	}

	public void dispose() {
		clear();
		if(video != null) {
			video.disposeExtendedVars();
		}
		video = null;
		parent = null;
	}

	public void run() {
		load();
	}
	
	public void showLink() {
		TextBox t = new TextBox("", "", 64, TextField.URL);
		t.setString("https://www.youtube.com/watch?v=" + video.getVideoId() + (video.isFromPlaylist() ? "&list=" + video.getPlaylistId() : ""));
		t.addCommand(backCmd);
		t.setCommandListener(this);
		ui.display(t);
	}
	
	public void download() {
		App.download(video.getVideoId(), video.getTitle());
	}

}
