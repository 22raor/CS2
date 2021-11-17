package nand;

public class Code {

	public static String dest(String in) {
		int[] a = new int[3];

		if (in != null) {
			a[0] = in.contains("A") ? 1 : 0;
			a[1] = in.contains("D") ? 1 : 0;
			a[2] = in.contains("M") ? 1 : 0;
		}
		return arrToStr(a);
	}

	public static String jump(String in) {
		int[] a = new int[3];
		if (in != null) {
			a[0] = in.matches("JLT|JNE|JLE|JMP") ? 1 : 0;
			a[1] = in.matches("JEQ|JGE|JLE|JMP") ? 1 : 0;
			a[2] = in.matches("JGT|JGE|JNE|JMP") ? 1 : 0;
		}
		return arrToStr(a);
	}

	public static String comp(String in) {
		int[] a = new int[7];
		if (in != null) {
			a[0] = in.contains("M") ? 1 : 0;
			in = in.replace("M", "A");
			a[1] = in.matches("0|1|-1|A|!A|-A|A\\+1|A-1|1\\+A") ? 1 : 0;
			a[2] = in.matches("1|-1|A|!A|-A|D\\+1|1\\+D|A\\+1|1\\+A|A-1|D-A|D\\|A|A\\|D") ? 1 : 0;
			a[3] = in.matches("0|1|-1|D|!D|-D|D\\+1|1\\+D|D-1") ? 1 : 0;
			a[4] = in.matches("1|D|!D|-D|D\\+1|1\\+D|A\\+1|1\\+A|D-1|A-D|D\\|A|A\\|D") ? 1 : 0;
			a[5] = !in.matches("D|A|!D|!A|D&A|A&D|D\\|A|A\\|D") ? 1 : 0;
			a[6] = !in.matches("0|-1|D|A|D-1|A-1|D\\+A|A\\+D|D&A|A&D") ? 1 : 0;
		}
		return arrToStr(a);
	}

	public static String arrToStr(int[] a) {
		String i = "";
		for (int j : a) {
			i += j;
		}
		return i;
	}

	public static String binary(String a) {
		int b = Integer.parseInt(a);
		a = Integer.toBinaryString(b);
		a = "0".repeat(15 - a.length()) + a;
		return a;
	}

}
