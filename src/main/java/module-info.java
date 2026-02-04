module org.grupo2.carreracamelloapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;

    exports org.grupo2.carreracamelloapp.controller;
    exports org.grupo2.carreracamelloapp;
    opens org.grupo2.carreracamelloapp.controller;
    opens org.grupo2.carreracamelloapp to javafx.fxml;
}