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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;

import ui.TestCanvas;

public class Util implements Constants {
	
	private static int buffer_size = Settings.isLowEndDevice() ? 512 : 4096;

	public static byte[] get(String url) throws IOException {
		if (url == null)
			throw new IllegalArgumentException("URL is null");
		boolean isLoader = Thread.currentThread() instanceof LoaderThread;
		LoaderThread il = null;
		if(isLoader) {
			il = (LoaderThread) Thread.currentThread();
		}
		ByteArrayOutputStream o = null;
		HttpConnection hc = null;
		InputStream in = null;
		try {
			hc = (HttpConnection) Connector.open(url);
			hc.setRequestMethod("GET");
			hc.setRequestProperty("User-Agent", userAgent);
			if(isLoader) {
				if(il.checkInterrupted()) {
					throw new RuntimeException("loader interrupt");
				}
			} else {
				Thread.sleep(1);
			}
			int r = hc.getResponseCode();
			int redirects = 0;
			while (r == 301 || r == 302) {
				String redir = hc.getHeaderField("Location");
				if (redir.startsWith("/")) {
					String tmp = url.substring(url.indexOf("//") + 2);
					String host = url.substring(0, url.indexOf("//")) + "//" + tmp.substring(0, tmp.indexOf("/"));
					redir = host + redir;
				}
				hc.close();
				hc = (HttpConnection) Connector.open(redir);
				hc.setRequestMethod("GET");
				if(redirects++ > 8) {
					throw new IOException("Too many redirects!");
				}
			}
			if(r >= 400 && r != 500) throw new IOException(r + " " + hc.getResponseMessage());
			in = hc.openInputStream();
			int read;
			o = new ByteArrayOutputStream();
			byte[] b = new byte[buffer_size];
			while((read = in.read(b)) != -1) {
				o.write(b, 0, read);
			}
			return o.toByteArray();
		} catch (InterruptedException e) {
			throw new RuntimeException("interrupted");
		} finally {
			try {
				if (in != null) in.close();
			} catch (IOException e) {
			}
			try {
				if (hc != null) hc.close();
			} catch (IOException e) {
			}
			try {
				if (o != null) o.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static String getUtf(String url) throws IOException {
		byte[] b = get(url);
		try {
			return new String(b, "UTF-8");
		} catch (Throwable e) {
			return new String(b);
		}
	}
	
	public static String url(String url) {
		StringBuffer sb = new StringBuffer();
		char[] chars = url.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			int c = chars[i];
			if (65 <= c && c <= 90) {
				sb.append((char) c);
			} else if (97 <= c && c <= 122) {
				sb.append((char) c);
			} else if (48 <= c && c <= 57) {
				sb.append((char) c);
			} else if (c == 32) {
				sb.append("%20");
			} else if (c == 45 || c == 95 || c == 46 || c == 33 || c == 126 || c == 42 || c == 39 || c == 40
					|| c == 41) {
				sb.append((char) c);
			} else if (c <= 127) {
				sb.append(hex(c));
			} else if (c <= 2047) {
				sb.append(hex(0xC0 | c >> 6));
				sb.append(hex(0x80 | c & 0x3F));
			} else {
				sb.append(hex(0xE0 | c >> 12));
				sb.append(hex(0x80 | c >> 6 & 0x3F));
				sb.append(hex(0x80 | c & 0x3F));
			}
		}
		return sb.toString();
	}

	private static String hex(int i) {
		String s = Integer.toHexString(i);
		return "%".concat(s.length() < 2 ? "0" : "").concat(s);
	}

	public static String replace(String str, String from, String to) {
		int j = str.indexOf(from);
		if (j == -1)
			return str;
		final StringBuffer sb = new StringBuffer();
		int k = 0;
		for (int i = from.length(); j != -1; j = str.indexOf(from, k)) {
			sb.append(str.substring(k, j)).append(to);
			k = j + i;
		}
		sb.append(str.substring(k, str.length()));
		return sb.toString();
	}

	public static String timeStr(int i) {
		if(i <= 0) return null;
		String s = "" + i % 60;
		if(s.length() < 2) s = "0" + s;
		String m = "" + (i % 3600) / 60;
		if(m.length() < 2) m = "0" + m;
		int h = i / 3600;
		if(h > 0) {
			return h + ":" + m + ":" + s;
		} else {
			return m + ":" + s;
		}
	}
	
	public static String[] getStringArray(String text, int maxWidth, Font font) {
		if (text == null || text.length() == 0 || text.equals(" ")) {
			return new String[0];
		}
		final int max = 3;
		Vector v = new Vector(max);
		v: {
			if (font.stringWidth(text) > maxWidth) {
				int i1 = 0;
				for (int i2 = 0; i2 < text.length(); i2++) {
					if(v.size() >= max) break v;
					if (text.charAt(i2) == '\n') {
						v.addElement(text.substring(i1, i2));
						i2 = i1 = i2 + 1;
					} else {
						if (text.length() - i2 <= 1) {
							v.addElement(text.substring(i1, text.length()));
							break;
						} else if (font.stringWidth(text.substring(i1, i2)) >= maxWidth) {
							boolean space = false;
							for (int j = i2; j > i1; j--) {
								char c = text.charAt(j);
								if (c == ' ' || (c >= ',' && c <= '/')) {
									String s = text.substring(i1, j + 1);
									if(font.stringWidth(s) >= maxWidth - 1) {
										continue;
									}
									space = true;
									v.addElement(s);
									i2 = i1 = j + 1;
									break;
								}
							}
							if (!space) {
								i2 = i2 - 2;
								v.addElement(text.substring(i1, i2));
								i2 = i1 = i2 + 1;
							}
						}
					}
				}
			} else {
				return new String[] { text };
			}
		}
		String[] arr = new String[v.size()];
		v.copyInto(arr);
		return arr;
	}
	
	public static int lerp(int start, int target, int mul, int div) {
		return start + ((target - start) * mul / div);
	}

	public static int clamp(int val, int min, int max) {
		return Math.max(Math.min(val, max), min);
	}
	
	public static void gc() {
		System.gc();
	}
	
	public static void platReq(String s) throws ConnectionNotFoundException {
		if(App.midlet.platformRequest(s)) {
			App.midlet.notifyDestroyed();
		}
	}

	public static void testCanvas() {
		Canvas c = new TestCanvas();
		App.width = c.getWidth();
		App.height = c.getHeight();
	}

}
