package edu.berkeley.gcweb.gui.gamescubeman.Cuboid;


public class ColorSpitter {
	public static final int[][][] pieces = new int[][][] {
			{ {0, 0}, {1, 0}, {1, 0} },
			{ {0, 1}, {1, 1}, {1, 0} },
			{ {1, 0}, {1, 0}, {1, 1} },
			{ {1, 1}, {1, 1}, {1, 1} },
			
			{ {0, 0}, {0, 0}, {0, 0} },
			{ {0, 1}, {0, 0}, {0, 1} },
			{ {1, 0}, {0, 1}, {0, 0} },
			{ {1, 1}, {0, 1}, {0, 1} },
	};
	private static final char[][] solved_cube = {{'U','R','F'},{'U','F','L'},{'U','B','R'},{'U','L','B'},{'D','F','R'},{'D','L','F'},{'D','R','B'},{'D','B','L'}};
	public static CubeFace[][] solved_cube_faces;
	static {
		solved_cube_faces = new CubeFace[solved_cube.length][];
		for(int i=0; i<solved_cube_faces.length; i++) {
			solved_cube_faces[i] = new CubeFace[solved_cube[i].length];
			for(int j=0; j<solved_cube_faces[i].length; j++)
				solved_cube_faces[i][j] = CubeFace.decodeFace(solved_cube[i][j]);
		}
	}
	public static CubeFace[][] spit_out_colors(int[] pieces, int[] orientations){
		/**
		 * Take in an array of pieces and their orientations and return a char[][] containing the 
		 * stickers on each side.
		 */
		CubeFace[][] actual_colors = new CubeFace[8][];
		int location = 0;
		for(int i=0; i<pieces.length; i++){
			int piece = pieces[i], orientation = orientations[i];
			CubeFace[] current_chunk = new CubeFace[3];
            current_chunk[0] = (solved_cube_faces[piece][(orientation)%3]); //top piece
            current_chunk[1] = (solved_cube_faces[piece][(1+orientation)%3]); //right piece
            current_chunk[2] = (solved_cube_faces[piece][(2+orientation)%3]); //left piece
            actual_colors[location] = current_chunk;
            location++;
		}
		return actual_colors;
	}
	public static void main(String[] args){
		System.out.println("Debugging");
		int[] pieces = { 0, 1, 2, 3, 4, 5, 6, 7 };
		int[] orientations = { 1, 2, 0, 0, 0, 0, 0, 0 };
		CubeFace[][] current_state = spit_out_colors(pieces, orientations);
		String cube_string = "                                 ___________\n";
		cube_string += "                                 |     |    |\n";
        cube_string += "                    __________   |  "+current_state[2][1]+"  | "+current_state[3][2]+"  |\n";
        cube_string += "   /|              / "+current_state[3][0]+"  / "+current_state[2][0]+"  /|  |_____|____|\n";
        cube_string += "  / |             /____/____/ |  |     |    |\n";
        cube_string += " /| |            / "+current_state[1][0]+"  / "+current_state[0][0]+"  /| |  |  "+current_state[6][2]+"  | "+current_state[7][1]+"  |\n";
        cube_string += "/ |"+current_state[1][2]+"|           /____/____/ |"+current_state[2][2]+"|  |_____|____|\n";
        cube_string += "|"+current_state[3][1]+"| |          |     |    |"+current_state[0][1]+"| |     BACK\n";
        cube_string += "| |/|          |  "+current_state[1][1]+"  | "+current_state[0][2]+"  | |/|\n";
        cube_string += "|/|"+current_state[5][1]+"|          |_____|____|/|"+current_state[6][1]+"|\n";
        cube_string += "|"+current_state[7][2]+"| |          |     |    |"+current_state[4][2]+"| |\n";
        cube_string += "| |/           |  "+current_state[5][2]+"  | "+current_state[4][1]+"  | |/\n";
        cube_string += "|/LEFT         |_____|____|/\n\n\n";
        cube_string += "              __________\n";
        cube_string += "             / "+current_state[5][0]+"  / "+current_state[4][0]+"  /\n";
        cube_string += "            /____/____/\n";
        cube_string += "           / "+current_state[7][0]+"  / "+current_state[6][0]+"  /\n";
        cube_string += "          /____/____/\n";
        cube_string += "              DOWN\n";
        System.out.println(cube_string);
	}
}
