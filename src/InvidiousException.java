import cc.nnproject.json.JSONObject;

public class InvidiousException extends RuntimeException {

	private JSONObject json;

	public InvidiousException(JSONObject j) {
		super(j.getNullableString("error"));
		json = j;
	}

	public InvidiousException(JSONObject j, String msg) {
		super(msg);
		json = j;
	}
	
	public JSONObject getJSON() {
		return json;
	}
	
	public String toString() {
		return "API error: " + getMessage();
	}

}
