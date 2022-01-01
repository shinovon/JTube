/*
Copyright (c) 2022 Arman Jussupgaliyev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package cc.nnproject.utils;

public class PlatformUtils {

	public static final long S40_MEM = 2048 * 1024;
	public static final long ASHA_MEM = 2621424;
	
	public static final String platform = System.getProperty("microedition.platform");
	public static final long startMemory = Runtime.getRuntime().totalMemory();
	public static final String os = System.getProperty("os.name");
	public static final String vendor = System.getProperty("java.vendor");
	
	private static final String[] ashaFullTouchModels = new String[] { "230", "305", "306", "308", "309", "310", "311", "500", "501", "502", "503" };
	private static final String[] ashaTouchAndTypeModels = new String[] { "202", "203", "300", "303" };
	private static final String[] ashaTypeModels = new String[] { "200", "201", "205", "210", "302" };
	
	public static final boolean isKemulator;
	
	public static int width;
	public static int height;
	
	private static int isS603rd;
	
	static {
		boolean b = false;
		try {
			Class.forName("emulator.custom.CustomMethod");
			b = true;
			
		} catch (ClassNotFoundException e) {
		}
		isKemulator = b;
	}
	
	private static boolean isS60PlatformVersion(String v) {
		return platform.indexOf("platform_version=" + v) != -1;
	}
	
	public static boolean isNotS60() {
		return platform.indexOf("S60") == -1;
	}
	
	public static boolean isSymbianTouch() {
		return !isNotS60() && isS60PlatformVersion("5");
	}
	
	public static boolean isSymbian3Based() {
		return isSymbianTouch() && !isS60PlatformVersion("5.0");
	}
	
	public static boolean isSymbian94() {
		return !isNotS60() && isS60PlatformVersion("5.0");
	}

	public static boolean isSymbianAnna() {
		return !isNotS60() && isS60PlatformVersion("5.2");
	}
	
	public static boolean isSymbian93() {
		return isS60PlatformVersion("3.2");
	}
	
	public static boolean isS603rd() {
		if(isS603rd != -1) return isS603rd == 1;
		String s = platform.substring(5);
		boolean b = isS60PlatformVersion("3") || (platform.startsWith("Nokia") && 
				(s.startsWith("N73") || s.startsWith("N95") || s.startsWith("E90") || 
				s.startsWith("N93") || s.startsWith("N82") || s.startsWith("E71") || 
				s.startsWith("E70") || s.startsWith("N80") || s.startsWith("E63") || 
				s.startsWith("E66") || s.startsWith("E51") || s.startsWith("E50") || 
				s.startsWith("E65") || s.startsWith("E61") || s.startsWith("E60") ||
				s.startsWith("N91") || s.startsWith("E62") || s.startsWith("N78") ||
				s.startsWith("3250") || s.startsWith("N71") || s.startsWith("N75") ||
				s.startsWith("N77") || s.startsWith("N92") || s.startsWith("5500") ||
				s.startsWith("5700") ||s.startsWith("6110") ||s.startsWith("612") ||
				s.startsWith("6290") ||s.startsWith("N76") ||s.startsWith("N81")
				));
		isS603rd = b ? 1 : 0;
		return b;
	}

	public static boolean isS40() {
		return isNotS60() && platform.startsWith("Nokia") && startMemory == S40_MEM;
	}
	
	public static boolean isBada() {
		String s2;
		return platform.startsWith("SAMSUNG-GT-") &&
				((s2 = platform.substring("SAMSUNG-GT-".length())).startsWith("538")
				|| s2.startsWith("85") || s2.startsWith("72") || s2.startsWith("525")
				|| s2.startsWith("533") || s2.startsWith("57")|| s2.startsWith("86"));
	}
	
	public static boolean isAsha() {
		if(!isNotS60() || !platform.startsWith("Nokia")) return false;
		String s = platform.substring(5);
		if(!(s.length() == 3 || s.charAt(3) == '/' || s.charAt(3) == '(' || s.charAt(3) == ' ')) return false;

		for(int i = 0; i < ashaTypeModels.length; i++) {
			if(s.startsWith(ashaTypeModels[i])) return true;
		}
		for(int i = 0; i < ashaFullTouchModels.length; i++) {
			if(s.startsWith(ashaFullTouchModels[i])) return true;
		}
		for(int i = 0; i < ashaTouchAndTypeModels.length; i++) {
			if(s.startsWith(ashaTouchAndTypeModels[i])) return true;
		}
		return false;
	}
	
	public static boolean isAshaFullTouch() {
		if(!isNotS60() || !platform.startsWith("Nokia")) return false;
		String s = platform.substring(5);
		if(!(s.length() == 3 || s.charAt(3) == '/' || s.charAt(3) == '(' || s.charAt(3) == ' ')) return false;
		for(int i = 0; i < ashaFullTouchModels.length; i++) {
			if(s.startsWith(ashaFullTouchModels[i])) return true;
		}
		return false;
	}
	
	public static boolean isAshaTouchAndType() {
		if(!isNotS60() || !platform.startsWith("Nokia")) return false;
		String s = platform.substring(5);
		if(!(s.length() == 3 || s.charAt(3) == '/' || s.charAt(3) == '(' || s.charAt(3) == ' ')) return false;
		for(int i = 0; i < ashaTouchAndTypeModels.length; i++) {
			if(s.startsWith(ashaTouchAndTypeModels[i])) return true;
		}
		return false;
	}
	
	public static boolean supportsTouch() {
		return isSymbianTouch() || isAshaFullTouch() || isAshaTouchAndType();
	}

	public static boolean isJ2ML() {
		return os != null && os.equals("Linux") && vendor != null && vendor.equals("The Android Project");
	}

}
