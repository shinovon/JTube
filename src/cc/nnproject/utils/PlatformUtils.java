package cc.nnproject.utils;

public class PlatformUtils {

	public static final long S40_MEM = 2048 * 1024;
	
	public static final String platform = System.getProperty("microedition.platform");
	public static final long startMemory = Runtime.getRuntime().totalMemory();
	public static final String os = System.getProperty("os.name");
	
	private static final String[] ashaFullTouchModels = new String[] { "230", "305", "306", "308", "309", "310", "311", "500", "501", "502", "503" };
	private static final String[] ashaTouchAndTypeModels = new String[] { "202", "203", "300", "303" };
	
	public static final boolean isKemulator;
	
	public static int width;
	public static int height;
	
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
		return !isNotS60() && (isS60PlatformVersion("5"));
	}
	
	public static boolean isSymbian3() {
		return !isNotS60() && (isS60PlatformVersion("5.1") || isS60PlatformVersion("5.2") || isS60PlatformVersion("5.3") || isS60PlatformVersion("5.4") || isS60PlatformVersion("5.5"));
	}
	
	public static boolean isSymbian94() {
		return !isNotS60() && isS60PlatformVersion("5.0");
	}
	
	public static boolean isS603rd() {
		String s = platform.substring(5);
		return isS60PlatformVersion("3") || (platform.startsWith("Nokia") && 
				(s.startsWith("N73") || s.startsWith("N95") || s.startsWith("E90") || 
				s.startsWith("N93") || s.startsWith("N82") || s.startsWith("E71") || 
				s.startsWith("E70") || s.startsWith("N80") || s.startsWith("E63") || 
				s.startsWith("E66") || s.startsWith("E51") || s.startsWith("E50") || 
				s.startsWith("E65") || s.startsWith("E61") || s.startsWith("E60")));
	}

	public static boolean isS40() {
		return isNotS60() && platform.startsWith("Nokia") && startMemory == S40_MEM;
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

}
