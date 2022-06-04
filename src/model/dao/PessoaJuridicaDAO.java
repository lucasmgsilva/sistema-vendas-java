package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.bean.Cidade;
import others.Data;
import model.bean.Estado;
import model.bean.PessoaJuridica;

public class PessoaJuridicaDAO {
    private Connection connection;

    public PessoaJuridicaDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public void adicionaPessoaJuridica(PessoaJuridica pessoaJuridica){
        final String sqlPessoaJuridica = "INSERT INTO PessoaJuridica (razaoSocial, nomeFantasia, cnpj,"
                + "ie, im, dataAbertura, isFornecedor, idPessoa)" 
                +  "VALUES (?, ?, ?, ?, ?, ?, ?, (SELECT max(idPessoa) AS idPessoa FROM Pessoa))";
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PessoaDAO dao = new PessoaDAO();
            dao.adicionaPessoa(pessoaJuridica);
            
            PreparedStatement stmt = this.connection.prepareStatement(sqlPessoaJuridica);
            stmt.setString(1, pessoaJuridica.getRazaoSocial());
            stmt.setString(2, pessoaJuridica.getNomeFantasia());
            stmt.setString(3, pessoaJuridica.getCnpj());
            stmt.setString(4, pessoaJuridica.getIe());
            stmt.setString(5, pessoaJuridica.getIm());
            stmt.setString(6, Data.dataParaBanco(pessoaJuridica.getDataAbertura()));
            stmt.setInt(7, pessoaJuridica.getIsFornecedor());
            stmt.execute(); 
            stmt.close();
            this.connection.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }      
    
    public void atualizaPessoaJuridica(PessoaJuridica pessoaJuridica){
        final String sqlPessoaJuridica = "UPDATE PessoaJuridica SET razaoSocial = ?, nomeFantasia = ?, cnpj = ?, "
                    + "ie = ?, im = ?, dataAbertura = ? WHERE idPessoaJuridica = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlPessoaJuridica);
            stmt.setString(1, pessoaJuridica.getRazaoSocial());
            stmt.setString(2, pessoaJuridica.getNomeFantasia());
            stmt.setString(3, pessoaJuridica.getCnpj());
            stmt.setString(4, pessoaJuridica.getIe());
            stmt.setString(5, pessoaJuridica.getIm());
            stmt.setString(6, Data.dataParaBanco(pessoaJuridica.getDataAbertura()));
            stmt.setInt(7, pessoaJuridica.getIdPessoaJuridica());
            stmt.execute(); 
            stmt.close();
            this.connection.close();
            
            PessoaDAO dao = new PessoaDAO();
            dao.atualizaPessoa(pessoaJuridica);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }     
    
    public boolean removePessoaJuridica (PessoaJuridica pessoaJuridica){
        final String sqlProduto = "DELETE FROM PessoaJuridica WHERE idPessoaJuridica = ?";
            try {
                //verifica participações em vendas
                if(!(new PessoaDAO().verificaParticipacaoEmVenda(pessoaJuridica.getIdPessoa()))){
                    PreparedStatement stmt = this.connection.prepareStatement(sqlProduto);
                    stmt.setInt(1, pessoaJuridica.getIdPessoaJuridica());
                    stmt.execute();
                    this.connection.close();

                    PessoaDAO dao = new PessoaDAO();
                        if(!dao.removePessoa(pessoaJuridica))
                            return false;
                        else return true;
                } else return false;
            } catch (SQLException e){
                if(e.toString().contains("Cannot delete or update a parent row"))
                    return false;
            } 
        return false;
    }

    public List<PessoaJuridica> getListaFornecedor (){
        final String sqlFornecedor = "SELECT * FROM PessoaJuridica " 
                            + "JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado "
                            + "WHERE isFornecedor = 1";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlFornecedor);
            List<PessoaJuridica> listaFornecedor = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    PessoaJuridica pessoaJuridica = new PessoaJuridica();
                    pessoaJuridica.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                    
                    pessoaJuridica.setIdPessoaJuridica(rs.getInt("PessoaJuridica.idPessoaJuridica"));
                    pessoaJuridica.setRazaoSocial(rs.getString("PessoaJuridica.razaoSocial"));
                    pessoaJuridica.setNomeFantasia(rs.getString("PessoaJuridica.nomeFantasia"));
                    pessoaJuridica.setCnpj(rs.getString("PessoaJuridica.cnpj"));
                    pessoaJuridica.setIe(rs.getString("PessoaJuridica.ie"));
                    pessoaJuridica.setIm(rs.getString("PessoaJuridica.im"));
                    pessoaJuridica.setDataAbertura(Data.dataParaAplicacao(rs.getString("PessoaJuridica.dataAbertura")));
                    pessoaJuridica.setIsFornecedor(rs.getInt("PessoaJuridica.isFornecedor"));
                    
                    pessoaJuridica.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                    pessoaJuridica.setLogradouro(rs.getString("Pessoa.logradouro"));
                    pessoaJuridica.setNumero(rs.getString("Pessoa.numero"));
                    pessoaJuridica.setComplemento(rs.getString("Pessoa.complemento"));
                    pessoaJuridica.setBairro(rs.getString("Pessoa.bairro"));

                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("Estado.idEstado"));
                    estado.setEstado(rs.getString("Estado.estado"));
                    estado.setUf(rs.getString("Estado.uf"));
                    
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.setCidade(rs.getString("Cidade.cidade"));
                    cidade.setEstado(estado);
                    
                    pessoaJuridica.setCidade(cidade);
                    pessoaJuridica.setCep(rs.getString("Pessoa.cep"));
                    pessoaJuridica.setEmail(rs.getString("Pessoa.email"));
                    pessoaJuridica.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                    pessoaJuridica.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                    pessoaJuridica.setObservacoes(rs.getString("Pessoa.observacoes"));
                    pessoaJuridica.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                    listaFornecedor.add(pessoaJuridica);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaFornecedor;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public PessoaJuridica getFornecedor (int idPessoaJuridica){
        final String sqlFornecedor = "SELECT * FROM PessoaJuridica " 
                            + "JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado "
                            + "WHERE idPessoaJuridica = ? AND isFornecedor = 1";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlFornecedor);
            stmt.setInt(1, idPessoaJuridica);
            ResultSet rs = stmt.executeQuery();
            PessoaJuridica pessoaJuridica = new PessoaJuridica();
                if(rs.next()){
                    pessoaJuridica.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                    
                    pessoaJuridica.setIdPessoaJuridica(rs.getInt("PessoaJuridica.idPessoaJuridica"));
                    pessoaJuridica.setRazaoSocial(rs.getString("PessoaJuridica.razaoSocial"));
                    pessoaJuridica.setNomeFantasia(rs.getString("PessoaJuridica.nomeFantasia"));
                    pessoaJuridica.setCnpj(rs.getString("PessoaJuridica.cnpj"));
                    pessoaJuridica.setIe(rs.getString("PessoaJuridica.ie"));
                    pessoaJuridica.setIm(rs.getString("PessoaJuridica.im"));
                    pessoaJuridica.setDataAbertura(Data.dataParaAplicacao(rs.getString("PessoaJuridica.dataAbertura")));
                    pessoaJuridica.setIsFornecedor(rs.getInt("PessoaJuridica.isFornecedor"));
                    
                    pessoaJuridica.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                    pessoaJuridica.setLogradouro(rs.getString("Pessoa.logradouro"));
                    pessoaJuridica.setNumero(rs.getString("Pessoa.numero"));
                    pessoaJuridica.setComplemento(rs.getString("Pessoa.complemento"));
                    pessoaJuridica.setBairro(rs.getString("Pessoa.bairro"));

                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("Estado.idEstado"));
                    estado.setEstado(rs.getString("Estado.estado"));
                    estado.setUf(rs.getString("Estado.uf"));
                    
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.setCidade(rs.getString("Cidade.cidade"));
                    cidade.setEstado(estado);
                    
                    pessoaJuridica.setCidade(cidade);
                    pessoaJuridica.setCep(rs.getString("Pessoa.cep"));
                    pessoaJuridica.setEmail(rs.getString("Pessoa.email"));
                    pessoaJuridica.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                    pessoaJuridica.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                    pessoaJuridica.setObservacoes(rs.getString("Pessoa.observacoes"));
                    pessoaJuridica.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return pessoaJuridica;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public List<PessoaJuridica> getListaPessoaJuridica (){
        final String sqlPessoaJuridica = "SELECT * FROM PessoaJuridica " 
                            + "JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado "
                            + "WHERE isFornecedor = 0 AND idPessoaJuridica NOT IN(SELECT idPessoaJuridica FROM Empresa)";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlPessoaJuridica);
            List<PessoaJuridica> listaPessoaJuridica = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    PessoaJuridica pessoaJuridica = new PessoaJuridica();
                    pessoaJuridica.setIdPessoaJuridica(rs.getInt("PessoaJuridica.idPessoaJuridica"));
                    pessoaJuridica.setRazaoSocial(rs.getString("PessoaJuridica.razaoSocial"));
                    pessoaJuridica.setNomeFantasia(rs.getString("PessoaJuridica.nomeFantasia"));
                    pessoaJuridica.setCnpj(rs.getString("PessoaJuridica.cnpj"));
                    pessoaJuridica.setIe(rs.getString("PessoaJuridica.ie"));
                    pessoaJuridica.setIm(rs.getString("PessoaJuridica.im"));
                    pessoaJuridica.setDataAbertura(Data.dataParaAplicacao(rs.getString("PessoaJuridica.dataAbertura")));
                    pessoaJuridica.setIsFornecedor(rs.getInt("PessoaJuridica.isFornecedor"));
                    
                    pessoaJuridica.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                    pessoaJuridica.setLogradouro(rs.getString("Pessoa.logradouro"));
                    pessoaJuridica.setNumero(rs.getString("Pessoa.numero"));
                    pessoaJuridica.setComplemento(rs.getString("Pessoa.complemento"));
                    pessoaJuridica.setBairro(rs.getString("Pessoa.bairro"));

                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("Estado.idEstado"));
                    estado.setEstado(rs.getString("Estado.estado"));
                    estado.setUf(rs.getString("Estado.uf"));
                    
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.setCidade(rs.getString("Cidade.cidade"));
                    cidade.setEstado(estado);
                    
                    pessoaJuridica.setCidade(cidade);
                    pessoaJuridica.setCep(rs.getString("Pessoa.cep"));
                    pessoaJuridica.setEmail(rs.getString("Pessoa.email"));
                    pessoaJuridica.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                    pessoaJuridica.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                    pessoaJuridica.setObservacoes(rs.getString("Pessoa.observacoes"));
                    pessoaJuridica.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                    pessoaJuridica.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                    listaPessoaJuridica.add(pessoaJuridica);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaPessoaJuridica;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public List<PessoaJuridica> getListaPessoaJuridicaHabilitada (){
        final String sqlPessoaJuridica = "SELECT * FROM PessoaJuridica " 
                            + "JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado "
                            + "WHERE isFornecedor = 0 AND idPessoaJuridica NOT IN(SELECT idPessoaJuridica FROM Empresa) AND Pessoa.isHabilitado = 1";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlPessoaJuridica);
            List<PessoaJuridica> listaPessoaJuridica = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    PessoaJuridica pessoaJuridica = new PessoaJuridica();
                    pessoaJuridica.setIdPessoaJuridica(rs.getInt("PessoaJuridica.idPessoaJuridica"));
                    pessoaJuridica.setRazaoSocial(rs.getString("PessoaJuridica.razaoSocial"));
                    pessoaJuridica.setNomeFantasia(rs.getString("PessoaJuridica.nomeFantasia"));
                    pessoaJuridica.setCnpj(rs.getString("PessoaJuridica.cnpj"));
                    pessoaJuridica.setIe(rs.getString("PessoaJuridica.ie"));
                    pessoaJuridica.setIm(rs.getString("PessoaJuridica.im"));
                    pessoaJuridica.setDataAbertura(Data.dataParaAplicacao(rs.getString("PessoaJuridica.dataAbertura")));
                    pessoaJuridica.setIsFornecedor(rs.getInt("PessoaJuridica.isFornecedor"));
                    
                    pessoaJuridica.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                    pessoaJuridica.setLogradouro(rs.getString("Pessoa.logradouro"));
                    pessoaJuridica.setNumero(rs.getString("Pessoa.numero"));
                    pessoaJuridica.setComplemento(rs.getString("Pessoa.complemento"));
                    pessoaJuridica.setBairro(rs.getString("Pessoa.bairro"));

                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("Estado.idEstado"));
                    estado.setEstado(rs.getString("Estado.estado"));
                    estado.setUf(rs.getString("Estado.uf"));
                    
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.setCidade(rs.getString("Cidade.cidade"));
                    cidade.setEstado(estado);
                    
                    pessoaJuridica.setCidade(cidade);
                    pessoaJuridica.setCep(rs.getString("Pessoa.cep"));
                    pessoaJuridica.setEmail(rs.getString("Pessoa.email"));
                    pessoaJuridica.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                    pessoaJuridica.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                    pessoaJuridica.setObservacoes(rs.getString("Pessoa.observacoes"));
                    pessoaJuridica.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                    pessoaJuridica.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                    listaPessoaJuridica.add(pessoaJuridica);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaPessoaJuridica;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public List<PessoaJuridica> buscaFornecedor (String palavrasChave, int index){
        String sqlFornecedor = "SELECT * FROM PessoaJuridica " 
                            + "JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado "
                            + "WHERE isFornecedor = 1 AND ";
        if(index==0)
            sqlFornecedor += "PessoaJuridica.razaoSocial like ?";
        else if (index==1)
            sqlFornecedor += "PessoaJuridica.nomeFantasia like ?";
        else if (index==2)
            sqlFornecedor += "PessoaJuridica.cnpj like ?";
        else if (index==3)
            sqlFornecedor += "Pessoa.observacoes like ?";
        
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlFornecedor);
            stmt.setString(1, "%" + palavrasChave + "%");   
            List<PessoaJuridica> listaFornecedor = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    PessoaJuridica pessoaJuridica = new PessoaJuridica();
                    pessoaJuridica.setIdPessoaJuridica(rs.getInt("PessoaJuridica.idPessoaJuridica"));
                    pessoaJuridica.setRazaoSocial(rs.getString("PessoaJuridica.razaoSocial"));
                    pessoaJuridica.setNomeFantasia(rs.getString("PessoaJuridica.nomeFantasia"));
                    pessoaJuridica.setCnpj(rs.getString("PessoaJuridica.cnpj"));
                    pessoaJuridica.setIe(rs.getString("PessoaJuridica.ie"));
                    pessoaJuridica.setIm(rs.getString("PessoaJuridica.im"));
                    pessoaJuridica.setDataAbertura(Data.dataParaAplicacao(rs.getString("PessoaJuridica.dataAbertura")));
                    pessoaJuridica.setIsFornecedor(rs.getInt("PessoaJuridica.isFornecedor"));
                    
                    pessoaJuridica.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                    pessoaJuridica.setLogradouro(rs.getString("Pessoa.logradouro"));
                    pessoaJuridica.setNumero(rs.getString("Pessoa.numero"));
                    pessoaJuridica.setComplemento(rs.getString("Pessoa.complemento"));
                    pessoaJuridica.setBairro(rs.getString("Pessoa.bairro"));

                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("Estado.idEstado"));
                    estado.setEstado(rs.getString("Estado.estado"));
                    estado.setUf(rs.getString("Estado.uf"));
                    
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.setCidade(rs.getString("Cidade.cidade"));
                    cidade.setEstado(estado);
                    
                    pessoaJuridica.setCidade(cidade);
                    pessoaJuridica.setCep(rs.getString("Pessoa.cep"));
                    pessoaJuridica.setEmail(rs.getString("Pessoa.email"));
                    pessoaJuridica.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                    pessoaJuridica.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                    pessoaJuridica.setObservacoes(rs.getString("Pessoa.observacoes"));
                    pessoaJuridica.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                    pessoaJuridica.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                    listaFornecedor.add(pessoaJuridica);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaFornecedor;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public List<PessoaJuridica> buscaPessoaJuridica (String palavrasChave, int index){
        String sqlPessoaJuridica = "SELECT * FROM PessoaJuridica " 
                            + "JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado "
                            + "WHERE isFornecedor = 0 AND idPessoaJuridica NOT IN(SELECT idPessoaJuridica FROM Empresa) AND ";
      
        if(index==0)
            sqlPessoaJuridica += "PessoaJuridica.razaoSocial like ?";
        else if (index==1)
            sqlPessoaJuridica += "PessoaJuridica.nomeFantasia like ?";
        else if (index==2)
            sqlPessoaJuridica += "PessoaJuridica.cnpj like ?";
        else if (index==3)
            sqlPessoaJuridica += "Pessoa.observacoes like ?";
       
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlPessoaJuridica);
            stmt.setString(1, "%" + palavrasChave + "%");
            List<PessoaJuridica> listaPessoaJuridica = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    PessoaJuridica pessoaJuridica = new PessoaJuridica();
                    pessoaJuridica.setIdPessoaJuridica(rs.getInt("PessoaJuridica.idPessoaJuridica"));
                    pessoaJuridica.setRazaoSocial(rs.getString("PessoaJuridica.razaoSocial"));
                    pessoaJuridica.setNomeFantasia(rs.getString("PessoaJuridica.nomeFantasia"));
                    pessoaJuridica.setCnpj(rs.getString("PessoaJuridica.cnpj"));
                    pessoaJuridica.setIe(rs.getString("PessoaJuridica.ie"));
                    pessoaJuridica.setIm(rs.getString("PessoaJuridica.im"));
                    pessoaJuridica.setDataAbertura(Data.dataParaAplicacao(rs.getString("PessoaJuridica.dataAbertura")));
                    pessoaJuridica.setIsFornecedor(rs.getInt("PessoaJuridica.isFornecedor"));
                    
                    pessoaJuridica.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                    pessoaJuridica.setLogradouro(rs.getString("Pessoa.logradouro"));
                    pessoaJuridica.setNumero(rs.getString("Pessoa.numero"));
                    pessoaJuridica.setComplemento(rs.getString("Pessoa.complemento"));
                    pessoaJuridica.setBairro(rs.getString("Pessoa.bairro"));

                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("Estado.idEstado"));
                    estado.setEstado(rs.getString("Estado.estado"));
                    estado.setUf(rs.getString("Estado.uf"));
                    
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.setCidade(rs.getString("Cidade.cidade"));
                    cidade.setEstado(estado);
                    
                    pessoaJuridica.setCidade(cidade);
                    pessoaJuridica.setCep(rs.getString("Pessoa.cep"));
                    pessoaJuridica.setEmail(rs.getString("Pessoa.email"));
                    pessoaJuridica.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                    pessoaJuridica.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                    pessoaJuridica.setObservacoes(rs.getString("Pessoa.observacoes"));
                    pessoaJuridica.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                    pessoaJuridica.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                    listaPessoaJuridica.add(pessoaJuridica);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaPessoaJuridica;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public int verificaExistenciaFornecedor(String cnpj){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM PessoaJuridica WHERE isFornecedor = 1 AND cnpj = ?");
            stmt.setString(1, cnpj);
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next()){
                    code = rs.getInt("idPessoaJuridica");
                }
            stmt.close();
            rs.close();
            this.connection.close();
            return code;
        } catch (SQLException e){
            throw new RuntimeException();
        }
    }
    
    public int getLastCodePessoaJuridica(){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT MAX(idPessoaJuridica) AS idPessoaJuridica FROM PessoaJuridica LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next()){
                    code = rs.getInt("idPessoaJuridica");
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
