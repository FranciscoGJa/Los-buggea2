package mx.uam.ayd.proyecto.presentacion.PerfilCitas;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.CrearPerfilCitas.VentanaCrearPerfilCitas;
import mx.uam.ayd.proyecto.presentacion.HistorialCitas.VentanaHistorialCitas;
import mx.uam.ayd.proyecto.presentacion.menuPsicologo.VentanaMenuPsicologo;
import mx.uam.ayd.proyecto.negocio.ServicioPerfilCitas;
import mx.uam.ayd.proyecto.negocio.modelo.PerfilCitas;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión de perfiles de citas.
 * Utiliza Spring para la inyección de dependencias y JavaFX para la interfaz gráfica.
 */
@Component
public class ControladorPerfil {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TableView<PerfilCitas> tablaResultados;

    // Columnas (pueden ser null si no están en FXML)
    @FXML
    private TableColumn<PerfilCitas, String> colNombre;

    @FXML
    private TableColumn<PerfilCitas, Integer> colEdad;

    @FXML
    private TableColumn<PerfilCitas, String> colSexo;

    @FXML
    private TableColumn<PerfilCitas, String> colDireccion;

    @FXML
    private TableColumn<PerfilCitas, String> colOcupacion;

    @FXML
    private TableColumn<PerfilCitas, String> colTelefono;

    @Autowired
    private VentanaCrearPerfilCitas ventanaCrearPerfil;

    @Autowired
    private ServicioPerfilCitas servicioPerfilCitas;

    @Autowired
    private VentanaHistorialCitas ventanaHistorialCitas;

    /**
     * Esta referencia es opcional y solo existe cuando se usa dentro del menú
     * del psicólogo. No la usaremos para abrir el historial, para que el
     * comportamiento sea igual en administrador y psicólogo.
     */
    @Autowired(required = false)
    private VentanaMenuPsicologo ventanaMenuPsicologo;

    /**
     * Método initialize para configurar la tabla.
     */
    @FXML
    public void initialize() {
        System.out.println("[ControladorPerfil] inicializado");
        System.out.println("[ControladorPerfil] VentanaHistorialCitas inyectada: " + (ventanaHistorialCitas != null));
        System.out.println("[ControladorPerfil] VentanaMenuPsicologo inyectada: " + (ventanaMenuPsicologo != null));
        System.out.println("[ControladorPerfil] ServicioPerfilCitas inyectado: " + (servicioPerfilCitas != null));

        configurarTablaSegura();
        cargarTodosLosPerfiles();
    }

    /**
     * Configura las columnas de la tabla de manera segura.
     */
    private void configurarTablaSegura() {
        try {
            if (colNombre != null) {
                colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
            }
            if (colEdad != null) {
                colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
            }
            if (colSexo != null) {
                colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
            }
            if (colDireccion != null) {
                colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
            }
            if (colOcupacion != null) {
                colOcupacion.setCellValueFactory(new PropertyValueFactory<>("ocupacion"));
            }
            if (colTelefono != null) {
                colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            }

            System.out.println("[ControladorPerfil] Tabla configurada correctamente");

        } catch (Exception e) {
            System.err.println("[ControladorPerfil] Advertencia al configurar tabla: " + e.getMessage());
        }
    }

    /**
     * Carga todos los perfiles al iniciar.
     */
    private void cargarTodosLosPerfiles() {
        try {
            List<PerfilCitas> perfiles = servicioPerfilCitas.obtenerTodosLosPerfiles();
            if (tablaResultados != null) {
                tablaResultados.getItems().setAll(perfiles);
                System.out.println("[ControladorPerfil] Perfiles cargados en tabla: " + perfiles.size());
            }
        } catch (Exception e) {
            System.err.println("[ControladorPerfil] Error al cargar perfiles: " + e.getMessage());
            if (tablaResultados != null) {
                tablaResultados.getItems().clear();
            }
            mostrarAlerta("Información", "No hay perfiles registrados o no se pudieron cargar");
        }
    }

    /**
     * Método para buscar perfiles de citas.
     */
    @FXML
    public void buscarPerfiles() {
        String nombre = txtNombre != null ? txtNombre.getText().trim() : "";
        String telefono = txtTelefono != null ? txtTelefono.getText().trim() : "";

        if (nombre.isEmpty() && telefono.isEmpty()) {
            mostrarAlerta("Información", "Mostrando todos los perfiles registrados");
            cargarTodosLosPerfiles();
            return;
        }

        try {
            List<PerfilCitas> resultados;

            if (!nombre.isEmpty() && !telefono.isEmpty()) {
                List<PerfilCitas> porNombre = servicioPerfilCitas.buscarPorNombre(nombre);
                List<PerfilCitas> porTelefono = servicioPerfilCitas.buscarPorTelefono(telefono);

                resultados = porNombre;
                resultados.addAll(porTelefono);

                resultados = resultados.stream()
                        .distinct()
                        .collect(Collectors.toList());

            } else if (!nombre.isEmpty()) {
                resultados = servicioPerfilCitas.buscarPorNombre(nombre);
            } else {
                resultados = servicioPerfilCitas.buscarPorTelefono(telefono);
            }

            if (tablaResultados != null) {
                tablaResultados.getItems().setAll(resultados);
            }

            if (!resultados.isEmpty()) {
                mostrarAlerta("Búsqueda completada",
                        "Se encontraron " + resultados.size() + " resultado(s)");
            } else {
                mostrarAlerta("Búsqueda completada",
                        "No se encontraron resultados para los criterios especificados");
                cargarTodosLosPerfiles();
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error durante la búsqueda: " + e.getMessage());
            e.printStackTrace();
            cargarTodosLosPerfiles();
        }
    }

    /**
     * Método para crear un nuevo perfil de citas.
     */
    @FXML
    public void crearNuevoPerfil() {
        try {
            System.out.println("[ControladorPerfil] Abriendo ventana para crear nuevo perfil de citas...");

            if (ventanaCrearPerfil != null) {
                ventanaCrearPerfil.muestra();
                cargarTodosLosPerfiles();
            } else {
                mostrarAlerta("Error", "No se pudo acceder a la ventana de creación de perfiles");
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de nuevo perfil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Para ver el historial de citas de un perfil seleccionado.
     * Ahora el comportamiento es el mismo para administrador y psicólogo:
     * siempre se abre en una ventana independiente.
     */
    @FXML
    public void verHistorialCitas() {
        try {
            PerfilCitas perfilSeleccionado = tablaResultados != null
                    ? tablaResultados.getSelectionModel().getSelectedItem()
                    : null;

            if (perfilSeleccionado == null) {
                mostrarAlerta("Información",
                        "Seleccione un perfil de la tabla para ver su historial de citas");
                return;
            }

            System.out.println("[ControladorPerfil] Abriendo historial para perfil: "
                    + perfilSeleccionado.getNombreCompleto());

            if (ventanaHistorialCitas != null) {
                // Siempre abrimos el historial como ventana independiente.
                ventanaHistorialCitas.mostrar(perfilSeleccionado);
            } else {
                mostrarAlerta("Error",
                        "No se pudo acceder al módulo de historial de citas");
            }

        } catch (Exception e) {
            mostrarAlerta("Error",
                    "No se pudo abrir el historial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método para limpiar los campos de búsqueda.
     */
    @FXML
    public void limpiarCampos() {
        if (txtNombre != null) {
            txtNombre.clear();
        }
        if (txtTelefono != null) {
            txtTelefono.clear();
        }
        cargarTodosLosPerfiles();
    }

    /**
     * Método para mostrar alertas al usuario.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
