package cueare;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.UIManager;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class CameraUtil {

	static FrameGrabber grabber;
	static boolean show = true;
	static Frame frame;

	static Java2DFrameConverter conv = new Java2DFrameConverter();
	CanvasFrame canvas;

	JSlider threshold;
	JSlider constant;
	JCheckBox gray;

	public CameraUtil() {

		try {
			grabber = new OpenCVFrameGrabber(0);
			grabber.start();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

		}

	}

	public void display() {
		JButton bigButt = new JButton("Selfie");

		bigButt.setSize(20, 10);

		try {
			frame = grabber.grab();

			CanvasFrame canvas = new CanvasFrame("Camera");
			// canvas.setVisible(false);

			canvas.setLayout(new FlowLayout());

			Thread one = new Thread() {
				boolean flag = true;

				public void run() {

					while (flag) {

						try {
							CameraUtil.this.frame = grabber.grab();
							canvas.showImage(frame);
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
					BufferedImage b = conv.convert(CameraUtil.this.frame);
					display(b);
					System.out.println("selfie");
				}
			});

			canvas.add(bigButt);
			canvas.pack();
			canvas.showImage(frame);

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

	public BufferedImage getCurrentFrame() {
		try {
			return conv.convert(grabber.grab());
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void initializeManualDisplay() {
		JButton bigButt = new JButton("Selfie");

		bigButt.setSize(20, 10);

		threshold = new JSlider(10, 255, 200);
		threshold.setBorder(BorderFactory.createTitledBorder("Threshold"));

		constant = new JSlider(10, 80, 40);
		constant.setBorder(BorderFactory.createTitledBorder("Constant"));

		gray = new JCheckBox("Gray?");

		try {
			frame = grabber.grab();
			canvas = new CanvasFrame("Camera");
			// canvas.setSize(800, 529);
			canvas.setLayout(new FlowLayout());

			try {
				CameraUtil.this.frame = grabber.grab();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			canvas.showImage(conv.convert(frame));

			bigButt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// System.out.println("Mouse clicked");

					// Java2DFrameConverter conv = new Java2DFrameConverter();
					BufferedImage b = conv.convert(CameraUtil.this.frame);
					display(b);
					// System.out.println("selfie");

					System.out.println(canvas.getWidth() + " " + canvas.getHeight());
				}
			});

			canvas.add(bigButt);
			canvas.add(threshold);
			canvas.add(constant);
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
		canvas.showImage(img);
	}

	public boolean getGray() {
		return gray.isSelected();
	}

	public int getThreshold() {
		return threshold.getValue();
	}

	public double getConstant() {
		return constant.getValue() / 1000.0;
	}

}
