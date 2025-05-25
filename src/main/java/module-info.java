module ProyectoProgra1 {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.proyecto to javafx.fxml;
    exports com.proyecto;
}
