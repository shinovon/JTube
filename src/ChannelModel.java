import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import cc.nnproject.json.JSONObject;

// TODO
public class ChannelModel {

	public ChannelModel(JSONObject o) {
	}

	public Item makeItemForList() {
		return new StringItem("Channel", "");
	}

}
