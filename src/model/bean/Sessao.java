package model.bean;

public class Sessao{
    private static Sessao instance = null;
    private Usuario usuario;
    
    public void setUsuario(Usuario usuario){
       this.usuario = usuario;
    }
    
    public Usuario getUsuario(){
           return usuario;
    }
    
    public static Sessao getInstance(){
          if(instance == null){
                instance = new Sessao();
          }
         return instance;
    }
}
