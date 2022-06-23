package Bot;

import BotObjects.GuildObject;
import CustomObjects.Response;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

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
     * Return a string which contains the product(s) requested for
     *
     * @param guildId The guild the request was sent from
     * @param productType The type of product to retrieve
     * @param amount The amount to get
     * @return {String} The product(s)
     */
    public static String getProductByName(String guildId, String productType, int amount)
    {
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
     * Update the punishment table for a punishment
     *
     * @param punishmentType The type of punishment
     * @param member The member punished
     * @param reason The reason for the punishment
     * @param staffMemberId The id of the staff member which punished
     * @throws SQLException  Common SQL exceptions to be dealt with
     */
    public static void insertPunishment(String punishmentType, Member member, String reason, String staffMemberId) throws SQLException {

        PreparedStatement updatePunishmentQuery = connection.prepareStatement("INSERT INTO Punishments (Type, Reason, MemberId, StaffMemberId) VALUES (?, ?, ?, ?)");

        // Setting type, reason and member, respectively, for punishment
        updatePunishmentQuery.setString(1, punishmentType);
        updatePunishmentQuery.setString(2, reason);
        updatePunishmentQuery.setString(3, member.getId());
        updatePunishmentQuery.setString(4, staffMemberId);

        // Executing the query
        updatePunishmentQuery.executeUpdate();
    }

    /**
     * Inserts an order into the database
     *
     * @param orderId The order id for the order
     * @param MemberId The member that claimed the order
     * @throws SQLException Common SQL exceptions to be dealt with
     */
    public static void addOrder(String orderId, String MemberId) throws SQLException {
        java.util.Date date = new java.util.Date();
        long time = date.getTime();

        PreparedStatement insertOrder = statement.getConnection().prepareStatement("INSERT INTO Orders (OrderID, MemberID, ClaimedDate) VALUES (?, ?, ?)");
        insertOrder.setString(1, orderId);
        insertOrder.setString(2, MemberId);
        insertOrder.setTimestamp(3, new Timestamp(time));
        insertOrder.executeUpdate();
    }

    /**
     * Gets the information of the order in a database with an order id
     *
     * @param orderId The order id of an order
     * @return {ResultSet} The result of the query
     * @throws SQLException Common SQL exceptions to be dealt with
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
     * @return ResultSet The result of a query to get a guild's information
     * @throws SQLException Common SQL exceptions to be dealt with
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
     * @param guild The guild object where all the information about the guild is stored
     * @throws SQLException Exception that must be caught when calling this method
     */
    public static void updateGuildInfo(GuildObject guild) throws SQLException {
        PreparedStatement updateGuildQuery = connection.prepareStatement("UPDATE Guilds SET Prefix = ?, TicketLimit = ?, OwnerID = ?, TicketCategoryId = ? WHERE GuildID = ?");
        updateGuildQuery.setString(1, guild.getPrefix());
        updateGuildQuery.setInt(2, guild.getTicketLimit());
        updateGuildQuery.setString(3, guild.getServerOwnerId());
        updateGuildQuery.setString(4, guild.getTicketCategoryId());
        updateGuildQuery.setString(5, guild.getGuildId());

        updateGuildQuery.executeUpdate();
    }

    /**
     * Adds a default record of a guild in the Guilds table
     *
     * @param guild The id of the guild
     * @throws SQLException Common SQL exceptions to be dealt with
     */
    public static void insertGuild(Guild guild) throws SQLException {
        PreparedStatement insertGuildQuery = connection.prepareStatement("INSERT INTO Guilds VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        // Guild Id
        insertGuildQuery.setString(1, guild.getId());

        // Guild command prefix
        insertGuildQuery.setString(2, "b!");

        // Ticket limited
        insertGuildQuery.setInt(3, 1);

        // Server owner id
        insertGuildQuery.setString(4, "");

        // Ticket category id
        insertGuildQuery.setString(5, "");

        // Staff role id
        insertGuildQuery.setString(6, "");

        // Log channel id
        insertGuildQuery.setString(7, "");

        // Customer role id
        insertGuildQuery.setString(8, "");

        insertGuildQuery.executeUpdate();
    }

    /**
     * Inserts a new ticket record into the Tickets table
     *
     * @param ticketChannelId The channel id of the new ticket channel
     * @param memberId The member of the creator of the ticket
     * @throws SQLException Common exception to be dealt with
     */
    public static void insertTicket(String ticketChannelId, String memberId) throws SQLException {
        PreparedStatement insertTicketQuery = connection.prepareStatement("INSERT INTO Tickets VALUES (?, ?)");

        insertTicketQuery.setString(1, ticketChannelId);
        insertTicketQuery.setString(2, memberId);

        insertTicketQuery.executeUpdate();
    }

    /**
     * Delete a ticket record from the database
     *
     * @param ticketChannelId The id of the ticket channel to delete
     * @throws SQLException Common SQL error which must be caught
     */
    public static void deleteTicket(String ticketChannelId) throws SQLException {
        PreparedStatement deleteTicketQuery = connection.prepareStatement("DELETE FROM Tickets WHERE TicketId = ?");

        deleteTicketQuery.setString(1, ticketChannelId);

        deleteTicketQuery.executeUpdate();
    }

    /**
     * Inserts a new record of a response to the Response table which will be used to respond to members depending on the trigger word
     *
     * @param guildId The id of the guild that the response will be associated
     * @param response The response object to populate the record
     * @throws SQLException Common SQL exceptions to be dealt with
     */
    public static void insertResponse(String guildId, Response response) throws SQLException {
        PreparedStatement insertResponseQuery = connection.prepareStatement("INSERT INTO Response VALUES (?, ?, ?, ?, ?)");

        insertResponseQuery.setString(1, guildId);
        insertResponseQuery.setString(2, response.getTriggerString());
        insertResponseQuery.setString(3, response.getResponse());
        insertResponseQuery.setBoolean(4, response.isDeleteTriggerMsg());
        insertResponseQuery.setBoolean(5, response.isContains());

        insertResponseQuery.executeUpdate();
    }

    /**
     * Delete a response from the Response table by finding the trigger word and guild id
     *
     * @param guildId The id of the guild the response belongs to
     * @param triggerWord The word that will trigger the response
     * @throws SQLException Common SQL exceptions to be dealt with
     */
    public static void deleteResponse(String guildId, String triggerWord) throws SQLException {
        // Building the query
        PreparedStatement deleteResponseQuery = connection.prepareStatement(
                "DELETE FROM Response " +
                    "WHERE TriggerWord = ? )" +
                    "AND GuildId = ?");

        // Populating the query
        deleteResponseQuery.setString(1, triggerWord);
        deleteResponseQuery.setString(2, guildId);

        // Executing the query
        deleteResponseQuery.executeUpdate();
    }
}
