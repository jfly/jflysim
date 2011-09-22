package edu.berkeley.gcweb.gui.gamescubeman.ThreeD;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

public class Polygon3D implements Comparable<Polygon3D> {
	private Polygon3D ogPoly;
	public Polygon3D(Color fill, Color border) {
		setFillColor(fill);
		setBorderColor(border);
	}
	public Polygon3D() {
		this(null, Color.BLACK);
	}
	private boolean visible = true;
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public boolean isVisible() {
		return visible;
	}
	public Polygon3D getOGPoly() {
		return ogPoly;
	}
	private AlphaComposite ac; { setOpacity(1); }
	private float opacity;
	public void setOpacity(float opacity) {
		this.opacity = opacity;
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
	}
	public float getPercentOpacity() {
		return opacity;
	}
	public AlphaComposite getOpacity() {
		return ac;
	}
	private Color fillColor, borderColor; //null means transparent
	public void setFillColor(Color fill) {
		fillColor = fill;
	}
	public void setBorderColor(Color border) {
		borderColor = border;
	}
	public Color getFillColor() {
		return fillColor;
	}
	public Color getBorderColor() {
		return borderColor;
	}
	
	public Polygon3D clone() {
		Polygon3D clone = new Polygon3D();
		copyInto(clone);
		return clone;
	}
	protected void copyInto(Polygon3D clone) {
		clone.ogPoly = this;
		clone.fillColor = fillColor;
		clone.borderColor = borderColor;
		clone.ac = this.ac;
		clone.visible = this.visible;
		for(double[] point : points)
			clone.addPoint(point[0], point[1], point[2]);
	}
	
	public double[] getPoint(int index) {
		return points.get(index);
	}
	
	private ArrayList<double[]> points;
	public void addPoint(double x, double y, double z) {
		if(points == null)
			points = new ArrayList<double[]>();
		points.add(new double[] { x, y, z });
	}
	public void addPoint(double[] point) {
		addPoint(point[0], point[1], point[2]);
	}

	public Polygon3D rotate(RotationMatrix m) {
		for(int i = 0; i < points.size(); i++)
			points.set(i, m.multiply(points.get(i)));
		return this;
	}
	public Polygon3D scale(double x, double y, double z) {
		for(double[] p : points) {
			p[0] *= x;
			p[1] *= y;
			p[2] *= z;
		}
		return this;
	}
	public Polygon3D translate(double x, double y, double z) {
		for(double[] p : points) {
			p[0] += x;
			p[1] += y;
			p[2] += z;
		}
		return this;
	}
	public Polygon3D translate(double[] amt) {
		return translate(amt[0], amt[1], amt[2]);
	}
	public Polygon3D mirror(int axis) {
		for(double[] p : points)
			p[axis] = -p[axis];
		return this;
	}

	private static double[] subtract(double[] a, double[] b) {
		double[] result = new double[3];
		for(int i = 0; i < result.length; i++)
			result[i] = a[i] - b[i];
		return result;
	}
	private static double dot(double[] a, double[] b) {
		double dot = 0;
		for(int i=0; i<a.length; i++)
			dot += a[i]*b[i];
		return dot;
	}
	private static double[] cross(double[] a, double[] b) {
		double[] cross = new double[3];
		cross[0] = a[1]*b[2] - a[2]*b[1];
		cross[1] = a[2]*b[0] - a[0]*b[2];
		cross[2] = a[0]*b[1] - a[1]*b[0];
		return cross;
	}
	
	public double aveZ() {
		double ave = 0;
		for(double[] point : points)
			ave += point[2];
		return ave / points.size();
	}
	
	//returns the z coordinate of the intersection of line through (0, 0, 0) -> (x, y, viewport)
	//and the plane our polygon lies on
	//we assume that we're dealing with flat convex polygons
	private double unproject(double x, double y, double scale) {
		int viewport = 1; //this really shouldn't matter for what we're using this function for
		//TODO - do the maths!
		double[] center = points.get(0);
		double[] vector1 = subtract(points.get(1), center);
		double[] vector2 = subtract(points.get(2), center);
		double[] normal = cross(vector1, vector2);
		//we want to choose a c such that c[(x, y) * normal] == center * normal
		double[] point = new double[] {x, y, viewport};
		double c = dot(center, normal) / dot(point, normal);
		return viewport * c;
	}
	
	public Shape projectXYPlane(double z, double scale) {
		GeneralPath poly = new GeneralPath();
		for(double[] p : points) {
			float x = (float) (scale*p[0]*z/p[2]);
			float y = (float) (scale*p[1]*z/p[2]);
			if(poly.getCurrentPoint() == null)
				poly.moveTo(x, y);
			else
				poly.lineTo(x, y);
		}
		poly.closePath();
		return poly;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(double[] p : points) {
			sb.append(" -> (" + p[0] + "," + p[1] + "," + p[2] + ")");
		}
		return sb.substring(4);
	}

	//returns true if this covers p, returns false if p covers this
	//returns null if this and p do not intersect
	public Boolean covers(Polygon3D p) {
		if(this == p) return null;
		Shape proj = projectXYPlane(1, 1);
		Shape proj2 = p.projectXYPlane(1, 1);
		Area a = new Area(proj);
		a.intersect(new Area(proj2));
		if(a.isEmpty())
			return null;
		
		PathIterator pi = a.getPathIterator(null);
		double[] avePoint = new double[2];
		double[] point = new double[2];
		int pointCount = 0;
		while(!pi.isDone()) {
			pointCount++;
			pi.currentSegment(point);
			for(int i=0; i<point.length; i++)
				avePoint[i] += point[i];
			pi.next();
		}
		for(int i=0; i<point.length; i++)
			avePoint[i] /= pointCount;
		double x = avePoint[0], y = avePoint[1]; //we want to look @ the center to hopefully avoid ties
		double diff = p.unproject(x, y, 1) - unproject(x, y, 1);
		if(Math.abs(diff) < 0.02)
			return null;
		return Math.signum(diff) < 0;
	}
	
	//TODO -delteated!
	//higher Z -> lower value
	public int compareTo(Polygon3D p) {
		Shape proj = projectXYPlane(1, 1);
		Shape proj2 = p.projectXYPlane(1, 1);
		Area a = new Area(proj);
		a.intersect(new Area(proj2));
		if(!a.isEmpty()) {
			PathIterator pi = a.getPathIterator(null);
			double[] avePoint = new double[2];
			double[] point = new double[2];
			int pointCount = 0;
			while(!pi.isDone()) {
				pointCount++;
				pi.currentSegment(point);
				for(int i=0; i<point.length; i++)
					avePoint[i] += point[i];
				pi.next();
			}
			for(int i=0; i<point.length; i++)
				avePoint[i] /= pointCount;
			double x = avePoint[0], y = avePoint[1]; //we want to look @ the center to hopefully avoid ties
			return (int) Math.signum(p.unproject(x, y, 1) - unproject(x, y, 1));
		} else //this will help deal with the multiple polygon case, but certainly not fix it :(
//			return 0;
			return (int) Math.signum(p.aveZ() - aveZ());
	}
}
