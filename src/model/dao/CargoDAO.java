package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.bean.Cargo;

public class CargoDAO {
    private Connection connection;
    
    public CargoDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public boolean adicionaCargo (Cargo cargo){
        final String sqlCargo = "INSERT INTO Cargo (cargo, permissoes)"
                + "VALUES (?, ?)";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlCargo);
            stmt.setString(1, cargo.getCargo());
            stmt.setString(2, cargo.getPermissoes());
            stmt.execute();
            stmt.close();
            this.connection.close();
            return true;
        } catch (SQLException e){
            String erro = e.toString();
                if(erro.contains("Duplicate entry"))
                    JOptionPane.showMessageDialog(null, "Já existe um cargo com este nome!", "Caixa de Afirmação", 0);
                else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
            return false;
        }
    }
    
    public boolean atualizaCargo (Cargo cargo){
        final String sqlCargo = "UPDATE Cargo set cargo = ?, permissoes = ? WHERE idCargo = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlCargo);
                stmt.setString(1, cargo.getCargo());
                stmt.setString(2, cargo.getPermissoes());
                stmt.setInt(3, cargo.getIdCargo());
                stmt.execute();
                stmt.close();
                this.connection.close();
                return true;
            } catch (SQLException e){
                String erro = e.toString();
                    if(erro.contains("Duplicate entry"))
                        JOptionPane.showMessageDialog(null, "Já existe um cargo com este nome!", "Caixa de Afirmação", 0);
                    else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
                return false;
            }
    }
    
    public boolean removeCargo (Cargo cargo){
        final String sqlCargo = "DELETE FROM Cargo WHERE idCargo = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlCargo);
                stmt.setInt(1, cargo.getIdCargo());
                stmt.execute();
                stmt.close();
                this.connection.close();
                return true;
            } catch (SQLException e){
                String erro = e.toString();
                    if(erro.contains("a foreign key constraint fails"))
                        JOptionPane.showMessageDialog(null, "Não foi possível excluir este cargo.\nProvavelmente há usuários vinculados a ele!", "Caixa de Afirmação", 0);
                    else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
                return false;
            }
    }
    
    public List<Cargo> getListaCargo (){
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM Cargo ORDER BY idCargo");
            List<Cargo> listaCargo = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Cargo cargo = new Cargo();
                    cargo.setIdCargo(rs.getInt("idCargo"));
                    cargo.setCargo(rs.getString("cargo"));
                    cargo.setPermissoes(rs.getString("permissoes"));
                    listaCargo.add(cargo);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaCargo;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public int getLastCodeCargo (){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT max(idCargo) AS idCargo FROM Cargo LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next())
                    code = rs.getInt("idCargo");
            rs.close();
            stmt.close();
            this.connection.close();
            return code;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
