/*
Copyright (c) 2022 Arman Jussupgaliyev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package ui;

import javax.microedition.lcdui.Command;

import Locale;
import LocaleConstants;

public interface Commands extends LocaleConstants {
	
	// Main form commands
	static final Command settingsCmd = new Command(Locale.s(CMD_Settings), Command.SCREEN, 9);
	static final Command idCmd = new Command(Locale.s(CMD_OpenByID), Command.SCREEN, 6);
	static final Command searchCmd = new Command(Locale.s(CMD_Search), Command.SCREEN, 2);
	static final Command aboutCmd = new Command(Locale.s(CMD_About), Command.SCREEN, 10);
	static final Command switchToPopularCmd = new Command(Locale.s(CMD_SwitchToPopular), Command.SCREEN, 4);
	static final Command switchToTrendsCmd = new Command(Locale.s(CMD_SwitchToTrends), Command.SCREEN, 4);
	static final Command exitCmd = new Command(Locale.s(CMD_Exit), Command.EXIT, 2);
	
	static final Command searchOkCmd = new Command(Locale.s(CMD_Search), Command.OK, 1);
	static final Command goCmd = new Command(Locale.s(CMD_Go), Command.OK, 1);
	static final Command cancelCmd = new Command(Locale.s(CMD_Cancel), Command.CANCEL, 2);
	static final Command backCmd = new Command(Locale.s(CMD_Back), Command.BACK, 1);
	
	static final Command applyCmd = new Command(Locale.s(CMD_Apply), Command.BACK, 1);
	
	// Video page commands
	static final Command watchCmd = new Command(Locale.s(CMD_Watch), Command.OK, 1);
	static final Command downloadCmd = new Command(Locale.s(CMD_Download), Command.SCREEN, 4);
	static final Command showLinkCmd = new Command(Locale.s(CMD_ShowLink), Command.SCREEN, 6);
	//static final Command browserCmd = new Command("Open with browser", Command.SCREEN, 3);
	public static Command openPlaylistCmd = new Command(Locale.s(CMD_OpenPlaylist), Command.SCREEN, 7);
	public static Command nextCmd = new Command(Locale.s(CMD_Next), Command.SCREEN, 6);
	public static Command prevCmd = new Command(Locale.s(CMD_Prev), Command.SCREEN, 5);
	
	// Downloader alert commands
	static final Command dlOkCmd = new Command(Locale.s(CMD_OK), Command.CANCEL, 1);
	//static final Command dlWatchCmd = new Command("Watch", Command.SCREEN, 2);
	static final Command dlOpenCmd = new Command(Locale.s(CMD_Open), Command.OK, 1);
	static final Command dlCancelCmd = new Command(Locale.s(CMD_Cancel), Command.CANCEL, 1);
	
	public static Command vOpenCmd = new Command(Locale.s(CMD_View), Command.ITEM, 3);
	public static Command vOpenChannelCmd = new Command(Locale.s(CMD_ViewChannel), Command.ITEM, 4);
	
	public static Command cVideosCmd = new Command(Locale.s(CMD_Videos), Command.ITEM, 3);

}
