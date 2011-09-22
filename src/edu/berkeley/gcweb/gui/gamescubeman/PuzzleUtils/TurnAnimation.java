package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.util.ArrayList;

public class TurnAnimation {
	private ArrayList<PuzzleTurn> independentTurns = new ArrayList<PuzzleTurn>();
	private TwistyPuzzle puzzle;
	public TurnAnimation(TwistyPuzzle p) {
		puzzle = p;
	}
	public TurnAnimation(TwistyPuzzle p, PuzzleTurn t) {
		this(p);
		independentTurns.add(t);
	}
	//merges Turn t with this animation and returns true if possible, false otherwise
	public boolean mergeTurn(PuzzleTurn t) {
		for(PuzzleTurn turn : independentTurns)
			if(!turn.isAnimationMergeble(t))
				return false;
		
		independentTurns.add(t);
		return true;
	}
	public boolean isEmpty() {
		return independentTurns.isEmpty();
	}
	//returns true when finished animating
	public ArrayList<PuzzleTurn> animate() {
		ArrayList<PuzzleTurn> finishedTurns = new ArrayList<PuzzleTurn>();
		//need to iterate through a shallow copy to be able to remove
		//finished animations from the original
		for(PuzzleTurn t : new ArrayList<PuzzleTurn>(independentTurns)) {
			if(t.animateMove()) {
				t.updateInternalRepresentation(true);
				independentTurns.remove(t);
				finishedTurns.add(t);
			}
		}
		puzzle.fireCanvasChange();
		return finishedTurns;
	}
}
