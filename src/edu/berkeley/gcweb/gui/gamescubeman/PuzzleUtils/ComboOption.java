package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ComboOption extends PuzzleOption<String> implements ActionListener {
	private JPanel pane;
	private JComboBox dropdown;
	public ComboOption(String name, boolean guify, String def, String[] choices) {
		super(name, guify);
		
		dropdown = new JComboBox(choices);
		dropdown.setFocusable(false);
		dropdown.addActionListener(this);
		
		pane = Utils.sideBySide(new JLabel(name), dropdown);
	}
	
	@Override
	public JPanel getComponent() {
		return pane;
	}

	@Override
	public String getValue() {
		return (String) dropdown.getSelectedItem();
	}
	
	@Override
	public String valueToString() {
		return getValue();
	}

	@Override
	public void setValue(String val) {
		dropdown.setSelectedItem(val);
	}

	public void actionPerformed(ActionEvent e) {
		fireOptionChanged();
	}
}
