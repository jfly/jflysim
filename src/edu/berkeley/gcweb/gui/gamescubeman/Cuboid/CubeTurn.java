package edu.berkeley.gcweb.gui.gamescubeman.Cuboid;

import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.PuzzleTurn;
import edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils.Utils;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;
import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.RotationMatrix;

public class CubeTurn extends PuzzleTurn {
	private CubeFace face;
	private int clockwise; //simply for easy inverting purposes
	private int layer, cw, legalTurns;
	private Cuboid cube;
	public CubeTurn(Cuboid cube, CubeFace f, int layer, int clockwise) {
		super(cube.getFramesPerAnimation());
		this.cube = cube;
		this.face = f;
		this.layer = layer;
		this.clockwise = clockwise;
		cw = clockwise;
		if(cw != -2) { //we want to distinguish between R2 and R2'
			cw = Utils.modulo(cw, 4);
			if(cw == 3) cw = -1;
		}
		if(cube.dimensions(face.getWidthAxis()) != cube.dimensions(face.getHeightAxis())) {
			if(cw % 2 != 0) {
				cw = (cw > 0) ? cw + 1 : cw - 1;
			}
			legalTurns = cw / 2;
		} else
			legalTurns = cw;
	}
	public CubeFace getFace() {
		return face;
	}
	public String toString() {
		String f = "" + face.getFaceName();
		if(layer == -1) {
			f = "" + "xyz".charAt(face.getRotationAxis());
		} else if(layer == 2)
			f = f.toLowerCase();
		else if(layer > 2)
			f = layer + " " + face;
		return f + Cuboid.DIRECTION_TURN.get(cw);
	}
	public boolean isInspectionLegal() {
		return layer == -1; //this is a cube rotation
	}
	public boolean isNullTurn() {
		return cw == 0;
	}
	public PuzzleTurn mergeTurn(PuzzleTurn o) {
		CubeTurn other = (CubeTurn) o;
		if(other.face != this.face || other.layer != this.layer)
			return null;
		return new CubeTurn(cube, this.face, this.layer, this.cw + other.cw);
	}
	
	public boolean isAnimationMergeble(PuzzleTurn o) {
		CubeTurn other = (CubeTurn) o;
		return (other.layer != -1 || this.layer != -1) && //we don't want to animate cube rotations simultaneously
				(face.getRotationAxis() == other.face.getRotationAxis()) && //don't want different axes of rotation
				((other.layer == -1 || this.layer == -1) || face.index() != other.face.index() || layer != other.layer); //don't want something like R + R' 
	}
	
	private int[][][] stickers;
	private RotationMatrix main_rotation;
	public void _animateMove(boolean firstFrame) {
		if(firstFrame) { //just starting animation
			int[][][] stickers1 = cube.getLayerIndices(face, layer);
			int[][][] stickers2 = new int[0][][];
			if(layer == -1) {
				stickers2 = cube.getLayerIndices(face.getOppositeFace(), 1);
				//reverse the cycles because cw on that face is the other way around
				for(int i = 0; i < stickers2.length; i++)
					Utils.reverse(stickers2[i]);
			}
			stickers = new int[stickers1.length + stickers2.length][][];
			for(int i=0; i<stickers.length; i++)
				stickers[i] = i < stickers1.length ? stickers1[i] : stickers2[i - stickers1.length];
			
			//multiply by -1 because the rotation matrix expects degrees ccw
			main_rotation = new RotationMatrix(face.getRotationAxis(), -1*(face.isCWWithAxis() ? 1 : -1)*cw*90./frames);
		}
		for(int i=0; i<stickers.length; i++)
			for(int[] index : stickers[i])
				cube.cubeStickers[index[0]][index[1]][index[2]].rotate(main_rotation);
	}

	//offset indicates that new_polys[(i + offset) % polys.length] = old_polys[i] for all 0 <= i < polys.length
	private static void cycle(Polygon3D[][][] polys, int[][] indices, int offset) {
		Polygon3D[][][] old_polys = new Polygon3D[polys.length][][];
		for(int i = 0; i < old_polys.length; i++) { //making a deep copy of the original array
			old_polys[i] = new Polygon3D[polys[i].length][];
			for(int j = 0; j < old_polys[i].length; j++) {
				old_polys[i][j] = new Polygon3D[polys[i][j].length];
				for(int k = 0; k < old_polys[i][j].length; k++)
					old_polys[i][j][k] = polys[i][j][k];
			}
		}
		
		for(int c = 0; c < indices.length; c++) {
			int[] from = indices[c], to = indices[Utils.modulo(c+offset, indices.length)];
			polys[to[0]][to[1]][to[2]] = old_polys[from[0]][from[1]][from[2]];
		}
	}
	public void updateInternalRepresentation(boolean polygons) {
		//TODO - possibly implement 90 degree turns for cuboids?
		if(!polygons) return;
		//updating internal representation
		for(int[][] cycleIndices : stickers)
			cycle(cube.cubeStickers, cycleIndices, legalTurns);
	}
	@Override
	public PuzzleTurn invert() {
		return new CubeTurn(cube, face, layer, -clockwise);
	}
}