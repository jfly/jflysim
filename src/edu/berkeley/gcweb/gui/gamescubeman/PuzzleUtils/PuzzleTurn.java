package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

public abstract class PuzzleTurn {
	protected int frames;
	private boolean animationStarted;
	public PuzzleTurn(int frames_animation) {
		frames = frames_animation;
	}
	public abstract PuzzleTurn invert();
	//returns true if this turn is legal during inspection
	public abstract boolean isInspectionLegal();
	//returns true if this turn has no visible effect on the puzzle
	//(a cube rotation would return false here)
	public abstract boolean isNullTurn();
	//if the moves are mergeable into one move (ex: R + R = R2), returns that move, otherwise null
	public abstract PuzzleTurn mergeTurn(PuzzleTurn other);
	//returns true if this moves are animatable simultaneously (ex: R & L, x & R, x & r, but not R & R)
	public abstract boolean isAnimationMergeble(PuzzleTurn other);
	//returns true when the move has finished animating
	public final boolean animateMove() {
		_animateMove(!animationStarted);
		animationStarted = true;
		return --frames == 0;
	}
	public abstract void _animateMove(boolean firstFrame);
	//we want to separate updating the internal representation from the state of the polygons
	//to deal with bandaged puzzles such as square one, because doTurn() (in TwistyPuzzle) needs to know if a turn
	//is legal without waiting for the queued animations to occur
	public abstract void updateInternalRepresentation(boolean polygons);
	public abstract String toString();
}
