import javafx.scene.shape.Line;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by kyled_000 on 11/12/2015.
 */
public class BlobDataFrame extends JFrame{

    private PaintPanel gPanel = new PaintPanel();
    private int timeInc = 17;   // Milliseconds between timer events
    Timer timer = new Timer(timeInc, new TimerListen());
    private int winW, winH, dyIn, dyHid, dyOut, dxN;
    private int bRad;
    Blob blob;
    BlobDataFrame thisFrame;
    ArrayList<BlobDataFrame> dataFrames;
    JLabel[] inputLabels;
    JLabel[] outputLabels;
    JLabel dataLabel;
    JButton breedButton;
    JButton saveButton;
    JButton copyButton;
    JLabel weightLabel;
    double maxWeight;
    int boxL, boxY, boxX, nSize;
    int mouseX = 0, mouseY = 0;
    boolean markState;
    ArrayList<WeightLine> lines = new ArrayList<WeightLine>();
    ArrayList<Point> ovals = new ArrayList<Point>();
    Locale locale  = new Locale("en", "UK");
    String pattern = "###.###";
    DecimalFormat decimalFormat;
    Blobolution3 mainFrame;

    public BlobDataFrame(Blob blob, ArrayList<BlobDataFrame> dataFrames, Blobolution3 mainFrame){
        super("Blob Data");
        thisFrame = this;
        this.mainFrame = mainFrame;
        this.blob = blob;
        markState = blob.marked;
        blob.marked = true;
        this.dataFrames = dataFrames;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int wW = (int)(screenSize.getWidth() * .32);
        int wH = (int) (screenSize.getHeight() * .45);
        setSize(wW, wH);
        setLayout(new BorderLayout());
        setResizable(true);
        setVisible(true);
        setAlwaysOnTop(true);
        determineLocation(wW, wH);
        gPanel.addMouseMotionListener(new BlobMouseMotionListener());

        decimalFormat = (DecimalFormat)
                NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);

        add(gPanel);
        timer.start();

        breedButton = new JButton("Breed");
        breedButton.addActionListener(new BreedButtonListener());
        breedButton.setFocusPainted(false);
        gPanel.add(breedButton);

        saveButton = new JButton("Save");
        saveButton.addActionListener(new SaveButtonListener());
        saveButton.setFocusPainted(false);
        gPanel.add(saveButton);

        copyButton = new JButton("Copy & Edit");
        copyButton.addActionListener(new CopyButtonListener());
        copyButton.setFocusPainted(false);
        gPanel.add(copyButton);

        dataLabel = new JLabel();
        dataLabel.setVerticalAlignment(JLabel.TOP);
        gPanel.add(dataLabel);

        weightLabel = new JLabel();
        weightLabel.setForeground(Color.WHITE);
        gPanel.add(weightLabel);

        inputLabels = new JLabel[blob.brain.inputNeurons.length];
        for (int i=0; i<inputLabels.length; i++){
            inputLabels[i] = new JLabel();
            gPanel.add(inputLabels[i]);
        }
        outputLabels = new JLabel[blob.brain.outputNeurons.length];
        for (int i=0; i<outputLabels.length; i++){
            outputLabels[i] = new JLabel();
            gPanel.add(outputLabels[i]);
            outputLabels[i].setForeground(Color.WHITE);
        }
        bRad = 8;

        addWindowListener(new BlobWindowAdapter());
        addComponentListener(new BlobComponentAdapter());
    }

    private class BlobWindowAdapter extends WindowAdapter {
        @Override
        public void windowClosing(java.awt.event.WindowEvent wE) {
            blob.marked = markState;
            dataFrames.remove(thisFrame);
        }
    }

    private class BlobComponentAdapter extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            winH = gPanel.getHeight();
            winW = gPanel.getWidth();
            boxL = 200;
            boxY = winH - boxL;
            boxX = winW - boxL;
            nSize = 10;  // diameter of neurons
            dyIn = winH / (blob.brain.inputNeurons.length + 1);
            dyHid = winH / (blob.brain.hiddenRows + 1);
            dyOut = winH / (blob.brain.outputNeurons.length + 1);
            dxN = boxX / (2 + blob.brain.hiddenColumns + 1);

            ovals.clear();;
            lines.clear();

            // INPUT NEURONS //
                // draw input neurons
            for (int i = 0; i < blob.brain.inputNeurons.length; i++) {
                ovals.add(new Point(dxN, (i + 1) * dyIn));
                lines.add(new WeightLine(
                        dxN + nSize/2, (i + 1) * dyIn + nSize/2 - 15,
                        dxN + nSize/2, (i + 1) * dyIn + nSize/2 ,
                        blob.brain.inputNeurons[i].weights[ blob.brain.inputNeurons[i].weights.length-1 ]
                        ));
            }

            // HIDDEN NEURONS //
            for (int c = 0; c < blob.brain.hiddenColumns; c++) {
                for (int r = 0; r < blob.brain.hiddenRows; r++) {
                    // lines from prev input layers //
                    if (c == 0){
                        for (int j = 0; j < blob.brain.inputNeurons.length; j++) {
                            lines.add(new WeightLine(
                                    dxN + nSize/2, (j + 1) * dyIn + nSize/2,
                                    (c + 2) * dxN + nSize/2, (r + 1) * dyHid + nSize/2,
                                    blob.brain.hiddenNeurons[r][c].weights[j]
                                    ));
                        }
                    }
                    // lines from prev hidden layer //
                    else {
                        for (int i = 0; i < blob.brain.hiddenRows; i++) {
                            lines.add(new WeightLine(
                                    (c + 1) * dxN + nSize/2, (i + 1) * dyHid + nSize/2,
                                    (c + 2) * dxN + nSize/2, (r + 1) * dyHid + nSize/2,
                                    blob.brain.hiddenNeurons[r][c].weights[i]
                                    ));
                        }
                    }
                    // add dummy weight //
                    lines.add(new WeightLine(
                            (c + 2) * dxN + nSize/2, (r + 1) * dyHid + nSize/2 - 15,
                            (c + 2) * dxN + nSize/2, (r + 1) * dyHid + nSize/2,
                            blob.brain.hiddenNeurons[r][c].weights[ blob.brain.hiddenNeurons[r][c].weights.length-1 ]
                            ));

                    ovals.add( new Point((c + 2) * dxN, (r + 1) * dyHid));
                }
            }

            // OUTPUT NEURONS //
            for (int i = 0; i < blob.brain.outputNeurons.length; i++) {
                // lines from prev hidden layer //
                for (int j = 0; j < blob.brain.hiddenRows; j++) {
                    lines.add(new WeightLine(
                            (1 + blob.brain.hiddenColumns) * dxN + nSize/2,
                            (j + 1) * dyHid + nSize/2,
                            (2 + blob.brain.hiddenColumns) * dxN + nSize/2,
                            (i + 1) * dyOut + nSize/2,
                            blob.brain.outputNeurons[i].weights[j]
                            ));
                }

                // add dummy weight //
                lines.add(new WeightLine(
                        (2 + blob.brain.hiddenColumns) * dxN + nSize/2, (i + 1) * dyOut + nSize/2 - 15,
                        (2 + blob.brain.hiddenColumns) * dxN + nSize/2, (i + 1) * dyOut + nSize/2 ,
                        blob.brain.outputNeurons[i].weights[blob.brain.outputNeurons[i].weights.length-1]
                        ));

                // neurons //
                ovals.add(new Point((2 + blob.brain.hiddenColumns) * dxN, (i + 1) * dyOut));
            }

            // calc maxWeight //
            maxWeight = 0;
            for (WeightLine L : lines){
                if (Math.abs(L.weight) > maxWeight){
                    maxWeight = Math.abs(L.weight);
                }
            }

        }
    }

    class PaintPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.DARK_GRAY);

            // If blob is alive, draw it and it's nearBlobs //
            // draw grid //
            g.setColor(Color.BLACK);
            g.fillRect(boxX, boxY, boxL - 5, boxL - 5);
            for (int i = 1; i < 6; i++) {
                if (i == 3) g.setColor(new Color(125, 125, 0));
                else g.setColor(new Color(50, 50, 0));
                g.drawLine(boxX + i * boxL / 6, boxY, boxX + i * boxL / 6, winH - 5);
                g.drawLine(boxX, boxY + i * boxL / 6, winW - 5, boxY + i * boxL / 6);
            }

            if (blob.health > 0 && blob.age > 0) {
                // Draw nearBlobs //
                for (Blob B : blob.nearBlobs) {
                    boolean far = false;
                    int dx = (int) (blob.xTo(B) * 2 * bRad / blob.size);
                    int dy = (int) (blob.yTo(B) * 2 * bRad / blob.size);
                    if (Math.abs(dx) > (boxL / 2 - bRad)) {
                        dx = (int) Math.signum(dx) * boxL / 2;
                        far = true;
                    }
                    if (Math.abs(dy) > boxL / 2) {
                        dy = (int) Math.signum(dy) * boxL / 2;
                        far = true;
                    }
                    g.setColor(B.color);
                    if (far) {
                        g.fillRect(
                                boxX + boxL / 2 - bRad + dx,
                                boxY + boxL / 2 - bRad + dy,
                                2 * bRad, 2 * bRad);
                    } else {
                        g.fillOval(
                                boxX + boxL / 2 - bRad + dx,
                                boxY + boxL / 2 - bRad + dy,
                                2 * bRad, 2 * bRad);
                    }

                    g.setColor(new Color(1f, 0f, 0f, 1f));
                    if (blob.nutritionRatio(B) > B.nutritionRatio(blob))
                        g.setColor(new Color(0f, 1f, 0f, 1f));
                    else if (blob.nutritionRatio(B) == B.nutritionRatio(blob)) g.setColor(Color.WHITE);
                    g.drawOval(
                            boxX + boxL / 2 - bRad * 3 / 2 + dx,
                            boxY + boxL / 2 - bRad * 3 / 2 + dy,
                            3 * bRad, 3 * bRad);
                }
                // Draw Velocity Vector //
                g.setColor(Color.WHITE);
                g.drawLine(
                        boxX + boxL / 2, boxY + boxL / 2,
                        boxX + boxL / 2 + (int) (blob.vx * bRad * 2.5),
                        boxY + boxL / 2 + (int) (blob.vy * bRad * 2.5));
                // Draw Blob //
                g.setColor(blob.color);
                g.fillOval(
                        boxX + boxL / 2 - bRad, boxY + boxL / 2 - bRad,
                        2 * bRad, 2 * bRad);

            }
            g.setColor(Color.YELLOW);
            g.drawRect(boxX, boxY, boxL - 5, boxL - 5);


            // Draw the blob's brain //

            g.setColor(Color.BLACK);
            g.fillRect(5, 5, winW - boxL - 10, winH - 10);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(5, 5, winW - boxL - 10, winH - 10);
            // INPUT NEURONS //
            // draw input values
            if (blob.age > 0){

                for (int i = 0; i < blob.maxPerceivable * 5; i++) {
                    switch (i % 5) {
                        case 0:
                            inputLabels[i].setText("" + (int) (180 / Math.PI * Math.atan2(
                                    blob.yTo(blob.nearBlobs[(i - 1) / 5]),
                                    blob.xTo(blob.nearBlobs[(i - 1) / 5])))
                            );
                            inputLabels[i].setForeground(Color.WHITE);
                            break;
                        case 1:
                            inputLabels[i].setText("" + (int) Math.sqrt(
                                    Math.pow(blob.xTo(blob.nearBlobs[(i - 1) / 5]), 2) +
                                            Math.pow(blob.yTo(blob.nearBlobs[(i - 1) / 5]), 2))
                            );
                            inputLabels[i].setForeground(Color.WHITE);
                            break;
                        case 2:
                            inputLabels[i].setText("" + blob.nearBlobs[(i - 2) / 5].color.getRed());
                            inputLabels[i].setForeground(Color.RED);
                            break;
                        case 3:
                            inputLabels[i].setText("" + blob.nearBlobs[(i - 3) / 5].color.getGreen());
                            inputLabels[i].setForeground(Color.GREEN);
                            break;
                        case 4:
                            inputLabels[i].setText("" + blob.nearBlobs[(i - 4) / 5].color.getBlue());
                            inputLabels[i].setForeground(Color.BLUE);
                            break;
                    }
                    inputLabels[i].setBounds(dxN / 2, (i + 1) * dyIn - bRad, dxN, 20);
                }
            }
            for (int i = blob.maxPerceivable*5; i < blob.brain.inputNeurons.length; i++) {
                switch (i % 3) {
                    case 0:
                        inputLabels[i].setText("" + blob.color.getRed());
                        inputLabels[i].setForeground(Color.RED);
                        break;
                    case 1:
                        inputLabels[i].setText("" + blob.color.getGreen());
                        inputLabels[i].setForeground(Color.GREEN);
                        break;
                    case 2:
                        inputLabels[i].setText("" + blob.color.getBlue());
                        inputLabels[i].setForeground(Color.BLUE);
                        break;
                }
                inputLabels[i].setBounds(dxN/2, (i + 1) * dyIn - bRad, dxN, 20);
            }



            for (WeightLine L : lines) {
                L.draw(g, maxWeight);
            }
            g.setColor(Color.WHITE);
            for (Point p : ovals){
                g.fillOval(p.x, p.y, nSize, nSize);
            }


            for (int i=0; i< outputLabels.length; i++) {
                outputLabels[i].setBounds(
                        (int) ((2.25 + blob.brain.hiddenColumns) * dxN), (i + 1) * dyOut - bRad,
                        (3 + blob.brain.hiddenColumns) * dxN, 20);
            }


            double angle = Math.atan2(blob.vy,blob.vx)*360;
            outputLabels[0].setText("" + (int)(angle*100)/100.0);

            // DRAW CURRENT BLOB STATS
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(boxX, 5, boxL - 5, winH - boxL - 10);
            g.setColor(Color.WHITE);
            g.drawRect(boxX, 5, boxL - 5, winH - boxL - 10);
            dataLabel.setForeground(Color.BLACK);
            dataLabel.setBounds(boxX+10, 10, boxL, winH-boxL);
            dataLabel.setText("<html>" +
                    "<b>Age: </b>" + blob.age/100 + "<br />" +
                    "<b>Health: </b>" + (int)(blob.health*1000)/1000.0 + "<br />" +
                    "<b>Generation: </b>" + blob.generation +  "<br />" +
                    "<b>Red: </b><b style='color:red'>" + blob.color.getRed() + "</b><br />" +
                    "<b>Green: </b><b style='color:lime'>" + blob.color.getGreen() + "</b><br />" +
                    "<b>Blue: </b><b style='color:blue'>" + blob.color.getBlue() + "</b><br />" +
                    "<b>Perceivable Blobs: </b>" + blob.maxPerceivable +  "<br />" +
                    "<b>Brain Variance: </b>" + blob.brainVar + "<br />" +
                    "<b>Color Variance: </b>" + blob.colorVar + "<br />" +
                    "<b>Offspring: </b>" + blob.numKids + "<br />" +
                    "<b>Brain Type: </b>" + blob.brain.functionType + "<br />" +
                    "</html>");

            // DRAW BUTTONS
            breedButton.setLocation(boxX + 10, winH-boxL-20-breedButton.getHeight());
            saveButton.setLocation(boxX + breedButton.getWidth() + 20, winH-boxL-20-breedButton.getHeight());
            copyButton.setLocation(boxX + 10, winH-boxL-25-2*breedButton.getHeight());

            weightLabel.setBounds(mouseX + 20, mouseY,100,20);
        }

    } // PaintPanel end

    private void determineLocation(int wW, int wH){
        int currentLayers = 0;
        int layers;

        for (BlobDataFrame F : dataFrames)
            if (F.getX() == 0 && F.getY() == 0) currentLayers++;

        for (int j = 0; j < 2; j++){
            for(int i = 0; i < 3; i++){
                layers = 0;
                for (BlobDataFrame F : dataFrames){
                    if (F.getX() == i*wW && F.getY() == j*wH){
                        layers++;
                    }
                }

                if (layers < currentLayers) {
                    setLocation(i * wW, j * wH);
                    return;
                }
            }
        }
    }

    private class BreedButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            blob.health = 2*blob.birthHealth;
        }
    }

    private class CopyButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new BlobCreator(blob, mainFrame);
        }
    }

    private class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Blob");
            fileChooser.setApproveButtonText("Save");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Blob Files", "blob");
            fileChooser.setFileFilter(filter);
            //fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(mainFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                blob.save(file);
            }
        }
    }

    private class TimerListen implements ActionListener
    {
        //////EVERY TIME ONE timeInc GOES BY...///////
        public void actionPerformed(ActionEvent e)
        {
            repaint();
        }
    }//TimerListen end

    public static void main (String[] args){
        new Blobolution3();
    }


    private class BlobMouseMotionListener implements MouseMotionListener {

        public void mouseMoved(MouseEvent e) {
            double x0, y0, xf, yf, distToLine = Double.POSITIVE_INFINITY;
            int closeIndex = 0;
            for (int L = 0; L < lines.size(); L++){
                lines.get(L).highlight = false;
                if (lines.get(L).length == 0) continue;
                x0 = e.getX()-lines.get(L).x0; y0 = e.getY()-lines.get(L).y0;
                xf = e.getX()-lines.get(L).xf; yf = e.getY()-lines.get(L).yf;
                if (Math.abs( (x0*yf)-(y0*xf) )/lines.get(L).length < distToLine &&
                        lines.get(L).isInBoundBox(e.getX(), e.getY()) )
                {
                    distToLine = Math.abs((x0 * yf) - (y0 * xf)) / lines.get(L).length;
                    closeIndex = L;
                }
                //System.out.println(c + " " + Math.abs( (x0*yf)-(y0*xf) )/c + " " + distToLine);
            }
            if (distToLine < 10) {
                lines.get(closeIndex).highlight = true;
                mouseX = e.getX();
                mouseY = e.getY();

                String format = decimalFormat.format(lines.get(closeIndex).weight);
                weightLabel.setText(format+"");
            }
            else {
                lines.get(closeIndex).highlight = false;
                weightLabel.setText("");
            }

        }

        public void mouseDragged(MouseEvent e) {
        }

    }

    private class WeightLine {
        int x0, y0, xf, yf;
        double weight, length;
        boolean highlight = false;
        WeightLine(int x0, int y0, int xf, int yf, double w){
            this.x0 = x0; this.xf = xf;
            this.y0 = y0; this.yf = yf;
            weight = w;
            length = Math.sqrt(Math.pow(xf-x0,2)+Math.pow(yf-y0,2));
        }

        public void draw(Graphics g, double maxWeight){
            float alpha = (float)(Math.signum(weight)*Math.pow(weight/maxWeight, 2));
            if (highlight) {
                if (alpha > 0) g.setColor(new Color(0, 1.0f, .6f));
                else g.setColor(new Color(1.0f, 0, .6f));
            }
            else {
                if (alpha > 0) g.setColor(new Color(0, 1.0f, 0, alpha));
                else g.setColor(new Color(1.0f, 0, 0, Math.abs(alpha)));
            }
            g.drawLine(x0, y0, xf, yf);
        }

        public boolean isInBoundBox(int x, int y){
            if ( xf == x0) {
                if (yf > y0) {
                    if ( y > yf || y < y0 ) return false;
                }
                else if (yf < y0) {
                    if ( y < yf || y > y0 ) return false;
                }
            }
            else if (xf > x0) {
                if ( x > xf || x < x0 ) return false;
            }
            else if (xf < x0) {
                if ( x < xf || x > x0 ) return false;
            }
            return true;
        }
    }

} // BlobDataFrame end

