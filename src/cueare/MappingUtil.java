package cueare;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MappingUtil {

	public String nums = "0123456789";
	public String abc = "abcdefghijklmnopqrstuvwxyz";
	public String alphabet;

	public MappingUtil() {
		alphabet = nums + abc.toUpperCase() + abc;
	}

	public boolean[][] encode(String text) {

		text = text + "0".repeat(7 - text.length());

		boolean[][] spec = new boolean[7][7];

		int sum = 0;

		int[] regIndex = { 0, 2, 3, 4, 6 };

		for (int i : regIndex) {
			boolean[] bin = charToBinary(text.charAt(i));

			for (int j = 0; j < 6; j++) {
				spec[j][i] = bin[j];
				sum += bin[j] ? 1 : 0;
			}

		}

		for (int i = 1; i < 6; i += 4) {
			boolean[] bin = charToBinary(text.charAt(i));
			int ind = 0;
			for (int j : regIndex) {
				spec[j][i] = bin[ind];
				sum += bin[ind] ? 1 : 0;
				ind++;
			}
		}

		spec[6][0] = charToBinary(text.charAt(1))[5];
		spec[6][6] = charToBinary(text.charAt(5))[5];

		sum += spec[6][0] ? 1 : 0;
		sum += spec[6][6] ? 1 : 0;

		// Orientation
		spec[1][5] = true;
		spec[5][1] = true;
		spec[5][5] = true;

		String ee = Integer.toBinaryString(sum % 8);
		ee = "0".repeat(3 - ee.length()) + ee;
		// System.out.println(sum + " "+ sum%8 + " " + ee);

		for (int i = 2; i < 5; i++) {
			spec[6][i] = ee.charAt(i - 2) == '1';
		}

		return spec;
	}

	public String decode(boolean[][] spec) throws Exception {
		int[] regIndex = new int[] { 0, 2, 3, 4, 6 };

		ArrayList<boolean[]> chars = new ArrayList<>();

		int sum = 0;

		for (int i : regIndex) {
			boolean[] bin = new boolean[6];

			for (int j = 0; j < 6; j++) {
				bin[j] = spec[j][i];
				sum += spec[j][i] ? 1 : 0;
			}
			chars.add(bin);

		}

		for (int i = 1; i < 6; i += 4) {
			boolean[] bin = new boolean[6];
			int ind = 0;
			for (int j : regIndex) {
				bin[ind] = spec[j][i];
				sum += spec[j][i] ? 1 : 0;
				ind++;
			}
			bin[5] = i == 2 ? spec[6][0] : spec[6][6];
			sum += (spec[6][0] ? 1 : 0) + (spec[6][6] ? 1 : 0);
			chars.add(i, bin);
		}

		sum = sum % 8;
		int correctSum = Integer
				.parseInt("" + (spec[6][2] ? '1' : '0') + (spec[6][3] ? '1' : '0') + (spec[6][4] ? '1' : '0'), 2);

		// System.out.println(sum + " " + correctSum);

		if (correctSum != sum) {
			throw new Exception("Checksum invalid");
		}

		String output = "";
		for (boolean[] c : chars) {
			output += binToText(c);
		}
		return output;

	}

	private String binToText(boolean[] c) {
		String j = "";
		for (boolean a : c) {
			j += a ? 1 : 0;
		}

		int i = Integer.parseInt(j, 2);
		return alphabet.charAt(i) + "";
	}

	public boolean[] charToBinary(char a) {
		boolean[] out = new boolean[6];

		int num = alphabet.indexOf(a);
		String bin = Integer.toBinaryString(num);
		bin = "0".repeat(6 - bin.length()) + bin;

		for (int i = 0; i < 6; i++) {
			out[i] = bin.charAt(i) == '1' ? true : false;
		}
		return out;
	}

	public boolean[][] orient(boolean[][] a) {

		boolean[] orienters = { a[1][1], a[1][5], a[5][1], a[5][5] };

		int which = 0;
		int count = 0;
		for (int i = 0; i < orienters.length; i++) {
			if (!orienters[i]) {
				which = i;
			} else {
				count++;
			}
		}

		assert count == 3 : "Orientation markers are invalid";
	
		int[] rots = { 0, 270, 90, 180 };
		if (which == 0) {
			return a;
		} else {
			return rotate(a, rots[which]);
		}

	}

	public boolean[][] rotate(boolean[][] mat, int i) {
		final int M = mat.length;

		boolean[][] ret = null;
		for (int j = 0; j < i / 90; j++) {
			if (j > 0) {
				mat = ret;
			}
			ret = new boolean[M][M];

			for (int r = 0; r < M; r++) {
				for (int c = 0; c < M; c++) {
					ret[c][M - 1 - r] = mat[r][c];
				}
			}

		}

		return ret;
	}

	public BufferedImage displayJR(boolean[][] spec) {
		BufferedImage img = new BufferedImage(630, 630, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.fillRect(20, 20, 590, 590);

		for (int i = 0; i < spec.length; i++) {
			for (int j = 0; j < spec[0].length; j++) {
				g.setColor(spec[j][i] ? Color.black : Color.white);
				g.fillRect(i * 80 + 35, j * 80 + 35, 80, 80);
			}
		}
		display(img);
		return img;
	}

	public void display(BufferedImage... img) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());

		for (BufferedImage i : img) {
			frame.getContentPane().add(new JLabel(new ImageIcon(i)));
		}

		frame.pack();
		// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public void print(BufferedImage img) {
		
	}
	
}