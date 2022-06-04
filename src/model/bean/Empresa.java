package model.bean;

public class Empresa extends PessoaJuridica {
    private int idEmpresa;
    private int isFilial;
    private Empresa matriz;

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIsFilial() {
        return isFilial;
    }

    public void setIsFilial(int isFilial) {
        this.isFilial = isFilial;
    }

    public Empresa getMatriz() {
        return matriz;
    }

    public void setMatriz(Empresa matriz) {
        this.matriz = matriz;
    }

    @Override
    public String toString() {
        return this.getRazaoSocial();
    }
    
    
}
