package disnaking.Hueco.model;

import java.util.ArrayList;
import java.util.List;

public class Negocio {
    private long id;
    private String name;
    private String eslogan;
    private String direccion;
    private String telefono;
    private String email;
    private List<TramoHorario> horario = new ArrayList<TramoHorario>();
    private List<RedSocial> redesSociales = new ArrayList<RedSocial>();
}
