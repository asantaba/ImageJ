package ij.plugin.frame;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;

import javax.swing.BoxLayout;

import ij.IJ;
import ij.IJEventListener;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GUI;
import ij.gui.Toolbar;
import ij.plugin.Colors;

/** Implements the Image/Color/Color Picker command. */
public class ColorPicker extends PlugInDialog implements ColorI {
	private int colorWidth = 22;
	private int colorHeight = 16;
	private int columns = 5;
	private int rows = 20;
	private static final String LOC_KEY = "cp.loc";
	private static ColorPicker instance;
	private ColorGenerator cg; 
	private Canvas colorCanvas;
	TextField colorField;
	private static Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	private static Cursor crosshairCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	
    public ColorPicker() {
		super("CP");
		if (instance!=null) {
			instance.toFront();
			return;
		}
		double scale = Prefs.getGuiScale();
		instance = this;
		WindowManager.addWindow(this);
        int width = (int)(columns*colorWidth*scale);
        int height = (int)((rows*colorHeight+ybase)*scale);
        addKeyListener(IJ.getInstance());
		setLayout(new BorderLayout());
		cg = new ColorGenerator(width, height, new int[width*height]);
        cg.drawColors(colorWidth, colorHeight, columns, rows);
        colorCanvas = new ColorCanvas(width, height, this, cg, scale, defaultCursor, crosshairCursor);
        Panel panel = new Panel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(colorCanvas);
		String hexColor = Colors.colorToString(Toolbar.getForegroundColor());
        colorField = new TextField(hexColor+" ",7);
        colorField.setEditable(false);
        colorField.select(hexColor.length(),hexColor.length());
        GUI.scale(colorField);
        panel.add(colorField);
        add(panel);
		setResizable(false);
		pack();
		Point loc = Prefs.getLocation(LOC_KEY);
		if (loc!=null)
			setLocation(loc);
		else
			GUI.centerOnImageJScreen(this);
		show();
	}
    
    public void close() {
	 	super.close();
		instance = null;
		Prefs.saveLocation(LOC_KEY, getLocation());
		IJ.notifyEventListeners(IJEventListener.COLOR_PICKER_CLOSED);
	}
	
	public static void update() {
		ColorPicker cp = instance;
		if (cp!=null && cp.colorCanvas!=null) {
			cp.cg.refreshBackground(false);
			cp.cg.refreshForeground(false);
			cp.colorCanvas.repaint();
			cp.colorField.setText(Colors.colorToString(Toolbar.getForegroundColor()));
		}
	}
	
}

