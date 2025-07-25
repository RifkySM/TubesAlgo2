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
    exports com.activa.controllers.auth;
    exports com.activa.controllers.attendance;
    exports com.activa.controllers.layout;
    exports com.activa.controllers.user;
    exports com.activa.controllers.report;
    exports com.activa.controllers.member;
    exports com.activa.controllers.announcement;
    exports com.activa.controllers.activity;
    exports com.activa.controllers.club.approval;
    exports com.activa.controllers.dashboard;
    exports com.activa.middlewares;
    exports com.activa.models;
    exports com.activa.repositories;
    exports com.activa.services;
    exports com.activa.utils;

    opens com.activa to javafx.fxml;
    opens com.activa.controllers.layout to javafx.fxml;
    opens com.activa.controllers.announcement to javafx.fxml;
    opens com.activa.controllers.dashboard to javafx.fxml;
    opens com.activa.controllers.member to javafx.fxml;
    opens com.activa.controllers.report to javafx.fxml;
    opens com.activa.controllers.club.approval to javafx.fxml;
    opens com.activa.controllers.auth to javafx.fxml;
    opens com.activa.controllers.activity to javafx.fxml;
    opens com.activa.controllers.attendance to javafx.fxml;
    opens com.activa.controllers.user to javafx.fxml;
    opens com.activa.controllers.registration to javafx.fxml;
    opens com.activa.middlewares to javafx.fxml;
    opens com.activa.models to javafx.fxml;
}
