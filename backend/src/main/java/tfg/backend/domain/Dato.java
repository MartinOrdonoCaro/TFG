package tfg.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dato implements Serializable {

    private static final long serialVersionUID = -8480297055116605621L;

    @Id
    private Long id;
    private String anio;
    private String periodo;
    private Float valor;
    private Serie serie;
}
