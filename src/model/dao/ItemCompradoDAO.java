package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.bean.Compra;
import model.bean.Grupo;
import model.bean.ItemComprado;
import model.bean.Marca;
import model.bean.Produto;
import model.bean.Subgrupo;

public class ItemCompradoDAO {
    private Connection connection;
    
    public ItemCompradoDAO(){
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public void adicionaItemComprado (Compra compra){
        final String sqlItemComprado = "INSERT INTO ItemComprado (qtdComprada, precoCompra, idCompra, idProduto) VALUES (?, ?, (SELECT MAX(idCompra) FROM Compra), ?)";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlItemComprado);
                int retorno;
                    for(ItemComprado itemComprado:compra.getItensComprados()){
                        stmt.setInt(1, itemComprado.getQtdComprada());
                        stmt.setFloat(2, itemComprado.getPrecoCompra());
                        
                        ProdutoDAO daoprod = new ProdutoDAO();
                        retorno = daoprod.verificaExistenciaProduto(itemComprado.getProduto().getDescricao());
                            if(retorno==-1){ //Produto não existe
                                UnidadeMedidaDAO daoun = new UnidadeMedidaDAO();

                                retorno = daoun.verificaExistenciaUnidadeMedida(itemComprado.getProduto().getUnidadeMedida().getSigla());
                                    if(retorno==-1){ //UN não existe
                                         //Inserir UN
                                         itemComprado.getProduto().getUnidadeMedida().setSigla(itemComprado.getProduto().getUnidadeMedida().getSigla());
                                         itemComprado.getProduto().getUnidadeMedida().setUnidadeMedida(itemComprado.getProduto().getUnidadeMedida().getSigla());
                                         if(daoun.adicionaUnidadeMedida(itemComprado.getProduto().getUnidadeMedida())){
                                            itemComprado.getProduto().getUnidadeMedida().setIdUnidadeMedida(daoun.getLastCodeUnidadeMedida());
                                         }
                                    } else itemComprado.getProduto().getUnidadeMedida().setIdUnidadeMedida(retorno);

                                    Marca marca = new Marca();
                                    marca.setIdMarca(1);
                                    itemComprado.getProduto().setMarca(marca);

                                    Subgrupo subgrupo = new Subgrupo();
                                    subgrupo.setIdSubgrupo(1);
                                    Grupo grupo = new Grupo();
                                    grupo.setIdGrupo(1);
                                    subgrupo.setGrupo(grupo);
                                    itemComprado.getProduto().setSubgrupo(subgrupo);

                                    itemComprado.getProduto().setDataCadastro(definirDataCadastro());
                                    itemComprado.getProduto().setIsHabilitado(1);
                                    ProdutoDAO daop = new ProdutoDAO();
                                        if(daop.adicionaProduto(itemComprado.getProduto())){
                                            itemComprado.getProduto().setIdProduto(daop.getLastCodeProduto());  
                                            stmt.setInt(3, itemComprado.getProduto().getIdProduto());
                                            stmt.execute();
                                        }
                            } else { //Produto existe
                                itemComprado.getProduto().setIdProduto(retorno);
                                daoprod.aumentaEstoqueProduto(itemComprado.getProduto());
                                stmt.setInt(3, itemComprado.getProduto().getIdProduto());
                                stmt.execute();
                            }
                    }
                stmt.close();
                this.connection.close();
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public List<ItemComprado> getListaItemComprado (Compra compra){
        final String sqlItemComprado = "SELECT * FROM ItemComprado WHERE idCompra = ?";
            try {
                List<ItemComprado> listaItemComprado = new ArrayList<>();
                PreparedStatement stmt = this.connection.prepareStatement(sqlItemComprado);
                stmt.setInt(1, compra.getIdCompra());
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        
                        ItemComprado itemComprado = new ItemComprado();
                        itemComprado.setQtdComprada(rs.getInt("qtdComprada"));
                        itemComprado.setPrecoCompra(rs.getFloat("precoCompra"));
                        
                        Produto produto = new Produto();
                        produto.setIdProduto(rs.getInt("idProduto"));
                        ProdutoDAO dao = new ProdutoDAO();
                        produto = dao.getProduto(produto);
                        itemComprado.setProduto(produto);
                        
                        listaItemComprado.add(itemComprado);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaItemComprado;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    private String definirDataCadastro (){
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
	Date date = new Date(); 
	return dateFormat.format(date); 
    }
}
