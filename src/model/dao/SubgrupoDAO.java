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
import model.bean.Subgrupo;

public class SubgrupoDAO {
    private Connection connection;
    
    public SubgrupoDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public boolean adicionaSubgrupo (Subgrupo subgrupo){
        final String sqlSubgrupo = "INSERT INTO Subgrupo (subgrupo, idGrupo)"
                + "VALUES (?, ?)";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlSubgrupo);
            stmt.setString(1, subgrupo.getSubgrupo());
            stmt.setInt(2, subgrupo.getGrupo().getIdGrupo());
            stmt.execute();
            stmt.close();
            this.connection.close();
            return true;
        } catch (SQLException e){
            String erro = e.toString();
                if(erro.contains("Duplicate entry"))
                    JOptionPane.showMessageDialog(null, "Já existe um subgrupo neste grupo com este nome!", "Caixa de Afirmação", 0);
                else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
            return false;
        }
    }
    
    public boolean atualizaSubgrupo (Subgrupo subgrupo){
        final String sqlSubgrupo = "UPDATE Subgrupo SET subgrupo = ? WHERE idSubgrupo = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlSubgrupo);
            stmt.setString(1, subgrupo.getSubgrupo());
            stmt.setInt(2, subgrupo.getIdSubgrupo());
            stmt.execute();
            stmt.close();
            this.connection.close();
            return true;
        } catch (SQLException e){
            String erro = e.toString();
                if(erro.contains("Duplicate entry"))
                    JOptionPane.showMessageDialog(null, "Já existe um subgrupo neste grupo com este nome!", "Caixa de Afirmação", 0);
                else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
            return false;
        }
    }
    
    public boolean removeSubgrupo (Subgrupo subgrupo){
        final String sqlSubgrupo = "DELETE FROM Subgrupo WHERE idSubgrupo = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlSubgrupo);
                stmt.setInt(1, subgrupo.getIdSubgrupo());
                stmt.execute();
                stmt.close();
                this.connection.close();
                return true;
            } catch (SQLException e){
                String erro = e.toString();
                    if(erro.contains("a foreign key constraint fails"))
                        JOptionPane.showMessageDialog(null, "Não foi possível excluir este subgrupo.\nProvavelmente há produtos vinculados a ele!", "Caixa de Afirmação", 0);
                    else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
                return false;
            }
    }
    
    public List<Subgrupo> getListaGrupo (Grupo grupo){
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM Subgrupo WHERE idGrupo = ? ORDER BY idSubgrupo");
            stmt.setInt(1, grupo.getIdGrupo());
            List<Subgrupo> listaSubgrupo = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Subgrupo subgrupo = new Subgrupo();
                    subgrupo.setIdSubgrupo(rs.getInt("idSubgrupo"));
                    subgrupo.setSubgrupo(rs.getString("subgrupo"));
                    subgrupo.setGrupo(grupo);
                    listaSubgrupo.add(subgrupo);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaSubgrupo;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public int getLastCodeSubgrupo (){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT max(idSubgrupo) AS idSubgrupo FROM Subgrupo LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next())
                    code = rs.getInt("idSubgrupo");
            rs.close();
            stmt.close();
            this.connection.close();
            return code;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
