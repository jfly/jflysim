//package edu.berkeley.gcweb.gui.gamescubeman.Megaminx;
//
//import static java.lang.Math.abs;
//import static java.lang.Math.acos;
//import static java.lang.Math.cos;
//import static java.lang.Math.sin;
//import static java.lang.Math.tan;
//import static java.lang.Math.toDegrees;
//import static java.lang.Math.toRadians;
//
//import java.awt.Color;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Random;
//
//import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.DoubleSliderOption;
//import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleOption;
//import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleSticker;
//import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleTurn;
//import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.SpinnerOption;
//import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.TwistyPuzzle;
//import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.Utils;
//import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.PolygonCollection;
//import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.RotationMatrix;
//
//public class Megaminx extends TwistyPuzzle {
//
//	public Megaminx() {
//		super(0, 0, 4);
//	}
//
//	private PuzzleOption<Double> stickerGap = new DoubleSliderOption("gap", true, 30, 0, 100, 1000);
//	private PuzzleOption<Double> center_rad = new DoubleSliderOption("center_rad", true, 50, 0, 100, 100);
//	private PuzzleOption<Integer> layers = new SpinnerOption("layers", true, 1, 1, null, 1);
//	
//	@Override
//	public ArrayList<PuzzleOption<?>> _getDefaultOptions() {
//		ArrayList<PuzzleOption<?>> options = new ArrayList<PuzzleOption<?>>();
//		options.add(stickerGap);
//		options.add(center_rad);
//		options.add(layers);
//		return options;
//	}
//
//	public void puzzleOptionChanged(PuzzleOption<?> src) {
//		if(src == stickerGap || src == center_rad || src == layers) {
//			createPolys(true);
//			fireStateChanged(null);
//		}
//	}
//
//	@Override
//	public String getPuzzleName() {
//		return "Megaminx";
//	}
//
//	@Override
//	public String getState() {
//		return "";
//	}
//
//	@Override
//	public boolean isSolved() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public RotationMatrix getPreferredViewAngle() {
//		return new RotationMatrix(0, -0.5*(180-toDegrees(adjAngleRad)));
//	}
//
//	@Override
//	protected void _scramble() {
//		Random r = new Random();
//		for(int i = 0; i < 60; i++) {
//			int axis = r.nextInt(FACES.length);
//			int dir = r.nextInt(2)+1;
//			if(r.nextBoolean()) dir = -dir; //clockwise or counterclockwise
//			boolean wide = r.nextBoolean();
//			appendTurn(new MegaminxTurn(axis, dir, false, wide));
//		}
//	}
//	
////	private ArrayList<PolygonCollection<PuzzleSticker>> centerStickers, cornerStickers, edgeStickers;
////	private int[] centerPermutation, cornerPermutation, edgePermutation, cornerOrientation, edgeOrientation;
//	private ArrayList<PolygonCollection<PuzzleSticker>> faces;
//	
//	//this is the angle between adjacent faces
//	//see this: http://members.tripod.com/~Paul_Kirby/Linear/dodoca.html
//	private static final double adjAngleRad = 2*acos(0.5/sin(toRadians(72)));
//	
//	@Override
//	protected void _createPolys(boolean copyOld) {
//		//TODO - gap!
//		//TODO - reuse old polys
//		double gap = stickerGap.getValue();
//		double cornerRad = .85;
//		double edgeRad = cos(toRadians(36)) * cornerRad;
//		double centerRad = center_rad.getValue();
//		double centerEdgeRad = cos(toRadians(36)) * centerRad;
//
//		int numLayers = layers.getValue();
//		double layerDiag = (cornerRad - centerRad) / numLayers;
//		double layerHeight = (edgeRad - centerEdgeRad) / numLayers;
//		double cornerSideLen = layerHeight / cos(toRadians(108-90));
//		double edgeSlantWidth = tan(toRadians(108-90)) * layerHeight;
//		
//		double[] centerPoint = new double[] { -centerEdgeRad*tan(toRadians(36)), 0, centerEdgeRad };
//		
//		PolygonCollection<PuzzleSticker> stickers = new PolygonCollection<PuzzleSticker>();		
//		//constructing our numLayers*numLayers corners and numLayers edges
//		double z = centerPoint[2];
//		for(int i = 0; i < numLayers; i++) {
//			double x = centerPoint[0] + edgeSlantWidth*i;
//			
//			PuzzleSticker edge = new PuzzleSticker();
//			edge.addPoint(x, 0, z);
//			edge.addPoint(x + edgeSlantWidth, 0, z + layerHeight);
//			edge.addPoint(-x - edgeSlantWidth, 0, z + layerHeight);
//			edge.addPoint(-x, 0, z);
//			stickers.add(edge);
//			
//			for(int j = 0; j < numLayers; j++) {
//				PuzzleSticker corner = new PuzzleSticker();
//				corner.addPoint(0, 0, 0);
//				corner.addPoint(-cornerSideLen, 0, 0);
//				corner.addPoint(-cornerSideLen + edgeSlantWidth, 0, layerHeight);
//				corner.addPoint(edgeSlantWidth, 0, layerHeight);
//				corner.translate(x, 0, z);
//				stickers.add(corner);
//				x -= cornerSideLen;
//			}
//			z += layerHeight;
//		}
//
//		PuzzleSticker center = new PuzzleSticker();
//		PolygonCollection<PuzzleSticker> face = new PolygonCollection<PuzzleSticker>();
//		face.add(center);
//		RotationMatrix m = new RotationMatrix(1, -72);
//		for(int i = 0; i < 5; i++) {
//			center.addPoint(centerPoint);
//			centerPoint = m.multiply(centerPoint);
//			face.addAll(stickers.clone());
//			stickers.rotate(m);
//		}
//		
//		ArrayList<PolygonCollection<PuzzleSticker>> topHalf = new ArrayList<PolygonCollection<PuzzleSticker>>();
//		ArrayList<PolygonCollection<PuzzleSticker>> bottomHalf = new ArrayList<PolygonCollection<PuzzleSticker>>();
//
//		RotationMatrix halfIncrement = new RotationMatrix(1, 36);
//		
//		double cos45 = cos(toRadians(45));
//		face.rotate(halfIncrement);
//		face.translate(0, sin(adjAngleRad/2.0)*cornerRad*(cos45+cos(adjAngleRad/2.0)*(1+cos45)), 0);
//		topHalf.add(face.clone());
//		
//		face.rotate(halfIncrement);
//		face.rotate(new RotationMatrix(0, -(180-toDegrees(adjAngleRad))));
//		for(int i = 0; i < 5; i++) {
//			topHalf.add(face.clone());
//			face.rotate(m);
//		}
//
//		for(PolygonCollection<PuzzleSticker> f : topHalf) {
//			PolygonCollection<PuzzleSticker> f2 = f.clone();
//			f2.mirror(1);
//			f2.rotate(halfIncrement);
//			bottomHalf.add(f2);
//		}
//		
//		//this is to order the faces according to FACES
//		faces = new ArrayList<PolygonCollection<PuzzleSticker>>();
//
//		PolygonCollection<PuzzleSticker> Fface = topHalf.get(1);
//		setFace(Fface, "F");
//		faces.add(Fface);
//		PolygonCollection<PuzzleSticker> Uface = topHalf.get(0);
//		setFace(Uface, "U");
//		faces.add(Uface);
//		PolygonCollection<PuzzleSticker> Rface = topHalf.get(5);
//		setFace(Rface, "R");
//		faces.add(Rface);
//		PolygonCollection<PuzzleSticker> DRface = bottomHalf.get(1);
//		setFace(DRface, "DR");
//		faces.add(DRface);
//		PolygonCollection<PuzzleSticker> DLface = bottomHalf.get(2);
//		setFace(DLface, "DL");
//		faces.add(DLface);
//		PolygonCollection<PuzzleSticker> Lface = topHalf.get(2);
//		setFace(Lface, "L");
//		faces.add(Lface);
//		PolygonCollection<PuzzleSticker> Bface = bottomHalf.get(4);
//		setFace(Bface, "B");
//		faces.add(Bface);
//		PolygonCollection<PuzzleSticker> Dface = bottomHalf.get(0);
//		setFace(Dface, "D");
//		faces.add(Dface);
//		PolygonCollection<PuzzleSticker> BLDface = bottomHalf.get(3);
//		setFace(BLDface, "BLD");
//		faces.add(BLDface);
//		PolygonCollection<PuzzleSticker> BLface = topHalf.get(3);
//		setFace(BLface, "BL");
//		faces.add(BLface);
//		PolygonCollection<PuzzleSticker> BRface = topHalf.get(4);
//		setFace(BRface, "BR");
//		faces.add(BRface);
//		PolygonCollection<PuzzleSticker> BRDface = bottomHalf.get(5);
//		setFace(BRDface, "BRD");
//		faces.add(BRDface);
//		
//		for(PolygonCollection<PuzzleSticker> f : faces)
//			addPolys(f);
//	}
//	
//	private void setFace(PolygonCollection<PuzzleSticker> stickers, String face) {
//		for(PuzzleSticker ps : stickers)
//			ps.setFace(face);
//	}
//	
//	//to make things easier, we are only looking for face to start with the actual face
//	private static int getAxis(String face) {
//		int matchLength = -1, match = -1;
//		for(int c = 0; c < FACES.length; c++) {
//			if(face.startsWith(FACES[c]) && FACES[c].length() > matchLength) {
//				matchLength = FACES[c].length();
//				match = c;
//			}
//		}
//		return match;
//	}
//	private static final String[] FACES = { "F", "U", "R",   "DR", "DL", "L", //faces on the front 
//											"B", "D", "BLD", "BL", "BR", "BRD" }; //and the faces directly opposite them
//	private static int[] handPositions = new int[FACES.length];
//	//public so we can modify them with reflection
//	public static int F, U, R, DR, DL, L, B, D, BLD, BL, BR, BRD;
//	static {
//		Arrays.fill(handPositions, 1);
//		for(int i = 0; i < FACES.length; i++) {
//			try {
//				Megaminx.class.getField(FACES[i]).setInt(null, i);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	@Override
//	protected boolean _doTurn(String turn) {
//		boolean cubeRotation = turn.startsWith("[");
//		int axis, dir;
//		if(cubeRotation) {
//			axis = getAxis(turn.substring(1));
//			turn = turn.substring(FACES[axis].length() + 2);
//		} else {
//			axis = getAxis(turn);
//			turn = turn.substring(FACES[axis].length());
//		}
//		if(axis == -1)
//			return false;
//		
//		boolean wide = turn.startsWith("w");
//		if(wide)
//			turn = turn.substring(1);
//		
//		boolean ccw = turn.indexOf('\'') != -1;
//		turn = turn.replaceAll("'", "");
//		if(turn.length() == 0)
//			dir = 1;
//		else
//			dir = Integer.parseInt(turn);
//		if(ccw)
//			dir = -dir;
//		
//		appendTurn(new MegaminxTurn(axis, dir, cubeRotation, wide));
//		return true;
//	}
//
//	@Override
//	public HashMap<String, Color> getDefaultColorScheme() {
//		HashMap<String, Color> colorScheme = new HashMap<String, Color>();
//		colorScheme.put("F", Color.GRAY.brighter());
//		colorScheme.put("U", Color.WHITE);
//		colorScheme.put("R", new Color(102, 0, 102)); //purple
//		colorScheme.put("DR", Color.ORANGE.darker());
//		colorScheme.put("DL", Color.PINK);
//		colorScheme.put("L", Color.GREEN);
//		colorScheme.put("B", Color.YELLOW);
//		colorScheme.put("D", Color.BLUE);
//		colorScheme.put("BLD", Color.RED);
//		colorScheme.put("BL", Color.ORANGE); //this is actually a pretty light orange, using .brighter() makes it yellow
//		colorScheme.put("BR", Color.BLUE.darker().darker());
//		colorScheme.put("BRD", new Color(0, 255, 204)); //light blue
//		return colorScheme;
//	}
//	
//	class MegaminxTurn extends PuzzleTurn {
//		private int axis, dir, frames;
//		private boolean wide, cubeRotation;
//		private RotationMatrix rotate;
//		private Integer[][] faceCycles;
//		public MegaminxTurn(int axis, int dir, boolean cubeRotation, boolean wide) {
//			if(cubeRotation && wide) throw new RuntimeException();
//			
//			this.axis = axis;
//			this.dir = dir;
//			this.cubeRotation = cubeRotation;
//			this.wide = wide;
//			frames = getFramesPerAnimation();
//			rotate = getRotationMatrix(axis, -dir*72.0/frames);
//			faceCycles = getRotationCycles(axis);
//		}
//
//		private RotationMatrix getRotationMatrix(int axis, double amtCCW) {
//			double x, y, z;
//			String f = FACES[axis % 6];
//			if(f.equals("F")) {
//				x = 0;
//				y = tan(adjAngleRad - toRadians(90));
//				z = -1;
//			} else if(f.equals("U")) {
//				x = z = 0;
//				y = 1;
//			} else if(f.equals("R")) {
//				x = -cos(toRadians(18));
//				y = tan(adjAngleRad - toRadians(90));
//				z = -sin(toRadians(18));
//			} else if(f.equals("DR")) {
//				x = -cos(toRadians(54));
//				y = -tan(adjAngleRad - toRadians(90));
//				z = -sin(toRadians(54));
//			} else if(f.equals("DL")) {
//				x = cos(toRadians(54));
//				y = -tan(adjAngleRad - toRadians(90));
//				z = -sin(toRadians(54));
//			} else if(f.equals("L")) {
//				x = cos(toRadians(18));
//				y = tan(adjAngleRad - toRadians(90));
//				z = -sin(toRadians(18));
//			} else {
//				throw new RuntimeException();
//			}
//			if(axis >= 6)
//				amtCCW = -amtCCW;
//			return new RotationMatrix(x, y, z, amtCCW);
//		}
//		
//		@Override
//		public boolean isAnimationMergeble(PuzzleTurn other) {
//			return false;
////			MegaminxTurn o = (MegaminxTurn) other;
////			return isDisjoint(edgeCycles, o.edgeCycles) && isDisjoint(cornerCycles, o.cornerCycles) && isDisjoint(centerCycles, o.centerCycles);
//		}
//		
////		private boolean isDisjoint(ArrayList<int[]> a1, ArrayList<int[]> a2) {
////			HashSet<Integer> seen = new HashSet<Integer>();
////			for(int[] c : a1)
////				for(int i : c)
////					seen.add(i);
////			for(int[] c : a2)
////				for(int i : c)
////					if(seen.contains(i))
////						return false;
////			
////			return true;
////		}
//
//		@Override
//		public boolean isInspectionLegal() {
//			return cubeRotation;
//		}
//
//		@Override
//		public boolean isNullTurn() {
//			return dir == 0;
//		}
//
//		@Override
//		public PuzzleTurn mergeTurn(PuzzleTurn other) {
//			MegaminxTurn o = (MegaminxTurn) other;
//			if(axis == o.axis && wide == o.wide && cubeRotation == o.cubeRotation) {
//				int newDir = Utils.modulo(dir + o.dir, 5);
//				if(newDir >= 3)
//					newDir -= 5;
//				return new MegaminxTurn(axis, newDir, cubeRotation, wide);
//			}
//			return null;
//		}
//
//		@Override
//		public String toString() {
//			String turn = FACES[axis];
//			if(cubeRotation)
//				turn = "[" + turn + "]";
//			if(wide)
//				turn += "w";
//			if(abs(dir) != 1)
//				turn += abs(dir);
//			if(dir < 0)
//				turn += "'";
//			return turn;
//		}
//		
//		@Override
//		public boolean animateMove() {
////			rotatePieces(activeEdges, edgeStickers, rotate);
////			rotatePieces(activeCorners, cornerStickers, rotate);
////			rotatePieces(activeCenters, centerStickers, rotate);
//			if(cubeRotation) {
//				for(PolygonCollection<PuzzleSticker> face : faces)
//					face.rotate(rotate);
//			} else {
//				faces.get(axis).rotate(rotate); //we want to animate the center, so we rotate here as opposed to in the loop
//				for(Integer face : faceCycles[0]) {
//					int[][] stickerIndices = getIndicesOfTouchingStickers(axis, face, handPositions[axis]);
//					for(int sticker : stickerIndices[1]) {
//						faces.get(face).get(sticker).rotate(rotate);
//					}
//				}
//			}
//			return --frames == 0;
//		}
//
//		@Override
//		public void updateInternalRepresentation(boolean polygons) {
//			// TODO this method shouldn't need an argument...
//			// just fix sq1!
//			if(polygons) {
//				if(cubeRotation) {
//					cycle(faces, faceCycles, dir);
//					
//					//TODO - adjust for incorrect rotation
//					for(int i = 0; i < faceCycles[0].length; i++) {
//						getRotationCycles(axis)
//					}
//				} else {
//					for(int d = 0; d < Utils.modulo(dir, 5); d++) {
//						int lastFaceIndex = faceCycles[0].length - 1;
//						ArrayList<PuzzleSticker> lastFaceCopy = new ArrayList<PuzzleSticker>(faces.get(faceCycles[0][lastFaceIndex]));
//						ArrayList<PuzzleSticker> rotateFaceCopy = new ArrayList<PuzzleSticker>(faces.get(axis));
//						for(int i = lastFaceIndex; i >= 0; i--) {
//							int[][] srcStickers = getIndicesOfTouchingStickers(axis, Utils.moduloAcces(faceCycles[0], i-1), handPositions[axis]);
//							int[][] dstStickers = getIndicesOfTouchingStickers(axis, faceCycles[0][i], handPositions[axis]);
//							ArrayList<PuzzleSticker> srcFace = (i == 0 ? lastFaceCopy : faces.get(faceCycles[0][i-1]));
//							for(int stickerHandle = 0; stickerHandle < dstStickers[0].length; stickerHandle++) {
//								faces.get(axis).set(dstStickers[0][stickerHandle], rotateFaceCopy.get(srcStickers[0][stickerHandle]));
//								faces.get(faceCycles[0][i]).set(dstStickers[1][stickerHandle], srcFace.get(srcStickers[1][stickerHandle]));
//							}
//						}
//					}
//				}
//			}
//		}
//		
//		/**
//		 * returns 2 arrays (for face1 and face2), each with the indices of the stickers affected by turning layer
//		 */
//		private int[][] getIndicesOfTouchingStickers(int face1, int face2, int layer) {
//			//TODO - blah
//			return new int[][] { { 1 }, { 1 } };
//		}
//		
//		/**
//		 * returns 2 arrays, where the 0th index is the array of the faces touching axis
//		 */
//		private Integer[][] getRotationCycles(int axis) {
//			Integer[][] cycles;
//			String f = FACES[axis % 6];
//			if(f.equals("F")) {
//				cycles = new Integer[][] {
//						{ U, R, DR, DL, L },
//						{ BR, BRD, D, BLD, BL }
//						};
//			} else if(f.equals("U")) {
//				cycles = new Integer[][] { 
//						{ F, L, BL, BR, R },
//						{ DR, DL, BLD, B, BRD }
//						};
//			} else if(f.equals("R")) {
//				cycles = new Integer[][] { 
//						{ F, U, BR, BRD, DR },
//						{ L, BL, B, D, DL }
//				};
//			} else if(f.equals("DR")) {
//				cycles = new Integer[][] { 
//						{ F, R, BRD, D, DL },
//						{ U, BR, B, BLD, L }
//				};
//			} else if(f.equals("DL")) {
//				cycles = new Integer[][] { 
//						{ F, DR, D, BLD, L },
//						{ U, R, BRD, B, BL }
//				};
//			} else if(f.equals("L")) {
//				cycles = new Integer[][] { 
//						{ U, F, DL, BLD, BL },
//						{ R, DR, D, B, BR }
//				};
//			} else {
//				throw new RuntimeException();
//			}
//			if(axis >= 6)
//				Utils.reverse(cycles);
//			
//			return cycles;
//		}
//		
//		private <H> void cycle(ArrayList<H> cycleMe, Integer[][] cycles, int dir) {
//			ArrayList<H> copy = new ArrayList<H>(cycleMe);
//			for(Integer[] cycle : cycles)
//				for(int i = 0; i < cycle.length; i++)
//					cycleMe.set(Utils.moduloAcces(cycle, i+dir), copy.get(cycle[i]));
//		}
//	}
//}
