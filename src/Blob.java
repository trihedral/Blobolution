import java.awt.*;
import java.awt.geom.Arc2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Blob{

    int numKids;
    int maxPerceivable;
    double size;
    int maxAge;
    double maxSpeed, x, y, health, birthHealth;
    int age, generation;
    double variance;
    private int timeInc;
    public double vx, vy, winX, winY;
    ArrayList<Blob> blobs;
    Blob[] nearBlobs;
    Random rand;
    Color color;
    public NeuralNet brain;
    public boolean marked;
    public boolean grabbed;

    /**
     * Initializes a Blob with default/random values
      */

    public Blob(int maxPerc, double winX, double winY, ArrayList<Blob> blobs){
        rand = new Random();
        numKids = 0;
        maxAge = Integer.MAX_VALUE;
        marked = false;
        grabbed = false;
        maxPerceivable = maxPerc;
        maxSpeed = .05;
        size = 20;
        health = 10;
        age = 0;
        birthHealth = health;
        nearBlobs = new Blob[maxPerceivable];
        this.winX = winX;
        this.winY = winY;
        this.blobs = blobs;
        variance = .05;
        timeInc = 17;
        brain = new NeuralNet((maxPerceivable)*5 + 3, 5, 2, 1, 1);  // inputs, hidden rows, hidden columns, outputs, activation function type
        generation = 0;
        color = new Color(
                rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
        x = winX*rand.nextDouble();
        y = winY*rand.nextDouble();
    }

    /**
     * Copy Constructor
     */
    public Blob(Blob blob){
        rand = blob.rand;
        variance = blob.variance;
        maxAge = blob.maxAge;
        numKids = blob.numKids;
        marked = blob.marked;
        maxPerceivable = blob.maxPerceivable;
        maxSpeed = blob.maxSpeed;
        size = blob.size;
        health = blob.health;
        //age = blob.age;
        birthHealth = blob.birthHealth;
        nearBlobs = new Blob[maxPerceivable];
        winX = blob.winX;
        winY = blob.winY;
        blobs = blob.blobs;
        timeInc = blob.timeInc;
        brain = new NeuralNet(blob.brain);
        timeInc = blob.timeInc;
        generation = blob.generation;
        color = blob.color;
        x = blob.x;
        y = blob.y;
    }

    public void varyChild(){
        rand = new Random();
        marked = false;
        grabbed = false;
        age = 0;
        brain.vary(variance);
        color = new Color(
                (int) vary(color.getRed(), variance, 0, 255),
                (int) vary(color.getGreen(), variance, 0, 255),
                (int) vary(color.getBlue(), variance, 0, 255));
        generation ++;


        //size = vary(size, variance, 5, 30);
        //maxSpeed = vary(maxSpeed, variance, .01, .5);
    }

    public Blob(File file){
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            char[] buff = new char[(int)file.length()];
            fr.read(buff, 0, (int)file.length());
            fr.close();
            String data = new String(buff);
            int p1 = 0;
            int p2 = data.indexOf(' ', p1);
            numKids = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            maxPerceivable = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            maxAge = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            maxSpeed = Double.parseDouble(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            x = Double.parseDouble(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            y = Double.parseDouble(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            health = Double.parseDouble(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            birthHealth = Double.parseDouble(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            size = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            age = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            generation = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            variance = Double.parseDouble(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            winX = Double.parseDouble(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            winY = Double.parseDouble(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            int red = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            int green = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf('\n', p1);
            int blue = Integer.parseInt(data.substring(p1, p2));
            color = new Color(red, green, blue);

            p1 = p2+1; p2 = data.indexOf(' ', p1);
            int fType = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            int rows = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            int cols = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            int ins = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            int outs = Integer.parseInt(data.substring(p1, p2));
            p1 = p2+1; p2 = data.indexOf(' ', p1);
            brain = new NeuralNet(ins, rows, cols, outs, fType);  // inputs, hidden rows, hidden columns, outputs, activation function type
            for (int i=0; i<brain.inputNeurons.length; i++) {
                for(int j=0; j<brain.inputNeurons[i].weights.length; j++){
                    brain.inputNeurons[i].weights[j] = Double.parseDouble(data.substring(p1, p2));
                    p1 = p2+1; p2 = data.indexOf(' ', p1);
                }
            }
            for (int i=0; i<brain.outputNeurons.length; i++) {
                for(int j=0; j<brain.outputNeurons[i].weights.length; j++){
                    brain.outputNeurons[i].weights[j] = Double.parseDouble(data.substring(p1, p2));
                    p1 = p2+1; p2 = data.indexOf(' ', p1);
                }
            }
            for (int c=0; c<brain.hiddenColumns; c++){
                for (int r = 0; r < brain.hiddenRows; r++) {
                    for (int i = 0; i < brain.hiddenNeurons[r][c].weights.length; i++) {
                        brain.hiddenNeurons[r][c].weights[i] = Double.parseDouble(data.substring(p1, p2));
                        p1 = p2+1; p2 = data.indexOf(' ', p1);
                    }
                }
            }

            nearBlobs = new Blob[maxPerceivable];
            marked = true;
            timeInc = 17;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Blob> live(ArrayList <Blob> b){
        // remove self from list if health <= 0 //
        if (health<=0 && age > 1) {
            //System.out.println("Removing blob due to death: Age " + age + ", Gen " + generation);
            blobs.remove(this);
            return blobs;
        }
        if (age > maxAge) return blobs; // do nothing if died of old age
        if (b.size() <= maxPerceivable) return blobs; // do nothing if there aren't enough other blobs

        blobs = b;
        look();
        think();
        act();
        age++;
        return blobs;
    }

    private void look(){

        // Find nearest blobs //
        double[] minDistances = new double[maxPerceivable];
        for (int i=0; i<maxPerceivable; i++) minDistances[i] = Double.POSITIVE_INFINITY;
        int maxIndex = 0;
        double d;
        for (Blob B : blobs) {
            if (B != this) {
                d = this.distTo(B);
                if (d <= minDistances[maxIndex]) {
                    minDistances[maxIndex] = d;
                    nearBlobs[maxIndex] = B;
                }
                for (int i = 0; i < maxPerceivable; i++) {
                    if (minDistances[i] > minDistances[maxIndex])
                        maxIndex = i;
                }
            }
        }
    }

    private void think(){
        ArrayList<Double> data = new ArrayList<Double>();

        // Send all perceptual data to neural net //
        for (int i=0; i<maxPerceivable; i++){
            data.add((this.distTo(nearBlobs[i]))/winX);
            data.add( Math.atan2(yTo(nearBlobs[i]), xTo(nearBlobs[i]))/(2*Math.PI) );
            data.add(nearBlobs[i].color.getRed()/255.0);
            data.add(nearBlobs[i].color.getGreen()/255.0);
            data.add(nearBlobs[i].color.getBlue()/255.0);
        }
        data.add(color.getRed()/255.0);
        data.add(color.getGreen()/255.0);
        data.add(color.getBlue()/255.0);

        // get neural net output //
        brain.think(data);
        double angle =  brain.getOutput(0)*2*Math.PI;
        vx = Math.cos(angle);
        vy = Math.sin(angle);

         // normalize vectors //
        double mag = Math.sqrt(vx*vx+vy*vy);
        if (mag==0) mag=1;
        vx = vx/mag;
        vy = vy/mag;
    }

    private void act(){
        if (!grabbed) {
            // recalculate position //
            x += vx * maxSpeed * timeInc;
            y += vy * maxSpeed * timeInc;

            // apply entropy //
            //health -= .0001*Math.pow(maxSpeed,2)*Math.pow(size,3);

            // pac-man offscreen logic //
            if (x > winX) x = size/2;
            if (y > winY) y = size/2;
            if (x < 0) x = winX-size/2;
            if (y < 0) y = winY-size/2;
        }

        // eat blobs you're in contact with //
        for (Blob B : blobs) {
            if (this.distTo(B) <= (this.size/2 + B.size/2) ) {
                if (B.health - nutritionRatio(B) > 0) {
                    health += nutritionRatio(B);
                    B.health -= nutritionRatio(B);
                }
                else if (B.health > 0) {
                    health += B.health;
                    B.health = 0;
                }
            }
        }

        while (health >= 2*birthHealth){
            mitosis();
        }
    }

    private void mitosis(){
        health -= birthHealth;
        Blob child = new Blob(this);
        child.varyChild();
        blobs.add(child);
        numKids++;
    }

    private int totalColor(){
        return color.getRed()+color.getGreen()+color.getBlue();
    }

    /** Returns a random number between  num+ratio*255 and num-ratio*255 **/
    double vary(double num, double ratio, double min, double max){
        double R = 1-2*rand.nextDouble();  // random number from -1 to 1
        double newNum = num + R*ratio*max;
        if (newNum > max) newNum = max;
        else if (newNum < min) newNum = min;
        return newNum;
    }

    public void save(File file) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write("");

            fw.append(String.valueOf(numKids));
            fw.append(" ");
            fw.append(String.valueOf(maxPerceivable));
            fw.append(" ");
            fw.append(String.valueOf(maxAge));
            fw.append(" ");
            fw.append(String.valueOf(maxSpeed));
            fw.append(" ");
            fw.append(String.valueOf(x));
            fw.append(" ");
            fw.append(String.valueOf(y));
            fw.append(" ");
            fw.append(String.valueOf(health));
            fw.append(" ");
            fw.append(String.valueOf(birthHealth));
            fw.append(" ");
            fw.append(String.valueOf(size));
            fw.append(" ");
            fw.append(String.valueOf(age));
            fw.append(" ");
            fw.append(String.valueOf(generation));
            fw.append(" ");
            fw.append(String.valueOf(variance));
            fw.append(" ");
            fw.append(String.valueOf(winX));
            fw.append(" ");
            fw.append(String.valueOf(winY));
            fw.append(" ");
            fw.append(String.valueOf(color.getRed()));
            fw.append(" ");
            fw.append(String.valueOf(color.getGreen()));
            fw.append(" ");
            fw.append(String.valueOf(color.getBlue()));
            fw.append("\n");

            fw.append(String.valueOf(brain.functionType));
            fw.append(" ");
            fw.append(String.valueOf(brain.hiddenRows));
            fw.append(" ");
            fw.append(String.valueOf(brain.hiddenColumns));
            fw.append(" ");
            fw.append(String.valueOf(brain.inputNeurons.length));
            fw.append(" ");
            fw.append(String.valueOf(brain.outputNeurons.length));
            fw.append(" ");
            for (int i=0; i<brain.inputNeurons.length; i++) {
                for(int j=0; j<brain.inputNeurons[i].weights.length; j++){
                    fw.append(String.valueOf(brain.inputNeurons[i].weights[j]));
                    fw.append(" ");
                }
            }
            for (int i=0; i<brain.outputNeurons.length; i++) {
                for(int j=0; j<brain.outputNeurons[i].weights.length; j++){
                    fw.append(String.valueOf(brain.outputNeurons[i].weights[j]));
                    fw.append(" ");
                }
            }
            for (int c=0; c<brain.hiddenColumns; c++){
                for (int r = 0; r < brain.hiddenRows; r++) {
                    for (int i = 0; i < brain.hiddenNeurons[r][c].weights.length; i++) {
                        fw.append(String.valueOf(brain.hiddenNeurons[r][c].weights[i]));
                        fw.append(" ");
                    }
                }
            }

            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double distTo(Blob b){
        return Math.sqrt(Math.pow(yTo(b),2)+Math.pow(xTo(b),2));
    }

    public double xTo(Blob b){
        double dx = b.x-this.x;
        if (Math.abs(dx) > winX/2) dx = winX-dx;
        return dx;
    }

    public double yTo(Blob b){
        double dy = b.y-this.y;
        if (Math.abs(dy) > winY/2) dy = winY-dy;
        return dy;
    }

    public double nutritionRatio(Blob B) {
        double colorTaste =
                (
                  color.getRed()  / 255.0 * B.color.getGreen()/ 255.0
                + color.getGreen()/ 255.0 * B.color.getBlue() / 255.0             // R eats G, G eats B, B eats R
                + color.getBlue() / 255.0 * B.color.getRed()  / 255.0
                ) / 3;
        //double sizeTaste = 1;
        //if (size < B.size) sizeTaste = size/B.size;
        return (colorTaste);// * sizeTaste);
    }

    ///////////// SORTERS ///////////////

    public static Comparator<Blob> sortByAge = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b2.age - b1.age;}
    };
    public static Comparator<Blob> sortByHealth = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) {
            if (b2.health < b1.health) return -1;
            if (b2.health > b1.health) return 1;
            return 0;
        }
    };
    public static Comparator<Blob> sortByGeneration = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b2.generation - b1.generation;}
    };
    public static Comparator<Blob> sortByRed = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b2.color.getRed() - b1.color.getRed();}
    };
    public static Comparator<Blob> sortByGreen = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b2.color.getGreen() - b1.color.getGreen();}
    };
    public static Comparator<Blob> sortByBlue = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b2.color.getBlue() - b1.color.getBlue();}
    };
    public static Comparator<Blob> sortByMarked = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) {
            if (b1.marked && !b2.marked) return -1;
            if (!b1.marked && b2.marked) return 1;
            return 0;
        }
    };
    public static Comparator<Blob> sortByPerceivable = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b2.maxPerceivable - b1.maxPerceivable;}
    };
    public static Comparator<Blob> sortByVariance = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) {
            if (b2.variance < b1.variance) return -1;
            if (b2.variance > b1.variance) return 1;
            return 0;
        }
    };
    public static Comparator<Blob> sortByKids = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b2.numKids - b1.numKids;}
    };
    public static Comparator<Blob> sortByFunction = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b2.brain.functionType - b1.brain.functionType;}
    };

    public static Comparator<Blob> sortByAgeUp = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b1.age - b2.age;}
    };
    public static Comparator<Blob> sortByHealthUp = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) {
            if (b1.health < b2.health) return -1;
            if (b1.health > b2.health) return 1;
            return 0;
        }
    };
    public static Comparator<Blob> sortByGenerationUp = new Comparator<Blob>() {
        public int compare(Blob b2, Blob b1) { return b2.generation - b1.generation;}
    };
    public static Comparator<Blob> sortByRedUp = new Comparator<Blob>() {
        public int compare(Blob b2, Blob b1) { return b2.color.getRed() - b1.color.getRed();}
    };
    public static Comparator<Blob> sortByGreenUp = new Comparator<Blob>() {
        public int compare(Blob b2, Blob b1) { return b2.color.getGreen() - b1.color.getGreen();}
    };
    public static Comparator<Blob> sortByBlueUp = new Comparator<Blob>() {
        public int compare(Blob b2, Blob b1) { return b2.color.getBlue() - b1.color.getBlue();}
    };
    public static Comparator<Blob> sortByMarkedUp = new Comparator<Blob>() {
        public int compare(Blob b2, Blob b1) {
            if (b1.marked && !b2.marked) return 1;
            if (!b1.marked && b2.marked) return -1;
            return 0;
        }
    };
    public static Comparator<Blob> sortByPerceivableUp = new Comparator<Blob>() {
        public int compare(Blob b2, Blob b1) { return b2.maxPerceivable - b1.maxPerceivable;}
    };
    public static Comparator<Blob> sortByVarianceUp = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) {
            if (b1.variance < b2.variance) return -1;
            if (b1.variance > b2.variance) return 1;
            return 0;
        }
    };
    public static Comparator<Blob> sortByKidsUp = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b1.numKids - b2.numKids;}
    };
    public static Comparator<Blob> sortByFunctionUp = new Comparator<Blob>() {
        public int compare(Blob b1, Blob b2) { return b1.brain.functionType - b2.brain.functionType;}
    };



}