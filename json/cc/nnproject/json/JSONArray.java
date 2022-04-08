package cc.nnproject.json;

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
				if (o instanceof String)
					vector.setElementAt(o = JSON.parseJSON((String) o), index);
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
		if(o instanceof Boolean) return ((Boolean) o).booleanValue();
		if(o instanceof Integer) return ((Integer) o).intValue() > 0;
		if(o instanceof String) {
			String s = (String) o;
			if(s.equals("1") || s.equals("true") || s.equals("TRUE")) return true;
			else if(s.equals("0") || s.equals("false") || s.equals("FALSE") || s.equals("-1")) return false;
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
	
	public void clear() {
		vector.removeAllElements();
	}
	
	public int size() {
		return vector.size();
	}
	
	public String toString() {
		return "JSONArray " + vector.toString();
	}
}
