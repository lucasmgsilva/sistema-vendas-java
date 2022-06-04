package model.dao;

import com.mysql.jdbc.StringUtils;
import model.bean.Sessao;
import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.bean.Cargo;
import model.bean.Cidade;
import others.Data;
import model.bean.Empresa;
import model.bean.Estado;
import model.bean.Usuario;

public class UsuarioDAO {
    private Connection connection;
    
    public UsuarioDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }
    
    public void adicionaUsuario (Usuario usuario){
        final String sqlUsuario = "INSERT INTO Usuario (login, senha, salario, comissao, dataUltimoAcesso, idCargo, "
                + "idPessoaFisica, idEmpresa) VALUES (?, ?, ?, ?, ?, ?, (SELECT MAX(idPessoaFisica) FROM PessoaFisica), ?)";
        try {
            PessoaFisicaDAO dao = new PessoaFisicaDAO();
            dao.adicionaPessoaFisica(usuario);
            
            PreparedStatement stmt = this.connection.prepareStatement(sqlUsuario);
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setFloat(3, usuario.getSalario());
            stmt.setFloat(4, usuario.getComissao());
            stmt.setObject(5, null);
            stmt.setInt(6, usuario.getCargo().getIdCargo());
            stmt.setInt(7, usuario.getEmpresa().getIdEmpresa());
            stmt.execute(); 
            stmt.close();
            this.connection.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public boolean removeUsuario (Usuario usuario){
        final String sqlProduto = "DELETE FROM Usuario WHERE idUsuario = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlProduto);
                stmt.setInt(1, usuario.getIdUsuario());
                stmt.execute();
                this.connection.close();
                
                PessoaFisicaDAO dao = new PessoaFisicaDAO();
                    if(!dao.removePessoaFisica(usuario))
                        return false;
                    else return true;
            } catch (SQLException e){
                if(e.toString().contains("Cannot delete or update a parent row"))
                    return false;
            } 
        return false;
    }
    
    public void atualizaUsuario (Usuario usuario){
        final String sqlUsuario = "UPDATE Usuario SET login = ?, senha = ?, salario = ?, comissao = ?, idCargo = ?, idEmpresa = ? WHERE idUsuario = ?";
        try {           
            PreparedStatement stmt = this.connection.prepareStatement(sqlUsuario);
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setFloat(3, usuario.getSalario());
            stmt.setFloat(4, usuario.getComissao());
            stmt.setInt(5, usuario.getCargo().getIdCargo());
            stmt.setInt(6, usuario.getEmpresa().getIdEmpresa());
            stmt.setInt(7, usuario.getIdUsuario());
            stmt.execute(); 
            stmt.close();
            this.connection.close();
            
            PessoaFisicaDAO dao = new PessoaFisicaDAO();
            dao.atualizaPessoaFisica(usuario);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public byte validaUsuario (Usuario usuario){
        final String sqlUsuario = "SELECT * FROM Usuario "
                            + "INNER JOIN Cargo ON Usuario.idCargo = Cargo.idCargo "
                            + "INNER JOIN PessoaFisica ON Usuario.idPessoaFisica = PessoaFisica.idPessoaFisica "
                            + "INNER JOIN Pessoa ON PessoaFisica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado WHERE Usuario.login = ? AND Usuario.senha = ?";
        byte validacao = -1;
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlUsuario);
                stmt.setString(1, usuario.getLogin());
                stmt.setString(2, usuario.getSenha());
                ResultSet rs = stmt.executeQuery();
                    if(rs.next()){
                        usuario.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                        if(usuario.getIsHabilitado()==1){
                            usuario.setIdUsuario(rs.getInt("Usuario.idUsuario"));
                            usuario.setSalario(rs.getFloat("Usuario.salario"));
                            usuario.setComissao(rs.getFloat("Usuario.comissao"));
                                if(!StringUtils.isNullOrEmpty(rs.getString("Usuario.dataUltimoAcesso")))
                                    usuario.setDataUltimoAcesso(Data.dataHoraParaAplicacao(rs.getString("Usuario.dataUltimoAcesso")));
                                else usuario.setDataUltimoAcesso("Nenhum");
                                    
                            Empresa empresa = new Empresa();
                            empresa.setIdEmpresa(rs.getInt("Usuario.idEmpresa"));
                            EmpresaDAO dao = new EmpresaDAO();
                            usuario.setEmpresa(dao.getEmpresa(empresa));

                            Cargo cargo = new Cargo();
                            cargo.setIdCargo(rs.getInt("Cargo.idCargo"));
                            cargo.setCargo(rs.getString("Cargo.cargo"));
                            cargo.setPermissoes(rs.getString("Cargo.permissoes"));
                            usuario.setCargo(cargo);
                            
                            usuario.setIdPessoaFisica(rs.getInt("PessoaFisica.idPessoaFisica"));
                            usuario.setNome(rs.getString("PessoaFisica.nome"));
                            usuario.setApelido(rs.getString("PessoaFisica.apelido"));
                            usuario.setSexo(rs.getString("PessoaFisica.sexo"));
                            usuario.setCpf(rs.getString("PessoaFisica.cpf"));
                            usuario.setRg(rs.getString("PessoaFisica.rg"));
                            usuario.setDataNascimento(Data.dataParaAplicacao(rs.getString("PessoaFisica.dataNascimento")));
                            
                            usuario.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                            usuario.setLogradouro(rs.getString("Pessoa.logradouro"));
                            usuario.setNumero(rs.getString("Pessoa.numero"));
                            usuario.setComplemento(rs.getString("Pessoa.complemento"));
                            usuario.setBairro(rs.getString("Pessoa.bairro"));
                            
                            Cidade cidade = new Cidade();
                            cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                            cidade.setCidade(rs.getString("Cidade.cidade"));
                            
                            Estado estado = new Estado();
                            estado.setIdEstado(rs.getInt("Estado.idEstado"));
                            estado.setEstado(rs.getString("Estado.estado"));
                            estado.setUf(rs.getString("Estado.uf"));
                            
                            cidade.setEstado(estado);
                            usuario.setCidade(cidade);
                            
                            usuario.setCep(rs.getString("Pessoa.cep"));
                            usuario.setEmail(rs.getString("Pessoa.email"));
                            usuario.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                            usuario.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                            usuario.setObservacoes(rs.getString("Pessoa.observacoes"));
                            usuario.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                            
                            atualizaDataUltimoAcesso(usuario);
                            Sessao.getInstance().setUsuario(usuario);
                            validacao = 0;
                        }
                        else validacao = 1;
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return validacao;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
        public void atualizaDataUltimoAcesso(Usuario usuario){
            String sql = "UPDATE Usuario SET dataUltimoAcesso = DEFAULT WHERE idUsuario = ?";
                try {
                    PreparedStatement stmt = this.connection.prepareStatement(sql);
                    stmt.setInt(1, usuario.getIdUsuario());
                    stmt.execute();
                    stmt.close();
                    this.connection.close();
                } catch (SQLException e){
                    throw new RuntimeException(e);
                }
        }
    
    public List<Usuario> getListaUsuario (){
        final String sqlUsuario = "SELECT * FROM Usuario "
                            + "INNER JOIN Cargo ON Usuario.idCargo = Cargo.idCargo "
                            + "INNER JOIN PessoaFisica ON Usuario.idPessoaFisica = PessoaFisica.idPessoaFisica "
                            + "INNER JOIN Pessoa ON PessoaFisica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlUsuario);
                List<Usuario> listaUsuario = new ArrayList<>();
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        Usuario usuario = new Usuario();
                        usuario.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                        usuario.setIdUsuario(rs.getInt("Usuario.idUsuario"));
                        usuario.setSalario(rs.getFloat("Usuario.salario"));
                        usuario.setComissao(rs.getFloat("Usuario.comissao"));
                        usuario.setLogin(rs.getString("Usuario.login"));
                        usuario.setSenha(rs.getString("Usuario.senha"));

                        Empresa empresa = new Empresa();
                        empresa.setIdEmpresa(rs.getInt("Usuario.idEmpresa"));
                        EmpresaDAO dao = new EmpresaDAO();
                        usuario.setEmpresa(dao.getEmpresa(empresa));

                        Cargo cargo = new Cargo();
                        cargo.setIdCargo(rs.getInt("Cargo.idCargo"));
                        cargo.setCargo(rs.getString("Cargo.cargo"));
                        cargo.setPermissoes(rs.getString("Cargo.permissoes"));
                        usuario.setCargo(cargo);
                            
                        usuario.setIdPessoaFisica(rs.getInt("PessoaFisica.idPessoaFisica"));
                        usuario.setNome(rs.getString("PessoaFisica.nome"));
                        usuario.setApelido(rs.getString("PessoaFisica.apelido"));
                        usuario.setSexo(rs.getString("PessoaFisica.sexo"));
                        usuario.setCpf(rs.getString("PessoaFisica.cpf"));
                        usuario.setRg(rs.getString("PessoaFisica.rg"));
                        usuario.setDataNascimento(Data.dataParaAplicacao(rs.getString("PessoaFisica.dataNascimento")));
                            
                        usuario.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                        usuario.setLogradouro(rs.getString("Pessoa.logradouro"));
                        usuario.setNumero(rs.getString("Pessoa.numero"));
                        usuario.setComplemento(rs.getString("Pessoa.complemento"));
                        usuario.setBairro(rs.getString("Pessoa.bairro"));
                            
                        Cidade cidade = new Cidade();
                        cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                        cidade.setCidade(rs.getString("Cidade.cidade"));
                            
                        Estado estado = new Estado();
                        estado.setIdEstado(rs.getInt("Estado.idEstado"));
                        estado.setEstado(rs.getString("Estado.estado"));
                        estado.setUf(rs.getString("Estado.uf"));
                            
                        cidade.setEstado(estado);
                        usuario.setCidade(cidade);
                            
                        usuario.setCep(rs.getString("Pessoa.cep"));
                        usuario.setEmail(rs.getString("Pessoa.email"));
                        usuario.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                        usuario.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                        usuario.setObservacoes(rs.getString("Pessoa.observacoes"));
                        usuario.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                        listaUsuario.add(usuario);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaUsuario;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public Usuario getUsuario (Usuario us){
        final String sqlUsuario = "SELECT * FROM Usuario "
                            + "INNER JOIN Cargo ON Usuario.idCargo = Cargo.idCargo "
                            + "INNER JOIN PessoaFisica ON Usuario.idPessoaFisica = PessoaFisica.idPessoaFisica "
                            + "INNER JOIN Pessoa ON PessoaFisica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado WHERE Usuario.idUsuario = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlUsuario);
                stmt.setInt(1, us.getIdUsuario());
                ResultSet rs = stmt.executeQuery();
                Usuario usuario = new Usuario();
                    if(rs.next()){
                        usuario.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                        usuario.setIdUsuario(rs.getInt("Usuario.idUsuario"));
                        usuario.setSalario(rs.getFloat("Usuario.salario"));
                        usuario.setComissao(rs.getFloat("Usuario.comissao"));
                        usuario.setLogin(rs.getString("Usuario.login"));
                        usuario.setSenha(rs.getString("Usuario.senha"));

                        Empresa empresa = new Empresa();
                        empresa.setIdEmpresa(rs.getInt("Usuario.idEmpresa"));
                        EmpresaDAO dao = new EmpresaDAO();
                        usuario.setEmpresa(dao.getEmpresa(empresa));

                        Cargo cargo = new Cargo();
                        cargo.setIdCargo(rs.getInt("Cargo.idCargo"));
                        cargo.setCargo(rs.getString("Cargo.cargo"));
                        cargo.setPermissoes(rs.getString("Cargo.permissoes"));
                        usuario.setCargo(cargo);
                            
                        usuario.setIdPessoaFisica(rs.getInt("PessoaFisica.idPessoaFisica"));
                        usuario.setNome(rs.getString("PessoaFisica.nome"));
                        usuario.setApelido(rs.getString("PessoaFisica.apelido"));
                        usuario.setSexo(rs.getString("PessoaFisica.sexo"));
                        usuario.setCpf(rs.getString("PessoaFisica.cpf"));
                        usuario.setRg(rs.getString("PessoaFisica.rg"));
                        usuario.setDataNascimento(Data.dataParaAplicacao(rs.getString("PessoaFisica.dataNascimento")));
                            
                        usuario.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                        usuario.setLogradouro(rs.getString("Pessoa.logradouro"));
                        usuario.setNumero(rs.getString("Pessoa.numero"));
                        usuario.setComplemento(rs.getString("Pessoa.complemento"));
                        usuario.setBairro(rs.getString("Pessoa.bairro"));
                            
                        Cidade cidade = new Cidade();
                        cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                        cidade.setCidade(rs.getString("Cidade.cidade"));
                            
                        Estado estado = new Estado();
                        estado.setIdEstado(rs.getInt("Estado.idEstado"));
                        estado.setEstado(rs.getString("Estado.estado"));
                        estado.setUf(rs.getString("Estado.uf"));
                            
                        cidade.setEstado(estado);
                        usuario.setCidade(cidade);
                            
                        usuario.setCep(rs.getString("Pessoa.cep"));
                        usuario.setEmail(rs.getString("Pessoa.email"));
                        usuario.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                        usuario.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                        usuario.setObservacoes(rs.getString("Pessoa.observacoes"));
                        usuario.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return usuario;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public List<Usuario> buscaUsuario (String palavrasChave, int index){
        String sqlUsuario = "SELECT * FROM Usuario "
                            + "INNER JOIN Cargo ON Usuario.idCargo = Cargo.idCargo "
                            + "INNER JOIN PessoaFisica ON Usuario.idPessoaFisica = PessoaFisica.idPessoaFisica "
                            + "INNER JOIN Pessoa ON PessoaFisica.idPessoa = Pessoa.idPessoa "
                            + "INNER JOIN Cidade ON Pessoa.idCidade = Cidade.idCidade "
                            + "INNER JOIN Estado ON Cidade.idEstado = Estado.idEstado WHERE ";
        if(index==0)
            sqlUsuario += "PessoaFisica.nome like ?";
        else if (index==1)
            sqlUsuario += "PessoaFisica.apelido like ?";
        else if (index==2)
            sqlUsuario += "PessoaFisica.cpf like ?";
        else if (index==3)
            sqlUsuario += "Pessoa.observacoes like ?";

            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqlUsuario);
                stmt.setString(1, "%" + palavrasChave + "%");   
                List<Usuario> listaUsuario = new ArrayList<>();
                ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        Usuario usuario = new Usuario();
                        usuario.setIsHabilitado(rs.getInt("Pessoa.isHabilitado"));
                        usuario.setIdUsuario(rs.getInt("Usuario.idUsuario"));
                        usuario.setSalario(rs.getFloat("Usuario.salario"));
                        usuario.setComissao(rs.getFloat("Usuario.comissao"));
                        usuario.setLogin(rs.getString("Usuario.login"));
                        usuario.setSenha(rs.getString("Usuario.senha"));

                        Empresa empresa = new Empresa();
                        empresa.setIdEmpresa(rs.getInt("Usuario.idEmpresa"));
                        EmpresaDAO dao = new EmpresaDAO();
                        usuario.setEmpresa(dao.getEmpresa(empresa));

                        Cargo cargo = new Cargo();
                        cargo.setIdCargo(rs.getInt("Cargo.idCargo"));
                        cargo.setCargo(rs.getString("Cargo.cargo"));
                        cargo.setPermissoes(rs.getString("Cargo.permissoes"));
                        usuario.setCargo(cargo);
                            
                        usuario.setIdPessoaFisica(rs.getInt("PessoaFisica.idPessoaFisica"));
                        usuario.setNome(rs.getString("PessoaFisica.nome"));
                        usuario.setApelido(rs.getString("PessoaFisica.apelido"));
                        usuario.setSexo(rs.getString("PessoaFisica.sexo"));
                        usuario.setCpf(rs.getString("PessoaFisica.cpf"));
                        usuario.setRg(rs.getString("PessoaFisica.rg"));
                        usuario.setDataNascimento(Data.dataParaAplicacao(rs.getString("PessoaFisica.dataNascimento")));
                            
                        usuario.setIdPessoa(rs.getInt("Pessoa.idPessoa"));
                        usuario.setLogradouro(rs.getString("Pessoa.logradouro"));
                        usuario.setNumero(rs.getString("Pessoa.numero"));
                        usuario.setComplemento(rs.getString("Pessoa.complemento"));
                        usuario.setBairro(rs.getString("Pessoa.bairro"));
                            
                        Cidade cidade = new Cidade();
                        cidade.setIdCidade(rs.getInt("Cidade.idCidade"));
                        cidade.setCidade(rs.getString("Cidade.cidade"));
                            
                        Estado estado = new Estado();
                        estado.setIdEstado(rs.getInt("Estado.idEstado"));
                        estado.setEstado(rs.getString("Estado.estado"));
                        estado.setUf(rs.getString("Estado.uf"));
                            
                        cidade.setEstado(estado);
                        usuario.setCidade(cidade);
                            
                        usuario.setCep(rs.getString("Pessoa.cep"));
                        usuario.setEmail(rs.getString("Pessoa.email"));
                        usuario.setTelefoneFixo(rs.getString("Pessoa.telefoneFixo"));
                        usuario.setTelefoneCelular(rs.getString("Pessoa.telefoneCelular"));
                        usuario.setObservacoes(rs.getString("Pessoa.observacoes"));
                        usuario.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Pessoa.dataCadastro")));
                        listaUsuario.add(usuario);
                    }
                stmt.close();
                rs.close();
                this.connection.close();
                return listaUsuario;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public int getLastCodeUsuario(){
        try {
            if(this.connection.isClosed())
                this.connection = new ConnectionFactory().getConnection();
            PreparedStatement stmt = this.connection.prepareStatement("SELECT MAX(idUsuario) AS idUsuario FROM Usuario LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if(rs.next()){
                    code = rs.getInt("idUsuario");
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
