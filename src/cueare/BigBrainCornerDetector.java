package cueare;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import nu.pattern.OpenCV;

public class BigBrainCornerDetector {

	static BufferedImage currentImage;
	static Feeder feeder;
	static CameraUtil c;

	static double k = 0.04;
	static int threshold = 200;
	static int cornersCount = 30;

	static boolean harris = false;

	// param
	static int minDistance = 20;

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

			// currentImage = processFrameButCooler(c.getCurrentFrame(), c.getGray(), false,
			// harris);
			currentImage = contourDraw(c.getCurrentFrame());
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

		// Mat srcGray = src;

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

	public static BufferedImage processFrameButCooler(BufferedImage img) {
		return processFrameButCooler(img, false, false, false);
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

	@SuppressWarnings("unchecked")
	public static BufferedImage noiseCancelling(BufferedImage img) {

		Mat src = BufferedImage2Mat(img);
		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		Mat blur = new Mat();
		Imgproc.GaussianBlur(gray, blur, new Size(9, 9), 0);
		Mat thresh = new Mat();
		Imgproc.threshold(blur, thresh, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

		Mat finale = new Mat();
		Imgproc.cvtColor(thresh, finale, Imgproc.COLOR_GRAY2BGR);

		Mat close = new Mat();

		thresh.copyTo(close);

		Mat close2 = new Mat();
		Imgproc.Canny(close, close2, 90, 150);

		Mat hier = new Mat();
		List<MatOfPoint> points = new ArrayList<>();
		Imgproc.findContours(close2, points, hier, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		// Imgproc.drawContours(close2, points, -1, new Scalar(0,255,0), 3);
		// important Imgproc.drawContours(finale, points, -1, new Scalar(0,255,0), 3);

		for (int i = 0; i < points.size(); i++) {
			// Convert contours(i) from MatOfPoint to MatOfPoint2f
			MatOfPoint2f r = new MatOfPoint2f();
			MatOfPoint2f tar = new MatOfPoint2f();
			points.get(i).convertTo(r, CvType.CV_32FC2);
			// Processing on mMOP2f1 which is in type MatOfPoint2f
			Imgproc.approxPolyDP(r, tar, 0.04 * Imgproc.arcLength(r, true), true);
			// Convert back to MatOfPoint and put the new values back into the contours list
			tar.convertTo(points.get(i), CvType.CV_32S);

		}

		Collections.sort(points, new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				MatOfPoint one = (MatOfPoint) o1;
				MatOfPoint two = (MatOfPoint) o2;
				double area1 = Imgproc.contourArea(one);
				double area2 = Imgproc.contourArea(two);

				if (area1 > area2) {
					return 1;
				} else if (area1 < area2) {
					return -1;
				}

				return 0;
			}

		});

		MatOfPoint ppp = points.get(points.size() - 1);

		Imgproc.drawContours(src, points.subList(points.size() - 1, points.size()), -1, new Scalar(255, 0, 0), 3);
		var pts = ppp.toList();
		// System.out.println(ppp.size()); This statement can verify that it is a square
		// System.out.println(pts);

		if (pts.size() != 4) {
			return Mat2BufferedImage(src);
		} else {

			Mat startM = vector_Point2f_to_Mat(pts);

			Point aa = new Point(0, finale.height());
			Point bb = new Point(finale.width(), finale.height());
			Point cc = new Point(finale.width(), 0);
			Point dd = new Point(0, 0);

			Mat cropped = new Mat(finale.width(), finale.height(), CvType.CV_8UC4);
			List<Point> dest = new ArrayList<Point>(Arrays.asList(aa, bb, cc, dd));

			Mat endM = vector_Point2f_to_Mat(dest);

			Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

			Imgproc.warpPerspective(finale, cropped, perspectiveTransform, new Size(finale.width(), finale.height()),
					Imgproc.INTER_CUBIC);

			Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size((2 * 2) + 1, (2 * 2) + 1));
			Imgproc.dilate(cropped, cropped, kernel);

			return Mat2BufferedImage(cropped);
		}

		// return Mat2BufferedImage(src);

		// return null;s
	}

	@SuppressWarnings("unchecked")
	public static BufferedImage contourDraw(BufferedImage img) {

		Mat src = BufferedImage2Mat(img);
		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		Mat blur = new Mat();
		Imgproc.GaussianBlur(gray, blur, new Size(9, 9), 0);
		Mat thresh = new Mat();
		Imgproc.threshold(blur, thresh, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

		Mat finale = new Mat();
		Imgproc.cvtColor(thresh, finale, Imgproc.COLOR_GRAY2BGR);

		Mat close = new Mat();

		thresh.copyTo(close);

		Mat close2 = new Mat();
		Imgproc.Canny(close, close2, 90, 150);

		Mat hier = new Mat();
		List<MatOfPoint> points = new ArrayList<>();
		Imgproc.findContours(close2, points, hier, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		// Imgproc.drawContours(close2, points, -1, new Scalar(0,255,0), 3);
		// important Imgproc.drawContours(finale, points, -1, new Scalar(0,255,0), 3);

		for (int i = 0; i < points.size(); i++) {
			// Convert contours(i) from MatOfPoint to MatOfPoint2f
			MatOfPoint2f r = new MatOfPoint2f();
			MatOfPoint2f tar = new MatOfPoint2f();
			points.get(i).convertTo(r, CvType.CV_32FC2);
			// Processing on mMOP2f1 which is in type MatOfPoint2f
			Imgproc.approxPolyDP(r, tar, 0.04 * Imgproc.arcLength(r, true), true);
			// Convert back to MatOfPoint and put the new values back into the contours list
			tar.convertTo(points.get(i), CvType.CV_32S);

		}

		Collections.sort(points, new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				MatOfPoint one = (MatOfPoint) o1;
				MatOfPoint two = (MatOfPoint) o2;
				double area1 = Imgproc.contourArea(one);
				double area2 = Imgproc.contourArea(two);

				if (area1 > area2) {
					return 1;
				} else if (area1 < area2) {
					return -1;
				}

				return 0;
			}

		});

		MatOfPoint ppp = points.get(points.size() - 1);

		Imgproc.drawContours(src, points.subList(points.size() - 1, points.size()), -1, new Scalar(255, 0, 0), 3);
		var pts = ppp.toList();
		// System.out.println(ppp.size()); This statement can verify that it is a square
		// System.out.println(pts);

		return Mat2BufferedImage(src);

	}

	public static boolean[][] cropToCode(BufferedImage img) {
		// Graphics g = img.getGraphics();
		// g.setColor(Color.green);
		boolean[][] output = new boolean[7][7];

		for (int i = 85; i < 600; i += 80) {
			for (int j = 70; j < 450; j += 59) {
				// g.fillRect(i-10, j-10, 10, 10);

				// pic is inverted so white is true;
				output[(i - 85) / 80][(j - 70) / 59] = isWhite(img.getRGB(i, j));

			}

		}
		// MappingUtil util = new MappingUtil();
		// util.displayJR(output);
		// c.display(img);
		return output;

	}

	static boolean isWhite(int rgb) {
		Color c = new Color(rgb);
		if (c.getRed() > 100 && c.getBlue() > 100) {
			return true;
		}

		return false;
	}

	public static Mat vector_Point2f_to_Mat(List<Point> pts) {
		return vector_Point_to_Mat(pts, CvType.CV_32F);
	}

	public static Mat vector_Point_to_Mat(List<Point> pts, int typeDepth) {
		Mat res;
		int count = (pts != null) ? pts.size() : 0;
		if (count > 0) {
			switch (typeDepth) {
			case CvType.CV_32S: {
				res = new Mat(count, 1, CvType.CV_32SC2);
				int[] buff = new int[count * 2];
				for (int i = 0; i < count; i++) {
					Point p = pts.get(i);
					buff[i * 2] = (int) p.x;
					buff[i * 2 + 1] = (int) p.y;
				}
				res.put(0, 0, buff);
			}
				break;

			case CvType.CV_32F: {
				res = new Mat(count, 1, CvType.CV_32FC2);
				float[] buff = new float[count * 2];
				for (int i = 0; i < count; i++) {
					Point p = pts.get(i);
					buff[i * 2] = (float) p.x;
					buff[i * 2 + 1] = (float) p.y;
				}
				res.put(0, 0, buff);
			}
				break;

			case CvType.CV_64F: {
				res = new Mat(count, 1, CvType.CV_64FC2);
				double[] buff = new double[count * 2];
				for (int i = 0; i < count; i++) {
					Point p = pts.get(i);
					buff[i * 2] = p.x;
					buff[i * 2 + 1] = p.y;
				}
				res.put(0, 0, buff);
			}
				break;

			default:
				throw new IllegalArgumentException("'typeDepth' can be CV_32S, CV_32F or CV_64F");
			}
		} else {
			res = new Mat();
		}
		return res;
	}

}