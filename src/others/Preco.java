/**
 *
 * @author LUCAS MATHEUS
 */

package others;
public class Preco {
    public static String precoParaBanco (String precoAplicacao){
       return precoAplicacao.replace("R$ ", "").replace(".", "").replace(",", ".");
   }
}
