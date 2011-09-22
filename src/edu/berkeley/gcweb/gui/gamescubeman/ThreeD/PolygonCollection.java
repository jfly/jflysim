package edu.berkeley.gcweb.gui.gamescubeman.ThreeD;

import java.util.ArrayList;

import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.Utils;

public class PolygonCollection<P extends Polygon3D> extends ArrayList<P> {
	public PolygonCollection(P... src) {
		for(P p : src)
			add(p);
	}
	/**
	 * Although it shouldn't, this method actually *does* clone the underlying elements ... urgh
	 */
	@SuppressWarnings("unchecked")
	public PolygonCollection<P> clone() {
		PolygonCollection<P> clone = new PolygonCollection<P>();
		for(P poly : this)
			clone.add((P) poly.clone());
		return clone;
	}
	private RotationMatrix netRotations = new RotationMatrix();
	public RotationMatrix getNetRotations() {
		return netRotations;
	}
	public PolygonCollection<P> rotate(RotationMatrix m) {
		return rotate(m, true);
	}
	public PolygonCollection<P> rotate(RotationMatrix m, boolean storeRotation) {
		if(storeRotation)
			netRotations = m.multiply(netRotations);
		for(P poly : this)
			poly.rotate(m);
		return this;
	}
	public PolygonCollection<P> scale(double x, double y, double z) {
		for(P poly : this)
			poly.scale(x, y, z);
		return this;
	}
	public PolygonCollection<P> translate(double[] amt) {
		return translate(amt[0], amt[1], amt[2]);
	}
	public PolygonCollection<P> translate(double x, double y, double z) {
		for(P poly : this)
			poly.translate(x, y, z);
		return this;
	}
	public PolygonCollection<P> mirror(int axis) {
		for(P poly : this)
			poly.mirror(axis);
		return this;
	}
	public void swap(int index1, int index2) {
		Utils.swap(this, index1, index2);
	}
}
