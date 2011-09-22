package edu.berkeley.gcweb.gui.gamescubeman.Cuboid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JApplet;

@SuppressWarnings("serial")
public class Boarding extends JApplet implements MouseListener {
	private class Center {
		private int x, y;
		private Color c;
		public Center(int x, int y, Color c) {
			this.x = x;
			this.y = y;
			this.c = c;
		}
	}
	
	private Image offscreen;
	private Graphics bufferGraphics;
	@Override
	public void init() {
		Dimension dim = getSize();
		offscreen = createImage(dim.width, dim.height);
		bufferGraphics = offscreen.getGraphics();
		addMouseListener(this);
	}
	
	private ArrayList<Center> centers = new ArrayList<Center>(Arrays.asList(
			new Center(0, 0, Color.RED),
			new Center(100, 100, Color.CYAN),
			new Center(200, 200, Color.ORANGE),
			new Center(300, 300, Color.GREEN),
			new Center(200, 100, Color.MAGENTA),
			new Center(300, 150, Color.YELLOW),
			new Center(150, 300, Color.BLACK)
	));

	private int square(int a) {
		return a*a;
	}
	
	@Override
	public void paint(Graphics g) {
		for(int x = 0; x < getWidth(); x++) {
			for(int y = 0; y < getHeight(); y++) {
				Center minCenter = getNearestCenter(x, y);
				g.setColor(minCenter.c);
				g.drawRect(x, y, 1, 1);
//				bufferGraphics.setColor(minCenter.c);
//				bufferGraphics.drawRect(x, y, 1, 1);
			}
		}
		
//		// draw the offscreen image to the screen like a normal image.
//		// Since offscreen is the screen width we start at 0,0.
//		g.drawImage(offscreen, 0, 0, this);
	}

	public void update(Graphics g) {
		paint(g);
	}

	private Center getNearestCenter(int x, int y) {
		Center minCenter = null;
		int minDist = Integer.MAX_VALUE;
		for(Center c : centers) {
			int new_dist = square(x-c.x)+square(y-c.y);
			if(new_dist < minDist) {
				minCenter = c;
				minDist = new_dist;
			}
		}
		return minCenter;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		Center closest = getNearestCenter(p.x, p.y);
		closest.c = closest.c.darker();
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
}
