module org.example.collegemanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    //java.sql module
    requires java.sql;




    opens org.example.collegemanagement to javafx.fxml;
    exports org.example.collegemanagement;
}