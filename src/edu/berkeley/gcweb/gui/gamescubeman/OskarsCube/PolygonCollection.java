package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.RotationMatrix;

public class PolygonCollection {
	private Polygon3D[] collection;

	public PolygonCollection(Object[] input_array) {
		/**
		 * Constructor for the PolygonCollection class.
		 * 
		 */
		collection = new Polygon3D[20000];
		int current_position = 0;
		for (int i = 0; i < input_array.length; i++) {
			if (input_array[i] instanceof PolygonCollection) {
				PolygonCollection temp = (PolygonCollection) input_array[i];
				Polygon3D[] temp_array = temp.extract_polygons();
				for (int k = 0; k < temp_array.length; k++) {
					if (temp_array[k] != null) {
						collection[current_position] = temp_array[k];
						current_position++;
					}
				}
			} else if (input_array[i] instanceof Polygon3D) {
				Polygon3D temp = (Polygon3D) input_array[i];
				collection[current_position] = temp;
				current_position++;
			}
		}
	}

	public void rotate(char axis, int degreesCCW) {
		/**
		 * rotate the entire set of polygons by the said number of degrees CCW
		 * along an axis. The axis is entered as a char, i.e. 'x', 'y', or 'z'
		 */
		int rotation_number = -1;
		RotationMatrix m;
		if (axis == 'x')
			rotation_number = 0;
		else if (axis == 'y')
			rotation_number = 1;
		else
			rotation_number = 2;
		m = new RotationMatrix(rotation_number, degreesCCW);
		for (int i = 0; i < collection.length; i++) {
			if (collection[i] != null)
				collection[i].rotate(m);
		}
	}

	public void translate(double x, double y, double z) {
		/**
		 * Translate the set of polygons position by amount (x,y,z). i.e. an
		 * object at (0,1,0) running translate(0,1,0) will translate to (0,2,0).
		 */
		for (int i = 0; i < collection.length; i++) {
			if (collection[i] != null)
				collection[i].translate(x, y, z);
		}
	}

	public Polygon3D[] extract_polygons() {
		/**
		 * Returns all the polygon's currently in this collection.
		 */
		return collection;
	}
}