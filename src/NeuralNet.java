import java.util.ArrayList;

class NeuralNet {

    Neuron inputNeurons[];
    Neuron outputNeurons[];
    Neuron hiddenNeurons[][];
    int hiddenRows;
    int hiddenColumns;
    private ArrayList<Double> outputs;
    int functionType;

    // Creates a randomly weighted neural net //
    NeuralNet(int numInputs, int hiddenRows, int hiddenColumns, int numOutputs, int functionType, boolean feedback){
        int nI = numInputs;
        if (feedback) nI++;
        this.functionType = functionType;
        inputNeurons = new Neuron[nI];
        outputNeurons = new Neuron[numOutputs];
        hiddenNeurons = new Neuron[hiddenRows][hiddenColumns];
        outputs = new ArrayList<Double>();
        this.hiddenRows = hiddenRows;
        this.hiddenColumns = hiddenColumns;

        // Create randomly weighted input neurons //
        for (int i=0; i<inputNeurons.length; i++){
            inputNeurons[i] = new Neuron(1, functionType);
        }
        // Create first column of hidden neurons //
        for (int r=0; r<hiddenRows; r++){
            hiddenNeurons[r][0] = new Neuron(nI, functionType); // one input from each input node
        }
        // Create all other hidden neurons //
        for (int c=1; c<hiddenColumns; c++){
            for (int r=0; r<hiddenRows; r++){
                hiddenNeurons[r][c] = new Neuron(hiddenRows, functionType); // one input from each hiddenNode in prev column
            }
        }
        // Create output neurons //
        for (int i=0; i<outputNeurons.length; i++){
            outputNeurons[i] = new Neuron(hiddenRows, functionType); // one input from each hiddenNode in prev column
        }

    }

    // Copy constructor //
    NeuralNet(NeuralNet oldNN){
        functionType = oldNN.functionType;
        inputNeurons = new Neuron[oldNN.inputNeurons.length];
        outputNeurons = new Neuron[oldNN.outputNeurons.length];
        hiddenNeurons = new Neuron[oldNN.hiddenRows][oldNN.hiddenColumns];
        outputs = new ArrayList<Double>();
        hiddenRows = oldNN.hiddenRows;
        hiddenColumns = oldNN.hiddenColumns;

        // Copy Over input neurons //
        for (int i=0; i<inputNeurons.length; i++){
            inputNeurons[i] = new Neuron(oldNN.inputNeurons[i]);
        }

        // Copy Over hidden neurons //
        for (int c=0; c<hiddenColumns; c++){
            for (int r=0; r<hiddenRows; r++){
                hiddenNeurons[r][c] = new Neuron(oldNN.hiddenNeurons[r][c]);
            }
        }
        // Copy Over output neurons //
        for (int i=0; i<outputNeurons.length; i++){
            outputNeurons[i] = new Neuron(oldNN.outputNeurons[i]);
        }
    }

    void vary(double ratio){
        for (Neuron N : inputNeurons){
            N.vary(ratio);
        }
        for (Neuron[] nRow : hiddenNeurons){
            for (Neuron N : nRow){
                N.vary(ratio);
            }
        }
        for (Neuron N : outputNeurons){
            N.vary(ratio);
        }
    }

    void think(ArrayList<Double> in){
        outputs.clear();
        ArrayList<Double> inSignals = new ArrayList<Double>();
        ArrayList<Double> signals = new ArrayList<Double>();
        for (int i=0; i<in.size(); i++){
            inSignals.add(inputNeurons[i].fire(in.get(i)));
        }

        for (int c=0; c<hiddenColumns; c++){
            for (int r = 0; r < hiddenRows; r++) {
                signals.add(hiddenNeurons[r][c].fire(inSignals));
            }
            inSignals = new ArrayList<Double>(signals);
            signals.clear();
        }

        for (Neuron N : outputNeurons){
            outputs.add(N.fire(inSignals));
        }

    }

    double getOutput(int i){
        return outputs.get(i);
    }

}


