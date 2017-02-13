package greycat.ml.neuralnet.layers;

import greycat.struct.ENode;

public class Registry {

    final static int FeedForwardLayer = 0;

    public static Layer toLayer(ENode node) {
        switch ((int) node.get("type")) {
            case FeedForwardLayer:
                return new FeedForwardLayer(node);
        }
        return null;
    }

}