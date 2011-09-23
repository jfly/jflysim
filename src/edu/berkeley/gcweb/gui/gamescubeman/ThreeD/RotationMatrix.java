package edu.berkeley.gcweb.gui.gamescubeman.ThreeD;

import java.text.DecimalFormat;
import java.util.Arrays;

public class RotationMatrix {
	private int SIZE = 3;
	private double[][] data;
	public RotationMatrix() {
		 data = new double[SIZE][SIZE];
		 for(int j = 0; j < SIZE; j++)
			 data[j][j] = 1; //constructing the identity matrix
	}
	private RotationMatrix(double[][] data) {
		this.data = data;
	}
	private static int toBit(boolean b) {
		return b ? 1 : 0;
	}
	public RotationMatrix(int axis, double degreesCCW) {
		this(toBit(axis==0), toBit(axis==1), toBit(axis==2), degreesCCW);
	}
	private double lx, ly, lz;
	private double degreesCCW;
	public RotationMatrix(double lx, double ly, double lz, double degreesCCW) {
		this.lx = lx;
		this.ly = ly;
		this.lz = lz;
		this.degreesCCW = degreesCCW;
		//normalizing the vector to rotate about
		double lx2 = lx*lx; double ly2 = ly*ly; double lz2 = lz*lz;
		double scale = Math.sqrt(lx2 + ly2 + lz2);
		lx /= scale; ly /= scale; lz /= scale;
		lx2 = lx*lx; ly2 = ly*ly; lz2 = lz*lz;
		//this math is coming straight from wikipedia, i don't claim to understand it at all
		double c = Math.cos(Math.toRadians(degreesCCW));
		double s = Math.sin(Math.toRadians(degreesCCW));
		data = new double[][] {
				{ lx2 + (1-lx2)*c, lx*ly*(1-c) + lz*s, lx*lz*(1-c) - ly*s },
				{ lx*ly*(1-c) - lz*s, ly2 + (1-ly2)*c, ly*lz*(1-c) + lx*s },
				{ lx*lz*(1-c) + ly*s, ly*lz*(1-c) - lx*s, lz2 + (1-lz2)*c }
		};
	}
	public RotationMatrix multiply(RotationMatrix m) {
		int matchingSide = this.data[0].length;
		if(matchingSide != m.data.length)
			return null;
		RotationMatrix result = new RotationMatrix(new double[this.data.length][m.data[0].length]);
		for(int i=0; i<result.data.length; i++) {
			for(int j=0; j<result.data[0].length; j++) {
				double dot = 0;
				for(int ch=0; ch<matchingSide; ch++)
					dot += this.data[i][ch] * m.data[ch][j];
				result.data[i][j] = dot;
			}
		}
		return result;
	}
	public double[] leftMultiply(double[] point) {
		return new RotationMatrix(new double[][]{ point }).multiply(this).data[0];
	}
	public double[] leftMultiply(double x, double y, double z) {
		return leftMultiply(new double[] { x, y, z });
	}
	private double[][] transpose(double[][] m) {
		double[][] t = new double[m[0].length][m.length];
		for(int i=0; i<t.length; i++)
			for(int j=0; j<t[0].length; j++)
				t[i][j] = m[j][i];
		return t;
	}
	public RotationMatrix scaleRotation(double scale) {
		return new RotationMatrix(this.lx, this.ly, this.lz, scale*degreesCCW);
	}
	public boolean isIdentity() {
		return isIdentity(0);
	}
	public boolean isIdentity(double tolerance) {
		return equals(new RotationMatrix(), tolerance);
	}
	public boolean equals(RotationMatrix other) {
		return equals(other, 0);
	}
	public boolean equals(RotationMatrix other, double tolerance) {
		for(int i=0; i<data.length; i++)
			for(int j=0; j<data[i].length; j++)
				if(Math.abs(data[i][j] - other.data[i][j]) > tolerance)
					return false;
		return true;
	
	}
	private static final DecimalFormat df = new DecimalFormat("0.000");
	public String toString(double[][] data) {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<data.length; i++) {
			for(int j=0; j<data[0].length; j++)
				sb.append("  " + df.format(data[i][j]));
			sb.append("\n");
		}
		return "[" + sb.toString().substring(1, sb.length() - 1) + " ]\n";
	}
	public String toString() {
		return toString(data);
	}
	public static void main(String[] args) {
		System.out.println(new RotationMatrix(1, 30.));
		System.out.println(new RotationMatrix(1, 30./5));
		System.out.println(new RotationMatrix(1, 30.).multiply(new RotationMatrix(0, 180.)));
		
		System.out.println(new RotationMatrix(2, 90));
		System.out.println(Arrays.toString(new RotationMatrix(2, 90).leftMultiply(new double[] { 1, 0, 0 })));
	}
}
