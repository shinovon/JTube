package ru.integration;

import java.util.Enumeration;
import java.util.Hashtable;

public class IntegrationUtils {
	
	private static final String PROTOCOL = "javaapp:";
	private static final String ARG_SEPARATOR = ";";
	
	/**
	 * Сгенерировать URL для запроса platformRequest на запуск мидлета
	 * @param name MIDlet-Name
	 * @param vendor MIDlet-Vendor
	 * @return URL
	 */
	public static String getMIDletRequestURL(String name, String vendor) {
		return makeMIDletRequestURL(name, vendor, "");
	}

	/**
	 * Сгенерировать URL для запроса platformRequest на запуск мидлета c дополнительными аргументами
	 * @param name MIDlet-Name
	 * @param vendor MIDlet-Vendor
	 * @param args Аргументы
	 * @return URL
	 */
	public static String getMIDletRequestURL(String name, String vendor, Hashtable args) {
		return makeMIDletRequestURL(name, vendor, generateArgs(args));
	}
	
	/**
	 * Получить таблицу аргументов из cmdline
	 * @param cmdline Закодированные аргументы
	 * @return Таблица
	 */
	public static Hashtable parseRequestArguments(String cmdline) {
		return split(cmdline, ARG_SEPARATOR);
	}

	static String makeMIDletRequestURL(String name, String vendor, String args) {
		return PROTOCOL + "midlet-name=" + url(name) + ARG_SEPARATOR + "midlet-vendor=" + url(vendor) + args;
	}
	
	private static String generateArgs(Hashtable args) {
		StringBuffer sb = new StringBuffer();
		sb.append(ARG_SEPARATOR);
		Enumeration en = args.keys();
		while (true) {
			Object k = en.nextElement();
			sb.append(k.toString()).append("=").append(url(args.get(k).toString()));
			if (!en.hasMoreElements()) {
				return sb.toString();
			}
			sb.append(ARG_SEPARATOR);
		}
	}

	// модифицированная утилка для сплита из сурсов S60
	static Hashtable split(String str, String separator) {
		Hashtable ht = new Hashtable();
		int separatorLen = separator.length();
		int index = str.indexOf(separator);
		boolean b = true;
		while (b) {
			if (index == -1) {
				b = false;
				index = str.length();
			}
			String token = str.substring(0, index).trim();
			int index2 = token.indexOf("=");
			ht.put(token.substring(0, index2).trim(), token.substring(index2 + 1));
			if (b) {
				str = str.substring(index + separatorLen);
				index = str.indexOf(separator);
			}
		}
		return ht;
	}

	// декодер URL
	static String url(String url) {
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
			} else if (c == ' ') {
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
		return "%" + (s.length() < 2 ? "0" : "") + s;
	}

}
