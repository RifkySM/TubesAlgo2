package com.activa;
import atlantafx.base.theme.PrimerLight;

import com.activa.middlewares.AuthMiddleware;
import com.activa.utils.DatabaseUtil;
import com.activa.utils.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SessionManager.getInstance().setDatabaseConnection(DatabaseUtil.getConnection());
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        AuthMiddleware.setPrimaryStage(stage);

        URL fxmlLocation = getClass().getResource("/views/layout/base.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Parent root = fxmlLoader.load();

        stage.initStyle(StageStyle.UNDECORATED);

        Scene scene = new Scene(root);
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();

        AuthMiddleware.checkAuth();
    }

    public static void main(String[] args) {
        launch();
    }
}
