package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class CheckBoxOption extends PuzzleOption<Boolean> implements ActionListener {
	private JCheckBox slider;
	public CheckBoxOption(String name, boolean guify, Boolean def) {
		super(name, guify);
		
		slider = new JCheckBox(name, def);
		slider.setFocusable(false);
		slider.addActionListener(this);
	}
	
	@Override
	public JCheckBox getComponent() {
		return slider;
	}

	@Override
	public Boolean getValue() {
		return slider.isSelected();
	}
	
	@Override
	public String valueToString() {
		return getValue().toString();
	}

	@Override
	public void setValue(String val) {
		slider.setSelected(Utils.parseBoolean(val, slider.isSelected()));
	}
	
	public void actionPerformed(ActionEvent e) {
		fireOptionChanged();
	}
}
