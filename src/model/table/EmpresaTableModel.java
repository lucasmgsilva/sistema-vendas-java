package model.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.Empresa;

public class EmpresaTableModel extends AbstractTableModel{
    private List<Empresa> lista = new ArrayList<>();
    private String[] colunas = {"Razão Social", "Nome Fantasia", "CNPJ", "Cidade", "Estado"};
    
    
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
                return lista.get(linha).getRazaoSocial();
            case 1:
                return lista.get(linha).getNomeFantasia();
            case 2:
                return lista.get(linha).getCnpj();
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
    
    public void addRow (Empresa empresa){
        this.lista.add(empresa);
        this.fireTableDataChanged();
    }
    
    public void removeRow(int linha){
        this.lista.remove(linha);
        this.fireTableRowsDeleted(linha, linha);
    }
    
    public void removeRow(Empresa empresa){
        this.lista.remove(empresa);
        this.fireTableDataChanged();
    }
    
    public Object getRow (int linha){
        return this.lista.get(linha);
    }
    
}
