package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class KeyCustomizerPanel extends RollingJPanel implements ActionListener {
	private static final char[][] QWERTY_LOWER = {
		{ '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' },
		{ 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p' },
		{ 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', '\'' },
		{ 'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/' },
		{ ' ' }
	};
	private static final char[][] QWERTY_UPPER = {
		{ '!', '@', '#', '$', '%', '^', '&', '*', '(', ')' },
		{ 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P' },
		{ 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', ':', '"' },
		{ 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '<', '>', '?' },
		{ ' ' }
	};
	private static final char[][] DVORAK_LOWER = {
		{ '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' },
		{'\'', ',', '.', 'p', 'y', 'f', 'g', 'c', 'r', 'l' },
		{ 'a', 'o', 'e', 'u', 'i', 'd', 'h', 't', 'n', 's', '-' },
		{ ';', 'q', 'j', 'k', 'x', 'b', 'm', 'w', 'v', 'z' },
		{ ' ' }
	};
	private static final char[][] DVORAK_UPPER = {
		{ '!', '@', '#', '$', '%', '^', '&', '*', '(', ')' },
		{ '"', '<', '>', 'P', 'Y', 'F', 'G', 'C', 'R', 'L' },
		{ 'A', 'O', 'E', 'U', 'I', 'D', 'H', 'T', 'N', 'S', '_' },
		{ ':', 'Q', 'J', 'K', 'X', 'B', 'M', 'W', 'V', 'Z' },
		{ ' ' }
	};
	private static final String QWERTY_LAYOUT = "QWERTY";
	private static final HashMap<String, char[][][]> KEYBOARD_LAYOUTS = new HashMap<String, char[][][]>();
	static {
		KEYBOARD_LAYOUTS.put(QWERTY_LAYOUT, new char[][][] { QWERTY_LOWER, QWERTY_UPPER });
		KEYBOARD_LAYOUTS.put("DVORAK", new char[][][] { DVORAK_LOWER, DVORAK_UPPER });
	}
	
	private static final int KEYBOARD_HEIGHT = QWERTY_LOWER.length;
	
	private Properties keyLayoutBackup, qwertyKeyLayout;
	private final JButton reset;
	private final KeyEditor[][][] lowerUpperKeyEditors;
	private final ButtonGroup keyLayoutButtons;
	private final AppletSettings settings;
	public KeyCustomizerPanel(TwistyPuzzle puzzle, AppletSettings settings) {
		this.settings = settings;
		puzzle.setKeyCustomizerPanel(this);
		qwertyKeyLayout = new Properties();
		try {
			qwertyKeyLayout.load(puzzle.getClass().getResourceAsStream("keys.properties"));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(NullPointerException e) {
			System.err.println("keys.properties not found!");
			e.printStackTrace();
		}
		keyLayoutBackup = (Properties) qwertyKeyLayout.clone();
		
		loadKeys();
				
		JTabbedPane lowerUpperPane = new JTabbedPane();
		lowerUpperKeyEditors = new KeyEditor[2][KEYBOARD_HEIGHT][];
		for(int i=0; i<lowerUpperKeyEditors.length; i++) {
			KeyEditor[][] keyEditors = lowerUpperKeyEditors[i];
			JPanel keyPanel = new JPanel();
			lowerUpperPane.add(keyPanel, i == 0 ? "Lowercase" : "Uppercase");
			keyPanel.setLayout(new GridLayout(0, 1));
			for(int row=0; row<KEYBOARD_HEIGHT; row++) {
				JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
				keyPanel.add(rowPanel);
				rowPanel.add(new KeyEditor(row));
				lowerUpperKeyEditors[i][row] = new KeyEditor[QWERTY_LOWER[row].length];
				for(int col=0; col<lowerUpperKeyEditors[i][row].length; col++) {
					keyEditors[row][col] = new KeyEditor();
					rowPanel.add(keyEditors[row][col]);
				}
				rowPanel.add(new KeyEditor(QWERTY_LOWER.length-row));
			}
		}
		
		reset = new JButton("Reset");
		reset.setFocusable(false);
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				qwertyKeyLayout = (Properties) keyLayoutBackup.clone();
				keysChanged();
				saveKeys();
			}
		});
		
		ArrayList<JComponent> layouts = new ArrayList<JComponent>();
		keyLayoutButtons = new ButtonGroup();
		for(String layout : KEYBOARD_LAYOUTS.keySet()) {
			//TODO - store/read to/from cookie
			JRadioButton button = new JRadioButton(layout, layout.equals(QWERTY_LAYOUT));
			button.setActionCommand(layout);
			button.setFocusable(false);
			button.addActionListener(this);
			layouts.add(button);
			keyLayoutButtons.add(button);
		}
		keysChanged(); //force the layout to update

		setLayout(new BorderLayout());
		add(lowerUpperPane, BorderLayout.CENTER);
		layouts.add(0, reset);
		add(Utils.sideBySide(layouts.toArray(new JComponent[0])), BorderLayout.PAGE_END);
		
		MouseListener ml = new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				for(KeyEditor[][] keyEditor : lowerUpperKeyEditors)
					for(KeyEditor[] row : keyEditor)
						for(KeyEditor ed : row)
							ed.setEditing(ed.getMousePosition() != null);
			}

			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		};
		setMouseListener(this, ml);
		MouseMotionListener mml = new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {}

			public void mouseMoved(MouseEvent e) {
				for(KeyEditor[][] keyEditor : lowerUpperKeyEditors)
					for(KeyEditor[] row : keyEditor)
						for(KeyEditor ed : row)
							ed.updateBorder();
			}
		};
		setMouseMotionListener(this, mml);
		addMouseListener(new MouseListener() { //TODO - is this necessary?
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {
				for(KeyEditor[][] keyEditor : lowerUpperKeyEditors)
					for(KeyEditor[] row : keyEditor)
						for(KeyEditor ed : row)
							ed.updateBorder();
			
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
	}
	
	public void actionPerformed(ActionEvent e) {
		keysChanged();
	}

	private void setMouseMotionListener(JComponent comp, MouseMotionListener mml) {
		comp.addMouseMotionListener(mml);
		for(Component child : comp.getComponents()) {
			if(child instanceof JComponent)
				setMouseMotionListener((JComponent)child, mml);
			else {
				child.addMouseMotionListener(mml);
			}
		}
	}
	private void setMouseListener(JComponent comp, MouseListener ml) {
		comp.addMouseListener(ml);
		for(Component child : comp.getComponents()) {
			if(child instanceof JComponent)
				setMouseListener((JComponent)child, ml);
			else {
				child.addMouseListener(ml);
			}
		}
	}
	
	//attempts to load the cookie with the previous keyboard layout, if it exists
	private void loadKeys() {
		Properties newQwertyKeyLayout = null;
		for(char[][] keyboard : KEYBOARD_LAYOUTS.get(QWERTY_LAYOUT)) {
			for(char[] row : keyboard) {
				for(char key : row) {
					String turn = settings.get("key_" + key, null);
					if(turn != null) {
						if(newQwertyKeyLayout == null) newQwertyKeyLayout = new Properties();
						newQwertyKeyLayout.setProperty(key + "", turn);
					}
				}
			}
		}
		if(newQwertyKeyLayout != null)
			qwertyKeyLayout = newQwertyKeyLayout;
	}
	private void saveKeys() {
		for(char[][] keyboard : KEYBOARD_LAYOUTS.get(QWERTY_LAYOUT)) {
			for(char[] row : keyboard) {
				for(char key : row) {
					settings.set("key_" + key, qwertyKeyLayout.getProperty("" + key));
				}
			}
		}
	}
	
	private String getSelectedLayout() {
		return keyLayoutButtons.getSelection().getActionCommand();
	}
	
	private void keysChanged() {
		char[][][] keyLayout = KEYBOARD_LAYOUTS.get(getSelectedLayout());
		for(int i = 0; i < keyLayout.length; i++) {
			KeyEditor[][] keyEditors = lowerUpperKeyEditors[i];
			for(int r=0; r<keyEditors.length; r++) {
				for(int c=0; c<keyEditors[r].length; c++) {
					keyEditors[r][c].setKey(keyLayout[i][r][c]);
					keyEditors[r][c].setTurn(qwertyKeyLayout.getProperty(""+toQwerty(keyEditors[r][c].key, false)));
				}
			}
		}
	}
	
	private Character toQwerty(char key, boolean forceLower) {
		char[][][] layout = KEYBOARD_LAYOUTS.get(getSelectedLayout());
		int i=-1, j=-1, k=-1;
		boolean found = false;
		outer: for(i=0; i<layout.length; i++) {
			for(j=0; j<layout[i].length; j++) {
				for(k=0; k<layout[i][j].length; k++) {
					if(key == layout[i][j][k]) {
						found = true;
						break outer;
					}
				}
			}
		}
		if(!found) return key;
		if(forceLower) i = 0;
		return KEYBOARD_LAYOUTS.get(QWERTY_LAYOUT)[i][j][k];
	}
	
	private class KeyEditor extends JPanel {
		private JLabel keyLabel, turnLabel;
		private JTextField editor;
		private char key;
		private String turn;
		public KeyEditor() {
			keyLabel = new JLabel();
			keyLabel.setFont(getFont().deriveFont(10f));
			turnLabel = new JLabel();
			editor = new JTextField(2);
			editor.setFont(getFont().deriveFont(10f));
			setEditing(false);
			editor.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					setEditing(false);
				}
			});
			editor.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						String turn = editor.getText();
						qwertyKeyLayout.setProperty(""+toQwerty(key, false), turn);
						setTurn(turn);
						saveKeys();
					} else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
						setEditing(false);
				}
			});
			
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			JPanel test = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			test.add(keyLabel);
			add(test);

			test = new JPanel();
			test.setLayout(new BoxLayout(test, BoxLayout.PAGE_AXIS));
			test.add(turnLabel);
			turnLabel.setAlignmentX(.5f);
			add(test);
			add(editor);
			
			updateBorder();
		}
		public void setKey(char key) {
			this.key = key;
			setEditing(false);
		}
		public void setTurn(String turn) {
			this.turn = turn;
			setEditing(false);
		}
		private void setEditing(boolean editing) {
			updateBorder();
			setToolTipText(turn);
			keyLabel.setText(""+key);
			turnLabel.setText(turn);
			editor.setText(turn);
			keyLabel.setVisible(!editing);
			turnLabel.setVisible(!editing);
			editor.setVisible(editing);
			if(editing) {
				editor.requestFocusInWindow();
				editor.selectAll();
			}
		}
		private void updateBorder() {
			setBorder(BorderFactory.createLineBorder(getMousePosition() != null ? Color.WHITE : Color.BLACK));
		}
		private int row=-1; //this is used for spacing the keyboard
		public KeyEditor(int row) {
			this.row = row;
		}
		private static final int SIZE = 32;
		public Dimension getPreferredSize() {
			if(row != -1)
				return new Dimension(row*SIZE/2, SIZE);
			return new Dimension(SIZE, SIZE);
		}
	}
	
	public String getTurnForKey(KeyEvent e) {
		if(e.isAltDown()) return null;
		String turn = (String) qwertyKeyLayout.get(""+toQwerty(e.getKeyChar(), false));
		if(turn == null)
			turn = (String) qwertyKeyLayout.get(""+toQwerty(e.getKeyChar(), true));
		return turn;
	}
}
