package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class RollingJPanel extends JPanel {
	private Timer t;
	public RollingJPanel() {
		setOpaque(false);
		t = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == t) {
					double increment = direction * Math.abs(preferredHeight - visible) / 5.;
					if(Math.round(increment) == 0)
						increment = direction;
					visible += increment;
					if(visible >= preferredHeight) {
						visible = preferredHeight;
						t.stop();
					} else if(visible <= 0) {
						visible = 0;
//						RollingJPanel.this.super.setVisible(false);
						t.stop();
					}
					updateSize();
				}
			}
		});
		super.setVisible(false);
		addMouseListener(new MouseAdapter() {}); //this is to prevent key presses from falling through the tab
		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
				getParent().addComponentListener(new ComponentListener() {
					public void componentHidden(ComponentEvent e) {}
					public void componentMoved(ComponentEvent e) {}
					public void componentResized(ComponentEvent e) {
						updateSize();
					}
					public void componentShown(ComponentEvent e) {}
				});
			}
			public void ancestorMoved(AncestorEvent event) {}
			public void ancestorRemoved(AncestorEvent event) {}
		});
	}
	
	private int preferredHeight;
	private double visible;
	private int direction;
	public void setVisible(boolean visible) {
		if(getParent() != null) //this is to get the jpanel rolling down on top
			getParent().setComponentZOrder(this, visible ? 0 : 1);
		direction = visible ? 1 : -1;
		if(visible != isVisible()) {
			if(visible) {
				preferredHeight = Math.min(getPreferredSize().height, getParent().getHeight());
				super.setVisible(true);
			}
			else if(getParent() != null) {
				getParent().setCursor(Cursor.getDefaultCursor());
				firePropertyChange("visibility", !visible, visible);
			}
		}
		t.start();
	}
	
	private void updateSize() {
		setBounds(0, 0, getParent().getWidth(), (int) visible);
		validate();
	}
}
