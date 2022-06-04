package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.bean.Cidade;
import model.bean.Estado;

public class CidadeDAO {
    private Connection connection;
    
    public CidadeDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public List<Cidade> getListaCidade(Estado estado){
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM Cidade WHERE idEstado = ?");
            stmt.setInt(1, estado.getIdEstado());
            List<Cidade> listaCidade = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("idCidade"));
                    cidade.setCidade(rs.getString("cidade"));
                    cidade.setEstado(estado);
                    listaCidade.add(cidade);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaCidade;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public Cidade getCidadeEstado(Cidade cidade){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM Cidade INNER JOIN Estado ON Estado.idEstado = Cidade.idEstado WHERE cidade like ? AND uf like ?");
            stmt.setString(1, "%" + cidade.getCidade() + "%"); 
            stmt.setString(2, "%" + cidade.getEstado().getUf() + "%"); 
            ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.getEstado().setIdEstado(rs.getInt("Estado.idEstado"));
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return cidade;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
