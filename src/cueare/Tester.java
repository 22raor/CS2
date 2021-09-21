package cueare;

import java.util.Arrays;

import nu.pattern.OpenCV;

public class Tester {

	public static void main(String[] args) throws Exception {
//		CameraUtil util = new CameraUtil();
//		util.display();

	MappingUtil u = new MappingUtil();
		OpenCV.loadLocally();
		boolean[][] bool = u.encode("rishivr");
//		// System.out.println(Arrays.toString(u.charToBinary('z')));

		for (boolean[] a : bool) {
			System.out.println(Arrays.toString(a));
		}

		u.displayJR(bool);


	}

}
