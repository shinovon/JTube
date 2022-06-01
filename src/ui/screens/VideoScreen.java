package ui.screens;

import App;
import Errors;
import ui.AppUI;
import Settings;
import models.ILoader;
import models.VideoModel;
import ui.AbstractListScreen;

public class VideoScreen extends AbstractListScreen implements ILoader {

	private VideoModel video;

	protected VideoScreen(VideoModel v) {
		super("", AppUI.inst.main);
		this.video = v;
	}

	private void init() {
		if(video == null) return;
		add(video.makePreviewItem());
	}

	public void load() {
		try {
			if(!video.isExtended()) {
				video.extend();
				init();
			}
			if(Settings.videoPreviews) video.load();
		} catch (NullPointerException e) {
			// ignore
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			App.error(this, Errors.VideoForm_load, e);
		}
	}

}
