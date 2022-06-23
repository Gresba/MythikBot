package BotObjects;

public class GuildObject {
    private final String guildId;
    private int ticketLimit;

    private String prefix;
    private String serverOwnerId;
    private String ticketCategoryId;
    private String logChannelId;
    private String staffRoleId;

    private String customerRoleId;

    public GuildObject(
            String guildId,
            String prefix,
            int ticketLimit,
            String serverOwnerId,
            String ticketCategoryId,
            String logChannelId,
            String staffRoleId,
            String customerRoleId) {
        this.guildId = guildId;
        this.prefix = prefix;
        this.ticketLimit = ticketLimit;
        this.serverOwnerId = serverOwnerId;
        this.ticketCategoryId = ticketCategoryId;
        this.logChannelId = logChannelId;
        this.staffRoleId = staffRoleId;
        this.customerRoleId = customerRoleId;
    }

    public String getGuildId() {
        return guildId;
    }
    public String getPrefix() {
        return prefix;
    }
    public int getTicketLimit() {
        return ticketLimit;
    }
    public String getServerOwnerId() {
        return serverOwnerId;
    }
    public String getTicketCategoryId() { return ticketCategoryId; }
    public String getLogChannelId() { return logChannelId; }
    public String getStaffRoleId() { return staffRoleId; }
    public String getCustomerRoleId() { return customerRoleId; }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public void setTicketLimit(int ticketLimit) {
        this.ticketLimit = ticketLimit;
    }
    public void setServerOwnerId(String serverOwnerId) {
        this.serverOwnerId = serverOwnerId;
    }
    public void setTicketCategoryId(String ticketCategoryId) {this.ticketCategoryId = ticketCategoryId; }
    public void setLogChannelId(String logChannelId) {
        this.logChannelId = logChannelId;
    }
    public void setStaffRoleId(String staffRoleId) {
        this.staffRoleId = staffRoleId;
    }
    public void setCustomerRoleId(String customerRoleId) { this.customerRoleId = customerRoleId; }
}
