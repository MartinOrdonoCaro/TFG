package tfg.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "series")
public class Serie implements Serializable {

    private static final long serialVersionUID = 1723039391417629631L;

    @Id
    private String id;
    @Indexed(unique=true)
    private String codigo;
    private String tasa;
    private String unidad;
    private String descripcion;
    private String nombreFuente;
    private String siglasFuente;
    private String territorio;
    private String periodicidad;
    private String origen;
    private Set<Dato> datos;
}
