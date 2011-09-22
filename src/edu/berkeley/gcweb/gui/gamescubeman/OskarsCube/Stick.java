package edu.berkeley.gcweb.gui.gamescubeman.OskarsCube;

import java.awt.Color;

import edu.berkeley.gcweb.gui.gamescubeman.ThreeD.Polygon3D;

public class Stick {
	PolygonCollection holder;

	public Stick() {

		Polygon3D cap_1 = new Polygon3D();
		cap_1.setFillColor(Color.RED);
		cap_1.addPoint(0, 0, 0);
		cap_1.addPoint(1, 0, 0);
		cap_1.addPoint(1, 1, 0);
		cap_1.addPoint(0, 1, 0);
		Polygon3D top_long = new Polygon3D();
		top_long.setFillColor(Color.RED);
		top_long.addPoint(0, 1, 0);
		top_long.addPoint(1, 1, 0);
		top_long.addPoint(1, 1, 21);
		top_long.addPoint(0, 1, 21);
		Polygon3D left_long = new Polygon3D();
		left_long.setFillColor(Color.RED);
		left_long.addPoint(0, 0, 0);
		left_long.addPoint(0, 1, 0);
		left_long.addPoint(0, 1, 21);
		left_long.addPoint(0, 0, 21);
		Polygon3D right_long = new Polygon3D();
		right_long.setFillColor(Color.RED);
		right_long.addPoint(1, 0, 0);
		right_long.addPoint(1, 1, 0);
		right_long.addPoint(1, 1, 21);
		right_long.addPoint(1, 0, 21);
		Polygon3D bottom_long = new Polygon3D();
		bottom_long.setFillColor(Color.RED);
		bottom_long.addPoint(0, 0, 0);
		bottom_long.addPoint(0, 0, 21);
		bottom_long.addPoint(1, 0, 21);
		bottom_long.addPoint(1, 0, 0);
		Polygon3D cap2 = new Polygon3D();
		cap2.setFillColor(Color.RED);
		cap2.addPoint(0, 0, 21);
		cap2.addPoint(0, 1, 21);
		cap2.addPoint(1, 1, 21);
		cap2.addPoint(1, 0, 21);
		Polygon3D[] input_array = { cap_1, top_long, left_long, right_long,
				bottom_long, cap2 };
		holder = new PolygonCollection(input_array);
	}

	public Stick(int length) {
		Polygon3D cap_1 = new Polygon3D();
		cap_1.setFillColor(Color.GRAY);
		cap_1.addPoint(0, 0, 0);
		cap_1.addPoint(1, 0, 0);
		cap_1.addPoint(1, 1, 0);
		cap_1.addPoint(0, 1, 0);
		Polygon3D top_long = new Polygon3D();
		top_long.setFillColor(Color.GRAY);
		top_long.addPoint(0, 1, 0);
		top_long.addPoint(1, 1, 0);
		top_long.addPoint(1, 1, length);
		top_long.addPoint(0, 1, length);
		Polygon3D left_long = new Polygon3D();
		left_long.setFillColor(Color.GRAY);
		left_long.addPoint(0, 0, 0);
		left_long.addPoint(0, 1, 0);
		left_long.addPoint(0, 1, length);
		left_long.addPoint(0, 0, length);
		Polygon3D right_long = new Polygon3D();
		right_long.setFillColor(Color.GRAY);
		right_long.addPoint(1, 0, 0);
		right_long.addPoint(1, 1, 0);
		right_long.addPoint(1, 1, length);
		right_long.addPoint(1, 0, length);
		Polygon3D bottom_long = new Polygon3D();
		bottom_long.setFillColor(Color.GRAY);
		bottom_long.addPoint(0, 0, 0);
		bottom_long.addPoint(0, 0, length);
		bottom_long.addPoint(1, 0, length);
		bottom_long.addPoint(1, 0, 0);
		Polygon3D cap2 = new Polygon3D();
		cap2.setFillColor(Color.GRAY);
		cap2.addPoint(0, 0, length);
		cap2.addPoint(0, 1, length);
		cap2.addPoint(1, 1, length);
		cap2.addPoint(1, 0, length);
		Polygon3D[] input_array = { cap_1, top_long, left_long, right_long,
				bottom_long, cap2 };
		holder = new PolygonCollection(input_array);
	}

	public Stick(double length, int color) {
		Color colour;
		if (color == 1) {
			//Not on sol, not seen
			colour = Color.LIGHT_GRAY;
		} else if(color ==2) {
			//on sol, seen
			colour = Color.GREEN;
		} else if(color ==3) {
			//not on sol, seen
			colour = Color.RED;
		} else if(color ==5) {
			colour = Color.orange;
		}else {
			//on sol, not seen
			colour = Color.YELLOW;
		}
		Polygon3D cap_1 = new Polygon3D();
		cap_1.setFillColor(colour);
		cap_1.addPoint(.1, .1, .1);
		cap_1.addPoint(.9, .1, .1);
		cap_1.addPoint(.9,.9,.1);
		cap_1.addPoint(.1,.9,.1);
		Polygon3D top_long = new Polygon3D();
		top_long.setFillColor(colour);
		top_long.addPoint(.1,.9,.1);
		top_long.addPoint(.9,.9,.1);
		top_long.addPoint(.9, .9, length);
		top_long.addPoint(.1, .9, length);
		Polygon3D left_long = new Polygon3D();
		left_long.setFillColor(colour);
		left_long.addPoint(.1, .1, .1);
		left_long.addPoint(.1, .9, .1);
		left_long.addPoint(.1, .9, length);
		left_long.addPoint(.1, .1, length);
		Polygon3D right_long = new Polygon3D();
		right_long.setFillColor(colour);
		right_long.addPoint(.9, .1, .1);
		right_long.addPoint(.9, .9, .1);
		right_long.addPoint(.9, .9, length);
		right_long.addPoint(.9, .1, length);
		Polygon3D bottom_long = new Polygon3D();
		bottom_long.setFillColor(colour);
		bottom_long.addPoint(.1, .1, .1);
		bottom_long.addPoint(.1, .1, length);
		bottom_long.addPoint(.9, .1, length);
		bottom_long.addPoint(.9, .1, .1);
		Polygon3D cap2 = new Polygon3D();
		cap2.setFillColor(colour);
		cap2.addPoint(.1, .1, length);
		cap2.addPoint(.1, .9, length);
		cap2.addPoint(.9, .9, length);
		cap2.addPoint(.9, .1, length);
		Polygon3D[] input_array = { cap_1, top_long, left_long, right_long,
				bottom_long, cap2 };
		holder = new PolygonCollection(input_array);
	}

	public PolygonCollection returnItem() {
		return holder;
	}
}