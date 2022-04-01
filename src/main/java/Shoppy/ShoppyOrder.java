package Shoppy;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Date;

public class ShoppyOrder {
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

    }

    public EmbedBuilder sendOrderEmbed(ShoppyOrder order)
    {
        boolean paidBoolean = false;
        if(order.getPaid_at() != null)
            paidBoolean = true;

        EmbedBuilder orderEmbed = new EmbedBuilder()
                .setTitle("**Better Alts Order**")
                .setDescription("**Order ID:** " + order.getId())
                .setColor(7419530)
                .addField("**Product Name:**", order.getProduct().getTitle(), false)
                .addField("**Price:**", "$" + (order.getPrice() * order.getQuantity()), true)
                .addField("**Quantity:**", order.getQuantity() + "", true)
                .addField("**Payment Method:**", order.getGateway(), true)
                .addField("**Paid:**", "" + paidBoolean, true)
                .addField("**Customer Email:**", order.getEmail(), true)
                .addField("**Order Placed:**", order.getCreated_at().toString(), true);
        return orderEmbed;
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
