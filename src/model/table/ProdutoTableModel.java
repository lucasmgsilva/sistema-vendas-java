package model.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.Produto;

public class ProdutoTableModel extends AbstractTableModel{
    private List<Produto> lista = new ArrayList<>();
    private String[] colunas = {"Descrição", "Unidade de Medida", "Qtde. Disponível", "Preço de Venda", "Cód. Fabricante", "Cód. Original", "Localização"};
    
    
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
                return lista.get(linha).getDescricao();
            case 1:
                return lista.get(linha).getUnidadeMedida().getSigla();
            case 2:
                return lista.get(linha).getQtdDisponivel();
            case 3:
                return String.format("R$ %.2f", lista.get(linha).getPrecoVenda());
            case 4:
                return lista.get(linha).getCodigoFabricante();
            case 5:
                return lista.get(linha).getCodigoOriginal();
            case 6:
                return lista.get(linha).getLocalizacao();
        }
        return null;
    }
    
   public void clearRows(){
        this.lista.clear();
        fireTableDataChanged();
    }
    
    public void addRow (Produto produto){
        this.lista.add(produto);
        this.fireTableDataChanged();
    }
    
    public void removeRow(int linha){
        this.lista.remove(linha);
        this.fireTableRowsDeleted(linha, linha);
    }
    
    public void removeRow(Produto produto){
        this.lista.remove(produto);
        this.fireTableDataChanged();
    }
    
    public Object getRow (int linha){
        return this.lista.get(linha);
    }
}
