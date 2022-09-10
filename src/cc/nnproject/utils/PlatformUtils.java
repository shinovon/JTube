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

	public static final long _2MB = 2048 * 1024;
	public static final long _1MB = 1024 * 1024;
	public static final long ASHA_MEM = 2621424;
	
	public static final String platform = System.getProperty("microedition.platform");
	public static final long startMemory = Runtime.getRuntime().totalMemory();
	public static final String os = System.getProperty("os.name");
	public static final String vendor = System.getProperty("java.vendor");
	public static final String version = System.getProperty("java.version");
	
	private static final String[] ashaFullTouchModels = new String[] { "230", "305", "306", "308", "309", "310", "311", "500", "501", "502", "503" };
	private static final String[] ashaTouchAndTypeModels = new String[] { "202", "203", "300", "303" };
	private static final String[] ashaTypeModels = new String[] { "200", "201", "205", "210", "302" };
	
	public static final boolean isKemulator;
	
	public static int width;
	public static int height;
	
	// Cached values
	private static int isS603rd;
	private static int isS40;
	private static int isAsha;
	private static int isBada;
	
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
	
	// S60 3.2 or higher check
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

	public static boolean isBelle() {
		int i = platform.indexOf("version=5.") + "version=5.".length();
		return !isNotS60() && isS60PlatformVersion("5") && Integer.parseInt(platform.substring(i, i+1)) >= 3;
	}
	
	public static boolean isSymbian93() {
		return isS60PlatformVersion("3.2");
	}
	
	public static boolean isS603rd() {
		if(isS603rd == 0) {
			String s;
			boolean b = false;
			try {
				b = (isSymbian9() && !isSymbianTouch()) || isS60PlatformVersion("3") || 
					(platform.startsWith("Nokia") &&
					(s = platform.substring(5)).length() > 0 &&
					//9.1 & 9.2 nokia models
					(s.startsWith("N73")|| s.startsWith("N95") || s.startsWith("E90") || 
					s.startsWith("N93") || s.startsWith("N82") || s.startsWith("E71") || 
					s.startsWith("E70") || s.startsWith("N80") || s.startsWith("E63") || 
					s.startsWith("E66") || s.startsWith("E51") || s.startsWith("E50") || 
					s.startsWith("E65") || s.startsWith("E61") || s.startsWith("E60") ||
					s.startsWith("N91") || s.startsWith("E62") || s.startsWith("N78") ||
					s.startsWith("3250")|| s.startsWith("N71") || s.startsWith("N75") ||
					s.startsWith("N77") || s.startsWith("N92") || s.startsWith("5500")||
					s.startsWith("5700")|| s.startsWith("6110")|| s.startsWith("612") ||
					s.startsWith("6290")|| s.startsWith("N76") || s.startsWith("N81")
					));
			} catch (Exception e) {
			}
			isS603rd = b ? 1 : -1;
		}
		return isS603rd == 1;
	}

	// Symbian 9.x or higher check
	public static boolean isSymbian9() {
		return platform.indexOf("platform=S60") != -1 ||
				System.getProperty("com.symbian.midp.serversocket.support") != null ||
				System.getProperty("com.symbian.default.to.suite.icon") != null;
	}

	public static boolean isS40() {
		if(isS40 == 0) {
			boolean b = false;
			if(!isSymbian9() || !platform.startsWith("Nokia")) {
				int i;
				if((i = platform.indexOf('.')) != -1 && i != platform.length() - 1 && platform.indexOf('.', i+1) == -1) {
					String p = System.getProperty("fileconn.dir.private");
					if(p != null) {
						if(p.indexOf("/private/") == -1) {
							b = false;
						} else {
							b = true;
						}
					} else {
						b = true;
					}
				}
			}
			isS40 = b ? -1 : 1;
			return b;
		}
		return isS40 == 1;
	}
	
	public static boolean isBada() {
		if(isBada == 0) {
			String s2;
			boolean b = false;
			if(platform.startsWith("SAMSUNG-GT-")) {
				s2 = platform.substring("SAMSUNG-GT-".length());
				b = s2.startsWith("538")
								|| s2.startsWith("85") || s2.startsWith("72") || s2.startsWith("525")
								|| s2.startsWith("533") || s2.startsWith("57")|| s2.startsWith("86");
			}
			isBada = b ? 1 : -1;
		}
		return isBada == 1;
	}
	
	public static boolean isAsha() {
		if(isAsha == 0) {
			boolean b = false;
			l: {
				if(!isNotS60() || !platform.startsWith("Nokia")) break l;
				String s = platform.substring(5);
				if(s.length() < 3 || !(s.length() == 3 || s.charAt(3) == '/' || s.charAt(3) == '(' || s.charAt(3) == ' ')) break l;
		
				for(int i = 0; i < ashaTypeModels.length; i++) {
					if(s.startsWith(ashaTypeModels[i])) {
						b = true;
						break l;
					}
				}
				for(int i = 0; i < ashaFullTouchModels.length; i++) {
					if(s.startsWith(ashaFullTouchModels[i])) {
						b = true;
						break l;
					}
				}
				for(int i = 0; i < ashaTouchAndTypeModels.length; i++) {
					if(s.startsWith(ashaTouchAndTypeModels[i])) {
						b = true;
						break l;
					}
				}
			}
			isAsha = b ? 1 : -1;
		}
		return isAsha == 1;
	}
	
	public static boolean isAshaFullTouch() {
		if(!isAsha()) return false;
		String s = platform.substring(5);
		for(int i = 0; i < ashaFullTouchModels.length; i++) {
			if(s.startsWith(ashaFullTouchModels[i])) return true;
		}
		return false;
	}
	
	public static boolean isAshaTouchAndType() {
		if(!isAsha()) return false;
		String s = platform.substring(5);
		for(int i = 0; i < ashaTouchAndTypeModels.length; i++) {
			if(s.startsWith(ashaTouchAndTypeModels[i])) return true;
		}
		return false;
	}
	
	public static boolean isAshaNoTouch() {
		if(!isAsha()) return false;
		String s = platform.substring(5);
		for(int i = 0; i < ashaTypeModels.length; i++) {
			if(s.startsWith(ashaTypeModels[i])) return true;
		}
		return false;
	}

	// J2ME Loader check
	public static boolean isJ2ML() {
		return os != null && os.equals("Linux") && vendor != null && vendor.equals("The Android Project");
	}

	public static boolean isSamsung() {
		return platform != null && platform.toLowerCase().startsWith("samsung");
	}

	public static boolean isPhoneme() {
		return PlatformUtils.version != null && PlatformUtils.version.indexOf("phoneme") != -1;
	}

	public static boolean isS30() {
		return platform != null && platform.indexOf("Series30") != -1;
	}

	public static boolean isNokia() {
		return platform != null && platform.startsWith("Nokia");
	}

	public static boolean isSonyEricsson() {
		return System.getProperty("com.sonyericsson.java.platform") != null || (platform != null && platform.toLowerCase().startsWith("sonyericsson"));
	}

	public static boolean isWTK() {
		return platform != null && (platform.startsWith("wtk") || platform.endsWith("wtk"));
	}

	public static boolean isJ2ME() {
		return "j2me".equals(platform);
	}

}
