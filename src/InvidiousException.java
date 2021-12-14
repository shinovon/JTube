import cc.nnproject.json.JSONObject;

public class InvidiousException extends RuntimeException {

	private JSONObject json;
	private String url;
	private String msg2;

	public InvidiousException(JSONObject j) {
		super(j.getNullableString("error"));
		json = j;
	}

	public InvidiousException(JSONObject j, String msg, String url, String msg2) {
		super(msg);
		json = j;
		this.url = url;
		this.msg2 = msg2;
	}
	
	public JSONObject getJSON() {
		return json;
	}
	
	public String toString() {
		return "API error: " + getMessage();
	}
	
	public String toErrMsg() {
		boolean j = json != null;
		boolean bt = j && json.has("backtrace");
		boolean u = url != null;
		boolean m2 = msg2 != null;
		return  (j ? "Raw json: " + json.build() : "") + (u ? " \nAPI request: " + url : "") + (m2 ? " \n" + msg2 : "") + (bt ? " \nBacktrace: " + json.getString("backtrace") : "");
	}
	
	public String getUrl() {
		return url;
	}

}
