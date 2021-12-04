package ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import App;
import Constants;
import Errors;
import Locale;
import models.AbstractModel;
import models.ChannelModel;
import models.PlaylistModel;
import models.VideoModel;

public class PlaylistForm extends ModelForm implements CommandListener, Constants {

	private PlaylistModel playlist;
	
	private ChannelModel channel;

	private Form formContainer;

	private StringItem loadingItem;

	//private int page = 1;

	public PlaylistForm(PlaylistModel p) {
		super(p.getTitle());
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
			/*
			VideoModel[] videos = playlist.getVideos();
			System.gc();
			int l = videos.length;;
			System.out.println(l);
			for(int i = 0; i < l; i++) {
				Item item = item(videos[i]);
				if(item == null) continue;
				append(item);
				if(i >= LATESTVIDEOS_LIMIT) break;
			}
			try {
				if(App.videoPreviews) {
					for(int i = 0; i < l; i++) {
						videos[i].load();
					}
				}
			} catch (RuntimeException e) {
				throw e;
			} catch (Throwable e) {
				e.printStackTrace();
				App.error(this, Errors.PlaylistForm_init_previews, e);
			}
			*/
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			App.error(this, Errors.PlaylistForm_init, e);
		}
	}

	private Item item(VideoModel v) {
		v.setFormContainer(this);
		//if(App.videoPreviews) App.inst.addAsyncLoad(v);
		return v.makeItemForList();
	}

	public void commandAction(Command c, Displayable d) {
		if(c == backCmd) {
			if(formContainer != null) {
				App.display(formContainer);
			} else {
				App.back(this);
			}
			dispose();
		}
	}

	private void dispose() {
		deleteAll();
		playlist.disposeExtendedVars();
		playlist = null;
		channel = null;
		App.gc();
	}

	public void load() {
		try {
			init();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.PlaylistForm_load, e.toString());
		}
	}

	public AbstractModel getModel() {
		return playlist;
	}

	public void setFormContainer(Form form) {
		this.formContainer = form;
		if(form instanceof ChannelForm) {
			channel = ((ChannelForm) form).getChannel();
		}
	}

}
