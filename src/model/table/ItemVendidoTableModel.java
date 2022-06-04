package model.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.ItemVendido;

public class ItemVendidoTableModel extends AbstractTableModel{
    private List<ItemVendido> lista = new ArrayList<>();
    private String[] colunas = {"Descrição", "Unidade de Medida", "Qtde.", "Preço Unitário", "Preço Total"};
    
    
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
                return lista.get(linha).getProduto().getDescricao();
            case 1:
                return lista.get(linha).getProduto().getUnidadeMedida();
            case 2:
                return lista.get(linha).getQtdVendida();
            case 3:
                return String.format("R$ %.2f", lista.get(linha).getPrecoVenda());
            case 4:
                return String.format("R$ %.2f", (lista.get(linha).getPrecoVenda())*(lista.get(linha).getQtdVendida()));
        }
        return null;
    }
    
   public void clearRows(){
        this.lista.clear();
        fireTableDataChanged();
    }
    
    public void addRow (ItemVendido itemVendido){
        this.lista.add(itemVendido);
        this.fireTableDataChanged();
    }
    
    public void removeRow(int linha){
        this.lista.remove(linha);
        this.fireTableRowsDeleted(linha, linha);
    }
    
    public Object getRow (int linha){
        return this.lista.get(linha);
    }
    
    public List<ItemVendido> getList (){
        return this.lista;
    }
    
    public void updateRow(int linha, ItemVendido itemVendido){
       this.lista.set(linha, itemVendido);
       this.fireTableRowsUpdated(linha, linha);
    }
}
