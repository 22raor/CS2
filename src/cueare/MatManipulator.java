package cueare;

import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import nu.pattern.OpenCV;

public class MatManipulator {

	public static void main(String[] args) throws InterruptedException {
		
		OpenCV.loadLocally();
		CameraUtil util = new CameraUtil();
		BufferedImage img = util.getCurrentFrame();

		BufferedImage img2 = toBufferedImage(img.getScaledInstance(img.getWidth() / 2, img.getHeight() / 2, BufferedImage.SCALE_FAST));

		display(img, img2);

		//Thread.sleep(30);
		
		try {
			BufferedImage2Mat(img2);
		} catch (Exception e) {
			System.out.println("second");
		}

	}

	public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		ImageIO.write(image, "png", byteArrayOutputStream);
		byteArrayOutputStream.flush();
		return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);

		// return null;
	}
	
	
	public static Mat matify(BufferedImage sourceImg) {

	 //   long millis = System.currentTimeMillis();

	    DataBuffer dataBuffer = sourceImg.getRaster().getDataBuffer();
	    byte[] imgPixels = null;
	    Mat imgMat = null;

	    int width = sourceImg.getWidth();
	    int height = sourceImg.getHeight();

	    if(dataBuffer instanceof DataBufferByte) {      
	            imgPixels = ((DataBufferByte)dataBuffer).getData();
	    }

	    if(dataBuffer instanceof DataBufferInt) {

	        int byteSize = width * height;      
	        imgPixels = new byte[byteSize*3];

	        int[] imgIntegerPixels = ((DataBufferInt)dataBuffer).getData();

	        for(int p = 0; p < byteSize; p++) {         
	            imgPixels[p*3 + 0] = (byte) ((imgIntegerPixels[p] & 0x00FF0000) >> 16);
	            imgPixels[p*3 + 1] = (byte) ((imgIntegerPixels[p] & 0x0000FF00) >> 8);
	            imgPixels[p*3 + 2] = (byte) (imgIntegerPixels[p] & 0x000000FF);
	        }
	    }

	    if(imgPixels != null) {
	        imgMat = new Mat(height, width, CvType.CV_8UC3);
	        imgMat.put(0, 0, imgPixels);
	    }

	  //  System.out.println("matify exec millis: " + (System.currentTimeMillis() - millis));

	    return imgMat;
	}

	public static void display(BufferedImage... img) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());

		for (BufferedImage i : img) {
			frame.getContentPane().add(new JLabel(new ImageIcon(i)));
		}

		frame.pack();
		// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static BufferedImage toBufferedImage(Image img) {
		// not mine
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		return bimage;
	}

}
