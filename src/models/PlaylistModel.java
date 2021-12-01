package models;

import javax.microedition.lcdui.Item;

import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;
import ui.ModelForm;

public class PlaylistModel extends AbstractModel implements ILoader {

	private boolean extended;
	private boolean fromSearch;
	
	private String title;
	private String playlistId;
	private String author;
	private String authorId;
	private int videoCount;
	
	private VideoModel[] videos;

	public PlaylistModel(JSONObject o) {
		parse(o, false);
	}

	public PlaylistModel(JSONObject o, boolean extended) {
		parse(o, false);
	}
	private void parse(JSONObject o, boolean extended) {
		this.extended = extended;
		title = o.getString("title");
		playlistId = o.getString("playlistId");
		author = o.getString("author");
		authorId = o.getString("authorId");
		videoCount = o.getInt("videoCount", 0);
		if(extended) {
			JSONArray videos = o.getNullableArray("videos");
			if(videos != null) {
				this.videos = new VideoModel[videos.size()];
			}
		}
	}

	public void load() {
		
	}

	public Item makeItemForList() {
		return null;
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
		title = null;
	}

	public void disposeExtendedVars() {
		extended = false;
	}

	public ModelForm makeForm() {
		return null;
	}

}
