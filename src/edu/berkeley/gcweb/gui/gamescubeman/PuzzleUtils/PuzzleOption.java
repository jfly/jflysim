package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.util.ArrayList;

import javax.swing.JComponent;

public abstract class PuzzleOption<H> {
	private final String name;
	private final boolean guify;
	
	public PuzzleOption(String name, boolean guify) {
		this.name = name;
		this.guify = guify;
	}
	
	private boolean silentOption = false;
	public void setSilent(boolean silent) {
		silentOption = silent;
	}
	
	public boolean isGuifiable() {
		return guify;
	}
	
	public String getName() {
		return name;
	}
	
	//this could come from anywhere, possibly as an applet parameter or a url argument
	public abstract void setValue(String val);
	public abstract String valueToString();
	
	public abstract H getValue();
	public abstract JComponent getComponent();
	
	private ArrayList<PuzzleOptionChangeListener> listeners = new ArrayList<PuzzleOptionChangeListener>();
	
	public final void addChangeListener(PuzzleOptionChangeListener l) {
		listeners.add(l);
	}
	
	public final void fireOptionChanged() {
		if(silentOption) return;
		for(PuzzleOptionChangeListener l : listeners)
			l.puzzleOptionChanged(this);
	}
	
	public static abstract interface PuzzleOptionChangeListener {
		public abstract void puzzleOptionChanged(PuzzleOption<?> src);
	}
}
