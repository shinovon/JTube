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
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import ui.TestCanvas;

public class Util implements Constants {
	
	private static int buffer_size;
	
	private static String charset = "utf-8, iso-8859-1;q=0.5";
	private static String alt_charset = "iso-8859-1";
	
	private static Canvas testCanvas;
	
	static {
		try {
			buffer_size = Settings.isLowEndDevice() ? 512 : 4096;
		} catch (Throwable t) {
			buffer_size = 1024;
		}
		String s = System.getProperty("microedition.encoding");
		if(s != null) {
			alt_charset = s.toLowerCase();
		}
		// Test UTF-8 support
		try {
			String tmp = new String("test".getBytes("UTF-8"), "UTF-8");
			tmp.charAt(0);
		} catch (Throwable e) {
			charset = alt_charset;
		}
	} 

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
			if(charset != null) {
				hc.setRequestProperty("accept-charset", charset);
			}
			String locale = Locale.s(Locale.ISOLanguageCode);
			if(locale != null) {
				hc.setRequestProperty("accept-language", locale);
			}
			if(isLoader) {
				if(il.checkInterrupted()) {
					throw new RuntimeException("loader interrupt");
				}
			} else {
				Thread.sleep(1);
			}
			int r = hc.getResponseCode();
			int redirects = 0;
			while (r == 301 || r == 302 && hc.getHeaderField("Location") != null) {
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
			Thread.sleep(200);
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
			charset = alt_charset;
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
		int h = i / 3600;
		if(h > 0) {
			if(m.length() < 2)
				m = "0" + m;
			return h + ":" + m + ":" + s;
		} else {
			return m + ":" + s;
		}
	}
	
	public static String[] getStringArray(String text, int maxWidth, Font font) {
		if (text == null || text.length() == 0 || text.equals(" ") || maxWidth < font.charWidth('W') + 2) {
			return new String[0];
		}
		text = replace(text, "\r", "");
		Vector v = new Vector(3);
		char[] chars = text.toCharArray();
		if (text.indexOf('\n') > -1) {
			int j = 0;
			for (int i = 0; i < text.length(); i++) {
				if (chars[i] == '\n') {
					v.addElement(text.substring(j, i));
					j = i + 1;
				}
			}
			v.addElement(text.substring(j, text.length()));
		} else {
			v.addElement(text);
		}
		for (int i = 0; i < v.size(); i++) {
			String s = (String) v.elementAt(i);
			if(font.stringWidth(s) >= maxWidth) {
				int i1 = 0;
				for (int i2 = 0; i2 < s.length(); i2++) {
					if (font.stringWidth(s.substring(i1, i2+1)) >= maxWidth) {
						boolean space = false;
						for (int j = i2; j > i1; j--) {
							char c = s.charAt(j);
							if (c == ' ' || (c >= ',' && c <= '/')) {
								space = true;
								v.setElementAt(s.substring(i1, j + 1), i);
								v.insertElementAt(s.substring(j + 1), i + 1);
								i += 1;
								i2 = i1 = j + 1;
								break;
							}
						}
						if (!space) {
							i2 = i2 - 2;
							v.setElementAt(s.substring(i1, i2), i);
							v.insertElementAt(s.substring(i2), i + 1);
							i2 = i1 = i2 + 1;
							i += 1;
						}
					}
				}
			}
		}
		String[] arr = new String[v.size()];
		v.copyInto(arr);
		return arr;
	}
	
	public static float lerp(float start, float target, float mul, float div) {
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

	public static Canvas testCanvas() {
		if(testCanvas == null)
			testCanvas = new TestCanvas();
		App.width = testCanvas.getWidth();
		App.height = testCanvas.getHeight();
		return testCanvas;
	}

	public static String getFileName(String s) {
		char[] cs = s.toCharArray();
		StringBuffer sb = new StringBuffer();
		final char[] arr1 = new char[]     { 'щ',  'ю', 'я',  'ц',  'ч',  'ш',  'а', 'б', 'в', 'г', 'д', 'е', 'ж',  'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ъ','ы','ь', 'э'};
		final String[] arr2 = new String[] { "sh", "u", "ya", "ts", "ch", "sh", "a", "b", "v", "g", "d", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "", "y", "", "e"};
		for(int i = 0; i < cs.length && i < 32; i++) {
			char c = cs[i];
			// replace spaces
			if(c <= 32) sb.append('_');
			// chars to remove
			else if(c == '/' || c == '\\'|| c == '.' ||
					c == ',' || c == '*' || c == ':' ||
					c == '?' || c == '!' || c == '\''|| 
					c == '"' || c == '<' || c == '>' || 
					c == '[' || c == ']' || c == '(' || 
					c == ')' || c == '^' || c == '`' ||
					c == ';')
				continue;
			else if(c >= 1040 && c <= 1103) {
				c = Character.toLowerCase(c);
				// replace russian letters
				for(int j = 0; j < arr1.length; j++) {
					if(arr1[j] == c) {
						sb.append(arr2[j]);
						break;
					}
				}
			} else if(c >= 123) continue;
			else sb.append(c);
		}
		s = sb.toString();
		return s;
	}
	
	public static String decodeURL(String s) {
		boolean needToChange = false;
		int numChars = s.length();
		StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2 : numChars);
		int i = 0;
		char c;
		byte[] bytes = null;
		while (i < numChars) {
			c = s.charAt(i);
			switch (c) {
			case '%':
				try {
					if (bytes == null)
						bytes = new byte[(numChars - i) / 3];
					int pos = 0;
					while (((i + 2) < numChars) && (c == '%')) {
						int v = Integer.parseInt(s.substring(i + 1, i + 3), 16);
						if (v < 0)
							throw new IllegalArgumentException();
						bytes[pos++] = (byte) v;
						i += 3;
						if (i < numChars)
							c = s.charAt(i);
					}
					if ((i < numChars) && (c == '%'))
						throw new IllegalArgumentException();
					sb.append(new String(bytes, 0, pos, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new IllegalArgumentException();
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException();
				}
				needToChange = true;
				break;
			default:
				sb.append(c);
				i++;
				break;
			}
		}
		return (needToChange ? sb.toString() : s);
	}
	
	public static Image invert(Image img) {
		int w = img.getWidth();
		int h = img.getHeight();

		int[] buffer = new int[w * h];

		img.getRGB(buffer, 0, w, 0, 0, w, h);
		img = null;
		for(int i = 0; i < buffer.length; i++) {
			int c = buffer[i];
			int a = (c >> 24) & 0xFF;
			int r = 0xFF - ((c >> 16) & 0xFF);
			int g = 0xFF - ((c >> 8) & 0xFF);
			int b = 0xFF - (c & 0xFF);
			buffer[i] = (a<<24) | (r<<16) | (g<<8) | b;
		}
		return Image.createRGBImage(buffer, w, h, true);
	}

}
