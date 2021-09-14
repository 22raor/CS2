package secrets;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Secrets {

	static String toEncode = "flower.png";
	static String encodedImage = "bw.jpg";
	static String encodedImage2 = "bw1.png";
	static String encodedImage3 = "bw2.jpg";
	
	
	
	
	static String output = "encode.png";

	static String toDecode = output;

	static BufferedImage yeet;

	static String textToEncode = "hi my name is rishi";

	public static void main(String... args) throws Exception {
		textToEncode = textToEncode.repeat(40);
	//encodeTextImage();
	//	 decodeTextFromImage();
		//encodeThreeImageImage();
		//decodeImage();

	}

	public static void encodeImageImage() {
		BufferedImage img = getImage(toEncode);
		BufferedImage bwImg = getImage(encodedImage);

		BufferedImage modified = copyImage(img);

		// scale bwImg to img
		int wid = img.getWidth();
		int height = img.getHeight();

		int newHeight = (int) ((double) wid / bwImg.getWidth() * (double) bwImg.getHeight());
		int newWid = (newHeight/bwImg.getHeight()) * wid;
		
		
		if (newHeight > height) {
			newHeight = height;
		}

		bwImg = toBufferedImage(bwImg.getScaledInstance(newWid, newHeight, Image.SCALE_SMOOTH));

		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {

				int a = img.getRGB(i, j);

				modified.setRGB(i, j, LSBoffAll(a));
			}
		}

		for (int i = 0; i < bwImg.getWidth(); i++) {
			for (int j = 0; j < bwImg.getHeight(); j++) {

				int a = bwImg.getRGB(i, j);
				if (isBlack(a)) {
					modified.setRGB(i, j, LSBon(modified.getRGB(i, j), LSB.RED));
				}

			}
		}

		saveImage(output, modified);

		display(img, modified);

		yeet = modified;

	}

	public static void encodeThreeImageImage() {
		BufferedImage img = getImage(toEncode);
		BufferedImage bwImg = getImage(encodedImage);
		BufferedImage bwImg2 = getImage(encodedImage2);
		BufferedImage bwImg3 = getImage(encodedImage3);
		
		
		BufferedImage modified = copyImage(img);

		// scale bwImg to img
		int wid = img.getWidth();
		int height = img.getHeight();

		//double newHeight = (int) ((double) wid / bwImg.getWidth() * (double) bwImg.getHeight());
		double newWid =wid;
		double newHeight = (newWid/bwImg.getWidth()) * height;
		
		
		if (newHeight > height) {
			newHeight = height;
		}

		bwImg = toBufferedImage(bwImg.getScaledInstance((int)newWid, (int)newHeight, Image.SCALE_SMOOTH));
		

		newWid =wid;
	 newHeight = (newWid/bwImg2.getWidth()) * height;
		
		
		if (newHeight > height) {
			newHeight = height;
		}

		bwImg2 = toBufferedImage(bwImg2.getScaledInstance((int)newWid,(int) newHeight, Image.SCALE_SMOOTH));
		
		
		newWid =wid;
		 newHeight = (newWid/bwImg3.getWidth()) * height;
		
		
		if (newHeight > height) {
			newHeight = height;
		}

		bwImg3 = toBufferedImage(bwImg3.getScaledInstance((int)newWid, (int)newHeight, Image.SCALE_SMOOTH));
		
		
		
		
		
		
		

		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {

				int a = img.getRGB(i, j);

				modified.setRGB(i, j, LSBoffAll(a));
			}
		}

		for (int i = 0; i < bwImg.getWidth(); i++) {
			for (int j = 0; j < bwImg.getHeight(); j++) {

				int a = bwImg.getRGB(i, j);
				if (isBlack(a)) {
					modified.setRGB(i, j, LSBon(modified.getRGB(i, j), LSB.RED));
				}

			}
		}
		
		for (int i = 0; i < bwImg2.getWidth(); i++) {
			for (int j = 0; j < bwImg2.getHeight(); j++) {

				int a = bwImg2.getRGB(i, j);
				if (isBlack(a)) {
					modified.setRGB(i, j, LSBon(modified.getRGB(i, j), LSB.BLUE));
				}

			}
		}
		
		
		for (int i = 0; i < bwImg3.getWidth(); i++) {
			for (int j = 0; j < bwImg3.getHeight(); j++) {

				int a = bwImg3.getRGB(i, j);
				if (isBlack(a)) {
					modified.setRGB(i, j, LSBon(modified.getRGB(i, j), LSB.GREEN));
				}

			}
		}
		
		
		
		
		
		
		

		saveImage(output, modified);

		display(img, modified);

		yeet = modified;

	}

	
	
	public static void encodeTextImage() {
		BufferedImage img = getImage(toEncode);

		BufferedImage modified = copyImage(img);

		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {

				int a = img.getRGB(i, j);

				modified.setRGB(i, j, LSBoffAll(a)); // need to lsb off all
			}
		}

		int binaryIndex = 0;
		ArrayList<String> binaryString = getBinaryInFour();

		outer: for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {

				if (binaryIndex == binaryString.size()) {
					break outer;
				}

				String a = binaryString.get(binaryIndex);

				modified.setRGB(i, j, embedFour(modified.getRGB(i, j), a));
				binaryIndex++;
			}
		}

		saveImage(output, modified);

		display(img, modified);

		yeet = modified;

	}

	private static int embedFour(int rgb, String a) {

		Color rr = new Color(rgb, true);

		int red = rr.getRed();
		int green = rr.getGreen();
		int blue = rr.getBlue();
		int alpha = rr.getAlpha();

		if (a.charAt(0) == '1') {
			red += red == 0 ? 1 : -1;
		}
		if (a.charAt(1) == '1') {
			green += green == 0 ? 1 : -1;
		}
		if (a.charAt(2) == '1') {
			blue += blue == 0 ? 1 : -1;
		}
		if (a.charAt(3) == '1') {
			alpha += alpha == 0 ? 1 : -1;
		}

		return new Color(red, green, blue, alpha).getRGB();
	}

	private static ArrayList<String> getBinaryInFour() {
		// textToEncode
		byte[] a = textToEncode.getBytes();
		String allBinary = "";

		for (byte eeee : a) {
			String eight = Integer.toBinaryString(Byte.toUnsignedInt(eeee));

			int z = 8 - eight.length();
			String aa = " ".repeat(z);

			eight = aa + eight;

			allBinary += eight;

		}
		// System.out.println(allBinary);

		ArrayList<String> byteInFour = new ArrayList<>();

		String ee = "";
		for (int i = 0; i < allBinary.length(); i++) {
			ee += allBinary.charAt(i);
			if ((i + 1) % 4 == 0) {

				byteInFour.add(ee);
				ee = "";
			}

		}

		// System.out.println(byteInFour);

		return byteInFour;
	}

	public static int LSBon(int rgb, LSB lsb) {

		Color a = new Color(rgb, true);

		int b = 0;
		switch (lsb) {
		case RED:
			b = a.getRed();
			if (b % 2 == 0) {
				b += b == 0 ? 1 : -1;

			}
			return new Color(b, a.getGreen(), a.getBlue(), a.getAlpha()).getRGB();
		case GREEN:
			b = a.getGreen();
			if (b % 2 == 0) {
				b += b == 0 ? 1 : -1;
			}
			return new Color(a.getRed(), b, a.getBlue(), a.getAlpha()).getRGB();

		case BLUE:
			b = a.getBlue();
			if (b % 2 == 0) {
				b += b == 0 ? 1 : -1;
			}
			return new Color(a.getRed(), a.getGreen(), b, a.getAlpha()).getRGB();
		case ALPHA:
			b = a.getAlpha();
			if (b % 2 == 0) {
				b += b == 0 ? 1 : -1;
			}
			return new Color(a.getRed(), a.getGreen(), a.getBlue(), b).getRGB();
		}

		return 0;

	}

	public static int LSBoffAll(int rgb) {
		Color a = new Color(rgb, true);
		int b = a.getRed();
		if (b % 2 == 1) {
			b += b == 0 ? 1 : -1;
		}

		int c = a.getGreen();
		if (c % 2 == 1) {
			c += c == 0 ? 1 : -1;
		}

		int d = a.getBlue();
		if (d % 2 == 1) {
			d += d == 0 ? 1 : -1;
		}

		int f = a.getAlpha();

		if (f % 2 == 1) {
			f += f == 0 ? 1 : -1;
		}

		return new Color(b, c, d, f).getRGB();
	}

	public static boolean isLSBon(int rgb) {
		Color a = new Color(rgb, true);
		int b = a.getRed();
		return b % 2 == 1;
	}

	
	
	public static boolean isLSBon(int rgb, LSB lsb) {
		Color a = new Color(rgb, true);
		switch(lsb) {
		case RED:
			int b = a.getRed();
			return b % 2 == 1;
		case BLUE:
			int b2 = a.getBlue();
			return b2 % 2 == 1;
		case GREEN:

			int b3 = a.getGreen();
			return b3 % 2 == 1;
		}
		
		return false; 
	}

	
	
	
	public static void decodeImage() {
		BufferedImage forDecode = getImage(toDecode);

		BufferedImage b = new BufferedImage(forDecode.getWidth(), forDecode.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < b.getWidth(); i++) {
			for (int j = 0; j < b.getHeight(); j++) {

				int a = forDecode.getRGB(i, j);

				if (isLSBon(a, LSB.RED)) {

					b.setRGB(i, j, Color.black.getRGB());

				} else {
					b.setRGB(i, j, Color.white.getRGB());
				}

			}
		}
		
		
		BufferedImage b2 = new BufferedImage(forDecode.getWidth(), forDecode.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < b2.getWidth(); i++) {
			for (int j = 0; j < b2.getHeight(); j++) {

				int a = forDecode.getRGB(i, j);

				if (isLSBon(a, LSB.BLUE)) {

					b2.setRGB(i, j, Color.black.getRGB());

				} else {
					b2.setRGB(i, j, Color.white.getRGB());
				}

			}
		}
		
		BufferedImage b3 = new BufferedImage(forDecode.getWidth(), forDecode.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < b3.getWidth(); i++) {
			for (int j = 0; j < b3.getHeight(); j++) {

				int a = forDecode.getRGB(i, j);

				if (isLSBon(a, LSB.GREEN)) {

					b3.setRGB(i, j, Color.black.getRGB());

				} else {
					b3.setRGB(i, j, Color.white.getRGB());
				}

			}
		}
		
		
		
		
		
		
		
		

		display(forDecode, b,b2,b3);

	}

	public static void decodeTextFromImage() {
		BufferedImage forDecode = getImage(toDecode);

		String output = "";

		for (int i = 0; i < forDecode.getWidth(); i++) {
			for (int j = 0; j < forDecode.getHeight(); j++) {

				int a = forDecode.getRGB(i, j);

				output += colorToBinary(a);

			}
		}

		// System.out.println(getBinaryInFour());
		// System.out.println(output.substring(0, 30));
		output = binaryToText(output);

		output = output.replaceAll("[^a-zA-Z0-9_]", "");
		System.out.println(output);
	}

	private static String binaryToText(String output) {
		String out = "";

		String temp = "";
		int index = 0;

		for (int i = 0; i < output.length(); i++) {
			temp += output.charAt(i);

			if ((index + 1) % 8 == 0) {
				// System.out.println(temp);
				int zz = Integer.parseInt(temp, 2);
				char b = (char) zz;
				out += b;
				temp = "";
			}
			index++;
		}

		return out;
	}

	private static String colorToBinary(int a) {
		String ee = "";
		Color b = new Color(a, true);

		int c = b.getRed();
		int d = b.getGreen();
		int e = b.getBlue();
		int f = b.getAlpha();

		// System.out.println(f);

		ee += (c % 2 == 1) ? 1 : 0;
		ee += (d % 2 == 1) ? 1 : 0;
		ee += (e % 2 == 1) ? 1 : 0;
		ee += (f % 2 == 1) ? 1 : 0;

		return ee;

	}

	public static boolean isBlack(int a) {

		Color b = new Color(a, true);
		if (b.getBlue() < 30 && b.getRed() < 30 && b.getGreen() < 30) {
			return true;
		}

		return false;
	}

	public static void stageFour() {
		BufferedImage img = getImage("flower.png");
		BufferedImage modified = copyImage(img);

		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {

				int a = img.getRGB(i, j);

				Color bb = new Color(a, true);
				bb = new Color(bb.getRed(), bb.getGreen(), bb.getBlue(), 90);

				modified.setRGB(i, j, bb.getRGB());

			}
		}

		File outputfile = new File("imageMod.png");
		try {
			ImageIO.write(modified, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		BufferedImage back = getImage("imageMod.png");
		back = copyImage(back);

		display(img, back);

	}

	public static void stageOne() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("flower.png").toURI().toURL());
		} catch (Exception e) {
		}

		display(img);
	}

	public static void stageThree() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("flower.png").toURI().toURL());
		} catch (Exception e) {
		}

		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j += 4) {
				img.setRGB(i, j, Color.blue.getRGB());
			}
		}
		display(img);

		File outputfile = new File("image.jpg");
		try {
			ImageIO.write(img, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage copyImage(BufferedImage source) {
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}

	public static BufferedImage getImage(String path) {
		BufferedImage a = null;
		try {
			a = ImageIO.read(new File(path));
		} catch (Exception e) {
		}
		return a;
	}

	public static void saveImage(String path, BufferedImage img) {
		try {
			ImageIO.write(img, "png", new File(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void display(BufferedImage... img) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());

		for (BufferedImage i : img) {
			frame.getContentPane().add(new JLabel(new ImageIcon(i)));
		}

		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static BufferedImage toBufferedImage(Image img) {
		// not mine
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		return bimage;
	}

	public static enum LSB {
		RED, GREEN, BLUE, ALPHA
	}

	public static void saveTester() {
		BufferedImage img = getImage(toEncode);

		BufferedImage modified = copyImage(img);

		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {

				int a = img.getRGB(i, j);

				modified.setRGB(i, j, LSBoffAll(a)); // need to lsb off all
			}
		}

		int binaryIndex = 0;
		ArrayList<String> binaryString = getBinaryInFour();

		outer: for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {

				if (binaryIndex == binaryString.size()) {
					break outer;
				}

				String a = binaryString.get(binaryIndex);

				int b = modified.getRGB(i, j);
				Color cc = new Color(b, true);
				cc = new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), 50);

				modified.setRGB(i, j, cc.getRGB());
				binaryIndex++;
			}
		}

		saveImage(output, modified);

		display(img, modified);

		yeet = modified;

	}

}
