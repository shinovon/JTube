import javax.microedition.lcdui.Command;

public interface Constants {
	
	// urls
	static final String getlinksphp = "http://nnproject.cc/getlinks.php";
	static final String hproxy = "http://nnproject.cc/hproxy.php?";
	static final String inv = "http://iteroni.com/";
	
	static final String CONFIG_RECORD_NAME = "ytconfig";
	
	// Main form commands
	static final Command settingsCmd = new Command("Settings", Command.SCREEN, 1);
	static final Command idCmd = new Command("Open by ID", Command.SCREEN, 2);
	static final Command searchCmd = new Command("Search", Command.SCREEN, 4);

	static final Command searchOkCmd = new Command("Search", Command.OK, 1);
	static final Command exitCmd = new Command("Exit", Command.EXIT, 2);
	static final Command goCmd = new Command("Go", Command.OK, 1);
	static final Command cancelCmd = new Command("Cancel", Command.CANCEL, 2);
	static final Command backCmd = new Command("Back", Command.BACK, 1);
	
	// Video page commands
	static final Command watchCmd = new Command("Watch", Command.OK, 1);
	static final Command downloadCmd = new Command("Download", Command.SCREEN, 2);
	//static final Command browserCmd = new Command("Open with browser", Command.SCREEN, 3);
	
	// Downloader alert commands
	static final Command dlOkCmd = new Command("OK", Command.CANCEL, 1);
	//static final Command dlWatchCmd = new Command("Watch", Command.SCREEN, 2);
	static final Command dlOpenCmd = new Command("Open", Command.OK, 1);
	static final Command dlCancelCmd = new Command("Cancel", Command.CANCEL, 1);
	
	// Limits
	static final int TRENDS_LIMIT = 20;
	static final int SEARCH_LIMIT = 20; 
	
	static final String NAME = "Some";
	static final String[] VIDEO_QUALITIES = new String[] { "144p", "360p", "720p" };
	
	static final String platform = System.getProperty("microedition.platform");
	
	static final String downloadUserAgent = "User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0";
	static final String apiUserAgent = "User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0";

}
