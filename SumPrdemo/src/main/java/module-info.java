module ru.etu.studypract {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;

    opens ru.etu.controllers to javafx.fxml;
    exports ru.etu.studypract;
}