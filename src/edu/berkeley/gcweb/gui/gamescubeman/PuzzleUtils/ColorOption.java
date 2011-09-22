package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.awt.Color;

import javax.swing.JPanel;

public class ColorOption extends PuzzleOption<Color> {
	private Color color;
	public ColorOption(String name, boolean guify, Color def) {
		super(name, guify);
		color = def;
	}
	
	@Override
	public JPanel getComponent() {
		return null;
	}

	@Override
	public Color getValue() {
		return color;
	}
	
	@Override
	public String valueToString() {
		return color.getRGB() + "";
	}

	@Override
	public void setValue(String val) {
		color = Utils.stringToColor(val, true);
	}
}
