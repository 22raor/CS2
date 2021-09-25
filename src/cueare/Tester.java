package cueare;

import javax.swing.UIManager;

public class Tester {

	public static void main(String[] args) throws Exception {
		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	MappingUtil u = new MappingUtil();

	u.encodeAndDisplay();
	

	}

}
