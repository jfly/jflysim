package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import netscape.javascript.JSObject;

//import netscape.javascript.JSObject;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleOption.PuzzleOptionChangeListener;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Canvas3D;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.RotationMatrix;

@SuppressWarnings("serial")
public class GamesCubeMan extends JApplet implements ActionListener, PuzzleStateChangeListener, MouseWheelListener, PuzzleTimerListener {
	private TwistyPuzzle puzzle;
	private PuzzleCanvas puzzleCanvas;
	private Canvas3D canvas;
	private JCheckBox[] tabBoxes;
	private JCheckBox colorChooserCheckBox, optionsCheckBox, keysCheckBox, cornerChooserBox;
	private RollingJPanel optionsPanel;
	private KeyCustomizerPanel keysPanel;
	private JButton changeView, scramble, resetPuzzle;
	private JTextField turnHistoryField;

	private String puzzle_class = "edu.berkeley.gcweb.gui.gamescubeman.Cuboid.Cuboid";
//	private String puzzle_class = "edu.berkeley.gcweb.gui.gamescubeman.Pyraminx.Pyraminx";
//	private String puzzle_class = "edu.berkeley.gcweb.gui.gamescubeman.SquareOne.SquareOne";
	
	private ColorOption bg_color = new ColorOption("bg_color", false, Color.GRAY);
	private ColorOption fg_color = new ColorOption("fg_color", false, Color.WHITE);
	private CheckBoxOption show_options = new CheckBoxOption("show_options", false, true);
	private CheckBoxOption focus_indicator = new CheckBoxOption("focus_indicator", true, true);
	private CheckBoxOption draw_axis = new CheckBoxOption("draw_axis", false, false);
	private CheckBoxOption free_rotation = new CheckBoxOption("free_rotation", true, true);
	private CheckBoxOption free_rotation_spin = new CheckBoxOption("free_rotation_spin", true, true);
	private CheckBoxOption antialiasing = new CheckBoxOption("antialiasing", true, false);
	private CheckBoxOption show_history = new CheckBoxOption("show_history", true, false);
	private CheckBoxOption bld_mode = new CheckBoxOption("bld_mode", true, false);
	private SliderOption scale = new SliderOption("scale", true, (int) Canvas3D.DEFAULT_SCALE, 0, 10000);
	private SliderOption distance = new SliderOption("distance", true, 4, 4, 100);
	
	public void paint(Graphics g) {
		super.paint(g);
		if(puzzle == null)
			g.drawString("Loading puzzle class: " + puzzle_class, 0, 20);
	}
	
	private JSObject jso;
	private AppletSettings settings = new AppletSettings(this, null); //this is a just a default until we can load the url arguments
	public void init() {
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				canvas.requestFocusInWindow();
			}
			public void focusLost(FocusEvent e) {}
		});
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					System.out.println("Puzzle " + puzzle_class);
					puzzle_class = settings.get("puzzle_class", puzzle_class);
					System.out.println("Puzzle " + puzzle_class);
					createGUI();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	@Override
	public void start() {
		try {
			jso.call("appletLoaded", new Object[0]);
		} catch(Exception e) {
			
		}
	}
	
	private void createGUI() {
		try {
			jso = JSObject.getWindow(GamesCubeMan.this);
			settings = new AppletSettings(GamesCubeMan.this, jso);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			Class<? extends TwistyPuzzle> puzzleClass = Class.forName(puzzle_class).asSubclass(TwistyPuzzle.class);
			puzzle = puzzleClass.getConstructor().newInstance();
			settings.loadCookies(puzzle.getPuzzleName());
		} catch (Exception e) {
			System.err.println("Error loading puzzle class " + puzzle_class);
			e.printStackTrace();
			return;
		}
		
		optionsPanel = new RollingJPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
		keysPanel = new KeyCustomizerPanel(puzzle, settings);
		
		puzzle.addStateChangeListener(GamesCubeMan.this);
		puzzle.addPuzzleTimerListener(GamesCubeMan.this);

		puzzleCanvas = new PuzzleCanvas(settings, puzzle, optionsPanel, keysPanel);
		canvas = puzzleCanvas.getCanvas();
		
		changeView = new JButton();
		changeView.setMnemonic(KeyEvent.VK_V);
		changeView.setFocusable(false);
		changeView.addActionListener(GamesCubeMan.this);
		changeView(true);
		
		resetPuzzle = new JButton("Reset");
		resetPuzzle.setToolTipText(resetPuzzle.getText());
		resetPuzzle.setFocusable(false);
		resetPuzzle.addActionListener(GamesCubeMan.this);
		
		scramble = new JButton("Scramble");
		scramble.setToolTipText(scramble.getText());
		scramble.setMnemonic(KeyEvent.VK_S);
		scramble.setFocusable(false);
		scramble.addActionListener(GamesCubeMan.this);
		
		colorChooserCheckBox = new JCheckBox("Choose colors", false);
		colorChooserCheckBox.setMnemonic(KeyEvent.VK_C);
		colorChooserCheckBox.setFocusable(false);
		colorChooserCheckBox.addActionListener(GamesCubeMan.this);
		if(puzzleCanvas.getPieceEditorPanel() != null) {
			puzzleCanvas.getPieceEditorPanel().addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals("visibility")) {
						boolean visible = (Boolean) evt.getNewValue();
						if(!visible)
							cornerChooserBox.setSelected(false);
					}
				}
			});
		}
		
		cornerChooserBox = new JCheckBox("Choose Corners", false);
		cornerChooserBox.setMnemonic(KeyEvent.VK_A);
		cornerChooserBox.setFocusable(false);
		cornerChooserBox.addActionListener(GamesCubeMan.this);
		
		optionsCheckBox = new JCheckBox("Options", false);
		optionsCheckBox.setMnemonic(KeyEvent.VK_O);
		optionsCheckBox.setFocusable(false);
		optionsCheckBox.addActionListener(GamesCubeMan.this);
		
		keysCheckBox = new JCheckBox("Keys", false);
		keysCheckBox.setMnemonic(KeyEvent.VK_K);
		keysCheckBox.setFocusable(false);
		keysCheckBox.addActionListener(GamesCubeMan.this);

		PuzzleOptionChangeListener pl = new PuzzleOptionChangeListener() {
			public void puzzleOptionChanged(PuzzleOption<?> src) {
				if(src != null)
					settings.set(src.getName(), src.valueToString());

				canvas.setFocusIndicator(focus_indicator.getValue());
				canvas.setDrawAxis(draw_axis.getValue());
				canvas.setFreeRotation(free_rotation.getValue());
				canvas.setFreeRotationSpin(free_rotation_spin.getValue());
				turnHistoryField.setVisible(show_history.getValue());
				GamesCubeMan.this.validate();
				canvas.setAntialiasing(antialiasing.getValue());
				double[] center = puzzle.getCenter();
				puzzle.setCenter(center[0], center[1], distance.getValue());
				canvas.setScale(scale.getValue());
				updatePiecePicker();
				puzzle.setBLDMode(bld_mode.getValue());
			}
		};
		
		//TODO - probably better to have the PuzzleOption class do all this, if possible
		PuzzleOption<?>[] options = new PuzzleOption<?>[] { bg_color, fg_color, show_options, focus_indicator, draw_axis,
				free_rotation, free_rotation_spin, antialiasing, show_history, scale, distance, bld_mode };
		for(PuzzleOption<?> option : options) {
			String val = settings.get(option.getName(), null);
			if(val != null)
				option.setValue(val);
			option.addChangeListener(pl);
		}
		optionsPanel.add(Utils.sideBySide(antialiasing.getComponent(), show_history.getComponent()));
		optionsPanel.add(Utils.sideBySide(free_rotation.getComponent(), free_rotation_spin.getComponent(), bld_mode.getComponent()));
		optionsPanel.add(Utils.sideBySide(scale.getComponent(), distance.getComponent()));
		
		for(PuzzleOption<?> option : puzzle.getDefaultOptions()) {
			//TODO - methinks the guifiable stuff can be removed
			if(show_options.getValue() && option.isGuifiable())
				optionsPanel.add(option.getComponent());
			String val = settings.get(option.getName(), null);
			if(val != null)
				option.setValue(val);
			option.addChangeListener(puzzle);
			option.addChangeListener(pl);
		}
		
		
		final JButton forward = new JButton(">");
		forward.setFocusable(false);
		final JButton backward = new JButton("<");
		backward.setFocusable(false);
		ActionListener history = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e == null) {
					//this is for initialization
				} else if(e.getSource() == forward) {
					puzzle.forward();
				} else if(e.getSource() == backward) {
					puzzle.backward();
				}
			}
		};
		forward.addActionListener(history);
		backward.addActionListener(history);
		puzzle.addStateChangeListener(new PuzzleStateChangeListener() {
			public void puzzleStateChanged(TwistyPuzzle src, PuzzleTurn turn) {
				int remaining = puzzle.getRemaining();
				int completed = puzzle.getCompleted();
				forward.setText("> " + remaining);
				forward.setEnabled(remaining > 0);
				backward.setText("< " + completed);
				backward.setEnabled(completed > 0);
			}
		});

		JButton play_pause = new JButton("Play");
		play_pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				puzzle.playPause();
			}
		});
		play_pause.setFocusable(false);

		JPanel leftHalf = new JPanel(new BorderLayout());
		JPanel tabs = new JPanel();
		tabs.setLayout(new BoxLayout(tabs, BoxLayout.PAGE_AXIS));
		tabBoxes = new JCheckBox[] { optionsCheckBox, keysCheckBox, cornerChooserBox, colorChooserCheckBox};
		boolean vertical = false; //TODO - vertical doesn't look very good
		tabs.add(Utils.sideBySide(true, vertical, changeView, resetPuzzle, scramble));//, backward, play_pause, forward));
		tabs.add(Utils.sideBySide(false, vertical, tabBoxes));
		leftHalf.add(tabs, BorderLayout.PAGE_START);
		
		turnHistoryField = new JTextField();
		turnHistoryField.setEditable(false);
		leftHalf.add(turnHistoryField, BorderLayout.CENTER);

		JPanel pane = new JPanel(new BorderLayout());
		setContentPane(pane);
		
		if(show_options.getValue())
			pane.add(leftHalf, vertical ? BorderLayout.LINE_START : BorderLayout.PAGE_START);
		pane.add(puzzleCanvas, BorderLayout.CENTER);
		canvas.requestFocusInWindow();
		
		setMouseWheelListener(pane, GamesCubeMan.this);
		setBG_FG(pane, bg_color.getValue(), fg_color.getValue());

		//now that all the options have been set, we can create the puzzle!
		puzzle.createPolys(false);
		pl.puzzleOptionChanged(null);
		puzzle.queueTurns(settings.get("move", null));
		puzzle.initialize(settings.get("initmove", null), false);
		puzzle.initialize(settings.get("initrevmove", null), true);
		puzzle.setDisabled(!settings.getBoolean("edit", true));
		history.actionPerformed(null);
	}
	
	private void setMouseWheelListener(JComponent comp, MouseWheelListener l) {
		comp.addMouseWheelListener(l);
		for(Component child : comp.getComponents()) {
			if(child instanceof JComponent)
				setMouseWheelListener((JComponent)child, l);
			else {
				child.addMouseWheelListener(l);
			}
		}
	}
	
	private void setBG_FG(JComponent comp, Color bg, Color fg) {
		if(comp instanceof JButton)
			return;
		comp.setBackground(bg);
		//we want to set the background of the jradiobuttons,
		//but the foreground determines the selected button, so
		//we don't want to set it to white (which isn't visible)
		if(comp instanceof JRadioButton)
			return;
		comp.setForeground(fg);
		for(Component child : comp.getComponents()) {
			if(child instanceof JComponent)
				setBG_FG((JComponent)child, bg, fg);
			else if(!(child instanceof JButton)) {
				child.setBackground(bg);
				child.setForeground(fg);
			}
		}
	}

	private void updatePiecePicker() {
		if(puzzle.piecePickerSupport())
			cornerChooserBox.setVisible(true);
		else
			cornerChooserBox.setVisible(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == changeView)
			changeView(false);
		else if(e.getSource() == scramble)
			puzzle.scramble();
		else if(e.getSource() == resetPuzzle) {
			if(puzzleCanvas.getPieceEditorPanel() != null && puzzleCanvas.getPieceEditorPanel().isVisible())
				puzzleCanvas.getPieceEditorPanel().reset();
			else
				puzzle.resetPuzzle();
		} else if(Utils.indexOf(e.getSource(), tabBoxes) != -1) {
			for(JCheckBox box : tabBoxes)
				if(box != e.getSource() && box != null)
					box.setSelected(false);
			puzzleCanvas.setColorEditing(colorChooserCheckBox.isSelected());
			puzzleCanvas.setPieceEditing(cornerChooserBox.isSelected());
			optionsPanel.setVisible(optionsCheckBox.isSelected());
			keysPanel.setVisible(keysCheckBox.isSelected());
		} else
			throw new RuntimeException();
	}

	private int rotationIndex = 0;
	private void changeView(boolean reset) {
		RotationMatrix[] angles = puzzle.getPreferredViewAngles();
		if(reset)
			rotationIndex = 0;
		else if(puzzle.getRotation().equals(angles[rotationIndex])) //only switch to next view if the current view isn't "dirty"
			rotationIndex = (rotationIndex + 1) % angles.length;
		String text = "Change View (" + rotationIndex + ")";
		changeView.setText(text);
		changeView.setToolTipText(text);
		puzzle.setRotation(angles[rotationIndex]);
		//stop any spinning
		canvas.mousePressed(null);
	}
	
	public void puzzleStateChanged(final TwistyPuzzle src, final PuzzleTurn turn) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String hist = Utils.join(" ", src.getTurnHistory().toArray());
				//this check reduces flickering
				if(!hist.equals(turnHistoryField.getText()))
					turnHistoryField.setText(hist);
			}
		});
		if(jso != null) {
			new Thread() {
				public void run() {
					//we do this in a separate thread because the call() method will
					//hang for the javascript
					try {
						jso.call("puzzleStateChanged", new Object[] { turn, src.getState() });
					} catch (Exception e) {}
				}
			}.start();
		}
	}
	
	public static void main(String[] args) {
		final GamesCubeMan a = new GamesCubeMan();
		if(args.length > 0) {
			ArrayList<String> options = new ArrayList<String>(Arrays.asList("Cuboid", "Pyraminx", "OldMegaminx", "SquareOne"));
			if(!options.contains(args[0])) {
				System.out.println("First argument must be one of " + options + "!");
				return;
			}
			a.puzzle_class = "edu.berkeley.gcweb.gui.gamescubeman." + args[0] + "." + args[0];
		}
		a.setPreferredSize(new Dimension(400, 500));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				a.createGUI();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JPanel pane = new JPanel();
				f.setContentPane(pane);
				f.add(a);
				f.pack();
				f.setVisible(true);
				if(a.canvas != null)
					a.canvas.requestFocusInWindow();
			}
		});
		
		//added for hackathon 2010! =)
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String line;
			while((line = br.readLine()) != null) {
				a.puzzle.doTurn(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(jso != null) {
			try {
				jso.eval("window.scrollBy(0, " + (e.getUnitsToScroll() * 50) + ")");
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/*** Begin live connect methods ***/
	
	public void setPuzzleClass(String clz) {
		puzzle_class = clz;
		createGUI();
	}
	
	public String getBoardString() {
		return puzzle.getState();
	}
	
	public boolean doMove(String move) {
		return puzzle.doTurn(move);
	}
	
	public void playPause() {
		puzzle.playPause();
	}
	
	public void reset() {
		puzzle.resetPuzzle();
	}
	
	public int getRemaining() {
		return puzzle.getRemaining();
	}
	
	public boolean isPlaying() {
		return puzzle.isPlaying();
	}
	
	public int getCompleted() {
		return puzzle.getCompleted();
	}
	
	public void forward() {
		puzzle.forward();
	}
	
	public void backward() {
		puzzle.backward();
	}

	public void inspectionStarted(String scramble) {
		if(jso == null) { return; }
		try {
			jso.call("inspectionStarted", new Object[] { scramble });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void timerReset() {
		if(jso == null) { return; }
		try {
			jso.call("timerReset", new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void timerStarted() {
		if(jso == null) { return; }
		try {
			jso.call("timerStarted", new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void timerStopped(double time) {
		if(jso == null) { return; }
		try {
			jso.call("timerStopped", new Object[] { time });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*** End live connect methods ***/
}
