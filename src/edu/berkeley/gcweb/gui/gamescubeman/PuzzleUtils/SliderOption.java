package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderOption extends PuzzleOption<Integer> implements ChangeListener {
	private JPanel pane;
	private JSlider slider;
	public SliderOption(String name, boolean guify, Integer def, Integer min, Integer max) {
		super(name, guify);
		
		slider = new JSlider(min, max, def);
		slider.setFocusable(false);
		slider.addChangeListener(this);
		
		pane = Utils.sideBySide(new JLabel(name), slider);
	}
	
	@Override
	public JPanel getComponent() {
		return pane;
	}

	@Override
	public Integer getValue() {
		return slider.getValue();
	}

	@Override
	public void setValue(String val) {
		try {
			setValue(Integer.parseInt(val));
		} catch(NumberFormatException e) {}
	}
	
	public void setValue(Integer val) {
		slider.setValue(val);
	}
	
	@Override
	public String valueToString() {
		return getValue().toString();
	}

	public void stateChanged(ChangeEvent e) {
		fireOptionChanged();
	}
}
