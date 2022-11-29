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
import java.util.Vector;

public class JSONArray extends AbstractJSON {

	private Vector vector;

	public JSONArray() {
		this.vector = new Vector();
	}

	public JSONArray(Vector vector) {
		this.vector = vector;
	}
	
	public Object get(int index) throws JSONException {
		try {
			if (JSON.parse_members)
				return vector.elementAt(index);
			else {
				Object o = vector.elementAt(index);
				if (o instanceof JSONString) {
					vector.setElementAt(o = JSON.parseJSON(o.toString()), index);
				}
				return o;
			}
		} catch (Exception e) {
		}
		throw new JSONException("No value at " + index);
	}
	
	public Object get(int index, Object def) {
		try {
			return get(index);
		} catch (Exception e) {
			return def;
		}
	}
	
	public String getString(int index) throws JSONException {
		return get(index).toString();
	}
	
	public String getString(int index, String def) {
		try {
			return get(index).toString();
		} catch (Exception e) {
			return def;
		}
	}
	
	public JSONObject getObject(int index) throws JSONException {
		try {
			return (JSONObject) get(index);
		} catch (ClassCastException e) {
			throw new JSONException("Not object at " + index);
		}
	}
	
	public JSONObject getNullableObject(int index) {
		try {
			return getObject(index);
		} catch (Exception e) {
			return null;
		}
	}
	
	public JSONArray getArray(int index) throws JSONException {
		try {
			return (JSONArray) get(index);
		} catch (ClassCastException e) {
			throw new JSONException("Not array at " + index);
		}
	}
	
	public JSONArray getNullableArray(int index) {
		try {
			return getArray(index);
		} catch (Exception e) {
			return null;
		}
	}
	
	public int getInt(int index) throws JSONException {
		return (int) JSON.getLong(get(index)).longValue();
	}
	
	public int getInt(int index, int def) {
		try {
			return getInt(index);
		} catch (Exception e) {
			return def;
		}
	}
	
	public long getLong(int index) throws JSONException {
		return JSON.getLong(get(index)).longValue();
	}

	public long getLong(int index, long def) {
		try {
			return getLong(index);
		} catch (Exception e) {
			return def;
		}
	}
	
	public double getDouble(int index) throws JSONException {
		return JSON.getDouble(get(index)).doubleValue();
	}

	public double getDouble(int index, double def) {
		try {
			return getDouble(index);
		} catch (Exception e) {
			return def;
		}
	}
	
	public boolean getBoolean(int index) throws JSONException {
		Object o = get(index);
		if(o == JSON.TRUE) return true;
		if(o == JSON.FALSE) return false;
		if(o instanceof Boolean) return ((Boolean) o).booleanValue();
		if(o instanceof String) {
			String s = (String) o;
			s = s.toLowerCase();
			if(s.equals("true")) return true;
			if(s.equals("false")) return false;
		}
		throw new JSONException("Not boolean: " + o + " (" + index + ")");
	}

	public boolean getBoolean(int index, boolean def) {
		try {
			return getBoolean(index);
		} catch (Exception e) {
			return def;
		}
	}
	
	public void put(String s) {
		vector.addElement("\"".concat(s).concat("\""));
	}
	
	public void put(Object obj) {
		vector.addElement(JSON.getJSON(obj));
	}
	
	public void clear() {
		vector.removeAllElements();
	}
	
	public int size() {
		return vector.size();
	}
	
	public String toString() {
		return "JSONArray " + vector.toString();
	}

	public String build() {
		if (size() == 0)
			return "[]";
		String s = "[";
		int i = 0;
		while(i < size()) {
			Object v = null;
			try {
				v = get(i);
			} catch (JSONException e) {
			}
			if (v instanceof JSONObject) {
				s += ((JSONObject) v).build();
			} else if (v instanceof JSONArray) {
				s += ((JSONArray) v).build();
			} else if (v instanceof String) {
				s += "\"" + JSON.escape_utf8(v.toString()) + "\"";
			} else s += v;
			i++;
			if (i < size()) s += ",";
		}
		s += "]";
		return s;
	}

	protected String format(int l) {
		if (size() == 0)
			return "[]";
		String t = "";
		String s = "";
		for (int i = 0; i < l; i++) {
			t += JSON.FORMAT_TAB;
		}
		String t2 = t + JSON.FORMAT_TAB;
		s += "[\n";
		s += t2;
		for (int i = 0; i < size(); ) {
			Object v = null;
			try {
				v = get(i);
			} catch (JSONException e) {
			}
			if (v instanceof JSONObject) {
				s += ((JSONObject) v).format(l + 1);
			} else if (v instanceof String) {
				s += "\"" + JSON.escape_utf8(v.toString()) + "\"";
			} else s += v;
			i++;
			if(i < size()) s += ",\n" + t2;
		}
		if (l > 0) {
			s += "\n" + t + "]";
		} else {
			s += "\n]";
		}
		return s;
	}

	public Enumeration elements() {
		return new Enumeration() {
			int i = 0;
			public boolean hasMoreElements() {
				return i < vector.size();
			}
			public Object nextElement() {
				try {
					return get(i++);
				} catch (Exception e) {
					return null;
				}
			}
		};
	}
	
	public void copyInto(Object[] arr, int offset, int length) {
		int i = offset;
		int j = 0;
		while(i < arr.length && j < length && j < size()) {
			Object o = get(j++);
			if(o == JSON.null_equivalent) o = null;
			arr[i++] = o;
		}
	}

}
