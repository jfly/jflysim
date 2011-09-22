package edu.berkeley.gcweb.gui.gamescubeman.Pyraminx;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.DoubleSliderOption;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleOption;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleSticker;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleTurn;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.TwistyPuzzle;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.Utils;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.PolygonCollection;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.RotationMatrix;

public class Pyraminx extends TwistyPuzzle {
	public Pyraminx() {
		super(0, 0, 4);
	}
	//NOTE: there's a big difference between the notation used for turns and the notation used for faces
	public HashMap<String, Color> getDefaultColorScheme() {
		HashMap<String, Color> colors = new HashMap<String, Color>();
		colors.put("F", Color.RED);
		colors.put("L", Color.GREEN);
		colors.put("R", Color.BLUE);
		colors.put("D", Color.YELLOW);
		return colors;
	}
	public String getPuzzleName() {
		return "Pyraminx";
	}

	public String getState() {
		// TODO - make this work with cube rotations and sticker changes
		return Utils.join(",", edgeLocations.toArray()) + ";"
				+ Utils.join(",", edgeOrientations.toArray()) + ";"
				+ Utils.join(",", centerOrientations.toArray());
	}

	public boolean isSolved() {
		for(int i=0; i<edgeLocations.size(); i++)
			if(i != edgeLocations.get(i) || edgeOrientations.get(i) != 0)
				return false;
		for(int i=0; i<centerOrientations.size(); i++)
			if(centerOrientations.get(i) != 0 || tipOrientations.get(i) != 0)
				return false;
		return true;
	}

//	private PuzzleOption<Double> gap = new DoubleSliderOption("gap", true, 0, 0, 50, 100);
	private PuzzleOption<Double> gap = new DoubleSliderOption("gap", true, 10, 0, 50, 100);

	@Override
	public List<PuzzleOption<?>> _getDefaultOptions() {
		ArrayList<PuzzleOption<?>> options = new ArrayList<PuzzleOption<?>>();
		options.add(gap);
		return options;
	}
	
	public void puzzleOptionChanged(PuzzleOption<?> src) {
		if(src == gap) {
			createPolys(true);
			fireStateChanged(null);
		}
	}
	
	@Override
	public RotationMatrix[] getPreferredViewAngles() {
		return new RotationMatrix[] { new RotationMatrix(), new RotationMatrix(0, faceDegree).multiply(new RotationMatrix(1, 60)) };
	}
	
	private ArrayList<PolygonCollection<PuzzleSticker>> tips = null;
	private ArrayList<PolygonCollection<PuzzleSticker>> centers = null;
	private ArrayList<PolygonCollection<PuzzleSticker>> edges = null;
	private ArrayList<Integer> edgeLocations;
	private ArrayList<Integer> edgeOrientations;
	private ArrayList<Integer> tipOrientations;
	private ArrayList<Integer> centerOrientations;
	private int[] axisMap;
	
	private static double sin60 = Math.sin(Math.toRadians(60));
	private static double sin30 = Math.sin(Math.toRadians(30));
	private static double cos30 = Math.cos(Math.toRadians(30));
	private static double faceDegree = Math.toDegrees(1/(4*cos30*cos30));
	private static final double stickerLen = .8;
	private double halfSticker = stickerLen / 2.0;
	private double stickerHeight = sin60*stickerLen;
	private double radius = 2*stickerHeight;
	private double layerHeight = Math.cos(Math.toRadians(faceDegree)) * stickerHeight;
	private double puzzleHeight = 3*layerHeight;
	private double centerHeight = (puzzleHeight - radius*radius/2.0)/2.0;
	protected void _createPolys(boolean copyOld) {
		axisMap = new int[] { 0, 1, 2, 3 };
		PuzzleSticker ts = new PuzzleSticker();
		double stickerGap = gap.getValue();
		ts.addPoint(0, stickerHeight - stickerGap, 0);
		ts.addPoint(halfSticker - stickerGap*cos30, stickerGap*sin30, 0);
		ts.addPoint(-halfSticker + stickerGap*cos30, stickerGap*sin30, 0);
		
		PuzzleSticker cs = ts.clone();
		cs.rotate(new RotationMatrix(2, 180));
		
		PuzzleSticker es = ts.clone();
		es.translate(-halfSticker, -stickerHeight, 0);
		
		PolygonCollection<PuzzleSticker> facePolys = new PolygonCollection<PuzzleSticker>();
		facePolys.add(ts);
		facePolys.add(cs);
		facePolys.add(es);
		RotationMatrix third = new RotationMatrix(2, 360/3);
		facePolys.translate(0, stickerHeight, 0);
		for(int i = 0; i < 2; i++) {
			(ts = ts.clone()).rotate(third);
			(cs = cs.clone()).rotate(third);
			(es = es.clone()).rotate(third);
			facePolys.add(ts);
			facePolys.add(cs);
			facePolys.add(es);
		}
		facePolys.translate(0, -2*stickerHeight, 0);
		
		PolygonCollection<PuzzleSticker> bottom = facePolys.clone();
		bottom.rotate(new RotationMatrix(0, 90));
		bottom.translate(0, -3*layerHeight, 2*stickerHeight);
		for(PuzzleSticker ps : bottom)
			ps.setFace("D");
		addPolys(bottom);
		facePolys.rotate(new RotationMatrix(0, faceDegree));
		facePolys.translate(0, 3*layerHeight-centerHeight, 0);
		bottom.translate(0, 3*layerHeight-centerHeight, 0);
		
		ArrayList<PolygonCollection<PuzzleSticker>> ogTips = tips;
		ArrayList<PolygonCollection<PuzzleSticker>> ogCenters = centers;
		ArrayList<PolygonCollection<PuzzleSticker>> ogEdges = edges;
		tips = new ArrayList<PolygonCollection<PuzzleSticker>>();
		centers = new ArrayList<PolygonCollection<PuzzleSticker>>();
		edges = new ArrayList<PolygonCollection<PuzzleSticker>>();
		
		ArrayList<Integer> ogEdgeLocations = edgeLocations;
		ArrayList<Integer> ogEdgeOrientations = edgeOrientations;
		ArrayList<Integer> ogTipOrientations = tipOrientations;
		ArrayList<Integer> ogCenterOrientations = centerOrientations;
		edgeLocations = new ArrayList<Integer>();
		edgeOrientations = new ArrayList<Integer>();
		tipOrientations = new ArrayList<Integer>();
		centerOrientations = new ArrayList<Integer>();
		for(int i=0; i<4; i++) {
			tips.add(new PolygonCollection<PuzzleSticker>());
			centers.add(new PolygonCollection<PuzzleSticker>());
			tipOrientations.add(0); centerOrientations.add(0);
		}
		for(int i=0; i<6; i++) {
			edges.add(new PolygonCollection<PuzzleSticker>());
			edgeLocations.add(i);
			edgeOrientations.add(0);
		}
		//the elements of facePolys should be as follows
		//	      /\
		//	     /0_\
		//	    /\1 /\
		//	   /8_\/2_\
		//	  /\7 /\4 /\
		//	 /6_\/5_\/_3\

		tips.get(RIGHT).add(bottom.get(3));
		tips.get(LEFT).add(bottom.get(6));
		tips.get(BACK).add(bottom.get(0));
		centers.get(RIGHT).add(bottom.get(4));
		centers.get(LEFT).add(bottom.get(7));
		centers.get(BACK).add(bottom.get(1));
		edges.get(3).add(bottom.get(5));
		edges.get(4).add(bottom.get(8));
		edges.get(5).add(bottom.get(2));
		String[] faces = new String[] { "F", "R", "L" };
		for(int i=0; i<faces.length; i++) {
			String face = faces[i];
			facePolys = facePolys.clone();
			for(PuzzleSticker ps : facePolys)
				ps.setFace(face);
			addPolys(facePolys);
			tips.get(UP).add(facePolys.get(0));
			centers.get(UP).add(facePolys.get(1));
			int leftCenter = -1, rightCenter = -1, leftEdge = -1, rightEdge = -1, bottomEdge = -1;
			if(face.equals("F")) {
				leftCenter = LEFT;
				rightCenter = RIGHT;
				leftEdge = 1;
				rightEdge = 0;
				bottomEdge = 3;
			} else if(face.equals("R")) {
				leftCenter = RIGHT;
				rightCenter = BACK;
				leftEdge = 0;
				rightEdge = 2;
				bottomEdge = 5;
			} else if(face.equals("L")) {
				leftCenter = BACK;
				rightCenter = LEFT;
				leftEdge = 2;
				rightEdge = 1;
				bottomEdge = 4;
			}
			tips.get(leftCenter).add(facePolys.get(6));
			tips.get(rightCenter).add(facePolys.get(3));
			centers.get(leftCenter).add(facePolys.get(7));
			centers.get(rightCenter).add(facePolys.get(4));
			edges.get(leftEdge).add(facePolys.get(8));
			edges.get(rightEdge).add(facePolys.get(2));
			edges.get(bottomEdge).add(facePolys.get(5));
			if(i != 0) //we don't want to rotate the front
				facePolys.rotate(new RotationMatrix(1, 360/3), false);
		}
		if(copyOld) {
			for(int i=0; i<ogTips.size(); i++) {
				tips.get(i).rotate(ogTips.get(i).getNetRotations());
			}
			for(int i=0; i<ogCenters.size(); i++) {
				centers.get(i).rotate(ogCenters.get(i).getNetRotations());
			}
			for(int i=0; i<ogEdges.size(); i++) {
				edges.get(i).rotate(ogEdges.get(i).getNetRotations());
			}
			edgeLocations = ogEdgeLocations;
			edgeOrientations = ogEdgeOrientations;
			tipOrientations = ogTipOrientations;
			centerOrientations = ogCenterOrientations;
		}
	}
	
	//TODO - do puzzle rotations! how?
	protected boolean _doTurn(String turn, boolean invert) {
		int layer = 1;
		if(turn.startsWith("(")) {
			turn = turn.substring(1, turn.length()-1);
			layer = 3;
		}
		boolean ccw = turn.endsWith("'");
		if(ccw)	turn = turn.substring(0, turn.length()-1);
		if(turn.length() != 1) return false;
		char face = turn.charAt(0);
		if(Character.isUpperCase(face)) {
			layer = 2;
			face = Character.toLowerCase(face);
		}
		int axis;
		if(face == 'u') {
			axis = UP;
		} else if(face == 'r') {
			axis = RIGHT;
		} else if(face == 'l') {
			axis = LEFT;
		} else if(face == 'b') {
			axis = BACK;
		} else {
			return false;
		}
		if(invert) ccw = !ccw;
		appendTurn(new PyraminxTurn(axis, ccw ? -1 : 1, layer));
		return true;
	}
	
	private static final String AXES="URLB";
	private static final int UP=0, RIGHT=1, LEFT=2, BACK=3;
	private class PyraminxTurn extends PuzzleTurn {
		private int axis, og_axis, dir, layer;
		public PyraminxTurn(int axis, int dir, int layer) {
			super(getFramesPerAnimation());
			this.axis = axis;
			dir = Utils.modulo(dir, 3);
			if(dir == 2) dir = -1;
			this.dir = dir;
			this.layer = layer;
		}
		
		private int frames = -1;
		private RotationMatrix rotation;
		private Integer[] edgeIndices;
		public void _animateMove(boolean firstFrame) {
			if(firstFrame) {
				frames = getFramesPerAnimation();
				double degree = -dir*360/3.0/frames;
				switch(axis) {
				case UP:
					rotation = new RotationMatrix(1, degree);
					break;
				case LEFT:
					rotation = new RotationMatrix(sin60*radius, -centerHeight, -stickerHeight, degree);
					break;
				case RIGHT:
					rotation = new RotationMatrix(-sin60*radius, -centerHeight, -stickerHeight, degree);
					break;
				case BACK:
					rotation = new RotationMatrix(0, -centerHeight, radius, degree);
					break;
				}
				og_axis = axisMap[axis];
				switch(og_axis) {
				case UP:
					edgeIndices = new Integer[] { 0, 1, 2 };
					break;
				case LEFT:
					edgeIndices = new Integer[] { 3, 4, 1 };
					break;
				case RIGHT:
					edgeIndices = new Integer[] { 3, 0, 5 };
					break;
				case BACK:
					edgeIndices = new Integer[] { 2, 4, 5 };
					break;
				}
			}
			switch(layer) {
			case 2:
				centers.get(og_axis).rotate(rotation);
				for(int i : edgeIndices)
					edges.get(edgeLocations.get(i)).rotate(rotation);
			case 1:
				tips.get(og_axis).rotate(rotation);
				break;
			case 3:
				for(PolygonCollection<PuzzleSticker> center : centers) {
					center.rotate(rotation);
				}
				for(PolygonCollection<PuzzleSticker> edge : edges)  {
					edge.rotate(rotation);
				}
				for(PolygonCollection<PuzzleSticker> tip : tips) {
					tip.rotate(rotation);
				}
				break;
			}
		}

		public boolean isAnimationMergeble(PuzzleTurn other) {
			PyraminxTurn o = (PyraminxTurn) other;
			return (this.layer != 3 && o.layer != 3) && (this.layer == 1 || o.layer == 1);
		}
		public boolean isInspectionLegal() {
			return this.layer == 3;
		}
		public boolean isNullTurn() {
			return dir == 0;
		}
		public PuzzleTurn mergeTurn(PuzzleTurn other) {
			PyraminxTurn o = (PyraminxTurn) other;
			if(o.axis == this.axis && o.layer == this.layer) {
				return new PyraminxTurn(this.axis, this.dir + o.dir, this.layer);
			}
			return null;
		}
		public String toString() {
			char face = AXES.charAt(axis);
			if(layer == 1 || layer == 3) face = Character.toLowerCase(face);
			else if(layer == 2) face = Character.toUpperCase(face);
			String turn = face + ((dir == -1) ? "'" : "");
			if(layer == 3)
				turn = "(" + turn + ")";
			return turn;
		}
		//TODO - i'm thinking that it would be better to never update the internal representation of the polygons
		public void updateInternalRepresentation(boolean polygons) {
			if(polygons) {
				switch(layer) {
				case 2:
					cycle(edgeLocations, edgeIndices, dir);
					centerOrientations.set(og_axis, Utils.modulo(centerOrientations.get(og_axis) + dir, 3));
					cycle(edgeOrientations, edgeIndices, dir);
					//NOTE: we're updating the orientations *after* their permutations have been updated
					if(og_axis == LEFT || og_axis == RIGHT) {
						//these twists don't affect edge orientation
					} else {
						int edge1 = -1, edge2 = -1;
						if(og_axis == BACK) {
							if(dir == -1) {
								edge1 = 4;
								edge2 = 5;
							} else if(dir == 1) {
								edge1 = 5;
								edge2 = 2;
							}
						} else if(og_axis == UP) {
							if(dir == -1) {
								edge1 = 0;
								edge2 = 1;
							} else if(dir == 1) {
								edge1 = 1;
								edge2 = 2;
							}
						}
						edgeOrientations.set(edge1, 1-edgeOrientations.get(edge1));
						edgeOrientations.set(edge2, 1-edgeOrientations.get(edge2));
					}
				case 1:
					tipOrientations.set(og_axis, Utils.modulo(tipOrientations.get(og_axis) + dir, 3));
					break;
				case 3:
					int dir = this.dir;
					if(dir == -1) dir = 2;
					for(int d = 0; d < dir; d++) {
						switch(axis) {
						case LEFT:
							cycle(axisMap, UP, RIGHT, BACK);
							break;
						case RIGHT:
							cycle(axisMap, UP, BACK, LEFT);
							break;
						case BACK:
							cycle(axisMap, RIGHT, UP, LEFT);
							break;
						case UP:
							cycle(axisMap, LEFT, BACK, RIGHT);
							break;
						}
					}
					break;
				}
			}
		}
		
		private void cycle(int[] arr, int i1, int i2, int i3) {
			int temp = arr[i1];
			arr[i1] = arr[i3];
			arr[i3] = arr[i2];
			arr[i2] = temp;
		}
		private void cycle(ArrayList<Integer> arr, Integer[] indices, int offset) {
			ArrayList<Integer> clone = new ArrayList<Integer>(arr);
			for(int i=0; i<indices.length; i++) {
				arr.set(Utils.moduloAcces(indices, i+offset), clone.get(indices[i])); 
			}
		}

		@Override
		public PuzzleTurn invert() {
			// TODO INVERTING
			return null;
		}
	}
	@Override
	protected void _cantScramble() {
		// TODO Auto-generated method stub
		
	}
	protected void _scramble() {
		String scramble = generateScramble();
		for(String turn : scramble.split(" ")) {
			boolean ccw = turn.endsWith("'");
			if(ccw)
				turn = turn.substring(0, turn.length()-1);
			int layer = Character.isUpperCase(turn.charAt(0)) ? 2 : 1;
			
			//TODO - hack for gamescrafters
			if(layer == 1) continue;
			
			int axis = AXES.indexOf(Character.toUpperCase(turn.charAt(0)));
			appendTurn(new PyraminxTurn(axis, ccw ? -1 : 1, layer));
		}
	}
	
	//copied from cct's PyraminxScramble
	private static class JavascriptArray <H> extends Vector<H> {
		public H set(int index, H element) {
			if(index >= super.size())
				super.setSize(index + 1);
			return super.set(index, element);
		}
	}

	JavascriptArray<String> b = new JavascriptArray<String>();
	static JavascriptArray<Integer> g = new JavascriptArray<Integer>();
	static JavascriptArray<Integer> f = new JavascriptArray<Integer>();
	JavascriptArray<Integer> k = new JavascriptArray<Integer>();
	JavascriptArray<Integer> h = new JavascriptArray<Integer>();
	JavascriptArray<Integer> i = new JavascriptArray<Integer>();
	static JavascriptArray<JavascriptArray<Integer>> d = new JavascriptArray<JavascriptArray<Integer>>();
	static JavascriptArray<JavascriptArray<Integer>> e = new JavascriptArray<JavascriptArray<Integer>>();

	static {
		int c, p, q, l, m;
		for (p = 0; p < 720; p++) {
			g.set(p, -1);
			d.set(p, new JavascriptArray<Integer>());
			for (m = 0; m < 4; m++)
				d.get(p).set(m, w(p, m));
		}
		g.set(0, 0);
		for (l = 0; l <= 6; l++)
			for (p = 0; p < 720; p++)
				if (g.get(p) == l)
					for (m = 0; m < 4; m++) {
						q = p;
						for (c = 0; c < 2; c++) {
							q = d.get(q).get(m);
							if (g.get(q) == -1)
								g.set(q, l + 1);
						}
					}
		for (p = 0; p < 2592; p++) {
			f.set(p, -1);
			e.set(p, new JavascriptArray<Integer>());
			for (m = 0; m < 4; m++)
				e.get(p).set(m, x(p, m));
		}
		f.set(0, 0);
		for (l = 0; l <= 5; l++)
			for (p = 0; p < 2592; p++)
				if (f.get(p) == l)
					for (m = 0; m < 4; m++) {
						q = p;
						for (c = 0; c < 2; c++) {
							q = e.get(q).get(m);
							if (f.get(q) == -1)
								f.set(q, l + 1);
						}
					}
	}

	private String generateScramble() {
		k = new JavascriptArray<Integer>();
		int t = 0, s = 0, q = 0, m, l, p;
		h = new JavascriptArray<Integer>();
		h.add(0);
		h.add(1);
		h.add(2);
		h.add(3);
		h.add(4);
		h.add(5);
		for (m = 0; m < 4; m++) {
			p = m + n(6 - m);
			l = h.get(m);
			h.set(m, h.get(p));
			h.set(p, l);
			if (m != p)
				s++;
		}
		if (s % 2 == 1) {
			l = h.get(4);
			h.set(4, h.get(5));
			h.set(5, l);
		}
		s = 0;
		i = new JavascriptArray<Integer>();
		for (m = 0; m < 5; m++) {

			i.set(m, n(2));
			s += i.get(m);
		}
		i.set(5, s % 2);
		for (m = 6; m < 10; m++) {
			i.set(m, n(3));
		}
		for (m = 0; m < 6; m++) {
			l = 0;
			for (p = 0; p < 6; p++) {
				if (h.get(p) == m)
					break;
				if (h.get(p) > m)
					l++;
			}
			q = q * (6 - m) + l;
		}
		for (m = 9; m >= 6; m--)
			t = t * 3 + i.get(m);
		for (m = 4; m >= 0; m--)
			t = t * 2 + i.get(m);
		if (q != 0 || t != 0)
			for (m = 0; m < 12; m++)
				if (v(q, t, m, -1))
					break;

		String scramble = "";
		for (p = 0; p < k.size(); p++)
			scramble += new String[] { "U", "L", "R", "B" }[k.get(p) & 7]
					+ new String[] { "", "'" }[(k.get(p) & 8) / 8] + " ";
		String[] tips = new String[] { "l", "r", "b", "u" };
		for (p = 0; p < 4; p++) {
			q = n(3);
			if (q < 2)
				scramble += tips[p] + new String[] { "", "'" }[q] + " ";
		}
		return scramble;
	}

	private boolean v(int q, int t, int l, int c) {
		if (l == 0) {
			if (q == 0 && t == 0)
				return true;
		} else {
			if (g.get(q) > l || f.get(t) > l)
				return false;
			int p, s, a, m;
			for (m = 0; m < 4; m++)
				if (m != c) {
					p = q;
					s = t;
					for (a = 0; a < 2; a++) {
						p = d.get(p).get(m);
						s = e.get(s).get(m);
						k.set(k.size(), m + 8 * a);
						if (v(p, s, l - 1, m))
							return true;
						k.setSize(k.size() - 1);
					}
				}
		}
		return false;
	}

	private static int w(int p, int m) {
		int a, l, c, q = p;
		JavascriptArray<Integer> s = new JavascriptArray<Integer>();
		for (a = 1; a <= 6; a++) {
			c = (int) Math.floor(q / a);
			l = q - a * c;
			q = c;
			for (c = a - 1; c >= l; c--) {
				int val = c < s.size() ? s.get(c) : 0;
				s.set(c + 1, val);
			}
			s.set(l, 6 - a);
		}
		if (m == 0)
			y(s, 0, 3, 1);
		if (m == 1)
			y(s, 1, 5, 2);
		if (m == 2)
			y(s, 0, 2, 4);
		if (m == 3)
			y(s, 3, 4, 5);
		q = 0;
		for (a = 0; a < 6; a++) {
			l = 0;
			for (c = 0; c < 6; c++) {
				if (s.get(c) == a)
					break;
				if (s.get(c) > a)
					l++;
			}
			q = q * (6 - a) + l;
		}
		return q;
	}

	private static int x(int p, int m) {
		int a, l, c, t = 0, q = p;
		JavascriptArray<Integer> s = new JavascriptArray<Integer>();
		for (a = 0; a <= 4; a++) {
			s.set(a, q & 1);
			q >>= 1;
			t ^= s.get(a);
		}
		s.set(5, t);
		for (a = 6; a <= 9; a++) {
			c = (int) Math.floor(q / 3);
			l = q - 3 * c;
			q = c;
			s.set(a, l);
		}
		if (m == 0) {
			s.set(6, s.get(6) + 1);
			if (s.get(6) == 3)
				s.set(6, 0);
			y(s, 0, 3, 1);
			s.set(1, s.get(1) ^ 1);
			s.set(3, s.get(3) ^ 1);
		}
		if (m == 1) {
			s.set(7, s.get(7) + 1);
			if (s.get(7) == 3)
				s.set(7, 0);
			y(s, 1, 5, 2);
			s.set(2, s.get(2) ^ 1);
			s.set(5, s.get(5) ^ 1);
		}
		if (m == 2) {
			s.set(8, s.get(8) + 1);
			if (s.get(8) == 3)
				s.set(8, 0);
			y(s, 0, 2, 4);
			s.set(0, s.get(0) ^ 1);
			s.set(2, s.get(2) ^ 1);
		}
		if (m == 3) {
			s.set(9, s.get(9) + 1);
			if (s.get(9) == 3)
				s.set(9, 0);
			y(s, 3, 4, 5);
			s.set(3, s.get(3) ^ 1);
			s.set(4, s.get(4) ^ 1);
		}
		q = 0;
		for (a = 9; a >= 6; a--)
			q = q * 3 + s.get(a);
		for (a = 4; a >= 0; a--)
			q = q * 2 + s.get(a);
		return q;
	}
	 
	private static void y(JavascriptArray<Integer> p, int a, int c, int t) {
	 int s = p.get(a);
	 p.set(a, p.get(c));
	 p.set(c, p.get(t));
	 p.set(t, s);
	}
	 
	private static int n(int c) {
	 return (int) Math.floor(Math.random()*c);
	}
}
