package cc.nnproject.json;

import java.util.Enumeration;
import java.util.Hashtable;

public class JSONObject {

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
			if (table.containsKey(name)) {
				if (JSON.parse_members) {
					return table.get(name);
				} else {
					Object o = table.get(name);
					if (o instanceof String)
						table.put(name, o = JSON.parseJSON((String) o));
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
			return get(name).toString();
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
		try {
			return getArray(name);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Double getNumber(String name) throws JSONException {
		return JSON.getDouble(get(name));
	}
	
	public int getInt(String name) throws JSONException {
		return getNumber(name).intValue();
	}
	
	public int getInt(String name, int def) {
		try {
			return getInt(name);
		} catch (Exception e) {
			return def;
		}
	}
	
	public long getLong(String name) throws JSONException {
		return getNumber(name).longValue();
	}

	public long getLong(String name, long def) {
		try {
			return getLong(name);
		} catch (Exception e) {
			return def;
		}
	}
	
	public double getDouble(String name) throws JSONException {
		return getNumber(name).doubleValue();
	}

	public double getDouble(String name, double def) {
		try {
			return getDouble(name);
		} catch (Exception e) {
			return def;
		}
	}
	
	public boolean getBoolean(String name) throws JSONException {
		Object o = get(name);
		if(o instanceof Boolean) return ((Boolean) o).booleanValue();
		if(o instanceof Integer) return ((Integer) o).intValue() > 0;
		if(o instanceof String) {
			String s = (String) o;
			if(s.equals("1") || s.equals("true") || s.equals("TRUE")) return true;
			else if(s.equals("0") || s.equals("false") || s.equals("FALSE") || s.equals("-1")) return false;
		}
		throw new JSONException("Not boolean: " + o);
	}

	public boolean getBoolean(String name, boolean def) {
		try {
			return getBoolean(name);
		} catch (Exception e) {
			return def;
		}
	}
	
	public boolean isNull(String name) throws JSONException {
		return JSON.isNull(get(name));
	}
	
	public void clear() {
		table.clear();
	}
	
	public int size() {
		return table.size();
	}
	
	public String toString() {
		if(JSON.build_functions) return build();
		else return "JSONObject " + table.toString();
	}
	
	public String build() {
		if(!JSON.build_functions) return "";
		else {
			if (size() == 0)
				return "{}";
			String s = "{";
			java.util.Enumeration elements = table.keys();
			int i = 0;
			while(elements.hasMoreElements()) {
				String k = elements.nextElement().toString();
				s += "\"" + k + "\":";
				Object v = null;
				try {
					v = get(k);
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
			s += "}";
			return s;
		}
	}
	
	public String format() {
		if(!JSON.build_functions) return "";
		else return format(0);
	}
	
	String format(int l) {
		if(!JSON.build_functions) return "";
		else {
			if (size() == 0)
				return "{}";
			String t = "";
			String s = "";
			for (int i = 0; i < l; i++) {
				t += JSON.format_space;
			}
			String t2 = t + JSON.format_space;
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
				if (v instanceof JSONObject) {
					s += ((JSONObject) v).format(l + 1);
				} else if (v instanceof JSONArray) {
					s += ((JSONArray) v).format(l + 1);
				} else if (v instanceof String) {
					s += "\"" + JSON.escape_utf8(v.toString()) + "\"";
				} else s += v;
				i++;
				if(i < size()) s += ",\n" + t2;
			}
			if (l > 0) {
				s += "\n" + t + "}";
			} else s += "\n}";
			return s;
		}
	}

}
