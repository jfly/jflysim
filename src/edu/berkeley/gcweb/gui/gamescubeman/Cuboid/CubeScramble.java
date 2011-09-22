package edu.berkeley.gcweb.gui.gamescubeman.Cuboid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//The public arrays are going to be accessed via reflection from the ScramblePlugin class
//This way, other scramble plugins will not be able to modify the static arrays of other
//scramble plugins.
@SuppressWarnings("unused") 
public class CubeScramble extends Scramble {
	private static final String[][] FACE_NAMES_COLORS = 
	{ { "L",	  "D",		"B", 	  "R", 		"U", 	  "F" },
	  { "ffc800", "ffff00", "0000ff", "ff0000", "ffffff", "00ff00" } };
	private static final String PUZZLE_NAME = "Cube";
	private static final String[] VARIATIONS = { "2x2x2", "3x3x3", "4x4x4", "5x5x5", "6x6x6", "7x7x7", "8x8x8", "9x9x9", "10x10x10", "11x11x11" };
	private static final int[] DEFAULT_LENGTHS = { 25,	 25,		40,		60,		80,			100,	120,	140,	160,		180 };
	private static final String[] ATTRIBUTES = {"%%multislice%%", "%%widenotation%%", "Optimal Cross"};
	private static final String[] DEFAULT_ATTRIBUTES = ATTRIBUTES;
	private static final int DEFAULT_UNIT_SIZE = 11;
	private static final Pattern TOKEN_REGEX = Pattern.compile("^((?:\\d+)?[LDBRUFldbruf](?:\\(\\d+\\))?w?[2']?)(.*)$");
	
	private static final String FACES = "LDBRUFldbruf";
	private static final boolean shortNotation = true;
	private boolean multislice;
	private boolean wideNotation;
	private boolean optimalCross;
	private int size;
	private int[][][] image;

	private static int getSizeFromVariation(String variation) {
		return variation.isEmpty() ? 3 : Integer.parseInt(variation.split("x")[0]);
	}

	public CubeScramble(String variation, int length, String generatorGroup, String... attrs) {
		this(getSizeFromVariation(variation), length, attrs);
	}

	private CubeScramble(int size, int length, String... attrs) {
		this.size = size;
		super.length = length;
		setAttributes(attrs);
	}

	public CubeScramble(String variation, String s, String generatorGroup, String... attrs) throws InvalidScrambleException {
		super(s);
		this.size = Integer.parseInt(variation.split("x")[0]);
		if(!setAttributes(attrs))
			throw new InvalidScrambleException(s);
	}

	private boolean setAttributes(String... attributes) {
		multislice = false;
		wideNotation = false;
		optimalCross = false;
		for(String attr : attributes) {
			if(attr.equals(ATTRIBUTES[0]))
				multislice = true;
			else if(attr.equals(ATTRIBUTES[1]))
				wideNotation = true;
			else if(attr.equals(ATTRIBUTES[2]))
				optimalCross = true;
		}
		initializeImage();
		
		if(scramble == null) {
			if(size == 2) {
				calcperm();
				mix();
				scramble = solve();
			} else if(size == 3) {
//				scramble = Search.solution(Tools.randomCube(), 21, 10, false);
			}
		}
		
		boolean success = true;
		if(scramble != null) {
			success = validateScramble();
		} else {
			generateScramble();
		}
		return success;
	}
	
	private String cacheInfo = null;
	@Override
	public String getExtraInfo() {
		if(!optimalCross) return null;
		if(cacheInfo == null)
			cacheInfo = getCrossSolutions().toString();
		return cacheInfo;
	}
	
	private ArrayList<String> getCrossSolutions() {
		return null;
//		ACube.err.enabled = false;
//		CubeReader r = new CubeReader();
//		Options o = new Options();
//		o.findAll = true;
//		o.findOptimal = true;
//		o.metric = Turn.FACE_METRIC;
//		r.initTurns(o, "UF UR UB UL @? @? @? @? @? @? @? @? @? @? @? @? @? @? @? @?", scramble);
//		return r.solve();
	}

	JavascriptArray<Integer> posit = new JavascriptArray<Integer>();
	private void initbrd(){
	    posit = new JavascriptArray<Integer>(
	                1,1,1,1,
	                2,2,2,2,
	                5,5,5,5,
	                4,4,4,4,
	                3,3,3,3,
	                0,0,0,0);
	}
	{
		initbrd();
	}
	JavascriptArray<Integer> seq = new JavascriptArray<Integer>();
	private boolean solved(){
	    for (int i=0;i<24; i+=4){
	        int c=posit.get(i);
	        for(int j=1;j<4;j++)
	            if(posit.get(i+j)!=c) return false;
	    }
	    return true;
	}
	int[][] cornerIndices = new int[][] { {15, 16, 21}, {14, 20, 4}, {13, 9, 17}, {12, 5, 8}, {3, 23, 18}, {2, 6, 22}, {1, 19, 11}, {0, 10, 7} };
	String[] cornerNames =   new String[] { "URF", 		"UFL", 		"UBR", 		"ULB", 			"DFR", 		"DLF", 		"DRB", 		"DBL" };
	HashMap<Character, Integer> faceToIndex = new HashMap<Character, Integer>();
	{
		faceToIndex.put('D', 1);
		faceToIndex.put('L', 2);
		faceToIndex.put('B', 5);
		faceToIndex.put('U', 4);
		faceToIndex.put('R', 3);
		faceToIndex.put('F', 0);
	}
	private void mix(){
		//Modified to choose a random state, rather than apply 500 random turns
		//-Jeremy Fleischman
		ArrayList<Integer> remaining = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
		ArrayList<Integer> cp = new ArrayList<Integer>();
		while(remaining.size() > 0)
			cp.add(remaining.remove((int)Math.floor(Math.random()*remaining.size())));
		//it would appear that the solver only works if the BLD piece is fixed, which is fine
		cp.add(7);

	    initbrd();
	    ArrayList<Integer> co = new ArrayList<Integer>();
		int sum = 0;
		for(int i = 0; i < cp.size(); i++) {
			int orientation;
			if(i == cp.size() - 1)
				orientation = 0;
			else if(i == cp.size() - 2)
				orientation = (3 - sum) % 3;
			else
				orientation = (int) Math.floor(Math.random()*3);
			co.add(orientation);
			sum = (sum + orientation) % 3;
			for(int j = 0; j < 3; j++) {
				int jj = (j + orientation) % 3;
				posit.set(cornerIndices[i][j], faceToIndex.get(cornerNames[cp.get(i)].charAt(jj)));
			}
		}
	}
	JavascriptArray<Integer> piece = new JavascriptArray<Integer>(15,16,16,21,21,15,  13,9,9,17,17,13,  14,20,20,4,4,14,  12,5,5,8,8,12,
	                        3,23,23,18,18,3,   1,19,19,11,11,1,  2,6,6,22,22,2,    0,10,10,7,7,0);
	JavascriptArray<JavascriptArray<Integer>> adj = new JavascriptArray<JavascriptArray<Integer>>();
	{
		adj.set(0, new JavascriptArray<Integer>());
		adj.set(1, new JavascriptArray<Integer>());
		adj.set(2, new JavascriptArray<Integer>());
		adj.set(3, new JavascriptArray<Integer>());
		adj.set(4, new JavascriptArray<Integer>());
		adj.set(5, new JavascriptArray<Integer>());
	}
	JavascriptArray<Integer> opp=new JavascriptArray<Integer>();
	int auto;
	JavascriptArray<Integer> tot;
	private void calcadj(){
	    //count all adjacent pairs (clockwise around corners)
	    int a,b;
	    for(a=0;a<6;a++)for(b=0;b<6;b++) adj.get(a).set(b, 0);
	    for(a=0;a<48;a+=2){
	        if(posit.get(piece.get(a))<=5 && posit.get(piece.get(a+1))<=5) {
	            JavascriptArray<Integer> temp = adj.get(posit.get(piece.get(a)));
	            int index = posit.get(piece.get(a+1));
	            temp.set(index, temp.get(index) + 1);
	        }
	    }
	}
	private void calctot(){
	    //count how many of each colour
	    tot=new JavascriptArray<Integer>(0,0,0,0,0,0,0);
	    for(int e=0;e<24;e++) tot.set(posit.get(e), tot.get(posit.get(e))+1);
	}
	JavascriptArray<JavascriptArray<Integer>> mov2fc = new JavascriptArray<JavascriptArray<Integer>>();
	{
		mov2fc.set(0, new JavascriptArray<Integer>(0, 2, 3, 1, 23,19,10,6 ,22,18,11,7 )); //D
		mov2fc.set(1, new JavascriptArray<Integer>(4, 6, 7, 5, 12,20,2, 10,14,22,0, 8 )); //L
		mov2fc.set(2, new JavascriptArray<Integer>(8, 10,11,9, 12,7, 1, 17,13,5, 0, 19)); //B
		mov2fc.set(3, new JavascriptArray<Integer>(12,13,15,14,8, 17,21,4, 9, 16,20,5 )); //U
		mov2fc.set(4, new JavascriptArray<Integer>(16,17,19,18,15,9, 1, 23,13,11,3, 21)); //R
		mov2fc.set(5, new JavascriptArray<Integer>(20,21,23,22,14,16,3, 6, 15,18,2, 4 )); //F
	}
	private void domove(int y){
	    int q=1+(y>>4);
	    int f=y&15;
	    while(q > 0){
	        for(int i=0;i<mov2fc.get(f).size();i+=4){
	            int c=posit.get(mov2fc.get(f).get(i));
	            posit.set(mov2fc.get(f).get(i), posit.get(mov2fc.get(f).get(i+3)));
	            posit.set(mov2fc.get(f).get(i+3), posit.get(mov2fc.get(f).get(i+2)));
	            posit.set(mov2fc.get(f).get(i+2), posit.get(mov2fc.get(f).get(i+1)));
	            posit.set(mov2fc.get(f).get(i+1), c);
	        }
	        q--;
	    }
	}
	JavascriptArray<Integer> sol=new JavascriptArray<Integer>();
	private String solve(){
	    calcadj();
	    JavascriptArray<Integer> opp=new JavascriptArray<Integer>();
	    for(int a=0;a<6;a++){
	        for(int b=0;b<6;b++){
	            if(a!=b && adj.get(a).get(b)+adj.get(b).get(a)==0) { opp.set(a, b); opp.set(b, a); }
	        }
	    }
	    //Each piece is determined by which of each pair of opposite colours it uses.
	    JavascriptArray<Integer> ps=new JavascriptArray<Integer>();
	    JavascriptArray<Integer> tws=new JavascriptArray<Integer>();
	    int a=0;
	    for(int d=0; d<7; d++){
	        int p=0;
	        for(int b=a;b<a+6;b+=2){
	            if(posit.get(piece.get(b))==posit.get(piece.get(42))) p+=4;
	            if(posit.get(piece.get(b))==posit.get(piece.get(44))) p+=1;
	            if(posit.get(piece.get(b))==posit.get(piece.get(46))) p+=2;
	        }
	        ps.set(d, p);
	        if(posit.get(piece.get(a))==posit.get(piece.get(42)) || posit.get(piece.get(a))==opp.get(posit.get(piece.get(42)))) tws.set(d, 0);
	        else if(posit.get(piece.get(a+2))==posit.get(piece.get(42)) || posit.get(piece.get(a+2))==opp.get(posit.get(piece.get(42)))) tws.set(d, 1);
	        else tws.set(d, 2);
	        a+=6;
	    }
	    //convert position to numbers
	    int q=0;
	    for(a=0;a<7;a++){
	        int b=0;
	        for(int c=0;c<7;c++){
	            if(ps.get(c)==a)break;
	            if(ps.get(c)>a)b++;
	        }
	        q=q*(7-a)+b;
	    }
	    int t=0;
	    for(a=5;a>=0;a--){
	        t=(int) (t*3+tws.get(a)-3*Math.floor(tws.get(a)/3));
	    }
	    if(q!=0 || t!=0){
	    	sol.clear();
	        for(int l=seq.size();l<100;l++){
	            if(search(0,q,t,l,-1)) break;
	        }
	        String tt="";
	        for(q=0;q<sol.size();q++){
	            tt = "URF".charAt(sol.get(q)/10)+ "" + "\'2 ".charAt(sol.get(q)%10) + " " + tt;
	        }
	        return tt;
	    }
	    return null;
	}
	private boolean search(int d, int q, int t, int l, int lm){
	    //searches for solution, from position q|t, in l moves exactly. last move was lm, current depth=d
	    if(l==0){
	        if(q==0 && t==0){
	            return true;
	        }
	    }else{
	        if(perm.get(q)>l || twst.get(t)>l)
	        	return false;
	        int p,s,a,m;
	        for(m=0;m<3;m++){
	            if(m!=lm){
	                p=q; s=t;
	                for(a=0;a<3;a++){
	                    p=permmv.get(p).get(m);
	                    s=twstmv.get(s).get(m);
	                    sol.set(d, 10*m+a);
	                    if(search(d+1,p,s,l-1,m)) {
	                    	return true;
	                    }
	                }
	            }
	        }
	    }
	    return false;
	}
	JavascriptArray<Integer> perm=new JavascriptArray<Integer>();
	JavascriptArray<Integer> twst=new JavascriptArray<Integer>();
	JavascriptArray<JavascriptArray<Integer>> permmv=new JavascriptArray<JavascriptArray<Integer>>();
	JavascriptArray<JavascriptArray<Integer>> twstmv=new JavascriptArray<JavascriptArray<Integer>>();
	private void calcperm(){
	    //calculate solving arrays
	    //first permutation
	 
	    for(int p=0;p<5040;p++){
	        perm.set(p, -1);
	        permmv.set(p, new JavascriptArray<Integer>());
	        for(int m=0;m<3;m++){
	            permmv.get(p).set(m, getprmmv(p,m));
	        }
	    }
	 
	    perm.set(0, 0);
	    for(int l=0;l<=6;l++){
	        int n=0;
	        for(int p=0;p<5040;p++){
	            if(perm.get(p)==l){
	                for(int m=0;m<3;m++){
	                    int q=p;
	                    for(int c=0;c<3;c++){
	                        q=permmv.get(q).get(m);
	                        if(perm.get(q)==-1) { perm.set(q, l+1); n++; }
	                    }
	                }
	            }
	        }
	    }
	 
	    //then twist
	    for(int p=0;p<729;p++){
	        twst.set(p, -1);
	        twstmv.set(p, new JavascriptArray<Integer>());
	        for(int m=0;m<3;m++){
	            twstmv.get(p).set(m, gettwsmv(p,m));
	        }
	    }
	 
	    twst.set(0, 0);
	    for(int l=0;l<=5;l++){
	        int n=0;
	        for(int p=0;p<729;p++){
	            if(twst.get(p)==l){
	                for(int m=0;m<3;m++){
	                    int q=p;
	                    for(int c=0;c<3;c++){
	                        q=twstmv.get(q).get(m);
	                        if(twst.get(q)==-1) { twst.set(q, l+1); n++; }
	                    }
	                }
	            }
	        }
	    }
	    //remove wait sign
	}
	private int getprmmv(int p, int m){
	    //given position p<5040 and move m<3, return new position number
	    int a,b,c,q;
	    //convert number into array;
	    JavascriptArray<Integer> ps=new JavascriptArray<Integer>();
	    q=p;
	    for(a=1;a<=7;a++){
	        b=q%a;
	        q=(q-b)/a;
	        for(c=a-1;c>=b;c--) {
	        	Integer ii = null;
	        	try {
	        		ii = ps.get(c);
	        	} catch(Exception e) {}
	        	ps.set(c+1, ii);
	        }
	        ps.set(b, 7-a);
	    }
	    //perform move on array
	    if(m==0){
	        //U
	        c=ps.get(0);ps.set(0, ps.get(1));ps.set(1, ps.get(3));ps.set(3, ps.get(2));ps.set(2, c);
	    }else if(m==1){
	        //R
	    	c=ps.get(0);ps.set(0, ps.get(4));ps.set(4, ps.get(5));ps.set(5, ps.get(1));ps.set(1, c);
	    }else if(m==2){
	        //F
	    	c=ps.get(0);ps.set(0, ps.get(2));ps.set(2, ps.get(6));ps.set(6, ps.get(4));ps.set(4, c);
	    }
	    //convert array back to number
	    q=0;
	    for(a=0;a<7;a++){
	        b=0;
	        for(c=0;c<7;c++){
	            if(ps.get(c)==a)break;
	            if(ps.get(c)>a)b++;
	        }
	        q=q*(7-a)+b;
	    }
	    return q;
	}
	private int gettwsmv(int p, int m){
	    //given orientation p<729 and move m<3, return new orientation number
	    int a,b,c,d,q;
	    //convert number into array;
	    JavascriptArray<Integer> ps=new JavascriptArray<Integer>();
	    q=p;
	    d=0;
	    for(a=0;a<=5;a++){
	        c=(int) Math.floor(q/3);
	        b=q-3*c;
	        q=c;
	        ps.set(a, b);
	        d-=b;if(d<0)d+=3;
	    }
	    ps.set(6, d);
	    //perform move on array
	    if(m==0){
	        //U
	        c=ps.get(0);ps.set(0, ps.get(1));ps.set(1, ps.get(3));ps.set(3, ps.get(2));ps.set(2, c);
	    }else if(m==1){
	        //R
	    	c=ps.get(0);ps.set(0, ps.get(4));ps.set(4, ps.get(5));ps.set(5, ps.get(1));ps.set(1, c);
	    	ps.set(0, ps.get(0)+2); ps.set(1, ps.get(1)+1); ps.set(5, ps.get(5)+2); ps.set(4, ps.get(4)+1);
	    }else if(m==2){
	        //F
	    	c=ps.get(0);ps.set(0, ps.get(2));ps.set(2, ps.get(6));ps.set(6, ps.get(4));ps.set(4, c);
	    	ps.set(2, ps.get(2)+2); ps.set(0, ps.get(0)+1); ps.set(4, ps.get(4)+2); ps.set(6, ps.get(6)+1);
	    }
	    //convert array back to number
	    q=0;
	    for(a=5;a>=0;a--){
	        q=q*3+(ps.get(a)%3);
	    }
	    return q;
	}
	
	private void generateScramble(){
		scramble = "";
		StringBuilder scram = new StringBuilder();
		int lastAxis = -1;
		int axis = 0;
		int slices = size - ((multislice || size % 2 != 0) ? 1 : 0);
		int[] slicesMoved = new int[slices];
		int[] directionsMoved = new int[3];
		int moved = 0;

		for(int i = 0; i < length; i += moved){
			moved = 0;
			do{
				axis = random(3);
			} while(axis == lastAxis);

			for(int j = 0; j < slices; j++) slicesMoved[j] = 0;
			for(int j = 0; j < 3; j++) directionsMoved[j] = 0;

			do{
				int slice;
				do{
					slice = random(slices);
				} while(slicesMoved[slice] != 0);
				int direction = random(3);

				if(multislice || slices != size || (directionsMoved[direction] + 1) * 2 < slices ||
					(directionsMoved[direction] + 1) * 2 == slices && directionsMoved[0] + directionsMoved[1] + directionsMoved[2] == directionsMoved[direction]){
					directionsMoved[direction]++;
					moved++;
					slicesMoved[slice] = direction + 1;
				}
			} while(random(3) == 0 && moved < slices && moved + i < length);

			for(int j = 0; j < slices; j++){
				if(slicesMoved[j] > 0){
					int direction = slicesMoved[j] - 1;
					int face = axis;
					int slice = j;
					if(2 * j + 1 >= slices){
						face += 3;
						slice = slices - 1 - slice;
						direction = 2 - direction;
					}

					int n = ((slice * 6 + face) * 4 + direction);
					scram.append(" ");
					scram.append(moveString(n));

					do{
						slice(face, slice, direction);
						slice--;
					} while(multislice && slice >= 0);
				}
			}
			lastAxis = axis;
		}
		if(scram.length() > 0)
			scramble = scram.substring(1);
	}
	
	public static String htmlify(String formatMe) {
		return formatMe.replaceAll("\\((\\d+)\\)", "<sub>$1</sub>");
	}

	private String moveString(int n) {
		String move = "";
		int face = n >> 2;
		int direction = n & 3;

		if(size <= 5){
			if(wideNotation){
				move += FACES.charAt(face % 6);
				if(face / 6 != 0) move += "w";
			}
			else{
				move += FACES.charAt(face);
			}
		}
		else{
			String f = "" + FACES.charAt(face % 6);
			if(face / 6 == 0) {
				move += f;
			} else {
				if(shortNotation) {
					move += (face / 6 + 1) + f;
				} else {
					move += f + "(" + (face / 6 + 1) + ")";
				}
			}
		}
		if(direction != 0) move += " 2'".charAt(direction);

		return move;
	}

	private final static String regexp2 = "^[LDBRUF][2']?$";
	private final static String regexp345 = "^(?:[LDBRUF]w?|[ldbruf])[2']?$";
	private final static String regexp = "^(\\d+)?([LDBRUF])(?:\\((\\d+)\\))?[2']?$";
	private final static Pattern shortPattern = Pattern.compile(regexp);
	private boolean validateScramble(){
		String[] strs = scramble.split("\\s+");
		length = strs.length;

		if(size < 2) return false;
		else if(size == 2){
			for(int i = 0; i < strs.length; i++){
				if(!strs[i].matches(regexp2)) return false;
			}
		}
		else if(size <= 5){
			for(int i = 0; i < strs.length; i++){
				if(!strs[i].matches(regexp345)) return false;
			}
		}
		else{
			for(int i = 0; i < strs.length; i++){
				if(!strs[i].matches(regexp)) return false;
			}
		}

		StringBuilder newScram = new StringBuilder();
		try{
			for(int i = 0; i < strs.length; i++){
				int face;
				String slice1 = null;
				if(size > 5) {
					Matcher m = shortPattern.matcher(strs[i]);
					if(!m.matches()) {
						return false;
					}
					slice1 = m.group(1);
					String slice2 = m.group(3);
					if(slice1 != null && slice2 != null) { //only short notation or long notation is allowed, not both
						return false;
					}
					if(slice1 == null)
						slice1 = slice2;
					face = FACES.indexOf(m.group(2));
				} else {
					face = FACES.indexOf(strs[i].charAt(0) + "");
				}

				int slice = face / 6;
				face %= 6;
				if(strs[i].indexOf("w") >= 0) slice++;
				else if(slice1 != null)
					slice = Integer.parseInt(slice1) - 1;

				int dir = " 2'".indexOf(strs[i].charAt(strs[i].length() - 1) + "");
				if(dir < 0) dir = 0;
				
				int n = ((slice * 6 + face) * 4 + dir);
				newScram.append(" ");
				newScram.append(moveString(n));

				do{
					slice(face, slice, dir);
					slice--;
				} while(multislice && slice >= 0);
			}
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}

		if(newScram.length() > 0)
			scramble = newScram.substring(1); //we do this to force notation update when an attribute changes
		else scramble = newScram.toString();
		return true;
	}
	private void initializeImage(){
		image = new int[6][size][size];

		for(int i = 0; i < 6; i++){
			for(int j = 0; j < size; j++){
				for(int k = 0; k < size; k++){
					image[i][j][k] = i;
				}
			}
		}
	}

	private void slice(int face, int slice, int dir){
		face %= 6;
		int sface = face;
		int sslice = slice;
		int sdir = dir;

		if(face > 2){
			sface -= 3;
			sslice = size - 1 - slice;
			sdir = 2 - dir;
		}
		for(int i = 0; i <= sdir; i++){
			for(int j = 0; j < size; j++){
				if(sface == 0){
					int temp = image[4][j][sslice];
					image[4][j][sslice] = image[2][size-1-j][size-1-sslice];
					image[2][size-1-j][size-1-sslice] = image[1][j][sslice];
					image[1][j][sslice] = image[5][j][sslice];
					image[5][j][sslice] = temp;
				}
				else if(sface == 1){
					int temp = image[0][size-1-sslice][j];
					image[0][size-1-sslice][j] = image[2][size-1-sslice][j];
					image[2][size-1-sslice][j] = image[3][size-1-sslice][j];
					image[3][size-1-sslice][j] = image[5][size-1-sslice][j];
					image[5][size-1-sslice][j] = temp;
				}
				else if(sface == 2){
					int temp = image[4][sslice][j];
					image[4][sslice][j] = image[3][j][size-1-sslice];
					image[3][j][size-1-sslice] = image[1][size-1-sslice][size-1-j];
					image[1][size-1-sslice][size-1-j] = image[0][size-1-j][sslice];
					image[0][size-1-j][sslice] = temp;
				}
			}
		}
		if(slice == 0){
			for(int i = 0; i <= 2-dir; i++){
				for(int j = 0; j < (size+1)/2; j++){
					for(int k = 0; k < size/2; k++){
						int temp = image[face][j][k];
						image[face][j][k] = image[face][k][size-1-j];
						image[face][k][size-1-j] = image[face][size-1-j][size-1-k];
						image[face][size-1-j][size-1-k] = image[face][size-1-k][j];
						image[face][size-1-k][j] = temp;
					}
				}
			}
		}
	}

	public BufferedImage getScrambleImage(int gap, int cubieSize, Color[] colorScheme) {
		Dimension dim = getImageSize(gap, cubieSize, size);
		BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
		drawCube(buffer.createGraphics(), image, gap, cubieSize, colorScheme);
		return buffer;
	}
	
	public static int getNewUnitSize(int width, int height, int gap, String variation) {
		return getNewUnitSize(width, height, gap, getSizeFromVariation(variation));
	}
	private static int getNewUnitSize(int width, int height, int gap, int size) {
		return (int) Math.min((width - 5*gap) / 4. / size,
				(height - 4*gap) / 3. / size);
	}
	
	public static Dimension getImageSize(int gap, int unitSize, String variation) {
		return getImageSize(gap, unitSize, getSizeFromVariation(variation));
	}
	private static Dimension getImageSize(int gap, int unitSize, int size) {
		return new Dimension(getCubeViewWidth(unitSize, gap, size), getCubeViewHeight(unitSize, gap, size));
	}

	private void drawCube(Graphics2D g, int[][][] state, int gap, int cubieSize, Color[] colorScheme){
		int size = state[0].length;
		paintCubeFace(g, gap, 2*gap+size*cubieSize, size, cubieSize, state[0], colorScheme);
		paintCubeFace(g, 2*gap+size*cubieSize, 3*gap+2*size*cubieSize, size, cubieSize, state[1], colorScheme);
		paintCubeFace(g, 4*gap+3*size*cubieSize, 2*gap+size*cubieSize, size, cubieSize, state[2], colorScheme);
		paintCubeFace(g, 3*gap+2*size*cubieSize, 2*gap+size*cubieSize, size, cubieSize, state[3], colorScheme);
		paintCubeFace(g, 2*gap+size*cubieSize, gap, size, cubieSize, state[4], colorScheme);
		paintCubeFace(g, 2*gap+size*cubieSize, 2*gap+size*cubieSize, size, cubieSize, state[5], colorScheme);
	}

	private void paintCubeFace(Graphics2D g, int x, int y, int size, int cubieSize, int[][] faceColors, Color[] colorScheme) {
		for(int row = 0; row < size; row++) {
			for(int col = 0; col < size; col++) {
				g.setColor(Color.BLACK);
				int tempx = x + col*cubieSize;
				int tempy = y + row*cubieSize;
				g.drawRect(tempx, tempy, cubieSize, cubieSize);
				g.setColor(colorScheme[faceColors[row][col]]);
				g.fillRect(tempx + 1, tempy + 1, cubieSize - 1, cubieSize - 1);
			}
		}
	}
	private static int getCubeViewWidth(int cubie, int gap, int size) {
		return (size*cubie + gap)*4 + gap;
	}
	private static int getCubeViewHeight(int cubie, int gap, int size) {
		return (size*cubie + gap)*3 + gap;
	}

	public static Shape[] getFaces(int gap, int cubieSize, String variation) {
		int size = getSizeFromVariation(variation);
		return new Shape[] {
				getFace(gap, 2*gap+size*cubieSize, size, cubieSize),
				getFace(2*gap+size*cubieSize, 3*gap+2*size*cubieSize, size, cubieSize),
				getFace(4*gap+3*size*cubieSize, 2*gap+size*cubieSize, size, cubieSize),
				getFace(3*gap+2*size*cubieSize, 2*gap+size*cubieSize, size, cubieSize),
				getFace(2*gap+size*cubieSize, gap, size, cubieSize),
				getFace(2*gap+size*cubieSize, 2*gap+size*cubieSize, size, cubieSize)
		};
	}
	private static Shape getFace(int leftBound, int topBound, int size, int cubieSize) {
		return new Rectangle(leftBound, topBound, size * cubieSize, size * cubieSize);
	}
	
	public static void main(String[] args) {
////		CubeScramble cs = new CubeScramble(3, 25);
////		System.out.println(cs);
////		System.out.println(cs.getExtraInfo());
//		
//		ACube.err.enabled = true;
//		CubeReader r = new CubeReader();
//		Options o = new Options();
////		o.findAll = true;
////		o.findOptimal = true;
//		o.metric = Turn.FACE_METRIC;
//		r.initRandom(o);
////		r.initTurns(o, null, "R2 U R U R' U' R' U' R' U R'");
////		r.initTurns(o, null, "");
//		long start = System.currentTimeMillis();
//		System.out.println(r.solve());
//		System.out.println("Took " + (System.currentTimeMillis()-start)/1000. + " seconds to solve.");
//		start = System.currentTimeMillis();
//		System.out.println(r.solve());
//		System.out.println("Took " + (System.currentTimeMillis()-start)/1000. + " seconds to solve.");
		
//		long start = System.currentTimeMillis();
//		System.out.println(Search.solution(Tools.randomCube(), 21, 10, false));
//		System.out.println("Took " + (System.currentTimeMillis()-start)/1000. + " seconds to solve.");
//		
//		start = System.currentTimeMillis();
//		System.out.println(Search.solution(Tools.randomCube(), 21, 10, false));
//		System.out.println("Took " + (System.currentTimeMillis()-start)/1000. + " seconds to solve.");
//		
//		start = System.currentTimeMillis();
//		System.out.println(Search.solution(Tools.randomCube(), 21, 10, false));
//		System.out.println("Took " + (System.currentTimeMillis()-start)/1000. + " seconds to solve.");
		
		System.out.println("*" + new CubeScramble(2, 3));
	}
}
