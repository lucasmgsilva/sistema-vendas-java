package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.bean.UnidadeMedida;

public class UnidadeMedidaDAO {
    private Connection connection;
    
    public UnidadeMedidaDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public boolean adicionaUnidadeMedida (UnidadeMedida unidadeMedida){
        final String sqlUnidadeMedida = "INSERT INTO UnidadeMedida (sigla, unidadeMedida)"
                + "VALUES (?, ?)";
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement(sqlUnidadeMedida);
            stmt.setString(1, unidadeMedida.getSigla());
            stmt.setString(2, unidadeMedida.getUnidadeMedida());
            stmt.execute();
            stmt.close();
            this.connection.close();
            return true;
        } catch (SQLException e){
            String erro = e.toString();
                if(erro.contains("Duplicate entry"))
                    JOptionPane.showMessageDialog(null, "Já existe uma sigla com este nome!", "Caixa de Afirmação", 0);
                else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
            return false;
        }
    }
    
    public boolean atualizaUnidadeMedida (UnidadeMedida unidadeMedida){
        final String sqlCargo = "UPDATE UnidadeMedida set sigla = ?, unidadeMedida = ? WHERE idUnidadeMedida = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlCargo);
                stmt.setString(1, unidadeMedida.getSigla());
                stmt.setString(2, unidadeMedida.getUnidadeMedida());
                stmt.setInt(3, unidadeMedida.getIdUnidadeMedida());
                stmt.execute();
                stmt.close();
                this.connection.close();
                return true;
            } catch (SQLException e){
                String erro = e.toString();
                    if(erro.contains("Duplicate entry"))
                        JOptionPane.showMessageDialog(null, "Já existe uma sigla com este nome!", "Caixa de Afirmação", 0);
                    else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
                return false;
            }
    }
    
    public boolean removeUnidadeMedida (UnidadeMedida unidadeMedida){
        final String sqlCargo = "DELETE FROM UnidadeMedida WHERE idUnidadeMedida = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlCargo);
                stmt.setInt(1, unidadeMedida.getIdUnidadeMedida());
                stmt.execute();
                stmt.close();
                this.connection.close();
                return true;
            } catch (SQLException e){
                String erro = e.toString();
                    if(erro.contains("a foreign key constraint fails"))
                        JOptionPane.showMessageDialog(null, "Não foi possível excluir esta unidade de medida.\nProvavelmente há produtos vinculados a ela!", "Caixa de Afirmação", 0);
                    else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
                return false;
            }
    }
    
    public List<UnidadeMedida> getListaUnidadeMedida (){
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM UnidadeMedida ORDER BY idUnidadeMedida");
            List<UnidadeMedida> listaUnidadeMedida = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    UnidadeMedida unidadeMedida = new UnidadeMedida();
                    unidadeMedida.setIdUnidadeMedida(rs.getInt("idUnidadeMedida"));
                    unidadeMedida.setSigla(rs.getString("sigla"));
                    unidadeMedida.setUnidadeMedida(rs.getString("unidadeMedida"));
                    listaUnidadeMedida.add(unidadeMedida);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaUnidadeMedida;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public int getLastCodeUnidadeMedida (){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT max(idUnidadeMedida) AS idUnidadeMedida FROM UnidadeMedida LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next())
                    code = rs.getInt("idUnidadeMedida");
            rs.close();
            stmt.close();
            this.connection.close();
            return code;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public int verificaExistenciaUnidadeMedida(String un){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM UnidadeMedida WHERE sigla = ?");
            stmt.setString(1, un);
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next()){
                    code = rs.getInt("idUnidadeMedida");
                }
            stmt.close();
            rs.close();
            this.connection.close();
            return code;
        } catch (SQLException e){
            throw new RuntimeException();
        }
    }
}
