import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kyled_000 on 11/25/2015.
 */
public class BlobCreator extends JFrame{
    ArrayList<Blob> blobs;
    Blob oldBlob;
    PaintPanel gPanel = new PaintPanel();
    JSlider redSlider, greenSlider, blueSlider;
    Random rand = new Random();
    JPanel colorPanel, aNNPanel, buttonPanel, previewPanel;
    JLabel rLabel = new JLabel(), gLabel = new JLabel(), bLabel = new JLabel();
    JTextArea perceiveArea, hiddenRowsArea, hiddenColumnsArea, brainVarArea,
            colorVarArea, numArea, ageArea, sizeArea, speedArea, healthArea;
    JRadioButton expRadio, oneRadio, zeroRadio;
    JCheckBox randBox, markBox;
    Blobolution3 mainFrame;
    JButton addButton;

    public BlobCreator(Blob blob, Blobolution3 mainFrame) {
        super("Blob Creator");
        this.mainFrame = mainFrame;
        this.blobs = blob.blobs;
        oldBlob = blob;
        setup();

        if (blob.maxAge > 0) {
            ageArea.setText(blob.maxAge + "");
        }
        else ageArea.setText("-");
        sizeArea.setText(blob.size+"");
        speedArea.setText(blob.maxSpeed+"");
        healthArea.setText(blob.birthHealth+"");
        perceiveArea.setText(blob.maxPerceivable+"");
        hiddenRowsArea.setText(blob.brain.hiddenRows+"");
        hiddenColumnsArea.setText(blob.brain.hiddenColumns+"");
        brainVarArea.setText(blob.brainVar+"");
        colorVarArea.setText(blob.colorVar+"");
        switch(blob.brain.functionType) {
            case 0:
                expRadio.setSelected(true);
                break;
            case 1:
                oneRadio.setSelected(true);
                break;
            case 2:
                zeroRadio.setSelected(true);
        }
        redSlider.setValue(blob.color.getRed());
        greenSlider.setValue(blob.color.getGreen());
        blueSlider.setValue(blob.color.getBlue());
    }

    void setup(){
        setResizable(true);
        setVisible(true);
        setLayout(new GridLayout(1, 3));
        setAlwaysOnTop(true);

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        buildANNPanel();
        buildColorPanel();
        buildButtonPanel();
        panel1.setLayout(new GridLayout(1, 2));
        panel2.setLayout(new GridLayout(1, 2));

        add(aNNPanel);
        add(colorPanel);
        add(buttonPanel);
        setLocation(60, 60);

        pack();

    }

    void buildColorPanel(){
        JPanel  redPanel, greenPanel, bluePanel;
        JLabel redLabel, greenLabel, blueLabel;
        colorPanel = new JPanel();
        colorPanel.setLayout(new GridLayout(1,3));
        redPanel = new JPanel(); greenPanel = new JPanel(); bluePanel = new JPanel();
        redPanel.setLayout(new BoxLayout(redPanel, BoxLayout.Y_AXIS));
        greenPanel.setLayout(new BoxLayout(greenPanel, BoxLayout.Y_AXIS));
        bluePanel.setLayout(new BoxLayout(bluePanel, BoxLayout.Y_AXIS));
        colorPanel.setBorder(BorderFactory.createTitledBorder("Color"));
        redSlider   = new JSlider(JSlider.VERTICAL, 0, 255, rand.nextInt(255));
        redSlider.addChangeListener(new ColorChangeListener());
        greenSlider = new JSlider(JSlider.VERTICAL, 0, 255, rand.nextInt(255));
        greenSlider.addChangeListener(new ColorChangeListener());
        blueSlider  = new JSlider(JSlider.VERTICAL, 0, 255, rand.nextInt(255));
        blueSlider.addChangeListener(new ColorChangeListener());
        redLabel = new JLabel("R");
        greenLabel = new JLabel("G");
        blueLabel = new JLabel("B");
        redLabel.setForeground(Color.RED);
        greenLabel.setForeground(new Color(0,180,0));
        blueLabel.setForeground(Color.BLUE);
        redPanel.add(redSlider);
        redPanel.add(redLabel);
        greenPanel.add(greenSlider);
        greenPanel.add(greenLabel);
        bluePanel.add(blueSlider);
        bluePanel.add(blueLabel);
        colorPanel.add(redPanel);
        colorPanel.add(greenPanel);
        colorPanel.add(bluePanel);

    }

    void buildANNPanel(){
        aNNPanel = new JPanel();
        aNNPanel.setLayout(new GridLayout(3,1));


        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new GridLayout(3,2));
        generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
        JPanel generalPanel1 = new JPanel();
        JPanel generalPanel2 = new JPanel();
        JPanel generalPanel3 = new JPanel();
        JPanel generalPanel4 = new JPanel();
        JPanel generalPanel5 = new JPanel();
        JPanel generalPanel6 = new JPanel();
        generalPanel1.setBorder(new EmptyBorder(1, 1, 1, 1));
        generalPanel2.setBorder(new EmptyBorder(1, 1, 1, 1));
        generalPanel3.setBorder(new EmptyBorder(1, 1, 1, 1));
        generalPanel4.setBorder(new EmptyBorder(1, 1, 1, 1));
        generalPanel5.setBorder(new EmptyBorder(1, 1, 1, 1));
        generalPanel6.setBorder(new EmptyBorder(1, 1, 1, 1));
        ageArea = new JTextArea(1, 3);
        sizeArea = new JTextArea(1, 3);
        speedArea = new JTextArea(1, 3);
        healthArea = new JTextArea(1, 3);
        colorVarArea = new JTextArea(1, 3);
        generalPanel1.add(new JLabel("Max age: "));
        generalPanel1.add(ageArea);
        generalPanel2.add(new JLabel("Size: "));
        generalPanel2.add(sizeArea);
        generalPanel3.add(new JLabel("Speed: "));
        generalPanel3.add(speedArea);
        generalPanel4.add(new JLabel("Health: "));
        generalPanel4.add(healthArea);
        generalPanel5.add(new JLabel("Color vary: "));
        generalPanel5.add(colorVarArea);
        generalPanel.add(generalPanel1);
        generalPanel.add(generalPanel2);
        generalPanel.add(generalPanel3);
        generalPanel.add(generalPanel4);
        generalPanel.add(generalPanel5);

        ButtonGroup buttonGroup = new ButtonGroup();
        JPanel activationPanel = new JPanel();
        activationPanel.setLayout(new GridLayout(3,1));
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(4,1));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Brain"));
        JPanel detailsPanel1 = new JPanel();
        JPanel detailsPanel2 = new JPanel();
        JPanel detailsPanel3 = new JPanel();
        JPanel detailsPanel4 = new JPanel();
        perceiveArea = new JTextArea(1, 3);
        hiddenRowsArea = new JTextArea(1, 3);
        hiddenColumnsArea = new JTextArea(1, 3);
        brainVarArea = new JTextArea(1, 3);
        expRadio = new JRadioButton("1 / [ 1-exp(i) ]");
        oneRadio = new JRadioButton("i");
        zeroRadio = new JRadioButton(("0 (Brainless)"));
        activationPanel.setBorder(BorderFactory.createTitledBorder("Activation Function"));
        activationPanel.add(expRadio);
        activationPanel.add(oneRadio);
        activationPanel.add(zeroRadio);
        buttonGroup.add(expRadio);
        buttonGroup.add(oneRadio);
        buttonGroup.add(zeroRadio);

        detailsPanel1.add(new JLabel("Max Perceivable blobs  "));
        detailsPanel1.add(perceiveArea);
        detailsPanel1.setBorder(new EmptyBorder(1, 1, 1, 1));
        detailsPanel2.add(new JLabel("Hidden Neuron Rows     "));
        detailsPanel2.add(hiddenRowsArea);
        detailsPanel2.setBorder(new EmptyBorder(1, 1, 1, 1));
        detailsPanel3.add(new JLabel("Hidden Neuron Columns "));
        detailsPanel3.add(hiddenColumnsArea);
        detailsPanel3.setBorder(new EmptyBorder(1, 1, 1, 1));
        detailsPanel4.add(new JLabel("Child ANN Variance "));
        detailsPanel4.add(brainVarArea);
        detailsPanel4.setBorder(new EmptyBorder(1, 1, 1, 1));
        detailsPanel.add(detailsPanel1);
        detailsPanel.add(detailsPanel2);
        detailsPanel.add(detailsPanel3);
        detailsPanel.add(detailsPanel4);

        aNNPanel.add(generalPanel);
        aNNPanel.add(detailsPanel);
        aNNPanel.add(activationPanel);
    }

    void buildButtonPanel(){
        buttonPanel = new JPanel();
        JPanel bPanel = new JPanel();
        bPanel.setLayout(new GridLayout(4, 1));
        JPanel qPanel = new JPanel();
        qPanel.add(new Label("Quantity:"));
        numArea = new JTextArea("1", 1, 3);
        qPanel.add(numArea);
        bPanel.add(qPanel);
        randBox = new JCheckBox("Random brains & colors");
        bPanel.add(randBox);
        markBox = new JCheckBox("Mark added blobs");
        bPanel.add(markBox);
        addButton = new JButton("Add");
        addButton.addActionListener(new AddButtonListener());
        bPanel.add(addButton);

        buttonPanel.setLayout(new GridLayout(2, 1));
        buttonPanel.add(gPanel);
        buttonPanel.add(bPanel);
    }

    class PaintPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBorder(BorderFactory.createTitledBorder("Preview"));
            setLayout(null);
            int lWidth = 30;
            rLabel.setForeground(Color.RED);
            gLabel.setForeground(new Color(0,180,0));
            bLabel.setForeground(Color.BLUE);
            add(rLabel); add(gLabel); add(bLabel);
            g.setColor(new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue()));
            g.fillOval(this.getWidth() / 2 - 10, this.getHeight() / 4, 20, 20);

            //rLabel.setBounds(0,0,20,20);
            rLabel.setBounds(this.getWidth() / 2 - lWidth * 3/2, this.getHeight() / 2,
                    this.getWidth() / 2 - lWidth / 2, this.getHeight()/2);
            gLabel.setBounds(this.getWidth() / 2 - lWidth / 2, this.getHeight() / 2,
                    this.getWidth() / 2 + lWidth / 2, this.getHeight()/2);
            bLabel.setBounds(this.getWidth() / 2 + lWidth / 2, this.getHeight() / 2,
                    this.getWidth() / 2 + lWidth * 3/2, this.getHeight()/2);
            rLabel.setText(redSlider.getValue() + "");
            gLabel.setText(greenSlider.getValue()+"");
            bLabel.setText(blueSlider.getValue()+"");
        }
    }

    private class ColorChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            gPanel.repaint();
        }
    }

    private class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int num = Integer.parseInt(numArea.getText());
            for (int i = 0; i < num; i++) {
                int maxPerc = Integer.parseInt(perceiveArea.getText());
                Blob blob = new Blob(maxPerc, mainFrame.getWidth(), mainFrame.getHeight(), blobs);
                blob.brainVar = Double.parseDouble(brainVarArea.getText());
                blob.colorVar = Double.parseDouble(colorVarArea.getText());
                if (ageArea.getText().equals("-")){
                    blob.maxAge = 0; // immortal
                }
                else {
                    blob.maxAge = Integer.parseInt(ageArea.getText());
                }
                blob.maxSpeed = Double.parseDouble(speedArea.getText());
                blob.health = Double.parseDouble(healthArea.getText());
                blob.size = Double.parseDouble(sizeArea.getText());
                blob.birthHealth = blob.health;

                int fType = 0;
                if (expRadio.isSelected()) fType = 0;
                if (oneRadio.isSelected()) fType = 1;
                if (zeroRadio.isSelected()) fType = 2;
                int ins = blob.maxPerceivable * 5 + 3;
                int rows = Integer.parseInt(hiddenRowsArea.getText());
                int cols = Integer.parseInt(hiddenColumnsArea.getText());
                int outs = 1;
                blob.brain = new NeuralNet(ins, rows, cols, outs, fType, false);  // inputs, hidden rows, hidden columns, outputs, activation function type
                blob.maxPerceivable = Integer.parseInt(perceiveArea.getText());
                if (randBox.isSelected()) {
                    blob.color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
                }
                else {
                    blob.color = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
                }
                if (markBox.isSelected()) blob.marked = true;


                mainFrame.pop ++;
                blobs.add(blob);
            }
            mainFrame.repaint();
            dispose();
        }
    }

    private class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        }
    }
}
