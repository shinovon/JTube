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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;

import cc.nnproject.json.JSONObject;
import ui.AppUI;

public class Downloader implements CommandListener, Runnable, Constants, LocaleConstants {
	
	private String id;
	private String res;
	private String name;
	
	private Alert alert;
	private Gauge indicator;
	
	private String file;
	private Thread t;
	private boolean cancel;
	
	static final Command dlOkCmd = new Command(Locale.s(CMD_OK), Command.CANCEL, 1);
	static final Command dlOpenCmd = new Command(Locale.s(CMD_Open), Command.OK, 1);
	static final Command dlCancelCmd = new Command(Locale.s(CMD_Cancel), Command.CANCEL, 1);

	public Downloader(String vid, String res, String downloadDir, String name) {
		this.id = vid;
		this.name = name;
		this.res = res;
		this.file = "file:///" + downloadDir;
		if(!(file.endsWith("/") || file.endsWith("\\"))) {
			file += Path_separator;
		}
	}
	
	public void run() {
		if(cancel) return;
		FileConnection fc = null;
		OutputStream out = null;
		HttpConnection hc = null;
		InputStream in = null;
		try {
			String f = id;
			if(name != null) {
				f = Util.getFileName(name);
				if(f.length() <= 1)
					f = id;
			}
			if(res != null) {
				if(res.equals("144p")) {
					f += ".3gp";
				} /*else if(res.startsWith("_audio")) {
					f += ".aac";
				} */else {
					f += ".mp4";
				}
			} else {
				f += ".mp4";
			} 
			file = file + f;
			info(f);
			
			JSONObject o = App.getVideoInfo(id, res);
			String url = o.getString("url");
			if(Settings.httpStream) {
				if(Settings.iteroniPlaybackProxy) {
					int i = url.indexOf("/videoplayback");
					url = "http://iteroni.com" + url.substring(i);
				} else {
					url = Settings.serverstream + Util.url(url);
				}
			}
			int contentLength = o.getInt("clen", 0);
			o = null;
			// wait
			Thread.sleep(500);
			fc = (FileConnection) Connector.open(file, Connector.READ_WRITE);
			
			if (fc.exists()) {
				try {
					fc.delete();
				} catch (IOException e) {
				}
			}
			fc.create();
			info(Locale.s(TXT_Connecting));
			hc = (HttpConnection) Connector.open(url);
			hc.setRequestProperty("User-Agent", userAgent);
			int r;
			try {
				r = hc.getResponseCode();
			} catch (IOException e) {
				info(Locale.s(TXT_Waiting));
				try {
					hc.close();
				} catch (Exception e2) {
				}
				Thread.sleep(2000);
				info("Connection retry");
				hc = (HttpConnection) Connector.open(url);
				hc.setRequestMethod("GET");
				hc.setRequestProperty("User-Agent", userAgent);
				r = hc.getResponseCode();
			}
			if(cancel) return;
			int redirectCount = 0;
			while (r == 301 || r == 302) {
				info(Locale.s(TXT_Redirected) + " (" + redirectCount++ + ")");
				String redir = hc.getHeaderField("Location");
				if (redir.startsWith("/")) {
					String tmp = url.substring(url.indexOf("//") + 2);
					String host = url.substring(0, url.indexOf("//")) + "//" + tmp.substring(0, tmp.indexOf("/"));
					redir = host + redir;
				}
				hc.close();
				hc = (HttpConnection) Connector.open(redir);
				hc.setRequestMethod("GET");
				hc.setRequestProperty("User-Agent", userAgent);
				r = hc.getResponseCode();
			}
			if(cancel) return;
			out = fc.openOutputStream();
			in = hc.openInputStream();
			info(Locale.s(TXT_Connected));
			int percent = 0;
			int bufSize = Settings.downloadBuffer;
			byte[] buf = new byte[bufSize];
			int read = 0;
			int downloaded = 0;
			int l = contentLength;
			if(l <= 0) {
				try {
					l = (int) hc.getLength();
				} catch (Exception e) {
			
				}
			}
			boolean ind = true;
			if(l <= 0) {
				// indicator unavailable
				alert.setIndicator(null);
				ind = false;
			}
			int a = 10;
			if(bufSize <= 1024) {
				a = 100;
			} else if(bufSize < 10240) {
				a = 50;
			} else if(bufSize >= 10240) {
				a = 10;
			} else if(bufSize >= 32768) {
				a = 2;
			}
			if(cancel) return;
			String sizeStr = "" + ((int) (((double)l / 1024 / 1024) * 10)) / 10D;
			long t = System.currentTimeMillis();
			int s = 0;
			int i = 0;
			while((read = in.read(buf)) != -1) {
				out.write(buf, 0, read);
				downloaded += read;
				if(i++ % a == 0) {
					if(cancel) return;
					long t2 = System.currentTimeMillis();
					int j = 0;
					String spd = null;
					if(s != 0) {
						j = (int) ((((double) (downloaded - s) / (t2 - t)) * 1000) / 1024D);
						if(j <= 0) {
							j = 0;
						}
					}
					if(j > 1024) {
						spd = (((int)((j / 1024D) * 10)) / 10D) + " MB/s";
					} else {
						spd = j + " KB/s";
					}
					if(ind) {
						percent = (int)(((double)downloaded / (double)l) * 100d);
						info(Locale.s(TXT_Downloading) + " \n" + 
						((int) ((downloaded / 1024D / 1024D) * 100)) / 100D + " / " + sizeStr + " MB \n("
								+ spd + ") " + percent + "%"
								, percent);
					} else {
						info(Locale.s(TXT_Downloaded) + " " + (downloaded / 1024) + " Kbytes \n" + spd);
					}
					t = t2;
					s = downloaded;
				}
			}
			done();
		} catch (InterruptedException e) {
			fail(Locale.s(TXT_Canceled), "");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString(), Locale.s(TXT_DownloadFailed));
		} finally {
			try {
				if(out != null) out.close();
			} catch (Exception e) {
			} 
			try {
				if(fc != null) fc.close();
			} catch (Exception e) {
			} 
			try {
				if(in != null) in.close();
			} catch (Exception e) {
			} 
			try {
				if(hc != null) hc.close();
			} catch (Exception e) {
			} 
		}
	}
	
	public void start() {
		alert = new Alert("", Locale.s(TXT_Initializing), null, null);
		alert.addCommand(dlCancelCmd);
		alert.setTimeout(Alert.FOREVER);
		AppUI.inst.display(alert);
		alert.setCommandListener(this);
		this.indicator = new Gauge(null, false, 100, 0); 
		alert.setIndicator(indicator);
		t = new Thread(this);
		t.start();
	}

	private void done() {
		hideIndicator();
		info(Locale.s(TXT_Done) + "\n" + file);
		alert.addCommand(dlOkCmd);
		alert.addCommand(dlOpenCmd);
	}

	private void hideIndicator() {
		alert.removeCommand(dlCancelCmd);
		alert.setIndicator(null);
	}

	private void fail(String s, String title) {
		hideIndicator();
		alert.setType(AlertType.ERROR);
		alert.setTitle(title);
		alert.setString(s);
		alert.addCommand(dlOkCmd);
	}

	private void info(String s, int percent) {
		if(indicator != null && percent >= 0 && percent <= 100) {
			indicator.setValue(percent);
		}
		info(s);
	}

	private void info(String s) {
		alert.setString(s);
	}

	public void commandAction(Command c, Displayable _d) {
		if(c == dlOkCmd) {
			AppUI.inst.display(null);
		}
		if(c == dlCancelCmd) {
			cancel = true;
			t.interrupt();
			AppUI.inst.display(null);
		}
		if(c == dlOpenCmd) {
			try {
				Util.platReq(file);
				AppUI.inst.display(null);
			} catch (Exception e) {
				e.printStackTrace();
				info(e.toString());
			}
		}
	}

}
