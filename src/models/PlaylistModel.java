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
package models;

import ui.UIItem;
import ui.UIScreen;
import ui.IModelScreen;
import ui.items.PlaylistItem;
import ui.screens.PlaylistScreen;
import cc.nnproject.json.JSONObject;

public class PlaylistModel extends AbstractModel implements ILoader {
	
	public String title;
	public String playlistId;
	public String author;
	public String authorId;
	public int videoCount;
	
	private UIScreen containerScreen;

	public PlaylistModel(String id) {
		this.playlistId = id;
	}

	public PlaylistModel(JSONObject o) {
		parse(o, false);
	}

	public PlaylistModel(JSONObject o, boolean extended) {
		parse(o, extended);
	}

	public PlaylistModel(JSONObject j, UIScreen s, ChannelModel channel) {
		this(j, false);
		this.containerScreen = s;
		authorId = channel.authorId;
	}

	private void parse(JSONObject o, boolean extended) {
		title = o.getString("title");
		playlistId = o.getString("playlistId");
		author = o.getNullableString("author");
		authorId = o.getNullableString("authorId");
		videoCount = o.getInt("videoCount", 0);
	}

	public void load() {
	}

	public void dispose() {
		playlistId = null;
		title = null;
		author = null;
		authorId = null;
	}

	public UIItem makeListItem() {
		return new PlaylistItem(this);
	}

	public IModelScreen makeScreen() {
		return new PlaylistScreen(this);
	}

	public UIScreen getContainerScreen() {
		return containerScreen;
	}

}
