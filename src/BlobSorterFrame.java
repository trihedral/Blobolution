import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

class BlobSorterFrame extends JFrame{
    private ArrayList<Blob> blobs;
    private ArrayList<BlobDataFrame> blobDataFrames;
    private JFrame frame = this;
    private Blobolution3 mainFrame;
    private static final int AGE = 0, HEALTH = 1, GENERATION = 2, RED = 3, GREEN = 4, BLUE = 5,
            MARKED = 6, PERCEIVABLE = 7,  VARIANCE = 8, OFFSPRING = 9, FUNCTION = 10;
    private String[] strings = {"Age", "Health", "Gen", "Red", "Green", "Blue",
            "Marked", "Perceives", "Variance", "Offspring", "Brain Type"};
    private int sortIndex = 0;
    private boolean ascend = false;
    private int columns = strings.length;
    private JButton[] buttons = new JButton[columns];
    private JTextArea[] textAreas = new JTextArea[columns];
    private JPanel[] panels = new JPanel[columns];
    private JLabel fpsLabel, popLabel, totHealthLabel;
    private JMenuBar menuBar;
    private int prevFrames = 0;
    private double oldEventTime;


    BlobSorterFrame(ArrayList<Blob> blobs, ArrayList<BlobDataFrame> blobDataFrames,
                    Blobolution3 mainFrame) {
        super("All Blob Details");
        this.mainFrame = mainFrame;
        this.blobDataFrames = blobDataFrames;
        this.blobs = blobs;
        setResizable(true);
        setVisible(true);
        setLayout(new GridLayout(1, columns));
        setAlwaysOnTop(true);
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        initComponents();
        pack();
        setSize(1500, (int)(mainFrame.getHeight()*.75));

        int timeInc = 100;
        Timer timer = new Timer(timeInc, new TimerListen());
        timer.start();
    }

    private void initComponents(){

        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new BoxLayout(textAreaPanel, BoxLayout.X_AXIS));

        for (int i = 0; i < columns; i++){

            // Init text areas //
            textAreas[i] = new JTextArea();
            textAreas[i].setEditable(false);
            textAreas[i].setBorder(BorderFactory.createBevelBorder(0));
            DefaultCaret caret = (DefaultCaret)textAreas[i].getCaret();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
            textAreas[i].addMouseListener(new TextMouseListener());

            // Init buttons //
            buttons[i] = new JButton("  " + strings[i] + "  ");
            buttons[i].addActionListener(new buttonListener());
            buttons[i].setBorder(null);

            // Init panels //
            panels[i] = new JPanel();
            panels[i].setLayout(new BoxLayout(panels[i], BoxLayout.Y_AXIS));
            panels[i].add(buttons[i]);
            panels[i].add(textAreas[i]);

            // Add panels to main frame //
            textAreaPanel.add(panels[i]);
        }
        buttons[0].setText("  " + strings[0] + " v"); // mark initial sort button
        JScrollPane scrollPane = new JScrollPane(textAreaPanel);
        scrollPane.setPreferredSize(new Dimension(600,600));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        fpsLabel = new JLabel();
        menuBar.add(fpsLabel);
        popLabel = new JLabel();
        menuBar.add(popLabel);
        totHealthLabel = new JLabel();
        menuBar.add(totHealthLabel);

        add(scrollPane);


    }
    private void updateTextAreas() {

        // Re-sort in desired order //
        if (ascend) {
            switch (sortIndex) {
                case AGE:
                    Collections.sort(blobs, Blob.sortByAgeUp);
                    break;
                case HEALTH:
                    Collections.sort(blobs, Blob.sortByHealthUp);
                    break;
                case GENERATION:
                    Collections.sort(blobs, Blob.sortByGenerationUp);
                    break;
                case RED:
                    Collections.sort(blobs, Blob.sortByRedUp);
                    break;
                case GREEN:
                    Collections.sort(blobs, Blob.sortByGreenUp);
                    break;
                case BLUE:
                    Collections.sort(blobs, Blob.sortByBlueUp);
                    break;
                case MARKED:
                    Collections.sort(blobs, Blob.sortByMarkedUp);
                    break;
                case PERCEIVABLE:
                    Collections.sort(blobs, Blob.sortByPerceivableUp);
                    break;
                case VARIANCE:
                    Collections.sort(blobs, Blob.sortByBrainVarUp);
                    break;
                case OFFSPRING:
                    Collections.sort(blobs, Blob.sortByKidsUp);
                    break;
                case FUNCTION:
                    Collections.sort(blobs, Blob.sortByFunctionUp);
                    break;
            }
        }
        else {
            switch (sortIndex) {
                case AGE:
                    Collections.sort(blobs, Blob.sortByAge);
                    break;
                case HEALTH:
                    Collections.sort(blobs, Blob.sortByHealth);
                    break;
                case GENERATION:
                    Collections.sort(blobs, Blob.sortByGeneration);
                    break;
                case RED:
                    Collections.sort(blobs, Blob.sortByRed);
                    break;
                case GREEN:
                    Collections.sort(blobs, Blob.sortByGreen);
                    break;
                case BLUE:
                    Collections.sort(blobs, Blob.sortByBlue);
                    break;
                case MARKED:
                    Collections.sort(blobs, Blob.sortByMarked);
                    break;
                case PERCEIVABLE:
                    Collections.sort(blobs, Blob.sortByPerceivable);
                    break;
                case VARIANCE:
                    Collections.sort(blobs, Blob.sortByBrainVar);
                    break;
                case OFFSPRING:
                    Collections.sort(blobs, Blob.sortByKids);
                    break;
                case FUNCTION:
                    Collections.sort(blobs, Blob.sortByFunction);
                    break;
            }
        }

        // Set Text //
        for (int i = 0; i < columns; i++) textAreas[i].setText(""); // clear previous order values
        for (Blob b : blobs) {
            textAreas[AGE].append(b.age/100 + " \n");
            textAreas[HEALTH].append((int) (b.health * 100) / 100.0 + " \n");
            textAreas[GENERATION].append(b.generation + " \n");
            textAreas[RED].append(b.color.getRed() + " \n");
            textAreas[GREEN].append(b.color.getGreen() + " \n");
            textAreas[BLUE].append(b.color.getBlue() + " \n");
            textAreas[MARKED].append(b.marked + " \n");
            textAreas[PERCEIVABLE].append(b.maxPerceivable + " \n");
            textAreas[VARIANCE].append(b.brainVar + " \n");
            textAreas[OFFSPRING].append(b.numKids + " \n");
            textAreas[FUNCTION].append(b.brain.functionType + " \n");
        }
    }

    private class TimerListen implements ActionListener {
        //////EVERY TIME ONE timeInc GOES BY...///////
        public void actionPerformed(ActionEvent e) {
            updateTextAreas();

            int seconds = 2;
            if (e.getWhen() - oldEventTime > seconds*1000) {
                int h=0;
                for (Blob B : blobs) h+= B.health;

                fpsLabel.setText("   FPS: " +
                        (int)((mainFrame.totalFrames-prevFrames) / (e.getWhen()-oldEventTime) * 1000) );
                popLabel.setText("    Population: " + blobs.size());
                totHealthLabel.setText("    Total Health: " + h);

                prevFrames = mainFrame.totalFrames;
                oldEventTime = e.getWhen();
            }
        }
    }

    private class buttonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            // Determined index of pressed button //
            int newSortIndex = -1;
            for (int i = 0; i < columns; i++){
                if (("  " + strings[i] + "  ").equals(((JButton) e.getSource()).getText())) newSortIndex = i;
            }
            if (newSortIndex == -1) {
                ascend = !ascend;
            }
            else {
                ascend = false;
                sortIndex = newSortIndex;
            }

            // Update all text //
            for (int i = 0; i < columns; i++) {
                buttons[i].setText("  " + strings[i] + "  ");
            }
            if (ascend)
                buttons[sortIndex].setText("  " + strings[sortIndex] + " ^");
            else
                buttons[sortIndex].setText("  " + strings[sortIndex] + " v");
            updateTextAreas();
        }
    }

    private class TextMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {
            int blobIndex = 0;
            try {
                for (int i = 0; i < columns; i++){
                    blobIndex = textAreas[i].getLineOfOffset(textAreas[i].getCaretPosition());
                    //System.out.println(blobIndex);
                    if (blobIndex > 0) break;
                }
                if (frame.isFocused()){
                    blobDataFrames.add(new BlobDataFrame(blobs.get(blobIndex), blobDataFrames, mainFrame));
                }
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {}
    }

}
