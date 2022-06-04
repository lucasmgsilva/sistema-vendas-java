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
import javax.swing.JOptionPane;
import model.bean.Compra;
import model.bean.PessoaJuridica;
import model.bean.Usuario;
import others.Data;

public class CompraDAO {
    private Connection connection;
    
    public CompraDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public boolean adicionaCompra (Compra compra){
        final String sqlCompra = "INSERT INTO Compra (dataCompra, chaveAcesso, precoTotal, idPessoaJuridica, idUsuario) VALUES (?, ?, ?, ?, ?)";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlCompra);
                stmt.setString(1, Data.dataHoraParaBanco(compra.getDataCompra()));
                stmt.setString(2, compra.getChaveAcesso());
                stmt.setFloat(3, compra.getPrecoTotal());

                //Consultar existência do fornecedor
                PessoaJuridicaDAO daopj = new PessoaJuridicaDAO();
                int retorno = daopj.verificaExistenciaFornecedor(compra.getFornecedor().getCnpj());
                
                    if(retorno==-1){ //Fornecedor não existe
                        //Inserir fornecedor
                        CidadeDAO daocid = new CidadeDAO();
                        compra.getFornecedor().setCidade(daocid.getCidadeEstado(compra.getFornecedor().getCidade()));
                        compra.getFornecedor().setDataCadastro(definirDataCadastro());
                        compra.getFornecedor().setDataAbertura(definirDataCadastro());
                        daopj.adicionaPessoaJuridica(compra.getFornecedor());
                        compra.getFornecedor().setIdPessoaJuridica(daopj.getLastCodePessoaJuridica());
                    } else compra.getFornecedor().setIdPessoaJuridica(retorno);
                    
                stmt.setInt(4, compra.getFornecedor().getIdPessoaJuridica());
                stmt.setInt(5, compra.getUsuario().getIdUsuario());
                stmt.execute();
                stmt.close();
                this.connection.close();
                
                //Inserir Itens de Compra
                ItemCompradoDAO dao = new ItemCompradoDAO();
                dao.adicionaItemComprado(compra);
                return true;
            } catch (SQLException e){
                String erro = e.toString();
                    if(erro.contains("Duplicate entry"))
                        JOptionPane.showMessageDialog(null, "Não foi possível incluir esta compra.\nProvavelmente este XML já foi importado!", "Caixa de Afirmação", 0);
                    else JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado: \n" + erro , "Caixa de Afirmação", 0);
                return false;
            }
    }
    
    public List<Compra> getListaCompra (){
        final String sqlCompra = "SELECT * FROM Compra";
            try {
                List<Compra> listaCompra = new ArrayList<>();
                PreparedStatement stmt = this.connection.prepareStatement(sqlCompra);
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        Compra compra = new Compra();
                        compra.setIdCompra(rs.getInt("idCompra"));
                        compra.setDataCompra(Data.dataHoraParaAplicacao(rs.getString("dataCompra")));
                        compra.setChaveAcesso(rs.getString("chaveAcesso"));
                        compra.setPrecoTotal(rs.getFloat("precoTotal"));

                        PessoaJuridicaDAO daopj = new PessoaJuridicaDAO();                    
                        PessoaJuridica fornecedor = daopj.getFornecedor(rs.getInt("idPessoaJuridica"));
                        compra.setFornecedor(fornecedor);
                            
                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("idUsuario"));
                        UsuarioDAO daous = new UsuarioDAO();
                        usuario = daous.getUsuario(usuario);
                        compra.setUsuario(usuario);

                        listaCompra.add(compra);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaCompra;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public List<Compra> buscaCompra (String palavrasChave, int index){
        String sqlCompra = "SELECT Compra.*, PessoaFisica.nome, PessoaJuridica.razaoSocial FROM Compra " +
                        "INNER JOIN Usuario ON Compra.idUsuario = Usuario.idUsuario " +
                        "INNER JOIN PessoaFisica ON Usuario.idPessoaFisica = PessoaFisica.idPessoaFisica " +
                        "INNER JOIN PessoaJuridica ON Compra.idPessoaJuridica = PessoaJuridica.idPessoaJuridica WHERE ";
            if(index==0)
                sqlCompra += "Compra.idCompra like ?";
            else if (index==1)
                sqlCompra += "PessoaJuridica.razaoSocial like ?";
            else if (index==2)
                sqlCompra += "Compra.chaveAcesso like ?";
            else if (index==3)
                sqlCompra += "PessoaFisica.nome like ?";
        
            try {
                List<Compra> listaCompra = new ArrayList<>();
                PreparedStatement stmt = this.connection.prepareStatement(sqlCompra);
                stmt.setString(1, "%" + palavrasChave + "%"); 
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        Compra compra = new Compra();
                        compra.setIdCompra(rs.getInt("idCompra"));
                        compra.setDataCompra(Data.dataHoraParaAplicacao(rs.getString("dataCompra")));
                        compra.setChaveAcesso(rs.getString("chaveAcesso"));
                        compra.setPrecoTotal(rs.getFloat("precoTotal"));

                        PessoaJuridicaDAO daopj = new PessoaJuridicaDAO();                    
                        PessoaJuridica fornecedor = daopj.getFornecedor(rs.getInt("idPessoaJuridica"));
                        compra.setFornecedor(fornecedor);
                            
                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("idUsuario"));
                        UsuarioDAO daous = new UsuarioDAO();
                        usuario = daous.getUsuario(usuario);
                        compra.setUsuario(usuario);

                        listaCompra.add(compra);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaCompra;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public int getLastCodeCompra(){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT MAX(idCompra) AS idCompra FROM Compra LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = 0;
                if(rs.next()){
                    code = rs.getInt("idCompra");
                }
            stmt.close();
            rs.close();
            this.connection.close();
            return code;
        } catch (SQLException e){
            throw new RuntimeException();
        }
    }
    
    private String definirDataCadastro (){
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
	Date date = new Date(); 
	return dateFormat.format(date); 
    }
}
