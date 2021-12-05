package models;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

import App;
import InvidiousException;
import Locale;
import cc.nnproject.json.JSONObject;
import ui.ModelForm;
import ui.PlaylistForm;
import ui.custom.PlaylistItem;

public class PlaylistModel extends AbstractModel implements ILoader, ItemCommandListener {
	
	private static final Command pOpenCmd = new Command(Locale.s(CMD_Open), Command.ITEM, 3);

	private boolean extended;
	private boolean fromSearch;
	
	private String title;
	private String playlistId;
	private String author;
	private String authorId;
	private int videoCount;
	
	//private VideoModel[] videos;
	
	private PlaylistItem customItem;

	public PlaylistModel(JSONObject o) {
		parse(o, false);
	}

	public PlaylistModel(JSONObject o, boolean extended) {
		parse(o, extended);
	}
	
	private void parse(JSONObject o, boolean extended) {
		this.extended = extended;
		title = o.getString("title");
		playlistId = o.getString("playlistId");
		author = o.getString("author");
		authorId = o.getString("authorId");
		videoCount = o.getInt("videoCount", 0);
		/*
		if(extended) {
			JSONArray vids = o.getNullableArray("videos");
			if(vids != null) {
				int l = vids.size();
				this.videos = new VideoModel[l];
				for(int i = 0; i < l; i++) {
					this.videos[i] = new VideoModel(vids.getObject(i));
				}
			}
		}
		*/
	}
	
	public PlaylistModel extend() throws InvidiousException, IOException {
		/*if(!extended) {
			parse((JSONObject) App.invApi("v1/playlists/" + playlistId + "?fields=" + PLAYLIST_EXTENDED_FIELDS + "&page=" + page), true);
		}*/
		return this;
	}

	public void load() {
	}

	public Item makeItemForList() {
		customItem = new PlaylistItem(this);
		customItem.addCommand(pOpenCmd);
		customItem.setDefaultCommand(pOpenCmd);
		customItem.setItemCommandListener(this);
		return customItem;
	}

	public void setFromSearch() {
		fromSearch = true;
	}

	public boolean isFromSearch() {
		return fromSearch;
	}

	public boolean isExtended() {
		return extended;
	}

	public void dispose() {
		playlistId = null;
		title = null;
		author = null;
		authorId = null;
	}

	public void disposeExtendedVars() {
	//	videos = null;
		extended = false;
	}

	public ModelForm makeForm() {
		return new PlaylistForm(this);
	}

	public void commandAction(Command c, Item i) {
		if(c == pOpenCmd || c == null) {
			App.open(this);
		}
	}
	
	public String getPlaylistId() {
		return playlistId;
	}

	public String getTitle() {
		return title;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getAuthorId() {
		return authorId;
	}

	public int getVideoCount() {
		return videoCount;
	}

}
