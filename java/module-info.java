module org.example.toy_social_v1_1_ {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens org.example.toy_social_v1_1 to javafx.fxml;
    exports org.example.toy_social_v1_1;
    exports org.example.toy_social_v1_1.factory;
    opens org.example.toy_social_v1_1.factory to javafx.fxml;

    requires com.fasterxml.jackson.databind;
    requires org.postgresql.jdbc;
    requires java.sql;
    requires java.desktop;
}