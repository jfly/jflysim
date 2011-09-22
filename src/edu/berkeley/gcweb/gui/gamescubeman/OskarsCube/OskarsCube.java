package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingConstants;
//import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import netscape.javascript.JSObject;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Canvas3D;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.RotationMatrix;

@SuppressWarnings("serial")
public class OskarsCube extends JApplet implements KeyListener, ActionListener {
	private final static boolean USE_JAVA_SOLVER = true; 
	private boolean display_remoteness_default = false;
	private boolean display_best_move_default = false;
	private boolean display_number_viable_default = false;
	private static final boolean find_best_start_end_default = true;
	private static final boolean find_best_subcomponent = true;
	private boolean random_faces = true;
	
	private int boardsize = 5;
	private int goalRemoteness = 0;
	private int goalBushiness = 0; //Too large and maze has too many open lines == easy routes to visualize
	private int goalSubcomponents = 100; //NOTE, this is at most!!
	private int goalBranches = 0; 
	private int goalBranchbyDegree = 0;
	private int goalMaxBrDegree = 0;
	private int goalTurns = 0;
	private int goalPlaneTurns = 0; //not in plane turns
	
	private int movesmade =0;
	
	public boolean topdownview = true;
	
	public static int acheivable;
	public static Solver solved_map;
	public MyShape cube;
	private CubeGen cubefaces;
	private Canvas3D canvas;
	public static JSObject jso;
	
	private JMenuBar menu_bar;
	private JMenuBar bottom_menu_bar;
	private JMenu new_puzzle, display;
	private JLabel remoteness, best_move, num_viable;
	private JCheckBoxMenuItem display_best_move, display_remoteness, display_solution_path, display_unachievable;
	private JMenuItem display_reset;
	private JMenuItem restart;
	private JMenuItem new_make;
	private JRadioButtonMenuItem new_random, new_saved, new_oski;
	private JRadioButtonMenuItem top_down_view,side_view;
	private ButtonGroup view_group;
	private JTextField saved_game_b, saved_game_w, saved_game_r;
	private JLabel saved_game;
	private JLabel remote, bushiness, subcomponents, branches, brbydegree, maxbrdeg, turns, planeturns;
	private JLabel moves_made, remoteness_label, best_move_label;
	private JLabel open_alleys, abs_distance, face_distance, linwalls, branchturns;
	private ButtonGroup back_view_group;
	private JRadioButtonMenuItem blue_view, white_view, red_view;
	
	public static boolean[][][] traveled_map;
	//for if decide to implement searchable random in GUI
	/*
	private JSlider board_size;
	private JSlider goal_remoteness;
	private JSlider goal_bushiness;
	private JSlider goal_components;
	private JSlider goal_branches;
	private JSlider goal_br_degree;
	private JSlider goal_plane_turns;
	*/

	
	public void init() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					canvas = new Canvas3D();
					make_new_puzzle();
					
					//menu set up
					menu_bar = new JMenuBar();
					bottom_menu_bar = new JMenuBar();
					new_puzzle = new JMenu("Game");
					new_puzzle.setFocusable(false);
					new_puzzle.setLayout(new BoxLayout(new_puzzle, BoxLayout.X_AXIS));
					new_make = new JMenuItem("New Puzzle", KeyEvent.VK_T);
					new_puzzle.add(new_make);
					new_make.addActionListener(OskarsCube.this);
					new_puzzle.addSeparator();
					ButtonGroup group = new ButtonGroup();
					JRadioButtonMenuItem new_classic = new JRadioButtonMenuItem("Oskar's Cube");
					new_oski = new JRadioButtonMenuItem("Oski's Cube");
					new_classic.setSelected(!random_faces);
					group.add(new_classic);
					group.add(new_oski);
					new_puzzle.add(new_classic);
					new_puzzle.add(new_oski);
					new_random = new JRadioButtonMenuItem("Random Cube");
					group.add(new_random);
					new_random.setSelected(random_faces);
					new_saved = new JRadioButtonMenuItem("Load Cube");
					new_saved.setSelected(false);
					group.add(new_saved);
					new_puzzle.add(new_saved);
					new_puzzle.add(new_random);
					new_puzzle.addSeparator();
					saved_game = new JLabel("B/W/R Face Values");
					new_puzzle.add(saved_game);
					
					int bs = java.lang.Integer.signum(cubefaces.BlueInt);
					long b = ((long) cubefaces.BlueInt*bs) + (1-bs)/2* ((long) java.lang.Integer.MAX_VALUE);
					int ws = java.lang.Integer.signum(cubefaces.WhiteInt);
					long w = ((long) cubefaces.WhiteInt*ws) + (1-ws)/2*( (long) java.lang.Integer.MAX_VALUE);
					int rs = java.lang.Integer.signum(cubefaces.RedInt);
					long r = ((long) cubefaces.RedInt*rs) + (1-rs)/2*((long) java.lang.Integer.MAX_VALUE);
					saved_game_b = new JTextField(b +"", 1);
					new_puzzle.add(saved_game_b);
					saved_game_w = new JTextField(w + "", 1);
					new_puzzle.add(saved_game_w);
					saved_game_r = new JTextField(r + "", 1);
					new_puzzle.add(saved_game_r);
					new_puzzle.addSeparator();
					
					//Add Sliders Here
					remote = new JLabel("Remoteness: "+ solved_map.getRemoteness(cubefaces.start) / 2);
					bushiness = new JLabel("Bushiness: " + cubefaces.bushiness);
					subcomponents = new JLabel("Subcomponents: " + cubefaces.subcomponents+"");
					branches = new JLabel("Branches: " + cubefaces.branches+"");
					brbydegree = new JLabel("Branches by Deg: " + cubefaces.brfactor+"");
					maxbrdeg = new JLabel("Max Branch: " + cubefaces.maxbrfactor+"");
					turns = new JLabel("Turns: "+ cubefaces.turns+"");
					planeturns = new JLabel("Not in Plane Turns: " +cubefaces.planeTurns+"");
					num_viable = new JLabel("Achievable: " + acheivable + "/" + (boardsize*boardsize*boardsize));
					
					open_alleys = new JLabel("Alleys: ");
					for(int i = 0; i< cubefaces.boardsize*2-1; i++) {
						open_alleys.setText(open_alleys.getText() + cubefaces.alleys[i]);
						if(i != cubefaces.boardsize*2-2) {
							open_alleys.setText(open_alleys.getText() + "/");
						}
					}
					abs_distance = new JLabel("Linear Dist: " + cubefaces.sumlindistance);
					face_distance = new JLabel("Solve Dist: " + cubefaces.sumsoldistance);
					branchturns = new JLabel("Branch Turns: " + cubefaces.branchturns);
					//linwalls = new JLabel("Linear Walls: " + cubefaces.linearwalls);
					
					new_puzzle.add(remote);
					new_puzzle.add(bushiness);
					new_puzzle.add(subcomponents);
					new_puzzle.add(branches);
					new_puzzle.add(brbydegree);
					new_puzzle.add(maxbrdeg);
					new_puzzle.add(turns);
					new_puzzle.add(planeturns);
					new_puzzle.add(num_viable);
					new_puzzle.add(open_alleys);
					new_puzzle.add(abs_distance);
					new_puzzle.add(face_distance);
					new_puzzle.add(branchturns);
					//new_puzzle.add(linwalls);
					
					display = new JMenu("Options");
					display.setFocusable(false);
					display.setLayout(new BoxLayout(display, BoxLayout.X_AXIS));
					display_reset = new JMenuItem("Reset View",KeyEvent.VK_T);
					display_reset.addActionListener(OskarsCube.this);
					display.add(display_reset);
					display.addSeparator();
					restart = new JMenuItem("Restart",KeyEvent.VK_T);
					restart.addActionListener(OskarsCube.this);
					display.add(restart);
					display.addSeparator();
					//View Button Group
					view_group = new ButtonGroup();
					top_down_view = new JRadioButtonMenuItem("Top Down View");
					view_group.add(top_down_view);
					side_view = new JRadioButtonMenuItem("Side View");
					view_group.add(side_view);
					display.add(top_down_view);
					display.add(side_view);
					top_down_view.addActionListener(OskarsCube.this);
					side_view.addActionListener(OskarsCube.this);
					side_view.setSelected(!topdownview);
					top_down_view.setSelected(topdownview);
					display.addSeparator();
					back_view_group = new ButtonGroup();
					blue_view = new JRadioButtonMenuItem("Blue Back");
					red_view = new JRadioButtonMenuItem("Red Back");
					white_view = new JRadioButtonMenuItem("White Back");
					back_view_group.add(blue_view);
					back_view_group.add(white_view);
					back_view_group.add(red_view);
					blue_view.addActionListener(OskarsCube.this);
					red_view.addActionListener(OskarsCube.this);
					white_view.addActionListener(OskarsCube.this);
					blue_view.setSelected(true);
					display.add(blue_view);
					display.add(red_view);
					display.add(white_view);
					
					display.addSeparator();
					display_best_move = new JCheckBoxMenuItem("Show Move Value");
					display_best_move.setSelected(display_best_move_default);
					display_best_move.addActionListener(OskarsCube.this);
					display.add(display_best_move);
					display_remoteness = new JCheckBoxMenuItem("Show Remoteness");
					display_remoteness.setSelected(display_remoteness_default);
					display_remoteness.addActionListener(OskarsCube.this);
					display.add(display_remoteness);
					display_solution_path = new JCheckBoxMenuItem("Show Solution Path");
					//display_solution_path.setSelected(display_remoteness_default);
					display_solution_path.addActionListener(OskarsCube.this);
					display.add(display_solution_path);
					display_unachievable = new JCheckBoxMenuItem("Show Unachievable");
					//display_unachievable.setSelected(display_number_viable_default);
					display_unachievable.addActionListener(OskarsCube.this);
					display.add(display_unachievable);
					display.addSeparator();
					
					moves_made = new JLabel("Moves: " + movesmade + " ");
					moves_made.setLayout(new BoxLayout(moves_made, BoxLayout.X_AXIS));
					moves_made.setVerticalTextPosition(SwingConstants.BOTTOM);
					
					new_puzzle.setVerticalTextPosition(SwingConstants.BOTTOM);
					display.setVerticalTextPosition(SwingConstants.BOTTOM);
					moves_made.setVerticalAlignment(SwingConstants.BOTTOM);
					menu_bar.add(new_puzzle);
					menu_bar.add(display);
										
					menu_bar.setMaximumSize(new Dimension(100000000,1));
					menu_bar.setLayout(new BoxLayout(menu_bar, BoxLayout.X_AXIS));
					bottom_menu_bar.add(moves_made);
					
					bottom_menu_bar.setLayout(new BoxLayout(bottom_menu_bar, BoxLayout.X_AXIS));
					bottom_menu_bar.setMaximumSize(new Dimension(100000000,1));
					
					JPanel full_panel = new JPanel();
					full_panel.setLayout(new BoxLayout(full_panel,BoxLayout.PAGE_AXIS));
					JPanel buttons = new JPanel();
					buttons.setLayout(new BoxLayout(buttons, BoxLayout.PAGE_AXIS));
					best_move = new JLabel();
					
					remoteness = new JLabel();
					
					
					
					if (solved_map.getRemoteness(cubefaces.start) !=0) {
						remoteness.setText(solved_map.getRemoteness(cubefaces.start) / 2 + " ");
						remoteness.setVisible(false);
						best_move.setText(solved_map.getBestMove(cubefaces.start) +" ");
						best_move.setVisible(display_best_move_default);
					} else {
						remoteness.setText("NA");
						remoteness.setVisible(display_remoteness_default);
						best_move.setText("NA");
						best_move.setVisible(display_best_move_default);
						
					}
					
		
					
					remoteness_label = new JLabel("Remoteness: " + remoteness.getText());
					remoteness_label.setLayout(new BoxLayout(remoteness_label, BoxLayout.X_AXIS));
					remoteness_label.setVerticalTextPosition(SwingConstants.BOTTOM);
					best_move_label = new JLabel("Best Move: " + best_move.getText());
					best_move_label.setLayout(new BoxLayout(best_move_label, BoxLayout.X_AXIS));
					best_move_label.setVerticalTextPosition(SwingConstants.BOTTOM);
					bottom_menu_bar.add(remoteness_label);
					bottom_menu_bar.add(best_move_label);
					remoteness_label.setVisible(display_remoteness_default);
					best_move_label.setVisible(display_best_move_default);
					
					menu_bar.setFocusable(false);
					full_panel.add(menu_bar);
					bottom_menu_bar.setFocusable(false);
					full_panel.add(bottom_menu_bar);
					
					
					
					buttons.add(remoteness);
					buttons.add(best_move);
					
					
					full_panel.add(buttons);
					full_panel.add(canvas);
					
					getContentPane().add(full_panel);
					cube.setSecondFacesVisible(true);
					cube.setAwayRVisible(solved_map.getBestMove(cube.current_position) == "away from RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,1}), display_best_move.isSelected());
					cube.setTowardRVisible(solved_map.getBestMove( cube.current_position) == "towards RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,-1}), display_best_move.isSelected());
					cube.setAwayBVisible(solved_map.getBestMove(cube.current_position) == "away from BLUE", solved_map.isValidMove(cube.current_position, new int[] {0,1,0}), display_best_move.isSelected());
					cube.setTowardBVisible(solved_map.getBestMove(cube.current_position) == "towards BLUE",solved_map.isValidMove(cube.current_position, new int[] {0,-1,0}), display_best_move.isSelected());
					cube.setTowardWVisible(solved_map.getBestMove(cube.current_position) == "towards WHITE",solved_map.isValidMove(cube.current_position, new int[] {-1,0,0}), display_best_move.isSelected());
					cube.setAwayWVisible(solved_map.getBestMove(cube.current_position) == "away from WHITE", solved_map.isValidMove(cube.current_position, new int[] {1,0,0}), display_best_move.isSelected());
					if(!topdownview) {
						cube.setSecondFacesVisible(topdownview);
					} else if(blue_view.isSelected()) {
						cube.setSecondFacesBlueVisible(true);
					}else if(red_view.isSelected()) {
						cube.setSecondFacesRedVisible(true);
					}else if(white_view.isSelected()) {
						cube.setSecondFacesWhiteVisible(true);
					}
					
					
					canvas.fireCanvasChange();
					set_view();
					update_displays();

				}
			});
		
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/* Make a new puzzle with int values in CubeGen set to input, default to original if not valid */
	private void make_new_puzzle(long blue, long white, long red) {
		cubefaces = new CubeGen(blue,white,red);
		if (USE_JAVA_SOLVER)
			solved_map = new Solver(cubefaces);
		int zoom = (25 + 2*(5-boardsize)*(5-boardsize))/17*12;
		traveled_map = new boolean[2*cubefaces.boardsize-1][2*cubefaces.boardsize-1][2*cubefaces.boardsize-1];
		traveled_map[cubefaces.start[0]][cubefaces.start[1]][cubefaces.start[2]] = true;
		canvas.setLightBorders(true);
		cube = new MyShape(0, 0, zoom, cubefaces);
		cube.setCanvas(canvas);
		canvas.addShape3D(cube);
		canvas.addKeyListener(OskarsCube.this);
		
	}
	
	private void make_new_puzzle() {
		cubefaces = new CubeGen(random_faces, find_best_start_end_default, find_best_subcomponent, boardsize);
		
		if (USE_JAVA_SOLVER)
			solved_map = new Solver(cubefaces);
		/*while(!(solved_map.move_map.containsKey(solved_map.end[0] * boardsize*boardsize*4 + solved_map.end[1] * boardsize*2 + solved_map.end[2]) && solved_map.move_map.containsKey(solved_map.start[0] * boardsize*boardsize*4 + solved_map.start[1]
			* boardsize*2 + solved_map.start[2]))) {
			System.out.println("failed");
			cubefaces = new CubeGen(random_faces, find_best_start_end_default, find_best_subcomponent, boardsize);
			solved_map = new Solver(cubefaces);
		}
		*/
		int tries = 0;
		int maxremotenessseen =0;
		int maxbushinessseen = 0;
		boolean found = true;
		while (found) {
			if (maxremotenessseen < solved_map.getRemoteness(solved_map.start)/2) {
				maxremotenessseen = solved_map.getRemoteness(solved_map.start)/2;
			}
			if (maxbushinessseen < cubefaces.bushiness) {
				maxbushinessseen = cubefaces.bushiness;
			}
				System.out.println("failed " + tries + " " + solved_map.getRemoteness(solved_map.start)/2 + " " + maxremotenessseen + " " + maxbushinessseen);
				cubefaces = new CubeGen(random_faces, find_best_start_end_default, find_best_subcomponent, boardsize);
				solved_map = new Solver(cubefaces);
				tries++;
				found = (solved_map.getRemoteness(solved_map.start)/2 < goalRemoteness) || (cubefaces.branches < goalBranches) ||
					(cubefaces.brfactor < goalBranchbyDegree) || (cubefaces.maxbrfactor < goalMaxBrDegree) || (cubefaces.bushiness < goalBushiness) 
					|| (cubefaces.subcomponents > goalSubcomponents) || (cubefaces.turns < goalTurns) || (cubefaces.planeTurns < goalPlaneTurns);
				//System.out.println("found" + found + " " + cubefaces.maxbrfactor);
		}
		int zoom = (25 + 2*(5-boardsize)*(5-boardsize))/17*12;
		canvas.setLightBorders(true);
		traveled_map = new boolean[2*cubefaces.boardsize-1][2*cubefaces.boardsize-1][2*cubefaces.boardsize-1];
		traveled_map[cubefaces.start[0]][cubefaces.start[1]][cubefaces.start[2]] = true;
		cube = new MyShape(0, 0, zoom, cubefaces);
		cube.setCanvas(canvas);
		canvas.addShape3D(cube);
		canvas.addKeyListener(OskarsCube.this);
		
		
		
		
	}

	@SuppressWarnings("unused")
	private void setBG_FG(JComponent comp, Color bg, Color fg) {
		if (comp instanceof JButton)
			return;
		comp.setBackground(bg);
		comp.setForeground(fg);
		for (Component child : comp.getComponents())  {
			if (child instanceof JComponent)
				setBG_FG((JComponent) child, bg, fg);
			else if (!(child instanceof JButton)) {
				child.setBackground(bg);
				child.setForeground(fg);
			}
		}
	}

	public String getBoardString() {
		MyShape piece_holder = (MyShape) cube;
		String position = Integer.toString(piece_holder.current_position[0]);
		position += Integer.toString(piece_holder.current_position[1]);
		position += Integer.toString(piece_holder.current_position[2]);
		return position;
	}

	@SuppressWarnings("unused")
	private JPanel sideBySide(JComponent... cs) {
		JPanel p = new JPanel();
		p.setBackground(Color.WHITE);
		for (JComponent c : cs)
			p.add(c);
		return p;
	}

	public static void main(String[] args) {
		final OskarsCube a = new OskarsCube();
		a.init();
		a.setPreferredSize(new Dimension(4000, 5000));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JPanel pane = new JPanel();
				f.setContentPane(pane);
				f.add(a);
				f.pack();
				f.setVisible(true);
				a.canvas.requestFocusInWindow();
			}
		});
	}
	
	private boolean movement_key_held = false;
	private boolean move_key_held = false;
	private int tW = KeyEvent.VK_W;
	private int aW = KeyEvent.VK_S;
	private int tB = KeyEvent.VK_E;
	private int aB = KeyEvent.VK_D;
	private int tR = KeyEvent.VK_R;
	private int aR = KeyEvent.VK_F;
	
	private int right = KeyEvent.VK_RIGHT;
	private int left = KeyEvent.VK_LEFT;
	private int up = KeyEvent.VK_UP; 
	private int down = KeyEvent.VK_DOWN;
	private int right2 = KeyEvent.VK_L;
	private int left2 = KeyEvent.VK_J;
	private int up2 = KeyEvent.VK_I; 
	private int down2 = KeyEvent.VK_K;
	

	public void keyPressed(KeyEvent arg0) {
		//System.out.println(arg0.getKeyChar());
		if(move_key_held) {
			move_key_held = false;
		if((arg0.getKeyCode() == tB || arg0.getKeyCode() == aB) && top_down_view.isSelected()) {
			//switch to topdownblue
			if(!blue_view.isSelected()) {
			blue_view.setSelected(true);
			arg0.setKeyChar(';');
			}
		} else if ((arg0.getKeyCode() == tW || arg0.getKeyCode() == aW) && top_down_view.isSelected()) {
			//switch to topdownwhite
			if(!white_view.isSelected()) {
				white_view.setSelected(true);
				arg0.setKeyChar(';');
				}
		} else if ((arg0.getKeyCode() == tR || arg0.getKeyCode() == aR) && top_down_view.isSelected()) {
			//switch to topdownred
			if(!red_view.isSelected()) {
				red_view.setSelected(true);
				arg0.setKeyChar(';');
				}
		} else if ((arg0.getKeyCode() == right || arg0.getKeyCode() == right2) && top_down_view.isSelected()) {
			if(blue_view.isSelected()) {
				arg0.setKeyCode(tW);
			} else if (red_view.isSelected()) {
				arg0.setKeyCode(tW);
			} else if (white_view.isSelected()) {
				arg0.setKeyCode(tR);
			}
		} else if ((arg0.getKeyCode() == left || arg0.getKeyCode() == left2) && top_down_view.isSelected()) {
			if(blue_view.isSelected()) {
				arg0.setKeyCode(aW);
			} else if (red_view.isSelected()) {
				arg0.setKeyCode(aW);
			} else if (white_view.isSelected()) {
				arg0.setKeyCode(aR);
			}
		} else if ((arg0.getKeyCode() == up || arg0.getKeyCode() == up2) && top_down_view.isSelected()) {
			if(blue_view.isSelected()) {
				arg0.setKeyCode(tR);
			} else if (red_view.isSelected()) {
				arg0.setKeyCode(aB);
			} else if (white_view.isSelected()) {
				arg0.setKeyCode(tB);
			}
		} else if ((arg0.getKeyCode() == down || arg0.getKeyCode() == down2) && top_down_view.isSelected()) {
			if(blue_view.isSelected()) {
				arg0.setKeyCode(aR);
			} else if (red_view.isSelected()) {
				arg0.setKeyCode(tB);
			} else if (white_view.isSelected()) {
				arg0.setKeyCode(aB);
			}
		}
		}
		
		if (arg0.getKeyCode() == aB) {
			if (!movement_key_held) {
				movement_key_held = true;
				MyShape holder = (MyShape) cube;
				boolean valid= solved_map.isValidMove(holder.current_position,
							new int[] { 0, 1, 0 });
				if (valid) {
					traveled_map[cube.current_position[0]][cube.current_position[1] +1][cube.current_position[2]] = true;
					traveled_map[cube.current_position[0]][cube.current_position[1] +2][cube.current_position[2]] = true;
					holder.big_red_axis.xyholder.translate(0, -2, 0);
					holder.big_red_axis.xzholder.translate(0, -2, 0);
					holder.current_position[1] += 2;
					movesmade= movesmade +1;
				}
				canvas.fireCanvasChange();
			}
		}
		if (arg0.getKeyCode() == tB) {
			if (!movement_key_held) {
				movement_key_held = true;
				MyShape holder = (MyShape) cube;
				boolean valid = solved_map.isValidMove(holder.current_position,
							new int[] { 0, -1, 0 });
				if (valid) {
					traveled_map[cube.current_position[0]][cube.current_position[1]-1][cube.current_position[2]] = true;
					traveled_map[cube.current_position[0]][cube.current_position[1]-2][cube.current_position[2]] = true;
					holder.big_red_axis.xyholder.translate(0, 2, 0);
					holder.big_red_axis.xzholder.translate(0, 2, 0);
					holder.current_position[1] -= 2;
					movesmade= movesmade +1;
				}
				canvas.fireCanvasChange();
			}
		}
		if (arg0.getKeyCode() == aR) {
			if (!movement_key_held) {
				movement_key_held = true;
				MyShape holder = (MyShape) cube;
				boolean valid = solved_map.isValidMove(holder.current_position,
							new int[] { 0, 0, 1 });
				if (valid) {
					traveled_map[cube.current_position[0]][cube.current_position[1]][cube.current_position[2]+1] = true;
					traveled_map[cube.current_position[0]][cube.current_position[1]][cube.current_position[2]+2] = true;
					holder.big_red_axis.xyholder.translate(0, 0, 2);
					holder.big_red_axis.yzholder.translate(0, 0, 2);
					holder.current_position[2] += 2;
					movesmade= movesmade +1;
				}
				canvas.fireCanvasChange();
			}
		}
		if (arg0.getKeyCode() == tR) {
			if (!movement_key_held) {
				movement_key_held = true;
				MyShape holder = (MyShape) cube;
				boolean valid = solved_map.isValidMove(holder.current_position,
							new int[] { 0, 0, -1 });
				if (valid) {
					traveled_map[cube.current_position[0]][cube.current_position[1]][cube.current_position[2]-1] = true;
					traveled_map[cube.current_position[0]][cube.current_position[1]][cube.current_position[2]-2] = true;
					holder.big_red_axis.yzholder.translate(0, 0, -2);
					holder.big_red_axis.xyholder.translate(0, 0, -2);
					holder.current_position[2] -= 2;
					movesmade= movesmade +1;
				}
				canvas.fireCanvasChange();
			}
		}
		if (arg0.getKeyCode() == aW) {
			if (!movement_key_held) {
				movement_key_held = true;
				MyShape holder = (MyShape) cube;
				boolean valid = solved_map.isValidMove(holder.current_position,
							new int[] { 1, 0, 0 });
				if (valid) {
					traveled_map[cube.current_position[0]+1][cube.current_position[1]][cube.current_position[2]] = true;
					traveled_map[cube.current_position[0]+2][cube.current_position[1]][cube.current_position[2]] = true;
					holder.big_red_axis.yzholder.translate(2, 0, 0);
					holder.big_red_axis.xzholder.translate(2, 0, 0);
					holder.current_position[0] += 2;
					movesmade= movesmade +1;
				}
				canvas.fireCanvasChange();
			}
		}
		if (arg0.getKeyCode() == tW ) {
			if (!movement_key_held) {
				movement_key_held = true;
				MyShape holder = (MyShape) cube;
				boolean valid= solved_map.isValidMove(holder.current_position,
							new int[] { -1, 0, 0 });
				if (valid) {
					traveled_map[cube.current_position[0]-1][cube.current_position[1]][cube.current_position[2]] = true;
					traveled_map[cube.current_position[0]-2][cube.current_position[1]][cube.current_position[2]] = true;
					holder.big_red_axis.yzholder.translate(-2, 0, 0);
					holder.big_red_axis.xzholder.translate(-2, 0, 0);
					holder.current_position[0] -= 2;
					movesmade= movesmade +1;
				}
				canvas.fireCanvasChange();
			}
		}
		
		cube.setSecondFacesVisible(true);
		cube.setAwayRVisible(solved_map.getBestMove(cube.current_position) == "away from RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,1}), display_best_move.isSelected());
		cube.setTowardRVisible(solved_map.getBestMove( cube.current_position) == "towards RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,-1}), display_best_move.isSelected());
		cube.setAwayBVisible(solved_map.getBestMove(cube.current_position) == "away from BLUE", solved_map.isValidMove(cube.current_position, new int[] {0,1,0}), display_best_move.isSelected());
		cube.setTowardBVisible(solved_map.getBestMove(cube.current_position) == "towards BLUE",solved_map.isValidMove(cube.current_position, new int[] {0,-1,0}), display_best_move.isSelected());
		cube.setTowardWVisible(solved_map.getBestMove(cube.current_position) == "towards WHITE",solved_map.isValidMove(cube.current_position, new int[] {-1,0,0}), display_best_move.isSelected());
		cube.setAwayWVisible(solved_map.getBestMove(cube.current_position) == "away from WHITE", solved_map.isValidMove(cube.current_position, new int[] {1,0,0}),display_best_move.isSelected());
		if(!topdownview) {
			cube.setSecondFacesVisible(topdownview);
		} else if(blue_view.isSelected()) {
			cube.setSecondFacesBlueVisible(true);
		}else if(red_view.isSelected()) {
			cube.setSecondFacesRedVisible(true);
		}else if(white_view.isSelected()) {
			cube.setSecondFacesWhiteVisible(true);
		}
		cube.updateIntSol(display_solution_path.isSelected());
		moves_made.setText("Moves: " + movesmade + " ");
		//remoteness.setText("Remoteness: " + solved_map.getRemoteness(cube.current_position) / 2 + " ");
		set_view();
		canvas.fireCanvasChange();
		update_displays();
	}

	private void update_displays() {
		// catch if start or end is not valid.
		if (!(solved_map.move_map.containsKey(solved_map.end[0] * boardsize*boardsize*4
				+ solved_map.end[1] * boardsize*2 + solved_map.end[2]) && solved_map.move_map
				.containsKey(solved_map.start[0] * boardsize*boardsize*4 + solved_map.start[1]
						* boardsize*2 + solved_map.start[2]))) {
			System.out.println("Start or end are not achievable");
			return;
		}
		remoteness_label.setText("Remoteness: " + solved_map.getRemoteness(cube.current_position)/2 + " ");
		best_move_label.setText("Best Move: " + solved_map.getBestMove(cube.current_position) + " ");
		
	}

	public void keyReleased(KeyEvent e) {
		//  Auto-generated method stub
		movement_key_held = false;
	}

	public void keyTyped(KeyEvent e) {
		move_key_held = true;

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == display_reset) {
			topdownview = top_down_view.isSelected();
			set_view();
			canvas.fireCanvasChange();
		} else if (e.getSource() == restart) {
			//TODO reset to the start position
			cube.big_red_axis.xyholder.translate(0, -solved_map.start[1] + cube.current_position[1], solved_map.start[2] - cube.current_position[2]);
			cube.big_red_axis.yzholder.translate(solved_map.start[0] - cube.current_position[0], 0, solved_map.start[2] - cube.current_position[2]);
			cube.big_red_axis.xzholder.translate(solved_map.start[0] - cube.current_position[0], -solved_map.start[1] + cube.current_position[1], 0);
			
			cube.current_position[0] = solved_map.start[0];
			cube.current_position[1] = solved_map.start[1];
			cube.current_position[2] = solved_map.start[2];
			
			
			cube.setSecondFacesVisible(true);
			cube.setAwayRVisible(solved_map.getBestMove(cube.current_position) == "away from RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,1}), display_best_move.isSelected());
			cube.setTowardRVisible(solved_map.getBestMove( cube.current_position) == "towards RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,-1}), display_best_move.isSelected());
			cube.setAwayBVisible(solved_map.getBestMove(cube.current_position) == "away from BLUE", solved_map.isValidMove(cube.current_position, new int[] {0,1,0}), display_best_move.isSelected());
			cube.setTowardBVisible(solved_map.getBestMove(cube.current_position) == "towards BLUE",solved_map.isValidMove(cube.current_position, new int[] {0,-1,0}), display_best_move.isSelected());
			cube.setTowardWVisible(solved_map.getBestMove(cube.current_position) == "towards WHITE",solved_map.isValidMove(cube.current_position, new int[] {-1,0,0}), display_best_move.isSelected());
			cube.setAwayWVisible(solved_map.getBestMove(cube.current_position) == "away from WHITE", solved_map.isValidMove(cube.current_position, new int[] {1,0,0}),display_best_move.isSelected());
			if(!topdownview) {
				cube.setSecondFacesVisible(topdownview);
			} else if(blue_view.isSelected()) {
				cube.setSecondFacesBlueVisible(true);
			}else if(red_view.isSelected()) {
				cube.setSecondFacesRedVisible(true);
			}else if(white_view.isSelected()) {
				cube.setSecondFacesWhiteVisible(true);
			}
			moves_made.setText("Moves: " + movesmade);
			
			canvas.fireCanvasChange();
			update_displays();
			
		} else if (e.getSource() == display_unachievable) {
			cube.setInteriorVisible(display_unachievable.isSelected());
			canvas.fireCanvasChange();
		} else if (e.getSource() == display_remoteness) {
			remoteness.setVisible(false);
			remoteness_label.setVisible(display_remoteness.isSelected());
			canvas.fireCanvasChange();
		} else if (e.getSource() == display_best_move) {
			best_move.setVisible(false);
			best_move_label.setVisible(display_best_move.isSelected());
			
			cube.setSecondFacesVisible(true);
			cube.setAwayRVisible(solved_map.getBestMove(cube.current_position) == "away from RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,1}), display_best_move.isSelected());
			cube.setTowardRVisible(solved_map.getBestMove( cube.current_position) == "towards RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,-1}), display_best_move.isSelected());
			cube.setAwayBVisible(solved_map.getBestMove(cube.current_position) == "away from BLUE", solved_map.isValidMove(cube.current_position, new int[] {0,1,0}), display_best_move.isSelected());
			cube.setTowardBVisible(solved_map.getBestMove(cube.current_position) == "towards BLUE",solved_map.isValidMove(cube.current_position, new int[] {0,-1,0}), display_best_move.isSelected());
			cube.setTowardWVisible(solved_map.getBestMove(cube.current_position) == "towards WHITE",solved_map.isValidMove(cube.current_position, new int[] {-1,0,0}), display_best_move.isSelected());
			cube.setAwayWVisible(solved_map.getBestMove(cube.current_position) == "away from WHITE", solved_map.isValidMove(cube.current_position, new int[] {1,0,0}),display_best_move.isSelected());
			if(!topdownview) {
				cube.setSecondFacesVisible(topdownview);
			} else if(blue_view.isSelected()) {
				cube.setSecondFacesBlueVisible(true);
			}else if(red_view.isSelected()) {
				cube.setSecondFacesRedVisible(true);
			}else if(white_view.isSelected()) {
				cube.setSecondFacesWhiteVisible(true);
			}
			canvas.fireCanvasChange();
			update_displays();
		} else if (e.getSource() == display_solution_path) {
			cube.setIntSolVisible(display_solution_path.isSelected());
			canvas.fireCanvasChange();
		} else if (e.getSource() == new_make) {
			random_faces = new_random.isSelected();
			//set cube size
			//set minimum remoteness
			//redo puzzle
			if(new_saved.isSelected()){
				make_new_puzzle(java.lang.Long.parseLong(saved_game_b.getText()),java.lang.Long.parseLong( saved_game_w.getText()),java.lang.Long.parseLong( saved_game_r.getText()));
			} else if(new_oski.isSelected() ) {
				make_new_puzzle(-173688576, -1777674529, 1081975273);
			}else{
			make_new_puzzle();		
			}
			int bs = java.lang.Integer.signum(cubefaces.BlueInt);
			long b = ((long) cubefaces.BlueInt*bs) + (1-bs)/2* ((long) java.lang.Integer.MAX_VALUE);
			int ws = java.lang.Integer.signum(cubefaces.WhiteInt);
			long w = ((long) cubefaces.WhiteInt*ws) + (1-ws)/2*( (long) java.lang.Integer.MAX_VALUE);
			int rs = java.lang.Integer.signum(cubefaces.RedInt);
			long r = ((long) cubefaces.RedInt*rs) + (1-rs)/2*((long) java.lang.Integer.MAX_VALUE);
			saved_game_b.setText(b + "");
			saved_game_w.setText(w + "");
			saved_game_r.setText(r + "");
			remoteness_label.setText("Remoteness: " + cubefaces.remoteness + "");
			remote.setText("Remoteness: " + cubefaces.remoteness + "");
			bushiness.setText("Bushiness: " +cubefaces.bushiness +"");
			subcomponents.setText("Subcomponents: " + cubefaces.subcomponents +"");	
			branches.setText("Branches: " + cubefaces.branches +"");
			brbydegree.setText("Branch by Degree: " + cubefaces.brfactor +"");
			maxbrdeg.setText("Max Branch Degree: " + cubefaces.maxbrfactor +"");
			turns.setText("Turns: " + cubefaces.turns+"");
			planeturns.setText("Not in Plane Turns: " + cubefaces.planeTurns+"");
			open_alleys.setText("Alleys: ");
			for(int i = 0; i< cubefaces.boardsize*2-1; i++) {
				open_alleys.setText(open_alleys.getText() + cubefaces.alleys[i]);
				if(i != cubefaces.boardsize*2-2) {
					open_alleys.setText(open_alleys.getText() + "/");
				}
			}
			num_viable.setText("Achievable: " + acheivable + "/" + (boardsize*boardsize*boardsize));
			abs_distance.setText("Linear Dist: " + cubefaces.sumlindistance);
			face_distance.setText("Solve Dist: " + cubefaces.sumsoldistance);
			//linwalls.setText("Linear Walls: " + cubefaces.linearwalls);
			
			set_view();
			cube.setSecondFacesVisible(topdownview);
			cube.setAwayRVisible(solved_map.getBestMove(cube.current_position) == "away from RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,1}), display_best_move.isSelected());
			cube.setTowardRVisible(solved_map.getBestMove( cube.current_position) == "towards RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,-1}), display_best_move.isSelected());
			cube.setAwayBVisible(solved_map.getBestMove(cube.current_position) == "away from BLUE", solved_map.isValidMove(cube.current_position, new int[] {0,1,0}), display_best_move.isSelected());
			cube.setTowardBVisible(solved_map.getBestMove(cube.current_position) == "towards BLUE",solved_map.isValidMove(cube.current_position, new int[] {0,-1,0}), display_best_move.isSelected());
			cube.setTowardWVisible(solved_map.getBestMove(cube.current_position) == "towards WHITE",solved_map.isValidMove(cube.current_position, new int[] {-1,0,0}), display_best_move.isSelected());
			cube.setAwayWVisible(solved_map.getBestMove(cube.current_position) == "away from WHITE", solved_map.isValidMove(cube.current_position, new int[] {1,0,0}), display_best_move.isSelected());
			if(!topdownview) {
				cube.setSecondFacesVisible(topdownview);
			}else if(blue_view.isSelected()) {
				cube.setSecondFacesBlueVisible(true);
			}else if(red_view.isSelected()) {
				cube.setSecondFacesRedVisible(true);
			}else if(white_view.isSelected()) {
				cube.setSecondFacesWhiteVisible(true);
			}
			movesmade =0;
			moves_made.setText("Moves: " + movesmade);
			traveled_map = new boolean[2*cubefaces.boardsize-1][2*cubefaces.boardsize-1][2*cubefaces.boardsize-1];
			canvas.fireCanvasChange();
			update_displays();
			canvas.fireCanvasChange();
		} else if (e.getSource() == side_view || e.getSource() == top_down_view) {
			topdownview = top_down_view.isSelected();
			cube.setSecondFacesVisible(topdownview);
			cube.setAwayRVisible(solved_map.getBestMove(cube.current_position) == "away from RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,1}), display_best_move.isSelected());
			cube.setTowardRVisible(solved_map.getBestMove( cube.current_position) == "towards RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,-1}), display_best_move.isSelected());
			cube.setAwayBVisible(solved_map.getBestMove(cube.current_position) == "away from BLUE", solved_map.isValidMove(cube.current_position, new int[] {0,1,0}), display_best_move.isSelected());
			cube.setTowardBVisible(solved_map.getBestMove(cube.current_position) == "towards BLUE",solved_map.isValidMove(cube.current_position, new int[] {0,-1,0}), display_best_move.isSelected());
			cube.setTowardWVisible(solved_map.getBestMove(cube.current_position) == "towards WHITE",solved_map.isValidMove(cube.current_position, new int[] {-1,0,0}), display_best_move.isSelected());
			cube.setAwayWVisible(solved_map.getBestMove(cube.current_position) == "away from WHITE", solved_map.isValidMove(cube.current_position, new int[] {1,0,0}), display_best_move.isSelected());
			if(!topdownview) {
				cube.setSecondFacesVisible(topdownview);
			} else if(blue_view.isSelected()) {
				cube.setSecondFacesBlueVisible(true);
			}else if(red_view.isSelected()) {
				cube.setSecondFacesRedVisible(true);
			}else if(white_view.isSelected()) {
				cube.setSecondFacesWhiteVisible(true);
			}
			
			set_view();
			canvas.fireCanvasChange();
		} else if (e.getSource() == blue_view) {
			cube.setSecondFacesVisible(true);
			cube.setAwayRVisible(solved_map.getBestMove(cube.current_position) == "away from RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,1}), display_best_move.isSelected());
			cube.setTowardRVisible(solved_map.getBestMove( cube.current_position) == "towards RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,-1}), display_best_move.isSelected());
			cube.setAwayBVisible(solved_map.getBestMove(cube.current_position) == "away from BLUE", solved_map.isValidMove(cube.current_position, new int[] {0,1,0}), display_best_move.isSelected());
			cube.setTowardBVisible(solved_map.getBestMove(cube.current_position) == "towards BLUE",solved_map.isValidMove(cube.current_position, new int[] {0,-1,0}), display_best_move.isSelected());
			cube.setTowardWVisible(solved_map.getBestMove(cube.current_position) == "towards WHITE",solved_map.isValidMove(cube.current_position, new int[] {-1,0,0}), display_best_move.isSelected());
			cube.setAwayWVisible(solved_map.getBestMove(cube.current_position) == "away from WHITE", solved_map.isValidMove(cube.current_position, new int[] {1,0,0}), display_best_move.isSelected());
			cube.setSecondFacesBlueVisible(true);
		
			topdownview = true;
			top_down_view.setSelected(true);
			set_view();
		} else if (e.getSource() == white_view) {
			cube.setSecondFacesVisible(true);
			cube.setAwayRVisible(solved_map.getBestMove(cube.current_position) == "away from RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,1}), display_best_move.isSelected());
			cube.setTowardRVisible(solved_map.getBestMove( cube.current_position) == "towards RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,-1}), display_best_move.isSelected());
			cube.setAwayBVisible(solved_map.getBestMove(cube.current_position) == "away from BLUE", solved_map.isValidMove(cube.current_position, new int[] {0,1,0}), display_best_move.isSelected());
			cube.setTowardBVisible(solved_map.getBestMove(cube.current_position) == "towards BLUE",solved_map.isValidMove(cube.current_position, new int[] {0,-1,0}), display_best_move.isSelected());
			cube.setTowardWVisible(solved_map.getBestMove(cube.current_position) == "towards WHITE",solved_map.isValidMove(cube.current_position, new int[] {-1,0,0}), display_best_move.isSelected());
			cube.setAwayWVisible(solved_map.getBestMove(cube.current_position) == "away from WHITE", solved_map.isValidMove(cube.current_position, new int[] {1,0,0}), display_best_move.isSelected());
			cube.setSecondFacesWhiteVisible(true);
			
			topdownview = true;
			top_down_view.setSelected(true);
			set_view();
		} else if (e.getSource() == red_view) {
			cube.setSecondFacesVisible(true);
			cube.setAwayRVisible(solved_map.getBestMove(cube.current_position) == "away from RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,1}), display_best_move.isSelected());
			cube.setTowardRVisible(solved_map.getBestMove( cube.current_position) == "towards RED", solved_map.isValidMove(cube.current_position, new int[] {0,0,-1}), display_best_move.isSelected());
			cube.setAwayBVisible(solved_map.getBestMove(cube.current_position) == "away from BLUE", solved_map.isValidMove(cube.current_position, new int[] {0,1,0}), display_best_move.isSelected());
			cube.setTowardBVisible(solved_map.getBestMove(cube.current_position) == "towards BLUE",solved_map.isValidMove(cube.current_position, new int[] {0,-1,0}), display_best_move.isSelected());
			cube.setTowardWVisible(solved_map.getBestMove(cube.current_position) == "towards WHITE",solved_map.isValidMove(cube.current_position, new int[] {-1,0,0}), display_best_move.isSelected());
			cube.setAwayWVisible(solved_map.getBestMove(cube.current_position) == "away from WHITE", solved_map.isValidMove(cube.current_position, new int[] {1,0,0}), display_best_move.isSelected());
			cube.setSecondFacesRedVisible(true);
			
			topdownview = true;
			top_down_view.setSelected(true);
			set_view();
		}
		
	}

	private void set_view() {
		if(!topdownview){
		//This one has blue ceiling
		cube.setRotation(new RotationMatrix(1,12,3,135));
		//cube.setRotation(new RotationMatrix(0,0,1,180));
		//cube.setRotation(new RotationMatrix(1,0,0,180));
		//cube.setRotation(new RotationMatrix(0,0,1,30));
		int zoom = (25 + (5-boardsize)*(5-boardsize))*17/18;
		cube.setCenter(0, -2, zoom);
		} else {
		//For looking down into the box
		cube.setRotation(new RotationMatrix(1000,1000,1000,120));
		cube.setRotation(new RotationMatrix(1,0,0,90));
		int zoom = (25 + 2*(5-boardsize)*(5-boardsize))/15*12;
		cube.setCenter(0, 0, zoom);
		//for red back, white top
		if(red_view.isSelected()) {
			cube.setRotation(new RotationMatrix(0,0,1,90));
			cube.setRotation(new RotationMatrix(1,0,0,180));
		} else if (white_view.isSelected()) {
		//add to get white back, second in front, blue top 
			cube.setRotation(new RotationMatrix(0,0,1,90));
			cube.setRotation(new RotationMatrix(0,1,0,90));
		}
		
		}
		//cube.setSecondFacesVisible(topdownview);
		
		//This one has blue floor *DOES NOT WORK*
		//cube.setRotation(new RotationMatrix(20000000,20000000,.0000005,90));
	}
	
}
