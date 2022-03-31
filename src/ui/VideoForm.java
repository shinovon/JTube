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
package ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import App;
import Util;
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
		addCommand(settingsCmd);
		addCommand(showLinkCmd);
		addCommand(downloadCmd);
		addCommand(watchCmd);
		if(v.isFromPlaylist()) {
			addCommand(openPlaylistCmd);
			addCommand(prevCmd);
			addCommand(nextCmd);
		}
		loadingItem = new StringItem(null, Locale.s(TITLE_Loading));
		loadingItem.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_VCENTER | Item.LAYOUT_2);
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
		t.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(t);
		if(App.videoPreviews) {
			append(video.makeAuthorItem());
		}
		if(video == null) return;
		Item author = new StringItem(null, video.getAuthor());
		author.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(author);
		Item dur = new StringItem(Locale.s(TXT_VideoDuration), Util.timeStr(video.getLengthSeconds()));
		dur.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(dur);
		if(video == null) return;
		Item vi = new StringItem(Locale.s(TXT_Views), Locale.views(video.getViewCount()));
		vi.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(vi);
		// YouTube doesn't show ratings anymore
		/*
		Item ld = new StringItem(Locale.s(TXT_LikesDislikes), "" + video.getLikeCount() + " / " + video.getDislikeCount());
		ld.setLayout(Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_2);
		append(ld);
		*/
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
		} catch (NullPointerException e) {
			// ignore
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.VideoForm_load, e);
		}
	}

	public void commandAction(Command c, Displayable d) {
		if(d instanceof TextBox) {
			if(c == backCmd) AppUI.display(this);
			return;
		}
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
		if(c == showLinkCmd) {
			TextBox t = new TextBox("", "", 64, TextField.URL);
			t.setString("https://www.youtube.com/watch?v=" + video.getVideoId());
			t.addCommand(backCmd);
			t.setCommandListener(this);
			AppUI.display(t);
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
			return;
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
