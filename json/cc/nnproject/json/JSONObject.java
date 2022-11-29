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
package cc.nnproject.json;

import java.util.Enumeration;
import java.util.Hashtable;

public class JSONObject extends AbstractJSON {

	private Hashtable table;

	public JSONObject() {
		this.table = new Hashtable();
	}

	public JSONObject(Hashtable table) {
		this.table = table;
	}
	
	public boolean has(String name) {
		return table.containsKey(name);
	}
	
	public Object get(String name) throws JSONException {
		try {
			if (has(name)) {
				if (JSON.parse_members) {
					return table.get(name);
				} else {
					Object o = table.get(name);
					if (o instanceof JSONString)
						table.put(name, o = JSON.parseJSON(o.toString()));
					return o;
				}
			}
		} catch (JSONException e) {
			throw e;
		} catch (Exception e) {
		}
		throw new JSONException("No value for name: " + name);
	}
	
	public Object get(String name, Object def) {
		if(!has(name)) return def;
		try {
			return get(name);
		} catch (Exception e) {
			return def;
		}
	}
	
	public Object getNullable(String name) {
		return get(name, null);
	}
	
	public String getString(String name) throws JSONException {
		return get(name).toString();
	}
	
	public String getString(String name, String def) {
		try {
			Object o = get(name, def);
			if(o == null || o instanceof String) {
				return (String) o;
			}
			return o.toString();
		} catch (Exception e) {
			return def;
		}
	}
	
	public String getNullableString(String name) {
		return getString(name, null);
	}
	
	public JSONObject getObject(String name) throws JSONException {
		try {
			return (JSONObject) get(name);
		} catch (ClassCastException e) {
			throw new JSONException("Not object: " + name);
		}
	}
	
	public JSONObject getNullableObject(String name) {
		if(!has(name)) return null;
		try {
			return getObject(name);
		} catch (Exception e) {
			return null;
		}
	}
	
	public JSONArray getArray(String name) throws JSONException {
		try {
			return (JSONArray) get(name);
		} catch (ClassCastException e) {
			throw new JSONException("Not array: " + name);
		}
	}
	
	public JSONArray getNullableArray(String name) {
		if(!has(name)) return null;
		try {
			return getArray(name);
		} catch (Exception e) {
			return null;
		}
	}
	
	public int getInt(String name) throws JSONException {
		return (int) JSON.getLong(get(name)).longValue();
	}
	
	public int getInt(String name, int def) {
		if(!has(name)) return def;
		try {
			return getInt(name);
		} catch (Exception e) {
			return def;
		}
	}
	
	public long getLong(String name) throws JSONException {
		return JSON.getLong(get(name)).longValue();
	}

	public long getLong(String name, long def) {
		if(!has(name)) return def;
		try {
			return getLong(name);
		} catch (Exception e) {
			return def;
		}
	}
	
	public double getDouble(String name) throws JSONException {
		return JSON.getDouble(get(name)).doubleValue();
	}

	public double getDouble(String name, double def) {
		if(!has(name)) return def;
		try {
			return getDouble(name);
		} catch (Exception e) {
			return def;
		}
	}
	
	public boolean getBoolean(String name) throws JSONException {
		Object o = get(name);
		if(o == JSON.TRUE) return true;
		if(o == JSON.FALSE) return false;
		if(o instanceof Boolean) return ((Boolean) o).booleanValue();
		if(o instanceof String) {
			String s = (String) o;
			s = s.toLowerCase();
			if(s.equals("true")) return true;
			if(s.equals("false")) return false;
		}
		throw new JSONException("Not boolean: " + o);
	}

	public boolean getBoolean(String name, boolean def) {
		if(!has(name)) return def;
		try {
			return getBoolean(name);
		} catch (Exception e) {
			return def;
		}
	}
	
	public boolean isNull(String name) throws JSONException {
		return JSON.isNull(get(name));
	}

	public void put(String name, String s) {
		table.put(name, "\"".concat(s).concat("\""));
	}
	
	public void put(String name, Object obj) {
		table.put(name, JSON.getJSON(obj));
	}
	
	public void clear() {
		table.clear();
	}
	
	public int size() {
		return table.size();
	}
	
	public String toString() {
		return "JSONObject " + table.toString();
	}

	public String build() {
		int l = size();
		if (l == 0)
			return "{}";
		String s = "{";
		java.util.Enumeration elements = table.keys();
		while (true) {
			String k = elements.nextElement().toString();
			s += "\"" + k + "\":";
			Object v = null;
			try {
				v = table.get(k);
				if(v instanceof String) {
					v = JSON.parseJSON((String) v);
				}
			} catch (JSONException e) {
			}
			if (v instanceof JSONObject) {
				s += ((JSONObject) v).build();
			} else if (v instanceof JSONArray) {
				s += ((JSONArray) v).build();
			} else if (v instanceof String) {
				s += "\"" + JSON.escape_utf8((String) v) + "\"";
			} else s += v.toString();
			if(!elements.hasMoreElements()) {
				return s + "}";
			}
			s += ",";
		}
	}

	protected String format(int l) {
		if (size() == 0)
			return "{}";
		String t = "";
		String s = "";
		for (int i = 0; i < l; i++) {
			t += JSON.FORMAT_TAB;
		}
		String t2 = t + JSON.FORMAT_TAB;
		s += "{\n";
		s += t2;
		Enumeration elements = table.keys();
		for (int i = 0; elements.hasMoreElements(); ) {
			String k = elements.nextElement().toString();
			s += "\"" + k + "\": ";
			Object v = null;
			try {
				v = get(k);
			} catch (JSONException e) {
			}
			if (v instanceof AbstractJSON) {
				s += ((AbstractJSON) v).format(l + 1);
			} else if (v instanceof String) {
				s += "\"" + JSON.escape_utf8(v.toString()) + "\"";
			} else s += v;
			i++;
			if(i < size()) s += ",\n" + t2;
		}
		if (l > 0) {
			s += "\n" + t + "}";
		} else {
			s += "\n}";
		}
		return s;
	}

	public Enumeration keys() {
		return table.keys();
	}

}
