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
import java.util.Collections;

/*
 * Controlador para la gestión de perfiles de citas.
 * Utiliza Spring para la inyección de dependencias y JavaFX para la interfaz gráfica.
 * Incluye funcionalidades para buscar, crear y ver el historial de citas de perfiles.
 * Maneja errores de manera robusta para asegurar una buena experiencia de usuario.
 * Configura la tabla de perfiles de manera segura, evitando errores si alguna columna no está definida en el FXML.
 * Proporciona mensajes informativos al usuario en caso de errores o resultados vacíos.
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

    @Autowired
    private VentanaMenuPsicologo ventanaMenuPsicologo;

    /**
     * Método initialize para configurar la tabla
     */
    @FXML
    public void initialize() {
        System.out.println("ControladorPerfil inicializado");
        System.out.println("VentanaHistorialCitas inyectada: " + (ventanaHistorialCitas != null));
        
        // Configurar tabla de manera segura
        configurarTablaSegura();
        
        // Cargar todos los perfiles
        cargarTodosLosPerfiles();
    }

    /**
     * Configura las columnas de la tabla de manera segura
     */
    private void configurarTablaSegura() {
        try {
            // Configurar cada columna solo si no es null
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
            
            System.out.println("Tabla configurada correctamente");
            
        } catch (Exception e) {
            System.err.println("Advertencia al configurar tabla: " + e.getMessage());
            // No lanzamos excepción para permitir que la aplicación continúe
        }
    }

    /**
     * Carga todos los perfiles al iniciar
     */
    private void cargarTodosLosPerfiles() {
        try {
            List<PerfilCitas> perfiles = servicioPerfilCitas.obtenerTodosLosPerfiles();
            if (tablaResultados != null) {
                tablaResultados.getItems().setAll(perfiles);
                System.out.println("Perfiles cargados en tabla: " + perfiles.size());
            }
        } catch (Exception e) {
            System.err.println("Error al cargar perfiles: " + e.getMessage());
            // En caso de error, mostrar tabla vacía
            if (tablaResultados != null) {
                tablaResultados.getItems().clear();
            }
            mostrarAlerta("Información", "No hay perfiles registrados o no se pudieron cargar");
        }
    }

    /**
     * Método para buscar perfiles de citas
     */
    @FXML
    public void buscarPerfiles() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        
        // Validar que al menos un campo tenga información
        if (nombre.isEmpty() && telefono.isEmpty()) {
            mostrarAlerta("Información", "Mostrando todos los perfiles registrados");
            cargarTodosLosPerfiles();
            return;
        }
        
        try {
            List<PerfilCitas> resultados;
            
            if (!nombre.isEmpty() && !telefono.isEmpty()) {
                // Buscar por ambos criterios
                resultados = servicioPerfilCitas.buscarPorNombre(nombre);
                resultados.addAll(servicioPerfilCitas.buscarPorTelefono(telefono));
                // Eliminar duplicados
                resultados = resultados.stream().distinct().collect(java.util.stream.Collectors.toList());
            } else if (!nombre.isEmpty()) {
                resultados = servicioPerfilCitas.buscarPorNombre(nombre);
            } else {
                resultados = servicioPerfilCitas.buscarPorTelefono(telefono);
            }
            
            // Actualizar la tabla
            if (tablaResultados != null) {
                tablaResultados.getItems().setAll(resultados);
            }
            
            if (!resultados.isEmpty()) {
                mostrarAlerta("Búsqueda completada", 
                    "Se encontraron " + resultados.size() + " resultado(s)");
            } else {
                mostrarAlerta("Búsqueda completada", "No se encontraron resultados para los criterios especificados");
                cargarTodosLosPerfiles(); // Recargar todos si no hay resultados
            }
            
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error durante la búsqueda: " + e.getMessage());
            e.printStackTrace();
            cargarTodosLosPerfiles(); // Recargar todos en caso de error
        }
    }

    /**
     * Método para crear un nuevo perfil de citas
     */
    @FXML
    public void crearNuevoPerfil() {
        try {
            System.out.println("Abriendo ventana para crear nuevo perfil de citas...");
            
            if (ventanaCrearPerfil != null) {
                ventanaCrearPerfil.muestra();
                // Recargar la tabla cuando se cierre la ventana de creación
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
    * Para ver el historial de citas de un perfil seleccionado
     */
    @FXML
    public void verHistorialCitas() {
        try {
            PerfilCitas perfilSeleccionado = tablaResultados.getSelectionModel().getSelectedItem();
            if (perfilSeleccionado != null) {
                System.out.println("Abriendo historial para perfil: " + perfilSeleccionado.getNombreCompleto());
                // Intentar mostrar embebido en la ventana principal y actualizar breadcrumb
                if (ventanaHistorialCitas != null && ventanaMenuPsicologo != null) {
                    try {
                        javafx.scene.Parent vista = ventanaHistorialCitas.getVista(perfilSeleccionado);
                        ventanaMenuPsicologo.actualizaBreadcrumb(java.util.List.of("Inicio", "Perfiles", "Historial"));
                        ventanaMenuPsicologo.cargarVista(vista);
                    } catch (Exception e) {
                        // Fallback a ventana independiente si algo falla
                        e.printStackTrace();
                        ventanaHistorialCitas.mostrar(perfilSeleccionado);
                    }
                } else if (ventanaHistorialCitas != null) {
                    ventanaHistorialCitas.mostrar(perfilSeleccionado);
                } else {
                    mostrarAlerta("Error", "No se pudo acceder al módulo de historial de citas");
                }
            } else {
                mostrarAlerta("Información", "Seleccione un perfil de la tabla para ver su historial de citas");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el historial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método para limpiar los campos de búsqueda
     */
    @FXML
    public void limpiarCampos() {
        if (txtNombre != null) txtNombre.clear();
        if (txtTelefono != null) txtTelefono.clear();
        cargarTodosLosPerfiles(); // Recargar todos los perfiles
    }

    /**
     * Método para mostrar alertas al usuario
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}