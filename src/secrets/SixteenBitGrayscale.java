package secrets;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SixteenBitGrayscale {

	public static void main(String...args) {
		
		String flower = "flower.png";
		BufferedImage flo = getImage(flower);
		BufferedImage gray = toGrayscale(flo);
		display(flo,gray);
	
	}
	
	
	public static BufferedImage toGrayscale(BufferedImage colorImage) {
		BufferedImage image = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(),  
			    BufferedImage.TYPE_BYTE_GRAY);  
			Graphics g = image.getGraphics();  
			g.drawImage(colorImage, 0, 0, null);  
			g.dispose();
			return image;  		
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
}