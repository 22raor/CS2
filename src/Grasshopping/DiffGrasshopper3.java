package Grasshopping;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DiffGrasshopper3 {

	static final int IMG_SIZE = 300;

	static Pix[][] quickAccess = new Pix[IMG_SIZE][IMG_SIZE];
	
	
	public static class Pix {
		int x;
		int y;
		double num;

		public Pix(int x, int y, double num) {
			this.x = x;
			this.y = y;
			this.num = num;
		}

		@Override
		public boolean equals(Object a) {
			Pix b = (Pix) a;
			return b.x == this.x && b.y == this.y;

		}

		public void setNum(int i) {
			// TODO Auto-generated method stub
			this.num = i;
		}

	}

	public static ArrayList<Pix> all = new ArrayList<>();
	public static ArrayList<Pix> lawn = new ArrayList<>();

	static int currentIter = 0;

	public static void main(String[] args) {

		generateStartingLawn();
		news.add(pan);
		news.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		display();

		while (true) {

			int i = 0;
			while (i < 200000000) {
				simulateLanding();
				i++;
			}
			updateLawn();
			currentIter++;
			display();
			i = 0;

		}

	}

	static int newX = 0;
	static int newY = 0;

	public static void simulateLanding() {
		try {

			Pix b = lawn.get((int) (Math.random() * lawn.size()));
			double direction = Math.toRadians(Math.random() * 360.0);
			newX = (int) (b.x + Math.cos(direction) * 30.0);
			newY = (int) (b.y + Math.sin(direction) * 30.0);
//
//			Pix key = new Pix(newX, newY, 0);
//
//			// INCREMENT LANDINGS HERE
//
//			get(all, key).num++;
//			
			quickAccess[newX][newY].num++;

		} catch (Exception e) {
			System.out.println(newX + " " + newY);
		}

	}

	public static Pix get(ArrayList<Pix> ls, Pix a) {

		for (Pix e : ls) {
			if (e.equals(a)) {
				return e;
			}
		}
		return null;

	}

	public static void updateLawn() {

		Collections.shuffle(all); //OPTIONAL
		
		Collections.sort(all, (a, b) -> (int) (b.num - a.num));

		//lawn = new ArrayList<Pix>();
		lawn.clear();
		for (int i = 0; i < IMG_SIZE * IMG_SIZE; i++) {
			all.get(i).num = 0;
			if(i < 10000) {
				lawn.add(all.get(i));
			} 
			

		}

	}

	static int a = (IMG_SIZE - 100) / 2;
	static int tomato = 100 + a;

	public static void generateStartingLawn() {

		for (int i = 0; i < IMG_SIZE; i++) {
			for (int j = 0; j < IMG_SIZE; j++) {
				Pix tom = new Pix(i, j, 0);
				
				quickAccess[i][j] = tom;
				
				if (i > a && i < tomato && j > a && j < tomato) {

					
					lawn.add(tom);
					all.add(tom);
				} else {
					all.add(tom);
				}

			}
		}

	}

	static JFrame news = new JFrame();

	static Image scale;
	static JLabel lab;
	static JPanel pan = new JPanel();

	public static void display() {
		BufferedImage img = new BufferedImage(IMG_SIZE, IMG_SIZE, BufferedImage.TYPE_INT_RGB);

		
		for (Pix i : lawn) {

			int col = Color.blue.getRGB();

			img.setRGB(i.x, i.y, col);

		}

		scale = img.getScaledInstance(800, 800, BufferedImage.SCALE_SMOOTH);
		pan.removeAll();
		pan.add(new JLabel(new ImageIcon(scale)));
		pan.repaint();
		news.revalidate();
		news.pack();
		news.setVisible(true);

	}

}
