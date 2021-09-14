package fractals;

import java.util.ArrayList;

public class DragonFractal {

	static int length = 2;
	static int iter = 15;
	
	public static void main(String[] args) {

	drawCurve(generateMoves(iter));		

		//System.out.println(generateMoves(iter));
	}

	public static void drawCurve(String moves) {
		Turtle t = new Turtle(true, 1200,700);
		
			for (char z : moves.toCharArray()) {
				t.paint((z == '1' ? 1 : -1) * 90, length);
			
			}
		
	}

	public static String generateMoves(int iter) {
		ArrayList<String> moves = new ArrayList<>();

		for (int i = 0; i < iter; i++) {
			if (i == 0) {
				moves.add("1");
			} else {
				String a = moves.get(i - 1) + "1" + swapMid(moves.get(i - 1));
				moves.add(a);
			}

		}
		return moves.get(moves.size() - 1);
	}

	private static String swapMid(String string) {

		char[] chars = string.toCharArray();
		char b = chars[(string.length() - 1) / 2];
		chars[(string.length() - 1) / 2] = b == '1' ? '0' : '1';
		return new String(chars);

	}

}
