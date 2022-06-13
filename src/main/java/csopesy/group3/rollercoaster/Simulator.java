package csopesy.group3.rollercoaster;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Simulator {
    private RollerCoaster model;

    @FXML
    public Button btnStart;

    public void initModel(RollerCoaster model) {
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model = model;
    }

    @FXML
    public void startSimulation(ActionEvent actionEvent) {
        btnStart.setVisible(false);
        model.initialize();
    }
}
