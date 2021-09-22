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
import org.opencv.core.Mat;

public class CameraUtil {

	static FrameGrabber grabber;
	static boolean show = true;
	static BufferedImage frame;
	static boolean takeProcessedOutput;
	static boolean processOlder = false;
	static boolean mirror = true;

	static boolean noiseCancel = true;
	
	static Java2DFrameConverter conv = new Java2DFrameConverter();
	CanvasFrame canvas;

	// scaling
	static boolean downScale = false;
	static double downScaleFactor = 2;
	static int scaleMethod = BufferedImage.SCALE_FAST;

	JSlider threshold;
	JSlider constant;
	JSlider corners;

	JCheckBox gray;
	JTextArea mouseCoords = new JTextArea("x: y: ");
	MappingUtil util;
	
	public CameraUtil() {
		util = new MappingUtil();
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
		JButton bigButt = new JButton("GrabQR");

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
			frame.getContentPane().add(new JLabel(new ImageIcon(rescale(i))));
		}

		frame.pack();
		// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	public void display(Mat... img) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());

		for (Mat i : img) {
			frame.getContentPane().add(new JLabel(new ImageIcon(rescale(BigBrainCornerDetector.Mat2BufferedImage(i)))));
		}

		frame.pack();
		// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public BufferedImage getCurrentFrame(boolean mirror) {
		try {

			BufferedImage img = conv.convert(grabber.grab());

			if (downScale) {
				img = this.toBufferedImage(img.getScaledInstance((int) (img.getWidth() / CameraUtil.downScaleFactor),
						(int) (img.getHeight() / CameraUtil.downScaleFactor), this.scaleMethod));

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
		return getCurrentFrame(mirror);
	}

	public void initializeManualDisplay(boolean harris) {
		JButton bigButt = new JButton("Selfie");

		bigButt.setSize(20, 10);

		JButton downscale = new JButton("Toggle Downscale");

		downscale.setSize(20, 10);

		JButton reverse = new JButton("Mirror Image");

		reverse.setSize(20, 10);

		JButton anotherButt = new JButton("Print Parameters");

		anotherButt.setSize(20, 10);

		threshold = new JSlider(10, 255, 199);
		threshold.setBorder(BorderFactory.createTitledBorder("Threshold"));

		constant = new JSlider(10, 80, 52);
		constant.setBorder(BorderFactory.createTitledBorder("Constant"));

		corners = new JSlider(0, 100, BigBrainCornerDetector.cornersCount);
		corners.setBorder(BorderFactory.createTitledBorder("Yeet LOL"));

		gray = new JCheckBox("Gray?");

		try {
			frame = this.getCurrentFrame();
			canvas = new CanvasFrame("Rishi's Epic Camera Frame");
			// canvas.setSize(800, 529);
			canvas.setLayout(new FlowLayout());

			canvas.setPreferredSize(new Dimension(1222, 580));

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

							if (noiseCancel) {
								BufferedImage res = rescale(
										BigBrainCornerDetector.noiseCancelling(CameraUtil.this.getCurrentFrame(true)));
								display(res);
								
										
										boolean[][] finalOutput = util.orient(BigBrainCornerDetector.cropToCode(res));
										util.displayJR(finalOutput);
										try {
											System.out.println(util.decode(finalOutput));
										} catch (Exception e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
							} else {
								display(rescale(BigBrainCornerDetector.processFrameButCooler(
										CameraUtil.this.getCurrentFrame(), false, true, harris)));
							}

							// display(rescale(BigBrainCornerDetector
							// .processFrameIntoQR(CameraUtil.this.getCurrentFrame())));

						}

					}
				}
			});

			anotherButt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("Threshold: " + threshold.getValue() + "     Constant: "
							+ constant.getValue() / 1000.0 + "     Yeet LOL: " + corners.getValue());
				}
			});

			downscale.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					CameraUtil.downScale = !downScale;
				}
			});

			reverse.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					CameraUtil.mirror = !mirror;
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
			canvas.add(downscale);
			canvas.add(reverse);
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
				canvas.showImage(img.getScaledInstance(640, 480, this.scaleMethod));
			} else {
				canvas.showImage(img);
			}

		}

		// canvas.showImage(img);

	}

	public BufferedImage rescale(BufferedImage img) {
		return this.toBufferedImage(img.getScaledInstance(640, 480, this.scaleMethod));
	}
	
	public BufferedImage rescale(BufferedImage img, int scaleMethod) {
		return this.toBufferedImage(img.getScaledInstance(640, 480, scaleMethod));
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

	public boolean isDownscaled() {
		return this.downScale;
	}

	public void setMirror(boolean a) {
		mirror = a;
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
