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

import Locale;
import LocaleConstants;
import javax.microedition.lcdui.Command;

public interface Commands extends LocaleConstants {

	static final Command okCmd = new Command(Locale.s(CMD_OK), Command.OK, 1);
	static final Command exitCmd = new Command(Locale.s(CMD_Exit), Command.EXIT, 12);
	static final Command optsCmd = new Command(Locale.s(CMD_Func), Command.SCREEN, 13);
	static final Command menuCmd = new Command(Locale.s(CMD_FuncMenu), Command.SCREEN, 13);
	static final Command searchOkCmd = new Command(Locale.s(CMD_Search), Command.OK, 1);
	static final Command goCmd = new Command(Locale.s(CMD_Go), Command.OK, 1);
	static final Command cancelCmd = new Command(Locale.s(CMD_Cancel), Command.CANCEL, 2);
	static final Command backCmd = new Command(Locale.s(CMD_Back), Command.BACK, 1);
	
	// Video page commands
	/*
	static final Command watchCmd = new Command(Locale.s(CMD_Watch), Command.OK, 1);
	static final Command downloadCmd = new Command(Locale.s(CMD_Download), Command.SCREEN, 4);
	static final Command showLinkCmd = new Command(Locale.s(CMD_ShowLink), Command.SCREEN, 5);
	public static Command openPlaylistCmd = new Command(Locale.s(CMD_OpenPlaylist), Command.SCREEN, 8);
	public static Command nextCmd = new Command(Locale.s(CMD_Next), Command.SCREEN, 7);
	public static Command prevCmd = new Command(Locale.s(CMD_Prev), Command.SCREEN, 6);
	*/
	static final Command applyCmd = new Command(Locale.s(CMD_Apply), Command.BACK, 1);
}