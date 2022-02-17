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
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import App;
import Errors;
import Locale;
import Constants;
import models.VideoModel;
import models.AbstractModel;
import models.PlaylistModel;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;

public class PlaylistForm extends ModelForm implements CommandListener, Commands, Constants {

	private PlaylistModel playlist;

	private Form formContainer;

	private StringItem loadingItem;

	private JSONArray vidsjson;

	private VideoModel[] videos;

	public PlaylistForm(PlaylistModel p) {
		super(p.getTitle());
		formContainer = p.getFormContainer();
		loadingItem = new StringItem(null, Locale.s(TITLE_Loading));
		loadingItem.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_VCENTER | Item.LAYOUT_2);
		this.playlist = p;
		setCommandListener(this);
		addCommand(backCmd);
	}
	
	private void init() {
		try {
			if(get(0) == loadingItem) {
				delete(0);
			}
		} catch (Exception e) {
		}
		try {
			int l = vidsjson.size();
			System.out.println(l);
			App.gc();
			videos = new VideoModel[l];
			for(int i = 0; i < l; i++) {
				Item item = item(vidsjson.getObject(i), i);
				if(item == null) continue;
				append(item);
				App.gc();
			}
			vidsjson = null;
			App.gc();
			try {
				if(App.videoPreviews) {
					for(int i = 0; i < l && i < 20; i++) {
						if(videos[i] == null) continue;
						videos[i].loadImage();
					}
				}
			} catch (RuntimeException e) {
				throw e;
			} catch (Throwable e) {
				e.printStackTrace();
				App.error(this, Errors.PlaylistForm_init_previews, e);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			App.error(this, Errors.PlaylistForm_init, e);
		}
	}

	private Item item(JSONObject j, int i) {
		VideoModel v = new VideoModel(j);
		v.setIndex(i);
		v.setFormContainer(this);
		videos[i] = v;
		return v.makeItemForList();
	}

	public void commandAction(Command c, Displayable d) {
		if(c == backCmd) {
			if(formContainer != null) {
				AppUI.display(formContainer);
			} else {
				AppUI.back(this);
			}
			dispose();
		}
	}

	private void dispose() {
		deleteAll();
		playlist.disposeExtendedVars();
		playlist = null;
		App.gc();
	}

	public void load() {
		try {
			vidsjson = ((JSONObject) App.invApi("v1/playlists/" + playlist.getPlaylistId() + "?", PLAYLIST_EXTENDED_FIELDS)).getArray("videos");
			init();
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			App.error(this, Errors.PlaylistForm_load, e);
		}
	}

	public AbstractModel getModel() {
		return playlist;
	}

	public void setFormContainer(Form form) {
		this.formContainer = form;
	}

	public int getLength() {
		return videos.length;
	}
	
	public VideoModel getVideo(int i) {
		return videos[i];
	}

}
