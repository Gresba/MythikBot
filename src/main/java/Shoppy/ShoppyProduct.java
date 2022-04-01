package Shoppy;

public class ShoppyProduct {
    private String title;
    private String description;
    private boolean unlisted;
    private String type;
    private double price;
    private String currency;
    private int stock_warning;
    private int confirmations;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUnlisted() {
        return unlisted;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public int getStock_warning() {
        return stock_warning;
    }


    public int getConfirmations() {
        return confirmations;
    }
}
