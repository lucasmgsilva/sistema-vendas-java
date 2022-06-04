package model.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.Compra;
import model.bean.PessoaJuridica;

public class CompraTableModel extends AbstractTableModel{
    private List<Compra> lista = new ArrayList<>();
    private String[] colunas = {"Número", "Fornecedor", "Data", "Chave de Acesso", "Preço Final", "Usuário"};
    
    
    @Override
    public int getRowCount() {
        return this.lista.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int i) {
        return colunas[i];
    }
    
    @Override
    public Object getValueAt(int linha, int coluna) {
        switch (coluna){
            case 0:
                return lista.get(linha).getIdCompra();
            case 1:
                return ((PessoaJuridica) lista.get(linha).getFornecedor()).getRazaoSocial();
            case 2:
                return lista.get(linha).getDataCompra();
            case 3:
                return lista.get(linha).getChaveAcesso();
            case 4:
                return String.format("R$ %.2f", lista.get(linha).getPrecoTotal());
            case 5:
                return lista.get(linha).getUsuario().getNome();
        }
        return null;
    }
    
   public void clearRows(){
        this.lista.clear();
        fireTableDataChanged();
    }
    
    public void addRow (Compra compra){
        this.lista.add(compra);
        this.fireTableDataChanged();
    }
    
    public void removeRow(int linha){
        this.lista.remove(linha);
        this.fireTableRowsDeleted(linha, linha);
    }
    
    public void removeRow(Compra compra){
        this.lista.remove(compra);
        this.fireTableDataChanged();
    }
    
    public Object getRow (int linha){
        return this.lista.get(linha);
    }
}
