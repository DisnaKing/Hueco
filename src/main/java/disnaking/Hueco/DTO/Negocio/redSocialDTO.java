package disnaking.Hueco.DTO.Negocio;

import disnaking.Hueco.model.TipoRedSocial;

public class redSocialDTO {
    private TipoRedSocial tipo;
    private String url;

    public redSocialDTO(TipoRedSocial tipo, String url) {
        this.tipo = tipo;
        this.url = url;
    }

    public TipoRedSocial getTipo() {
        return tipo;
    }

    public String getUrl() {
        return url;
    }
}
