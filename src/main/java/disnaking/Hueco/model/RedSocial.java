package disnaking.Hueco.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class RedSocial {

    @Enumerated(EnumType.STRING)
    private TipoRedSocial tipo;
    private String url;

    public RedSocial() {}

    public TipoRedSocial getTipo() {
        return tipo;
    }

    public String getUrl() {
        return url;
    }
}
