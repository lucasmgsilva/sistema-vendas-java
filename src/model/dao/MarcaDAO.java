package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.bean.Marca;

public class MarcaDAO {
    private Connection connection;
    
    public MarcaDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public boolean adicionaMarca (Marca marca){
        final String sqlMarca = "INSERT INTO Marca (marca) VALUES (?)";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlMarca);
            stmt.setString(1, marca.getMarca());
            stmt.execute();
            stmt.close();
            this.connection.close();
            return true;
        } catch (SQLException e){
            String erro = e.toString();
                if(erro.contains("Duplicate entry"))
                    JOptionPane.showMessageDialog(null, "Já existe uma marca com este nome!", "Caixa de Afirmação", 0);
                else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
            return false;
        }
    }
    
    public boolean atualizaMarca (Marca marca){
        final String sqlMarca = "UPDATE Marca set marca = ? WHERE idMarca = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlMarca);
                stmt.setString(1, marca.getMarca());
                stmt.setInt(2, marca.getIdMarca());
                stmt.execute();
                stmt.close();
                this.connection.close();
                return true;
            } catch (SQLException e){
                String erro = e.toString();
                    if(erro.contains("Duplicate entry"))
                        JOptionPane.showMessageDialog(null, "Já existe uma marca com este nome!", "Caixa de Afirmação", 0);
                    else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
                return false;
            }
    }
    
    public boolean removeMarca (Marca marca){
        final String sqlMarca = "DELETE FROM Marca WHERE idMarca = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlMarca);
                stmt.setInt(1, marca.getIdMarca());
                stmt.execute();
                stmt.close();
                this.connection.close();
                return true;
            } catch (SQLException e){
                String erro = e.toString();
                    if(erro.contains("a foreign key constraint fails"))
                        JOptionPane.showMessageDialog(null, "Não foi possível excluir esta marca.\nProvavelmente há produtos vinculados a ela!", "Caixa de Afirmação", 0);
                    else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
                return false;
            }
    }
    
    public List<Marca> getListaMarca (){
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM Marca ORDER BY idMarca");
            List<Marca> listaMarca = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Marca marca = new Marca();
                    marca.setIdMarca(rs.getInt("idMarca"));
                    marca.setMarca(rs.getString("marca"));
                    listaMarca.add(marca);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaMarca;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public int getLastCodeMarca (){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT max(idMarca) AS idMarca FROM Marca LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next())
                    code = rs.getInt("idMarca");
            rs.close();
            stmt.close();
            this.connection.close();
            return code;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
