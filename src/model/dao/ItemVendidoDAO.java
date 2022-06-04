package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.bean.ItemVendido;
import model.bean.Produto;
import model.bean.Venda;

public class ItemVendidoDAO {
    private Connection connection;
    
    public ItemVendidoDAO(){
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public void adicionaItemVendido (Venda venda){
        final String sqlItemVendido = "INSERT INTO ItemVendido (qtdVendida, precoVenda, idVenda, idProduto) VALUES (?, ?, (SELECT MAX(idVenda) FROM Venda), ?)";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlItemVendido);
                    for(ItemVendido itemVendido:venda.getItensVendidos()){
                        stmt.setInt(1, itemVendido.getQtdVendida());
                        stmt.setFloat(2, itemVendido.getPrecoVenda());
                        stmt.setInt(3, itemVendido.getProduto().getIdProduto());
                        stmt.execute();
                    }
                stmt.close();
                this.connection.close();
                
                ProdutoDAO dao = new ProdutoDAO();
                dao.baixaEstoqueProduto(venda.getItensVendidos());
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public List<ItemVendido> getListaItemVendido (Venda venda){
        final String sqlItemVendido = "SELECT * FROM ItemVendido WHERE idVenda = ?";
            try {
                List<ItemVendido> listaItemVendido = new ArrayList<>();
                PreparedStatement stmt = this.connection.prepareStatement(sqlItemVendido);
                stmt.setInt(1, venda.getIdVenda());
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        
                        ItemVendido itemVendido = new ItemVendido();
                        itemVendido.setQtdVendida(rs.getInt("qtdVendida"));
                        itemVendido.setPrecoVenda(rs.getFloat("precoVenda"));
                        
                        Produto produto = new Produto();
                        produto.setIdProduto(rs.getInt("idProduto"));
                        ProdutoDAO dao = new ProdutoDAO();
                        produto = dao.getProduto(produto);
                        itemVendido.setProduto(produto);
                        
                        listaItemVendido.add(itemVendido);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaItemVendido;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
}
