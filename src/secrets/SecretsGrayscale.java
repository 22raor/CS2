package secrets;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SecretsGrayscale {

	static String toEncode = "flower.png";
	static String encodedImage = "shoe.jpg";
	static String output = "encode.png";

	static String toDecode = output;

	static BufferedImage yeet;

	public static void main(String... args) throws Exception {

		decodeImage();

	}

	public static void encodeImage() {
		BufferedImage img = getImage(toEncode);
		BufferedImage bwImg = toGrayscale(getImage(encodedImage));

		BufferedImage modified = copyImage(img);

		// scale bwImg to img
		int wid = img.getWidth();
		int height = img.getHeight();

		int newHeight = (int) ((double) wid / bwImg.getWidth() * (double) bwImg.getHeight());

		if (newHeight > height) {
			newHeight = height;
		}

		bwImg = toBufferedImage(bwImg.getScaledInstance(wid, newHeight, Image.SCALE_SMOOTH));

		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {

				int a = img.getRGB(i, j);

				modified.setRGB(i, j, LSBoff(a));
			}
		}

		for (int i = 0; i < bwImg.getWidth(); i++) {
			for (int j = 0; j < bwImg.getHeight(); j++) {

				int a = bwImg.getRGB(i, j);
				
					modified.setRGB(i, j, LSBon(bwImg.getRGB(i,j), modified.getRGB(i, j)));
				

			}
		}

		saveImage(output, modified);

		display(img, modified);

		yeet = modified;

	}

	public static int LSBon(int reference, int rgb) {
		
		//set last two bits based on reference
		
		Color a = new Color(rgb);
		int b = a.getRed();
		if (b % 2 == 0) {
			b += b == 0 ? 1 : -1;
		}
		return new Color(b, a.getGreen(), a.getBlue()).getRGB();

	}

	public static int LSBoff(int rgb) {
		
		//set 2 bits to off for everything
		
		Color a = new Color(rgb);
		int b = a.getRed();
		if (b % 2 == 1) {
			b += b == 0 ? 1 : -1;
		}
		return new Color(b, a.getGreen(), a.getBlue()).getRGB();
	}

	public static int getGray(int rgb) {
	//return the gray type based on last 2 btis
		Color a = new Color(rgb);
		int b = a.getRed();
		return 1;
	}

	public static void decodeImage() {
		BufferedImage forDecode = getImage(toDecode);

		BufferedImage b = new BufferedImage(forDecode.getWidth(), forDecode.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < b.getWidth(); i++) {
			for (int j = 0; j < b.getHeight(); j++) {

				int a = forDecode.getRGB(i, j);

				

					b.setRGB(i, j, getGray(a));


			}
		}

		display(forDecode, b);

	}

	public static boolean isBlack(int a) {

		Color b = new Color(a);
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
				a = LSBoff(a);

				modified.setRGB(i, j, a);

			}
		}

		File outputfile = new File("image.png");
		try {
			ImageIO.write(modified, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		display(img, modified);

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
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
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
		}
	}

	public static void display(BufferedImage... img) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());

		for (BufferedImage i : img) {
			frame.getContentPane().add(new JLabel(new ImageIcon(i)));
		}

		frame.pack();
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
	
	public static BufferedImage toGrayscale(BufferedImage colorImage) {
		BufferedImage image = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(),  
			    BufferedImage.TYPE_BYTE_GRAY);  
			Graphics g = image.getGraphics();  
			g.drawImage(colorImage, 0, 0, null);  
			g.dispose();
			return image;  		
	}
	
}
