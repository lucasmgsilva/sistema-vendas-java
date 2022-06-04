package model.dao;

import com.mysql.jdbc.StringUtils;
import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.bean.Pessoa;
import model.bean.Usuario;
import model.bean.Venda;
import others.Data;

public class VendaDAO {
    private Connection connection;
    
    public VendaDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public void adicionaVenda (Venda venda){
        final String sqlVenda = "INSERT INTO Venda (dataVenda, desconto, precoTotal, idPessoa, idUsuario) VALUES (?, ?, ?, ?, ?)";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlVenda);
                stmt.setString(1, Data.dataHoraParaBanco(venda.getDataVenda()));
                stmt.setFloat(2, venda.getDesconto());
                stmt.setFloat(3, venda.getPrecoTotal());
                stmt.setInt(4, ((Pessoa) venda.getPessoa()).getIdPessoa());
                stmt.setInt(5, venda.getUsuario().getIdUsuario());
                stmt.execute();
                stmt.close();
                this.connection.close();
                
                ItemVendidoDAO dao = new ItemVendidoDAO();
                dao.adicionaItemVendido(venda);
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public List<Venda> getListaVenda (){
        final String sqlVenda = "SELECT * FROM Venda";
            try {
                List<Venda> listaVenda = new ArrayList<>();
                PreparedStatement stmt = this.connection.prepareStatement(sqlVenda);
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        Venda venda = new Venda();
                        venda.setIdVenda(rs.getInt("idVenda"));
                        venda.setDataVenda(Data.dataHoraParaAplicacao(rs.getString("dataVenda")));
                        venda.setDesconto(rs.getFloat("desconto"));
                        venda.setPrecoTotal(rs.getFloat("precoTotal"));

                        PessoaDAO daop = new PessoaDAO();                    
                        Object cliente = daop.getPessoa(rs.getInt("idPessoa"));
                        venda.setPessoa(cliente);
                            
                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("idUsuario"));
                        UsuarioDAO daous = new UsuarioDAO();
                        usuario = daous.getUsuario(usuario);
                        venda.setUsuario(usuario);

                        /*ItemVendidoDAO dao = new ItemVendidoDAO();
                        venda.setItensVendidos(dao.getListaItemVendido(venda));*/

                        listaVenda.add(venda);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaVenda;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public List<Venda> buscaVenda (String palavrasChave, int index){
        String sqlVenda = "SELECT Venda.*, PessoaFisica.nome FROM Venda "
                        + "INNER JOIN Usuario ON Usuario.idUsuario = Venda.idUsuario "
                        + "INNER JOIN PessoaFisica ON PessoaFisica.idPessoaFisica = Usuario.idPessoaFisica WHERE ";
            
            if(index==0)
                sqlVenda += "Venda.idVenda like ?";
            else if (index==1)
                if(!StringUtils.isNullOrEmpty(palavrasChave)){
                sqlVenda = "(SELECT Venda.*, PessoaFisica.nome, Cliente.nome FROM Venda "
                        + "INNER JOIN Usuario ON Usuario.idUsuario = Venda.idUsuario  "
                        + "INNER JOIN PessoaFisica ON PessoaFisica.idPessoaFisica = Usuario.idPessoaFisica "
                        + "INNER JOIN Pessoa ON Venda.idPessoa = Pessoa.idPessoa "
                        + "INNER JOIN PessoaFisica Cliente ON Cliente.idPessoa = Pessoa.idPessoa WHERE Cliente.nome like ?) "
                        + "UNION "
                        + "(SELECT Venda.*, PessoaFisica.nome, Cliente.razaoSocial FROM Venda  "
                        + "INNER JOIN Usuario ON Usuario.idUsuario = Venda.idUsuario  "
                        + "INNER JOIN PessoaFisica ON PessoaFisica.idPessoaFisica = Usuario.idPessoaFisica  "
                        + "INNER JOIN Pessoa ON Venda.idPessoa = Pessoa.idPessoa  "
                        + "INNER JOIN PessoaJuridica Cliente ON Cliente.idPessoa = Pessoa.idPessoa WHERE Cliente.razaoSocial like ?)";
                } else sqlVenda += "Venda.idVenda like ?";
            else if (index==2)
                sqlVenda += "PessoaFisica.nome like ?";
            try {
                List<Venda> listaVenda = new ArrayList<>();
                PreparedStatement stmt = this.connection.prepareStatement(sqlVenda);
                stmt.setString(1, "%" + palavrasChave + "%"); 
                    if(index==1 && !StringUtils.isNullOrEmpty(palavrasChave))
                        stmt.setString(2, "%" + palavrasChave + "%"); 
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        Venda venda = new Venda();
                        venda.setIdVenda(rs.getInt("idVenda"));
                        venda.setDataVenda(Data.dataHoraParaAplicacao(rs.getString("dataVenda")));
                        venda.setDesconto(rs.getFloat("desconto"));
                        venda.setPrecoTotal(rs.getFloat("precoTotal"));

                        PessoaDAO daop = new PessoaDAO();                    
                        Object cliente = daop.getPessoa(rs.getInt("idPessoa"));
                        venda.setPessoa(cliente);
                            
                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("idUsuario"));
                        UsuarioDAO daous = new UsuarioDAO();
                        usuario = daous.getUsuario(usuario);
                        venda.setUsuario(usuario);

                        /*ItemVendidoDAO dao = new ItemVendidoDAO();
                        venda.setItensVendidos(dao.getListaItemVendido(venda));*/

                        listaVenda.add(venda);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaVenda;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public int getLastCodeVenda(){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT MAX(idVenda) AS idVenda FROM Venda LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = 0;
                if(rs.next()){
                    code = rs.getInt("idVenda");
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
