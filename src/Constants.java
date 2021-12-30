import cc.nnproject.utils.PlatformUtils;

public interface Constants {
	
	// urls
	static final String getlinksphp = "http://nnproject.cc/getlinks.php";
	static final String hproxy = "http://nnproject.cc/hproxy.php?";
	static final String iteroni = "http://iteroni.com/";
	static final String streamphp = "http://nnproject.cc/stream.php";
	
	static final String CONFIG_RECORD_NAME = "ytconfig";
	
	// Limits
	static final int TRENDS_LIMIT_S60 = 23;
	static final int SEARCH_LIMIT_S60 = 30; 
	static final int TRENDS_LIMIT_LOWEND = 20;
	static final int SEARCH_LIMIT_LOWEND = 25; 
	static final int TRENDS_LIMIT = PlatformUtils.isNotS60() ? TRENDS_LIMIT_LOWEND : TRENDS_LIMIT_S60;
	static final int SEARCH_LIMIT = PlatformUtils.isNotS60() ? SEARCH_LIMIT_LOWEND : SEARCH_LIMIT_S60;
	static final int LATESTVIDEOS_LIMIT = 30;
	
	static final String NAME = "JTube";
	
	public static String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0";
	
	static final String VIDEO_EXTENDED_FIELDS = "title,videoId,author,authorId,description,videoCount,published,publishedText,lengthSeconds,likeCount,dislikeCount,viewCount";
	static final String CHANNEL_EXTENDED_FIELDS = "subCount,author,authorId,description,totalViews";
	static final String PLAYLIST_EXTENDED_FIELDS = "author,authorId,videoCount,videos,title,videoId,videoThumbnails,playlistId,index,lengthSeconds";
	static final String VIDEO_FIELDS = "title,videoId,author,lengthSeconds";
	static final String SEARCH_FIELDS = "title,authorId,videoId,author,lengthSeconds";
	
	public static final int VIDEOFORM_AUTHOR_IMAGE_HEIGHT = 32;
	public static final int AUTHORITEM_IMAGE_HEIGHT = 48;
	
	public static String Path_separator = "/";

}
