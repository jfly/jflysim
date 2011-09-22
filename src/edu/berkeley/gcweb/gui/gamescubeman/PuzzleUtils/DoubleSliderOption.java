package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DoubleSliderOption extends PuzzleOption<Double> implements ChangeListener {
	private JPanel pane;
	private JSlider slider;
	private Integer denominator;
	public DoubleSliderOption(String name, boolean guify, Integer defNumerator, Integer min, Integer max, Integer denominator) {
		super(name, guify);
		this.denominator = denominator;
		
		slider = new JSlider(min, max, defNumerator);
		slider.setFocusable(false);
		slider.addChangeListener(this);
		
		pane = Utils.sideBySide(new JLabel(name), slider);
	}
	
	@Override
	public JPanel getComponent() {
		return pane;
	}

	@Override
	public Double getValue() {
		return (double) slider.getValue() / denominator;
	}

	@Override
	public void setValue(String val) {
		try {
			slider.setValue(Integer.parseInt(val));
		} catch(NumberFormatException e) {}
	}

	@Override
	public String valueToString() {
		return slider.getValue()+"";
	}

	public void stateChanged(ChangeEvent e) {
		fireOptionChanged();
	}
}
