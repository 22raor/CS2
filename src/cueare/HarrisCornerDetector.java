package cueare;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import nu.pattern.OpenCV;

public class HarrisCornerDetector {

	static BufferedImage currentImage;
	static Feeder feeder;
	static CameraUtil c;

	static double k = 0.04;
	static int threshold = 200;

	public static void main(String... args) {
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		OpenCV.loadLocally();

		c = new CameraUtil();
		c.initializeManualDisplay();
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

			currentImage = processFrame(c.getCurrentFrame(), c.getGray());
			// currentImage = c.getCurrentFrame();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static BufferedImage processFrame(BufferedImage currentFrame, boolean gray) {
		// Mat m = new Mat();
		Mat src = BufferedImage2Mat(currentFrame);

		if (src == null) {
			return currentFrame;
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
					Imgproc.circle(gray ? dstNormScaled : src, new Point(j, i), 5, new Scalar(0), 2, 8, 0);
				}
			}
		}

		// System.out.println("frame found");

		return Mat2BufferedImage(gray ? dstNormScaled : src);

	}

	private static BufferedImage processFrameOrig(BufferedImage currentFrame) {
		// Mat m = new Mat();
		Mat src = BufferedImage2Mat(currentFrame);

		if (src == null) {
			return currentFrame;
		}

		Mat srcGray = new Mat();
		Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
		// Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2BGR565);

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
					Imgproc.circle(src, new Point(j, i), 5, new Scalar(0), 2, 8, 0);
				}
			}
		}

		// System.out.println("frame found");

		return Mat2BufferedImage(src);

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
					Thread.sleep(20);
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
			ImageIO.write(image, "jpg", byteArrayOutputStream);
			byteArrayOutputStream.flush();
			return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
		} catch (Exception e) {

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

}
