package cueare;

import nu.pattern.OpenCV;

public class Tester {

	public static void main(String[] args) throws Exception {
//		CameraUtil util = new CameraUtil();
//		util.display();

		MappingUtil u = new MappingUtil();
		OpenCV.loadLocally();

		boolean[][] bool = u.encode("00sde0z");
		// System.out.println(Arrays.toString(u.charToBinary('z')));

		for (boolean[] a : bool) {
			// System.out.println(Arrays.toString(a));
		}

		System.out.println(u.decode(bool));

		// BigBrainCornerDetector.c = new CameraUtil();
		// u.display(BigBrainCornerDetector.processFrameButCooler(u.displayJR(bool),
		// false, false, false));
		// System.out.println();

	}

}
