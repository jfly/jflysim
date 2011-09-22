package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import netscape.javascript.JSObject;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Canvas3D;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.RotationMatrix;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Shape3D;

@SuppressWarnings("serial")
public class Test3D extends JApplet {
	private Shape3D shape = new Shape3D(0, 0, 10) {
		{
			Polygon3D poly = new Polygon3D();
			poly.addPoint(-1, -1, 0);
			poly.addPoint(-1, 1, 0);
			poly.addPoint(1, 1, 0);
			poly.addPoint(1, -1, 0);
			poly.setFillColor(Color.RED);
			addPoly(poly);
			
			poly = poly.clone();
			poly.rotate(new RotationMatrix(0, 90));
			poly.translate(0, -1, 0);
			poly.setFillColor(Color.GREEN);
			addPoly(poly);
		}
	};
	private Canvas3D canvas;
	
	private JSObject jso;
	public void init() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					canvas = new Canvas3D();
					canvas.addShape3D(shape);

					JPanel pane = new JPanel(new BorderLayout());
					setContentPane(pane);
					pane.add(canvas, BorderLayout.CENTER);
					canvas.requestFocusInWindow();
				}
			});
			jso = JSObject.getWindow(this);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		final Test3D a = new Test3D();
		a.init();
		a.setPreferredSize(new Dimension(400, 500));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JPanel pane = new JPanel();
				f.setContentPane(pane);
				f.add(a);
				f.pack();
				f.setVisible(true);
				a.canvas.requestFocusInWindow();
			}
		});
	}
}
