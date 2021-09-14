package cueare;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

	public CameraUtil() {

		try {
			grabber = new OpenCVFrameGrabber(0);
			grabber.start();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			grabber.setVideoBitrate(5000);
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
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						canvas.showImage(frame);
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

					Java2DFrameConverter conv = new Java2DFrameConverter();
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

}
