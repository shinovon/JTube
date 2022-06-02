package ui.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import Util;
import ui.AppUI;
import ui.UIConstants;
import ui.UIItem;

public class LabelItem extends UIItem implements UIConstants {

	private String[] textArr;
	private int maxLines;
	private String text;
	private Font font;
	private int color;
	
	private int h;
	
	public LabelItem(String s) {
		this(s, mediumfont, AppUI.getColor(COLOR_MAINFOREGROUND));
	}
	
	public LabelItem(String s, Font f) {
		this(s, f, AppUI.getColor(COLOR_MAINFOREGROUND));
	}

	public LabelItem(String s, Font f, int c) {
		this.font = f;
		this.text = s;
		this.color = c;
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setFont(font);
		if(textArr == null) return;
		y+=2;
		g.setColor(color);
		for(int i = 0; i < textArr.length; i++) {
			g.drawString(textArr[i], x, y, 0);
			y+=2+font.getHeight();
		}
	}
	
	public void setMaxLines(int i) {
		this.maxLines = i;
	}
	
	public void setColor(int c) {
		this.color = c;
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = 4;
		String[] arr = Util.getStringArray(text, w - 10, font);
		textArr = new String[arr.length > maxLines && maxLines > 0 ? maxLines : arr.length];
		for(int i = 0; i < textArr.length; i++) {
			textArr[i] = arr[i];
			if(i == textArr.length-1 && arr.length > textArr.length) {
				textArr[i] = textArr[i].concat("...");
			}
		}
		h += textArr.length * (font.getHeight() + 2);
	}
	
	public void setFont(Font f) {
		this.font = f;
	}

}
