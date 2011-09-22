package edu.berkeley.gcweb.gui.gamescubeman.ThreeD;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Canvas3D extends JComponent implements KeyListener, ActionListener, MouseListener, MouseMotionListener, FocusListener {
	public static final double DEFAULT_SCALE = 600;
	private static final double VIEWPORT = 1;
	private final int DEFAULT_HEIGHT = 500;
	private final int DEFAULT_WIDTH = 400;
	private double scale = DEFAULT_SCALE;
	private boolean focusIndicator = true;
	private boolean drawAxis = false;
	private Timer t;
	public Canvas3D() {
		setFocusable(true);
		setOpaque(true);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addFocusListener(this);
		t = new Timer(10, this);
		t.start();
	}
	
	public void setDrawAxis(boolean drawAxis) {
		this.drawAxis = drawAxis;
	}
	public void setFocusIndicator(boolean focusIndicator) {
		this.focusIndicator = focusIndicator;
	}
	private boolean lightBorders;
	//this will draw borders lightly, but will disable the focus indicator
	public void setLightBorders(boolean lightBorders) {
		this.lightBorders = lightBorders;
	}
	
	public double getScale() {
		return scale;
	}
	public void setScale(double scale) {
		this.scale = scale;
		fireCanvasChange();
	}
	
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		fireCanvasChange();
	}
	
	public void focusGained(FocusEvent e) {
		fireCanvasChange();
	}
	public void focusLost(FocusEvent e) {
		fireCanvasChange();
	}
	public void actionPerformed(ActionEvent e) {
		int x = 0, y = 0;
		if(keys.contains(KeyEvent.VK_LEFT))
			y--;
		if(keys.contains(KeyEvent.VK_RIGHT))
			y++;
		if(keys.contains(KeyEvent.VK_UP))
			x++;
		if(keys.contains(KeyEvent.VK_DOWN))
			x--;
		if(dirty || !rotationRate.isIdentity() || x != 0 || y != 0) {
			if(!dragging) {
				rotationRate = new RotationMatrix(1, deltaX).multiply(new RotationMatrix(0, -deltaY));
				deltaX /= 1.01;
				deltaY /= 1.01;
				if(rotationRate.isIdentity(0.05)) {
					rotationRate = new RotationMatrix();
				}
				RotationMatrix temp = rotationRate.multiply(new RotationMatrix(0, x).multiply(new RotationMatrix(1, y)));
				for(Shape3D s : shapes)
					s.rotate(temp);
			}
			for(Shape3D s : shapes) {
				//this is the old, baaad way of doing z ordering
//				polys = s.getPolygons();
//				Collections.sort(polys);
				
				long start = System.nanoTime();
				//implementing painter's algorithm
				ArrayList<Polygon3D> oldPolys = s.getPolygons();
				int polyCount = oldPolys.size();
				//x_covered_by_y[i][j] = true when j covers i, false when i covers j, null when i and or j have been removed
				Boolean[][] x_covered_by_y = new Boolean[polyCount][polyCount];
				for(int i = 0; i < polyCount; i++) {
					for(int j = i; j < polyCount; j++) {
						Boolean i_covers_j = oldPolys.get(i).covers(oldPolys.get(j));
						Boolean j_covers_i;
						if(i_covers_j == null) {
							j_covers_i = i_covers_j = false;
						} else {
							j_covers_i = !i_covers_j;
						}
						x_covered_by_y[i][j] = i_covers_j;
						x_covered_by_y[j][i] = j_covers_i;
					}
				}
				
				polys = new ArrayList<Polygon3D>();
				while(polys.size() < polyCount) {
					int j;
					for(j = 0; j < polyCount; j++) {
						if(x_covered_by_y[j][j] == null) continue;
						boolean bottom = true;
						for(int i = 0; i < polyCount; i++) {
							if(x_covered_by_y[i][j] != null) {
								if(x_covered_by_y[i][j]) {
									bottom = false;
									break;
								}
							}
						}
						if(bottom)
							break;
					}
					if(j == polyCount) {
						//cycle detected in the polygon ordering, just choose one at random =(
						for(j = 0; j < polyCount; j++)
							if(x_covered_by_y[j][j] != null)
								break;
						System.out.println("Cycle detected, choosing index " + j);
					}
					polys.add(oldPolys.get(j));

					for(int i = 0; i < polyCount; i++) {
						x_covered_by_y[i][j] = null;
						x_covered_by_y[j][i] = null;
					}
				}
				
				polyProjection = new ArrayList<Shape>();
				for(Polygon3D poly : polys) {
					Shape proj = poly.projectXYPlane(VIEWPORT, scale * Math.min((double)getWidth() / DEFAULT_WIDTH, (double)getHeight() / DEFAULT_HEIGHT));
					polyProjection.add(proj);
				}
			}
			if(!dragging || colorEditing)
				refreshSelectedPolygon();
			repaint();
			dirty = false;
		}
	}
	
	private boolean dirty = false;
	public void fireCanvasChange() {
		dirty = true;
	}

	private RotationMatrix rotationRate = new RotationMatrix();
	private Point old;
	private long lastDrag;
	private HashSet<Integer> keys = new HashSet<Integer>();
	public void keyPressed(KeyEvent e) {
		keys.add(e.getKeyCode());
	}
	public void keyReleased(KeyEvent e) {
		keys.remove(e.getKeyCode());
	}
	public void keyTyped(KeyEvent e) {}
	public void mouseClicked(MouseEvent e) {
		requestFocusInWindow();
		if(colorEditing) {
			Polygon3D selected = getSelectedPolygon();
			if(selected != null) {
				firePolyClicked(selected.getOGPoly());
				dirty = true;
			}
		}
	}
	
	public interface PolyClickListener {
		public void polyClicked(Polygon3D clicked);
	}
	private ArrayList<PolyClickListener> polyListeners = new ArrayList<PolyClickListener>();
	public void addPolyClickListener(PolyClickListener l) {
		polyListeners.add(l);
	}
	private void firePolyClicked(Polygon3D selected) {
		for(PolyClickListener l : polyListeners)
			l.polyClicked(selected);
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		rotationRate = new RotationMatrix();
		deltaX = deltaY = 0;
		dirty = true;
	}
	public void mouseReleased(MouseEvent e) {
		if(!freeRotationSpin || System.currentTimeMillis() - lastDrag > 100)
			mousePressed(null);
		dragging = false;
	}
	
	private boolean freeRotation=true;
	public void setFreeRotation(boolean enabled) {
		freeRotation = enabled;
	}
	private boolean freeRotationSpin=true;
	public void setFreeRotationSpin(boolean enabled) {
		freeRotationSpin = enabled;
	}
	private double deltaX = 0, deltaY = 0;
	private boolean dragging = false;
	public void mouseDragged(MouseEvent e) {
		if(!freeRotation)
			return;
		dragging = true;
		lastDrag = System.currentTimeMillis();
		deltaX = e.getX() - old.x;
		deltaY = e.getY() - old.y;
		rotationRate = new RotationMatrix(1, deltaX).multiply(new RotationMatrix(0, -deltaY));
		old = e.getPoint();

		for(Shape3D s : shapes)
			s.rotate(rotationRate);
		dirty = true;
	}
	public void mouseMoved(MouseEvent e) {
		old = e.getPoint();
		if(colorEditing)
			refreshSelectedPolygon();
	}
	private boolean colorEditing = false;
	public void setColorEditing(boolean editing) {
		this.colorEditing = editing;
		dirty = true;
	}
	private void refreshSelectedPolygon() {
		if(!colorEditing) {
			for(Polygon3D rendered : polys) {
				rendered.setOpacity(1f);
			}
		} else {
			for(Polygon3D rendered : polys) {
				rendered.setOpacity(.8f);
				rendered.setBorderColor(null);
			}
			Polygon3D poly = getSelectedPolygon();
			if(poly != null) {
				poly.setOpacity(1f);
				poly.setBorderColor(poly.getOGPoly().getBorderColor());
			}
		}
		dirty = true;
	}
	private Polygon3D getSelectedPolygon() {
		Point p = getMousePosition();
		if(p == null)	return null;
		double x = -(p.x - getWidth() / 2.);
		double y = -(p.y - getHeight() / 2.);
		int match = -1;
		for(int i = 0; i < polyProjection.size(); i++)
			if(polyProjection.get(i) != null && polyProjection.get(i).contains(x, y))
				match = i;
		if(match == -1)
			return null;
		return polys.get(match);
	}

	//TODO - who are we kidding? this was coded w/ exactly one shape in mind
	//it'll take a bit of work to get this to work for n shape3ds
	private ArrayList<Shape3D> shapes = new ArrayList<Shape3D>();
	public void addShape3D(Shape3D s) {
		s.setCanvas(this);
		shapes.add(s);
	}
	
	public void clearShape3D() {
		shapes.clear();
	}

	private boolean antialiasing = true;
	public void setAntialiasing(boolean aa) {
		 antialiasing = aa;
		 dirty = true;
	}
	public boolean isAntialiasing() {
		return antialiasing;
	}
	
	private ArrayList<Polygon3D> polys;
	private ArrayList<Shape> polyProjection;
	protected void paintComponent(Graphics g) {
		if(isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		Graphics2D g2d = (Graphics2D) g;
		if(!isFocusOwner() && focusIndicator || lightBorders) {
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
			g2d.setComposite(ac);
		}
		
		Stroke oldStroke = g2d.getStroke();
		RenderingHints oldHints = g2d.getRenderingHints();
		if(antialiasing) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		}
		
		AffineTransform toCartesian = new AffineTransform();
		toCartesian.translate(getWidth() / 2, getHeight() / 2);
		toCartesian.rotate(Math.toRadians(180));
		g2d.transform(toCartesian);

		if(drawAxis) {
			g2d.setColor(Color.BLUE); //draw the axis
			g2d.drawLine(0, -getHeight() / 2, 0, getHeight() / 2);
			g2d.drawLine(-getWidth() / 2, 0, getWidth() / 2, 0);
		}
		
		g2d.setColor(Color.BLACK);
		
		for(int i = 0; polys != null && i < polys.size(); i++) {
			Polygon3D poly = polys.get(i);
			Shape proj = polyProjection.get(i);
			if(proj == null) continue; 
			if(poly.getFillColor() != null) {
				g2d.setColor(poly.getFillColor());
				Composite oldComposite = g2d.getComposite();
				g2d.setComposite(poly.getOpacity());
				g2d.fill(proj);
				g2d.setComposite(oldComposite);
			}
			if(poly.getBorderColor() != null) {
				g2d.setColor(poly.getBorderColor());
				g2d.draw(proj);
			}
		}
		try {
			g2d.transform(toCartesian.createInverse());
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		if(display != null) {
			g2d.setColor(textColor);
			g2d.drawString(display, 20, 20);
		}
		g2d.setRenderingHints(oldHints);
		g2d.setStroke(oldStroke);
	}
	//TODO - this is a fairly nasty hack
	private String display;
	private Color textColor;
	public void setDisplayString(Color c, String display) {
		this.display = display;
		textColor = c;
	}
}
