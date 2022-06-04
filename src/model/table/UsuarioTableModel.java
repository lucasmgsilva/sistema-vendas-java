package model.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.Usuario;

public class UsuarioTableModel extends AbstractTableModel{
    private List<Usuario> lista = new ArrayList<>();
    private String[] colunas = {"Nome", "Cargo", "CPF", "Cidade", "Estado"};
    
    
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
                return lista.get(linha).getNome();
            case 1:
                return lista.get(linha).getCargo().getCargo();
            case 2:
                return lista.get(linha).getCpf();
            case 3:
                return lista.get(linha).getCidade().getCidade();
            case 4:
                return lista.get(linha).getCidade().getEstado().getUf();
        }
        return null;
    }
    
   public void clearRows(){
        this.lista.clear();
        fireTableDataChanged();
    }
    
    public void addRow (Usuario usuario){
        this.lista.add(usuario);
        this.fireTableDataChanged();
    }
    
    public void removeRow(int linha){
        this.lista.remove(linha);
        this.fireTableRowsDeleted(linha, linha);
    }
    
    public void removeRow(Usuario usuario){
        this.lista.remove(usuario);
        this.fireTableDataChanged();
    }
    
    public Object getRow (int linha){
        return this.lista.get(linha);
    }
    
}
