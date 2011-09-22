package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class Utils {
	private static File root;
	public static File getRootDirectory() {
		if (root == null) {
			try {
				root = new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
				if(root.isFile())
					root = root.getParentFile();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return root;
	}

	//needed because java's modulo is weird with negative values
	//assumes m > 0
	public static int modulo(int x, int m) {
		int y = x % m;
		if(y >= 0) return y;
		return y + m;
	}
	
	public static int max(int... a) {
		int max = a[0];
		for(int i : a)
			if(i > max)
				max = i;
		return max;
	}
	
	public static<P> void swap(List<P> a, int i, int j) {
		P temp = a.get(i);
		a.set(i, a.get(j));
		a.set(j, temp);
	}
	
	@SuppressWarnings("unchecked")
	public static<H> H[] copyOf(H[] arr, int len) {
		Class<?> type = arr.getClass().getComponentType();
		H[] copy = (H[]) Array.newInstance(type, len);
		System.arraycopy(arr, 0, copy, 0, len);
		return copy;
	}
	
	public static String join(String join, int[] numbers) {
		if (numbers.length == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (int n : numbers) {
			builder.append(n);
			builder.append(join);
		}
		int end = builder.length();
		return builder.delete(end - join.length(), end).toString();
	}
	public static String join(String join, Object[] elements) {
		if (elements.length == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (Object element : elements) {
			builder.append(element);
			builder.append(join);
		}
		int end = builder.length();
		return builder.delete(end - join.length(), end).toString();
	}
	public static<H> H moduloAcces(H[] arr, int i) {
		return arr[modulo(i, arr.length)];
	}
	public static int indexOf(Object o, Object[] arr) {
		for(int i=0; arr != null && i<arr.length; i++)
			if(arr[i] == o)
				return i;
		return -1;
	}
	public static <H> int indexOfEquals(H h, H[] arr) {
		for(int i = 0; i < arr.length; i++)
			if(arr[i].equals(h))
				return i;
		return -1;
	}
	
	public static void reverse(int[] arr) {
		for(int left=0, right=arr.length-1; left<right; left++, right--) {
			// exchange the first and last
			int temp = arr[left]; arr[left] = arr[right]; arr[right] = temp;
		}
	}
	
	public static void reverse(Object[] arr) {
		for(int left=0, right=arr.length-1; left<right; left++, right--) {
			// exchange the first and last
			Object temp = arr[left]; arr[left] = arr[right]; arr[right] = temp;
		}
	}
	
	private static final Random r = new Random();
	public static<H> H choose(List<H> arr) {
		return arr.get(r.nextInt(arr.size()));
	}

	public static JPanel sideBySide(JComponent... cs) {
		return sideBySide(false, cs);
	}
	public static JPanel sideBySide(boolean resize, boolean vertical, JComponent... cs) {
		JPanel p = new JPanel();
		if(resize) {
			if(vertical)
				p.setLayout(new GridLayout(0, 1));
			else
				p.setLayout(new GridLayout(1, 0));
		} else {
			if(vertical)
				p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
			else
				p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		}
		p.setBackground(Color.WHITE);
		for(JComponent c : cs)
			if (c != null)
				p.add(c);
		return p;
	}
	public static JPanel sideBySide(boolean resize, JComponent... cs) {
		return sideBySide(resize, false, cs);
	}
	
	public static String colorToString(Color c) {
		if(c == null)
			return "";
		return padWith0s(Integer.toHexString(c.getRGB() & 0xffffff));
	}
	private static String padWith0s(String s) {
		int pad = 6 - s.length();
		if(pad > 0) {
			for(int i = 0; i < pad; i++)
				s = "0" + s;
		}
		return s;
	}
	public static Color stringToColor(String s, boolean nullIfInvalid) {
		try {
			if(s.startsWith("#"))
				s = s.substring(1);
			return new Color(Integer.parseInt(s, 16));
		} catch(Exception e) {
			return nullIfInvalid ? null : Color.WHITE;
		}
	}
	
	public static boolean parseBoolean(String val, boolean def) {
		if(val == null)
			return def;
		if(val.equalsIgnoreCase("true"))
			return true;
		else if(val.equalsIgnoreCase("false"))
			return false;
		return def;
	}
	
}
