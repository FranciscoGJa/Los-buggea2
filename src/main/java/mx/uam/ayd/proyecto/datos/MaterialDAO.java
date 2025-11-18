package mx.uam.ayd.proyecto.datos;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import mx.uam.ayd.proyecto.negocio.modelo.Material;
@Repository
public class MaterialDAO {

    @Autowired
    private JdbcTemplate jdbc;

    
    public List<Material> obtenerMateriales() {
    return jdbc.query("SELECT * FROM Material", (rs, rowNum) ->
        new Material(
            rs.getInt("id_material"),
            rs.getString("nombre"),
            rs.getString("tipo"),
            rs.getString("estado"),
            rs.getObject("id_psicologo") != null ? rs.getInt("id_psicologo") : null,
            rs.getString("imagen")
        )
    );
}


    public void apartarMaterial(int idMaterial, int idPsicologo) {
        jdbc.update("UPDATE Material SET estado='Apartado', id_psicologo=? WHERE id_material=? AND estado='Disponible'",
                idPsicologo, idMaterial);
    }

    public void liberarMaterial(int idMaterial) {
        jdbc.update("UPDATE Material SET estado='Disponible', id_psicologo=NULL WHERE id_material=?", idMaterial);
    }
}
