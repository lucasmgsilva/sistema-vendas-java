package model.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.ItemComprado;

public class ItemCompradoTableModel extends AbstractTableModel{
    private List<ItemComprado> lista = new ArrayList<>();
    private String[] colunas = {"Descrição", "Unidade de Medida", "Qtde.", "Preço Unitário", "Preço Total", "Lucro", "Preço de Venda"};
    
    
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
                return lista.get(linha).getQtdComprada();
            case 3:
                return String.format("R$ %.4f", lista.get(linha).getPrecoCompra());
            case 4:
                return String.format("R$ %.2f", (lista.get(linha).getPrecoCompra())*(lista.get(linha).getQtdComprada()));
            case 5:
                return String.format("%.2f %%", (lista.get(linha).getProduto().getPrecoVenda()/lista.get(linha).getPrecoCompra()-1)*100);
            case 6:
                return String.format("R$ %.2f ", lista.get(linha).getPrecoCompra()*((lista.get(linha).getProduto().getPrecoVenda()/lista.get(linha).getPrecoCompra()-1)*100)/100+lista.get(linha).getPrecoCompra());
        }
        return null;
    }
    
   public void clearRows(){
        this.lista.clear();
        fireTableDataChanged();
    }
    
    public void addRow (ItemComprado itemComprado){
        this.lista.add(itemComprado);
        this.fireTableDataChanged();
    }
    
    public void removeRow(int linha){
        this.lista.remove(linha);
        this.fireTableRowsDeleted(linha, linha);
    }
    
    public Object getRow (int linha){
        return this.lista.get(linha);
    }
    
    public List<ItemComprado> getList (){
        return this.lista;
    }
    
    public void updateRow(int linha, ItemComprado itemComprado){
       this.lista.set(linha, itemComprado);
       this.fireTableRowsUpdated(linha, linha);
    }
}
