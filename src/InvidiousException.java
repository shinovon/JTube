import cc.nnproject.json.JSONObject;

public class InvidiousException extends RuntimeException {

	private JSONObject json;
	private String msg;

	public InvidiousException(JSONObject j) {
		json = j;
		msg = j.getNullableString("error");
	}
	
	public JSONObject getJSON() {
		return json;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public String toString() {
		return "API error: " + msg;
	}

}
