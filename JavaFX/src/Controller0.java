import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Controller0 implements Initializable {

    @FXML
    private Label txtName, txtDate, txtBrand, txtSelected, txtError;

    @FXML
    private ImageView imgConsole;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private ProgressIndicator loading;
    private int loadingCounter = 0;

    @FXML
    private VBox vBoxList = new VBox();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Start choiceBox setting onaction event
        choiceBox.setOnAction((event) -> {
            loadBrandConsoles(choiceBox.getSelectionModel().getSelectedItem());
        });
    }

    @FXML
    private void setView1() {
        UtilsViews.setViewAnimating("View1");
    }

    private void showLoading () {
        loadingCounter++;
        loading.setVisible(true);
    }

    private void hideLoading () {
        loadingCounter--;
        if (loadingCounter <= 0) {
            loadingCounter = 0;
            loading.setVisible(false);
        }
    }

    private void showError () {
        // Mostra els errors
        txtError.setVisible(true);

        // Oculta els errors després de 3 segons
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), ae -> txtError.setVisible(false)));
        timeline.play();
    }

    @FXML
    public void loadBrandsList() {
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "marques");

        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            loadBrandsListCallback(response);
            hideLoading();
        });
    }

    private void loadBrandsListCallback (String response) {

        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {

            JSONArray JSONlist = objResponse.getJSONArray("result");
            ArrayList<String> brandsArr = new ArrayList<>();

            // Crea arraylist amb marques del JSON
            for (int i = 0; i < JSONlist.length(); i++) {
                brandsArr.add(JSONlist.getString(i));
            }

            // Estableix choicebox items amb marques de la arraylist
            choiceBox.getItems().clear();
            choiceBox.getItems().addAll(brandsArr);
            choiceBox.setValue(brandsArr.get(0));

            loadBrandConsoles(brandsArr.get(0));
        } else {
            showError();
        }
    }

    @FXML
    private void loadBrandConsoles (String brand) {

        // Estableix la marca seleccionada a l'etiqueta
        txtSelected.setText(choiceBox.getValue());

        vBoxList.getChildren().clear();

        // Carrega la llista de consoles de la marca seleccionada
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "marca");
        obj.put("name", brand);

        // Demana dades
        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            loadBrandConsolesCallback(response);
            hideLoading();
        });
    }

    private void loadBrandConsolesCallback (String response) {

        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {

            JSONArray JSONlist = objResponse.getJSONArray("result");
            URL resource = this.getClass().getResource("./assets/listItem.fxml");

            // Limpia la llista
            vBoxList.getChildren().clear();

            for (int i = 0; i < JSONlist.length(); i++) {

                // Obteniu informació de la consola
                JSONObject console = JSONlist.getJSONObject(i);

                try {
                    // Carregueu la plantilla i configureu el controlador
                    FXMLLoader loader = new FXMLLoader(resource);
                    Parent itemTemplate = loader.load();
                    ControllerItem itemController = loader.getController();
                
                    // Ompliu la plantilla amb informació de la consola
                    itemController.setTitle(console.getString("name"));
                    
                    // Afegeix una plantilla a la llista
                    vBoxList.getChildren().add(itemTemplate);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            showError();
        }
    }

    public void loadConsoleInfo (String consoleName) {

        JSONObject obj = new JSONObject("{}");
        obj.put("type", "consola");
        obj.put("name", consoleName);

        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            loadConsoleInfoCallback(response);
            hideLoading();
        });
    }

    private void loadConsoleInfoCallback (String response) {

        JSONObject objResponse = new JSONObject(response);
        
        if (objResponse.getString("status").equals("OK")) {

            JSONObject console = objResponse.getJSONObject("result");

            // Ompliu la informació de la consola amb les dades rebudes
            txtName.setText(console.getString("name"));
            txtDate.setText(console.getString("date"));
            txtBrand.setText(console.getString("brand"));
    
            try{
                // Carrega la imatge de la consola
                Image image = new Image(Main.protocol + "://" + Main.host + ":" + Main.port + "/" + console.getString("image")); 
                imgConsole.setImage(image); 
                imgConsole.setFitWidth(200);
                imgConsole.setPreserveRatio(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError();
        }
    }
}