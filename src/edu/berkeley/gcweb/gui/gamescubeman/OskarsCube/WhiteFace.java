package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import java.awt.Color;
import java.util.HashMap;

import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;

public class WhiteFace {
	PolygonCollection holder;

	public WhiteFace(CubeGen cube) {
		
		int max2 = cube.boardsize*2 +1;

		// The white sides polygons go in here.
		Polygon3D white_border = new Polygon3D();
		
//		white_border.setBorderColor(null);
		white_border.setFillColor(Color.WHITE);
		white_border.addPoint(0, 0, 0);
		white_border.addPoint(0, 0, max2);
		white_border.addPoint(0, -max2, max2);
		white_border.addPoint(0, -max2, 0);
		white_border.addPoint(0, -0, 0);
		HashMap<Integer, Boolean> check = new HashMap<Integer, Boolean>();
		check.putAll(cube.edges_white);
		int adj = 2*cube.boardsize;
		int max = 2*cube.boardsize -1;
		int count =0;
		int x0=0;
		int y0=0;
		for(; count <check.size(); count++) {
			white_border.addPoint(0,-x0-1,y0+1);
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
		

		Polygon3D end_dot = new Polygon3D();
		end_dot.setFillColor(Color.MAGENTA);
		// WHITE IS YZ so pull 1 and 2 out of end
		int endy = cube.end[1];
		int endz = cube.end[2];
		end_dot.addPoint(0, -(endy + 1), (endz + 1.5));
		end_dot.addPoint(0, -(endy + 1.5), (endz + 2));
		end_dot.addPoint(0, -(endy + 2), (endz + 1.5));
		end_dot.addPoint(0, -(endy + 1.5), (endz + 1));
		
		Object[] input_array = new Polygon3D[3];
		
		Polygon3D start_dot = new Polygon3D();
		start_dot.setFillColor(Color.white);
		start_dot.setOpacity(0);
		// WHITE IS YZ so pull 1 and 2 out of end
		int starty = cube.start[1];
		int startz = cube.start[2];
		if (starty != endy || startz != endz) {
		start_dot.addPoint(0, -(starty + 1), (startz + 1.5));
		start_dot.addPoint(0, -(starty + 1.5), (startz + 2));
		start_dot.addPoint(0, -(starty + 2), (startz + 1.5));
		start_dot.addPoint(0, -(starty + 1.5), (startz + 1));
		input_array[2] = start_dot;
		}
		
		input_array[0] = white_border;
		input_array[1] = end_dot;
		

		// create array of polygons here
		// put them into holder

		holder = new PolygonCollection(input_array);
	}

	public PolygonCollection returnItem() {
		return holder;
	}
}