import java.util.ArrayList;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class UtilsViews {

    public static StackPane parentContainer = new StackPane();
    public static ArrayList<Object> controllers = new ArrayList<>();

    // Afegiu una vista a la llista
    public static void addView(Class<?> cls, String name, String path) throws Exception {
        
        boolean defaultView = false;
        FXMLLoader loader = new FXMLLoader(cls.getResource(path));
        Pane view = loader.load();
        ObservableList<Node> children = parentContainer.getChildren();

        if (children.isEmpty()) {
            defaultView = true;
        }

        view.setId(name);
        view.setVisible(defaultView);
        view.setManaged(defaultView);

        children.add(view);
        controllers.add(loader.getController());
    }

    public static Object getController(String viewId) {
        int index = 0;
        for (Node n : parentContainer.getChildren()) {
            if (n.getId().equals(viewId)) {
                return controllers.get(index);
            }
            index++;
        }
        return null;
    }
    public static void setView(String viewId) {

        ArrayList<Node> list = new ArrayList<>();
        list.addAll(parentContainer.getChildrenUnmodifiable());

        // Ensenya la següent vista i oculta les altres
        for (Node n : list) {
            if (n.getId().equals(viewId)) {
                n.setVisible(true);
                n.setManaged(true);
            } else {
                n.setVisible(false);
                n.setManaged(false);
            }
        }
        parentContainer.requestFocus();
    }

    // Estableix la vista visible pel seu identificador (viewId) amb una animació
    public static void setViewAnimating(String viewId) {

        ArrayList<Node> list = new ArrayList<>();
        list.addAll(parentContainer.getChildrenUnmodifiable());

        // Agafa la vista actual
        Node curView = null;
        for (Node n : list) {
            if (n.isVisible()) {
                curView = n;
            }
        }

        if (curView.getId().equals(viewId)) {
            return; // Si la vista actual es la mateixa que la próxima, el programa no farà res
        }

        // Agafa la següent vista
        Node nxtView = null;
        for (Node n : list) {
            if (n.getId().equals(viewId)) {
                nxtView = n;
            }
        }

        nxtView.setVisible(true);
        nxtView.setManaged(true);

        // Per defecte, l'animació es configurarà cap a l'esquerra
        double width = parentContainer.getScene().getWidth();
        double xLeftStart = 0;
        double xLeftEnd = 0;
        double xRightStart = 0;
        double xRightEnd = 0;
        Node animatedViewLeft = null;
        Node animatedViewRight = null;

        if (list.indexOf(curView) < list.indexOf(nxtView)) {

            // Si curView és inferior a nxtView, s'anima cap a l'esquerra, en canvi, si curView es superior a nxtView, s'animarà cap a la dreta
            xLeftStart = 0;
            xLeftEnd = -width;
            xRightStart = width;
            xRightEnd = 0;
            animatedViewLeft = curView;
            animatedViewRight = nxtView;

            curView.translateXProperty().set(xLeftStart);
            nxtView.translateXProperty().set(xRightStart);

        } else { 

            xLeftStart = -width;
            xLeftEnd = 0;
            xRightStart = 0;
            xRightEnd = width;
            animatedViewLeft = nxtView;
            animatedViewRight = curView;

            curView.translateXProperty().set(xRightStart);
            nxtView.translateXProperty().set(xLeftStart);
        }

        // Vista animada esquerra
        final double seconds = 0.4;
        KeyValue kvLeft = new KeyValue(animatedViewLeft.translateXProperty(), xLeftEnd, Interpolator.EASE_BOTH);
        KeyFrame kfLeft = new KeyFrame(Duration.seconds(seconds), kvLeft);
        Timeline timelineLeft = new Timeline();
        timelineLeft.getKeyFrames().add(kfLeft);
        timelineLeft.play();

        // Vista animada dreta
        KeyValue kvRight = new KeyValue(animatedViewRight.translateXProperty(), xRightEnd, Interpolator.EASE_BOTH);
        KeyFrame kfRight = new KeyFrame(Duration.seconds(seconds), kvRight);
        Timeline timelineRight = new Timeline();
        timelineRight.getKeyFrames().add(kfRight);
        timelineRight.setOnFinished(t -> {
            // Oculta les altres vistes
            for (Node n : list) {
                if (!n.getId().equals(viewId)) {
                    n.setVisible(false);
                    n.setManaged(false);
                }
                n.translateXProperty().set(0);
            }
        });
        timelineRight.play();
        parentContainer.requestFocus();
    }
}