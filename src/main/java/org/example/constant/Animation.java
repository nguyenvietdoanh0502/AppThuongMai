package org.example.constant;

import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animation {
    public static void playClickAnimation(Node node) {
        ScaleTransition st1 = new ScaleTransition(Duration.millis(100), node);
        st1.setToX(0.9);
        st1.setToY(0.9);
        ScaleTransition st2 = new ScaleTransition(Duration.millis(100), node);
        st2.setToX(1.0);
        st2.setToY(1.0);
        SequentialTransition sequentialTransition = new SequentialTransition(st1, st2);
        sequentialTransition.play();
    }
}
