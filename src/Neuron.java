/**
 * MIT License (see LICENSE.md)
 * Copyright (c) 2018 Kyle D. Rocha-Brownell
 **/

import java.util.ArrayList;
import java.util.Random;

class Neuron{

    double weights[];  // dummy weight at last index
    private int functionType;


    /**
     *  Create a neuron with weights randomized from -1 to 1
     * @param numInputs Number of input neurons
     * @param functionType Type of activation function
     */
    Neuron(int numInputs, int functionType){
        this.functionType = functionType;
        weights = new double[numInputs+1];
        Random rand = new Random();
        for (int i=0; i<weights.length; i++){
            do {
                weights[i] = (1 - 2 * rand.nextDouble()); // randomize weights between -1 and 1;
            } while(weights[i]==0);
        }
    }

    /**
     * Copy Constructor
     * @param oldNeuron Neuron to copy
     */
    Neuron(Neuron oldNeuron) {
        functionType = oldNeuron.functionType;
        weights = new double[oldNeuron.weights.length];
        System.arraycopy(oldNeuron.weights, 0, weights, 0, weights.length);
    }

    /**
     * Varies all weights
     * @param localLimit Weights vary between weight1+localLimit and weight-localLimit
     */
    void vary(double localLimit){
        Random rand = new Random();
        for (int i=0; i<weights.length; i++){
            double R = 1 - 2 * rand.nextDouble();  // value from -1 to 1
            weights[i] += localLimit * R;
        }
    }

    /**
     * Run activation function on list of inputs
     * @param dendrites Inputs
     * @return Activation function output
     */
    double fire(ArrayList<Double> dendrites){
        double in = inputFunction(dendrites);
        return activationFunction(in);
    }

    /**
     * Run activation function on single input with dummy weight
     * @param dendrite Input
     * @return Activation function output
     */
    double fire(double dendrite){
        return activationFunction(dendrite*weights[0]);
    }

    /**
     * Adds all inputs times corresponding weights
     * @param dendrites List of input values
     * @return Sum of inputs times corresponding weights
     */
    private double inputFunction(ArrayList<Double> dendrites){
        double summation = weights[weights.length-1];  // add dummy weight to sum
        for (int i = 0; i < dendrites.size(); i++){
            double w = weights[i];
            summation += dendrites.get(i)*w;
        }
        return summation;
    }

    /**
     * Runs activation function (functionType)
     * @param in Function input
     * @return Function output
     */
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