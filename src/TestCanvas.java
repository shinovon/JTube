import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 * канвас определяющий размер экрана
 */
public class TestCanvas extends Canvas {
	
	TestCanvas() {
		setFullScreenMode(true);
	}

	protected void paint(Graphics g) {
		g.drawString("Loading...", 0, 0, 0);
	}
	
	// для того чтобы симбиан поняла что виртуальные кнопки пихать не надо
	public void pointerDragged(int x, int y) {}
	public void pointerPressed(int x, int y) {}
	public void pointerReleased(int x, int y) {}

}
