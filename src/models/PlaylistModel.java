package models;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;

import App;
import Locale;
import ui.AppUI;
import ui.ModelForm;
import ui.PlaylistForm;
import InvidiousException;
import ui.custom.PlaylistItem;
import cc.nnproject.json.JSONObject;

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

	private Form formContainer;

	public PlaylistModel(JSONObject o) {
		parse(o, false);
	}

	public PlaylistModel(JSONObject o, boolean extended) {
		parse(o, extended);
	}
	
	public PlaylistModel(JSONObject j, Form form, ChannelModel channel) {
		this(j, false);
		this.formContainer = form;
		//this.channel = channel;
		authorId = channel.getAuthorId();
	}

	private void parse(JSONObject o, boolean extended) {
		this.extended = extended;
		title = o.getString("title");
		playlistId = o.getString("playlistId");
		author = o.getNullableString("author");
		authorId = o.getNullableString("authorId");
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
			parse((JSONObject) App.invApi("v1/playlists/" + playlistId, PLAYLIST_EXTENDED_FIELDS + "&page=" + page), true);
		}*/
		return this;
	}

	public void load() {
	}

	public Item makeItemForList() {
		if(!App.customItems) {
			StringItem item = new StringItem(title, Locale.videos(getVideoCount()) + (formContainer == null ? "\n" + author : ""));
			item.addCommand(pOpenCmd);
			item.setDefaultCommand(pOpenCmd);
			item.setItemCommandListener(this);
			return item;
		}
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
			AppUI.open(this);
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

	public Form getFormContainer() {
		return formContainer;
	}

}
