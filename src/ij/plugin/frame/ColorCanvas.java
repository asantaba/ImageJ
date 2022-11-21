package ij.plugin.frame;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import ij.IJ;
import ij.gui.ColorChooser;
import ij.gui.Toolbar;
import ij.plugin.Colors;

class ColorCanvas extends Canvas implements MouseListener, MouseMotionListener {
	private static Cursor defaultCursor;
	private static Cursor crosshairCursor;
	int ybase = ColorPicker.ybase;
	Rectangle flipperRect = new Rectangle(86, ybase+268, 18, 18);
	Rectangle resetRect = new Rectangle(84, ybase+293, 21, 18);
	Rectangle foreground1Rect = new Rectangle(9, ybase+266, 45, 10);
	Rectangle foreground2Rect = new Rectangle(9, ybase+276, 23, 25);
	Rectangle background1Rect = new Rectangle(33, ybase+302, 45, 10);
	Rectangle background2Rect = new Rectangle(56, ybase+277, 23, 25);
	int width, height;
	Vector colors;
	boolean background;
	long mouseDownTime;
	ColorGenerator ip;
	ColorPicker cp;
	double scale;
	String status = "";
			
	public ColorCanvas(int width, int height, ColorPicker cp, ColorGenerator ip, double scale, Cursor defaultCursor,
			Cursor crosshairCursor) {
		this.width=width; this.height=height;
		this.ip = ip;
		this.cp = cp;
		this.defaultCursor = defaultCursor;
		this.crosshairCursor = crosshairCursor;
		addMouseListener(this);
 		addMouseMotionListener(this);
        addKeyListener(IJ.getInstance());
		setSize(width, height);
		this.scale = scale;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public void paint(Graphics g) {
		g.drawImage(ip.createImage(), 0, 0, (int)(ip.getWidth()*scale), (int)(ip.getHeight()*scale), null);
	}

	public void mousePressed(MouseEvent e) {
		//IJ.log("mousePressed "+e);
		ip.setLineWidth(1);
		if (Toolbar.getToolId()==Toolbar.DROPPER)
			IJ.setTool(Toolbar.RECTANGLE );
		int x = (int)(e.getX()/scale);
		int y = (int)(e.getY()/scale);
		long difference = System.currentTimeMillis()-mouseDownTime;
		boolean doubleClick = (difference<=250);
		mouseDownTime = System.currentTimeMillis();
		if (flipperRect.contains(x, y)) {
			Color c = Toolbar.getBackgroundColor();
			Toolbar.setBackgroundColor(Toolbar.getForegroundColor());
			Toolbar.setForegroundColor(c);
			Recorder.setForegroundColor(Toolbar.getForegroundColor());
			Recorder.setBackgroundColor(Toolbar.getBackgroundColor());
		} else if (resetRect.contains(x,y)) {
			Toolbar.setForegroundColor(Color.white);
			Toolbar.setBackgroundColor(Color.black);
			Recorder.setForegroundColor(Color.white);
			Recorder.setBackgroundColor(Color.black);
		} else if ((background1Rect.contains(x,y)) || (background2Rect.contains(x,y))) {
			background = true;
			if (doubleClick) editColor();
			ip.refreshForeground(background);
			ip.refreshBackground(background);
		} else if ((foreground1Rect.contains(x,y)) || (foreground2Rect.contains(x,y))) {
			background = false;
			if (doubleClick) editColor();
			ip.refreshBackground(background);
			ip.refreshForeground(background);
		} else {
			if (doubleClick)
				editColor();
			else {
				setDrawingColor(x, y, background);
			showStatus(" ", Toolbar.getForegroundColor().getRGB());
			} 
		}
		Color color;
		if (background) {
			ip.refreshForeground(background);
			ip.refreshBackground(background);
			color= Toolbar.getBackgroundColor();
		} else {
			ip.refreshBackground(background);
			ip.refreshForeground(background);
			color= Toolbar.getForegroundColor();
		}
		cp.colorField.setText(Colors.colorToString(color));
		showStatus(" ", color.getRGB());
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		int x = (int)(e.getX()/scale);
		int y = (int)(e.getY()/scale);
		if (flipperRect.contains(x, y))
			showStatus("Click to flip foreground and background colors", 0);
		else if (resetRect.contains(x,y))
			showStatus("Click to reset foreground to white, background to black", 0);
		else if (!background && (background1Rect.contains(x,y) || background2Rect.contains(x,y)))
			showStatus("Click to switch to background selection mode ", 0);
		else if (background && (foreground1Rect.contains(x,y) || foreground2Rect.contains(x,y)))
			showStatus("Click to switch to foreground selection mode", 0);
		else
			showStatus("", ip.getPixel(x, y));
	}

	String pad(int n) {
		String str = ""+n;
		while (str.length()<3)
		str = "0" + str;
		return str;
	}	

	void setDrawingColor(int x, int y, boolean setBackground) {
		int p = ip.getPixel(x, y);
		int r = (p&0xff0000)>>16;
		int g = (p&0xff00)>>8;
		int b = p&0xff;
		Color c = new Color(r, g, b);
		if (setBackground) {
			Toolbar.setBackgroundColor(c);
			if (Recorder.record)
				Recorder.setBackgroundColor(c);
		} else {
			Toolbar.setForegroundColor(c);
			if (Recorder.record)
				Recorder.setForegroundColor(c);
		}
	}

	void editColor() {
		Color c  = background?Toolbar.getBackgroundColor():Toolbar.getForegroundColor();
		ColorChooser cc = new ColorChooser((background?"Background":"Foreground")+" Color", c, false);
		c = cc.getColor();
		if (background)
			Toolbar.setBackgroundColor(c);
		else
			Toolbar.setForegroundColor(c);
	}
	
	public void refreshColors() {
		ip.refreshBackground(false);
		ip.refreshForeground(false);
		repaint();
	}
	
	private void showStatus(String msg, int rgb) {
		if (msg.length()>1)
			IJ.showStatus(msg);
		else {
			int r = (rgb&0xff0000)>>16;
			int g = (rgb&0xff00)>>8;
			int b = rgb&0xff;
			String hex = Colors.colorToString(new Color(r,g,b));
			IJ.showStatus("red="+pad(r)+", green="+pad(g)+", blue="+pad(b)+" ("+hex+") "+msg);
		}
	}
	
	public void mouseExited(MouseEvent e) {
		IJ.showStatus("");
		setCursor(defaultCursor);
	}

	public void mouseEntered(MouseEvent e) {
		setCursor(crosshairCursor);
	}

	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	
}

