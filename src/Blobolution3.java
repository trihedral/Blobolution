/**
* MIT License (see LICENSE.md)
* Copyright (c) 2018 Kyle D. Rocha-Brownell
**/

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Blobolution3 extends JFrame {
    private int timeInc = 25;   // Milliseconds between timer events. Ideal: 17
    private Timer timer;
    private double winX, winY;
    private double oldEventTime;
    private JMenuBar menuBar;
    private JMenuItem newBlobItem;
    //private JMenuItem loadSimItem;
    //private JMenuItem saveSimItem;
    private JRadioButton playRadioButton;
    private ArrayList<Blob> blobs = new ArrayList<Blob>();
    private ArrayList<BlobDataFrame> dataFrames = new ArrayList<BlobDataFrame>();
    Integer pop = 0;
    private boolean paused = true;
    private boolean visible = true;
    private Blobolution3 frame;
    private ArrayList<Double> dataList = new ArrayList<Double>();
    private BlobSorterFrame blobSorterFrame;
    int totalFrames = 0;
    private BlobCreatorFrame blobCreatorFrame;

    Blobolution3() {
        super("ANN Environment");
        setUIFont(new javax.swing.plaf.FontUIResource("",Font.PLAIN,24));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setAlwaysOnTop(false);
        winX = screenSize.getWidth();
        winY = screenSize.getHeight();
        setSize((int) winX / 3, (int) winY / 4);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation( (int)(screenSize.getWidth()  - getSize().getWidth()/2), 0 );
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        PaintPanel gPanel = new PaintPanel();
        gPanel.addMouseListener(new BlobMouseListener());
        gPanel.addMouseMotionListener(new BlobMouseMotionListener());
        add(gPanel);
        buildMenuBar();
        setJMenuBar(menuBar);
        setVisible(true);
        frame = this;

        dataList.add(0.0);
        dataList.add(0.0);
        dataList.add(0.0);

        timer = new Timer(timeInc, new TimerListen());

        quickTest();
    }

    private class PaintPanel extends JPanel {
        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            BufferedImage image = // Create an off-screen image
                    new BufferedImage((int)winX, (int)winY, BufferedImage.TYPE_INT_RGB);
            Graphics2D ig = image.createGraphics(); // Get its Graphics for drawing

            if (visible) {
                // Set the background to a gradient fill
                ig.setPaint(new GradientPaint(0, 0, Color.black,
                        (float) winX, (float) winY, new Color(87, 106, 115)));
                ig.fillRect(0, 0, (int) winX, (int) winY);

                // Set drawing attributes for the foreground
                ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                        RenderingHints.VALUE_ANTIALIAS_ON);
            }

            // Draw all Blobs //
            for (Blob B : blobs) {
                ig.setColor(B.color);
                ig.fillOval(
                        (int) (B.x - B.size / 2),
                        (int) (B.y - B.size / 2),
                        B.size, B.size);
                if (B.marked) {
                    ig.setColor(Color.WHITE);
                    ig.drawOval(
                            (int) (B.x - B.size),
                            (int) (B.y - B.size),
                            B.size * 2, B.size * 2);
                    ig.drawLine(
                            (int) (B.x - B.size), (int) (B.y),
                            (int) (B.x + B.size), (int) (B.y));
                    ig.drawLine(
                            (int) (B.x), (int) (B.y - B.size),
                            (int) (B.x), (int) (B.y + B.size));
                }
            }


            // Draw the image on to the Graphics panel
            g.drawImage(image, 0, 0, this);
        }
    }

    private void buildMenuBar(){
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu simMenu = new JMenu("Simulation");
        menuBar.add(fileMenu);
        menuBar.add(simMenu);
        newBlobItem = new JMenuItem("New Blob(s)...");
        newBlobItem.addActionListener(new NewBlobItemListener());
        fileMenu.add(newBlobItem);
        JMenuItem loadBlobItem = new JMenuItem("Load Blob...");
        loadBlobItem.addActionListener(new LoadBlobItemListener());
        fileMenu.add(loadBlobItem);
        /*loadSimItem = new JMenuItem("Load Sim...");
        loadSimItem.addActionListener(new LoadSimItemListener());
        fileMenu.add(loadSimItem);
        saveSimItem = new JMenuItem("Save Sim...");
        saveSimItem.addActionListener(new SaveSimItemListener());
        fileMenu.add(saveSimItem);*/

        playRadioButton = new JRadioButton("Run");
        playRadioButton.setSelected(false);
        playRadioButton.addActionListener(new PlayRadioListener());
        simMenu.add(playRadioButton);

        JRadioButton visibleRadioButton = new JRadioButton("Real-time");
        visibleRadioButton.setSelected(true);
        visibleRadioButton.addActionListener(new VisibleRadioListener());
        simMenu.add(visibleRadioButton);

        JMenuItem detailsItem = new JMenuItem("View details");
        detailsItem.addActionListener(new DetailsItemListener());
        simMenu.add(detailsItem);

        JMenuItem clearItem = new JMenuItem("Clear");
        clearItem.addActionListener(new ClearItemListener());
        simMenu.add(clearItem);

    }

    private class PlayRadioListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int minBlobs = 0;
            for (Blob B : blobs)
                if (B.maxPerceivable > minBlobs) minBlobs = B.maxPerceivable;
            minBlobs += 1;
            if (blobs.size() < minBlobs){
                playRadioButton.setSelected(false);
                String message = "Please add at least " + (minBlobs) + " blob";
                if (minBlobs > 1) message += "s";
                JOptionPane.showMessageDialog(frame,
                        message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (paused) {
                paused = false;
                timer.start();
            }
            else {
                paused = true;
                timer.stop();
            }
        }
    }

    private class VisibleRadioListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (visible){
                visible = false;
                timer.stop();
                timer = new Timer(0, new TimerListen());
                if (!paused) timer.start();
                //setAlwaysOnTop(true);
            }
            else {
                visible = true;
                timer.stop();
                timer = new Timer(timeInc, new TimerListen());
                if (!paused) timer.start();
                //setAlwaysOnTop(false);
            }
        }
    }

    private class NewBlobItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            blobCreatorFrame = new BlobCreatorFrame(new Blob(1, getWidth(), getHeight(), blobs), frame);
        }
    }

    private class LoadBlobItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setPreferredSize(new Dimension(1000, 700));
            fileChooser.setDialogTitle("Load Blob");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Blob Files", "blob");
            fileChooser.setFileFilter(filter);
            //fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                Blob b = new Blob(fileChooser.getSelectedFile());
                b.health = b.birthHealth;
                b.age = 0;
                b.numKids = 0;
                b.marked = true;
                blobs.add(b);
                repaint();
            }
        }
    }

    /*
    private class LoadSimItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Load Simulation");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Simulation Files", "bsim");
            fileChooser.setFileFilter(filter);
            //fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {

            }
        }
    }

    private class SaveSimItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Simulation");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Simulation Files", "bsim");
            fileChooser.setFileFilter(filter);
            //fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                // Create a buffer for reading the files
                byte[] buf = new byte[1024];
                int bNum = 0;

                try {
                    // Create the ZIP file
                    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fileChooser.getSelectedFile()));

                    // Compress the files
                    for (Blob B : blobs) {
                        File bFile = new File("Blob"+String.valueOf(bNum)+".blob");
                        B.save(bFile);
                        FileInputStream in = new FileInputStream(bFile);

                        // Add ZIP entry to output stream.
                        out.putNextEntry(new ZipEntry("Blob"+String.valueOf(bNum)+".blob"));

                        // Transfer bytes from the file to the ZIP file
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        // Complete the entry
                        out.closeEntry();
                        in.close();
                        bNum ++;
                    }

                    // Complete the ZIP file
                    out.close();
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        }
    }
    */

    private class ClearItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (playRadioButton.isSelected()) // stop simulation if it's running
                playRadioButton.doClick();
            blobs.clear();
            pop = 0;
            repaint();
        }
    }

    /**
     * Called when Simulation->View Details is clicked
     *
     */
    private class DetailsItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (blobSorterFrame == null || !blobSorterFrame.isVisible())
                blobSorterFrame = new BlobSorterFrame(blobs, dataFrames, frame);
            else
                blobSorterFrame.setState(java.awt.Frame.NORMAL);
            blobSorterFrame.toFront();
        }
    }

    /**
     * Called by JVM incrementally
     *
     */
    private class TimerListen implements ActionListener {
        //////EVERY TIME ONE timeInc GOES BY...///////
        public void actionPerformed(ActionEvent e) {
            int num = blobs.size();
            if (!paused) {
                for (int i = 0; i < num; i++) {
                    blobs = blobs.get(i).live(blobs);// tell each blob to do stuff for this instant (pass them the blobs list)
                    num = blobs.size();
                }
            }

            // every 5 seconds... //
            int seconds = 5;
            if (e.getWhen() - oldEventTime > seconds*1000) {

                while (num < pop){
                    Random rand = new Random();
                    Blob tB = blobs.get(rand.nextInt(num));
                    Blob blob = new Blob(tB);
                    blob.color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
                    //blob.brain.vary(.99);
                    blob.health = blob.birthHealth;
                    blob.age = 0;
                    blob.numKids = 0;
                    blobs.add(blob);
                    num = blobs.size();
                }

                if (!visible) repaint();
                oldEventTime = e.getWhen();
            }
            totalFrames++;
            if (visible) repaint();
        }
    }//TimerListen end

    /**
     * Called by JVM when mouse is clicked
     */
    private class BlobMouseListener implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            for (int i = blobs.size()-1; i>=0; i--) {
                Blob B = blobs.get(i);
                double dist = Math.sqrt(Math.pow(x - B.x, 2) + Math.pow(y - B.y, 2));
                if (dist <= B.size / 2) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        B.grabbed = true;
                        hideCursor(true);
                        break; // grab only top blob if overlapped
                    }
                    else if (e.getButton() == MouseEvent.BUTTON3) {
                        dataFrames.add(new BlobDataFrame(B, dataFrames, frame));
                    }
                    else if (e.getButton() == MouseEvent.BUTTON2) {
                        B.marked = !B.marked;
                    }
                }
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                for (Blob B : blobs) {
                    B.grabbed = false;
                }
                hideCursor(false);
            }
        }
    }

    /**
     * Called by JVM when mouse moves
     */
    private class BlobMouseMotionListener implements MouseMotionListener {
        public void mouseMoved(MouseEvent e) {}
        public void mouseDragged(MouseEvent e) {
            for (Blob B : blobs){
                if (B.grabbed) {
                    B.x = e.getX();
                    B.y = e.getY();
                    if (paused) repaint();
                }
            }
        }
    }

    /**
     * Hides or unhides the cursor
     * @param hide whether or not the cursor should be hidden
     */
    private void hideCursor(boolean hide){
        if (hide) {
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImg, new Point(0, 0), "blank cursor");
            getContentPane().setCursor(blankCursor);
        }
        else{
            getContentPane().setCursor(Cursor.getDefaultCursor());
        }
    }

    private static void setUIFont(javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }

    }

    public static void main(String[] args) {
        new Blobolution3();
    }

    private void quickTest(){
        newBlobItem.doClick();
        try {
            Thread.sleep(100);
        }catch (Exception ignored){

        }
        blobCreatorFrame.numArea.setText("300");
        blobCreatorFrame.randBox.doClick();
        //blobCreatorFrame.addButton.doClick();

        //playRadioButton.doClick();
    }

}//class end