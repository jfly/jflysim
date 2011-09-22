package edu.berkeley.gcweb.gui.gamescubeman.PuzzleUtils;

import java.applet.Applet;
import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;

import netscape.javascript.JSObject;

public class AppletSettings {
	
	private final HashMap<String, String> urlArguments = new HashMap<String, String>();;
	private final Applet applet;
	private JSObject document;
	public AppletSettings(Applet applet, JSObject jso) {
		this.applet = applet;
		
		if(jso != null) {
			document = (JSObject) jso.getMember("document");

			//this will read the arguments passed via the url
			String argString = ((String) ((JSObject) jso.getMember("location")).getMember("search"));
			if(argString.length() > 0) {
				argString = argString.substring(1);
				for(String param : argString.split("&")) {
					String[] key_val = param.split("=");
					if(key_val.length == 2) {
						urlArguments.put(unescape(key_val[0]), unescape(key_val[1]));
					} else {
						System.err.println("Expected key=value not found in " + param);
					}
				}
			}
		}
	}
	
	private String expires = null, category = null; //this allows duplicate keys under different categories
	private HashMap<String, String> cookieCache = null; 
	public void loadCookies(String cat) {
		if(document == null) return; //can't access cookie for some reason
		this.category = cat + "_";

		//set the cookie to expire in 1 year
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, 1);
		expires = "expires=" + c.getTime().toString() + ";";
		
		cookieCache = new HashMap<String, String>();
		//parse all the unexpired cookies
		for(String keyVal : ((String) document.getMember("cookie")).split(" *; *")) {
			String[] key_val = keyVal.split("=");
			if(key_val.length == 2)
				cookieCache.put(unescape(key_val[0]), unescape(key_val[1]));
		}
	}

	private static String escape(String s) {
		try {
			return URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
//		StringBuffer sb = new StringBuffer();
//		for(int i=0; i<s.length(); i++) {
//			char c = s.charAt(i);
//			if(c == '\\')
//				sb.append("\\\\");
//			else if(c == '=')
//				sb.append("\\e");
//			else if(c == ';')
//				sb.append("\\s");
//			else
//				sb.append(c);
//		}
//		return sb.toString();
	}
	private static String unescape(String s) {
		try {
			return URLDecoder.decode(s, "utf-8");
		} catch(Exception e) {
			return null;
		}
//		if(s == null) return null;
//		StringBuffer sb = new StringBuffer();
//		for(int i=0; i<s.length(); i++) {
//			char c = s.charAt(i);
//			if(c == '\\') {
//				if(i == s.length())
//					return null;
//				c = s.charAt(++i);
//				if(c == 'e') c = '=';
//				else if(c == 's') c = ';';
//				sb.append(c);
//			} else
//				sb.append(c);
//		}
//		return sb.toString();
	}
	
	public String get(String key, String def) {
		String val = urlArguments.get(key); //url arguments take precedence
		if(val == null) { //if the key wasn't specified in the url, try our cookies
			if(cookieCache != null) {
				val = cookieCache.get(category + key);
			}
			if(val == null) { //lastly, if the cookie was undefined, try the applet parameters
				try {
					val = applet.getParameter(key);
				} catch(NullPointerException e) {} //this indicates that we're not running as an applet
			}
		}
		return val == null ? def : val;
	}
	public void set(String key, String value) {
		if(cookieCache == null) return; //can't set anything unless we're using cookies!
		cookieCache.put(category + key, value);
		if(document != null) {
			document.setMember("cookie", escape(category + key) + "=" + escape(value) + ";" + expires);
		}
	}
	
	public void setColor(String key, Color val) {
		set(key, Utils.colorToString(val));
	}
	public Color getColor(String key, Color def) {
		String val = get(key, null);
		if(val == null)
			return def;
		try {
			return Color.decode(val);
		} catch(NumberFormatException e) {
			return def;
		}
	}
	
	public Boolean getBoolean(String key, boolean def) {
		return Utils.parseBoolean(get(key, null), def);
	}
}
