package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import others.Data;
import model.bean.Pessoa;
import model.bean.PessoaFisica;
import model.bean.PessoaJuridica;

public class PessoaDAO {
    private Connection connection;
    
    public PessoaDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public void adicionaPessoa(Pessoa pessoa){
        final String sqlPessoa = "INSERT INTO Pessoa (logradouro, numero, complemento, bairro,"
                + " idCidade, cep, email, telefoneFixo, telefoneCelular, observacoes, dataCadastro, isHabilitado)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlPessoa);
            stmt.setString(1, pessoa.getLogradouro());
            stmt.setString(2, pessoa.getNumero());
            stmt.setString(3, pessoa.getComplemento());
            stmt.setString(4, pessoa.getBairro());
            stmt.setInt(5, pessoa.getCidade().getIdCidade());
            stmt.setString(6, pessoa.getCep());
            stmt.setString(7, pessoa.getEmail());
            stmt.setString(8, pessoa.getTelefoneFixo());
            stmt.setString(9, pessoa.getTelefoneCelular());
            stmt.setString(10, pessoa.getObservacoes());
            stmt.setString(11, Data.dataHoraParaBanco(pessoa.getDataCadastro()));
            stmt.setInt(12, pessoa.getIsHabilitado());
            stmt.execute();
            stmt.close();
            this.connection.close();          
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public void atualizaPessoa(Pessoa pessoa){
        final String sqlPessoa = "UPDATE Pessoa SET logradouro = ?, numero = ?, "
                + "complemento = ?, bairro = ?, idCidade = ?, cep = ?, email = ?, telefoneFixo = ?, telefoneCelular = ?, observacoes = ?, dataCadastro = ?, isHabilitado = ? WHERE idPessoa = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlPessoa);
            stmt.setString(1, pessoa.getLogradouro());
            stmt.setString(2, pessoa.getNumero());
            stmt.setString(3, pessoa.getComplemento());
            stmt.setString(4, pessoa.getBairro());
            stmt.setInt(5, pessoa.getCidade().getIdCidade());
            stmt.setString(6, pessoa.getCep());
            stmt.setString(7, pessoa.getEmail());
            stmt.setString(8, pessoa.getTelefoneFixo());
            stmt.setString(9, pessoa.getTelefoneCelular());
            stmt.setString(10, pessoa.getObservacoes());
            stmt.setString(11, Data.dataHoraParaBanco(pessoa.getDataCadastro()));
            stmt.setInt(12, pessoa.getIsHabilitado());
            stmt.setInt(13, pessoa.getIdPessoa());
            stmt.execute();
            stmt.close();
            this.connection.close();          
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public boolean removePessoa (Pessoa pessoa){
        final String sqlProduto = "DELETE FROM Pessoa WHERE idPessoa = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlProduto);
                stmt.setInt(1, pessoa.getIdPessoa());
                stmt.execute();
                this.connection.close();
                return true;
            } catch (SQLException e){
                if(e.toString().contains("Cannot delete or update a parent row"))
                    return false;
            } 
        return false;
    }
    
    public int getLastCodePessoa(){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT MAX(idPessoa) AS idPessoa FROM Pessoa LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next()){
                    code = rs.getInt("idPessoa");
                }
            stmt.close();
            rs.close();
            this.connection.close();
            return code;
        } catch (SQLException e){
            throw new RuntimeException();
        }
    }
    
    public Object getPessoa (int idPessoa){
        final String sqlPessoa = "(SELECT PessoaFisica.idPessoaFisica AS idPessoaFisicaJuridica, PessoaFisica.nome as nomeRazaoSocial, PessoaFisica.cpf as cpfCnpj, Pessoa.idPessoa FROM PessoaFisica "
                + "INNER JOIN Pessoa ON PessoaFisica.idPessoa = Pessoa.idPessoa WHERE idPessoaFisica NOT IN(SELECT idPessoaFisica FROM Usuario) AND PessoaFisica.idPessoa = ?) "
                + "UNION "
                + "(SELECT PessoaJuridica.idPessoaJuridica, PessoaJuridica.razaoSocial, PessoaJuridica.cnpj, Pessoa.idPessoa FROM PessoaJuridica "
                + "INNER JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa WHERE isFornecedor = 0 AND idPessoaJuridica NOT IN(SELECT idPessoaJuridica FROM Empresa) AND PessoaJuridica.idPessoa = ?)";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlPessoa);
                stmt.setInt(1, idPessoa);
                stmt.setInt(2, idPessoa);
                ResultSet rs = stmt.executeQuery();
                Object cliente = null;
                    if(rs.next()){
                        if(rs.getString("cpfCnpj").length()==14){
                            PessoaFisica pf = new PessoaFisica();
                            pf.setIdPessoaFisica(rs.getInt("idPessoaFisicaJuridica"));
                            pf.setNome(rs.getString("nomeRazaoSocial"));
                            pf.setCpf(rs.getString("cpfCnpj"));
                            pf.setIdPessoa(idPessoa);
                            cliente = pf;
                        } else {
                            PessoaJuridica pj = new PessoaJuridica();
                            pj.setIdPessoaJuridica(rs.getInt("idPessoaFisicaJuridica"));
                            pj.setRazaoSocial(rs.getString("nomeRazaoSocial"));
                            pj.setCnpj(rs.getString("cpfCnpj"));
                            pj.setIdPessoa(idPessoa);
                            cliente = pj;
                        }
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return cliente;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public boolean verificaParticipacaoEmVenda (int idPessoa){
        final String sql = "SELECT count(*) contagem FROM Venda WHERE idPessoa = ?";
            try {
                boolean participacao = false;
                PreparedStatement stmt = this.connection.prepareStatement(sql);
                stmt.setInt(1, idPessoa);
                ResultSet rs = stmt.executeQuery();
                    if(rs.next())
                        if(rs.getInt("contagem")!=0)
                            participacao = true;
                stmt.close();
                rs.close();
                this.connection.close();
                return participacao;
            } catch (SQLException e){
                throw new RuntimeException();
            }
    }
}
