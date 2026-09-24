package disnaking.Hueco.DTO.Negocio;

import java.time.DayOfWeek;
import java.util.List;

public class negocioDTO {
    private String eslogan;
    private String sobreNosotros;
    private String direccion;
    private String telefono;
    private String email;
    private List<diaHorarioDTO> horario;
    private List<redSocialDTO> redesSociales;
    private List<testimonioDTO> testimonios;
    private estadoHoyDTO estadoHoy;
    // Día de hoy en la zona horaria del comercio
    private DayOfWeek hoy;

    public negocioDTO(String eslogan, String sobreNosotros, String direccion, String telefono, String email,
                      List<diaHorarioDTO> horario, List<redSocialDTO> redesSociales,
                      List<testimonioDTO> testimonios, estadoHoyDTO estadoHoy, DayOfWeek hoy) {
        this.eslogan = eslogan;
        this.sobreNosotros = sobreNosotros;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.horario = horario;
        this.redesSociales = redesSociales;
        this.testimonios = testimonios;
        this.estadoHoy = estadoHoy;
        this.hoy = hoy;
    }

    public String getEslogan() {
        return eslogan;
    }

    public String getSobreNosotros() {
        return sobreNosotros;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public List<diaHorarioDTO> getHorario() {
        return horario;
    }

    public List<redSocialDTO> getRedesSociales() {
        return redesSociales;
    }

    public List<testimonioDTO> getTestimonios() {
        return testimonios;
    }

    public estadoHoyDTO getEstadoHoy() {
        return estadoHoy;
    }

    public DayOfWeek getHoy() {
        return hoy;
    }
}
