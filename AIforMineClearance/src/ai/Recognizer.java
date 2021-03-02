package ai;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.ConstantCallSite;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import test.test4;
import tools.ScreenShotWindow;

public class Recognizer {
	private ScreenShotWindow ssw;
	private Rectangle selectedArea;
	private BufferedImage image;
	private BufferedImage[] imageSample;
	private static final int sampleSize=19; 

	private int a, b;
	private int[][] lastResult;
	private int[][] result;
	private ArrayList<Integer> divWELine;
	private ArrayList<Integer> divSNLine;

	public static final int ResultFlag = 9;
	public static final int ResultBlank = 10;

	public Rectangle getSelectedArea() {
		return selectedArea;
	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	public int[][] getResult() {
		return result;
	}

	public ArrayList<Integer> getDivWELine() {
		return divWELine;
	}

	public ArrayList<Integer> getDivSNLine() {
		return divSNLine;
	}

	public void clearResult() {
		lastResult=result;
		result = null;
	}

	public Recognizer() {
		// TODO Auto-generated constructor stub
		ssw = null;
		selectedArea = null;
		image = null;
		a = b = -1;
		lastResult=null;
		result = null;
		divWELine = null;
		divSNLine = null;
		imageSample = new BufferedImage[sampleSize];

		String basePath = Recognizer.class.getResource("").toString();
		basePath = basePath.substring(basePath.indexOf('/') + 1,
				basePath.length() - 1);
		basePath = basePath.substring(0, basePath.lastIndexOf('/'));
		basePath = basePath.substring(0, basePath.lastIndexOf('/'));
		// basePath = basePath.substring(0, basePath.lastIndexOf('/'));
		basePath = basePath + "/ImageSamples/";
		for (int i = 0; i <= sampleSize-1; i++) {
			System.out.println(basePath + i + ".png");
			File f = new File(basePath + i + ".png");
			try {
				imageSample[i] = ImageIO.read(f);
				System.out.println(i + ":" + imageSample[i].getWidth() + " "
						+ imageSample[i].getHeight());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Rectangle selectAimArea() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ssw = new ScreenShotWindow();
					ssw.setVisible(true);
				} catch (AWTException e) {
					e.printStackTrace();// -16776826
				}
			}
		});
		while (true) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (ssw != null) {
				if (ssw.getSelectedRectangle() != null) {
					// System.out.println("chosen");
					ssw.removeAll();
					ssw.setVisible(false);
					selectedArea = ssw.getSelectedRectangle();
					return selectedArea;
				}
			}
			// System.out.println("unChosen");
		}
	}

	public void judgeSelectedArea() throws Exception {
		Robot robot = new Robot();
		image = robot.createScreenCapture(selectedArea);
//		ImageIO.write(image, "jpg", new File("d:", "temp.png"));
		divWELine = new ArrayList<Integer>();
		divSNLine = new ArrayList<Integer>();
		for (int y = 0; y < image.getHeight(); y++) {
			if (hasDivLine(y, true, false, image)) {
				if (divWELine.size() > 0)
					if (y - divWELine.get(divWELine.size() - 1) < 4) {
						divWELine.set(divWELine.size() - 1, y);
						continue;
					}
				divWELine.add(y);
			}
		}
		System.out.println("WE has line: " + divWELine.size());
		this.a = divWELine.size() - 1;
		for (int x = 0; x < image.getWidth(); x++) {
			if (hasDivLine(x, false, true, image)) {
				if (divSNLine.size() > 0)
					if (x - divSNLine.get(divSNLine.size() - 1) < 4) {
						divSNLine.set(divSNLine.size() - 1, x);
						continue;
					}
				divSNLine.add(x);
			}
		}
		System.out.println("SN has line: " + divSNLine.size());
		this.b = divSNLine.size() - 1;
	}

	public int makeResult() throws AWTException, IOException {
		if (selectedArea == null || divWELine == null || divSNLine == null)
			return -1;
		Robot robot = new Robot();
		image = robot.createScreenCapture(selectedArea);
		result = createMatrix(this.a, this.b);
		boolean sameWithTheLast=true;
		System.out.println("Results: ");
		for (int i = 0; i < this.a; i++) {
			for (int j = 0; j < this.b; j++) {
				int x = divSNLine.get(j);
				int w = divSNLine.get(j + 1) - divSNLine.get(j);
				int y = divWELine.get(i);
				int h = divWELine.get(i + 1) - divWELine.get(i);
				x += w / 5;
				y += h / 5;
				w = w * 3 / 5;
				h = h * 3 / 5;
				BufferedImage subImage = image.getSubimage(x, y, w, h);
//				 ImageIO.write(subImage, "jpg", new
//				 File("d:/temp/"+"temp"+i+"a"+j+".png"));
				result[i][j] = judgeImageWithSamples(subImage);
				if(lastResult!=null&&result[i][j]!=lastResult[i][j])
					sameWithTheLast=false;
				System.out.print(result[i][j] + " ");
			}
			System.out.println();
		}
		if(lastResult!=null&&sameWithTheLast)
			return 2;
		return 1;
	}

	private int[][] createMatrix(int a, int b) {
		int[][] t = new int[a][];
		for (int i = 0; i < a; i++) {
			t[i] = new int[b];
		}
		return t;
	}

	private boolean hasDivLine(int t, boolean isWE, boolean isSN,
			BufferedImage image) {
		// System.out.println(t + ":");
		if (image == null) {
			System.out.println("no image");
			return false;
		}
		if (isWE) {
			if (t + 1 < image.getHeight()) {
				int start = -1, end = -1;
				for (int x = 0; x < image.getWidth(); x++) {
					int rgbThis = image.getRGB(x, t);
					if (getGreyLevel(rgbThis) < 140
							&& distanceToGrey(rgbThis) < 150) {// dark
						// System.out.print("D");
						if (start == -1) {
							start = x;
							continue;
						} else {
							if (end != -1) {
								// System.out
								// .println("do not allow a dark dot outside:"+x);
								return false;// do not allow a dark dot outside
												// the
												// line (happen when start!=-1)
							}
						}
					} else {// light
						// System.out.print("L");
						if (start == -1) {
							continue;
						}
						if (end == -1) {
							end = x - 1;
						}
					}

				}
				if (end == -1 && start == -1) {
					// System.out.println("no line");
					return false;
				}
				return true;
			} else {
				// System.out.println("too low");
				return false;
			}
		}
		if (isSN) {
			if (t + 1 < image.getWidth()) {
				int start = -1, end = -1;
				for (int y = 0; y < image.getHeight(); y++) {
					int rgbThis = image.getRGB(t, y);
					if (getGreyLevel(rgbThis) < 140
							&& distanceToGrey(rgbThis) < 150) {// dark
						// System.out.print("D");
						if (start == -1) {
							start = y;
							continue;
						} else {
							if (end != -1) {
								// System.out
								// .println("do not allow a dark dot outside:"+x);
								return false;// do not allow a dark dot outside
												// the
												// line (happen when start!=-1)
							}
						}
					} else {// light
						// System.out.print("L");
						if (start == -1) {
							continue;
						}
						if (end == -1) {
							end = y - 1;
						}
					}

				}
				if (end == -1 && start == -1) {
					// System.out.println("no line");
					return false;
				}
				return true;
			} else {
				// System.out.println("too low");
				return false;
			}
		}
		return false;
	}

	private int judgeImageWithSamples(BufferedImage image) {
		int[] distance = new int[sampleSize];
		for (int i = 0; i < sampleSize; i++) {
			distance[i] = 0;
			for (int y = 0; y < image.getHeight(); y++)
				for (int x = 0; x < image.getWidth(); x++) {
					distance[i] += rgbCompare(
							image.getRGB(x, y),
							imageSample[i].getRGB(
									(int) (((double) x) / image.getWidth() * imageSample[i]
											.getWidth()),
									(int) (((double) y) / image.getHeight() * imageSample[i]
											.getHeight())));
				}
		}
		int minOrder = 0;
		for (int i = 1; i < sampleSize; i++) {
			if (distance[i] < distance[minOrder]) {
				minOrder = i;
			}
		}
//		System.out.print("("+minOrder+","+distance[minOrder]+")");
		if (minOrder == 9 || minOrder == 10|| minOrder == 15|| minOrder == 16|| minOrder == 17)
			return ResultFlag;
		if (minOrder == 11 || minOrder == 12||minOrder == 14)
			return ResultBlank;
		if (minOrder == 13)
			return 0;
		if(minOrder==18)
			return 1;
		return minOrder;
	}

	private int rgbCompare(int rgb1, int rgb2) {
		Color c1 = new Color(rgb1);
		Color c2 = new Color(rgb2);
		int dr = Math.abs(c1.getRed() - c2.getRed());
		int dg = Math.abs(c1.getBlue() - c2.getBlue());
		int db = Math.abs(c1.getGreen() - c2.getGreen());
		return dr + dg + db;
	}

	private int getGreyLevel(int rgb) {
		Color c = new Color(rgb);
		return (c.getBlue() + c.getRed() + c.getGreen()) / 3;
	}

	private int distanceToGrey(int rgb) {
		Color c = new Color(rgb);
		int greyLevel = getGreyLevel(rgb);
		int dg = Math.abs(c.getRed() - greyLevel);
		dg += Math.abs(c.getBlue() - greyLevel);
		dg += Math.abs(c.getGreen() - greyLevel);
		return dg;
	}

}
