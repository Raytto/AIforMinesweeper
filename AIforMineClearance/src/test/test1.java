package test;  
  
import java.io.*;  
import javax.swing.*;  
import java.awt.*;  
import java.awt.event.*;  
import java.awt.image.*;  
import javax.imageio.*;  
  
/** 
 * һ���򵥵���Ļץͼ 
 *  
 **/  
  
public class test1 {  
	
	
    // test main  
    public static void main(String[] args) throws Exception {  
        String userdir = System.getProperty("user.dir");  
        File tempFile = new File("d:", "temp.png");  
        test1 capture = test1.getInstance();  
        capture.captureImage();  
        JFrame frame = new JFrame();  
        JPanel panel = new JPanel();  
        panel.setLayout(new BorderLayout());  
        JLabel imagebox = new JLabel();  
        panel.add(BorderLayout.CENTER, imagebox);  
        imagebox.setIcon(capture.getPickedIcon());  
        capture.saveToFile(tempFile);  
        capture.captureImage();  
        imagebox.setIcon(capture.getPickedIcon());  
        frame.setContentPane(panel);  
        frame.setSize(400, 300);  
        frame.show();  
        System.out.println("Over");  
    }  
  
    private test1() {  
  
        try {  
            robot = new Robot();  
        } catch (AWTException e) {  
            System.err.println("Internal Error: " + e);  
            e.printStackTrace();  
        }  
        JPanel cp = (JPanel) dialog.getContentPane();  
        cp.setLayout(new BorderLayout());  
        
        labFullScreenImage.addMouseListener(new MouseAdapter() {  
            public void mouseReleased(MouseEvent evn) {  
                isFirstPoint = true;  
                pickedImage = fullScreenImage.getSubimage(recX, recY, recW,  
                        recH);  
                dialog.setVisible(false);  
            }  
        });  
  
        labFullScreenImage.addMouseMotionListener(new MouseMotionAdapter() {  
            public void mouseDragged(MouseEvent evn) {  
                if (isFirstPoint) {  
                    x1 = evn.getX();  
                    y1 = evn.getY();  
                    isFirstPoint = false;  
                } else {  
                    x2 = evn.getX();  
                    y2 = evn.getY();  
                    int maxX = Math.max(x1, x2);  
                    int maxY = Math.max(y1, y2);  
                    int minX = Math.min(x1, x2);  
                    int minY = Math.min(y1, y2);  
                    recX = minX;  
                    recY = minY;  
                    recW = maxX - minX;  
                    recH = maxY - minY;  
                    labFullScreenImage.drawRectangle(recX, recY, recW, recH);  
                }  
            }  
  
            public void mouseMoved(MouseEvent e) {  
                labFullScreenImage.drawCross(e.getX(), e.getY());  
            }  
        });  
  
        cp.add(BorderLayout.CENTER, labFullScreenImage);  
        dialog.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));  
        dialog.setAlwaysOnTop(true);  
        dialog.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());  
        dialog.setUndecorated(true);  
        dialog.setSize(dialog.getMaximumSize());  
        dialog.setModal(true);  
    }  
  
    // Singleton Pattern  
    public static test1 getInstance() {  
        return defaultCapturer;  
    }  
  
    /** ��׽ȫ��Ľ */  
    public Icon captureFullScreen() {  
        fullScreenImage = robot.createScreenCapture(new Rectangle(Toolkit  
                .getDefaultToolkit().getScreenSize()));  
        ImageIcon icon = new ImageIcon(fullScreenImage);  
        return icon;  
    }  
  
    /** ��׽��Ļ��һ���������� */  
    public void captureImage() {  
        fullScreenImage = robot.createScreenCapture(new Rectangle(Toolkit  
                .getDefaultToolkit().getScreenSize()));  
        ImageIcon icon = new ImageIcon(fullScreenImage);  
        labFullScreenImage.setIcon(icon);  
        dialog.setVisible(true);  
    }  
  
    /** �õ���׽���BufferedImage */  
    public BufferedImage getPickedImage() {  
        return pickedImage;  
    }  
  
    /** �õ���׽���Icon */  
    public ImageIcon getPickedIcon() {  
        return new ImageIcon(getPickedImage());  
    }  
  
    /** 
     * ����Ϊһ���ļ�,ΪPNG��ʽ 
     *  
     * @deprecated replaced by saveAsPNG(File file) 
     **/  
    @Deprecated  
    public void saveToFile(File file) throws IOException {  
        ImageIO.write(getPickedImage(), defaultImageFormater, file);  
    }  
  
    /** ����Ϊһ���ļ�,ΪPNG��ʽ */  
    public void saveAsPNG(File file) throws IOException {  
        ImageIO.write(getPickedImage(), "png", file);  
    }  
  
    /** ����Ϊһ��JPEG��ʽͼ���ļ� */  
    public void saveAsJPEG(File file) throws IOException {  
        ImageIO.write(getPickedImage(), "JPEG", file);  
    }  
  
    /** д��һ��OutputStream */  
    public void write(OutputStream out) throws IOException {  
        ImageIO.write(getPickedImage(), defaultImageFormater, out);  
    }  
  
    // singleton design pattern  
    private static test1 defaultCapturer = new test1();  
    private int x1, y1, x2, y2;  
    private int recX, recY, recH, recW; // ��ȡ��ͼ��  
    private boolean isFirstPoint = true;  
    private BackgroundImage labFullScreenImage = new BackgroundImage();  
    private Robot robot;  
    private BufferedImage fullScreenImage;  
    private BufferedImage pickedImage;  
    private String defaultImageFormater = "png";  
    private JDialog dialog = new JDialog();  
}  
  
/** ��ʾͼƬ��Label */  
class BackgroundImage extends JLabel {  
    public void paintComponent(Graphics g) {  
        super.paintComponent(g);  
        g.drawRect(x, y, w, h);  
        String area = Integer.toString(w) + " * " + Integer.toString(h);  
        g.drawString(area, x + (int) w / 2 - 15, y + (int) h / 2);  
        g.drawLine(lineX, 0, lineX, getHeight());  
  
        g.drawLine(0, lineY, getWidth(), lineY);  
    }  
  
    public void drawRectangle(int x, int y, int width, int height) {  
        this.x = x;  
        this.y = y;  
        h = height;  
        w = width;  
        repaint();  
    }  
  
    public void drawCross(int x, int y) {  
        lineX = x;  
        lineY = y;  
        repaint();  
    }  
  
    int lineX, lineY;  
    int x, y, h, w;  
}  