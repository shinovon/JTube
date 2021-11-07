package cc.nnproject.json;

import java.util.Hashtable;
import java.util.Vector;

/**
 * JSON Library by nnproject.cc<br>
 * Usage:<p><code>JSONObject obj = NNJSON.getObject(str);</code>
 * @author Shinovon
 * @version 1.0
 */
public final class JSON {

	/**
	 * Parse all members once
	 */
	public final static boolean parse_members = false;
	/**
	 * Enable build functions
	 */
	public final static boolean build_functions = true;
	public final static String format_space = "  ";
	public final static Object null_equivalent = new NullEquivalent();

	public static JSONObject getObject(String string) throws JSONException {
		if (string == null || string.length() <= 1)
			throw new JSONException("Empty string");
		if (string.charAt(0) != '{')
			throw new JSONException("Not JSON object");
		return (JSONObject) parseJSON(string);
	}

	public static JSONArray getArray(String string) throws JSONException {
		if (string == null || string.length() <= 1)
			throw new JSONException("Empty string");
		if (string.charAt(0) != '[')
			throw new JSONException("Not JSON array");
		return (JSONArray) parseJSON(string);
	}

	static Object getJSON(Object obj) throws JSONException {
		if (obj instanceof Hashtable) {
			return new JSONObject((Hashtable) obj);
		} else if (obj instanceof Vector) {
			return new JSONArray((Vector) obj);
		} else {
			return obj;
		}
	}

	static Object parseJSON(String str) throws JSONException {
		if (str == null || str.equals(""))
			throw new JSONException("Empty string");
		if (str.length() < 2) {
			return str;
		} else {
			char first = str.charAt(0);
			char last = str.charAt(str.length() - 1);
			if (first == '{' && last != '}' || first == '[' && last != ']' || first == '"' && last != '"') {
				throw new JSONException("Unexpected end of text");
			} else if (first == '"') {
				// String
				str = str.substring(1, str.length() - 1);
				try {
					int l = str.length();
					StringBuffer sb = new StringBuffer();
					int i = 0;
					// Parse string escape chars
					loop: {
						while (i < l) {
							char c = str.charAt(i);
							switch (c) {
							/*
							case '&': {
								next: {
									replaced: {
										if(str.length() < i + 1) {
											sb.append(c);
											break loop;
										}
										try {
											switch (str.charAt(i + 1)) {
											case 'a':
												if(str.charAt(i + 2) == 'm' && str.charAt(i + 3) == 'p' && str.charAt(i + 4) == ';') {
													i += 5;
													sb.append('&');
													break replaced;
												}
												break next;
											case 'l':
												if(str.charAt(i + 2) == 't' && str.charAt(i + 3) == ';') {
													i += 4;
													sb.append('<');
													break replaced;
												}
												break next;
											case 'g':
												if(str.charAt(i + 2) == 't' && str.charAt(i + 3) == ';') {
													i += 4;
													sb.append('>');
													break replaced;
												}
												break next;
											case 'q':
												if(str.charAt(i + 2) == 'u' && str.charAt(i + 3) == 'o' && str.charAt(i + 4) == 't' && str.charAt(i + 5) == ';') {
													i += 6;
													sb.append('\"');
													break replaced;
												}
												break next;
											default:
												break next;
											}
										} catch (Exception e) {
											break next;
										}
									}
									break;
								}
								sb.append(c);
								i++;
								break;
							}
							*/
							case '<' : {
								if(str.length() < i + 1) {
									sb.append(c);
									break loop;
								}
								try {
									if(str.charAt(i + 1) == 'b' && str.charAt(i + 2) == 'r' && str.charAt(i + 3) == '>') 
										break;
								} catch (Exception e) {
								}
								sb.append(c);
								i++;
								break;
							}
							case '\\': {
								next: {
									replaced: {
										if(str.length() < i + 1) {
											sb.append(c);
											break loop;
										}
										char c1 = str.charAt(i + 1);
										switch (c1) {
										case 'u':
											i++;
											String u = "" + str.charAt(i++) + str.charAt(i++) + str.charAt(i++) + str.charAt(i++);
											sb.append((char) Integer.parseInt(u, 16));
											break replaced;
										case 'x':
											i++;
											String x = "" + str.charAt(i++) + str.charAt(i++);
											sb.append((char) Integer.parseInt(x, 16));
											break replaced;
										case 'n':
											sb.append('\n');
											i+=2;
											break replaced;
										case 'r':
											sb.append('\r');
											i+=2;
											break replaced;
										case 't':
											sb.append('\t');
											i+=2;
											break replaced;
										case 'f':
											sb.append('\f');
											i+=2;
											break replaced;
										case 'b':
											sb.append('\b');
											i+=2;
											break replaced;
										case '\"':
										case '\'':
										case '\\':
										case '/':
											i+=2;
											sb.append((char) c1);
											break replaced;
										default:
											break next;
										}
									}
									break;
								}
								sb.append(c);
								i++;
								break;
							}
							default:
								sb.append(c);
								i++;
							}
						}
					}
					str = sb.toString();
				} catch (Exception e) {
				}

				return str;
			} else if (first != '{' && first != '[') {
				if (str.equals("null"))
					return null_equivalent;
				if (str.equals("true"))
					return Boolean.TRUE;
				if (str.equals("false"))
					return Boolean.FALSE;
				if(str.charAt(0) == '0' && str.charAt(1) == 'x') {
					try {
						return new Integer(Integer.parseInt(str.substring(2), 16));
					} catch (Exception e) {
						try {
							return new Long(Long.parseLong(str.substring(2), 16));
						} catch (Exception e2) {
							// Skip
						}
					}
				}
				try {
					return Integer.valueOf(str);
				} catch (Exception e) {
					try {
						return new Long(Long.parseLong(str));
					} catch (Exception e2) {
						try {
							return Double.valueOf(str);
						} catch (Exception e3) {
						}
					}
				}
				/*
				if(str.length() == 0 || str.equals("") || str.equals(" "))
					throw new JSONException("Empty value");
				throw new JSONException("Unknown value: " + str);
				*/
				return str;
			} else {
				// Parse json object or array
				int unclosed = 0;
				boolean object = first == '{';
				int i = 1;
				int length = str.length() - 1;
				char nextDelimiter = object ? ':' : ',';
				boolean escape = false;
				String key = null;
				Object res = null;
				if (object) res = new Hashtable();
				else res = new Vector();
				
				for (int splIndex; i < length; i = splIndex + 1) {
					// skip all spaces
					for (char ci; i < length - 1 && ((ci = str.charAt(i)) == ' ' || ci == '\n' || ci == '\r' || ci == '	'); i++);

					splIndex = i;
					boolean quotes = false;
					for (; splIndex < length && (quotes || unclosed > 0 || str.charAt(splIndex) != nextDelimiter); splIndex++) {
						char c = str.charAt(splIndex);
						if (!escape) {
							if (c == '\\') {
								escape = true;
							} else if (c == '"') {
								quotes = !quotes;
							}
						} else escape = false;
		
						if (!quotes) {
							if (c == '{' || c == '[') {
								unclosed++;
							} else if (c == '}' || c == ']') {
								unclosed--;
							}
						}
					}

					if (quotes || unclosed > 0) {
						throw new JSONException("Corrupted JSON");
					}

					if (object && key == null) {
						key = str.substring(i, splIndex);
						//while(n.startsWith("\r") || n.startsWith("\n")) {
						//	n = n.substring(1);
						//}
						//while(n.endsWith("\r") || n.endsWith("\n") || n.endsWith(" ")) {
						//	n = n.substring(0, n.length() - 1);
						//}
						key = key.substring(1, key.length() - 1);
						nextDelimiter = ',';
					} else {
						String s = str.substring(i, splIndex);
						while (s.endsWith("\r") || s.endsWith("\n")) {
							s = s.substring(0, s.length() - 1);
						}
						Object value = s.trim();
						if (parse_members) value = parseJSON(value.toString());
						if (object) {
							((Hashtable) res).put(key, value);
							key = null;
							nextDelimiter = ':';
						} else if (splIndex > i) ((Vector) res).addElement(value);
					}
				}
				return getJSON(res);
			}
		}
	}
	
	public static boolean isNull(Object obj) {
		return null_equivalent.equals(obj);
	}
	
	public static String escape_utf8(String s) {
		if(!build_functions) {
			return "";
		} else {
			int len = s.length();
			StringBuffer sb = new StringBuffer();
			int i = 0;
			while (i < len) {
				char c = s.charAt(i);
				switch(c) {
				case '"':
				case '\\':
					sb.append("\\" + c);
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				default:
					if(c < 32 || c > 1103) {
						String u = Integer.toHexString(c);
						for(int z = u.length(); z < 4; z++) {
							u = "0" + u;
						}
						sb.append("\\u" + u);
					} else {
						sb.append(c);
					}
				}
				i++;
			}
			return sb.toString();
		}
	}

	static class NullEquivalent {
		public boolean equals(Object obj) {
			return obj == null || obj instanceof NullEquivalent || super.equals(obj);
		}

		public String toString() {
			return "null";
		}
	}

	public static Double getDouble(Object o) throws JSONException {
		try {
			if (o instanceof Short)
				return new Double(((Short)o).shortValue());
			else if (o instanceof Integer)
				return new Double(((Integer)o).doubleValue());
			else if (o instanceof Long)
				return new Double(((Long)o).doubleValue());
			else if (o instanceof Double)
				return (Double) o;
			//else if (o instanceof Float)
			//	return new Double(((Float)o).doubleValue());
			else if (o instanceof String)
				return Double.valueOf((String) o);
		} catch (Throwable e) {
		}
		throw new JSONException("Value cast failed: " + o);
	}
/*
	public static Float getFloat(Object o) throws NNJSONException {
		try {
			if (o instanceof Short)
				return new Float(((Short)o).floatValue());
			else if (o instanceof Integer)
				return new Float(((Integer)o).floatValue());
			else if (o instanceof Long)
				return new Float(((Long)o).floatValue());
			else if (o instanceof Double)
				return new Float(((Double)o).floatValue());
			else if (o instanceof Float)
				return (Float) o;
			else if (o instanceof String)
				return Float.valueOf((String) o);
		} catch (Throwable e) {
		}
		throw new NNJSONException("Value cast failed: " + o);
	}
*/
}
