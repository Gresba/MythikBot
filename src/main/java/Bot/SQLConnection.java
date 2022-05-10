package Bot;

import Shoppy.ShoppyConnection;
import Shoppy.ShoppyOrder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;
import java.sql.*;

public class SQLConnection {
    private static Statement statement;
    private static Connection connection;
    /**
     * Gets the statement to connect with the database
     *
     * @return The SQL statement
     */
    public static Statement getStatement() {
        try {

            String SQLUsername = System.getenv("MYTHIK_BOT_SQL_USER");
            String SQLPassword = System.getenv("MYTHIK_BOT_SQL_PASSWORD");

            connection = DriverManager.getConnection("jdbc:mysql://91.134.110.57:3306/mythikdb", SQLUsername, SQLPassword);

            statement = connection.createStatement();
        }catch (Exception e){
            e.printStackTrace();
        }
        return statement;
    }

    public static void insertOrder(String orderID, Member member, Date insertedDate) throws SQLException {
        PreparedStatement insertOrder = connection.prepareStatement("INSERT INTO Orders (OrderID, MemberID, ClaimedDate) VALUES (?, ?, ?)");
        insertOrder.setString(1, orderID);
        insertOrder.setString(2, member.getId());
        insertOrder.setDate(3, insertedDate);
        insertOrder.executeUpdate();
    }

    /**
     * Get the product from the order ID
     *
     * @param orderID The order id related to the product
     * @param guildId The guild that the member belongs to
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getProductDetails(String orderID, String guildId, int amount) throws IOException, InterruptedException {
        ShoppyOrder orderObject = ShoppyConnection.getShoppyOrder(orderID);

        String productType = orderObject.getProduct().getTitle();

        if(amount == 0)
            amount = orderObject.getQuantity();

        String accounts = "";

        try {
            // Building the query
            PreparedStatement retrieveProductQuery = connection.prepareStatement("SELECT AccountInfo FROM Accounts WHERE (AccountType = ? AND GuildId = ?) LIMIT ?");

            PreparedStatement deleteProductQuery = connection.prepareStatement("DELETE FROM Accounts WHERE (AccountType = ? AND GuildId = ?) LIMIT ?");

            // Setting the account type
            retrieveProductQuery.setString(1, productType);
            deleteProductQuery.setString(1, productType);

            // Setting the guild id
            retrieveProductQuery.setString(2, guildId);
            deleteProductQuery.setString(2, guildId);

            // Setting the amount of accounts to get
            retrieveProductQuery.setInt(3, amount);
            deleteProductQuery.setInt(3, amount);

            // Executing the query to get the accounts
            ResultSet resultSet = retrieveProductQuery.executeQuery();

            statement.close();

            // Loop through the result set to get the accounts
            while (resultSet.next()) {

                // Get the account
                String account = resultSet.getString(1);

                // Add the account to the string that will be returned
                accounts += account + "\n";
            }

            // Delete the accounts retrieved from the database
            deleteProductQuery.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    /**
     * Updates muted status of a user in the database
     *
     * @param member The member to update muted status for
     * @param reason The reason behind the mute if there is any
     */
    public static void updateMute(Member member, String reason) throws SQLException {
        // Variable to decide whether the user gets muted or unmuted
        boolean muted = true;

        // If there is no reason that means the command is unmute
        if(reason == null)
            muted = false;

        // Create the query
        PreparedStatement updateMuteQuery = connection.prepareStatement("UPDATE Users SET Muted = ? WHERE MemberID = ?");

        // Populate the query
        updateMuteQuery.setBoolean(1, muted);
        updateMuteQuery.setString(2, member.getId());

        // Execute the query
        updateMuteQuery.executeUpdate();

    }

    /**
     * Update the punishment table for a punishment
     *
     * @param punishmentType The type of punishment
     * @param member The member punished
     * @param reason The reason for the punishment
     */
    public static void updatePunishment(String punishmentType, Member member, String reason) throws SQLException {
        PreparedStatement updatePunishmentQuery = connection.prepareStatement("INSERT INTO Punishments VALUES (?, ?, ?)");

        // Setting type, reason and member, respectively, for punishment
        updatePunishmentQuery.setString(1, punishmentType);
        updatePunishmentQuery.setString(2, reason);
        updatePunishmentQuery.setString(3, member.getId());

        // Executing the query
        updatePunishmentQuery.executeUpdate();
    }


    /**
     * Add a member to the database and returns whether the member was successfully added into the database
     *
     * @param guild The guild ID the member belongs
     * @param member The member that will be added into the database
     * @return Whether the member was successfully added into the database
     */
    public static boolean addDefaultUser(Guild guild, Member member){

        // Try to add the user into the database
        try {

            // The SQL query to add the user into the database
            PreparedStatement insertUserIntoDb = connection.prepareStatement("INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?, ?)");

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

            // Inviter
            insertUserIntoDb.setString(7, "");

            insertUserIntoDb.executeUpdate();

            return false;

        // Checking if the user is in the database
        } catch (SQLIntegrityConstraintViolationException e) {
            try
            {
                // Checking if the user was muted
                PreparedStatement checkMuted = connection.prepareStatement("SELECT Muted FROM Users WHERE MemberID = ?");
                checkMuted.setString(1, member.getId());

                ResultSet mutedOutput = checkMuted.executeQuery();

                mutedOutput.next();
                boolean muted = mutedOutput.getBoolean(1);

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
        return true;
    }
}
