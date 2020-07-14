package tfg.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Serie implements Serializable {

    private static final long serialVersionUID = 1723039391417629631L;

    @Id
    private Long id;
    private String codigo;
    private String descripcion;
    private String periodicidad;
    private String ruta;
    private String fuente;
    private LocalDate inicioPeriodo;
    private LocalDate finPeriodo;
    private String nombreFuente;
    private String siglasFuente;
    private Set<Dato> datos;
}
