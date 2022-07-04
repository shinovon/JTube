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
	private int lineSpaces = 2;
	private int marginLeft;
	private int marginRight;
	private int marginTop = 2;
	private int marginBottom = 2;
	private boolean colorSet;
	
	public LabelItem(String s) {
		this(s, mediumfont);
	}
	
	public LabelItem(String s, Font f) {
		this.font = f;
		this.text = s;
	}

	public LabelItem(String s, Font f, int c) {
		this.font = f;
		this.text = s;
		this.color = c;
		this.colorSet = true;
	}

	public void paint(Graphics g, int w, int x, int y, int sc) {
		g.setFont(font);
		if(textArr == null) return;
		y+=marginTop;
		int fh = lineSpaces+font.getHeight();
		g.setColor(colorSet ? color : AppUI.getColor(COLOR_MAINFG));
		for(int i = 0; i < textArr.length; i++) {
			if(y + fh > 0 && y < ui.getHeight()) {
				g.drawString(textArr[i], x + marginLeft, y, 0);
			}
			y+=fh;
		}
	}
	
	public void setMaxLines(int i) {
		this.maxLines = i;
	}
	
	public void setColor(int c) {
		this.color = c;
		this.colorSet = true;
	}

	public int getHeight() {
		return h;
	}

	protected void layout(int w) {
		h = marginTop + marginBottom;
		String[] arr = Util.getStringArray(text, w - 10 - (marginLeft + marginRight), font);
		textArr = new String[arr.length > maxLines && maxLines > 0 ? maxLines : arr.length];
		for(int i = 0; i < textArr.length; i++) {
			textArr[i] = arr[i];
			if(i == textArr.length-1 && arr.length > textArr.length) {
				textArr[i] = textArr[i].trim().concat("...");
			}
		}
		h += textArr.length * (font.getHeight() + lineSpaces);
	}
	
	public void setFont(Font f) {
		this.font = f;
	}

	public void setLineSpaces(int i) {
		lineSpaces = i;
		relayout();
	}

	public void setMarginLeft(int i) {
		marginLeft = i;
		relayout();
	}

	public void setMarginRight(int i) {
		marginRight = i;
		relayout();
	}

	public void setMarginWidth(int i) {
		marginLeft = i;
		marginRight = i;
		relayout();
	}

	public void setMarginTop(int i) {
		marginTop = i;
		relayout();
	}

	public void setMarginBottom(int i) {
		marginBottom = i;
		relayout();
	}

	public void setMarginHeight(int i) {
		marginTop = i;
		marginBottom = i;
		relayout();
	}

}
