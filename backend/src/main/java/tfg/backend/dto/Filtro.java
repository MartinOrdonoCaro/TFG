package tfg.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Filtro implements Serializable {

    private static final long serialVersionUID = 2235815843800377926L;

    private List<String> territorios;
    private List<String> fuentes;
    private List<String> periodos;
}