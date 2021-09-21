package cueare;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
import org.opencv.core.Rect;
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

		//Mat srcGray = src;
		
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
		return processFrameButCooler(img,false,false,false);
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
		System.out.println("bye bye noise");
	//	var pts = getCornerData(img);

		Mat src = BufferedImage2Mat(img);
		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		Mat blur = new Mat();
		Imgproc.GaussianBlur(gray, blur, new Size(9,9), 0);
		Mat thresh = new Mat();
		Imgproc.threshold(blur,thresh, 0,255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
		
		Mat finale = new Mat();
		Imgproc.cvtColor(thresh, finale, Imgproc.COLOR_GRAY2BGR);
		BufferedImage img2 = Mat2BufferedImage(finale);
		
		//next step: find a way to only keep the middle
		
		Mat kernel = new Mat();
		Mat close = new Mat();
		
//		Mat close3 = new Mat();
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
//		Imgproc.morphologyEx(thresh, close3, Imgproc.MORPH_CLOSE, kernel);
//		
		thresh.copyTo(close);
		
		Mat close2 = new Mat();
		Imgproc.Canny(close, close2, 90, 150);
		
		Mat hier = new Mat();
		List<MatOfPoint> points = new ArrayList<>();
		Imgproc.findContours(close2, points, hier, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		//Imgproc.drawContours(close2, points, -1, new Scalar(0,255,0), 3);
		// important Imgproc.drawContours(finale, points, -1, new Scalar(0,255,0), 3);
		
		c.display(thresh, finale );
		
		//List<MatOfPoint> approxShapes = new ArrayList<>();
		
		for(int i=0;i<points.size();i++){
		    //Convert contours(i) from MatOfPoint to MatOfPoint2f
			MatOfPoint2f r = new MatOfPoint2f();
			MatOfPoint2f tar = new MatOfPoint2f();
		    points.get(i).convertTo(r, CvType.CV_32FC2);
		    //Processing on mMOP2f1 which is in type MatOfPoint2f
		    Imgproc.approxPolyDP(r, tar, 0.04 * Imgproc.arcLength(r, true), true); 
		    //Convert back to MatOfPoint and put the new values back into the contours list
		    tar.convertTo(points.get(i), CvType.CV_32S);
		   
		}
		
//		for(int i = 0; i < points.size(); i++) {
//			Imgproc.drawContours(finale, points.subList(i, i+1), -1, new Scalar((int) (Math.random() * 255),(int) (Math.random() * 255),(int) (Math.random() * 255)), 3);
//		}
//		
		Collections.sort(points, new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				MatOfPoint one = (MatOfPoint) o1;
				MatOfPoint two = (MatOfPoint) o2;
				double area1 = Imgproc.contourArea(one);
				double area2 = Imgproc.contourArea(two);
				
				if(area1 > area2) {
					return 1;
				} else if(area1 < area2) {
					return -1;
				}
				
				
				return 0;
			}
			
		});
	
		MatOfPoint ppp = points.get(points.size() - 1);
		Rect rect = Imgproc.boundingRect(ppp);
	//	Imgproc.drawContours(finale, points.subList(points.size()-1, points.size()), -1, new Scalar((int) (Math.random() * 255),(int) (Math.random() * 255),(int) (Math.random() * 255)), 3);
		Mat submat = new Mat(finale, rect);
	  Imgproc.rectangle(finale, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255,0,255), 3);

		return processFrameButCooler(Mat2BufferedImage(submat));
		
		
		//return null;
	}
	
	
}