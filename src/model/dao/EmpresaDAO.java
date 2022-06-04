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
import model.bean.Empresa;
import model.bean.Estado;

public class EmpresaDAO {
    private Connection connection;
    
    public EmpresaDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public void adicionaEmpresa (Empresa empresa){
        final String sqlEmpresa = "INSERT INTO Empresa (isFilial, idPessoaJuridica, idMatriz)"
                + " VALUES (?, (SELECT MAX(idPessoaJuridica) FROM PessoaJuridica), ?)";      
        try {
            PessoaJuridicaDAO dao = new PessoaJuridicaDAO();
            dao.adicionaPessoaJuridica(empresa);
            
            PreparedStatement stmt = this.connection.prepareStatement(sqlEmpresa);
            stmt.setInt(1, empresa.getIsFilial());
                if(empresa.getIsFilial()==1)
                    stmt.setInt(2, empresa.getMatriz().getIdEmpresa());
                else stmt.setObject(2, null);
            stmt.execute();
            stmt.close();
            this.connection.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public boolean removeEmpresa (Empresa empresa){
        final String sqlProduto = "DELETE FROM Empresa WHERE idEmpresa = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlProduto);
                stmt.setInt(1, empresa.getIdEmpresa());
                stmt.execute();
                this.connection.close();
                
                PessoaJuridicaDAO dao = new PessoaJuridicaDAO();
                    if(!dao.removePessoaJuridica(empresa))
                        return false;
                    else return true;
            } catch (SQLException e){
                if(e.toString().contains("Cannot delete or update a parent row"))
                    return false;
            } 
        return false;
    }
    
    public Empresa getEmpresa (Empresa empresa){
        final String sqlEmpresa = "SELECT * FROM Empresa " 
                            + "INNER JOIN PessoaJuridica ON Empresa.idPessoaJuridica = PessoaJuridica.idPessoaJuridica "
                            + "INNER JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado "
                            + "WHERE Empresa.idEmpresa = ?";
        try {
                if(this.connection.isClosed())
                    this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement(sqlEmpresa);
            stmt.setInt(1, empresa.getIdEmpresa());
            ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    empresa.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                    empresa.setLogradouro(rs.getString("Pessoa.logradouro"));
                    empresa.setNumero(rs.getString("Pessoa.numero"));
                    empresa.setComplemento(rs.getString("Pessoa.complemento"));
                    empresa.setBairro(rs.getString("Pessoa.bairro"));
                    
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.setCidade(rs.getString("Cidade.cidade"));
                    
                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("Estado.idEstado"));
                    estado.setEstado(rs.getString("Estado.estado"));
                    estado.setUf(rs.getString("Estado.uf"));
                    
                    cidade.setEstado(estado);
                    
                    empresa.setCidade(cidade);
                    empresa.setCep(rs.getString("Pessoa.cep"));
                    empresa.setEmail(rs.getString("Pessoa.email"));
                    empresa.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                    empresa.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                    empresa.setObservacoes(rs.getString("Pessoa.observacoes"));
                    empresa.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                    empresa.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                    empresa.setIdPessoaJuridica(rs.getInt("PessoaJuridica.idPessoaJuridica"));
                    empresa.setRazaoSocial(rs.getString("PessoaJuridica.razaoSocial"));
                    empresa.setNomeFantasia(rs.getString("PessoaJuridica.nomeFantasia"));
                    empresa.setCnpj(rs.getString("PessoaJuridica.cnpj"));
                    empresa.setIe(rs.getString("PessoaJuridica.ie"));
                    empresa.setIm(rs.getString("PessoaJuridica.im"));
                    empresa.setDataAbertura(Data.dataParaAplicacao(rs.getString("PessoaJuridica.dataAbertura")));
                    empresa.setIsFornecedor(rs.getInt("PessoaJuridica.isFornecedor"));
                    
                    empresa.setIdEmpresa(rs.getInt("Empresa.idEmpresa"));
                    empresa.setIsFilial(rs.getInt("Empresa.isFilial"));
                        if(empresa.getIsFilial()==1){
                            Empresa matriz = new Empresa();
                            matriz.setIdEmpresa(rs.getInt("Empresa.idMatriz"));
                            empresa.setMatriz(this.getEmpresa(matriz));
                        }
                }
            rs.close();
            stmt.close();
            //this.connection.close();
            return empresa;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public List<Empresa> getListaEmpresa (){
        final String sqlEmpresa = "SELECT * FROM Empresa " 
                            + "INNER JOIN PessoaJuridica ON Empresa.idPessoaJuridica = PessoaJuridica.idPessoaJuridica "
                            + "INNER JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado ";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlEmpresa);
            List<Empresa> listaEmpresa = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Empresa empresa = new Empresa();
                    empresa.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                    empresa.setLogradouro(rs.getString("Pessoa.logradouro"));
                    empresa.setNumero(rs.getString("Pessoa.numero"));
                    empresa.setComplemento(rs.getString("Pessoa.complemento"));
                    empresa.setBairro(rs.getString("Pessoa.bairro"));
                    
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.setCidade(rs.getString("Cidade.cidade"));
                    
                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("Estado.idEstado"));
                    estado.setEstado(rs.getString("Estado.estado"));
                    estado.setUf(rs.getString("Estado.uf"));
                    
                    cidade.setEstado(estado);
                    
                    empresa.setCidade(cidade);
                    empresa.setCep(rs.getString("Pessoa.cep"));
                    empresa.setEmail(rs.getString("Pessoa.email"));
                    empresa.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                    empresa.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                    empresa.setObservacoes(rs.getString("Pessoa.observacoes"));
                    empresa.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                    empresa.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                    empresa.setIdPessoaJuridica(rs.getInt("PessoaJuridica.idPessoaJuridica"));
                    empresa.setRazaoSocial(rs.getString("PessoaJuridica.razaoSocial"));
                    empresa.setNomeFantasia(rs.getString("PessoaJuridica.nomeFantasia"));
                    empresa.setCnpj(rs.getString("PessoaJuridica.cnpj"));
                    empresa.setIe(rs.getString("PessoaJuridica.ie"));
                    empresa.setIm(rs.getString("PessoaJuridica.im"));
                    empresa.setDataAbertura(Data.dataParaAplicacao(rs.getString("PessoaJuridica.dataAbertura")));
                    empresa.setIsFornecedor(rs.getInt("PessoaJuridica.isFornecedor"));
                    
                    empresa.setIdEmpresa(rs.getInt("Empresa.idEmpresa"));
                    empresa.setIsFilial(rs.getInt("Empresa.isFilial"));
                        if(empresa.getIsFilial()==1){
                            Empresa matriz = new Empresa();
                            matriz.setIdEmpresa(rs.getInt("Empresa.idMatriz"));
                            empresa.setMatriz(this.getEmpresa(matriz));
                        }
                    listaEmpresa.add(empresa);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaEmpresa;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public List<Empresa> getListaEmpresaMatriz (Empresa emp){
        String sqlEmpresa = "SELECT * FROM Empresa " 
                            + "INNER JOIN PessoaJuridica ON Empresa.idPessoaJuridica = PessoaJuridica.idPessoaJuridica "
                            + "INNER JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado WHERE Empresa.isFilial = 0 ";
        if(emp!=null && emp.getIsFilial()==0)
            sqlEmpresa += "AND Empresa.idEmpresa != ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlEmpresa);
                if(emp!=null && emp.getIsFilial()==0)
                    stmt.setInt(1, emp.getIdEmpresa());
            List<Empresa> listaEmpresa = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Empresa empresa = new Empresa();
                    empresa.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                    empresa.setLogradouro(rs.getString("Pessoa.logradouro"));
                    empresa.setNumero(rs.getString("Pessoa.numero"));
                    empresa.setComplemento(rs.getString("Pessoa.complemento"));
                    empresa.setBairro(rs.getString("Pessoa.bairro"));
                    
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.setCidade(rs.getString("Cidade.cidade"));
                    
                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("Estado.idEstado"));
                    estado.setEstado(rs.getString("Estado.estado"));
                    estado.setUf(rs.getString("Estado.uf"));
                    
                    cidade.setEstado(estado);
                    
                    empresa.setCidade(cidade);
                    empresa.setCep(rs.getString("Pessoa.cep"));
                    empresa.setEmail(rs.getString("Pessoa.email"));
                    empresa.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                    empresa.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                    empresa.setObservacoes(rs.getString("Pessoa.observacoes"));
                    empresa.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                    empresa.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                    empresa.setIdPessoaJuridica(rs.getInt("PessoaJuridica.idPessoaJuridica"));
                    empresa.setRazaoSocial(rs.getString("PessoaJuridica.razaoSocial"));
                    empresa.setNomeFantasia(rs.getString("PessoaJuridica.nomeFantasia"));
                    empresa.setCnpj(rs.getString("PessoaJuridica.cnpj"));
                    empresa.setIe(rs.getString("PessoaJuridica.ie"));
                    empresa.setIm(rs.getString("PessoaJuridica.im"));
                    empresa.setDataAbertura(Data.dataParaAplicacao(rs.getString("PessoaJuridica.dataAbertura")));
                    empresa.setIsFornecedor(rs.getInt("PessoaJuridica.isFornecedor"));
                    
                    empresa.setIdEmpresa(rs.getInt("Empresa.idEmpresa"));
                    empresa.setIsFilial(rs.getInt("Empresa.isFilial"));
                        if(empresa.getIsFilial()==1){
                            Empresa matriz = new Empresa();
                            matriz.setIdEmpresa(rs.getInt("Empresa.idMatriz"));
                            empresa.setMatriz(this.getEmpresa(matriz));
                        }
                    listaEmpresa.add(empresa);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaEmpresa;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Empresa> buscaEmpresa (String palavrasChave, int index){
        String sqlEmpresa = "SELECT * FROM Empresa " 
                            + "INNER JOIN PessoaJuridica ON Empresa.idPessoaJuridica = PessoaJuridica.idPessoaJuridica "
                            + "INNER JOIN Pessoa ON PessoaJuridica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado WHERE ";
        if(index==0)
            sqlEmpresa += "PessoaJuridica.razaoSocial like ?";
        else if (index==1)
            sqlEmpresa += "PessoaJuridica.nomeFantasia like ?";
        else if (index==2)
            sqlEmpresa += "PessoaJuridica.cnpj like ?";
        else if (index==3)
            sqlEmpresa += "Pessoa.observacoes like ?";
        
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlEmpresa);
            stmt.setString(1, "%" + palavrasChave + "%"); 
            List<Empresa> listaEmpresa = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Empresa empresa = new Empresa();
                    empresa.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                    empresa.setLogradouro(rs.getString("Pessoa.logradouro"));
                    empresa.setNumero(rs.getString("Pessoa.numero"));
                    empresa.setComplemento(rs.getString("Pessoa.complemento"));
                    empresa.setBairro(rs.getString("Pessoa.bairro"));
                    
                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                    cidade.setCidade(rs.getString("Cidade.cidade"));
                    
                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("Estado.idEstado"));
                    estado.setEstado(rs.getString("Estado.estado"));
                    estado.setUf(rs.getString("Estado.uf"));
                    
                    cidade.setEstado(estado);
                    
                    empresa.setCidade(cidade);
                    empresa.setCep(rs.getString("Pessoa.cep"));
                    empresa.setEmail(rs.getString("Pessoa.email"));
                    empresa.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                    empresa.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                    empresa.setObservacoes(rs.getString("Pessoa.observacoes"));
                    empresa.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                    empresa.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                    empresa.setIdPessoaJuridica(rs.getInt("PessoaJuridica.idPessoaJuridica"));
                    empresa.setRazaoSocial(rs.getString("PessoaJuridica.razaoSocial"));
                    empresa.setNomeFantasia(rs.getString("PessoaJuridica.nomeFantasia"));
                    empresa.setCnpj(rs.getString("PessoaJuridica.cnpj"));
                    empresa.setIe(rs.getString("PessoaJuridica.ie"));
                    empresa.setIm(rs.getString("PessoaJuridica.im"));
                    empresa.setDataAbertura(Data.dataParaAplicacao(rs.getString("PessoaJuridica.dataAbertura")));
                    empresa.setIsFornecedor(rs.getInt("PessoaJuridica.isFornecedor"));
                    
                    empresa.setIdEmpresa(rs.getInt("Empresa.idEmpresa"));
                    empresa.setIsFilial(rs.getInt("Empresa.isFilial"));
                        if(empresa.getIsFilial()==1){
                            Empresa matriz = new Empresa();
                            matriz.setIdEmpresa(rs.getInt("Empresa.idMatriz"));
                            empresa.setMatriz(this.getEmpresa(matriz));
                        }
                    listaEmpresa.add(empresa);
                }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaEmpresa;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public void atualizaEmpresa(Empresa empresa) {
        final String sqlEmpresa = "UPDATE Empresa SET isFilial = ?, idMatriz = ? WHERE idEmpresa = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlEmpresa);
            stmt.setInt(1, empresa.getIsFilial());
                if(empresa.getIsFilial()==1)
                    stmt.setInt(2, empresa.getMatriz().getIdEmpresa());
                else stmt.setObject(2, null);
            stmt.setInt(3, empresa.getIdEmpresa());
            stmt.execute();
            stmt.close();
            this.connection.close();
            
            PessoaJuridicaDAO dao = new PessoaJuridicaDAO();
            dao.atualizaPessoaJuridica(empresa);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public int getLastCodeEmpresa(){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT MAX(idEmpresa) AS idEmpresa FROM Empresa LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next()){
                    code = rs.getInt("idEmpresa");
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
