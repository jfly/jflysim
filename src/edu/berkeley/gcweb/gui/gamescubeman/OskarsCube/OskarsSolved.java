package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import java.util.ArrayList;

public class OskarsSolved {
	private int min_remoteness = 50;
	
	private CubeGen cubefaces;
	private Solver solve;

	public OskarsSolved(int blue, int white, int red, int tosolve, boolean random, String filename) {
		System.out.println("b\tw\tr\tremoteness\tsubcomponents\tbushiness\tbranches\tbrfactor\tmaxbr\tturns\tplaneturns\tsumlindist\tsolve distance\tachievable\talleys");
		
		if (!random) {
			for(int r = red; r < red + tosolve; r++) {
				for(int w = white; w < white + tosolve; w++) {
					for(int b = blue; b < blue + tosolve; b++) {
						cubefaces = new CubeGen(r,w,b);
						ArrayList<Integer> vals = new ArrayList<Integer>();
						if(cubefaces.original) {
							solve = new Solver(cubefaces);
							if (cubefaces.remoteness > min_remoteness) {
								vals.add(b);
								vals.add(w);
								vals.add(r);
								vals.add(cubefaces.remoteness);
								vals.add(cubefaces.subcomponents);
								vals.add(cubefaces.bushiness);
								vals.add(cubefaces.branches);
								vals.add(cubefaces.brfactor);
								vals.add(cubefaces.maxbrfactor);
								vals.add(cubefaces.turns);
								vals.add(cubefaces.planeTurns);
								vals.add(cubefaces.sumlindistance);
								vals.add(cubefaces.sumsoldistance);
								vals.add(OskarsCube.acheivable);
								for(int i =0; i< cubefaces.boardsize*2-1; i++)
									vals.add(cubefaces.alleys[i]);
								found_one(vals );
								
							}
						}
							
					}
				}
			}
		} else {
			for (int i = 0; i < tosolve; i++) {
				cubefaces = new CubeGen(true,true,true, 5);
				solve = new Solver(cubefaces);
				ArrayList<Integer> vals = new ArrayList<Integer>();
				if (cubefaces.remoteness > min_remoteness) {
					vals.add(cubefaces.BlueInt);
					vals.add(cubefaces.WhiteInt);
					vals.add(cubefaces.RedInt);
					vals.add(cubefaces.remoteness);
					vals.add(cubefaces.subcomponents);
					vals.add(cubefaces.bushiness);
					vals.add(cubefaces.branches);
					vals.add(cubefaces.brfactor);
					vals.add(cubefaces.maxbrfactor);
					vals.add(cubefaces.turns);
					vals.add(cubefaces.planeTurns);
					vals.add(cubefaces.sumlindistance);
					vals.add(cubefaces.sumsoldistance);
					vals.add(OskarsCube.acheivable);
					for(int j =0; j< cubefaces.boardsize*2-1; j++)
						vals.add(cubefaces.alleys[j]);
					found_one(vals);
				}
			}
		}
	}
	public static void main(String[] args) {
		new OskarsSolved(0,0,0,1200000, true, "default"); //600000 4 hours
		//OskarsSolved osolve = new OskarsSolved(0,0,0,1000, false, "default");
	}
		
			
			
					
					
				
	private void found_one(ArrayList<Integer> vals) {
		String tout = "";
		for(int i =0; i< vals.size(); i++) {
			tout = tout + vals.get(i);
			if (i< vals.size()-1) {
				tout = tout + "\t";
			}
		}
		System.out.println(tout);
	}	
		
}
