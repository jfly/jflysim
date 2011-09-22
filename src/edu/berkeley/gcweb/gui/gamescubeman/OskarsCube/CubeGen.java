package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import java.util.HashMap;
import java.util.Random;

public class CubeGen {

	public CubeGen(long blue, long white, long red) {
		if(blue> java.lang.Integer.MAX_VALUE)
			blue = -(blue- Integer.MAX_VALUE);
		if(red> java.lang.Integer.MAX_VALUE)
			red = -(red- Integer.MAX_VALUE);
		if(white> java.lang.Integer.MAX_VALUE)
			white = -(white- Integer.MAX_VALUE);
		this.findbest = true;
		this.findbestc = true;
		this.boardsize = 5;
		BlueInt = (int) blue;
		RedInt = (int) red;
		WhiteInt = (int) white;
		Blue = intToSet(BlueInt);
		White = intToSet(WhiteInt);
		Red = intToSet(RedInt);
		edges_blue = makeEdges(Blue);
		edges_white = makeEdges(White);
		edges_red = makeEdges(Red);
		if (!legalBoard(Blue, edges_blue) || !legalBoard(White, edges_white) || !legalBoard(Red, edges_red)) {
			if (loudprint) {
				System.out.println("Saved Failed: " + legalBoard(Blue, edges_blue) + " " + legalBoard(White, edges_white) + " " + legalBoard(Red, edges_red));
			}
			original = false;
			Blue = blocked_xz_face;
			White = blocked_yz_face;
			Red = blocked_xy_face;
			boardsize=5;
			validB = true;
			validW = true;
			validR = true;
			start[0] = 2;
			start[1] = 2;
			start[2] = 2;
			end[0] = 4;
			end[1] = 8;
			end[2] = 8;
			edges_blue = makeEdges(Blue);
			edges_white = makeEdges(White);
			edges_red = makeEdges(Red);
		}
	}
	public CubeGen(boolean random, boolean findbest, boolean findbestc, int boardsize) {
		this.findbest = findbest;
		this.findbestc = findbestc;
		this.boardsize = boardsize;
		if (!random) {
			// set to classic board
			Blue = blocked_xz_face;
			White = blocked_yz_face;
			Red = blocked_xy_face;
			boardsize=5;
			validB = true;
			validW = true;
			validR = true;
			start[0] = 2;
			start[1] = 2;
			start[2] = 2;
			end[0] = 4;
			end[1] = 8;
			end[2] = 8;
		}else {
			randomGen = new Random();
			BlueInt = randomGen.nextInt();
			RedInt = randomGen.nextInt();
			WhiteInt = randomGen.nextInt();
			Blue = intToSet(BlueInt);
			White = intToSet(WhiteInt);
			Red = intToSet(RedInt);
		}
		edges_blue = makeEdges(Blue);
		edges_white = makeEdges(White);
		edges_red = makeEdges(Red);
		
		while(!validB) {
			if (legalBoard(Blue, edges_blue)) {
				validB =true;
				
			} else {
				BlueInt = randomGen.nextInt();
				Blue = intToSet(BlueInt);
				edges_blue = makeEdges(Blue);
				
			}
		}
		while(!validR) {
			if (legalBoard(Red, edges_red)) {
				validR =true;
				
			} else {
				RedInt = randomGen.nextInt();
				Red = intToSet(RedInt);
				edges_red = makeEdges(Red);
				
				
			}
		}
		while(!validW) {
			if (legalBoard(White, edges_white)) {
				validW =true;
				//System.out.println("Legal board made");
			} else {
				WhiteInt = randomGen.nextInt();
				White = intToSet(WhiteInt);
				edges_white = makeEdges(White);
				
			}
		}
		
	}

	private int[][] intToSet(int seed) {
		// alg from int to face
		// for any two bits 00 is [ -], 01 is [ ' ], 10 is [- ], 11 is [ , ] ie
		// arc from 0 to e^(i*pi/2x)
		
		int numpieces = (boardsize-1)*(boardsize-1)*2;
		int[][] face = new int[numpieces][2];
		int i = 0;
		for (; i < numpieces/2; i++) {
			if(i%16==0 && i!=0) {
				seed = randomGen.nextInt();
			}
			int horz = 1;
			int vert = 1;
			int temp = (seed << 2 * (i%16)) >> (30 - 2 * (i%16));
			if (temp >= 1) {
				horz = -1 * horz;
				vert = -1 * vert;
			}
			if (temp % 2 == 0) {
				vert = 0;
			} else
				horz = 0;
			face[i][0] = 2 * (i / (boardsize-1)) + 1 + horz;
			face[i][1] = 2 * (i % (boardsize-1)) + 1 + vert;
		}
		for (i = 0; i < numpieces/2; i++) {
			face[numpieces/2 + i][0] = 2*(i/(boardsize-1)) +1;
			face[numpieces/2 + i][1] = 2*(i%(boardsize-1)) +1;
		}

		return face;
	}
	
	private HashMap<Integer, Boolean> makeEdges(int[][] face) {
		HashMap<Integer, Boolean> list = new HashMap<Integer, Boolean>();
		int adj = 2*boardsize;
		int max = 2*boardsize -1;
		//int len = face.length;
		int i=0;
		for(i=0; i< boardsize*2-1;i++) {
			list.put(i*2+1, true);
			list.put(adj*max*2 +2*i+1, true);
			list.put(adj*2*i, true);
			list.put(adj*i*2 + 2*max, true);
		}
		for(i=0; i< face.length && face[i]!=null;i++) {
			int x= face[i][0];
			int y= face[i][1];
			if (x < adj) {
				if(list.get(2*(adj*x +y)) != null) {
					list.put(2*(adj*x +y), !list.get(2*(adj*x +y)));
				} else {
					list.put(2*(adj*x +y), true);
				}
			}
			if (y < adj) {
				if(list.get(2*(adj*x +y) +1) != null) {
					list.put(2*(adj*x +y) +1, !list.get(2*(adj*x +y) +1));
				} else {
					list.put(2*(adj*x +y)+1, true);
				}
			}
			if (x +1< adj) {
				if(list.get(2*(adj*(x+1) +y)+1) != null) {
					list.put(2*(adj*(x+1) +y)+1, !list.get(2*(adj*(x+1) +y)+1));
				} else {
					list.put(2*(adj*(x+1) +y)+1, true);
				}
			}
			if (y +1< adj) {
				if(list.get(2*(adj*x +y+1)) != null) {
					list.put(2*(adj*x +y+1), !list.get(2*(adj*x +y+1)));
				} else {
					list.put(2*(adj*x +y+1), true);
				}
			}
			              
		}
		//System.out.println(list.toString());
		return list;
	}
	

	private boolean legalBoard(int[][] face, HashMap<Integer, Boolean> edges) {
		// check if legal board
		// two properties
		// one, no value repeats:
		int i;
		int j = 1;
		for (i = 0; i < (boardsize-1)*(boardsize-1)+1; i++) {
			for (j = i + 1; j < (boardsize-1)*(boardsize-1)+1; j++) {
				if (face[i][0] == face[j][0] && face[i][1] == face[j][1]) {
					return false;
				}
			}
		}
		//two, no cycles:
		HashMap<Integer, Boolean> check = new HashMap<Integer, Boolean>();
		check.putAll(edges);
		int adj = 2*boardsize;
		int max = 2*boardsize -1;
		int count =0;
		int x0=0;
		int y0=0;
		for(; count <check.size(); count++) {
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
		if(check.containsValue(true)) {
			return false;
		}
		
		return true;
	}

	
	/*
	// private static int[][] blocked_xz_face = {{1,0}, {7,0}, {1,1}, {2,1},
	// {3,1}, {5,1}, {7,1}, {5,2}, {1,3}, {3,3}, {4,3}, {5,3}, {6,3}, {7,3},
	// {1,4}, {3,4}, {1,5}, {3,5}, {5,5}, {6,5}, {7,5}, {1,6}, {3,6}, {5,6},
	// {1,7}, {2,7}, {3,7}, {4,7}, {5,7}, {6,7}, {7,7}, {3,8}};
	// private static int[][] blocked_yz_face = {{5,0}, {1,1}, {2,1}, {3,1},
	// {5,1}, {6,1}, {7,1}, {3,2}, {5,2}, {1,3}, {3,3}, {4,3}, {5,3}, {7,3},
	// {8,3}, {1,4}, {7,4}, {1,5}, {2,5}, {3,5}, {4,5}, {5,5}, {7,5}, {3,6},
	// {7,6}, {0,7}, {1,7}, {3,7}, {4,7}, {5,7}, {6,7}, {7,7}};
	// private static int[][] blocked_xy_face = {{1,0}, {7,0}, {1,1}, {2,1},
	// {3,1}, {4,1}, {5,1}, {7,1}, {3,2}, {7,2}, {1,3}, {3,3}, {5,3}, {7,3},
	// {1,4}, {3,4}, {5,4}, {0,5}, {1,5}, {3,5}, {5,5}, {6,5}, {7,5}, {8,5},
	// {1,6}, {3,6}, {1,7}, {3,7}, {4,7}, {5,7}, {7,7}, {7,8}};
	*/
	
	private static int[][] blocked_xz_face = { { 0, 5 }, { 1, 2 }, { 1, 6 },
			{ 2, 3 }, { 2, 5 }, { 3, 4 }, { 3, 8 }, { 4, 1 }, { 4, 7 },
			{ 5, 2 }, { 5, 4 }, { 6, 3 }, { 6, 7 }, { 7, 0 }, { 7, 4 },
			{ 7, 6 }, { 1, 1 }, { 1, 3 }, { 1, 5 }, { 1, 7 }, { 3, 1 },
			{ 3, 3 }, { 3, 5 }, { 3, 7 }, { 5, 1 }, { 5, 3 }, { 5, 5 },
			{ 5, 7 }, { 7, 1 }, { 7, 3 }, { 7, 5 }, { 7, 7 } };
	private static int[][] blocked_yz_face = { { 0, 5 }, { 1, 0 }, { 1, 2 },
			{ 1, 8 }, { 2, 5 }, { 3, 4 }, { 4, 1 }, { 4, 7 }, { 5, 2 },
			{ 5, 4 }, { 5, 6 }, { 6, 1 }, { 7, 0 }, { 7, 4 }, { 7, 6 },
			{ 8, 5 }, { 1, 1 }, { 1, 3 }, { 1, 5 }, { 1, 7 }, { 3, 1 },
			{ 3, 3 }, { 3, 5 }, { 3, 7 }, { 5, 1 }, { 5, 3 }, { 5, 5 },
			{ 5, 7 }, { 7, 1 }, { 7, 3 }, { 7, 5 }, { 7, 7 } };
	private static int[][] blocked_xy_face = { { 0, 1 }, { 0, 7 }, { 1, 6 }, //red face
			{ 2, 3 }, { 3, 2 }, { 3, 4 }, { 4, 5 }, { 4, 7 }, { 5, 2 },
			{ 6, 3 }, { 6, 5 }, { 6, 7 }, { 7, 2 }, { 7, 4 }, { 7, 6 },
			{ 8, 5 }, { 1, 1 }, { 1, 3 }, { 1, 5 }, { 1, 7 }, { 3, 1 },
			{ 3, 3 }, { 3, 5 }, { 3, 7 }, { 5, 1 }, { 5, 3 }, { 5, 5 },
			{ 5, 7 }, { 7, 1 }, { 7, 3 }, { 7, 5 }, { 7, 7 } };

	/*
	 * private int[][] stardardBlocked(int n) { //returns the odd blocked
	 * squares on an n x n board int[][] standard = new int[(n/2)(n/2)][]; int
	 * i=0; int j=0; for(i=0; i< n/2 +1; i+=1) { for(j=0; j< n/2 + 1; j+=1) {
	 * standard[n/2i +j][0] = 2i +1; standard[n/2i +j][0] = 2j +1; } } return
	 * standard; }
	 */
	
	public void compute_face_sp() {
		//compute clear alleys
		alleys = new int[2*boardsize-1];
		for(int i=0; i<2*(boardsize)-1; i=i+1) {
			int goodbx = 0, goodrx = 0, goodwx = 0;
			int goodby = 0, goodry = 0, goodwy = 0;					
			for(int k=0; k < 2*(boardsize-1)*(boardsize-1); k++) {					
					if(Blue[k][0] ==i) {
						goodbx++;
					}
					if(White[k][0] ==i) {
						goodwx++;
					}
					if(Red[k][0] ==i) {
						goodrx++;
					}		
					if(Blue[k][1] ==i) {
						goodby++;
					}
					if(White[k][1] ==i) {
						goodwy++;
					}
					if(Red[k][1] ==i) {
						goodry++;
					}		
					
			}
			alleys[goodbx] = alleys[goodbx]+1;
			alleys[goodby] = alleys[goodby]+1;
			alleys[goodwx] = alleys[goodwx]+1;
			alleys[goodwy] = alleys[goodwy]+1;
			alleys[goodrx] = alleys[goodrx]+1;
			alleys[goodry] = alleys[goodry]+1;
			
			
		}
		//distance btwn start/end
		sumlindistance = Math.abs(start[1]- end[1]) + Math.abs(start[0]-end[0]) + Math.abs(start[2]-end[2]);
		//solution distance btwn start/end in 2D face
		for(int board = 0; board<3; board++) {
			int[][] puzz = new int[boardsize][boardsize];
			int indx=0, indy=2;
			if(board ==1) {
				indx = 1;
				indy = 2;
			}
			if(board ==2) {
				indx = 0;
				indy = 1;
			}
			
			puzz[start[indx]/2][start[indy]/2] =1;
			while(puzz[end[indx]/2][end[indy]/2] == 0) {
				for(int i=0; i<boardsize; i++) {
					for(int j=0; j<boardsize; j++) {
						if(puzz[i][j] != 0) {
							//System.out.println("at " + board + " " + i + "," + j + " "+ puzz[i][j]);
							//check if neighbors seen and or if can get to them.
							if(j>0 && puzz[i][j-1] ==0 && !blocked(i,j-1,i,j, board)) {
								puzz[i][j-1] = puzz[i][j]+1;
							}
							if(i>0 && puzz[i-1][j] ==0 && !blocked(i-1,j,i,j, board)) {
								puzz[i-1][j] = puzz[i][j]+1;
							}
							if(j<boardsize-1 && puzz[i][j+1] ==0 && !blocked(i,j,i,j+1, board)) {
								puzz[i][j+1] = puzz[i][j]+1;
							}
							if(i<boardsize-1 && puzz[i+1][j] ==0 && !blocked(i,j,i+1,j, board)) {
								puzz[i+1][j] = puzz[i][j]+1;
							}
						}
					}
				}
				
			}
			sumsoldistance += puzz[end[indx]/2][end[indy]/2];
			//System.out.println("end " + board + " " + puzz[end[indx]/2][end[indy]/2]);
		}
		//calculate linear walls- the number of walls that are linear
		for(int b = 0; b<3; b++) {
			for(int i=0; i<boardsize-1; i++) {
				boolean linear = true, gap = false;
				for(int j=0; j<2*boardsize-1; j++) {
					
					if(!blocked(i,j,i,0,b) || i==boardsize-1) {
						gap = true;
					} else if(i>0) {
						gap = false;
					}
					if(i>0)
						if(blocked(i-1,j,i,0,b))
							linear = false;
					if(i<boardsize-2)
						if(blocked(i+1,j,i,0,b))
							linear = false;
					if(gap==true && linear ==true)
						linearwalls= linearwalls +1;
				}
			}
			for(int j=0; j<boardsize-1; j++) {
				boolean linear = true, gap = false;
				for(int i=0; i<2*boardsize-1; i++) {
					
					if(!blocked(i,j,i,0,b) || i==boardsize-1) {
						gap = true;
					} else if(i>0) {
						gap = false;
					}
					if(i>0)
						if(blocked(i-1,j,i,0,b))
							linear = false;
					if(i<boardsize-2)
						if(blocked(i+1,j,i,0,b))
							linear = false;
					if(gap==true && linear ==true)
						linearwalls= linearwalls +1;
				}
			}
		}
		
		
	}
	public boolean blocked(int x,int y,int z,int w, int board) {
		boolean ans= false;
		for(int k=0; k< 2*(boardsize-1)*(boardsize-1); k++) {
			if(board== 0 && Blue[k][0] == x+z && Blue[k][1] == y+w) {
				ans = true;
			}
			if(board ==1 && White[k][0] ==x+z && White[k][1] ==y+w) {
				ans = true;
			}
			if(board ==2 && Red[k][0] ==x+z && Red[k][1] ==y+w) {
				ans = true;
			}
		}
		return ans;
	}
	//Face related cube metrics
	public int[] alleys;
	public int sumlindistance=0;
	public int sumsoldistance=0;
	public int linearwalls =0;
	
	
	public int[] start = { 0,0,0 };
	public int[] end = { 1,1,1 };
	public int boardsize = 5;
	//Solve related cube metrics
	public int bushiness =0;
	public int branches = 0;
	public int brfactor = 0;
	public int subcomponents = 0;
	public int maxbrfactor = 0;
	public int turns = 0;
	public int planeTurns =0;
	public int remoteness;
	public int branchturns =0;
	
	public boolean findbest = false;
	public boolean findbestc = false;
	
	//board generating variables
	private Random randomGen;
	public HashMap<Integer, Boolean> edges_blue;
	public HashMap<Integer, Boolean> edges_red;
	public HashMap<Integer, Boolean> edges_white;
	public int BlueInt;
	public int WhiteInt;
	public int RedInt;
	public int[][] Blue;
	public int[][] White;
	public int[][] Red;
	public boolean validB;
	public boolean validR;
	public boolean validW;
	public boolean original = true;
	
	public boolean loudprint = false;
	
	public int probabilistic() {
		int i =0, count=0;
		randomGen = new Random();
		for(;i<100000; i=i+1) {
			int j = randomGen.nextInt();
			int[][] intset = intToSet(j);
			HashMap<Integer, Boolean> edges_i = makeEdges(intset);
			if(legalBoard(intset, edges_i)) {
				count= count +1;
			}
			//if((i/10000)*10000 ==i) 
			//	System.out.println("i at" + i);
		}
		return count;
	}
	
	public static void main(String[] args) {
		CubeGen cube = new CubeGen(false, false, false, 7);
		System.out.println(cube.probabilistic());
		
	}
	

}
