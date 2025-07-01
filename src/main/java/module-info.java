module com.activa {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires atlantafx.base;

    exports com.activa;
    exports com.activa.controllers;
    exports com.activa.controllers.auth;
    exports com.activa.controllers.layout;
    exports com.activa.middlewares;
    exports com.activa.models;
    exports com.activa.repositories;
    exports com.activa.services;
    exports com.activa.utils;

    opens com.activa to javafx.fxml;
    opens com.activa.controllers to javafx.fxml;
    opens com.activa.controllers.layout to javafx.fxml;
    opens com.activa.controllers.auth to javafx.fxml;
    opens com.activa.middlewares to javafx.fxml;
    opens com.activa.models to javafx.fxml;
}
