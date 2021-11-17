package util;

public class Rep16 {

	public static void main(String[] args) {
		String a = "Bit(in=in[~],load=load,out=out[~]);";
		String out = "";
		for(int i = 0; i < 16; i++) {
			String b = a.replace("~", i+"").replace("*", (i+1)+"");
			out+=b+"\n";
		}
		System.out.println(out.trim());

	}

}
