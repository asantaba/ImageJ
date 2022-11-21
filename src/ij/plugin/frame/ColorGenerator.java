package ij.plugin.frame;
import java.awt.Color;

import ij.gui.Toolbar;
import ij.process.ColorProcessor;

class ColorGenerator extends ColorProcessor {
	private int ybase = ColorPicker.ybase;
    private int w, h;
    private int[] colors = {0xff0000, 0x00ff00, 0x0000ff, 0xffffff, 0x00ffff, 0xff00ff, 0xffff00, 0x000000};

    public ColorGenerator(int width, int height, int[] pixels) {
        super(width, height, pixels);
        setAntialiasedText(true);
    }
    
    void drawColors(int colorWidth, int colorHeight, int columns, int rows) {
        w = colorWidth;
        h = colorHeight;
        setColor(0xffffff);
        setRoi(0, ybase, 110, 320);
        fill();
        drawRamp();
        resetBW();
        flipper();
        //drawLine(0, 256, 110, 256);
        
        refreshBackground(false);
        refreshForeground(false);

        Color c;
        float hue, saturation=1f, brightness=1f;
        double w=colorWidth, h=colorHeight;
        for (int x=2; x<10; x++) {
            for (int y=0; y<32; y++) {
                hue = (float)(y/(2*h)-.15);
                if (x<6) { 
                    saturation = 1f;
                    brightness = (float)(x*4/w);
                } else {
                    saturation = 1f - ((float)((5-x)*-4/w));
                    brightness = 1f;
                }
                c = Color.getHSBColor(hue, saturation, brightness);
                setRoi(x*(int)(w/2), ybase+y*(int)(h/2), (int)w/2, (int)h/2);
                setColor(c);
                fill();
            }
        }
        drawSpectrum(h);        
        resetRoi();
    }
       
    void drawColor(int x, int y, Color c) {
        setRoi(x*w, y*h, w, h);
        setColor(c);
        fill();
    }

    public void refreshBackground(boolean backgroundInFront) {
        //Boundary for Background Selection
        setColor(0x444444);
        drawRect((w*2)-12, ybase+276, (w*2)+4, (h*2)+4);
        setColor(0x999999);
        drawRect((w*2)-11, ybase+277, (w*2)+2, (h*2)+2);
        setRoi((w*2)-10, ybase+278, w*2, h*2);//Paints the Background Color
        Color bg = Toolbar.getBackgroundColor();
        setColor(bg);
        fill();
        if (backgroundInFront)
        	drawLabel("B", bg, w*4-18, ybase+278+h*2);
    }

    public void refreshForeground(boolean backgroundInFront) {
        //Boundary for Foreground Selection
        setColor(0x444444);
        drawRect(8, ybase+266, (w*2)+4, (h*2)+4);
        setColor(0x999999);
        drawRect(9, ybase+267, (w*2)+2, (h*2)+2);
        setRoi(10, ybase+268, w*2, h*2); //Paints the Foreground Color
        Color fg = Toolbar.getForegroundColor();
        setColor(fg);
        fill();
        if (backgroundInFront)
        	drawLabel("F", fg, 12, ybase+268+14);
    }
    
    private void drawLabel(String label, Color c, int x, int y) {
		int intensity = (c.getRed()+c.getGreen()+c.getBlue())/3;
		c = intensity<128?Color.white:Color.black;
		setColor(c);
		drawString(label, x, y);
	}

	void drawSpectrum(double h) {
		Color c;
		for ( int x=5; x<7; x++) {
			for ( int y=0; y<32; y++) {
				float hue = (float)(y/(2*h)-.15);        
				c = Color.getHSBColor(hue, 1f, 1f);
				setRoi(x*(int)(w/2), ybase+y*(int)(h/2), (int)w/2, (int)h/2);
				setColor(c);
				fill();
			}
		}
		setRoi(55, ybase+32, 22, 16); //Solid red
		setColor(0xff0000);
		fill();
		setRoi(55, ybase+120, 22, 16); //Solid green
		setColor(0x00ff00);
		fill();
		setRoi(55, ybase+208, 22, 16); //Solid blue
		setColor(0x0000ff);
		fill();
		setRoi(55, ybase+80, 22, 8); //Solid yellow
		setColor(0xffff00);
		fill();
		setRoi(55, ybase+168, 22, 8); //Solid cyan
		setColor(0x00ffff);
		fill();
		setRoi(55, ybase+248, 22, 8); //Solid magenta
		setColor(0xff00ff);
		fill();
	}

    void drawRamp() {
        int r,g,b;
        for (int x=0; x<w; x++) {
             for (int y=0; y<(h*16); y++) {
                r = g = b = (byte)y;
                set(x, ybase+y, 0xff000000 | ((r<<16)&0xff0000) | ((g<<8)&0xff00) | (b&0xff));
            }
        }
    }

    void resetBW() {   //Paints the Color Reset Button
        setColor(0x000000);
        setRoi(92, ybase+300, 9, 7);
        fill();
        drawRect(88, ybase+297, 9, 7);
        setColor(0xffffff);
        setRoi(89, ybase+298, 7, 5);
        fill();
    }

    void flipper() {   //Paints the Flipper Button
        int xa = 90; 
        int ya = ybase+272; 
        setColor(0x000000);
        drawLine(xa, ya, xa+9, ya+9);//Main Body
        drawLine(xa+1, ya, xa+9, ya+8);
        drawLine(xa, ya+1, xa+8, ya+9);
        drawLine(xa, ya, xa, ya+5);//Upper Arrow
        drawLine(xa+1, ya+1, xa+1, ya+6);
        drawLine(xa, ya, xa+5, ya);
        drawLine(xa+1, ya+1, xa+6, ya+1);
        drawLine(xa+9, ya+9, xa+9, ya+4);//Lower Arrow
        drawLine(xa+8, ya+8, xa+8, ya+3);
        drawLine(xa+9, ya+9, xa+4, ya+9);
        drawLine(xa+8, ya+8, xa+3, ya+8);
    }
    
} 