package fractals;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.math3.complex.Complex;

public class Mandelbrot {
	public static Color[] mapping = new Color[16];
	public static Color[] ultra = new Color[5];
	static BufferedImage picture;
	static JFrame frame;
	static int n = 680;
	
	
	static double xStart = -1.5;
	static double yStart = -1;
	static double scale = 2.0;
	static double transformX = 0;
	static double transformY = 0;
	
	static List<Color> map;
	
	public static void main(String[] args) {
		System.out.println("Computing...");

		
		
		mapping[0] = new Color(66, 30, 15);
		mapping[1] = new Color(25, 7, 26);
		mapping[2] = new Color(9, 1, 47);
		mapping[3] = new Color(4, 4, 73);
		mapping[4] = new Color(0, 7, 100);
		mapping[5] = new Color(12, 44, 138);
		mapping[6] = new Color(24, 82, 177);
		mapping[7] = new Color(57, 125, 209);
		mapping[8] = new Color(134, 181, 229);
		mapping[9] = new Color(211, 236, 248);
		mapping[10] = new Color(241, 233, 191);
		mapping[11] = new Color(248, 201, 95);
		mapping[12] = new Color(255, 170, 0);
		mapping[13] = new Color(204, 128, 0);
		mapping[14] = new Color(153, 87, 0);
		mapping[15] = new Color(106, 52, 3);
		
			
	 map = new ArrayList<Color>(Arrays.asList(mapping));
		Collections.shuffle(map);
		render();
		display(true);
	}

	
	public static void render() {

		for(int i = 0; i<map.size(); i++) {
			mapping[i] = map.get(i);
		}
		

		picture = new BufferedImage(n, n, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				double x = xStart + scale * i / n ;
				double y = yStart + scale * j / n ;
				Complex z = new Complex(x, y);
				int iter = escapeIter(z, z, 0);

				int color = getColor(iter);

				picture.setRGB(i, n - 1 - j, color);
			}
		}
		
	}
	
	public static void display(boolean first) {
		if(frame == null) {
			frame = new JFrame();
		}
		frame.getContentPane().removeAll();
		
		frame.getContentPane().setLayout(new FlowLayout());
		
		frame.getContentPane().add(new JLabel(new ImageIcon(picture)));
		
		if(first) {
	    frame.addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent e) {
	        	if (e.getButton() == MouseEvent.BUTTON1) {
	        		
		            zoom(e.getLocationOnScreen());
		            
	        	}
	        }
	      });
		}
		
		frame.pack();
		frame.setVisible(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void zoom(Point spot) {
		//scale/=1.1;
	xStart = scale *  (spot.getX()/2.0)/n;
	yStart = scale * (1.0/n - (spot.getY()/2.0)/n);
		System.out.println(xStart);
		System.out.println(yStart);
		
		
		render();
		display(false);
	}
	
	public static int escapeIter(Complex i, Complex i0, int a) {
		int count = 0;
		if (i.abs() < 2) {
			if (a < 255) {
				Complex i2 = i.multiply(i).add(i0);
				count = escapeIter(i2, i0, ++a);
			} else {
				count = 255;
			}
		} else {
			count = a*10;
		}
		return count;
	}

	public static int getColor(int n) {
		if (n < 256 && n > 0) {
			int i = n % 16;

			return mapping[i].getRGB();
		} else
			return Color.black.getRGB();

	}
	


}
