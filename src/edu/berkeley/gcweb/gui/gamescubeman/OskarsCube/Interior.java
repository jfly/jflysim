package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;

public class Interior {

	// the viable positions, and unviable ones appear inside
	public PolygonCollection holder;

	public Interior(Solver solved, CubeGen gened) {
		int boardsize = gened.boardsize;
		int x, y, z;
		int countgreen = 0;
		int countred = 0;
		Object[] input_array = new PolygonCollection[boardsize*boardsize*boardsize];
		for (x = 0; x < boardsize; x++) {
			for (y = 0; y < boardsize; y++) {
				for (z = 0; z < boardsize; z++) {
					PolygonCollection cube;
					if (solved.move_map.containsKey(boardsize*boardsize * 8 * x + boardsize * 4 * y
							+ 2 * z)) {
						// cube = new Stick(1,1).returnItem();
						countgreen += 1;
						// cube.translate(2*x,2*y, 2*z);
						// int a = x*25 + y*5 + z;
						// input_array[a] = cube;
					} else {
						cube = new Stick(1).returnItem();
						countred += 1;
						cube.translate(2 * x, 2 * -y, 2 * z);
						int a = x * boardsize*boardsize + boardsize * y + z;
						input_array[a] = cube;
					}

				}
			}
		}
		holder = new PolygonCollection(input_array);
		holder.translate(-boardsize+.5, boardsize-1.5, -boardsize+.5);
		// System.out.println("viable: " + countgreen + " unviable: " +
		// countred);
		acheivable = countgreen;
	}

	public Polygon3D[] extract() {
		return holder.extract_polygons();
	}

	public int acheivable;
}
