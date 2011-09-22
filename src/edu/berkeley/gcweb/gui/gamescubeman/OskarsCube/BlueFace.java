package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import java.awt.Color;
import java.util.HashMap;

import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;

public class BlueFace { // THE BLUE FACE IS XZ
	PolygonCollection holder;

	public BlueFace(CubeGen cube) {
		// The blue sides polygons go in here.
		int max2 = cube.boardsize*2 +1;
		
		Polygon3D blue_border = new Polygon3D();
		blue_border.setFillColor(Color.BLUE);
		blue_border.addPoint(0, 0, 0);
		blue_border.addPoint(0, 0, max2);
		blue_border.addPoint(max2, 0, max2);
		blue_border.addPoint(max2, 0, 0);
		blue_border.addPoint(0, 0, 0);
		//blue_border.addPoint(0, 0, 1);
		
		HashMap<Integer, Boolean> check = new HashMap<Integer, Boolean>();
		check.putAll(cube.edges_blue);
		int adj = 2*cube.boardsize;
		int max = 2*cube.boardsize -1;
		int count =0;
		int x0=0;
		int y0=0;
		for(; count <check.size(); count++) {
			blue_border.addPoint(x0+1,0,y0+1);
			if(check.containsKey(2*x0*adj + 2*y0) && x0<max) {
				if (check.get(2*x0*adj + 2*y0)==true) {
					check.put(adj*2*x0 + 2*y0, false);
					x0 = x0+1;
					//y0= y0;
					continue;
				}
			}
			if(check.containsKey(2*x0*adj + 2*y0 +1)&& y0<max) {
				if (check.get(2*x0*adj + 2*y0+1)==true) {
					check.put(2*x0*adj + 2*y0 +1, false);
					//x0 = x0;
					y0= y0 +1;
					continue;
				}
			}
			if(check.containsKey(2*(x0-1)*adj + 2*y0)&& x0>0) {
				if (check.get(2*(x0-1)*adj + 2*y0)==true) {
					check.put(2*(x0-1)*adj + 2*y0, false);
					x0 = x0-1;
					//y0= y0;
					continue;
				}
			}
			if(check.containsKey(2*x0*adj + 2*(y0-1) +1)&& y0>0) {
				if (check.get(2*x0*adj + 2*(y0-1)+1)==true) {
					check.put(2*x0*adj + 2*(y0-1) +1, false);
					//x0 = x0;
					y0= y0 -1;
					continue;
				}
			}
		}
		
		

		Polygon3D green_dot = new Polygon3D();
		green_dot.setFillColor(Color.MAGENTA);
		// BLUE IS XZ so pull 0 and 2 out of end
		int endx = cube.end[0];
		int endz = cube.end[2];
		green_dot.addPoint(endx + 1, 0, (endz + 1.5));
		green_dot.addPoint(endx + 1.5, 0, (endz + 2));
		green_dot.addPoint(endx + 2, 0, (endz + 1.5));
		green_dot.addPoint(endx + 1.5, 0, (endz + 1));

		Object[] input_array = new Polygon3D[3];
		
		Polygon3D start_dot = new Polygon3D();
		start_dot.setFillColor(Color.white);
		start_dot.setOpacity(0);
		int startx = cube.start[0];
		int startz = cube.start[2];
		if (startx != endx || startz != endz) {
			start_dot.addPoint(startx + 1, 0, (startz + 1.5));
			start_dot.addPoint(startx + 1.5, 0, (startz + 2));
			start_dot.addPoint(startx + 2, 0, (startz + 1.5));
			start_dot.addPoint(startx + 1.5, 0, (startz + 1));
			input_array[2] = start_dot;
		}
		
		
		input_array[0] = blue_border;
		input_array[1] = green_dot;
		

		// create array of polygons here
		// put them into holder

		holder = new PolygonCollection(input_array);
	}

	public PolygonCollection returnItem() {
		return holder;
	}
}