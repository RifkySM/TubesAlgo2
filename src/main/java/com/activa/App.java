package com.activa;
import atlantafx.base.theme.PrimerLight;

import com.activa.middlewares.AuthMiddleware;
import com.activa.utils.DatabaseUtil;
import com.activa.utils.Helper;
import com.activa.utils.SessionManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        System.out.println("Starting Application...");
        System.out.println(Helper.hashPassword("password"));
        SessionManager.getInstance().setDatabaseConnection(DatabaseUtil.getConnection());
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        stage.initStyle(StageStyle.UNDECORATED);

        AuthMiddleware.setPrimaryStage(stage);

        if (SessionManager.getInstance().isLoggedIn()) {
            stage.setMaximized(true);
            Helper.openWindow(stage, "/views/layout/base.fxml", "Dashboard");
        } else {
            AuthMiddleware.redirectToLogin();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
