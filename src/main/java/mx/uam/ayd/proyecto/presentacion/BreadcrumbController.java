package mx.uam.ayd.proyecto.presentacion;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
public class BreadcrumbController {

    @FXML
    // Contenedor principal del breadcrumb
    private FlowPane container;
    // Manejador de clics para los elementos del breadcrumb
    private Consumer<String> clickHandler;

    // Método para inicializar después de la inyección FXML
    @FXML
    public void initialize() {
        // Estilos iniciales
        container.getStyleClass().add("breadcrumb-container");
    }
    // Método para establecer la ruta del breadcrumb
    public void setPath(List<String> items, Consumer<String> onClick) {
        this.clickHandler = onClick;
        container.getChildren().clear();
        
        if (items == null || items.isEmpty()) {
            return;
        }

        // Lógica para manejar muchos elementos)
        if (items.size() > 6) {
            addLink(items.get(0), onClick);
            //añadir separador
            addSeparator();
            MenuButton menu = new MenuButton("...");
            menu.getStyleClass().add("breadcrumb-menu");
            
            for (int i = 1; i < items.size() - 1; i++) {
                String txt = items.get(i);
                MenuItem mi = new MenuItem(txt);
                mi.setOnAction(e -> {
                    if (clickHandler != null) {
                        clickHandler.accept(txt);
                    }
                });
                menu.getItems().add(mi);
            }
            
            container.getChildren().add(menu);
            addSeparator();
            addLink(items.get(items.size() - 1), onClick);
            return;
        }

        // Para rutas normales
        for (int i = 0; i < items.size(); i++) {
            addLink(items.get(i), onClick);
            if (i < items.size() - 1) {
                addSeparator();
            }
        }
    }

    private void addLink(String text, Consumer<String> onClick) {
        Hyperlink link = new Hyperlink(text);
        link.getStyleClass().add("breadcrumb-link");
        link.setOnAction(e -> {
            if (clickHandler != null) {
                clickHandler.accept(text);
            }
        });
        container.getChildren().add(link);
    }

    private void addSeparator() {
        Label sep = new Label(">");
        sep.getStyleClass().add("breadcrumb-sep");
        container.getChildren().add(sep);
    }

    // Método para limpiar el breadcrumb
    public void clear() {
        container.getChildren().clear();
    }
}