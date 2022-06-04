package model.bean;

import java.util.List;

public class Venda {
    private int idVenda;
    private String dataVenda;
    private float desconto;
    private float precoTotal;
    private Object pessoa;
    private Usuario usuario;
    private List<ItemVendido> itensVendidos;
    
    public int getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }

    public String getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(String dataVenda) {
        this.dataVenda = dataVenda;
    }

    public float getDesconto() {
        return desconto;
    }

    public void setDesconto(float desconto) {
        this.desconto = desconto;
    }

    public float getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(float precoTotal) {
        this.precoTotal = precoTotal;
    }

    public Object getPessoa() {
        return pessoa;
    }

    public void setPessoa(Object pessoa) {
        this.pessoa = pessoa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    } 

    public List<ItemVendido> getItensVendidos() {
        return itensVendidos;
    }

    public void setItensVendidos(List<ItemVendido> itensVendidos) {
        this.itensVendidos = itensVendidos;
    }
}
