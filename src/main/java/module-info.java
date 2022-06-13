module csopesy.group3.rollercoaster {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens csopesy.group3.rollercoaster to javafx.fxml;
    exports csopesy.group3.rollercoaster;
    exports csopesy.group3.rollercoaster.model;
    opens csopesy.group3.rollercoaster.model to javafx.fxml;
}