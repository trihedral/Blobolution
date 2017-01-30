import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kyled_000 on 11/13/2015.
 */

public class Neuron{

    double weights[];  // dummy weight at last index
    public int functionType;

    // Create a neuron with weights randomized from -1 to 1 //
    public Neuron(int numInputs, int functionType){
        this.functionType = functionType;
        weights = new double[numInputs+1];
        Random rand = new Random();
        for (int i=0; i<weights.length; i++){
            do {
                weights[i] = (1 - 2 * rand.nextDouble()); // randomize weights between -1 and 1;
            } while(weights[i]==0);
        }
    }

    // Copy Constructor //
    public Neuron(Neuron oldNeuron) {
        functionType = oldNeuron.functionType;
        weights = new double[oldNeuron.weights.length];
        for (int i=0; i<oldNeuron.weights.length; i++)
            weights[i] = oldNeuron.weights[i];
    }

    /**
     * Varies all weights between weight1+localLimit) and weight-localLimit)
     * @param localLimit
     */
    public void vary(double localLimit){
        Random rand = new Random();
        for (int i=0; i<weights.length; i++){
            double R = 1 - 2 * rand.nextDouble();  // value from -1 to 1
            weights[i] += localLimit * R;
        }
    }

    public double fire(ArrayList<Double> dendrites){
        double in = inputFunction(dendrites);
        return activationFunction(in);
    }

    public double fire(double dendrite){
        return activationFunction(dendrite*weights[0]);
    }

    private double inputFunction(ArrayList<Double> dendrites){
        double summation = weights[weights.length-1];  // add dummy weight to sum
        for (int i = 0; i < dendrites.size(); i++){
            double w = weights[i];
            summation += dendrites.get(i)*w;
        }
        return summation;
    }

    private double activationFunction(double in){
        switch(functionType) {
            case 0:
                return 1 / (1 + Math.exp(-in));
            case 1:
                return in;
        }
        return 0;
    }

}