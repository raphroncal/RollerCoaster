package csopesy.group3.rollercoaster;

import csopesy.group3.rollercoaster.model.RollerCoaster;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private TextField txtNumPassengers;
    @FXML
    private TextField txtNumCars;
    @FXML
    private TextField txtNumPassengersPerCar;

    @FXML
    protected void onConfirmClick(ActionEvent actionEvent) throws IOException {
        int nPassengers = Integer.parseInt(txtNumPassengers.getText());
        int nCars = Integer.parseInt(txtNumCars.getText());
        int nPassengersPerCar = Integer.parseInt(txtNumPassengersPerCar.getText());

        FXMLLoader simulatorFxml = new FXMLLoader(getClass().getResource("simulator.fxml"));
        Scene simulatorScene = new Scene(simulatorFxml.load());

        RollerCoaster model = new RollerCoaster(nPassengers, nCars, nPassengersPerCar);
        SimulatorController simulator = simulatorFxml.getController();
        simulator.initModel(model); // passing the model

        // showing the new window
        Stage menuStage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        menuStage.setResizable(false);
        menuStage.setScene(simulatorScene);
        menuStage.show();
    }
}