package edu.berkeley.gcweb.gui.gamescubeman.Cuboid;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class CubeFace {
	private static HashMap<Character, CubeFace> namesFaces = new HashMap<Character, CubeFace>();
	public static ArrayList<CubeFace> faces = new ArrayList<CubeFace>();
	public final static CubeFace UP = new CubeFace('U', 1, true, 0, 2, Color.WHITE);
	public final static CubeFace DOWN = new CubeFace('D', UP, Color.YELLOW);
	public final static CubeFace LEFT = new CubeFace('L', 0, true, 2, 1, Color.GREEN);
	public final static CubeFace RIGHT = new CubeFace('R', LEFT, Color.BLUE);
	public final static CubeFace FRONT = new CubeFace('F', 2, false, 0, 1, Color.RED);
	public final static CubeFace BACK = new CubeFace('B', FRONT, new Color(255, 128, 0));
	//cw_cw is whether turning the face clockwise is the same as rotating clockwise about the axis
	private boolean cw_cw;
	//isClockwise indicates whether the first dimension is in the clockwise direction
	private boolean isFirstAxisClockwise = true;
	private int rotationAxis, widthAxis, heightAxis;
	private int index;
	private Color color;
	private char faceName;
	private CubeFace(char faceName, int rotationAxis, boolean cw_cw, int widthAxis, int heightAxis, Color color) {
		this.faceName = faceName;
		namesFaces.put(faceName, this);
		this.rotationAxis = rotationAxis;
		this.cw_cw = cw_cw;
		this.widthAxis = widthAxis;
		this.heightAxis = heightAxis;
		this.color = color;
		index = faces.size();
		faces.add(this);
	}
	private CubeFace opposite;
	private CubeFace(char faceName, CubeFace opposite, Color color) {
		this(faceName, opposite.rotationAxis, !opposite.cw_cw, opposite.widthAxis, opposite.heightAxis, color);
		this.opposite = opposite;
		isFirstAxisClockwise = false;
		opposite.opposite = this;
	}
	public String toString() {
		return "" + getFaceName();
	}
	public char getFaceName() {
		return faceName;
	}
	public static CubeFace decodeFace(char face) {
		return namesFaces.get(Character.toUpperCase(face));
	}
	public static CubeFace decodeCubeRotation(char face) {
		switch(face) {
		case 'x':
			return RIGHT;
		case 'y':
			return UP;
		case 'z':
			return FRONT;
		default:
			return null;
		}
	}
	public static CubeFace[] faces() {
		return faces.toArray(new CubeFace[0]);
	}
	public int index() {
		return index;
	}
	public int getWidthAxis() {
		return widthAxis;
	}
	public int getHeightAxis() {
		return heightAxis;
	}
	public int getRotationAxis() {
		return rotationAxis;
	}
	public CubeFace getOppositeFace() {
		return opposite;
	}
	public Color getColor() {
		return color;
	}
	public boolean isFirstAxisClockwise() {
		return isFirstAxisClockwise;
	}
	public boolean isCWWithAxis() {
		return cw_cw;
	}
}