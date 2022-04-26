package Bot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.*;

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

    public static void addDefaultUser(Guild guild, Member member){
        try {
            PreparedStatement insertUserIntoDb = statement.getConnection().prepareStatement("INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?)");

            // Guild ID
            insertUserIntoDb.setString(1, guild.getId());

            // Member ID
            insertUserIntoDb.setString(2, member.getId());

            // Warning Count
            insertUserIntoDb.setInt(3, 0);

            // Muted
            insertUserIntoDb.setBoolean(4, false);

            // Ticket Count
            insertUserIntoDb.setInt(5, 0);

            // Invites
            insertUserIntoDb.setInt(6, 0);

            insertUserIntoDb.executeUpdate();


            System.out.println("[Log] SQL: User " + member.getUser().getAsTag() + "successfully registered into MySQL DB!");

            // Checking if the user is in the database
        } catch (SQLIntegrityConstraintViolationException e) {

            try
            {
                // Checking if the user was muted
                PreparedStatement checkMuted = statement.getConnection().prepareStatement("SELECT Muted FROM Users WHERE MemberID = ?");
                checkMuted.setString(1, member.getId());

                ResultSet mutedOutput = checkMuted.executeQuery();

                boolean muted = mutedOutput.next();

                // If the user was muted before then mute them again
                if (muted)
                {
                    guild.addRoleToMember(member.getId(), guild.getRoleById("936718165130481705")).queue();
                }
            } catch (SQLException ex) {
                System.out.println("[ERROR] Error with reading the user's information");
                ex.printStackTrace();
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
