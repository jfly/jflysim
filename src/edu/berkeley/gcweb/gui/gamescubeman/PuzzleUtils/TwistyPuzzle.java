package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Timer;

import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleOption.PuzzleOptionChangeListener;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.RotationMatrix;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Shape3D;

public abstract class TwistyPuzzle extends Shape3D implements ActionListener, PuzzleStateChangeListener, PuzzleOptionChangeListener {
	public TwistyPuzzle(double x, double y, double z) {
		super(x, y, z);
		addStateChangeListener(this);
	}

	private boolean bld = false;
	public void setBLDMode(boolean bld) {
		this.bld = bld;
	}
	
	@Override
	public ArrayList<Polygon3D> getPolygons() {
		ArrayList<Polygon3D> polys = super.getPolygons();
		if(bld) {
			for(Polygon3D poly : polys) {
				if(!(poly instanceof PuzzleSticker))
					throw new Error();
				PuzzleSticker ps = (PuzzleSticker) poly;
				ps.setFace(null);
			}
		}
		return polys;
	}
	
	public void resetPuzzle() {
		scrambling = false;
		resetTimer();
		createPolys(false);
		fireStateChanged(null);

		Integer val = frames_animation.getValue();
		frames_animation.setValue(1);
		doTurns(initialization, reverse);
		frames_animation.setValue(val);
		
		queueIndex = 0;
	}
	
	private ArrayList<PuzzleTurn> turns = new ArrayList<PuzzleTurn>();
	public ArrayList<PuzzleTurn> getTurnHistory() {
		return turns;
	}
	protected void appendTurn(PuzzleTurn nextTurn) {
		nextTurn.updateInternalRepresentation(false);
		PuzzleTurn lastTurn = turns.isEmpty() ? null : turns.remove(turns.size() - 1);
		PuzzleTurn newTurn = lastTurn == null ? nextTurn : nextTurn.mergeTurn(lastTurn);
		if(newTurn == null) {
			turns.add(lastTurn);
			turns.add(nextTurn);
		} else if(!newTurn.isNullTurn())
			turns.add(newTurn);
		if(animationQueue.isEmpty())
			animationQueue.add(new TurnAnimation(this));
		TurnAnimation lastAnim = animationQueue.get(animationQueue.size() - 1);
		if(!lastAnim.mergeTurn(nextTurn))
			animationQueue.add(new TurnAnimation(this, nextTurn));
		turner.start();
	}
	
	private Timer turner = new Timer(10, this);
	private ArrayList<TurnAnimation> animationQueue = new ArrayList<TurnAnimation>();
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == turner) {
			TurnAnimation anim = animationQueue.get(0);
			//this is to detect when scrambling is finished
			if(anim == END_SCRAMBLING) {
				scrambling = false;
				startInspection();
			}
			for(PuzzleTurn finished : anim.animate()) {
				fireStateChanged(finished);
			}
			if(anim.isEmpty()) {
				animationQueue.remove(0);
				if(animationQueue.isEmpty()) {
					turner.stop();
					if(playing)
						forward();
				}
			}
		} else if(e.getSource() == timer) {
			updateTimeDisplay();
		}
	}
	
	private void updateTimeDisplay() {
		canvas.setDisplayString(isInspecting() ? Color.RED : Color.BLACK, getTime());
		canvas.repaint();
	}

	private ArrayList<PuzzleStateChangeListener> stateListeners = new ArrayList<PuzzleStateChangeListener>();
	public void addStateChangeListener(PuzzleStateChangeListener l) {
		stateListeners.add(l);
	}
	public void fireStateChanged(PuzzleTurn turn) {
		for(PuzzleStateChangeListener l : stateListeners)
			l.puzzleStateChanged(this, turn);
		fireCanvasChange();
	}
	
	private ArrayList<PuzzleTimerListener> timerListeners = new ArrayList<PuzzleTimerListener>();
	public void addPuzzleTimerListener(PuzzleTimerListener l) {
		timerListeners.add(l);
	}
	public void fireInspectionStarted(String scramble) {
		for(PuzzleTimerListener l : timerListeners)
			l.inspectionStarted(scramble);
	}
	public void fireTimerStarted() {
		for(PuzzleTimerListener l : timerListeners)
			l.timerStarted();
	}
	public void fireTimerReset() {
		if(!isTiming() && !isInspecting())
			return;
		for(PuzzleTimerListener l : timerListeners)
			l.timerReset();
	}
	public void fireTimerStopped(double time) {
		for(PuzzleTimerListener l : timerListeners)
			l.timerStopped(time);
	}

	private final TurnAnimation END_SCRAMBLING = new TurnAnimation(this);
	private boolean scrambling = false;
	public final void scramble() {
		resetPuzzle();
		scrambling = true;
		_scramble();
		animationQueue.add(END_SCRAMBLING);
	}
	
	public final void createPolys(boolean copyOld) {
		if(!copyOld) {
			turns.clear();
			animationQueue.clear();
			turner.stop();
		}
		clearPolys();
		_createPolys(copyOld);
	}
	
	protected SliderOption frames_animation = new SliderOption("frames/animation", true, 5, 1, 100);
	{
		frames_animation.setSilent(true);
	}
	
	public int getFramesPerAnimation() {
		return frames_animation.getValue();
	}
	
	public final List<PuzzleOption<?>> getDefaultOptions() {
		List<PuzzleOption<?>> options = _getDefaultOptions();
		options.add(0, frames_animation);
		return options;
	}
	
	private String[] initialization = new String[0];
	private boolean reverse = false;
	public final void initialize(String turns, boolean reverse) {
		if(turns == null) return;
		if(turns.equals("#")) {
			initialization = queuedTurns;
		} else {
			initialization = turns.split(" ");
		}
		this.reverse = true;
		resetPuzzle();
	}
	
	private final void doTurns(String[] turns, boolean reverse) {
		String[] copy = turns.clone(); //this method will get called multiple times
		if(reverse) Utils.reverse(copy);
		for(String turn : copy) {
			_doTurn(turn, reverse);
		}
	}
	
	private int queueIndex = 0;
	private String[] queuedTurns = new String[0];
	public final void queueTurns(String turns) {
		if(turns != null)
			this.queuedTurns = turns.split(" ");
	}
	
	private boolean playing = false;
	public final void playPause() {
		//TODO - deal with key input =(
		playing = !playing;
		if(playing)
			forward();
	}
	public boolean isPlaying() {
		return playing;
	}
	public final void forward() {
		if(queueIndex < queuedTurns.length)
			_doTurn(queuedTurns[queueIndex++], false);
		else
			playing = false;
	}
	public final int getRemaining() {
		return queuedTurns.length - queueIndex;
	}
	public final int getCompleted() {
		return queueIndex;
	}
	
	public final void backward() {
		playing = false;
		if(queueIndex > 0)
			_doTurn(queuedTurns[--queueIndex], true);
	}
	
	//*** To implement a custom twisty puzzle, you must override the following methods and provide a noarg constructor ***
	protected abstract String getPuzzleName();
	
	protected abstract void _createPolys(boolean copyOld);
	protected abstract void _scramble();
	protected abstract void _cantScramble();
	protected abstract boolean _doTurn(String turn, boolean inverse);
	
	public abstract List<PuzzleOption<?>> _getDefaultOptions();

	public abstract String getState();
	public abstract boolean isSolved();
	public abstract HashMap<String, Color> getDefaultColorScheme();
	//this should be used set the default angle to view the puzzle from (using Shape3D's setRotation() method)
	public abstract RotationMatrix[] getPreferredViewAngles();
	
	//*** END TWISTY PUZZLE IMPLEMENTATION ***
	
	private KeyCustomizerPanel keyPanel;
	public void setKeyCustomizerPanel(KeyCustomizerPanel keyPanel) {
		this.keyPanel = keyPanel;
	}
	
	private boolean disabled = false;
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public final void doTurn(KeyEvent e) {
		if(!disabled && keyPanel != null)
			doTurn(keyPanel.getTurnForKey(e));
	}
	public void puzzleStateChanged(TwistyPuzzle src, PuzzleTurn turn) {
		//this will start the timer if we're currently inspecting
		if(!scrambling && isInspecting() && turn != null && !turn.isInspectionLegal()) {
			start = System.currentTimeMillis() - INSPECTION_TIME*1000;
			fireTimerStarted();
		}
		if(src.isSolved() && isTiming())
			stopTimer();
	}
	private static final int INSPECTION_TIME = 15;
	private static final DecimalFormat df = new DecimalFormat("0.00");
	private String getTime() {
		if(start == -1)
			return "";
		if(isInspecting())
			return "" + (int)Math.ceil(getCountdownSeconds());
		return "" + df.format(getElapsedTime()/1000.);
	}
	private long start = -1, stop = -1;
	private void stopTimer() {
		if(!isTiming()) return;
		stop = System.currentTimeMillis();
		timer.stop();
		updateTimeDisplay();
		fireTimerStopped(getElapsedTime()/1000.);
	}
	private long getElapsedTime() {
		if(start == -1) return 0;
		long time = stop;
		if(stop == -1)
			time = System.currentTimeMillis();
		return time - start - 1000*INSPECTION_TIME;
	}
	private double getCountdownSeconds() {
		return -getElapsedTime()/1000.;
	}
	private boolean isTiming() {
		return start != -1 && stop == -1;
	}
	private boolean isInspecting() {
		return start != -1 && getCountdownSeconds() > 0;
	}
	private void startInspection() {
		fireInspectionStarted(Utils.join(" ", getTurnHistory().toArray()));
		start = System.currentTimeMillis();
		stop = -1;
		timer.start();
	}
	private void resetTimer() {
		fireTimerReset();
		stop = start = -1;
		timer.stop();
		updateTimeDisplay();
	}
	private Timer timer = new Timer(100, this);
	//returns true if the String was recognized as a turn
	public final boolean doTurn(String turn2) {
//		if(turn == null || scrambling) return false;
//		if(turn.equals("scramble")) {
//			if(!isInspecting() && !isTiming())
//				scramble();
//			else
//				_cantScramble();
//			return true;
//		}
//		if(turn.equals("reset")) {
//			resetPuzzle();
//			return true;
//		}
//		if(turn.equals("undo")) {
//			if(!turns.isEmpty()) {
//				PuzzleTurn lastTurn = turns.get(turns.size()-1);
//				appendTurn(lastTurn.invert());
//			}
//			return true;
//		}
//		return _doTurn(turn, false);
		
//		//TODO - hacked together for hackathon 2010 =)
		if(turn2 == null || scrambling) return false;
		for(String turn : turn2.split(" ")) {
			if(turn.equals("scramble")) {
				if(!isInspecting() && !isTiming())
					scramble();
				else
					_cantScramble();
				continue;
			}
			if(turn.equals("reset")) {
				resetPuzzle();
				continue;
			}
			if(turn.equals("undo")) {
				if(!turns.isEmpty()) {
					PuzzleTurn lastTurn = turns.get(turns.size()-1);
					appendTurn(lastTurn.invert());
				}
				continue;
			}
			//TODO - hacked together for hackathon 2010 =)
			_doTurn(turn, false);
		}
		return true;
	}
	
	public boolean piecePickerSupport(){
		//if 2x2x2 return true
		return false;
	}
}
