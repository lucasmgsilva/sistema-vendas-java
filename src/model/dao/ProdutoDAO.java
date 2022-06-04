package model.dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import others.Data;
import model.bean.Grupo;
import model.bean.ItemVendido;
import model.bean.Marca;
import model.bean.Produto;
import model.bean.Subgrupo;
import model.bean.UnidadeMedida;

public class ProdutoDAO {

    private Connection connection;

    public ProdutoDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public boolean adicionaProduto(Produto produto) {
        final String sqlProduto = "INSERT INTO Produto (descricao, idUnidadeMedida, idSubgrupo,"
                + " qtdMinima, qtdDisponivel, precoCompra, precoVenda, idMarca, codigoFabricante,"
                + " codigoOriginal, localizacao, dimensoes, observacoes, dataCadastro, isHabilitado)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlProduto);
            stmt.setString(1, produto.getDescricao());
            stmt.setInt(2, produto.getUnidadeMedida().getIdUnidadeMedida());
            stmt.setInt(3, produto.getSubgrupo().getIdSubgrupo());
            stmt.setInt(4, produto.getQtdMinima());
            stmt.setInt(5, produto.getQtdDisponivel());
            stmt.setFloat(6, produto.getPrecoCompra());
            stmt.setFloat(7, produto.getPrecoVenda());
            stmt.setInt(8, produto.getMarca().getIdMarca());
            stmt.setString(9, produto.getCodigoFabricante());
            stmt.setString(10, produto.getCodigoOriginal());
            stmt.setString(11, produto.getLocalizacao());
            stmt.setString(12, produto.getDimensoes());
            stmt.setString(13, produto.getObservacoes());
            stmt.setString(14, Data.dataHoraParaBanco(produto.getDataCadastro()));
            stmt.setInt(15, produto.getIsHabilitado());
            stmt.execute();
            stmt.close();
            this.connection.close();
            return true;
        } catch (SQLException e) {
            if (e.toString().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(null, "Já existe um produto com esta descrição, código e marca.", "Caixa de Afirmação", 0);
            } else {
                JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado.\n " + e, "Caixa de Afirmação", 0);
            }
            return false;
        }
    }

    public boolean atualizaProduto(Produto produto) {
        final String sqlProduto = "UPDATE Produto set descricao = ?, idUnidadeMedida = ?, idSubGrupo = ?,"
                + " qtdMinima = ?, qtdDisponivel = ?, precoCompra = ?, precoVenda = ?, idMarca = ?, codigoFabricante = ?,"
                + " codigoOriginal = ?, localizacao = ?, dimensoes = ?, observacoes = ?, dataCadastro = ?, isHabilitado = ?"
                + " WHERE idProduto = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlProduto);
            stmt.setString(1, produto.getDescricao());
            stmt.setInt(2, produto.getUnidadeMedida().getIdUnidadeMedida());
            stmt.setInt(3, produto.getSubgrupo().getIdSubgrupo());
            stmt.setInt(4, produto.getQtdMinima());
            stmt.setInt(5, produto.getQtdDisponivel());
            stmt.setFloat(6, produto.getPrecoCompra());
            stmt.setFloat(7, produto.getPrecoVenda());
            stmt.setInt(8, produto.getMarca().getIdMarca());
            stmt.setString(9, produto.getCodigoFabricante());
            stmt.setString(10, produto.getCodigoOriginal());
            stmt.setString(11, produto.getLocalizacao());
            stmt.setString(12, produto.getDimensoes());
            stmt.setString(13, produto.getObservacoes());
            stmt.setString(14, Data.dataHoraParaBanco(produto.getDataCadastro()));
            stmt.setInt(15, produto.getIsHabilitado());
            stmt.setInt(16, produto.getIdProduto());
            stmt.execute();
            stmt.close();
            this.connection.close();
            return true;
        } catch (SQLException e) {
            if (e.toString().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(null, "Já existe um produto com esta descrição, código e marca.", "Caixa de Afirmação", 0);
            } else {
                JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado.\n " + e, "Caixa de Afirmação", 0);
            }
            return false;
        }
    }

    public boolean removeProduto(Produto produto) {
        final String sqlProduto = "DELETE FROM Produto WHERE idProduto = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlProduto);
            stmt.setInt(1, produto.getIdProduto());
            stmt.execute();
            this.connection.close();
            return true;
        } catch (SQLException e) {
            if (e.toString().contains("Cannot delete or update a parent row")) {
                return false;
            }
        }
        return false;
    }

    public List<Produto> getListaProduto() {
        final String sqlEmpresa = "SELECT * FROM Produto "
                + "JOIN UnidadeMedida ON Produto.idUnidadeMedida = UnidadeMedida.idUnidadeMedida "
                + "JOIN Subgrupo ON Produto.idSubGrupo = Subgrupo.idSubgrupo "
                + "JOIN Grupo ON Subgrupo.idGrupo = Grupo.idGrupo "
                + "JOIN Marca ON Produto.idMarca = Marca.idMarca ORDER BY Produto.idProduto";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlEmpresa);
            List<Produto> listaProduto = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Produto produto = new Produto();
                produto.setIdProduto(rs.getInt("Produto.idProduto"));
                produto.setDescricao(rs.getString("Produto.descricao"));

                UnidadeMedida unidadeMedida = new UnidadeMedida();
                unidadeMedida.setIdUnidadeMedida(rs.getInt("UnidadeMedida.idUnidadeMedida"));
                unidadeMedida.setSigla(rs.getString("UnidadeMedida.sigla"));
                unidadeMedida.setUnidadeMedida(rs.getString("UnidadeMedida.unidadeMedida"));
                produto.setUnidadeMedida(unidadeMedida);

                Grupo grupo = new Grupo();
                grupo.setIdGrupo(rs.getInt("Grupo.idGrupo"));
                grupo.setGrupo(rs.getString("Grupo.grupo"));

                Subgrupo subgrupo = new Subgrupo();
                subgrupo.setIdSubgrupo(rs.getInt("Subgrupo.idSubgrupo"));
                subgrupo.setSubgrupo(rs.getString("Subgrupo.subGrupo"));
                subgrupo.setGrupo(grupo);
                produto.setSubgrupo(subgrupo);

                produto.setQtdMinima(rs.getInt("Produto.qtdMinima"));
                produto.setQtdDisponivel(rs.getInt("Produto.qtdDisponivel"));
                produto.setPrecoCompra(rs.getFloat("Produto.precoCompra"));
                produto.setPrecoVenda(rs.getFloat("Produto.precoVenda"));

                Marca marca = new Marca();
                marca.setIdMarca(rs.getInt("Marca.idMarca"));
                marca.setMarca(rs.getString("Marca.marca"));
                produto.setMarca(marca);

                produto.setCodigoFabricante(rs.getString("Produto.codigoFabricante"));
                produto.setCodigoOriginal(rs.getString("Produto.codigoOriginal"));
                produto.setLocalizacao(rs.getString("Produto.localizacao"));
                produto.setDimensoes(rs.getString("Produto.dimensoes"));
                produto.setObservacoes(rs.getString("Produto.observacoes"));
                produto.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Produto.dataCadastro")));
                produto.setIsHabilitado(rs.getInt("Produto.isHabilitado"));
                listaProduto.add(produto);
            }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaProduto;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<Produto> getListaProdutoVenda() {
        final String sqlEmpresa = "SELECT * FROM Produto "
                + "JOIN UnidadeMedida ON Produto.idUnidadeMedida = UnidadeMedida.idUnidadeMedida "
                + "JOIN Subgrupo ON Produto.idSubGrupo = Subgrupo.idSubgrupo "
                + "JOIN Grupo ON Subgrupo.idGrupo = Grupo.idGrupo "
                + "JOIN Marca ON Produto.idMarca = Marca.idMarca WHERE Produto.isHabilitado = 1 ORDER BY Produto.idProduto";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlEmpresa);
            List<Produto> listaProduto = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Produto produto = new Produto();
                produto.setIdProduto(rs.getInt("Produto.idProduto"));
                produto.setDescricao(rs.getString("Produto.descricao"));

                UnidadeMedida unidadeMedida = new UnidadeMedida();
                unidadeMedida.setIdUnidadeMedida(rs.getInt("UnidadeMedida.idUnidadeMedida"));
                unidadeMedida.setSigla(rs.getString("UnidadeMedida.sigla"));
                unidadeMedida.setUnidadeMedida(rs.getString("UnidadeMedida.unidadeMedida"));
                produto.setUnidadeMedida(unidadeMedida);

                Grupo grupo = new Grupo();
                grupo.setIdGrupo(rs.getInt("Grupo.idGrupo"));
                grupo.setGrupo(rs.getString("Grupo.grupo"));

                Subgrupo subgrupo = new Subgrupo();
                subgrupo.setIdSubgrupo(rs.getInt("Subgrupo.idSubgrupo"));
                subgrupo.setSubgrupo(rs.getString("Subgrupo.subGrupo"));
                subgrupo.setGrupo(grupo);
                produto.setSubgrupo(subgrupo);

                produto.setQtdMinima(rs.getInt("Produto.qtdMinima"));
                produto.setQtdDisponivel(rs.getInt("Produto.qtdDisponivel"));
                produto.setPrecoCompra(rs.getFloat("Produto.precoCompra"));
                produto.setPrecoVenda(rs.getFloat("Produto.precoVenda"));

                Marca marca = new Marca();
                marca.setIdMarca(rs.getInt("Marca.idMarca"));
                marca.setMarca(rs.getString("Marca.marca"));
                produto.setMarca(marca);

                produto.setCodigoFabricante(rs.getString("Produto.codigoFabricante"));
                produto.setCodigoOriginal(rs.getString("Produto.codigoOriginal"));
                produto.setLocalizacao(rs.getString("Produto.localizacao"));
                produto.setDimensoes(rs.getString("Produto.dimensoes"));
                produto.setObservacoes(rs.getString("Produto.observacoes"));
                produto.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Produto.dataCadastro")));
                produto.setIsHabilitado(rs.getInt("Produto.isHabilitado"));
                listaProduto.add(produto);
            }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaProduto;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Produto getProduto(Produto p) {
        final String sqlEmpresa = "SELECT * FROM Produto "
                + "JOIN UnidadeMedida ON Produto.idUnidadeMedida = UnidadeMedida.idUnidadeMedida "
                //+ "JOIN Subgrupo ON Produto.idSubGrupo = Subgrupo.idSubgrupo "
                //+ "JOIN Grupo ON Subgrupo.idGrupo = Grupo.idGrupo "
                + "JOIN Marca ON Produto.idMarca = Marca.idMarca WHERE Produto.idProduto = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlEmpresa);
            stmt.setInt(1, p.getIdProduto());
            ResultSet rs = stmt.executeQuery();
            Produto produto = new Produto();
            if (rs.next()) {
                produto.setIdProduto(rs.getInt("Produto.idProduto"));
                produto.setDescricao(rs.getString("Produto.descricao"));

                UnidadeMedida unidadeMedida = new UnidadeMedida();
                unidadeMedida.setIdUnidadeMedida(rs.getInt("UnidadeMedida.idUnidadeMedida"));
                unidadeMedida.setSigla(rs.getString("UnidadeMedida.sigla"));
                unidadeMedida.setUnidadeMedida(rs.getString("UnidadeMedida.unidadeMedida"));
                produto.setUnidadeMedida(unidadeMedida);

                /*Grupo grupo = new Grupo();
                    grupo.setIdGrupo(rs.getInt("Grupo.idGrupo"));
                    grupo.setGrupo(rs.getString("Grupo.grupo"));
                    
                    Subgrupo subgrupo = new Subgrupo();
                    subgrupo.setIdSubgrupo(rs.getInt("Subgrupo.idSubgrupo"));
                    subgrupo.setSubgrupo(rs.getString("Subgrupo.subGrupo"));
                    subgrupo.setGrupo(grupo);
                    produto.setSubgrupo(subgrupo);
                    
                    produto.setQtdMinima(rs.getInt("Produto.qtdMinima"));*/
                produto.setQtdDisponivel(rs.getInt("Produto.qtdDisponivel"));
                //produto.setPrecoCompra(rs.getFloat("Produto.precoCompra"));
                produto.setPrecoVenda(rs.getFloat("Produto.precoVenda"));

                Marca marca = new Marca();
                marca.setIdMarca(rs.getInt("Marca.idMarca"));
                marca.setMarca(rs.getString("Marca.marca"));
                produto.setMarca(marca);

                /*produto.setCodigoFabricante(rs.getString("Produto.codigoFabricante"));
                    produto.setCodigoOriginal(rs.getString("Produto.codigoOriginal"));
                    produto.setLocalizacao(rs.getString("Produto.localizacao"));
                    produto.setDimensoes(rs.getString("Produto.dimensoes"));
                    produto.setObservacoes(rs.getString("Produto.observacoes"));
                    produto.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Produto.dataCadastro")));
                    produto.setIsHabilitado(rs.getInt("Produto.isHabilitado"));*/
            }
            rs.close();
            stmt.close();
            this.connection.close();
            return produto;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Produto> buscaProduto(String palavrasChave, int index) {
        String sqlEmpresa = "SELECT * FROM Produto "
                + "JOIN UnidadeMedida ON Produto.idUnidadeMedida = UnidadeMedida.idUnidadeMedida "
                + "JOIN Subgrupo ON Produto.idSubGrupo = Subgrupo.idSubgrupo "
                + "JOIN Grupo ON Subgrupo.idGrupo = Grupo.idGrupo "
                + "JOIN Marca ON Produto.idMarca = Marca.idMarca WHERE ";

        if (index == 0) {
            sqlEmpresa += "Produto.descricao like ? ORDER BY idProduto";
        } else if (index == 1) {
            sqlEmpresa += "Produto.codigoFabricante like ? ORDER BY idProduto";
        } else if (index == 2) {
            sqlEmpresa += "Produto.codigoOriginal like ? ORDER BY idProduto";
        } else if (index == 3) {
            sqlEmpresa += "Produto.observacoes like ? ORDER BY idProduto";
        }

        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlEmpresa);
            stmt.setString(1, "%" + palavrasChave + "%");
            List<Produto> listaProduto = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Produto produto = new Produto();
                produto.setIdProduto(rs.getInt("Produto.idProduto"));
                produto.setDescricao(rs.getString("Produto.descricao"));

                UnidadeMedida unidadeMedida = new UnidadeMedida();
                unidadeMedida.setIdUnidadeMedida(rs.getInt("UnidadeMedida.idUnidadeMedida"));
                unidadeMedida.setSigla(rs.getString("UnidadeMedida.sigla"));
                unidadeMedida.setUnidadeMedida(rs.getString("UnidadeMedida.unidadeMedida"));
                produto.setUnidadeMedida(unidadeMedida);

                Grupo grupo = new Grupo();
                grupo.setIdGrupo(rs.getInt("Grupo.idGrupo"));
                grupo.setGrupo(rs.getString("Grupo.grupo"));

                Subgrupo subgrupo = new Subgrupo();
                subgrupo.setIdSubgrupo(rs.getInt("Subgrupo.idSubgrupo"));
                subgrupo.setSubgrupo(rs.getString("Subgrupo.subGrupo"));
                subgrupo.setGrupo(grupo);
                produto.setSubgrupo(subgrupo);

                produto.setQtdMinima(rs.getInt("Produto.qtdMinima"));
                produto.setQtdDisponivel(rs.getInt("Produto.qtdDisponivel"));
                produto.setPrecoCompra(rs.getFloat("Produto.precoCompra"));
                produto.setPrecoVenda(rs.getFloat("Produto.precoVenda"));

                Marca marca = new Marca();
                marca.setIdMarca(rs.getInt("Marca.idMarca"));
                marca.setMarca(rs.getString("Marca.marca"));
                produto.setMarca(marca);

                produto.setCodigoFabricante(rs.getString("Produto.codigoFabricante"));
                produto.setCodigoOriginal(rs.getString("Produto.codigoOriginal"));
                produto.setLocalizacao(rs.getString("Produto.localizacao"));
                produto.setDimensoes(rs.getString("Produto.dimensoes"));
                produto.setObservacoes(rs.getString("Produto.observacoes"));
                produto.setDataCadastro(Data.dataHoraParaAplicacao(rs.getString("Produto.dataCadastro")));
                produto.setIsHabilitado(rs.getInt("Produto.isHabilitado"));
                listaProduto.add(produto);
            }
            rs.close();
            stmt.close();
            this.connection.close();
            return listaProduto;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aumentaEstoqueProduto(Produto produto) {
        final String sqProduto = "UPDATE Produto SET qtdDisponivel = qtdDisponivel+?, precoCompra = ?, precoVenda = ? WHERE idProduto = ?";
            try {
                if(this.connection.isClosed())
                    this.connection = new ConnectionFactory().getConnection();
                PreparedStatement stmt = this.connection.prepareStatement(sqProduto);
                stmt.setInt(1, produto.getQtdDisponivel());
                stmt.setFloat(2, produto.getPrecoCompra());
                stmt.setFloat(3, produto.getPrecoVenda());
                stmt.setInt(4, produto.getIdProduto());
                stmt.execute();
                stmt.close();
                this.connection.close();
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    
    public void baixaEstoqueProduto(List<ItemVendido> lista) {
        final String sqProduto = "UPDATE Produto SET qtdDisponivel = qtdDisponivel-? WHERE idProduto = ?";
            try {
                PreparedStatement stmt = this.connection.prepareStatement(sqProduto);
                    for(ItemVendido iv:lista){
                        stmt.setInt(1, iv.getQtdVendida());
                        stmt.setInt(2, iv.getProduto().getIdProduto());
                        stmt.execute();
                    }
                stmt.close();
                this.connection.close();
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }

    public int getLastCodeProduto() {
        try {
            if (this.connection.isClosed()) {
                this.connection = new ConnectionFactory().getConnection();
            }
            PreparedStatement stmt = this.connection.prepareStatement("SELECT MAX(idProduto) AS idProduto FROM Produto LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            int code = -1;
            if (rs.next()) {
                code = rs.getInt("idProduto");
            }
            stmt.close();
            rs.close();
            this.connection.close();
            return code;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    
    public int verificaExistenciaProduto(String descricao) {
        try {
            if (this.connection.isClosed()) {
                this.connection = new ConnectionFactory().getConnection();
            }
            PreparedStatement stmt = this.connection.prepareStatement("SELECT idProduto FROM Produto WHERE descricao = ?");
            stmt.setString(1, descricao);
            ResultSet rs = stmt.executeQuery();
            int code = -1;
                if (rs.next()) {
                    code = rs.getInt("idProduto");
                }
            stmt.close();
            rs.close();
            this.connection.close();
            return code;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }  
}
