package edu.berkeley.gcweb.gui.gamescubeman.Cuboid;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.ComboOption;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.DoubleSliderOption;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleOption;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleSticker;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.SpinnerOption;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.TwistyPuzzle;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.Utils;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.RotationMatrix;

public class Cuboid extends TwistyPuzzle implements ActionListener {
	//We're representing a cube of size 2 units by 2 units by 2 units
	//centered at (0, 0, 4). The cube can occupy anything in the sphere of
	//diameter sqrt(1+1+1) ~= 1.73 < 3.
	public Cuboid() {
		super(0, 0, 4);
	}
	
	public String getPuzzleName() {
		return "Cuboid";
	}
	
	public RotationMatrix[] getPreferredViewAngles() {
		return new RotationMatrix[] { new RotationMatrix(0, -45), new RotationMatrix(0, -45).multiply(new RotationMatrix(1, 45)) };
	}

	private static final String NORMAL = "Normal";
	private static final String VOID = "Void";
	private static final String BABYFACE = "Babyface";
	
	private PuzzleOption<Double> gapOption = new DoubleSliderOption("gap", true, 25, 0, 100, 100);
	private PuzzleOption<Integer> dimXOption = new SpinnerOption("dim_x", true, 2, 1, null, 1);
	private PuzzleOption<Integer> dimYOption = new SpinnerOption("dim_y", true, 2, 1, null, 1);
	private PuzzleOption<Integer> dimZOption = new SpinnerOption("dim_z", true, 2, 1, null, 1);
	private PuzzleOption<String> variationOption = new ComboOption("variation", true, NORMAL, new String[] { NORMAL, VOID, BABYFACE });
	
	public List<PuzzleOption<?>> _getDefaultOptions() {
		ArrayList<PuzzleOption<?>> options = new ArrayList<PuzzleOption<?>>();
		options.add(gapOption);
		options.add(dimXOption);
		options.add(dimYOption);
		options.add(dimZOption);
		options.add(variationOption);
		return options;
	}
	
	public int dimensions(int axis) {
		switch(axis) {
		case 0:
			return dimXOption.getValue();
		case 1:
			return dimYOption.getValue();
		case 2:
			return dimZOption.getValue();
		default:
			throw new RuntimeException();
		}
	}
	public void puzzleOptionChanged(PuzzleOption<?> src) {
		boolean copyOld = src == gapOption || src == variationOption;
		createPolys(copyOld);
		
		//notify anyone who's interested that something about this puzzle has changed
		fireStateChanged(null);
	}
	
	//This returns an array of arrays of indices, where each element is an index of a sticker (represented as a 2 element array)
	//So for the R face, an [2][][2] array faces would be returned where where faces[0] is a 4 element array of all the R face stickers,
	//and faces[1] is a 6 element array of half the F, U, B, D stickers
	//The return value is structured like this to facilitate cycling stickers as necessary
	public int[][][] getLayerIndices(CubeFace face, int layer) {
		int width = dimensions(face.getWidthAxis());
		int height = dimensions(face.getHeightAxis());
		int depth = dimensions(face.getRotationAxis());
		if(layer >= depth) //deal with hand positioning
			layer = depth - 1;
		else if(layer == 0)
			layer = 1;
		if(layer == -1)
			layer = depth - 1;
		
		//each cycle is of 4 stickers if width == height, 2 stickers otherwise
		int cycleLength = (width == height) ? 4 : 2;
		int faceCycles = (int)Math.ceil((double)width*height/cycleLength);
		int layerCycles = 2*layer*(width + height)/cycleLength;
		int[][][] stickers = new int[faceCycles + layerCycles][cycleLength][3];

		int nthCycle = 0;
		boolean square = width == height;
		if(!square) { //half turn only
			for(int h = 0; h < height; h++) {
				int maxW = (int) Math.ceil(width / 2.);
				if((width & 1) == 1 && h >= Math.ceil(height / 2.))
					maxW--;
				for(int w = 0; w < maxW; w++) {
					stickers[nthCycle][0] = new int[] { face.index(), h, w };
					stickers[nthCycle][1] = new int[] { face.index(), height - 1 - h, width - 1 - w };
					nthCycle++;
				}
			}
		} else { //quarter turn legal
			for(int h = 0; h < Math.ceil(height / 2.); h++) {
				for(int w = 0; w < width / 2; w++) {
					stickers[nthCycle][0] = new int[] { face.index(), h, w };
					stickers[nthCycle][1] = new int[] { face.index(), w, height - 1 - h };
					stickers[nthCycle][2] = new int[] { face.index(), width - 1 - h, height - 1 - w };
					stickers[nthCycle][3] = new int[] { face.index(), width - 1 - w, h };
					if(!face.isFirstAxisClockwise())
						Collections.reverse(Arrays.asList(stickers[nthCycle]));
					nthCycle++;
				}
			}
			//we include the center of odd cubes here, so it gets animated
			if((width & 1) == 1)
				stickers[nthCycle++] = new int[][] { {face.index(), width / 2, width / 2} };
		}
		
		if(face == CubeFace.RIGHT) {
			if(square) {
				for(int w = 0; w < width; w++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.FRONT.index(), w, l},
								{CubeFace.UP.index(), w, l},
								{CubeFace.BACK.index(), width - 1 - w, l},
								{CubeFace.DOWN.index(), width - 1 - w, l} };
			} else {
				for(int h = 0; h < height; h++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.FRONT.index(), h, l},
								{CubeFace.BACK.index(), height - 1 - h, l} };
				for(int w = 0; w < width; w++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.UP.index(), w, l},
								{CubeFace.DOWN.index(), width - 1 - w, l} };
			}
		} else if(face == CubeFace.LEFT) {
			if(square) {
				for(int l = 0; l < layer; l++)
					for(int w = 0; w < width; w++)
						stickers[nthCycle++] = new int[][] { {CubeFace.UP.index(), w, depth - 1 - l},
							{CubeFace.FRONT.index(), w, depth - 1 - l},
							{CubeFace.DOWN.index(), height - 1 - w, depth - 1 - l},
							{CubeFace.BACK.index(), height - 1 - w, depth - 1 - l} };
			} else {
				for(int h = 0; h < height; h++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.FRONT.index(), h, depth - 1 - l},
								{CubeFace.BACK.index(), height - 1 - h, depth - 1 - l} };
				for(int w = 0; w < width; w++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.UP.index(), w, depth - 1 - l},
								{CubeFace.DOWN.index(), width - 1 - w, depth - 1 - l} };
			}
		} else if(face == CubeFace.FRONT) {
			if(square) {
				for(int l = 0; l < layer; l++)
					for(int w = 0; w < width; w++)
						stickers[nthCycle++] = new int[][] { {CubeFace.UP.index(), l, w},
							{CubeFace.RIGHT.index(), w, l},
							{CubeFace.DOWN.index(), l, width - 1 - w},
							{CubeFace.LEFT.index(), width - 1 - w, l} };
			} else {
				for(int h = 0; h < height; h++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.LEFT.index(), h, l},
								{CubeFace.RIGHT.index(), height - 1 - h, l} };
				for(int w = 0; w < width; w++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.UP.index(), l, w},
								{CubeFace.DOWN.index(), l, width - 1 - w} };
			}
		} else if(face == CubeFace.BACK) {
			if(square) {
				for(int l = 0; l < layer; l++)
					for(int w = 0; w < width; w++)
						stickers[nthCycle++] = new int[][] { {CubeFace.UP.index(), depth - 1 - l, w},
							{CubeFace.LEFT.index(), width - 1 - w, depth - 1 - l},
							{CubeFace.DOWN.index(), depth - 1 - l, width - 1 - w},
							{CubeFace.RIGHT.index(), w, depth - 1 - l} };
			} else {
				for(int h = 0; h < height; h++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.LEFT.index(), h, depth - 1 - l},
								{CubeFace.RIGHT.index(), height - 1 - h, depth - 1 - l} };
				for(int w = 0; w < width; w++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.UP.index(), depth - 1 - l, w},
								{CubeFace.DOWN.index(), depth - 1 - l, width - 1 - w} };
			}
		} else if(face == CubeFace.UP) {
			if(square) {
				for(int l = 0; l < layer; l++)
					for(int w = 0; w < width; w++)
						stickers[nthCycle++] = new int[][] { {CubeFace.FRONT.index(), depth - 1 - l, w},
							{CubeFace.LEFT.index(), depth - 1 - l, w},
							{CubeFace.BACK.index(), depth - 1 - l, width - 1 - w},
							{CubeFace.RIGHT.index(), depth - 1 - l, width - 1 - w} };
			} else {
				for(int h = 0; h < height; h++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.LEFT.index(), depth - 1 - l, h},
								{CubeFace.RIGHT.index(), depth - 1 - l, height - 1 - h} };
				for(int w = 0; w < width; w++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.FRONT.index(), depth - 1 - l, w},
								{CubeFace.BACK.index(), depth - 1 - l, width - 1 - w} };
			}
		} else if(face == CubeFace.DOWN) {
			if(square) {
				for(int l = 0; l < layer; l++)
					for(int w = 0; w < width; w++)
						stickers[nthCycle++] = new int[][] { {CubeFace.FRONT.index(), l, w},
							{CubeFace.RIGHT.index(), l, width - 1 - w},
							{CubeFace.BACK.index(), l, width - 1 - w},
							{CubeFace.LEFT.index(), l, w} };
			} else {
				for(int h = 0; h < height; h++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.LEFT.index(), l, h},
								{CubeFace.RIGHT.index(), l, height - 1 - h} };
				for(int w = 0; w < width; w++)
					for(int l = 0; l < layer; l++)
						stickers[nthCycle++] = new int[][] { {CubeFace.FRONT.index(), l, w},
								{CubeFace.BACK.index(), l, width - 1 - w} };
			}
		}
		
		return stickers;
	}
	
	private void doTurn(CubeFace face, int layer, int cw) {
		layer = Math.min(layer, dimensions(face.getRotationAxis()) - 1);
		appendTurn(new CubeTurn(this, face, layer, cw));
	}
	
	private void doCubeRotation(CubeFace face, int cw) {
		appendTurn(new CubeTurn(this, face, -1, cw));
	}
	
	public PuzzleSticker[] getCorner(int n) {
		if(n < 0 || n >= 8 || cubeStickers == null || !piecePickerSupport())
			return null;
		
		ArrayList<PuzzleSticker[]> corners = new ArrayList<PuzzleSticker[]>();
		
		
		
		
		////////////////////////////////
		PuzzleSticker[] corner = new PuzzleSticker[3];
		corner[0] = cubeStickers[CubeFace.UP.index()][dimensions(1)-1][dimensions(1)-1];
		corner[1] = cubeStickers[CubeFace.LEFT.index()][dimensions(1)-1][dimensions(1)-1];
		corner[2] = cubeStickers[CubeFace.BACK.index()][dimensions(1)-1][dimensions(1)-1];
		corners.add(corner);
		
		corner = new PuzzleSticker[3];
		corner[0] = cubeStickers[CubeFace.UP.index()][0][dimensions(1)-1];
		corner[1] = cubeStickers[CubeFace.FRONT.index()][dimensions(1)-1][dimensions(1)-1];
		corner[2] = cubeStickers[CubeFace.LEFT.index()][dimensions(1)-1][0];
		corners.add(corner);
				
		corner = new PuzzleSticker[3];
		corner[0] = cubeStickers[CubeFace.UP.index()][0][0];
		corner[1] = cubeStickers[CubeFace.RIGHT.index()][dimensions(1)-1][0];
		corner[2] = cubeStickers[CubeFace.FRONT.index()][dimensions(1)-1][0];
		corners.add(corner);
				
		corner = new PuzzleSticker[3];
		corner[0] = cubeStickers[CubeFace.UP.index()][dimensions(1)-1][0];
		corner[1] = cubeStickers[CubeFace.BACK.index()][dimensions(1)-1][0];
		corner[2] = cubeStickers[CubeFace.RIGHT.index()][dimensions(1)-1][dimensions(1)-1];
		corners.add(corner);
		
		corner = new PuzzleSticker[3];
		corner[0] = cubeStickers[CubeFace.DOWN.index()][dimensions(1)-1][dimensions(1)-1];
		corner[1] = cubeStickers[CubeFace.BACK.index()][0][dimensions(1)-1];
		corner[2] = cubeStickers[CubeFace.LEFT.index()][0][dimensions(1)-1];
		corners.add(corner);
		
		corner = new PuzzleSticker[3];
		corner[0] = cubeStickers[CubeFace.DOWN.index()][0][dimensions(1)-1];
		corner[1] = cubeStickers[CubeFace.LEFT.index()][0][0];
		corner[2] = cubeStickers[CubeFace.FRONT.index()][0][dimensions(1)-1];
		corners.add(corner);
		
		corner = new PuzzleSticker[3];
		corner[0] = cubeStickers[CubeFace.DOWN.index()][0][0];
		corner[1] = cubeStickers[CubeFace.FRONT.index()][0][0];
		corner[2] = cubeStickers[CubeFace.RIGHT.index()][0][0];
		corners.add(corner);
		
		corner = new PuzzleSticker[3];
		corner[0] = cubeStickers[CubeFace.DOWN.index()][dimensions(1)-1][0];
		corner[1] = cubeStickers[CubeFace.RIGHT.index()][0][dimensions(1)-1];
		corner[2] = cubeStickers[CubeFace.BACK.index()][0][0];
		corners.add(corner);
		
		
		
		return corners.get(n);
	}
	
	public PuzzleSticker[][][] cubeStickers;
	protected void _createPolys(boolean copyOld) {
		//TODO - doesn't work quite right with adjusting gap during animation
		if(!copyOld)
			resetHandPositions();
		PuzzleSticker[][][] cubeStickersOld = cubeStickers;
		cubeStickers = new PuzzleSticker[6][][];
		double[] point = new double[3];
		double scale = 2. / Utils.max(dimensions(0), dimensions(1), dimensions(2));

		for(CubeFace f1 : CubeFace.faces) {
			if(f1.isCWWithAxis()) continue;
			CubeFace f2 = f1.getOppositeFace();
			int height = dimensions(f1.getHeightAxis());
			int width = dimensions(f1.getWidthAxis());
			int depth = dimensions(f1.getRotationAxis());
			double halfHeight = height / 2.;
			double halfWidth = width / 2.;
			double halfDepth = depth / 2.;
			cubeStickers[f1.index()] = new PuzzleSticker[height][width];
			cubeStickers[f2.index()] = new PuzzleSticker[height][width];
			for(int h = 0; h < height; h++) {
				for(int w = 0; w < width; w++) {
					PuzzleSticker sticker = new PuzzleSticker();
					double stickerGap = gapOption.getValue();
					List<Double> spaces1 = Arrays.asList(stickerGap, 1 - stickerGap);
					List<Double> spaces2 = new ArrayList<Double>(spaces1);
					for(double hh : spaces1) {
						for(double ww : spaces2) {
							point[f1.getHeightAxis()] = h + hh;
							point[f1.getWidthAxis()] = w + ww;
							point[f1.getRotationAxis()] = 0;
							sticker.addPoint(point);
						}
						Collections.reverse(spaces2); //want to form a box, not an x
					}

					double[] translate = new double[3];
					translate[f1.getWidthAxis()] = -halfWidth;
					translate[f1.getHeightAxis()] = -halfHeight;
					translate[f1.getRotationAxis()] = -halfDepth;
					sticker.translate(translate).scale(scale, scale, scale);
					if(copyOld) {
						sticker.setFace(cubeStickersOld[f1.index()][h][w].getFace());
					} else
						sticker.setFace(f1.toString());
					cubeStickers[f1.index()][h][w] = sticker;
					addPoly(sticker);

					translate = new double[3];
					translate[f1.getRotationAxis()] = scale*depth;
					sticker = (PuzzleSticker) sticker.clone().translate(translate);
					if(copyOld)
						sticker.setFace(cubeStickersOld[f2.index()][h][w].getFace());
					else
						sticker.setFace(f2.toString());
					cubeStickers[f2.index()][h][w] = sticker;
					addPoly(sticker);
				}
			}
		}

		String variation = variationOption.getValue();
		for(int f = 0; f < cubeStickers.length; f++) {
			int width = cubeStickers[f].length;
			int height = cubeStickers[f][0].length;
			for(int w = 0; w < width; w++)
				for(int h = 0; h < height; h++)
					cubeStickers[f][w][h].setVisible(true);
			if(variation.equals(VOID))
				for(int w = 1; w < width - 1; w++)
					for(int h = 1; h < height - 1; h++)
						cubeStickers[f][w][h].setVisible(false);
			else if(variation.equals(BABYFACE))
				for(int w = 0; w < width; w++)
					for(int h = 0; h < height; h++)
						if(w == 0 || w == width - 1 || h == 0 || h == height - 1)
							cubeStickers[f][w][h].setVisible(false);
		}
	}
	
	private static class InvertableHashMap<K, V> {
		private HashMap<K, V> kTov = new HashMap<K, V>();
		private HashMap<V, K> vTok = new HashMap<V, K>();
		public InvertableHashMap() {}
		public void put(K key, V value) {
			kTov.put(key, value);
			vTok.put(value, key);
		}
		public V getValue(K key) {
			return kTov.get(key);
		}
		public K getKey(V value) {
			return vTok.get(value);
		}
	}
	//look @ BLD
	//look for guy w/ BL stickers and deduce U color
	//look for LD stickers and deduce F color
	//look for BD stickers and deduce R color
	public String getState() {
		if(dimensions(0) != 2 || dimensions(1) != 2 || dimensions(2) != 2)
			return "Not a 2x2x2!";
		if(cubeStickers == null) //initing
			return "Invalid";
		for(PuzzleSticker[][] face : cubeStickers)
			for(PuzzleSticker[] row : face)
				for(PuzzleSticker stick : row)
					if(stick.getFace() == null)
						return "Invalid";
		
		int[] values = new int[CubeFace.faces.size()];
		values[CubeFace.FRONT.index()] = 0;
		values[CubeFace.RIGHT.index()] = 0;
		values[CubeFace.UP.index()] = 0;
		values[CubeFace.LEFT.index()] = 1;
		values[CubeFace.BACK.index()] = 2;
		values[CubeFace.DOWN.index()] = 4;
		
		InvertableHashMap<CubeFace, Color> colors = new InvertableHashMap<CubeFace, Color>();
		colors.put(CubeFace.BACK, cubeStickers[CubeFace.BACK.index()][0][1].getFillColor());
		colors.put(CubeFace.LEFT, cubeStickers[CubeFace.LEFT.index()][0][1].getFillColor());
		colors.put(CubeFace.DOWN, cubeStickers[CubeFace.DOWN.index()][1][1].getFillColor());
		for(int p=0; p<ColorSpitter.pieces.length-1; p++) {
			Color c;
			if((c = findThirdColor(p, colors.getValue(CubeFace.BACK), colors.getValue(CubeFace.LEFT))) != null) {
				colors.put(CubeFace.UP, c);
			} else if((c = findThirdColor(p, colors.getValue(CubeFace.LEFT), colors.getValue(CubeFace.DOWN))) != null) {
				colors.put(CubeFace.FRONT, c);
			} else if((c = findThirdColor(p, colors.getValue(CubeFace.BACK), colors.getValue(CubeFace.DOWN))) != null) {
				colors.put(CubeFace.RIGHT, c);
			}
		}
		int[] pieces = new int[ColorSpitter.pieces.length];
		int[] orientations = new int[ColorSpitter.pieces.length];
		for(int p=0; p<ColorSpitter.pieces.length; p++) {
			int piece = 0;
			CubeFace[] faces = ColorSpitter.solved_cube_faces[p];
			int[][] indices = ColorSpitter.pieces[p];
			int orientation = -1;
			for(int i=0; i<3; i++) {
				PuzzleSticker[][] face = cubeStickers[faces[i].index()];
				Color c = face[indices[i][0]][indices[i][1]].getFillColor();
				if(c.equals(colors.getValue(CubeFace.UP)) || c.equals(colors.getValue(CubeFace.DOWN)))
					orientation = (3 - i) % 3;
				piece += values[colors.getKey(c).index()];
			}
			pieces[p] = piece;
			orientations[p] = orientation;
		}
		
		CubeFace[][] decodedFaces = null;
		try {
			decodedFaces = ColorSpitter.spit_out_colors(pieces, orientations);
		} catch(Exception e) {
			return "Invalid"; //there may be a better way of doing this...
		}
		for(int p=0; p<ColorSpitter.pieces.length; p++) {
			CubeFace[] faces = ColorSpitter.solved_cube_faces[p];
			int[][] indices = ColorSpitter.pieces[p];
			for(int i=0; i<3; i++) {
				PuzzleSticker[][] face = cubeStickers[faces[i].index()];
				if(!colors.getKey(face[indices[i][0]][indices[i][1]].getFillColor()).equals(decodedFaces[p][i])) {
					return "Invalid";
				}
			}
		}
		
		int sum=0;
		for (int i:orientations)
			sum+=i;
		if (sum%3 != 0)
			return "Invalid";
		HashSet<Integer> notSeen = new HashSet<Integer>();
		for(int i = 0; i < 8; i++)
			notSeen.add(i);
		for (int i:pieces)
			notSeen.remove(i);
		if(!notSeen.isEmpty())
			return "Invalid";
		return Utils.join(",", pieces) + ";" + Utils.join(",", orientations);
	}
	
	private Color findThirdColor(int piece, Color color1, Color color2) {
		boolean c1=false, c2=false;
		Color thirdColor = null;
		for(int i=0; i<3; i++) {
			int[] indices = ColorSpitter.pieces[piece][i];
			Color c = cubeStickers[ColorSpitter.solved_cube_faces[piece][i].index()][indices[0]][indices[1]].getFillColor();
			boolean cc1 = c.equals(color1);
			boolean cc2 = c.equals(color2);
			if(!cc1 && !cc2)
				thirdColor = c;
			c1 = c1 || cc1;
			c2 = c2 || cc2;
		}
		return (c1 && c2) ? thirdColor : null;
	}
	
	public boolean isSolved() {
		if(cubeStickers == null) return false;
		for(Polygon3D[][] face : cubeStickers) {
			Color c = null;
			for(int i = 0; i < face.length; i++)
				for(int j = 0; j < face[i].length; j++) {
					if(c == null && face[i][j].isVisible())
						c = face[i][j].getFillColor();
					if(face[i][j].getFillColor() == null || (face[i][j].isVisible() && !face[i][j].getFillColor().equals(c)))
						return false;
				}
		}
		return true;
	}

	private int[] handPositions = new int[CubeFace.faces().length];
	private void resetHandPositions() {
		Arrays.fill(handPositions, 1);
	}

	private static final HashMap<String, Integer> TURN_DIRECTION = new HashMap<String, Integer>();
	public static final HashMap<Integer, String> DIRECTION_TURN = new HashMap<Integer, String>();
	{
		TURN_DIRECTION.put("", 1);
		TURN_DIRECTION.put("'", -1);
		TURN_DIRECTION.put("2", 2);
		TURN_DIRECTION.put("2'", -2);
		DIRECTION_TURN.put(1, "");
		DIRECTION_TURN.put(-1, "'");
		DIRECTION_TURN.put(-2, "2'");
		DIRECTION_TURN.put(2, "2");
	}
	public boolean _doTurn(String turn, boolean invert) {
		char ch = turn.charAt(0);
		CubeFace face = CubeFace.decodeFace(ch);
		Integer direction = TURN_DIRECTION.get(turn.substring(1));
		if(direction == null) { //hand shift
			int leftRightWidth = dimensions(CubeFace.RIGHT.getRotationAxis());
			direction = 0;
			if("--".equals(turn.substring(1)))
				direction = -1;
			else if("++".equals(turn.substring(1)))
				direction = 1;
			else
				return false;

			if(invert) direction = -direction;
			
			handPositions[face.index()] += direction;
			handPositions[face.index()] = Math.max(1, handPositions[face.index()]);
			handPositions[face.index()] = Math.min(leftRightWidth - 1, handPositions[face.index()]);
			return true;
		} else {
			if(invert) direction = -direction;
			if(face != null) { //n-layer face turn
				int layer = handPositions[face.index()] + ((Character.isUpperCase(ch)) ? 0 : 1);
				
				//TODO - hacked for gamescrafters
				if(Math.abs(direction) == 2) {
					direction = (int) Math.signum(direction);
					doTurn(face, layer, direction);
				}
				
				doTurn(face, layer, direction);
				return true;
			} else { //cube rotation
				CubeFace cf = CubeFace.decodeCubeRotation(ch);
				if(cf == null)
					return false;
				doCubeRotation(cf, direction);
				return true;
			}
		}
	}
	@Override
	protected void _cantScramble() {
		resetHandPositions();
	}
	public void _scramble() {
		CubeFace[] faces = CubeFace.faces();
		Random r = new Random();
		if(dimensions(0) == 2 && dimensions(1) == 2 && dimensions(2) == 2) {
			String scramble = new CubeScramble("2x2x2", 42, "").toString();
			for(String turn : scramble.split(" ")) {
				_doTurn(turn, false);
			}
		} else {
			for(int ch = 0; ch < 3*(dimensions(0)*dimensions(1)*dimensions(2)); ch++) {
				CubeFace f = faces[r.nextInt(faces.length)];
				doTurn(f, r.nextInt(Math.max(1, dimensions(f.getRotationAxis())-1))+1, (r.nextInt(2)+1));
			}
		}
	}

	public HashMap<String, Color> getDefaultColorScheme() {
		HashMap<String, Color> colors = new HashMap<String, Color>();
		for(CubeFace f : CubeFace.faces) {
			colors.put(f.toString(), f.getColor());
		}
		return colors;
	}
	
	public boolean piecePickerSupport(){
		if(dimensions(0) != 2 || dimensions(1) != 2 || dimensions(2) != 2)
			return false;
		return true;
	}
}
