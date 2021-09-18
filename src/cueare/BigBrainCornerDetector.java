package cueare;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import nu.pattern.OpenCV;

public class BigBrainCornerDetector {

	static BufferedImage currentImage;
	static Feeder feeder;
	static CameraUtil c;

	static double k = 0.04;
	static int threshold = 200;
	static int cornersCount = 80;

	static boolean harris = false;

	// param
	static int minDistance = 12;

	public static void main(String... args) {
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		OpenCV.loadLocally();

		c = new CameraUtil();
		c.preProcess(true);
		c.initializeManualDisplay(harris);
		beginFeederThread();

		drawBoundsThread();

	}

	public static void beginFeederThread() {
		feeder = new Feeder();
		feeder.start();
	}

	public static void drawBoundsThread() {
		feeder.toggleManualInput();
		while (true) {
			// currentImage = c.getCurrentFrame();
			threshold = c.getThreshold();
			k = c.getConstant();
			cornersCount = c.getCorners();

			currentImage = processFrameButCooler(c.getCurrentFrame(), c.getGray(), false, harris);
			// currentImage = c.getCurrentFrame();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static BufferedImage processFrame(BufferedImage currentFrame, boolean gray, boolean printLocs) {
		// Mat m = new Mat();
		Mat src = BufferedImage2Mat(currentFrame);

		if (src == null) {
			// System.out.println("ih");
			return currentFrame;
			// return null;
		}

		Mat srcGray = new Mat();
		Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
		// int threshold = 220;
		Mat dstNorm = new Mat();
		Mat dstNormScaled = new Mat();

		Mat dst = Mat.zeros(srcGray.size(), CvType.CV_32F);

		/// Detector parameters
		int blockSize = 2;
		int apertureSize = 3;
		// double k = 0.04;

		/// Detecting corners
		Imgproc.cornerHarris(srcGray, dst, blockSize, apertureSize, k);

		/// Normalizing
		Core.normalize(dst, dstNorm, 0, 255, Core.NORM_MINMAX);
		Core.convertScaleAbs(dstNorm, dstNormScaled);

		/// Drawing a circle around corners
		float[] dstNormData = new float[(int) (dstNorm.total() * dstNorm.channels())];
		dstNorm.get(0, 0, dstNormData);

		for (int i = 0; i < dstNorm.rows(); i++) {
			for (int j = 0; j < dstNorm.cols(); j++) {
				if ((int) dstNormData[i * dstNorm.cols() + j] > threshold) {
					Imgproc.circle(gray ? dstNormScaled : src, new Point(j, i), 3, new Scalar(0), 1, 5, 0);

					if (printLocs) {
						System.out.println("Corner at : " + j + " , " + i);
					}
				}
			}
		}

		// System.out.println("frame found");

		return Mat2BufferedImage(gray ? dstNormScaled : src);
		// return Mat2BufferedImage(dstNormScaled);
		// return null;
	}

	static Random rng = new Random();

	public static BufferedImage processFrameButCooler(BufferedImage currentFrame, boolean gray, boolean printLocs,
			boolean harris) {
		Mat src = BufferedImage2Mat(currentFrame);
		Mat srcGray = new Mat();
		Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);

		cornersCount = Math.max(cornersCount, 1);
		MatOfPoint corners = new MatOfPoint();
		double qualityLevel = 0.01;
		// double minDistance = 10;
		int blockSize = 3, gradientSize = 3;
		boolean useHarrisDetector = harris;

		// double k = 0.04;

		Mat copy = src.clone();
		Imgproc.goodFeaturesToTrack(srcGray, corners, cornersCount, qualityLevel, minDistance, new Mat(), blockSize,
				gradientSize, useHarrisDetector, k);
		// System.out.println("** Number of corners detected: " + corners.rows());
		int[] cornersData = new int[(int) (corners.total() * corners.channels())];
		corners.get(0, 0, cornersData);
		int radius = c.isDownscaled() ? 3 : 4;
		int thickness = c.isDownscaled() ? 1 : 2;
		for (int i = 0; i < corners.rows(); i++) {
//            Imgproc.circle(gray ? copy : src, new Point(cornersData[i * 2], cornersData[i * 2 + 1]), radius,
//                    new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256)), Core.FILLED);

			Imgproc.circle(gray ? srcGray : copy, new Point(cornersData[i * 2], cornersData[i * 2 + 1]), radius,
					new Scalar(0), thickness, 5, 0);

			if (printLocs) {
				System.out.println("Corner at : " + cornersData[i * 2] + " , " + cornersData[i * 2 + 1]);
			}
		}
		return Mat2BufferedImage(gray ? srcGray : copy);
	}

	public static BufferedImage contraster(BufferedImage img) {
		return img;
	}

	public static class Feeder extends Thread {
		boolean flag = true;
		boolean acceptManual = false;

		@Override
		public void run() {

			while (flag) {

				if (!acceptManual) {
					currentImage = c.getCurrentFrame();
				}

				c.feedFrame(currentImage);
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		public void interrupt() {
			flag = false;
		}

		public void toggleManualInput() {
			acceptManual = !acceptManual;
		}
	}

	public static Mat BufferedImage2Mat(BufferedImage image) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", byteArrayOutputStream);
			byteArrayOutputStream.flush();
			return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage Mat2BufferedImage(Mat matrix) {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		try {
			return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
		} catch (Exception e) {

		}
		return null;
	}

	public static BufferedImage processFrameIntoQR(BufferedImage currentFrame) {
		System.out.println(getCornerData(currentFrame));
		return currentFrame;
	}

	public static ArrayList<Point> getCornerData(BufferedImage img) {

		Mat src = BufferedImage2Mat(img);
		Mat srcGray = new Mat();
		Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);

		ArrayList<Point> cornersPoints = new ArrayList<>();
		cornersCount = Math.max(cornersCount, 1);
		MatOfPoint corners = new MatOfPoint();
		double qualityLevel = 0.01;
		double minDistance = 10;
		int blockSize = 3, gradientSize = 3;
		boolean useHarrisDetector = harris;

		// double k = 0.04;

		// Mat copy = src.clone();
		Imgproc.goodFeaturesToTrack(srcGray, corners, cornersCount, qualityLevel, minDistance, new Mat(), blockSize,
				gradientSize, useHarrisDetector, k);
		// System.out.println("** Number of corners detected: " + corners.rows());
		int[] cornersData = new int[(int) (corners.total() * corners.channels())];
		corners.get(0, 0, cornersData);

		for (int i = 0; i < corners.rows(); i++) {
//	            Imgproc.circle(gray ? copy : src, new Point(cornersData[i * 2], cornersData[i * 2 + 1]), radius,
//	                    new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256)), Core.FILLED);

			cornersPoints.add(new Point(cornersData[i * 2], cornersData[i * 2 + 1]));

		}
		return cornersPoints;

	}


	
	
}