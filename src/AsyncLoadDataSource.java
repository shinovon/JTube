

import java.io.IOException;

import javax.microedition.media.Control;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

public class AsyncLoadDataSource extends DataSource {

	private SourceStream[] streams = new SourceStream[1];
	private String locator;
	private AsyncLoadStream stream;
	private String type;
	private int length;

	public AsyncLoadDataSource(String aLocator, String type, int length) throws IOException {
		super(aLocator);
		this.locator = aLocator;
		this.type = type;
		this.length = length;
		stream = new AsyncLoadStream(locator, type, length);
		streams[0] = stream;
	}

	public Control getControl(String controlType) {
		return null;
	}

	public Control[] getControls() {
		return new Control[0];
	}
	
	public void setListener(AsyncLoadListener l) {
		if(stream != null) stream.setListener(l);
	}

	public void connect() throws IOException {
		if(stream == null) {
			stream = new AsyncLoadStream(locator, type, length);
			streams[0] = stream;
		}
	}

	public void disconnect() {
		try {
			stream.deallocate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getContentType() {
		return type;
	}

	public SourceStream[] getStreams() {
		return streams;
	}

	public AsyncLoadStream getStream() {
		return stream;
	}
	
	public void start() throws IOException {
	}

	public void stop() throws IOException {
	}

}
