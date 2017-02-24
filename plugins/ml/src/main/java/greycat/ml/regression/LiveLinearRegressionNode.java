/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greycat.ml.regression;

import greycat.Callback;
import greycat.Graph;
import greycat.Node;
import greycat.Type;
import greycat.ml.BaseMLNode;
import greycat.ml.RegressionNode;
import greycat.plugin.NodeState;
import greycat.utility.Enforcer;

import java.util.Random;

public class LiveLinearRegressionNode extends BaseMLNode implements RegressionNode {

    public static final String ALPHA_KEY = "ALPHA"; //learning rate
    public static final double ALPHA_DEF = 0.0001;

    public static final String LAMBDA_KEY = "LAMBDA"; //regularization rate
    public static final double LAMBDA_DEF = 0.00001;

    public static final String ITERATION_KEY = "ITERATION"; //Number of iterations on each values
    public static final int ITERATION_DEF = 5;

    public static final String THRESHOLD_KEY = "THRESHOLD"; //Number of iterations on each values
    public static final double THRESHOLD_DEF = 0.01;


    public static final String LAST_ERR_KEY = "_ERR";
    public static final String WEIGHT_KEY = "_WEIGHT";

    private static final String INTERNAL_TOTAL_KEY = "_TOTAL_KEY";
    private static final String INTERNAL_WEIGHT_BACKUP_KEY = "_WEIGHTBACKUP";

    private static final String MISMATCH_MSG = "Different Imput lengths are not supported";


    //Name of the algorithm to be used in the meta model
    public final static String NAME = "LiveLinearRegression";

    //Factory of the class integrated

    public LiveLinearRegressionNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    @Override
    public void learn(final double output, final Callback<Boolean> callback) {
        extractFeatures(new Callback<double[]>() {
            @Override
            public void on(double[] input) {
                internalLearn(input, output, callback);
            }
        });
    }

    public void internalLearn(double[] input, double output, Callback<Boolean> callback) {
        NodeState state = this._resolver.alignState(this);
        int iterations = state.getWithDefault(ITERATION_KEY, ITERATION_DEF);
        double alpha = state.getWithDefault(ALPHA_KEY, ALPHA_DEF);
        double lambda = state.getWithDefault(LAMBDA_KEY, LAMBDA_DEF);
        double[] weights = (double[]) state.get(WEIGHT_KEY);


        if (weights == null) {
            weights = new double[input.length + 1];
            Random random = new Random();
            for (int i = 0; i < weights.length; i++) {
                weights[i] = random.nextDouble() * 0.001;
            }
        }

        //ToDo test currentErr and update alpha automatically
        double prevErr = state.getWithDefault(LAST_ERR_KEY, 0.0);
        double currErr = calculate(weights, input) - output;
      /*  if (currErr > prevErr) {
            //toDo fill here
        }*/

        state.set(LAST_ERR_KEY, Type.DOUBLE, currErr);

        if (input == null || weights.length != (input.length + 1)) {
            throw new RuntimeException(MISMATCH_MSG);
        }
        int featuresize = input.length;

        for (int j = 0; j < iterations; j++) {
            double h = calculate(weights, input) - output;
            for (int i = 0; i < featuresize; i++) {
                weights[i] = weights[i] - alpha * (h * input[i] + lambda * weights[i]);
            }
            weights[featuresize] = weights[featuresize] - alpha * h;
        }

        double[] bckupWeight = (double[]) state.get(INTERNAL_WEIGHT_BACKUP_KEY);
        if (bckupWeight == null) {
            state.set(WEIGHT_KEY, Type.DOUBLE_ARRAY, weights);
            state.set(INTERNAL_WEIGHT_BACKUP_KEY, Type.DOUBLE_ARRAY, weights);
            state.set(INTERNAL_TOTAL_KEY, Type.INT, 1);
        } else {
            double diff = 0;
            for (int i = 0; i < weights.length; i++) {
                diff = Math.max(diff, Math.abs(weights[i] - bckupWeight[i]));
            }
            double deviation = state.getWithDefault(THRESHOLD_KEY, THRESHOLD_DEF);

            if (diff > deviation) {
                state = phasedState();
                //ToDo test weight here and play with alpha
                state.set(WEIGHT_KEY, Type.DOUBLE_ARRAY, weights);
                state.set(INTERNAL_WEIGHT_BACKUP_KEY, Type.DOUBLE_ARRAY, weights);
                state.set(INTERNAL_TOTAL_KEY, Type.INT, 1);
            } else {
                state.set(WEIGHT_KEY, Type.DOUBLE_ARRAY, weights);
                state.set(INTERNAL_TOTAL_KEY, Type.INT, (Integer) state.get(INTERNAL_TOTAL_KEY) + 1);
            }
        }

        if (callback != null) {
            callback.on(true);
        }
    }

    private static final Enforcer enforcer = new Enforcer()
            .asDouble(ALPHA_KEY)
            .asDouble(LAMBDA_KEY)
            .asInt(ITERATION_KEY);

    //Override default Abstract node default setters and getters
    @Override
    public Node set(String propertyName, byte propertyType, Object propertyValue) {
        enforcer.check(propertyName, propertyType, propertyValue);
        return super.set(propertyName, propertyType, propertyValue);
    }
    
    private double calculate(double[] weights, double[] input) {
        double h = 0;
        for (int j = 0; j < input.length; j++) {
            h += weights[j] * input[j];
        }
        h += weights[input.length];
        return h;
    }

    @Override
    public void extrapolate(final Callback<Double> callback) {
        final NodeState state = this._resolver.resolveState(this);
        final double[] weights = (double[]) state.get(WEIGHT_KEY);
        if (weights == null) {
            if (callback != null) {
                callback.on(0.0);
            }
        } else {
            extractFeatures(new Callback<double[]>() {
                @Override
                public void on(double[] input) {
                    if (input.length != weights.length - 1) {
                        throw new RuntimeException(MISMATCH_MSG);
                    } else if (callback != null) {
                        callback.on(calculate(weights, input));
                    }
                }
            });
        }
    }
}
