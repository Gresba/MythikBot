package Shoppy;

import Bot.Embeds;
import Bot.SQLConnection;
import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.exceptions.ContextException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class ShoppyOrder {
    private Statement statement;

    private String id;
    private String email;
    private String pay_id;
    private String product_id;

    private String coupon_id;
    private String currency;
    private double exchange_rate;
    private int delivered;
    private int confirmations;
    private int required_confirmations;
    private String transaction_id;
    private String crypto_address;
    private double crypto_amount;
    private String shipping_address;

    private String gateway;

    private Object[] custom_fields;

    private String refund_id;
    private boolean is_replacement;
    private String replacement_id;
    private Date paid_at;
    private Date disputed_at;
    private Date created_at;
    private Double is_partial;
    private boolean crypto_received;
    private Object[] accounts;
    private int quantity;
    private Date date;

    private ShoppyProduct product;

    private double price;

    public ShoppyOrder()
    {
        statement = SQLConnection.getStatement();
    }

    public EmbedBuilder sendOrderEmbed()
    {
        boolean paidBoolean = false;
        if(this.getPaid_at() != null)
            paidBoolean = true;
        EmbedBuilder orderEmbed = new EmbedBuilder();
        try{
            orderEmbed.setTitle("**Better Alts Order**")
                    .setColor(7419530)
                    .addField("**Order ID:**", this.id, false)
                    .addField("**Product Name:**", this.getProduct().getTitle(), false)
                    .addField("**Price:**", "$" + (this.getPrice() * this.getQuantity()), true)
                    .addField("**Quantity:**", this.getQuantity() + "", true)
                    .addField("**Payment Method:**", this.getGateway(), true)
                    .addField("**Paid:**", "" + paidBoolean, true)
                    .addField("**Customer Email:**", this.getEmail(), true)
                    .addField("**Order Placed:**", this.getCreated_at().toString(), true);
        }catch (NullPointerException e){
            orderEmbed.setTitle("**Better Alts Order**")
                    .setDescription("That order ID does not exist!");
        }
        return orderEmbed;
    }

    public String getAccountList(ShoppyOrder orderObject, int amount, Guild guild)
    {
        String accounts = "";
        String accountType = orderObject.getProduct().getTitle();
        System.out.println("ACCOUNT TYPE: " + accountType);
        if(amount == 0)
            amount = orderObject.getQuantity();

        String retrieveAccountsQuery = "SELECT AccountInfo FROM accounts WHERE AccountType = '" + accountType + "' LIMIT " + amount;

        String deleteQuery = "DELETE FROM accounts WHERE AccountType = '" + accountType + "' LIMIT " + amount;


//        String retrieveAccountsQuery = "SELECT AccountInfo FROM accounts WHERE (AccountType = '" + accountType + "' AND GuildId = '" + guild.getId() + "') LIMIT " + amount;

//        String deleteQuery = "DELETE FROM accounts WHERE (AccountType = '" + accountType + "' AND GuildId = '" + guild.getId() + "') LIMIT " + amount;
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(retrieveAccountsQuery);

            while (resultSet.next()) {

                String account = resultSet.getString(1);

                accounts += account + "\n";

                System.out.println(account);
            }

            statement.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    public void sendProductInformation(String orderID, Member member, TextChannel channel, Guild guild, int amount) throws IOException, InterruptedException {
        ShoppyConnection shoppyConnection = new ShoppyConnection();
        try {
            ShoppyOrder orderObject = shoppyConnection.getShoppyOrder(orderID);
            String accounts = null;
            if (orderObject.getPaid_at() != null) {
                PrintStream outputFile = new PrintStream("output");
                accounts = getAccountList(orderObject, amount, guild);

                if (accounts != null) {
                    EmbedBuilder orderEmbed = new EmbedBuilder()
                            .setTitle("**Better Alts Order**")
                            .addField("**Order ID:**", orderID, false)
                            .setDescription(setOrderDescription(orderObject.getProduct().getTitle()));

                    if (accounts.length() < 1000) {
                        // Add products to the description
                        orderEmbed.addField("Alts", accounts, false);
                    }

                    Embeds.sendEmbed(orderEmbed, member, false);
                    Embeds.sendEmbed(orderEmbed, guild.getMemberById("639094715605581852"), false);

                    if (accounts.length() >= 1000) {
                        outputFile.print(accounts);
                        // Add products to a text file
                        member.getUser().openPrivateChannel().flatMap(privateChannel ->
                                privateChannel.sendFile(new File("output"), "message.txt")
                        ).queue();
                        guild.getMemberById("639094715605581852").getUser().openPrivateChannel().flatMap(privateChannel ->
                                privateChannel.sendFile(new File("output"), "message.txt")
                        ).queue();
                    }
                }
            }

            File outputFileDelete = new File("output");
            outputFileDelete.delete();

            // Add customer role to the user
            guild.addRoleToMember(member.getId(), guild.getRoleById("929116572063244339")).queue();
        } catch (IllegalArgumentException e){
            channel.sendMessage("That order ID does not exist!").queue();
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e){
            channel.sendMessage("**[ERROR]** Unable to send the product. Maybe customer DMs are off?").queue();
            e.printStackTrace();
        }
    }

    public boolean requiresVerification(Member member, String paymentMethod)
    {
        if (paymentMethod.equalsIgnoreCase("LTC") || paymentMethod.equalsIgnoreCase("BTC") || paymentMethod.equalsIgnoreCase("ETH")) {
            return false;
        }

        try {
            PreparedStatement selectVerifiedUser = statement.getConnection().prepareStatement("SELECT * FROM WhiteList WHERE MemberID = ?");
            selectVerifiedUser.setString(1, member.getId());

            ResultSet resultSet = selectVerifiedUser.executeQuery();
            if(resultSet.next())
                return false;
        }catch (SQLException e){
            e.printStackTrace();
        }

        return true;
    }

    public String setOrderDescription(String accountType)
    {
        if(accountType.toLowerCase().contains("mfa")) {
            return "Thank you for ordering " + accountType + "\n" +
                    """
                    **Format:** email:emailPassword
                    **Minecraft Password:** Wanker!!22
                    **Mail Site:** yahoo.com or mail.com (Check the domain of your account)
                    **Security Questions:** a a a
                    **How to change email for Mojang Account:** https://www.youtube.com/watch?v=AAQFrR0ShNE
                    **How to change email for Microsoft Account:** https://www.youtube.com/watch?v=duowaqDnwdM
                    *If the minecraft password or security questions are incorrect then reset it since you have access to the email*
                    *If the account requires migration, then migrate it.*
                    """;
        }else if(accountType.toLowerCase().contains("Unbanned") ){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password:username\n" +
                    "Use VyprVPN to avoid getting them security banned on Hypixel";
        }else if(accountType.toLowerCase().contains("hypixel") || accountType.toLowerCase().contains("nfa")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password:username";
        }else if(accountType.toLowerCase().contains("vypr")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password\n" +
                    "**Note:** Don't change password or you may lose access fast";
        }else if(accountType.contains("Yahoo")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password | Total Mails: <number of mails>\n" +
                    "Log into the email at yahoo.com";
        }else{
            return  "There was an issue with the order, created a ticket in the discord server";
        }
    }

    public ShoppyProduct getProduct() {
        return product;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public String getPay_id() {
        return pay_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public String getCurrency() {
        return currency;
    }

    public double getExchange_rate() {
        return exchange_rate;
    }

    public int getDelivered() {
        return delivered;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public int getRequired_confirmations() {
        return required_confirmations;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public String getCrypto_address() {
        return crypto_address;
    }

    public double getCrypto_amount() {
        return crypto_amount;
    }

    public String getShipping_address() {
        return shipping_address;
    }


    public String getGateway() {
        return gateway;
    }

    public Object[] getCustom_fields() {
        return custom_fields;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public boolean isIs_replacement() {
        return is_replacement;
    }

    public String getReplacement_id() {
        return replacement_id;
    }

    public Date getPaid_at() {
        return paid_at;
    }

    public Date getDisputed_at() {
        return disputed_at;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public Double getIs_partial() {
        return is_partial;
    }

    public boolean isCrypto_received() {
        return crypto_received;
    }

    public Object[] getAccounts() {
        return accounts;
    }
}
