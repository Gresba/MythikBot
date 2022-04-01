package Bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SQLConnection {
    private static Statement statement;

    public static Statement getStatement() {
        try {
            String SQLUsername = System.getenv("MYTHIK_BOT_SQL_USER");
            String SQLPassword = System.getenv("MYTHIK_BOT_SQL_PASSWORD");

            Connection connection = DriverManager.getConnection("jdbc:mysql://91.134.110.57:3306/mythikdb", SQLUsername, SQLPassword);

            statement = connection.createStatement();
        }catch (Exception e){
            e.printStackTrace();
        }
        return statement;
    }
}
