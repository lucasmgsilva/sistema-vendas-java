package model.bean;
public class Subgrupo {
    private int idSubgrupo;
    private String subgrupo;
    private Grupo grupo;

    public int getIdSubgrupo() {
        return idSubgrupo;
    }

    public void setIdSubgrupo(int idSubgrupo) {
        this.idSubgrupo = idSubgrupo;
    }

    public String getSubgrupo() {
        return subgrupo;
    }

    public void setSubgrupo(String subgrupo) {
        this.subgrupo = subgrupo;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    @Override
    public String toString() {
        return this.getSubgrupo();
    }
}
