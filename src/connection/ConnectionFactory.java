package connection;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConnectionFactory {
    public Connection getConnection(){
        try {
            //return (Connection) DriverManager.getConnection("jdbc:mysql://mysql796.umbler.com:41890/bd_javamatrix", "us_javamatrix", "matrix123");
            return (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_javamatrix", "root", "");
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Não foi possível se conectar ao Banco de Dados!", "Caixa de Afirmação", 0);
            throw new RuntimeException(e);
        }
    }
}