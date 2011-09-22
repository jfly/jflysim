package edu.berkeley.gcweb.gui.gamescubeman.ThreeD;

import java.util.ArrayList;

public abstract class Shape3D {
	protected double centerX, centerY, centerZ;
	protected Canvas3D canvas;
	public Shape3D(double x, double y, double z) {
		setCenter(x, y, z);
	}
	public void setCanvas(Canvas3D canvas) {
		this.canvas = canvas;
	}
	public void fireCanvasChange() {
		if(canvas != null)
			canvas.fireCanvasChange();
	}
	public double[] getCenter() {
		return new double[] { centerX, centerY, centerZ };
	}
	public void setCenter(double x, double y, double z) {
		centerX = x;
		centerY = y;
		centerZ = z;
		fireCanvasChange();
	}
	protected ArrayList<Polygon3D> polys = new ArrayList<Polygon3D>();
	protected void clearPolys() {
		polys.clear();
	}
	protected void addPoly(Polygon3D poly) {
		polys.add(poly);
	}
	protected void addPolys(PolygonCollection<?> newPolys) {
		for(Polygon3D p : newPolys)
			polys.add(p);
	}
	
	//We're viewing this shape from the origin, looking down
	//the z-axis. It is up to the Shape3D subclass to ensure that our cube doesn't
	//intersect with the viewport (z=1)
	public ArrayList<Polygon3D> getPolygons() {
		ArrayList<Polygon3D> rendered = new ArrayList<Polygon3D>();
		for(Polygon3D poly : polys) {
			if(!poly.isVisible()) continue;
			poly = poly.clone();
			poly.rotate(rotation);
			poly.translate(centerX, centerY, centerZ);
			rendered.add(poly);
		}
		return rendered;
	}

	protected RotationMatrix rotation = new RotationMatrix();
	public void setRotation(RotationMatrix m) {
		rotation = (m == null) ? new RotationMatrix() : m;
	}
	public RotationMatrix getRotation() {
		return rotation;
	}
	public void rotate(RotationMatrix m) {
		rotation = m.multiply(rotation);
	}
}
