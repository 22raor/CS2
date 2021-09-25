package cueare;

import javax.swing.UIManager;

import util.BufferedLoader;

public class Tester {

	public static void main(String[] args) throws Exception {
		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	MappingUtil u = new MappingUtil();

	var b = u.encode("abcdefg");
	BufferedLoader bb = new BufferedLoader();
	
	bb.printImage(u.specToImage(b, false), 0, 0);
	
	
	//u.encodeAndDisplay();
	

	}

}
