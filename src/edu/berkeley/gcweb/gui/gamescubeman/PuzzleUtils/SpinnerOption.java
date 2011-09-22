package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpinnerOption extends PuzzleOption<Integer> implements ChangeListener {
	private JPanel pane;
	private JSpinner spinner;
	public SpinnerOption(String name, boolean guify, Integer def, Integer min, Integer max, Integer step) {
		super(name, guify);
		
		spinner = new JSpinner(new SpinnerNumberModel(def, min, max, step));
		((JSpinner.NumberEditor)spinner.getEditor()).getTextField().setFocusable(false);
		spinner.addChangeListener(this);
		
		pane = Utils.sideBySide(new JLabel(name), spinner);
	}
	
	@Override
	public JPanel getComponent() {
		return pane;
	}

	@Override
	public Integer getValue() {
		return (Integer) spinner.getValue();
	}

	@Override
	public String valueToString() {
		return getValue().toString();
	}
	
	@Override
	public void setValue(String val) {
		try {
			spinner.setValue(Integer.parseInt(val));
		} catch(NumberFormatException e) {}
	}

	public void stateChanged(ChangeEvent e) {
		fireOptionChanged();
	}
}
