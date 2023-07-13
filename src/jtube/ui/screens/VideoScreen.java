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
package jtube.ui.screens;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import jtube.App;
import jtube.Errors;
import jtube.Loader;
import jtube.LocalStorage;
import jtube.RunnableTask;
import jtube.Settings;
import jtube.models.AbstractModel;
import jtube.models.ChannelModel;
import jtube.models.VideoModel;
import jtube.ui.AppUI;
import jtube.ui.Commands;
import jtube.ui.IModelScreen;
import jtube.ui.Locale;
import jtube.ui.UIScreen;
import jtube.ui.items.ChannelItem;
import jtube.ui.items.Description;
import jtube.ui.items.Label;
import jtube.ui.items.LineSplit;
import jtube.ui.items.VideoButtons;
import jtube.ui.nokia_extensions.DirectFontUtil;

public class VideoScreen extends NavigationScreen implements IModelScreen, Runnable, Commands {

	private VideoModel video;
	private ChannelModel channel;
	
	private boolean shown;
	
	public boolean liked;

	public VideoScreen(VideoModel v) {
		super(v.title);
		this.video = v;
		menuOptions = video.fromPlaylist ? new String[] {
				Locale.s(CMD_Watch),
				Locale.s(CMD_Download),
				Locale.s(CMD_ShowLink),
				Locale.s(CMD_ViewChannel),
				Locale.s(CMD_Settings),
				Locale.s(CMD_OpenPlaylist),
				Locale.s(CMD_Next),
				Locale.s(CMD_Prev)
		} : new String[] {
				Locale.s(CMD_Watch),
				Locale.s(CMD_Download),
				Locale.s(CMD_ShowLink),
				Locale.s(CMD_ViewChannel),
				Locale.s(CMD_Settings)
		};
	}

	private void init() {
		scroll = 0;
		if(video == null) return;
		VideoModel video = this.video;
		if(Settings.videoPreviews)
			add(video.makePreviewItem());
		Font f = App.startWidth >= 360 ? DirectFontUtil.getFont(0, 0, 24, Font.SIZE_MEDIUM) : mediumfont;
		Label title = new Label(video.title, f);
		title.setMarginWidth(9);
		title.setMarginTop(12);
		title.setMaxLines(2);
		add(title);
		f = App.startWidth >= 360 ? DirectFontUtil.getFont(0, 0, 20, Font.SIZE_SMALL) : smallfont;
		Label views = new Label(Locale.views(video.viewCount).concat(" • ").concat(Locale.date(video.publishedText)), f, AppUI.getColor(COLOR_GRAYTEXT));
		views.setMarginTop(3);
		views.setMarginWidth(9);
		views.setMaxLines(2);
		views.setMarginBottom(9);
		add(views);
		add(new LineSplit());
		ChannelItem c = video.makeChannelItem();
		add(c);
		channel = c.getChannel();
		add(new VideoButtons(this, video.likeCount));
		Description d = new Description(video.description, f);
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
	}
	
	protected void menuAction(int action) {
		switch(action) {
		case 0:
			new Thread(new RunnableTask(video.videoId, RunnableTask.WATCH)).start();
			break;
		case 1:
			download();
			break;
		case 2:
			showLink();
			break;
		case 3:
			ui.open(channel);
			break;
		case 4:
			ui.showSettings();
			break;
		case 5:
			if(parent != null && video.fromPlaylist) {
				ui.nextScreen(parent);
			}
			break;
		case 6:
			if(parent != null && video.fromPlaylist) {
				PlaylistScreen p = (PlaylistScreen) parent;
				int cur = video.index;
				int l = p.videos.length;
				int i = 0;
				if(cur + 1 < l) {
					i = cur + 1;
				} else {
					i = 0;
					return;
				}
				VideoModel nv = p.videos[i];
				ui.open(nv, parent);
				dispose();
			}
			break;
		case 7:
			if(parent != null && video.fromPlaylist) {
				PlaylistScreen p = (PlaylistScreen) parent;
				int cur = video.index;
				int l = p.videos.length;
				int i = 0;
				if(cur - 1 > 0) {
					i = cur - 1;
				} else {
					i = l - 1;
					return;
				}
				VideoModel nv = p.videos[i];
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
		if(video == null) return;
		if(video.extended) return;
		busy = true;
		try {
			video.extend();
			init();
			busy = false;
			if(Settings.videoPreviews) video.load();
		} catch (NullPointerException e) {
			// ignore
		} catch (Exception e) {
			App.error(this, Errors.VideoForm_load, e);
		}
		try {
			liked = LocalStorage.isLiked(video.videoId);
		} catch (Exception e) {
		}
		busy = false;
	}
	
	public void showLink() {
		TextBox t = new TextBox("", "", 200, TextField.URL);
		t.setString("https://www.youtube.com/watch?v=" + video.videoId + (video.fromPlaylist ? "&list=" + video.playlistId : ""));
		t.addCommand(backCmd);
		t.setCommandListener(this);
		ui.display(t);
	}
	
	public void download() {
		App.download(video.videoId, video.title);
	}

	public void like() {
		if(busy) return;
		if(liked) {
			LocalStorage.removeLiked(video.videoId);
		} else {
			LocalStorage.addLiked(video.videoId, video.title);
		}
		liked = !liked;
	}

}
