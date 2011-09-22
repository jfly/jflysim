package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;

public class Faces {
	PolygonCollection holder;
	PolygonCollection holder_sf;
	PolygonCollection holder_sfb;
	PolygonCollection holder_sfr;
	PolygonCollection holder_sfw;

	public Faces(CubeGen cube) {
		PolygonCollection b_face = new BlueFace(cube).returnItem();
		PolygonCollection w_face = new WhiteFace(cube).returnItem();
		PolygonCollection r_face = new RedFace(cube).returnItem();
		PolygonCollection b_face2 = new BlueFace(cube).returnItem();
		PolygonCollection r_face2 = new RedFace(cube).returnItem();
		PolygonCollection w_face2 = new WhiteFace(cube).returnItem();
		r_face2.translate(0,0,11);
		w_face2.translate(11, 0, 0);
		b_face2.translate(0, -11, 0);
		
		// r_face.extract_polygons()[0].setFillColor(Color.RED);
		/*
		 * PolygonCollection r_face2 = new RedFace().returnItem();
		 * r_face2.extract_polygons()[0].setFillColor(Color.MAGENTA);
		 * PolygonCollection w_face2 = new WhiteFace().returnItem();
		 * w_face2.extract_polygons()[0].setFillColor(Color.GRAY);
		 * PolygonCollection b_face2 = new BlueFace().returnItem();
		 * b_face2.extract_polygons()[0].setFillColor(Color.CYAN);
		 */
		// The original faces were generated in the same plane.  Now defunct just for example.
		// w_face.translate(0, 0, -11);
		// r_face.rotate('x', 90);
		// r_face.translate(0, 0, -11);
		// b_face.rotate('y', 270);
		// b_face.translate(0, 0, -11);
		// w_face.rotate('z', 180);
		// b_face.translate(-1, 0, 10);
		/*
		 * r_face2.rotate('x', 90); r_face2.translate(0, -10, 10);
		 * b_face2.rotate('y', 90); b_face2.translate(10, 0, 10);
		 */
		//Top down view has blue 2 removed, normal view has all 2 faces removed.
		Object[] input_array = { b_face, b_face2, r_face, r_face2,
				w_face, w_face2};
		//For the removal of the 2 faces for normal view
		Object[] input_array_second_faces = {r_face2, w_face2, b_face2};
		Object[] input_array_second_face_blue = {b_face2};
		Object[] input_array_second_face_red = {r_face2};
		Object[] input_array_second_face_white = {w_face2};
		double adjust = cube.boardsize + .5;
		holder = new PolygonCollection(input_array);
		holder.translate(-adjust, adjust, -adjust);
		holder_sf = new PolygonCollection(input_array_second_faces);
		holder_sfb = new PolygonCollection(input_array_second_face_blue);
		holder_sfw = new PolygonCollection(input_array_second_face_white);
		holder_sfr = new PolygonCollection(input_array_second_face_red);
	}

	public Polygon3D[] extract() {
		return holder.extract_polygons();
	}
	public Polygon3D[] extract_sf() {
		return holder_sf.extract_polygons();
	}
	public Polygon3D[] extract_sfb() {
		return holder_sfb.extract_polygons();
	}
	public Polygon3D[] extract_sfr() {
		return holder_sfr.extract_polygons();
	}
	public Polygon3D[] extract_sfw() {
		return holder_sfw.extract_polygons();
	}
	
}