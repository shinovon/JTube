package ui;
import javax.microedition.lcdui.Command;

import Locale;
import LocaleConstants;

public interface Commands extends LocaleConstants {
	
	// Main form commands
	static final Command settingsCmd = new Command(Locale.s(CMD_Settings), Command.SCREEN, 4);
	static final Command idCmd = new Command(Locale.s(CMD_OpenByID), Command.SCREEN, 8);
	static final Command searchCmd = new Command(Locale.s(CMD_Search), Command.SCREEN, 7);
	static final Command aboutCmd = new Command(Locale.s(CMD_About), Command.SCREEN, 2);
	static final Command switchToPopularCmd = new Command(Locale.s(CMD_SwitchToPopular), Command.SCREEN, 5);
	static final Command switchToTrendsCmd = new Command(Locale.s(CMD_SwitchToTrends), Command.SCREEN, 5);
	static final Command exitCmd = new Command(Locale.s(CMD_Exit), Command.EXIT, 2);
	
	static final Command searchOkCmd = new Command(Locale.s(CMD_Search), Command.OK, 1);
	static final Command goCmd = new Command(Locale.s(CMD_Go), Command.OK, 1);
	static final Command cancelCmd = new Command(Locale.s(CMD_Cancel), Command.CANCEL, 2);
	static final Command backCmd = new Command(Locale.s(CMD_Back), Command.BACK, 1);
	
	static final Command applyCmd = new Command(Locale.s(CMD_Apply), Command.BACK, 1);
	
	// Video page commands
	static final Command watchCmd = new Command(Locale.s(CMD_Watch), Command.OK, 10);
	static final Command downloadCmd = new Command(Locale.s(CMD_Download), Command.SCREEN, 9);
	//static final Command browserCmd = new Command("Open with browser", Command.SCREEN, 3);
	public static Command openPlaylistCmd = new Command(Locale.s(CMD_OpenPlaylist), Command.SCREEN, 7);
	public static Command nextCmd = new Command(Locale.s(CMD_Next), Command.SCREEN, 6);
	public static Command prevCmd = new Command(Locale.s(CMD_Prev), Command.SCREEN, 5);
	
	// Downloader alert commands
	static final Command dlOkCmd = new Command(Locale.s(CMD_OK), Command.CANCEL, 1);
	//static final Command dlWatchCmd = new Command("Watch", Command.SCREEN, 2);
	static final Command dlOpenCmd = new Command(Locale.s(CMD_Open), Command.OK, 1);
	static final Command dlCancelCmd = new Command(Locale.s(CMD_Cancel), Command.CANCEL, 1);
	
	public static Command vOpenCmd = new Command(Locale.s(CMD_View), Command.ITEM, 10);
	public static Command vOpenChannelCmd = new Command(Locale.s(CMD_ViewChannel), Command.ITEM, 4);
	
	public static Command cVideosCmd = new Command(Locale.s(CMD_Videos), Command.ITEM, 3);
	
	public static Command qrCmd = new Command("qr scan", Command.SCREEN, 1);

}