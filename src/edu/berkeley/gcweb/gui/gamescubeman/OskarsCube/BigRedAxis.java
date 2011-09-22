package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import java.awt.Color;

import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;

public class BigRedAxis {
	public PolygonCollection holder;
	public PolygonCollection holder_stick;
	public PolygonCollection xyholder;
	public PolygonCollection yzholder;
	public PolygonCollection xzholder;
	public PolygonCollection towardW;
	public PolygonCollection towardB;
	public PolygonCollection towardR;
	public PolygonCollection awayW;
	public PolygonCollection awayB;
	public PolygonCollection awayR;
	public PolygonCollection RtowardW;
	public PolygonCollection RtowardB;
	public PolygonCollection RtowardR;
	public PolygonCollection RawayW;
	public PolygonCollection RawayB;
	public PolygonCollection RawayR;
	public PolygonCollection secondFacesBlue;
	public PolygonCollection secondFacesRed;
	public PolygonCollection secondFacesWhite;
	public PolygonCollection secondFaces;

	/*
	public BigRedAxis(CubeGen cube){ 
		PolygonCollection stick_1 = new Stick().returnItem(); 
		PolygonCollection stick_2 = new Stick().returnItem(); 
		PolygonCollection stick_3 = new Stick().returnItem(); 
		stick_1.rotate('x', 90); 
		stick_1.translate(10, 10, -1); 
		stick_2.rotate('y', 90); 
		stick_3.translate(10, 0, -11); 
		//rotate the sticks here 
		Object[] input_array = {stick_1, stick_2, stick_3}; 
		holder = new PolygonCollection(input_array); 
		holder.translate(-14.5, 3.5, -3.5);
		holder.translate(cube.start[0], -cube.start[1], cube.start[2]);
		}
	*/
	
	public BigRedAxis(CubeGen cube) {						
		int x = cube.start[0] + 1;
		int y = -cube.start[1] - 1;
		int z = cube.start[2] + 1;
		double s = cube.boardsize +.5;
		//These make the arrows, the side with green_b refers to the side with the arrow base 
		double[] green_a = new double[] {0,0,.5,.5,1,.5,.5};
		double[] green_b = new double[] {.25,.75,.75,.9,.5,.1,.25};
		/*
		PolygonCollection stick_1 = new Stick().returnItem(); 
		PolygonCollection stick_2 = new Stick().returnItem(); 
		PolygonCollection stick_3 = new Stick().returnItem(); 
		stick_1.rotate('x', 90); 
		stick_1.translate(10, 11, -1); 
		stick_2.rotate('y', 90); 
		stick_3.translate(10, 0, -11); 
		//rotate the sticks here 
		Object[] input_array2 = {stick_1, stick_2, stick_3}; 
		holder_stick = new PolygonCollection(input_array2); 
		holder_stick.translate(-14.5, 3.5, -3.5);
		holder_stick.translate(cube.start[0], -cube.start[1], cube.start[2]);
		*/
		Polygon3D startdot_w = new Polygon3D(); // THIS IS WHITE
		startdot_w.setFillColor(Color.orange);
		startdot_w.addPoint(-s, y + s, z - s);
		startdot_w.addPoint(-s, y + s - 1, z - s);
		startdot_w.addPoint(-s, y + s - 1, z - s + 1);
		startdot_w.addPoint(-s, y + s, z - s + 1);
		
		Polygon3D startdot_w2 = new Polygon3D(); // THIS IS WHITE
		startdot_w2.setFillColor(Color.orange);
		startdot_w2.addPoint(-s +11, y + s, z - s);
		startdot_w2.addPoint(-s +11, y + s - 1, z - s);
		startdot_w2.addPoint(-s +11, y + s - 1, z - s + 1);
		startdot_w2.addPoint(-s +11, y + s, z - s + 1);
		
		Polygon3D startdot_b = new Polygon3D(); // THIS IS BLUE
		startdot_b.setFillColor(Color.orange);
		startdot_b.addPoint(x - s, s, z - s);
		startdot_b.addPoint(x - s + 1, s, z - s);
		startdot_b.addPoint(x - s + 1, s, z - s + 1);
		startdot_b.addPoint(x - s, s, z - s + 1);
		
		Polygon3D startdot_b2 = new Polygon3D(); // THIS IS BLUE
		startdot_b2.setFillColor(Color.orange);
		startdot_b2.addPoint(x - s, s-11, z - s);
		startdot_b2.addPoint(x - s + 1, s-11, z - s);
		startdot_b2.addPoint(x - s + 1, s-11, z - s + 1);
		startdot_b2.addPoint(x - s, s-11, z - s + 1);
		
		Polygon3D startdot_r = new Polygon3D();
		startdot_r.setFillColor(Color.orange); // THIS IS RED
		startdot_r.addPoint(x - s, y + s, -s);
		startdot_r.addPoint(x - s + 1, y + s, -s);
		startdot_r.addPoint(x - s + 1, y + s - 1, -s);
		startdot_r.addPoint(x - s, y + s - 1, -s);
		
		Polygon3D startdot_r2 = new Polygon3D();
		startdot_r2.setFillColor(Color.orange); // THIS IS RED
		startdot_r2.addPoint(x - s, y + s, -s + 11);
		startdot_r2.addPoint(x - s + 1, y + s, -s +11);
		startdot_r2.addPoint(x - s + 1, y + s - 1, -s +11);
		startdot_r2.addPoint(x - s, y + s - 1, -s +11);
		
		Polygon3D arrow_w_tb = new Polygon3D();
		arrow_w_tb.setFillColor(Color.orange);
		arrow_w_tb.addPoint(-s, y+s + green_a[0], z-s + green_b[0]);
		arrow_w_tb.addPoint(-s, y+s + green_a[1], z-s + green_b[1]);
		arrow_w_tb.addPoint(-s, y+s + green_a[2], z-s + green_b[2]);
		arrow_w_tb.addPoint(-s, y+s + green_a[3], z-s + green_b[3]);
		arrow_w_tb.addPoint(-s, y+s + green_a[4], z-s + green_b[4]);
		arrow_w_tb.addPoint(-s, y+s + green_a[5], z-s + green_b[5]);
		arrow_w_tb.addPoint(-s, y+s + green_a[6], z-s + green_b[6]);
		
		Polygon3D arrow_w_ab = new Polygon3D();
		arrow_w_ab.setFillColor(Color.orange);
		arrow_w_ab.addPoint(-s, y+s-1 - green_a[0], z-s + green_b[0]);
		arrow_w_ab.addPoint(-s, y+s-1 - green_a[1], z-s + green_b[1]);
		arrow_w_ab.addPoint(-s, y+s-1 - green_a[2], z-s + green_b[2]);
		arrow_w_ab.addPoint(-s, y+s-1 - green_a[3], z-s + green_b[3]);
		arrow_w_ab.addPoint(-s, y+s-1 - green_a[4], z-s + green_b[4]);
		arrow_w_ab.addPoint(-s, y+s-1 - green_a[5], z-s + green_b[5]);
		arrow_w_ab.addPoint(-s, y+s-1 - green_a[6], z-s + green_b[6]);
		
		Polygon3D arrow_w_ar = new Polygon3D();
		arrow_w_ar.setFillColor(Color.orange);
		arrow_w_ar.addPoint(-s, y+s-1 + green_b[0], z-s+1 + green_a[0]);
		arrow_w_ar.addPoint(-s, y+s-1 + green_b[1], z-s+1 + green_a[1]);
		arrow_w_ar.addPoint(-s, y+s-1 + green_b[2], z-s+1 + green_a[2]);
		arrow_w_ar.addPoint(-s, y+s-1 + green_b[3], z-s+1 + green_a[3]);
		arrow_w_ar.addPoint(-s, y+s-1 + green_b[4], z-s+1 + green_a[4]);
		arrow_w_ar.addPoint(-s, y+s-1 + green_b[5], z-s+1 + green_a[5]);
		arrow_w_ar.addPoint(-s, y+s-1 + green_b[6], z-s+1 + green_a[6]);
		
		Polygon3D arrow_w_tr = new Polygon3D();
		arrow_w_tr.setFillColor(Color.orange);
		arrow_w_tr.addPoint(-s, y+s-1 + green_b[0], z-s - green_a[0]);
		arrow_w_tr.addPoint(-s, y+s-1 + green_b[1], z-s - green_a[1]);
		arrow_w_tr.addPoint(-s, y+s-1 + green_b[2], z-s - green_a[2]);
		arrow_w_tr.addPoint(-s, y+s-1 + green_b[3], z-s - green_a[3]);
		arrow_w_tr.addPoint(-s, y+s-1 + green_b[4], z-s - green_a[4]);
		arrow_w_tr.addPoint(-s, y+s-1 + green_b[5], z-s - green_a[5]);
		arrow_w_tr.addPoint(-s, y+s-1 + green_b[6], z-s - green_a[6]);
		
		Polygon3D arrow_w2_tr = new Polygon3D();
		arrow_w2_tr.setFillColor(Color.orange);
		arrow_w2_tr.addPoint(-s+11, y+s-1 + green_b[0], z-s - green_a[0]);
		arrow_w2_tr.addPoint(-s+11, y+s-1 + green_b[1], z-s - green_a[1]);
		arrow_w2_tr.addPoint(-s+11, y+s-1 + green_b[2], z-s - green_a[2]);
		arrow_w2_tr.addPoint(-s+11, y+s-1 + green_b[3], z-s - green_a[3]);
		arrow_w2_tr.addPoint(-s+11, y+s-1 + green_b[4], z-s - green_a[4]);
		arrow_w2_tr.addPoint(-s+11, y+s-1 + green_b[5], z-s - green_a[5]);
		arrow_w2_tr.addPoint(-s+11, y+s-1 + green_b[6], z-s - green_a[6]);
		
		Polygon3D arrow_w2_tb = new Polygon3D();
		arrow_w2_tb.setFillColor(Color.orange);
		arrow_w2_tb.addPoint(-s+11, y+s + green_a[0], z-s + green_b[0]);
		arrow_w2_tb.addPoint(-s+11, y+s + green_a[1], z-s + green_b[1]);
		arrow_w2_tb.addPoint(-s+11, y+s + green_a[2], z-s + green_b[2]);
		arrow_w2_tb.addPoint(-s+11, y+s + green_a[3], z-s + green_b[3]);
		arrow_w2_tb.addPoint(-s+11, y+s + green_a[4], z-s + green_b[4]);
		arrow_w2_tb.addPoint(-s+11, y+s + green_a[5], z-s + green_b[5]);
		arrow_w2_tb.addPoint(-s+11, y+s + green_a[6], z-s + green_b[6]);
		
		Polygon3D arrow_w2_ab = new Polygon3D();
		arrow_w2_ab.setFillColor(Color.orange);
		arrow_w2_ab.addPoint(-s+11, y+s-1 - green_a[0], z-s + green_b[0]);
		arrow_w2_ab.addPoint(-s+11, y+s-1 - green_a[1], z-s + green_b[1]);
		arrow_w2_ab.addPoint(-s+11, y+s-1 - green_a[2], z-s + green_b[2]);
		arrow_w2_ab.addPoint(-s+11, y+s-1 - green_a[3], z-s + green_b[3]);
		arrow_w2_ab.addPoint(-s+11, y+s-1 - green_a[4], z-s + green_b[4]);
		arrow_w2_ab.addPoint(-s+11, y+s-1 - green_a[5], z-s + green_b[5]);
		arrow_w2_ab.addPoint(-s+11, y+s-1 - green_a[6], z-s + green_b[6]);
		
		Polygon3D arrow_w2_ar = new Polygon3D();
		arrow_w2_ar.setFillColor(Color.orange);
		arrow_w2_ar.addPoint(-s+11, y+s-1 + green_b[0], z-s+1 + green_a[0]);
		arrow_w2_ar.addPoint(-s+11, y+s-1 + green_b[1], z-s+1 + green_a[1]);
		arrow_w2_ar.addPoint(-s+11, y+s-1 + green_b[2], z-s+1 + green_a[2]);
		arrow_w2_ar.addPoint(-s+11, y+s-1 + green_b[3], z-s+1 + green_a[3]);
		arrow_w2_ar.addPoint(-s+11, y+s-1 + green_b[4], z-s+1 + green_a[4]);
		arrow_w2_ar.addPoint(-s+11, y+s-1 + green_b[5], z-s+1 + green_a[5]);
		arrow_w2_ar.addPoint(-s+11, y+s-1 + green_b[6], z-s+1 + green_a[6]);
		
		
		
		Polygon3D arrow_b_aw = new Polygon3D();
		arrow_b_aw.setFillColor(Color.orange);
		arrow_b_aw.addPoint(x-s+1 + green_a[0],s, z-s + green_b[0]);
		arrow_b_aw.addPoint(x-s+1 + green_a[1],s, z-s + green_b[1]);
		arrow_b_aw.addPoint(x-s+1 + green_a[2],s, z-s + green_b[2]);
		arrow_b_aw.addPoint(x-s+1 + green_a[3],s, z-s + green_b[3]);
		arrow_b_aw.addPoint(x-s+1 + green_a[4],s, z-s + green_b[4]);
		arrow_b_aw.addPoint(x-s+1 + green_a[5],s, z-s + green_b[5]);
		arrow_b_aw.addPoint(x-s+1 + green_a[6],s, z-s + green_b[6]);
		
		Polygon3D arrow_b_tw = new Polygon3D();
		arrow_b_tw.setFillColor(Color.orange);
		arrow_b_tw.addPoint(x-s - green_a[0],s, z-s + green_b[0]);
		arrow_b_tw.addPoint(x-s - green_a[1],s, z-s + green_b[1]);
		arrow_b_tw.addPoint(x-s - green_a[2],s, z-s + green_b[2]);
		arrow_b_tw.addPoint(x-s - green_a[3],s, z-s + green_b[3]);
		arrow_b_tw.addPoint(x-s - green_a[4],s, z-s + green_b[4]);
		arrow_b_tw.addPoint(x-s - green_a[5],s, z-s + green_b[5]);
		arrow_b_tw.addPoint(x-s - green_a[6],s, z-s + green_b[6]);
		
		Polygon3D arrow_b_ar = new Polygon3D();
		arrow_b_ar.setFillColor(Color.orange);
		arrow_b_ar.addPoint(x-s + green_b[0],s, z-s+1 + green_a[0]);
		arrow_b_ar.addPoint(x-s + green_b[1],s, z-s+1 + green_a[1]);
		arrow_b_ar.addPoint(x-s + green_b[2],s, z-s+1 + green_a[2]);
		arrow_b_ar.addPoint(x-s + green_b[3],s, z-s+1 + green_a[3]);
		arrow_b_ar.addPoint(x-s + green_b[4],s, z-s+1 + green_a[4]);
		arrow_b_ar.addPoint(x-s + green_b[5],s, z-s+1 + green_a[5]);
		arrow_b_ar.addPoint(x-s + green_b[6],s, z-s+1 + green_a[6]);
		
		Polygon3D arrow_b_tr = new Polygon3D();
		arrow_b_tr.setFillColor(Color.orange);
		arrow_b_tr.addPoint(x-s+1 - green_b[0],s, z-s - green_a[0]);
		arrow_b_tr.addPoint(x-s+1 - green_b[1],s, z-s - green_a[1]);
		arrow_b_tr.addPoint(x-s+1 - green_b[2],s, z-s - green_a[2]);
		arrow_b_tr.addPoint(x-s+1 - green_b[3],s, z-s - green_a[3]);
		arrow_b_tr.addPoint(x-s+1 - green_b[4],s, z-s - green_a[4]);
		arrow_b_tr.addPoint(x-s+1 - green_b[5],s, z-s - green_a[5]);
		arrow_b_tr.addPoint(x-s+1 - green_b[6],s, z-s - green_a[6]);
		
		
		Polygon3D arrow_b2_aw = new Polygon3D();
		arrow_b2_aw.setFillColor(Color.orange);
		arrow_b2_aw.addPoint(x-s+1 + green_a[0],s-11, z-s + green_b[0]);
		arrow_b2_aw.addPoint(x-s+1 + green_a[1],s-11, z-s + green_b[1]);
		arrow_b2_aw.addPoint(x-s+1 + green_a[2],s-11, z-s + green_b[2]);
		arrow_b2_aw.addPoint(x-s+1 + green_a[3],s-11, z-s + green_b[3]);
		arrow_b2_aw.addPoint(x-s+1 + green_a[4],s-11, z-s + green_b[4]);
		arrow_b2_aw.addPoint(x-s+1 + green_a[5],s-11, z-s + green_b[5]);
		arrow_b2_aw.addPoint(x-s+1 + green_a[6],s-11, z-s + green_b[6]);
		
		Polygon3D arrow_b2_tw = new Polygon3D();
		arrow_b2_tw.setFillColor(Color.orange);
		arrow_b2_tw.addPoint(x-s - green_a[0],s-11, z-s + green_b[0]);
		arrow_b2_tw.addPoint(x-s - green_a[1],s-11, z-s + green_b[1]);
		arrow_b2_tw.addPoint(x-s - green_a[2],s-11, z-s + green_b[2]);
		arrow_b2_tw.addPoint(x-s - green_a[3],s-11, z-s + green_b[3]);
		arrow_b2_tw.addPoint(x-s - green_a[4],s-11, z-s + green_b[4]);
		arrow_b2_tw.addPoint(x-s - green_a[5],s-11, z-s + green_b[5]);
		arrow_b2_tw.addPoint(x-s - green_a[6],s-11, z-s + green_b[6]);
		
		Polygon3D arrow_b2_ar = new Polygon3D();
		arrow_b2_ar.setFillColor(Color.orange);
		arrow_b2_ar.addPoint(x-s + green_b[0],s-11, z-s+1 + green_a[0]);
		arrow_b2_ar.addPoint(x-s + green_b[1],s-11, z-s+1 + green_a[1]);
		arrow_b2_ar.addPoint(x-s + green_b[2],s-11, z-s+1 + green_a[2]);
		arrow_b2_ar.addPoint(x-s + green_b[3],s-11, z-s+1 + green_a[3]);
		arrow_b2_ar.addPoint(x-s + green_b[4],s-11, z-s+1 + green_a[4]);
		arrow_b2_ar.addPoint(x-s + green_b[5],s-11, z-s+1 + green_a[5]);
		arrow_b2_ar.addPoint(x-s + green_b[6],s-11, z-s+1 + green_a[6]);
		
		Polygon3D arrow_b2_tr = new Polygon3D();
		arrow_b2_tr.setFillColor(Color.orange);
		arrow_b2_tr.addPoint(x-s+1 - green_b[0],s-11, z-s - green_a[0]);
		arrow_b2_tr.addPoint(x-s+1 - green_b[1],s-11, z-s - green_a[1]);
		arrow_b2_tr.addPoint(x-s+1 - green_b[2],s-11, z-s - green_a[2]);
		arrow_b2_tr.addPoint(x-s+1 - green_b[3],s-11, z-s - green_a[3]);
		arrow_b2_tr.addPoint(x-s+1 - green_b[4],s-11, z-s - green_a[4]);
		arrow_b2_tr.addPoint(x-s+1 - green_b[5],s-11, z-s - green_a[5]);
		arrow_b2_tr.addPoint(x-s+1 - green_b[6],s-11, z-s - green_a[6]);
		
		Polygon3D arrow_r_aw = new Polygon3D();
		arrow_r_aw.setFillColor(Color.orange);
		arrow_r_aw.addPoint(x-s+1 + green_a[0], y+s-1 + green_b[0], -s);
		arrow_r_aw.addPoint(x-s+1 + green_a[1], y+s-1 + green_b[1], -s);
		arrow_r_aw.addPoint(x-s+1 + green_a[2], y+s-1 + green_b[2], -s);
		arrow_r_aw.addPoint(x-s+1 + green_a[3], y+s-1 + green_b[3], -s);
		arrow_r_aw.addPoint(x-s+1 + green_a[4], y+s-1 + green_b[4], -s);
		arrow_r_aw.addPoint(x-s+1 + green_a[5], y+s-1 + green_b[5], -s);
		arrow_r_aw.addPoint(x-s+1 + green_a[6], y+s-1 + green_b[6], -s);
		
		Polygon3D arrow_r_tw = new Polygon3D();
		arrow_r_tw.setFillColor(Color.orange);
		arrow_r_tw.addPoint(x-s - green_a[0], y+s-1 + green_b[0], -s);
		arrow_r_tw.addPoint(x-s - green_a[1], y+s-1 + green_b[1], -s);
		arrow_r_tw.addPoint(x-s - green_a[2], y+s-1 + green_b[2], -s);
		arrow_r_tw.addPoint(x-s - green_a[3], y+s-1 + green_b[3], -s);
		arrow_r_tw.addPoint(x-s - green_a[4], y+s-1 + green_b[4], -s);
		arrow_r_tw.addPoint(x-s - green_a[5], y+s-1 + green_b[5], -s);
		arrow_r_tw.addPoint(x-s - green_a[6], y+s-1 + green_b[6], -s);
		
		Polygon3D arrow_r_ab = new Polygon3D();
		arrow_r_ab.setFillColor(Color.orange);
		arrow_r_ab.addPoint(x-s + green_b[0], y+s-1 - green_a[0], -s);
		arrow_r_ab.addPoint(x-s + green_b[1], y+s-1 - green_a[1], -s);
		arrow_r_ab.addPoint(x-s + green_b[2], y+s-1 - green_a[2], -s);
		arrow_r_ab.addPoint(x-s + green_b[3], y+s-1 - green_a[3], -s);
		arrow_r_ab.addPoint(x-s + green_b[4], y+s-1 - green_a[4], -s);
		arrow_r_ab.addPoint(x-s + green_b[5], y+s-1 - green_a[5], -s);
		arrow_r_ab.addPoint(x-s + green_b[6], y+s-1 - green_a[6], -s);
		
		Polygon3D arrow_r_tb = new Polygon3D();
		arrow_r_tb.setFillColor(Color.orange);
		arrow_r_tb.addPoint(x-s + green_b[0], y+s + green_a[0], -s);
		arrow_r_tb.addPoint(x-s + green_b[1], y+s + green_a[1], -s);
		arrow_r_tb.addPoint(x-s + green_b[2], y+s + green_a[2], -s);
		arrow_r_tb.addPoint(x-s + green_b[3], y+s + green_a[3], -s);
		arrow_r_tb.addPoint(x-s + green_b[4], y+s + green_a[4], -s);
		arrow_r_tb.addPoint(x-s + green_b[5], y+s + green_a[5], -s);
		arrow_r_tb.addPoint(x-s + green_b[6], y+s + green_a[6], -s);
		
		Polygon3D arrow_r2_aw = new Polygon3D();
		arrow_r2_aw.setFillColor(Color.orange);
		arrow_r2_aw.addPoint(x-s+1 + green_a[0], y+s-1 + green_b[0], -s+11);
		arrow_r2_aw.addPoint(x-s+1 + green_a[1], y+s-1 + green_b[1], -s+11);
		arrow_r2_aw.addPoint(x-s+1 + green_a[2], y+s-1 + green_b[2], -s+11);
		arrow_r2_aw.addPoint(x-s+1 + green_a[3], y+s-1 + green_b[3], -s+11);
		arrow_r2_aw.addPoint(x-s+1 + green_a[4], y+s-1 + green_b[4], -s+11);
		arrow_r2_aw.addPoint(x-s+1 + green_a[5], y+s-1 + green_b[5], -s+11);
		arrow_r2_aw.addPoint(x-s+1 + green_a[6], y+s-1 + green_b[6], -s+11);
		
		Polygon3D arrow_r2_tw = new Polygon3D();
		arrow_r2_tw.setFillColor(Color.orange);
		arrow_r2_tw.addPoint(x-s - green_a[0], y+s-1 + green_b[0], -s+11);
		arrow_r2_tw.addPoint(x-s - green_a[1], y+s-1 + green_b[1], -s+11);
		arrow_r2_tw.addPoint(x-s - green_a[2], y+s-1 + green_b[2], -s+11);
		arrow_r2_tw.addPoint(x-s - green_a[3], y+s-1 + green_b[3], -s+11);
		arrow_r2_tw.addPoint(x-s - green_a[4], y+s-1 + green_b[4], -s+11);
		arrow_r2_tw.addPoint(x-s - green_a[5], y+s-1 + green_b[5], -s+11);
		arrow_r2_tw.addPoint(x-s - green_a[6], y+s-1 + green_b[6], -s+11);
		
		Polygon3D arrow_r2_ab = new Polygon3D();
		arrow_r2_ab.setFillColor(Color.orange);
		arrow_r2_ab.addPoint(x-s + green_b[0], y+s-1 - green_a[0], -s+11);
		arrow_r2_ab.addPoint(x-s + green_b[1], y+s-1 - green_a[1], -s+11);
		arrow_r2_ab.addPoint(x-s + green_b[2], y+s-1 - green_a[2], -s+11);
		arrow_r2_ab.addPoint(x-s + green_b[3], y+s-1 - green_a[3], -s+11);
		arrow_r2_ab.addPoint(x-s + green_b[4], y+s-1 - green_a[4], -s+11);
		arrow_r2_ab.addPoint(x-s + green_b[5], y+s-1 - green_a[5], -s+11);
		arrow_r2_ab.addPoint(x-s + green_b[6], y+s-1 - green_a[6], -s+11);
		
		Polygon3D arrow_r2_tb = new Polygon3D();
		arrow_r2_tb.setFillColor(Color.orange);
		arrow_r2_tb.addPoint(x-s + green_b[0], y+s + green_a[0], -s+11);
		arrow_r2_tb.addPoint(x-s + green_b[1], y+s + green_a[1], -s+11);
		arrow_r2_tb.addPoint(x-s + green_b[2], y+s + green_a[2], -s+11);
		arrow_r2_tb.addPoint(x-s + green_b[3], y+s + green_a[3], -s+11);
		arrow_r2_tb.addPoint(x-s + green_b[4], y+s + green_a[4], -s+11);
		arrow_r2_tb.addPoint(x-s + green_b[5], y+s + green_a[5], -s+11);
		arrow_r2_tb.addPoint(x-s + green_b[6], y+s + green_a[6], -s+11);
		
		Object[] input_array = { startdot_w, startdot_b, startdot_r,
				arrow_w_tb,arrow_w_ab,arrow_w_ar,arrow_w_tr
				,arrow_b_tr,arrow_b_aw,arrow_b_ar,arrow_b_tw
				,arrow_r_tb,arrow_r_ab,arrow_r_tw,arrow_r_aw, startdot_r2,startdot_w2,startdot_b2,
				arrow_w2_tb,arrow_w2_ab,arrow_w2_ar,arrow_w2_tr, arrow_r2_tb,arrow_r2_ab,arrow_r2_tw,arrow_r2_aw, arrow_b2_tr, arrow_b2_ar,arrow_b2_aw, arrow_b2_tw};
		Object[] input_array_xy = {startdot_w,startdot_w2,arrow_w_tb,arrow_w_ab,arrow_w_ar,arrow_w_tr,arrow_w2_tb,arrow_w2_ab,arrow_w2_ar,arrow_w2_tr};
		Object[] input_array_yz = {startdot_b, startdot_b2,arrow_b_tr,arrow_b_aw,arrow_b_ar,arrow_b_tw,arrow_b2_tr,arrow_b2_aw,arrow_b2_ar,arrow_b2_tw};
		Object[] input_array_xz = {startdot_r,startdot_r2, arrow_r_tb,arrow_r_ab,arrow_r_tw,arrow_r_aw,arrow_r2_tb,arrow_r2_ab,arrow_r2_tw,arrow_r2_aw};
		Object[] input_array_tw = {arrow_b_tw,arrow_b2_tw, arrow_r_tw, arrow_r2_tw};
		Object[] input_array_tb = {arrow_w_tb,arrow_r_tb,arrow_w2_tb, arrow_r2_tb};
		Object[] input_array_tr = {arrow_b_tr,  arrow_b2_tr,arrow_w_tr, arrow_w2_tr};
		Object[] input_array_aw = {arrow_b_aw,arrow_b2_aw,arrow_r_aw,arrow_r2_aw};
		Object[] input_array_ar = {arrow_b_ar,arrow_b2_ar,arrow_w_ar,arrow_w2_ar};
		Object[] input_array_ab = {arrow_w_ab,arrow_r_ab,arrow_w2_ab,arrow_r2_ab};
		Object[] input_array_second_face_blue = { startdot_b2, arrow_b2_tr,arrow_b2_aw,arrow_b2_ar,arrow_b2_tw};
		Object[] input_array_second_face_red = {startdot_r2, arrow_r2_tb,arrow_r2_ab,arrow_r2_tw,arrow_r2_aw};
		Object[] input_array_second_face_white = {startdot_w2, arrow_w2_tb,arrow_w2_ab,arrow_w2_ar,arrow_w2_tr};
		Object[] input_array_second_faces = {arrow_r2_tw, arrow_r2_tb, arrow_w2_tr, arrow_r2_ab, arrow_r2_aw, arrow_w2_ar,
				arrow_w2_ab, arrow_w2_tb, startdot_w2, startdot_r2,arrow_b2_tw, arrow_b2_tr, arrow_w2_tr, arrow_b2_ar, arrow_b2_aw, arrow_w2_ar,
				arrow_w2_ab, arrow_w2_tb, startdot_w2, startdot_b2,arrow_r2_tw, arrow_r2_tb, arrow_b2_tr, arrow_r2_ab, arrow_r2_aw, arrow_b2_ar,
				arrow_b2_aw, arrow_b2_tw, startdot_b2, startdot_r2};
		
		
		
		
		holder = new PolygonCollection(input_array);
		//holder_stick = new PolygonCollection(input_array2);
		xyholder = new PolygonCollection(input_array_xy);
		yzholder = new PolygonCollection(input_array_yz);
		xzholder = new PolygonCollection(input_array_xz);
		towardW = new PolygonCollection(input_array_tw);
		towardB = new PolygonCollection(input_array_tb);
		towardR = new PolygonCollection(input_array_tr);
		awayW = new PolygonCollection(input_array_aw);
		awayR = new PolygonCollection(input_array_ar);
		awayB = new PolygonCollection(input_array_ab);
		secondFacesBlue = new PolygonCollection(input_array_second_face_blue);
		secondFacesWhite = new PolygonCollection(input_array_second_face_white);
		secondFacesRed = new PolygonCollection(input_array_second_face_red);
		secondFaces = new PolygonCollection(input_array_second_faces);

	}
	

	public Polygon3D[] extract() {
		return holder.extract_polygons();
	}
	public Polygon3D[] extract_stick() {
		return holder_stick.extract_polygons();
	}
	public Polygon3D[] extractTW() {
		return towardW.extract_polygons();
	}
	public Polygon3D[] extractTR() {
		return towardR.extract_polygons();
	}
	public Polygon3D[] extractTB() {
		return towardB.extract_polygons();
	}
	public Polygon3D[] extractAW() {
		return awayW.extract_polygons();
	}
	public Polygon3D[] extractAR() {
		return awayR.extract_polygons();
	}
	public Polygon3D[] extractAB() {
		return awayB.extract_polygons();
	}
	public Polygon3D[] extractSFB() {
		return secondFacesBlue.extract_polygons();
	}
	public Polygon3D[] extractSFW() {
		return secondFacesWhite.extract_polygons();
	}
	public Polygon3D[] extractSFR() {
		return secondFacesRed.extract_polygons();
	}
	public Polygon3D[] extractSF() {
		return secondFaces.extract_polygons();
	}
}