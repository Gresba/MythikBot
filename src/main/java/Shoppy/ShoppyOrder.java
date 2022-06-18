package Shoppy;

import Bot.SQLConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class ShoppyOrder {
    private Statement statement;

    private String id;
    private String email;

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
            orderEmbed.setTitle("**__Better Alts Order__**")
                    .setColor(7419530)
                    .addField("**Order ID:**", id, false)
                    .addField("**Product Name:**", product.getTitle(), false)
                    .addField("**Price:**", "$" + (price * quantity), true)
                    .addField("**Quantity:**", quantity + "", true)
                    .addField("**Payment Method:**", gateway, true)
                    .addField("**Paid:**", "" + paidBoolean, true)
                    .addField("**Customer Email:**", email, true)
                    .addField("**Order Placed:**", created_at.toString(), true);
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
        if(amount == 0)
            amount = orderObject.getQuantity();

        String retrieveAccountsQuery = "SELECT AccountInfo FROM accounts WHERE AccountType = '" + accountType + "' LIMIT " + amount;

        String deleteQuery = "DELETE FROM accounts WHERE AccountType = '" + accountType + "' LIMIT " + amount;

        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(retrieveAccountsQuery);

            while (resultSet.next()) {

                String account = resultSet.getString(1);

                accounts += account + "\n";
            }

            statement.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    public boolean requiresVerification(Member member, String paymentMethod) {
        if (paymentMethod.equalsIgnoreCase("LTC") || paymentMethod.equalsIgnoreCase("BTC") || paymentMethod.equalsIgnoreCase("ETH")) {
            return false;
        }

        try {
            PreparedStatement selectVerifiedUser = statement.getConnection().prepareStatement("SELECT * FROM WhiteList WHERE MemberID = ?");
            selectVerifiedUser.setString(1, member.getId());

            ResultSet resultSet = selectVerifiedUser.executeQuery();
            if (resultSet.next())
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
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
