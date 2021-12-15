import java.io.IOException;

public class NetRequestException extends IOException {
	
	private String url;
	private IOException cause;

	public NetRequestException(IOException e, String url) {
		super(url);
		cause = e;
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public IOException getTheCause() {
		return cause;
	}

}
