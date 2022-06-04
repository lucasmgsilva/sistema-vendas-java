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
import model.bean.PessoaFisica;

public class PessoaFisicaDAO {
    private Connection connection;

    public PessoaFisicaDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public void adicionaPessoaFisica(PessoaFisica pessoaFisica){   
        final String sqlPessoaFisica = "INSERT INTO PessoaFisica (nome, apelido, sexo, cpf, rg,"
                + "dataNascimento, idPessoa)" 
                +  "VALUES (?, ?, ?, ?, ?, ?, (SELECT max(idPessoa) FROM Pessoa))";
        try {
            PessoaDAO dao = new PessoaDAO();
            dao.adicionaPessoa(pessoaFisica);
            
            PreparedStatement stmt = this.connection.prepareStatement(sqlPessoaFisica);
            stmt.setString(1, pessoaFisica.getNome());
            stmt.setString(2, pessoaFisica.getApelido());
            stmt.setString(3, pessoaFisica.getSexo());
            stmt.setString(4, pessoaFisica.getCpf());
            stmt.setString(5, pessoaFisica.getRg());
            stmt.setString(6, Data.dataParaBanco(pessoaFisica.getDataNascimento()));
            stmt.execute(); 
            stmt.close();
            this.connection.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }     
    
    public boolean removePessoaFisica (PessoaFisica pessoaFisica){
        final String sqlProduto = "DELETE FROM PessoaFisica WHERE idPessoaFisica = ?";
            try {
                //verifica participações em vendas
                if(!(new PessoaDAO().verificaParticipacaoEmVenda(pessoaFisica.getIdPessoa()))){
                    PreparedStatement stmt = this.connection.prepareStatement(sqlProduto);
                    stmt.setInt(1, pessoaFisica.getIdPessoaFisica());
                    stmt.execute();
                    this.connection.close();

                    PessoaDAO dao = new PessoaDAO();
                        if(!dao.removePessoa(pessoaFisica))
                            return false;
                        else return true;
                } else return false;
            } catch (SQLException e){
                if(e.toString().contains("Cannot delete or update a parent row"))
                    return false;
            } 
        return false;
    }
    
    public void atualizaPessoaFisica(PessoaFisica pessoaFisica){   
        final String sqlPessoaFisica = "UPDATE PessoaFisica SET nome = ?, apelido = ?, sexo = ?, cpf = ?, rg = ?, dataNascimento = ? WHERE idPessoaFisica = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlPessoaFisica);
            stmt.setString(1, pessoaFisica.getNome());
            stmt.setString(2, pessoaFisica.getApelido());
            stmt.setString(3, pessoaFisica.getSexo());
            stmt.setString(4, pessoaFisica.getCpf());
            stmt.setString(5, pessoaFisica.getRg());
            stmt.setString(6, Data.dataParaBanco(pessoaFisica.getDataNascimento()));
            stmt.setInt(7, pessoaFisica.getIdPessoaFisica());
            stmt.execute(); 
            stmt.close();
            this.connection.close();
            
            PessoaDAO dao = new PessoaDAO();
            dao.atualizaPessoa(pessoaFisica);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }  
    
    public List<PessoaFisica> getListaPessoaFisica (){
        final String sqlPessoaFisica = "SELECT * FROM PessoaFisica "
                            + "INNER JOIN Pessoa ON PessoaFisica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado "
                            + "WHERE idPessoaFisica NOT IN(SELECT idPessoaFisica FROM Usuario)";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlPessoaFisica);
                List<PessoaFisica> listaPessoaFisica = new ArrayList<>();
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        PessoaFisica pessoaFisica = new PessoaFisica();
                        pessoaFisica.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                            
                        pessoaFisica.setIdPessoaFisica(rs.getInt("PessoaFisica.idPessoaFisica"));
                        pessoaFisica.setNome(rs.getString("PessoaFisica.nome"));
                        pessoaFisica.setApelido(rs.getString("PessoaFisica.apelido"));
                        pessoaFisica.setSexo(rs.getString("PessoaFisica.sexo"));
                        pessoaFisica.setCpf(rs.getString("PessoaFisica.cpf"));
                        pessoaFisica.setRg(rs.getString("PessoaFisica.rg"));
                        pessoaFisica.setDataNascimento(Data.dataParaAplicacao(rs.getString("PessoaFisica.dataNascimento")));
                            
                        pessoaFisica.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                        pessoaFisica.setLogradouro(rs.getString("Pessoa.logradouro"));
                        pessoaFisica.setNumero(rs.getString("Pessoa.numero"));
                        pessoaFisica.setComplemento(rs.getString("Pessoa.complemento"));
                        pessoaFisica.setBairro(rs.getString("Pessoa.bairro"));
                            
                        Cidade cidade = new Cidade();
                        cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                        cidade.setCidade(rs.getString("Cidade.cidade"));
                            
                        Estado estado = new Estado();
                        estado.setIdEstado(rs.getInt("Estado.idEstado"));
                        estado.setEstado(rs.getString("Estado.estado"));
                        estado.setUf(rs.getString("Estado.uf"));
                            
                        cidade.setEstado(estado);
                        pessoaFisica.setCidade(cidade);
                            
                        pessoaFisica.setCep(rs.getString("Pessoa.cep"));
                        pessoaFisica.setEmail(rs.getString("Pessoa.email"));
                        pessoaFisica.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                        pessoaFisica.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                        pessoaFisica.setObservacoes(rs.getString("Pessoa.observacoes"));
                        pessoaFisica.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                        listaPessoaFisica.add(pessoaFisica);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaPessoaFisica;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public List<PessoaFisica> getListaPessoaFisicaHabilitada (){
        final String sqlPessoaFisica = "SELECT * FROM PessoaFisica "
                            + "INNER JOIN Pessoa ON PessoaFisica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado "
                            + "WHERE idPessoaFisica NOT IN(SELECT idPessoaFisica FROM Usuario) AND Pessoa.isHabilitado = 1";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlPessoaFisica);
                List<PessoaFisica> listaPessoaFisica = new ArrayList<>();
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        PessoaFisica pessoaFisica = new PessoaFisica();
                        pessoaFisica.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                            
                        pessoaFisica.setIdPessoaFisica(rs.getInt("PessoaFisica.idPessoaFisica"));
                        pessoaFisica.setNome(rs.getString("PessoaFisica.nome"));
                        pessoaFisica.setApelido(rs.getString("PessoaFisica.apelido"));
                        pessoaFisica.setSexo(rs.getString("PessoaFisica.sexo"));
                        pessoaFisica.setCpf(rs.getString("PessoaFisica.cpf"));
                        pessoaFisica.setRg(rs.getString("PessoaFisica.rg"));
                        pessoaFisica.setDataNascimento(Data.dataParaAplicacao(rs.getString("PessoaFisica.dataNascimento")));
                            
                        pessoaFisica.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                        pessoaFisica.setLogradouro(rs.getString("Pessoa.logradouro"));
                        pessoaFisica.setNumero(rs.getString("Pessoa.numero"));
                        pessoaFisica.setComplemento(rs.getString("Pessoa.complemento"));
                        pessoaFisica.setBairro(rs.getString("Pessoa.bairro"));
                            
                        Cidade cidade = new Cidade();
                        cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                        cidade.setCidade(rs.getString("Cidade.cidade"));
                            
                        Estado estado = new Estado();
                        estado.setIdEstado(rs.getInt("Estado.idEstado"));
                        estado.setEstado(rs.getString("Estado.estado"));
                        estado.setUf(rs.getString("Estado.uf"));
                            
                        cidade.setEstado(estado);
                        pessoaFisica.setCidade(cidade);
                            
                        pessoaFisica.setCep(rs.getString("Pessoa.cep"));
                        pessoaFisica.setEmail(rs.getString("Pessoa.email"));
                        pessoaFisica.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                        pessoaFisica.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                        pessoaFisica.setObservacoes(rs.getString("Pessoa.observacoes"));
                        pessoaFisica.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                        listaPessoaFisica.add(pessoaFisica);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaPessoaFisica;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public List<PessoaFisica> buscaPessoaFisica (String palavrasChave, int index){
        String sqlPessoaFisica = "SELECT * FROM PessoaFisica "
                            + "INNER JOIN Pessoa ON PessoaFisica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado "
                            + "WHERE idPessoaFisica NOT IN(SELECT idPessoaFisica FROM Usuario) AND ";
        
        if(index==0)
            sqlPessoaFisica += "PessoaFisica.nome like ?";
        else if (index==1)
            sqlPessoaFisica += "PessoaFisica.apelido like ?";
        else if (index==2)
            sqlPessoaFisica += "PessoaFisica.cpf like ?";
        else if (index==3)
            sqlPessoaFisica += "Pessoa.observacoes like ?";
       
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlPessoaFisica);
                stmt.setString(1, "%" + palavrasChave + "%");
                List<PessoaFisica> listaPessoaFisica = new ArrayList<>();
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        PessoaFisica pessoaFisica = new PessoaFisica();
                        pessoaFisica.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                            
                        pessoaFisica.setIdPessoaFisica(rs.getInt("PessoaFisica.idPessoaFisica"));
                        pessoaFisica.setNome(rs.getString("PessoaFisica.nome"));
                        pessoaFisica.setApelido(rs.getString("PessoaFisica.apelido"));
                        pessoaFisica.setSexo(rs.getString("PessoaFisica.sexo"));
                        pessoaFisica.setCpf(rs.getString("PessoaFisica.cpf"));
                        pessoaFisica.setRg(rs.getString("PessoaFisica.rg"));
                        pessoaFisica.setDataNascimento(Data.dataParaAplicacao(rs.getString("PessoaFisica.dataNascimento")));
                            
                        pessoaFisica.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                        pessoaFisica.setLogradouro(rs.getString("Pessoa.logradouro"));
                        pessoaFisica.setNumero(rs.getString("Pessoa.numero"));
                        pessoaFisica.setComplemento(rs.getString("Pessoa.complemento"));
                        pessoaFisica.setBairro(rs.getString("Pessoa.bairro"));
                            
                        Cidade cidade = new Cidade();
                        cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                        cidade.setCidade(rs.getString("Cidade.cidade"));
                            
                        Estado estado = new Estado();
                        estado.setIdEstado(rs.getInt("Estado.idEstado"));
                        estado.setEstado(rs.getString("Estado.estado"));
                        estado.setUf(rs.getString("Estado.uf"));
                            
                        cidade.setEstado(estado);
                        pessoaFisica.setCidade(cidade);
                            
                        pessoaFisica.setCep(rs.getString("Pessoa.cep"));
                        pessoaFisica.setEmail(rs.getString("Pessoa.email"));
                        pessoaFisica.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                        pessoaFisica.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                        pessoaFisica.setObservacoes(rs.getString("Pessoa.observacoes"));
                        pessoaFisica.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                        listaPessoaFisica.add(pessoaFisica);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaPessoaFisica;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public int getLastCodePessoaFisica(){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT MAX(idPessoaFisica) AS idPessoaFisica FROM PessoaFisica LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next()){
                    code = rs.getInt("idPessoaFisica");
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
