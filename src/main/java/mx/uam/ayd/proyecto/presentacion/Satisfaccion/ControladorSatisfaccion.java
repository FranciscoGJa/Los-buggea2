package mx.uam.ayd.proyecto.presentacion.Satisfaccion;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uam.ayd.proyecto.negocio.modelo.Encuesta;
import mx.uam.ayd.proyecto.negocio.modelo.Paciente;
import mx.uam.ayd.proyecto.datos.EncuestaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para la vista de estadísticas del psicólogo.
 * Muestra:
 *  - Listado de pacientes atendidos
 *  - Gráfica con calificaciones (1-5)
 */
@Component
public class ControladorSatisfaccion {

    @FXML
    private TableView<Paciente> tablaPacientes;

    @FXML
    private TableColumn<Paciente, String> colNombre;

    @FXML
    private TableColumn<Paciente, String> colApellido;

    @FXML
    private BarChart<String, Number> graficaCalificaciones;

    @Autowired
    private EncuestaRepositorio encuestaRepositorio;

    private int idPsicologo; // Se recibe desde la ventana que abra esta vista

    /**
     * Inicializa el controlador una vez que el FXML ha sido cargado.
     */
    @FXML
    public void initialize() {
        configurarTabla();
    }

    /**
     * Configura las columnas de la tabla de pacientes.
     */
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
    }

    /**
     * Método para recibir el id del psicólogo, cargar pacientes y estadísticas.
     *
     * @param idPsicologo identificador del psicólogo
     */
    public void cargarDatosPsicologo(int idPsicologo) {
        this.idPsicologo = idPsicologo;

        cargarPacientes();
        cargarGrafica();
    }

    /**
     * Carga al TableView la lista de pacientes asignados al psicólogo.
     */
    private void cargarPacientes() {
        // TODO: Implementar obtención de pacientes del psicólogo
        // Por ahora se deja vacío para permitir la compilación
        tablaPacientes.getItems().clear();
    }

    /**
     * Carga la gráfica de calificaciones basadas en las encuestas asociadas al psicólogo.
     */
    private void cargarGrafica() {
        List<Encuesta> encuestas = encuestaRepositorio.findByIdPsicologo(idPsicologo);

        Map<Integer, Long> conteo =
                encuestas.stream()
                        .collect(Collectors.groupingBy(Encuesta::getCalificacion, Collectors.counting()));

        graficaCalificaciones.getData().clear();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Evaluaciones");

        for (int i = 1; i <= 5; i++) {
            long total = conteo.getOrDefault(i, 0L);
            serie.getData().add(new XYChart.Data<>(String.valueOf(i), total));
        }

        graficaCalificaciones.getData().add(serie);
    }
}
