import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
public class ControllerUserList implements Initializable{
    
    @FXML
    private VBox vBoxList = new VBox();
    @FXML
    private Label txtError;

    @FXML
    private ProgressIndicator loading;

    private int loadingCounter = 0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
    }
    public void mostrarVista(){
        
        vBoxList.getChildren().clear();
        // Carrega la llista de consoles d'aquesta marca
        JSONObject obj = new JSONObject("{}");
        obj.put("type","carrega");
        // Demana dades
        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            loadList(response);
            hideLoading();
        });
    }

    private void loadList (String response) {
        
        JSONObject objResponse = new JSONObject(response);
        System.out.println(objResponse);
         if (objResponse.getString("status").equals("OK")) {

            JSONArray JSONlist = objResponse.getJSONArray("result");
            URL resource = this.getClass().getResource("./assets/listItem.fxml");
            
            vBoxList.getChildren().clear();
            for (int i = 0; i < JSONlist.length(); i++) {

                // Obté informació de la consola
                JSONObject user = JSONlist.getJSONObject(i);

                    try {
                    // Carrega la plantilla i configura el controlador
                    FXMLLoader loader = new FXMLLoader(resource);
                    Parent itemTemplate = loader.load();
                    ControllerItem itemController = loader.getController();
                        
                    
                    // Omple la plantilla amb informació de la consola
                    itemController.setTitle(user.getString("firstName"));                     
                    itemController.setId(user.getInt("id"));
                    // Afegeix la plantilla a la llista
                    vBoxList.getChildren().add(itemTemplate);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    @FXML
    public void goBack() {
        UtilsViews.setViewAnimating("ViewSign");
    }
    @FXML
    public void goToForm() {
        UtilsViews.setViewAnimating("ViewFormulari");
    }
}