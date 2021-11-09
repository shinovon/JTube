package cc.nnproject.json;

public class JSONException extends RuntimeException {
	
	JSONException() {
	}
	
	JSONException(String string) {
		super(string);
	}
	
	public String toString() {
		return getMessage() == null ? "JSONException" : "JSONException: " + getMessage();
	}

}
