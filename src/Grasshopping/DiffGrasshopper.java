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



public class DiffGrasshopper {
	
	static final int IMG_SIZE = 300;
	
	
	public static class Pix {
		int x;
		int y;
		int num;

		public Pix(int x, int y, int num) {
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
		//generate hashcode?? unique for 2 vals

		@Override
		public int hashCode() {
			return (int) (Math.pow(2, x) - Math.pow(2, y));
			
		}
		
	}

	public static ArrayList<Pix> nonLawn = new ArrayList<>();
	public static ArrayList<Pix> lawn = new ArrayList<>();

	public static void main(String[] args) {
		
		
		ArrayList<Point> p = new ArrayList<>();
		
		
		
		generateStartingLawn();
		news.add(pan);
		news.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		display();
	
		while (true) {
	
			
			
			int i = 0;
			while (i < 10000) {
				simulateLanding();
				i++;
			}
			updateLawn();
			display();
			i = 0;
		}

	}

	static int newX = 0;
	static int newY = 0;


	public static void simulateLanding() {
		try {
		//	Collections.shuffle(lawn);
			
			
			Pix b = lawn.get((int) (Math.random() * lawn.size()));
			double direction = Math.toRadians(Math.random() * 360);
			newX = (int) (b.x + Math.cos(direction) * 30);
			newY = (int) (b.y + Math.sin(direction) * 30);

			Pix key = new Pix(newX, newY, 0);

			// INCREMENT LANDINGS HERE
			if (lawn.contains(key)) {
				Pix old = get(lawn,key);
				old.num++;

			} else {
				Pix old = get(nonLawn,key);
				old.num++;
			}

		} catch (Exception e) {
			System.out.println(newX + " " + newY);
		}

	}
	
	public static Pix get(ArrayList<Pix> ls, Pix a) {
		
		for(Pix e : ls) {
			if(e.equals(a)) {
				return e;
			}
		}
		return null;
		
	}

	static int numLawn = IMG_SIZE * IMG_SIZE;
	
	public static void updateLawn() {


		List<Pix> aList = new ArrayList<Pix>();
		aList.addAll(lawn);
		aList.addAll(nonLawn);
		
		
		Collections.sort(aList, (a,b) -> b.num - a.num);

		lawn = new ArrayList<Pix>();
		nonLawn = new ArrayList<Pix>();
		
		
		for(int i = 0; i < numLawn; i++) {
			if(i < 10000) {
			//	aList.get(i).setNum(0);
				lawn.add(aList.get(i)); //start at 0 again or old value?
			} else {
				//aList.get(i).setNum(0);
				nonLawn.add(aList.get(i)); //start at 0 again or old value?
			}
		}
		
	

	}

	public static void generateStartingLawn() {
		int a = (IMG_SIZE - 100)/2;
		
		
		for (int i = 0; i < IMG_SIZE; i++) {
			for (int j = 0; j < IMG_SIZE; j++) {

				if (i > a && i < 100+a && j > a && j < 100+a) {
					lawn.add(new Pix(i, j, 0));
				} else {
					nonLawn.add(new Pix(i, j, 0));
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
			img.setRGB(i.x, i.y, Color.blue.getRGB());

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
