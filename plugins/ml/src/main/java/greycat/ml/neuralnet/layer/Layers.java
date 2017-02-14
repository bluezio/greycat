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
package greycat.ml.neuralnet.layer;

import greycat.struct.ENode;

public class Layers {
    final static String TYPE = "type";

    final static int FEED_FORWARD_LAYER = 0;
    final static int LINEAR_LAYER = 1;
    final static int GRU_LAYER = 2;
    final static int LSTM_LAYER = 3;
    final static int RNN_LAYER = 4;


    public static Layer toLayer(ENode node) {
        switch ((int) node.get(TYPE)) {
            case FEED_FORWARD_LAYER:
                return new FeedForward(node);
            case LINEAR_LAYER:
                return new Linear(node);
            case GRU_LAYER:
                return new GRU(node);
            case LSTM_LAYER:
                return new LSTM(node);
            case RNN_LAYER:
                return new RNN(node);

        }
        return null;
    }

}
