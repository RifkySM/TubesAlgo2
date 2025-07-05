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
        SessionManager.getInstance().setDatabaseConnection(DatabaseUtil.getConnection());
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        AuthMiddleware.setPrimaryStage(stage);

        stage.initStyle(StageStyle.UNDECORATED);
        Helper.openWindow(stage, "/views/layout/base.fxml", "Dashboard");

        AuthMiddleware.checkAuth();
    }


    public static void main(String[] args) {
        launch();
    }
}
