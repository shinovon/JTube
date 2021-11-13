import javax.microedition.lcdui.Command;

import ru.nnproject.utils.PlatformUtils;

public interface Constants {
	
	// urls
	static final String getlinksphp = "http://nnproject.cc/getlinks.php";
	static final String hproxy = "http://nnproject.cc/hproxy.php?";
	static final String iteroni = "http://iteroni.com/";
	static final String streamphp = "http://nnproject.cc/stream.php";
	
	static final String CONFIG_RECORD_NAME = "ytconfig";
	
	
	// Main form commands
	static final Command settingsCmd = new Command("Settings", Command.SCREEN, 2);
	static final Command idCmd = new Command("Open by ID", Command.SCREEN, 4);
	static final Command searchCmd = new Command("Search", Command.SCREEN, 5);
	static final Command switchToPopularCmd = new Command("Switch to popular", Command.SCREEN, 1);
	static final Command switchToTrendsCmd = new Command("Switch to trends", Command.SCREEN, 1);
	
	static final Command searchOkCmd = new Command("Search", Command.OK, 1);
	static final Command exitCmd = new Command("Exit", Command.EXIT, 2);
	static final Command goCmd = new Command("Go", Command.OK, 1);
	static final Command cancelCmd = new Command("Cancel", Command.CANCEL, 2);
	static final Command backCmd = new Command("Back", Command.BACK, 1);
	
	static final Command applyCmd = new Command("Apply", Command.BACK, 1);
	
	// Video page commands
	static final Command watchCmd = new Command("Watch", Command.OK, 3);
	static final Command downloadCmd = new Command("Download", Command.SCREEN, 2);
	//static final Command browserCmd = new Command("Open with browser", Command.SCREEN, 3);
	
	// Downloader alert commands
	static final Command dlOkCmd = new Command("OK", Command.CANCEL, 1);
	//static final Command dlWatchCmd = new Command("Watch", Command.SCREEN, 2);
	static final Command dlOpenCmd = new Command("Open", Command.OK, 1);
	static final Command dlCancelCmd = new Command("Cancel", Command.CANCEL, 1);
	
	public static Command vOpenCmd = new Command("Open video", Command.ITEM, 3);
	
	// Limits
	static final int TRENDS_LIMIT_S60 = 25;
	static final int SEARCH_LIMIT_S60 = 30; 
	static final int TRENDS_LIMIT_LOWEND = 20;
	static final int SEARCH_LIMIT_LOWEND = 25; 
	static final int TRENDS_LIMIT = PlatformUtils.isNotS60() ? TRENDS_LIMIT_LOWEND : TRENDS_LIMIT_S60;
	static final int SEARCH_LIMIT = PlatformUtils.isNotS60() ? SEARCH_LIMIT_LOWEND : SEARCH_LIMIT_S60;
	
	static final String NAME = "Unnamed";
	static final String[] VIDEO_QUALITIES = new String[] { "144p", "360p", "720p" };
	static final String[] SETTINGS_CHECKS = new String[] { "Video previews", "Channels in search", "Remember search", "HTTP Proxy Streaming" };
	
	public static String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0";
	
	static final String VIDEO_EXTENDED_FIELDS = "title,videoId,videoThumbnails,author,authorId,description,videoCount,published,publishedText,lengthSeconds,likeCount,dislikeCount,authorThumbnails,viewCount";
	static final String TRENDING_FIELDS = "title,videoId,author";
	
	public static final int VIDEOFORM_AUTHOR_IMAGE_HEIGHT = 32;
	public static final int AUTHORITEM_IMAGE_HEIGHT = 48;
	

}
