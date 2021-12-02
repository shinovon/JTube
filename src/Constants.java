import javax.microedition.lcdui.Command;

import cc.nnproject.utils.PlatformUtils;

public interface Constants extends LocaleConstants {
	
	// urls
	static final String getlinksphp = "http://nnproject.cc/getlinks.php";
	static final String hproxy = "http://nnproject.cc/hproxy.php?";
	static final String iteroni = "http://iteroni.com/";
	static final String streamphp = "http://nnproject.cc/stream.php";
	
	static final String CONFIG_RECORD_NAME = "ytconfig";
	
	// Main form commands
	static final Command settingsCmd = new Command(Locale.s(CMD_Settings), Command.SCREEN, 2);
	static final Command idCmd = new Command(Locale.s(CMD_OpenByID), Command.SCREEN, 4);
	static final Command searchCmd = new Command(Locale.s(CMD_Search), Command.SCREEN, 5);
	static final Command switchToPopularCmd = new Command(Locale.s(CMD_SwitchToPopular), Command.SCREEN, 1);
	static final Command switchToTrendsCmd = new Command(Locale.s(CMD_SwitchToTrends), Command.SCREEN, 1);
	
	static final Command searchOkCmd = new Command(Locale.s(CMD_Search), Command.OK, 1);
	static final Command exitCmd = new Command(Locale.s(CMD_Exit), Command.EXIT, 2);
	static final Command goCmd = new Command(Locale.s(CMD_Go), Command.OK, 1);
	static final Command cancelCmd = new Command(Locale.s(CMD_Cancel), Command.CANCEL, 2);
	static final Command backCmd = new Command(Locale.s(CMD_Back), Command.BACK, 1);
	
	static final Command applyCmd = new Command(Locale.s(CMD_Apply), Command.BACK, 1);
	
	// Video page commands
	static final Command watchCmd = new Command(Locale.s(CMD_Watch), Command.OK, 10);
	static final Command downloadCmd = new Command(Locale.s(CMD_Download), Command.SCREEN, 9);
	//static final Command browserCmd = new Command("Open with browser", Command.SCREEN, 3);
	public static Command openPlaylistCmd = new Command("open playlist", Command.SCREEN, 5); // TODO: localize
	public static Command nextCmd = new Command("next", Command.SCREEN, 4); // TODO: localize
	public static Command prevCmd = new Command("prev", Command.SCREEN, 3); // TODO: localize
	
	// Downloader alert commands
	static final Command dlOkCmd = new Command(Locale.s(CMD_OK), Command.CANCEL, 1);
	//static final Command dlWatchCmd = new Command("Watch", Command.SCREEN, 2);
	static final Command dlOpenCmd = new Command(Locale.s(CMD_Open), Command.OK, 1);
	static final Command dlCancelCmd = new Command(Locale.s(CMD_Cancel), Command.CANCEL, 1);
	
	public static Command vOpenCmd = new Command(Locale.s(CMD_View), Command.ITEM, 3);
	public static Command vOpenChannelCmd = new Command(Locale.s(CMD_ViewChannel), Command.ITEM, 4);
	
	public static Command cVideosCmd = new Command(Locale.s(CMD_Videos), Command.ITEM, 3);
	
	// Limits
	static final int TRENDS_LIMIT_S60 = 25;
	static final int SEARCH_LIMIT_S60 = 35; 
	static final int TRENDS_LIMIT_LOWEND = 20;
	static final int SEARCH_LIMIT_LOWEND = 25; 
	static final int TRENDS_LIMIT = PlatformUtils.isNotS60() ? TRENDS_LIMIT_LOWEND : TRENDS_LIMIT_S60;
	static final int SEARCH_LIMIT = PlatformUtils.isNotS60() ? SEARCH_LIMIT_LOWEND : SEARCH_LIMIT_S60;
	static final int LATESTVIDEOS_LIMIT = 30;
	
	static final String NAME = "JTube";
	static final String[] VIDEO_QUALITIES = new String[] { "144p", "360p", "720p" };
	static final String[] SETTINGS_CHECKS = new String[] { "Remember search", "HTTP Proxy Streaming", "Pre-load previews to RMS" };
	static final String[] APPEARANCE_CHECKS = new String[] { "Custom items", "Video previews", "Channels in search" };
	
	public static String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0";
	
	static final String VIDEO_EXTENDED_FIELDS = "title,videoId,author,authorId,description,videoCount,published,publishedText,lengthSeconds,likeCount,dislikeCount,viewCount";
	static final String CHANNEL_EXTENDED_FIELDS = "subCount,author,authorId,description,totalViews";
	static final String PLAYLIST_EXTENDED_FIELDS = "author,authorId,videoCount,videos,title,videoId,videoThumbnails,playlistId,index";
	static final String VIDEO_FIELDS = "title,videoId,author,lengthSeconds";
	static final String SEARCH_FIELDS = "title,authorId,videoId,author,lengthSeconds";
	
	public static final int VIDEOFORM_AUTHOR_IMAGE_HEIGHT = 32;
	public static final int AUTHORITEM_IMAGE_HEIGHT = 48;
	
	public static String Path_separator = "/";

}
