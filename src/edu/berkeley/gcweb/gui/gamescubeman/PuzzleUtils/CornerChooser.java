package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.Timer;

import edu.berkeley.gcweb.gui.gamescubeman.Cuboid.Cuboid;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Canvas3D;

public class CornerChooser extends RollingJPanel implements MouseListener, MouseMotionListener, ComponentListener, KeyListener, ActionListener {
	private static final int PREFERRED_HEIGHT = 50;
	private static final int STICKER_LENGTH = (int) (.25* PREFERRED_HEIGHT);
	private HashMap<String, Color> colors;
	private Canvas3D paintCanvas;
	private HashMap<GeneralPath, String> StickerColor;
	private String selectedCorner = null;
	private HashMap<String, Rectangle2D> colorRectangles;
	private HashMap<String,String> cornermap;
	private Cuboid cuboid;
	private int flip;
	private String lcach;
	private HashMap<String, Integer> dupcheck;
	private JButton clear;
	private Graphics2D g2d;
	private boolean clearCheck=false;

	public CornerChooser(HashMap<String, Color> colorScheme, PuzzleCanvas puzzleCanvas) {
		this.paintCanvas = puzzleCanvas.getCanvas();
		this.cuboid = (Cuboid) puzzleCanvas.getPuzzle();
		cornermap = new HashMap<String,String>();
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(100, PREFERRED_HEIGHT));
		setOpaque(true);
		
		colors = colorScheme;
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
		setOpaque(true);
		
		StickerColor = new HashMap<GeneralPath, String>();
		cornermap = new HashMap<String, String>();
		dupcheck = new HashMap<String,Integer>();
		
		flip = 0;
		lcach = "";
		
		clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		clear.setFocusable(false);
		clear.setBounds(0, 0, 40, 20);
		add(clear, BorderLayout.LINE_START);
	}
	
	public void reset() {
		currentCorner = 0;
		uniColor();
		dupcheck = new HashMap<String,Integer>();
		setCorner(7, ";", "D,R,B");
		cuboid.fireCanvasChange();
		clearCheck=true;	
	}

	private void uniColor(){
		for (PuzzleSticker[][] a : cuboid.cubeStickers)
			for(PuzzleSticker[] b: a)
				for (PuzzleSticker c: b){
					c.setFace(null);
				}
		cuboid.fireStateChanged(null);
	}
	

	private GeneralPath drawSticker(Graphics2D g2d, float x, float y, double theta, Color c){
		GeneralPath p = new GeneralPath();
		p.moveTo(x,y);
		double t =Math.sin((Math.PI)/3);
		float a = (float) (STICKER_LENGTH*Math.sin(Math.PI/3*2));
		float b = (float) (STICKER_LENGTH*Math.sin(Math.PI/3));
		p.lineTo((float)(x-STICKER_LENGTH*Math.cos(Math.PI/6)),(float)(y-STICKER_LENGTH*Math.sin(Math.PI/6)));
		p.lineTo((float)(x),(float)(y-2*STICKER_LENGTH*Math.sin(Math.PI/6)));
		p.lineTo((float)(x+STICKER_LENGTH*Math.cos(Math.PI/6)),(float)(y-STICKER_LENGTH*Math.sin(Math.PI/6)));
		p.closePath();
		p.transform(AffineTransform.getRotateInstance(theta, x, y));
		g2d.setColor(c);
		g2d.fill(p);
		g2d.setColor(Color.WHITE);
		g2d.draw(p);
		
		return p;
	}
	private void drawCorner(Graphics2D g2d, float x, float y, String a, String b, String c, String k){
		GeneralPath p;
		Color[] stickers = new Color[3];
		String faces = a+","+b+","+c;
		stickers[0]=colors.get(a);
		stickers[1]=colors.get(b);
		stickers[2]=colors.get(c);
		if (dupcheck.containsKey(k)){
			for(int i = 0; i < stickers.length; i++)
				stickers[i]=stickers[i].darker().darker();
		}	
		p=drawSticker(g2d,x,y,0,stickers[0]);
		StickerColor.put(p, faces);
		p=drawSticker(g2d,x,y,Math.PI/3*2,stickers[1]);
		StickerColor.put(p, faces);
		p=drawSticker(g2d,x,y,Math.PI/3*4,stickers[2]);
		StickerColor.put(p, faces);
		
		
	}
	private void paintkeyChar(String k, int x, int y, Graphics2D g2d){
		g2d.setColor(Color.WHITE);
		g2d.drawString(k, x-2, y);
	}
	
	private void cornertableUpdate(String stickers, String idx){

		cornermap.put(idx, stickers);
		
	}
	protected void paintComponent1() {
		
		//getComponentGraphic
		//Graphics2D g2d = (Graphics2D) getGraphics() ;
		if(isOpaque()) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}
		double gap = (double) getWidth() / 15;
		int x = 85;
		paintkeyChar("SPACE, BACKSPACE TO MOVE HIGHLIGHTED CORNER", x,12, g2d);
		
		drawCorner(g2d,x,PREFERRED_HEIGHT/2,"U","L","B", "a");
		paintkeyChar("A", x, PREFERRED_HEIGHT, g2d);
		cornertableUpdate("U,L,B","a");
		x +=STICKER_LENGTH+gap;
		drawCorner(g2d,x,PREFERRED_HEIGHT/2,"U","F","L","s");
		paintkeyChar("S", x, PREFERRED_HEIGHT, g2d);
		cornertableUpdate("U,F,L","s");
		
		x +=STICKER_LENGTH+gap;
		drawCorner(g2d,x,PREFERRED_HEIGHT/2,"U","R","F","d");
		paintkeyChar("D", x, PREFERRED_HEIGHT, g2d);
		cornertableUpdate("U,R,F","d");
		
		
		x +=STICKER_LENGTH+gap;
		drawCorner(g2d,x,PREFERRED_HEIGHT/2,"U","B","R","f");
		paintkeyChar("F", x, PREFERRED_HEIGHT, g2d);
		cornertableUpdate("U,B,R","f");
		
		x +=STICKER_LENGTH+gap;
		drawCorner(g2d,x,PREFERRED_HEIGHT/2,"D","B","L","j");
		paintkeyChar("J", x, PREFERRED_HEIGHT, g2d);
		cornertableUpdate("D,B,L","j");
		
		
		x +=STICKER_LENGTH+gap;
		drawCorner(g2d,x,PREFERRED_HEIGHT/2,"D","L","F","k");
		paintkeyChar("K", x, PREFERRED_HEIGHT, g2d);
		cornertableUpdate("D,L,F","k");
		
		x +=STICKER_LENGTH+gap;
		drawCorner(g2d,x,PREFERRED_HEIGHT/2,"D","F","R","l");
		paintkeyChar("L", x, PREFERRED_HEIGHT, g2d);
		cornertableUpdate("D,F,R","l");
		
		x +=STICKER_LENGTH+gap;
		drawCorner(g2d,x,PREFERRED_HEIGHT/2,"D","R","B",";");
		paintkeyChar(";", x, PREFERRED_HEIGHT, g2d);
		cornertableUpdate("D,R,B",";");
		

		colorRectangles = new HashMap<String, Rectangle2D>();
		for(String face : colors.keySet()) {
			colorRectangles.put(face, new Rectangle2D.Double());
		}
		colorRectangles.put("null", new Rectangle2D.Double());
		
	}

	protected void paintComponent(Graphics g) {
		g2d = (Graphics2D) g;
		paintComponent1();
	}
	private GeneralPath getClickedGP(){
		Point p = getMousePosition();
		if(p == null) return null;
		for(GeneralPath face : StickerColor.keySet())
			if(face.contains(p)){
				return face;
			}
		return null;
	}
	public String getClickedFace() {
		GeneralPath g = getClickedGP();
		if (g == null) return null;
		return StickerColor.get(g);
		/*
		Point p = getMousePosition();
		System.out.println(p);
		if(p == null) return null;
		for(GeneralPath face : StickerColor.keySet())
			if(face.contains(p)){
				System.out.println(StickerColor.get(face));
				return StickerColor.get(face);
			}
		return null;*/
	}
	
	private void pieceRotate(GeneralPath g){
		if(StickerColor.containsKey(g)){
			String[] swap = StickerColor.get(g).split(",");
			StickerColor.put(g, swap[1]+","+swap[2]+","+swap[0]);
		}	
	}
//	private void refreshCursor() {
//		Cursor c = selectedCorner == null ? Cursor.getDefaultCursor() : createCursor(selectedCorner);
//		this.setCursor(c);
//		paintCanvas.setCursor(c);
//		repaint();
//	}
//	
//	private static final int CURSOR_SIZE = 32;
//	private Cursor createCursor(String c) {
//		String[] faces =getClickedFace().split(",");
//		BufferedImage buffer = new BufferedImage((int) (2*CURSOR_SIZE*Math.cos(Math.PI/6)), (int) (3*CURSOR_SIZE*Math.sin(Math.PI/6)), BufferedImage.TYPE_INT_ARGB);
//		Graphics2D g2d = (Graphics2D) buffer.createGraphics();
//		
//		drawCorner(g2d, (float)(CURSOR_SIZE*Math.cos(Math.PI/6)), (float)(CURSOR_SIZE*Math.sin(Math.PI/6)), faces[0],faces[1], faces[2], null);
//		
//		Toolkit tool = Toolkit.getDefaultToolkit();
//		return tool.createCustomCursor(buffer, new Point(0, 0), "bucket");
//	}

	public void mouseClicked(MouseEvent e) {
		String face = getClickedFace();
		if(face != null)/*
		String[] faces = getClickedFace().split(",");
		Color[] face = new Color[3];
		face[0]=colors.get(faces[0]);
		face[1]=colors.get(faces[1]);
		face[2]=colors.get(faces[2]);		
		if(face != null) */{
			System.out.println("face is "+face);
			if (!face.equals(selectedCorner))
				selectedCorner = face;
			else{
				pieceRotate(getClickedGP());
				selectedCorner = getClickedFace();
			}
//			refreshCursor();
			System.out.println("Is corner changed?"+ getClickedFace());
		}
	}
	
	public String getSelectedFace() {
		return selectedCorner;
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {
		repaint();
	}
	public void mouseMoved(MouseEvent e) {
		repaint();
	}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	
	private void setCorner(int currentCorner, String s, String corner) {
		String[] cs = corner.split(",");
		PuzzleSticker[] ps = cuboid.getCorner(currentCorner);
		if (lcach.equals(s))
			flip+=1;
		else{
			flip = 0;
			lcach = s;
		}
		for (int i = 0; i < ps.length; i++) {
			ps[i].setFace(cs[(i+flip)%3]);
		}
		Integer dupcorner = dupcheck.get(s);
		if(dupcorner != null && dupcorner != currentCorner) {
			PuzzleSticker[] ps2 = cuboid.getCorner(dupcorner);
			for (int i = 0 ; i< ps2.length; i++)
				ps2[i].setFace(null);
			
		}
		for (String k : new HashSet<String>(dupcheck.keySet())){
			if(dupcheck.get(k).equals(currentCorner)){
				dupcheck.remove(k);
			}
		}
		dupcheck.put(s, currentCorner);
		
		cuboid.fireCanvasChange();
	}
	
	private void lastCorner(HashMap<String,Integer> chosen, Set<String> all){
		all.removeAll(chosen.keySet());
		
		Iterator <String> i = all.iterator();
		String s="";
		while(i.hasNext()){
			s = i.next();
			
		}
		Iterator <Integer> j = chosen.values().iterator();
		Integer t=1+2+3+4+5+6+7;
		while(j.hasNext())
			t-=j.next();
		currentCorner = t;
		while(cuboid.getState().equals("Invalid"))
			setCorner(currentCorner,s,cornermap.get(s));
		setVisible(false);
		
	}
	public void keyPressed(KeyEvent e) {
		if (!clearCheck)
			return;
		String s = e.getKeyChar() + "";
		if (cornermap.containsKey(s)) {
			setCorner(currentCorner, s, cornermap.get(s));
		} else if(e.getKeyCode() == KeyEvent.VK_SPACE){
			currentCorner++;
			lcach="";
			if (currentCorner>7)
				currentCorner-=8;
			if (dupcheck.keySet().size()==7)
				lastCorner(dupcheck,cornermap.keySet());
		}
			
		else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
			currentCorner--;
			lcach="";
			if(currentCorner<0)
				currentCorner+=8;
			if (dupcheck.keySet().size()==7)
				lastCorner(dupcheck,cornermap.keySet());
		}
		paintComponent1();


	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent arg0) {}

	private Timer glowTimer = new Timer(100, this);
	private int currentCorner = -1;
	private double[] default_center = null, lowered_center;
	public void setVisible(boolean visible){
		super.setVisible(visible);
		if(default_center == null) {
			default_center = cuboid.getCenter();
			lowered_center = default_center.clone();
			lowered_center[1]-=.25;
			lowered_center[2]++;
		}
		if(visible) {
			cuboid.setCenter(lowered_center[0], lowered_center[1], lowered_center[2]);
			glowTimer.start();
			cuboid.setDisabled(true);
			paintCanvas.addKeyListener(this);
		} else {
			cuboid.setCenter(default_center[0], default_center[1], default_center[2]);
			if(!cuboid.getState().equals("Invalid")) {
				currentCorner = -1;
				clearCheck = false;
			}
			cuboid.setDisabled(false);
			paintCanvas.removeKeyListener(this);
			glowTimer.stop();
			actionPerformed(null);
		}
	}

	public void actionPerformed(ActionEvent e) {
		//assuming the event is fired by glowTimer
		for(int i = 0; i < 8; i++) {
			PuzzleSticker[] polys = cuboid.getCorner(i);
			if(polys == null)
				return;
			for(PuzzleSticker poly : polys) {
				Color c;
				if(i == currentCorner && glowTimer.isRunning())
					c = new Color((int) System.currentTimeMillis());
				else
					c = Color.BLACK;
				poly.setBorderColor(c);
				cuboid.fireCanvasChange();
			}
		}
	}
}
