package model.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.PessoaFisica;
import model.bean.PessoaJuridica;

public class ClienteTableModel extends AbstractTableModel{
    private List<Object> lista = new ArrayList<>();
    private String[] colunas = {"Nome/Razão Social", "Apelido/Nome Fantasia", "CPF/CNPJ", "Cidade", "Estado"};

    public void setColunas(String[] colunas) {
        this.colunas = colunas;
    }
    
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
                if(this.lista.get(linha) instanceof PessoaFisica)
                    return ((PessoaFisica) lista.get(linha)).getNome();
                else return ((PessoaJuridica) lista.get(linha)).getRazaoSocial();
            case 1:
                if(this.lista.get(linha) instanceof PessoaFisica)
                    return ((PessoaFisica) lista.get(linha)).getApelido();
                else return ((PessoaJuridica) lista.get(linha)).getNomeFantasia();
            case 2:
                if(this.lista.get(linha) instanceof PessoaFisica)
                    return ((PessoaFisica) lista.get(linha)).getCpf();
                else return ((PessoaJuridica) lista.get(linha)).getCnpj();
            case 3:
                if(this.lista.get(linha) instanceof PessoaFisica)
                    return ((PessoaFisica) lista.get(linha)).getCidade().getCidade();
                else return ((PessoaJuridica) lista.get(linha)).getCidade().getCidade();
            case 4:
                if(this.lista.get(linha) instanceof PessoaFisica)
                    return ((PessoaFisica) lista.get(linha)).getCidade().getEstado().getUf();
                else return ((PessoaJuridica) lista.get(linha)).getCidade().getEstado().getUf();
        }
        return null;
    }
    
   public void clearRows(){
        this.lista.clear();
        fireTableDataChanged();
    }
    
    public void addRow (Object cliente){
        this.lista.add(cliente);
        this.fireTableDataChanged();
    }
    
    public void removeRow(int linha){
        this.lista.remove(linha);
        this.fireTableRowsDeleted(linha, linha);
    }
    
    public void removeRow(Object cliente){
        this.lista.remove(cliente);
        this.fireTableDataChanged();
    }
    
    public Object getRow (int linha){
        return this.lista.get(linha);
    }
    
}
