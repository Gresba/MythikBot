package Bot;

import BotObjects.GuildObject;
import CustomObjects.Response;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Scanner;

public class SQLConnection {
    private static Connection connection;

    /**
     * Gets the statement to connect with the database
     *
     * @return Statement The SQL statement
     */
    public static Connection getConnection() {
        try {
            String SQLUsername = System.getenv("MYTHIK_BOT_SQL_USER");
            String SQLPassword = System.getenv("MYTHIK_BOT_SQL_PASSWORD");

            connection = DriverManager.getConnection("jdbc:mysql://91.134.110.57:3306/mythikdb", SQLUsername, SQLPassword);

        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Return a string which contains the product(s) requested for
     *
     * @param guildId The guild the request was sent from
     * @param productType The type of product to retrieve
     * @param amount The amount to get
     * @return The product(s)
     */
    public static String getProductByName(String guildId, String productType, int amount){
        StringBuilder product = new StringBuilder();

        try {
            // Building the query
            PreparedStatement retrieveProductQuery = getConnection().prepareStatement(
            """
                SELECT AccountInfo
                FROM Accounts
                WHERE (AccountType = ? AND GuildId = ?)
                LIMIT ?
                """);

            PreparedStatement deleteProductQuery = getConnection().prepareStatement(
            """
                DELETE FROM Accounts
                WHERE (AccountType = ? AND GuildId = ?)
                LIMIT ?
                """);

            // Setting the product type
            retrieveProductQuery.setString(1, productType);
            deleteProductQuery.setString(1, productType);

            // Setting the guild id
            retrieveProductQuery.setString(2, guildId);
            deleteProductQuery.setString(2, guildId);

            // Setting the amount of product to get
            retrieveProductQuery.setInt(3, amount);
            deleteProductQuery.setInt(3, amount);

            // Executing the query to get the product
            ResultSet resultSet = retrieveProductQuery.executeQuery();

            connection.close();

            // Loop through the result set to get the product
            while (resultSet.next()) {

                // Get the product
                String productInfo = resultSet.getString(1);

                // Add the account to the string that will be returned
                product.append(productInfo).append("\n");
            }

            // Delete the accounts retrieved from the database
            deleteProductQuery.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing the connection");
                e.printStackTrace();
            }
        }

        return product.toString();
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
        PreparedStatement updatePunishmentQuery = getConnection().prepareStatement(
        """
            INSERT INTO Punishments (Type, Reason, MemberId, StaffMemberId)
            VALUES (?, ?, ?, ?)
            """);

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

        PreparedStatement insertOrder = getConnection().prepareStatement(
        """
            INSERT INTO Orders (OrderID, MemberID, ClaimedDate)
            VALUES (?, ?, ?)
            """);
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
        PreparedStatement getOrder = getConnection().prepareStatement(
        """
            SELECT * FROM Orders
            WHERE OrderID = ?
            """);
        getOrder.setString(1, orderId);

        return getOrder.executeQuery();
    }

    /**
     * Remove an order record from a database
     *
     * @param orderId The order ID for the record to remove
     * @throws SQLException Common SQL issues that need to be caught
     */
    public static void removeOrder(String orderId) throws SQLException {
        PreparedStatement removeOrder = getConnection().prepareStatement(
          """
          DELETE FROM Orders
          WHERE OrderId = ?
          """);

        removeOrder.setString(1, orderId);
        removeOrder.executeUpdate();
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
            PreparedStatement insertUserIntoDb = getConnection().prepareStatement(
            """
                INSERT INTO Users
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """);

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
        PreparedStatement preparedStatement = getConnection().prepareStatement(query);

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
    public static void updateGuildInfo(GuildObject guild) throws SQLException, IllegalAccessException {
        PreparedStatement updateGuildQuery = getConnection().prepareStatement(
        """
            UPDATE Guilds
            SET Prefix = ?, TicketLimit = ?, OwnerID = ?, TicketCategoryId = ?, StaffId = ?, LogChannelId = ?,
                CustomerRoleId = ?, MemberRoleId = ?, JoinChannelId = ?, LeaveChannelId = ?
            WHERE GuildID = ?
            """);

        int counter = 1;

        // Loops through the fields in the guilds class
        for (Field field : guild.getClass().getDeclaredFields()) {
            // Allow access
            field.setAccessible(true);

            var fieldValue = field.get(guild);

            // If the value isn't set then use the current value
            if(fieldValue == null || fieldValue.equals(0)) {
                fieldValue = String.valueOf(field.get(BotProperty.guildsHashMap.get(guild.getGuildId())));
            }else{
                field.set(BotProperty.guildsHashMap.get(guild.getGuildId()), fieldValue);
            }
            updateGuildQuery.setObject(counter++, fieldValue);
        }

        updateGuildQuery.executeUpdate();
    }

    /**
     * Adds a default record of a guild in the Guilds table
     *
     * @param guild The id of the guild
     * @throws SQLException Common SQL exceptions to be dealt with
     */
    public static void insertGuild(Guild guild) throws SQLException {
        PreparedStatement insertGuildQuery = getConnection().prepareStatement(
        """
            INSERT INTO Guilds
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """);

        // Guild ID
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
        PreparedStatement insertTicketQuery = getConnection().prepareStatement(
        """
            INSERT INTO Tickets
            VALUES (?, ?)
            """);

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
        PreparedStatement deleteTicketQuery = getConnection().prepareStatement(
        """
            DELETE FROM Tickets
            WHERE TicketId = ?
            """);

        deleteTicketQuery.setString(1, ticketChannelId);

        deleteTicketQuery.executeUpdate();
    }

    /**
     * Inserts a new record of a response to the Responses table which will be used to respond to members depending on the trigger word
     *
     * @param guildId The id of the guild that the response will be associated
     * @param response The response object to populate the record
     * @throws SQLException Common SQL exceptions to be dealt with
     */
    public static void insertResponse(String guildId, Response response) throws SQLException {
        PreparedStatement insertResponseQuery = getConnection().prepareStatement(
        """
            INSERT INTO Responses
            VALUES (?, ?, ?, ?, ?)
            """);

        insertResponseQuery.setString(1, guildId);
        insertResponseQuery.setString(2, response.getTriggerString());
        insertResponseQuery.setString(3, response.getResponse());
        insertResponseQuery.setBoolean(4, response.isDeleteTriggerMsg());
        insertResponseQuery.setBoolean(5, response.isContains());

        insertResponseQuery.executeUpdate();
    }

    /**
     * Delete a response from the Responses table by finding the trigger word and guild id
     *
     * @param guildId The id of the guild the response belongs to
     * @param triggerWord The word that will trigger the response
     * @throws SQLException Common SQL exceptions to be dealt with
     */
    public static void deleteResponse(String guildId, String triggerWord) throws SQLException {
        // Building the query
        PreparedStatement deleteResponseQuery = getConnection().prepareStatement(
        """
            DELETE FROM Responses
            WHERE TriggerWord = ?
            AND GuildId = ?
            """);

        // Populating the query
        deleteResponseQuery.setString(1, triggerWord);
        deleteResponseQuery.setString(2, guildId);

        // Executing the query
        deleteResponseQuery.executeUpdate();
    }

    /**
     * Uploads data from an inputFile to the database by iterating through every line.
     *
     * @param guildId The guild the account belongs to
     * @param productType The type of product that is being uploaded
     * @param inputFile The input file containing the products
     * @return The amount of products uploaded
     * @throws FileNotFoundException File may not be found so catch that error
     * @throws SQLException Common SQL exceptions the must be caught
     */
    public static int uploadProducts(String guildId, String productType, File inputFile) throws FileNotFoundException, SQLException {
        Scanner input = new Scanner(inputFile);

        PreparedStatement insertRecord = getConnection().prepareStatement("INSERT INTO Accounts (AccountInfo, AccountType, GuildID) VALUES (?, ?, ?)");
        int productCounter = 1;

        // Build the query
        while (input.hasNext()) {
            String productInfo = input.nextLine();

            insertRecord.setString(1, productInfo);
            insertRecord.setString(2, productType);
            insertRecord.setString(3, guildId);

            insertRecord.addBatch();

            productCounter++;
        }

        insertRecord.executeBatch();

        return productCounter;
    }

    public static ResultSet getInvites(String guildId) throws SQLException {
        PreparedStatement getInvites = getConnection().prepareStatement(
                """
                SELECT * FROM Invites
                WHERE GuildId = ?
                """);
        getInvites.setString(1, guildId);

        return getInvites.executeQuery();
    }

}
