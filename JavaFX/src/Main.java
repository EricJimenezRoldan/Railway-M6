import org.json.JSONObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Main extends Application {

    public static UtilsWS socketClient;

    public static int port = 3000;
    public static String protocol = "http";
    public static String host = "localhost";
    public static String protocolWS = "ws";

    // Exemple de configuració per Railway
    // public static int port = 443;
    // public static String protocol = "https";
    // public static String host = "server-production-46ef.up.railway.app";
    // public static String protocolWS = "wss";

    // Camps JavaFX a modificar
    public static Label consoleName = new Label();
    public static Label consoleDate = new Label();
    public static Label consoleBrand = new Label();
    public static ImageView imageView = new ImageView(); 

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {

        final int windowWidth = 800;
        final int windowHeight = 600;

        UtilsViews.parentContainer.setStyle("-fx-font: 12 arial;");
        UtilsViews.addView(getClass(), "ViewSign", "./assets/viewSign.fxml");
        UtilsViews.addView(getClass(), "ViewFormulari", "./assets/viewFormulari.fxml");
        UtilsViews.addView(getClass(), "ViewList", "./assets/viewList.fxml");
        UtilsViews.addView(getClass(), "ViewModificar", "./assets/viewModificar.fxml");

        Scene scene = new Scene(UtilsViews.parentContainer);
        
        stage.setScene(scene);
        stage.onCloseRequestProperty(); // Truca al mètode de tancament quan es tanca la finestra
        stage.setTitle("Guardar Dades - Eric Jimenez");
        stage.setMinWidth(windowWidth);
        stage.setMinHeight(windowHeight);
        stage.show();
    }

    @Override
    public void stop() { 
        System.exit(1); // Mata tots els serveis executors
    }
}