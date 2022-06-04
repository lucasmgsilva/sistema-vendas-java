package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.bean.Estado;

public class EstadoDAO {
    private Connection connection;
    
    public EstadoDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public List<Estado> getListaEstado(){
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM Estado");
            List<Estado> listaEstado = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("idEstado"));
                    estado.setEstado(rs.getString("estado"));
                    estado.setUf(rs.getString("uf"));
                    listaEstado.add(estado);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaEstado;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
