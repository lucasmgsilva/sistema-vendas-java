package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.bean.Grupo;

public class GrupoDAO {
    private Connection connection;
    
    public GrupoDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public boolean adicionaGrupo (Grupo grupo){
        final String sqlGrupo = "INSERT INTO Grupo (grupo) VALUES (?)";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlGrupo);
            stmt.setString(1, grupo.getGrupo());
            stmt.execute();
            stmt.close();
            this.connection.close();
            return true;
        } catch (SQLException e){
            String erro = e.toString();
                if(erro.contains("Duplicate entry"))
                    JOptionPane.showMessageDialog(null, "Já existe um grupo com este nome!", "Caixa de Afirmação", 0);
                else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
            return false;
        }
    }
    
    public boolean atualizaGrupo (Grupo grupo){
        final String sqlGrupo = "UPDATE Grupo SET grupo = ? WHERE idGrupo = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlGrupo);
            stmt.setString(1, grupo.getGrupo());
            stmt.setInt(2, grupo.getIdGrupo());
            stmt.execute();
            stmt.close();
            this.connection.close();
            return true;
        } catch (SQLException e){
            String erro = e.toString();
                if(erro.contains("Duplicate entry"))
                    JOptionPane.showMessageDialog(null, "Já existe um grupo com este nome!", "Caixa de Afirmação", 0);
                else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
            return false;
        }
    }
    
    public boolean removeGrupo (Grupo grupo){
        final String sqlSubgrupo = "DELETE FROM Grupo WHERE idGrupo = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlSubgrupo);
                stmt.setInt(1, grupo.getIdGrupo());
                stmt.execute();
                stmt.close();
                this.connection.close();
                return true;
            } catch (SQLException e){
                String erro = e.toString();
                    if(erro.contains("a foreign key constraint fails"))
                        JOptionPane.showMessageDialog(null, "Não foi possível excluir este grupo.\nProvavelmente há subgrupos vinculados a ele!", "Caixa de Afirmação", 0);
                    else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
                return false;
            }
    }
    
    public List<Grupo> getListaGrupo (){
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM Grupo ORDER BY idGrupo");
            List<Grupo> listaGrupo = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Grupo grupo = new Grupo();
                    grupo.setIdGrupo(rs.getInt("idGrupo"));
                    grupo.setGrupo(rs.getString("grupo"));
                    listaGrupo.add(grupo);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaGrupo;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public int getLastCodeGrupo (){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT max(idGrupo) AS idGrupo FROM Grupo LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next())
                    code = rs.getInt("idGrupo");
            rs.close();
            stmt.close();
            this.connection.close();
            return code;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
