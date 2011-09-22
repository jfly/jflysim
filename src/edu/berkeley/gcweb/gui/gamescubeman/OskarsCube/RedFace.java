package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import java.awt.Color;
import java.util.HashMap;

import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;

public class RedFace {
	PolygonCollection holder;

	public RedFace(CubeGen cube) {
		// The red sides polygons go in here.
		int max2 = cube.boardsize*2 +1;
		
		
		Polygon3D red_border = new Polygon3D();
		red_border.setFillColor(Color.RED);
//		red_border.setBorderColor(null);
		red_border.addPoint(0, 0, 0);
		red_border.addPoint(0, -max2, 0);
		red_border.addPoint(max2, -max2, 0);
		red_border.addPoint(max2, 0, 0);
		red_border.addPoint(0, 0, 0);
		
		HashMap<Integer, Boolean> check = new HashMap<Integer, Boolean>();
		check.putAll(cube.edges_red);
		int adj = 2*cube.boardsize;
		int max = 2*cube.boardsize -1;
		int count =0;
		int x0=0;
		int y0=0;
		for(; count <check.size(); count++) {
			red_border.addPoint(x0+1,-y0-1,0);
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
		// RED IS XY so pull 0 and 1 out of end
		int endx = cube.end[0];
		int endy = cube.end[1];
		green_dot.addPoint(endx + 1, -(endy + 1.5), 0);
		green_dot.addPoint(endx + 1.5, -(endy + 2), 0);
		green_dot.addPoint(endx + 2, -(endy + 1.5), 0);
		green_dot.addPoint(endx + 1.5, -(endy + 1), 0);
		
		Object[] input_array = new Polygon3D[3];
		
		Polygon3D start_dot = new Polygon3D();
		start_dot.setFillColor(Color.white);
		start_dot.setOpacity(0);
		int startx = cube.start[0];
		int starty = cube.start[1];
		if (startx != endx || starty != endy) {
			start_dot.addPoint(startx + 1, -(starty + 1.5), 0);
			start_dot.addPoint(startx + 1.5, -(starty + 2), 0);
			start_dot.addPoint(startx + 2, -(starty + 1.5), 0);
			start_dot.addPoint(startx + 1.5, -(starty + 1), 0);
			input_array[2] = start_dot;
		}
		
		
		input_array[0] = red_border;
		input_array[1] = green_dot;
		

		// create array of polygons here
		// put them into holder

		holder = new PolygonCollection(input_array);
	}

	public PolygonCollection returnItem() {
		return holder;
	}
}