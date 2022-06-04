package model.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.bean.PessoaFisica;
import model.bean.PessoaJuridica;
import model.bean.Venda;

public class VendaTableModel extends AbstractTableModel{
    private List<Venda> lista = new ArrayList<>();
    private String[] colunas = {"Número", "Cliente", "Data", "Preço Total", "Desconto", "Preço Final", "Usuário"};
    
    
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
                return lista.get(linha).getIdVenda();
            case 1:
                if(lista.get(linha).getPessoa() instanceof PessoaFisica)
                    return ((PessoaFisica) lista.get(linha).getPessoa()).getNome();
                else return ((PessoaJuridica) lista.get(linha).getPessoa()).getRazaoSocial();
            case 2:
                return lista.get(linha).getDataVenda();
            case 3:
                return String.format("R$ %.2f", lista.get(linha).getPrecoTotal());
            case 4:
                return String.format("R$ %.2f", lista.get(linha).getDesconto());
            case 5:
                return String.format("R$ %.2f", lista.get(linha).getPrecoTotal()-lista.get(linha).getDesconto());
            case 6:
                return lista.get(linha).getUsuario().getNome();
        }
        return null;
    }
    
   public void clearRows(){
        this.lista.clear();
        fireTableDataChanged();
    }
    
    public void addRow (Venda venda){
        this.lista.add(venda);
        this.fireTableDataChanged();
    }
    
    public void removeRow(int linha){
        this.lista.remove(linha);
        this.fireTableRowsDeleted(linha, linha);
    }
    
    public void removeRow(Venda venda){
        this.lista.remove(venda);
        this.fireTableDataChanged();
    }
    
    public Object getRow (int linha){
        return this.lista.get(linha);
    }
}
