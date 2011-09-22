package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import java.util.HashMap;
import java.util.LinkedList;

import edu.berkeley.gcweb.gui.gamescubeman.OskarsCube.Solver.Node;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;

public class InteriorSolutionPath {
	
	
	public PolygonCollection solution;
	
	//This class generates the solution path in the interior of the cube
	public InteriorSolutionPath(Solver solved, CubeGen gened, boolean[][][] traveled_map, int[] cur_pos) {
		
		//int remoteness = solved.getRemoteness(solved.start)/2;
		int boardsize = gened.boardsize;
		int crem = 0;
		LinkedList<Node> queue = new LinkedList<Node>();
		int start_key = solved.start[0] * boardsize*boardsize*4 + solved.start[1] * boardsize*2 + solved.start[2];
		queue.add(solved.move_map.get(start_key));
		HashMap<Integer, Boolean> seen_map = new HashMap<Integer, Boolean>();
		Object[] input_array = new PolygonCollection[4*125];
		
		
		while (!queue.isEmpty()) {
			Node head = queue.removeFirst();
			int the_key = head.board[0] * boardsize*boardsize*4 + head.board[1] * boardsize*2
					+ head.board[2];
			seen_map.put(the_key, true);
			for (int[] legal_move : head.moves) {
				int[] new_board = { head.board[0] + 2*legal_move[0],
						head.board[1] + 2*legal_move[1],
						head.board[2] + 2*legal_move[2] };
				int[] half_board = { head.board[0] + legal_move[0],
						head.board[1] + legal_move[1],
						head.board[2] + legal_move[2] };
				int new_key = new_board[0] * boardsize*boardsize*4 + new_board[1] * boardsize*2
				+ new_board[2];
				if (seen_map.containsKey(new_key))
					continue;
				int color = 1;
				int color2 = 1;
				//System.out.println(new_board[0] + " " + new_board[1] + " " + new_board[2]);
				if (solved.move_map.get(new_key).onsolutionpath == true && traveled_map[new_board[0]][new_board[1]][new_board[2]] == true) {
					color = 2;
					color2 = 2;
					//System.out.println("Colored green");
					
				} else if(traveled_map[new_board[0]][new_board[1]][new_board[2]] == true){
					color = 3;
					color2 = 3;
				} else if(solved.move_map.get(new_key).onsolutionpath == true) {
					color = 4;
					color2 = 4;
				} 
				
				if(new_board[0] == cur_pos[0] && new_board[1] == cur_pos[1] && new_board[2] == cur_pos[2]){
					color = 5;
					
				}
				PolygonCollection cube1= new Stick(1,color).returnItem();
				PolygonCollection cube2= new Stick(1,color2).returnItem();
			
				cube1.translate(new_board[0],-new_board[1],new_board[2]);
				cube2.translate(half_board[0], -half_board[1],half_board[2]);
				input_array[2*crem] = cube1;
				input_array[2*crem+1] =cube2;
				queue.add(solved.move_map.get(new_key));
				crem++;
			}
		}
		//Note the end square never gets added so fix that
		
		solution = new PolygonCollection(input_array);
		solution.translate(-boardsize+.5, boardsize-1.5, -boardsize+.5);
		
	}

	
	public Polygon3D[] extract() {
		return solution.extract_polygons();
	}
	
}


