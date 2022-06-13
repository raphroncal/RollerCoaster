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

        // initialize circles
        Circle[] passenger = new Circle[n];     // circles will represent the passengers
        for(int i = 0; i < n; i++) {
            passenger[i] = new Circle(0, 0, size, Color.WHITE);
            panePassenger.getChildren().add(passenger[i]);
            setPosition(passenger[i]);
            // all passengers are initially wandering
            wander(passenger[i]);
        }

        // initialize rectangles
        Rectangle[] car = new Rectangle[m];     // rectangles will represent the cars
        for(int i = 0; i < m; i++) {
            car[i] = new Rectangle(100, 200, Color.YELLOW);
            car[i].setTranslateX(220);
            car[i].setTranslateY(675);
            paneCar.getChildren().add(car[i]);
        }

        drive(car[0]);
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

        AnimationTimer animation = new AnimationTimer() {
            private long lastUpdate;
            @Override
            public void handle(long l) {
                // 1_000_000_000 (1M milliseconds) = 1 second
                if (l - lastUpdate >= 1_000_000_000) {
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
                            if(c.getTranslateX() < size)
                                c.setTranslateX(x + distance);
                        }

                        // move up
                        case 3 -> {
                            c.setTranslateY(y - distance);
                            if(c.getTranslateY() < size)
                                c.setTranslateY(y + distance);
                        }

                        // move down
                        case 4 -> {
                            c.setTranslateY(y + distance);
                            if(c.getTranslateY() > paneHeight)
                                c.setTranslateY(y - distance);
                        }
                    }

                    lastUpdate = l;
                }

                // TODO: if condition for passenger to stop wandering and start waiting to board the ride
//                    if(some condition) {
//                        stop();
//                        goToQueue(c);
//                    }
            }
        };

        // call this to make this specific passenger start wandering
        animation.start();
        // call this to make this specific passenger start wandering
//        animation.stop();
    }

    /**
     * drives the car upwards
     *
     * @param r is a rectangle representing a car;
     */
    private void drive(Rectangle r) {
        AnimationTimer animation = new AnimationTimer() {
            private long lastUpdate;
            @Override
            public void handle(long l) {
                if (l - lastUpdate >= 10_000_000) {
                    double y = r.getTranslateY();
                    System.out.println(y);
                    r.setTranslateY(y - 1);

                    lastUpdate = l;
                }

                // TODO: car stops and can start boarding passengers
                if(r.getTranslateY() == 400.0) {
                    stop();

                    // TODO: model methods to board passengers
                    // after boarding passengers, car can now start driving again
                    start();
                }

                // reset car position when it goes out of bounds
                if(r.getTranslateY() == -200.0) {
                    stop();
                    resetCarPosition(r);
                }
            }
        };

        animation.start();
    }

    /**
     * used when the car goes out of bounds to set it back to the beginning
     * and to make it seem as if the car drove to a circle
     *
     * @param r is a rectangle representing a car;
     */
    private void resetCarPosition(Rectangle r) {
        r.setTranslateX(220);
        r.setTranslateY(675);
    }

    // TODO
    private void goToQueue(Circle c) {

    }
}
