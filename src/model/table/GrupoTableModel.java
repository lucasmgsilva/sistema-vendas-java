package model.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.Grupo;

public class GrupoTableModel extends AbstractTableModel{
    private List<Grupo> lista = new ArrayList<>();
    private String[] colunas = {"Grupo"};
    
    
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
                return lista.get(linha).getGrupo();
        }
        return null;
    }
    
   public void clearRows(){
        this.lista.clear();
        fireTableDataChanged();
    }
    
    public void addRow (Grupo grupo){
        this.lista.add(grupo);
        this.fireTableDataChanged();
    }
    
    public void removeRow(int linha){
        this.lista.remove(linha);
        this.fireTableRowsDeleted(linha, linha);
    }
    
    public Object getRow (int linha){
        return this.lista.get(linha);
    }

    @Override
    public void setValueAt(Object o, int linha, int linha2) {
        super.setValueAt(o, linha, linha2);
        this.fireTableRowsUpdated(linha, linha2);
    }
    
    
}
