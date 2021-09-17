package cueare;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class CameraUtil {

	static FrameGrabber grabber;
	static boolean show = true;
	static BufferedImage frame;
	static boolean takeProcessedOutput;
	static boolean processOlder = false;

	static Java2DFrameConverter conv = new Java2DFrameConverter();
	CanvasFrame canvas;

	// scaling
	static boolean downScale = true;
	static double downScaleFactor = 2;

	JSlider threshold;
	JSlider constant;
	JSlider corners;

	JCheckBox gray;
	JTextArea mouseCoords = new JTextArea("x: y: ");

	public CameraUtil() {

		try {
			grabber = new OpenCVFrameGrabber(0);

			grabber.start();
			System.out.println("Initializing frame capture at " + grabber.getFrameRate() + " FPS...");
			System.out.println(grabber.grab().imageWidth + " " + grabber.grab().imageHeight);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void processOlder(boolean a) {
		processOlder = a;
	}

	public void display() {
		JButton bigButt = new JButton("Selfie");

		bigButt.setSize(20, 10);

		try {
			frame = this.getCurrentFrame();

			CanvasFrame canvas = new CanvasFrame("Camera");
			// canvas.setVisible(false);

			canvas.setLayout(new FlowLayout());

			Thread one = new Thread() {
				boolean flag = true;

				public void run() {

					while (flag) {

						try {

							feedFrame(CameraUtil.this.getCurrentFrame());
							Thread.sleep(30);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

				public void interrupt() {
					flag = false;
				}
			};

			bigButt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// System.out.println("Mouse clicked");
					one.interrupt();

					// Java2DFrameConverter conv = new Java2DFrameConverter();
					display(CameraUtil.this.getCurrentFrame());
					System.out.println("selfie");
				}
			});

			canvas.add(bigButt);
			canvas.pack();
			feedFrame(this.getCurrentFrame());

			canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			one.start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void display(BufferedImage... img) {
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

	public BufferedImage getCurrentFrame(boolean mirror) {
		try {

			BufferedImage img = conv.convert(grabber.grab());

			if (downScale) {
				img = this.toBufferedImage(img.getScaledInstance((int) (img.getWidth() / CameraUtil.downScaleFactor),
						(int) (img.getHeight() / CameraUtil.downScaleFactor), BufferedImage.SCALE_FAST));

			}

			if (mirror) {
				AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
				tx.translate(-img.getWidth(null), 0);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				img = op.filter(img, null);
			}

			return img;
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public BufferedImage getCurrentFrame() {
		return getCurrentFrame(false);
	}

	public void initializeManualDisplay() {
		JButton bigButt = new JButton("Selfie");

		bigButt.setSize(20, 10);

		JButton anotherButt = new JButton("Print Parameters");

		anotherButt.setSize(20, 10);

		threshold = new JSlider(10, 255, 199);
		threshold.setBorder(BorderFactory.createTitledBorder("Threshold"));

		constant = new JSlider(10, 80, 52);
		constant.setBorder(BorderFactory.createTitledBorder("Constant"));

		corners = new JSlider(0, 30, 15);
		corners.setBorder(BorderFactory.createTitledBorder("Yeet LOL"));

		gray = new JCheckBox("Gray?");

		try {
			frame = this.getCurrentFrame();
			canvas = new CanvasFrame("Camera");
			// canvas.setSize(800, 529);
			canvas.setLayout(new FlowLayout());

			canvas.setPreferredSize(new Dimension(1100, 580));

			try {
				CameraUtil.this.frame = this.getCurrentFrame();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			feedFrame(frame);

			bigButt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {

					if (!takeProcessedOutput) {

						display(rescale(CameraUtil.this.getCurrentFrame()));
					} else {

						if (CameraUtil.processOlder) {
							display(rescale(BigBrainCornerDetector.processFrame(CameraUtil.this.getCurrentFrame(),
									false, true)));
						} else {
							display(rescale(BigBrainCornerDetector
									.processFrameButCooler(CameraUtil.this.getCurrentFrame(), false, true)));
						}

					}
				}
			});

			anotherButt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println(
							"Threshold: " + threshold.getValue() + "     Constant: " + constant.getValue() / 1000.0);
				}
			});

			MouseMotionListener m = new MouseMotionListener() {
				@Override
				public void mouseDragged(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseMoved(MouseEvent e) {
					int xNum = e.getX();
					int yNum = e.getY();
					String x = xNum + "";
					String y = yNum + "";

					// if(xNum >= 0 && yNum >=0) {
					// x = "0".repeat(3 - String.valueOf(xNum).length()) + xNum;
					// y = "0".repeat(3 - String.valueOf(yNum).length()) + yNum;
					// }

					mouseCoords.setText("x: " + x + " y: " + y);
					// currentY = e.getY();

				}
			};

			canvas.getCanvas().addMouseMotionListener(m);
			canvas.addMouseMotionListener(m);

			mouseCoords.setPreferredSize(new Dimension(85, 20));

			canvas.add(mouseCoords);
			canvas.add(bigButt);
			canvas.add(anotherButt);
			canvas.add(threshold);
			canvas.add(constant);
			canvas.add(corners);
			canvas.add(gray);
			canvas.pack();
			// canvas.showImage(frame);

			canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void feedFrame(BufferedImage img) {

		if (img != null) {

			if (downScale) {
				canvas.showImage(img.getScaledInstance(640, 480, BufferedImage.SCALE_FAST));
			} else {
				canvas.showImage(img);
			}

		}

		// canvas.showImage(img);

	}

	public BufferedImage rescale(BufferedImage img) {
		return this.toBufferedImage(img.getScaledInstance(640, 480, BufferedImage.SCALE_FAST));
	}

	public boolean getGray() {
		return gray.isSelected();
	}

	public int getThreshold() {
		return threshold.getValue();
	}

	public int getCorners() {
		return corners.getValue();
	}

	public double getConstant() {
		return constant.getValue() / 1000.0;
	}

	public void preProcess(boolean a) {
		takeProcessedOutput = a;
	}

	public BufferedImage toBufferedImage(Image img) {
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
