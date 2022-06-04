/**
 *
 * @author LUCAS MATHEUS
 */

package others;
public class Data {
    public static String dataParaBanco (String dataAplicacao){
       return dataAplicacao.substring(6, 10) + "-" + dataAplicacao.substring(3, 5) + "-" + dataAplicacao.substring(0, 2); 
   }
   
   public static String dataParaAplicacao (String dataBanco){
       return dataBanco.substring(8, 10) + "/" + dataBanco.substring(5, 7) + "/" + dataBanco.substring(0, 4);
   }
   
   public static String dataHoraParaBanco (String dataHoraAplicacao){
       return Data.dataParaBanco(dataHoraAplicacao) + dataHoraAplicacao.substring(10, 16) + ":00";
   }
   
   public static String dataHoraParaAplicacao (String dataHoraBanco){
       return Data.dataParaAplicacao(dataHoraBanco) + dataHoraBanco.substring(10, 16);
   }   
}
