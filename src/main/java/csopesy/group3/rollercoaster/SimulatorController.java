package csopesy.group3.rollercoaster;

import csopesy.group3.rollercoaster.model.RollerCoaster;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.concurrent.ThreadLocalRandom;

public class SimulatorController {
    private RollerCoaster model;
    private int size;

    @FXML
    public AnchorPane pane;
    @FXML
    public Button btnStart;

    public void initModel(RollerCoaster model) {
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model = model;
        this.size = 10;
    }

    @FXML
    public void startSimulation(ActionEvent actionEvent) {
        btnStart.setVisible(false);
//        model.initialize();
        int n = model.nPassengers;
        int m = model.nCars;
        int C = model.nCapacity;

        Circle[] passenger = new Circle[n];
        Rectangle[] car = new Rectangle[m];

        for(int i = 0; i < n; i++) {
            // x and y data points are set to size since circles are set by their centers
            // this way, the sides of the circle is what considered the boundary
            passenger[i] = new Circle(size, size, size, Color.WHITE);
            pane.getChildren().add(passenger[i]);
            setPosition(passenger[i]);
        }

        // this allows the inside code to go into a loop
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long l) {
                // 1_000_000_000 (1M milliseconds) = 1 second
                if (l - lastUpdate >= 1_000_000_000) {
                    roam(passenger[0]);
                    lastUpdate = l;
                }
            }
        };
        timer.start();

    }

    private void setPosition(Circle c) {
        int minX = size;
        int maxX = (int)pane.getWidth() - size;
        int minY = size;
        int maxY = (int)pane.getHeight() - size;
        int x = ThreadLocalRandom.current().nextInt(minX, maxX + 1);
        int y = ThreadLocalRandom.current().nextInt(minY, maxY + 1);

        c.setTranslateX(x);
        c.setTranslateY(y);
    }

    private void roam(Circle c) {
        double x = c.getTranslateX();
        double y = c.getTranslateY();
        double paneWidth = pane.getWidth() - size;
        double paneHeight = pane.getHeight() - size;

        int action = ThreadLocalRandom.current().nextInt(0, 5 + 1);
        switch (action) {
            // cases 0 & 5 means circle will not move
            // move right
            case 1 -> {
                c.setTranslateX(x + 10);
                if(c.getTranslateX() > paneWidth)
                    c.setTranslateX(x - 10);
            }

            // move left
            case 2 -> {
                c.setTranslateX(x - 10);
                if(c.getTranslateX() < 0)
                    c.setTranslateX(x + 10);
            }

            // move up
            case 3 -> {
                c.setTranslateY(y - 10);
                if(c.getTranslateY() < 0)
                    c.setTranslateY(y + 10);
            }

            // move down
            case 4 -> {
                c.setTranslateY(y + 10);
                if(c.getTranslateY() > paneHeight)
                    c.setTranslateY(y - 10);
            }
        }
    }

    private void carArrive() {

    }
}
