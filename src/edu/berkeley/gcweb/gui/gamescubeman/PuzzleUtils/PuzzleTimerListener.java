package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

public interface PuzzleTimerListener {
	public void timerStopped(double time);
	public void timerReset();
	public void inspectionStarted(String scramble);
	public void timerStarted();
}
