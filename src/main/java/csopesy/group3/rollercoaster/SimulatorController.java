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
    public AnchorPane panePassenger;
    @FXML
    public AnchorPane paneCar;
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

        Circle[] passenger = new Circle[n];                     // circles will represent the passengers
        AnimationTimer[] timerWander = new AnimationTimer[n];   // each passenger has their own timer
        for(int i = 0; i < n; i++) {
            // x and y data points are set to size since circles are set by their centers
            // this way, the sides of the circle is what considered the boundary
            passenger[i] = new Circle(size, size, size, Color.WHITE);
            panePassenger.getChildren().add(passenger[i]);
            setPosition(passenger[i]);

            int finalI = i;
            timerWander[i] = new AnimationTimer() {
                private long lastUpdate;
                @Override
                public void handle(long l) {
                    // 1_000_000_000 (1M milliseconds) = 1 second
                    if (l - lastUpdate >= 1_000_000_000) {
                        wander(passenger[finalI]);
                        lastUpdate = l;
                    }
                }
            };
        }

        Rectangle[] car = new Rectangle[m];                     // rectangles will represent the cars
        AnimationTimer[] timerArrive = new AnimationTimer[n];   // each car has their own timer
        AnimationTimer[] timerDepart = new AnimationTimer[n];   // each car has their own timer
        for(int i = 0; i < m; i++) {
            car[i] = new Rectangle(100, 200, Color.YELLOW);
            car[i].setTranslateX(220);
            car[i].setTranslateY(675);
            paneCar.getChildren().add(car[i]);

            int finalI = i;
            timerArrive[i] = new AnimationTimer() {
                private long lastUpdate;
                @Override
                public void handle(long l) {
                    if (l - lastUpdate >= 100_000_000) {
                        arrive(car[finalI]);
                        lastUpdate = l;
                    }
                }
            };

            timerDepart[i] = new AnimationTimer() {
                private long lastUpdate;
                @Override
                public void handle(long l) {
                    if (l - lastUpdate >= 100_000_000) {
                        depart(car[finalI]);
                        lastUpdate = l;
                    }
                }
            };
        }

        // call this to make passenger[0] start wandering
        timerWander[0].start();
        // call this to make passenger[0] stop wandering
//        timerWander[0].stop();
    }

    /**
     * sets a random position within a specific area
     *
     * @param c is a circle representing a passenger
     */
    private void setPosition(Circle c) {
        int minX = size;
        int maxX = (int) panePassenger.getWidth() - (size * 2);
        int minY = size;
        int maxY = (int) panePassenger.getHeight() - (size * 2);
        int x = ThreadLocalRandom.current().nextInt(minX, maxX + 1);
        int y = ThreadLocalRandom.current().nextInt(minY, maxY + 1);

        c.setTranslateX(x);
        c.setTranslateY(y);
    }

    /**
     * allows a passenger to wander around a specific area
     *
     * @param c is a circle representing a passenger
     */
    private void wander(Circle c) {
        int distance = 25;  // how far the circles will "walk"
        double paneWidth = panePassenger.getWidth() - (size * 2);
        double paneHeight = panePassenger.getHeight() - (size * 2);
        double x = c.getTranslateX();
        double y = c.getTranslateY();

        int action = ThreadLocalRandom.current().nextInt(0, 5 + 1);
        switch (action) {
            // cases 0 & 5 means circle will not move
            // move right
            case 1 -> {
                c.setTranslateX(x + distance);
                if(c.getTranslateX() > paneWidth)
                    c.setTranslateX(x - distance);
            }

            // move left
            case 2 -> {
                c.setTranslateX(x - distance);
                if(c.getTranslateX() < 0)
                    c.setTranslateX(x + distance);
            }

            // move up
            case 3 -> {
                c.setTranslateY(y - distance);
                if(c.getTranslateY() < 0)
                    c.setTranslateY(y + distance);
            }

            // move down
            case 4 -> {
                c.setTranslateY(y + distance);
                if(c.getTranslateY() > paneHeight)
                    c.setTranslateY(y - distance);
            }
        }
    }

    // TODO
    private void arrive(Rectangle r) {

    }

    // TODO
    private void depart(Rectangle r) {

    }

    // TODO
    private void goToQueue(Circle c) {

    }
}
