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
     * @return Statement The SQL statement
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

    /**
     * Insert an order into the
     *
     * @param orderID
     * @param member
     * @param insertedDate
     * @throws SQLException
     */
    public static void insertOrder(String orderID, Member member, Date insertedDate) throws SQLException {
        PreparedStatement insertOrder = connection.prepareStatement("INSERT INTO Orders (OrderID, MemberID, ClaimedDate) VALUES (?, ?, ?)");
        insertOrder.setString(1, orderID);
        insertOrder.setString(2, member.getId());
        insertOrder.setDate(3, insertedDate);
        insertOrder.executeUpdate();
    }

    /**
     *
     */
    public static String getProductByName(String guildId, String accountType, int amount)
    {
        String accounts = "";

        try {
            // Building the query
            PreparedStatement retrieveProductQuery = connection.prepareStatement("SELECT AccountInfo FROM Accounts WHERE (AccountType = ? AND GuildId = ?) LIMIT ?");

            PreparedStatement deleteProductQuery = connection.prepareStatement("DELETE FROM Accounts WHERE (AccountType = ? AND GuildId = ?) LIMIT ?");

            // Setting the account type
            retrieveProductQuery.setString(1, accountType);
            deleteProductQuery.setString(1, accountType);

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

        return getProductByName(guildId, productType, amount);
    }

    /**
     * Update the punishment table for a punishment
     *
     * @param punishmentType The type of punishment
     * @param member The member punished
     * @param reason The reason for the punishment
     */
    public static void updatePunishment(String punishmentType, Member member, String reason) throws SQLException {
        PreparedStatement updatePunishmentQuery = connection.prepareStatement("INSERT INTO Punishments (Type, Reason, MemberID) VALUES (?, ?, ?)");

        // Setting type, reason and member, respectively, for punishment
        updatePunishmentQuery.setString(1, punishmentType);
        updatePunishmentQuery.setString(2, reason);
        updatePunishmentQuery.setString(3, member.getId());

        // Executing the query
        updatePunishmentQuery.executeUpdate();
    }

    /**
     * Inserts an order into the database
     *
     * @param orderId The order id for the order
     * @param MemberId The member that claimed the order
     * @param claimedDate The date the order was claimed
     */
    public static void addOrder(String orderId, String MemberId, Timestamp claimedDate) throws SQLException {
        PreparedStatement insertOrder = statement.getConnection().prepareStatement("INSERT INTO Orders (OrderID, MemberID, ClaimedDate) VALUES (?, ?, ?)");
        insertOrder.setString(1, orderId);
        insertOrder.setString(2, MemberId);
        insertOrder.setTimestamp(3, claimedDate);
        insertOrder.executeUpdate();
    }

    /**
     * Gets the information of the order in a database with an order id
     *
     * @param orderId The order id of an order
     * @return {ResultSet} The result of the query
     * @throws SQLException
     */
    public static ResultSet getOrder(String orderId) throws SQLException {
        PreparedStatement getOrder = statement.getConnection().prepareStatement("SELECT * FROM Orders WHERE OrderID = ?");
        getOrder.setString(1, orderId);

        ResultSet retrievedOrder = getOrder.executeQuery();
        return retrievedOrder;
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

            // Execute the query
            insertUserIntoDb.executeUpdate();

            return true;

        // Checking if the user is in the database
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the information about a guild or every guild depending on whether the argument is empty
     *
     * @param guildId Optional param for the id to get the guild information for
     *
     * @return ResultSet The result of a query to get a guild's information
     */
    public static ResultSet getGuildInfo(String ...guildId) throws SQLException {
        String query;

        // Check if the guildId is empty
        if(guildId.length == 0)
            query = "SELECT * FROM Guilds";
        else
            query = "SELECT * FROM Guilds WHERE GuildID = ?";

        // Get the query ready
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        // Fill in the query if needed
        if(guildId.length != 0)
            preparedStatement.setString(1, guildId[0]);

        // Return the result of the query
        return preparedStatement.executeQuery();
    }

    /**
     * Update the server configuration info for the guild the method is called in
     *
     * @param guild The id of the guild
     * @param guildPrefix The prefix to use the commands for
     * @param ticketLimit The amount of tickets a user can make in a guild
     * @param serverOwnerID The server owner's Id
     * @throws SQLException Exception that must be caught when calling this method
     */
    public static void updateGuildInfo(Guild guild, String guildPrefix, int ticketLimit, String serverOwnerID) throws SQLException {
        PreparedStatement updateGuildQuery = connection.prepareStatement("UPDATE Guilds SET Prefix = ?, TicketLimit = ?, OwnerID = ? WHERE GuildID = ?");
        updateGuildQuery.setString(1, guildPrefix);
        updateGuildQuery.setInt(2, ticketLimit);
        updateGuildQuery.setString(3, serverOwnerID);
        updateGuildQuery.setString(4, guild.getId());

        updateGuildQuery.executeUpdate();
    }

    /**
     * Adds a default record of a guild in the Guilds table
     *
     * @param guild The id of the guild
     * @throws SQLException
     */
    public static void addDefaultGuild(Guild guild) throws SQLException {
        PreparedStatement updateGuildQuery = connection.prepareStatement("INSERT INTO Guilds VALUES (?, ?, ?, ?)");
        updateGuildQuery.setString(1, guild.getId());
        updateGuildQuery.setString(2, "b!");
        updateGuildQuery.setInt(3, 1);
        updateGuildQuery.setString(4, "");

        updateGuildQuery.executeUpdate();
    }
}
