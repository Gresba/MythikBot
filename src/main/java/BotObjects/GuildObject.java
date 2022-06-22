package BotObjects;

public class GuildObject {
    private final String guildId;
    private String prefix;
    private int ticketLimit;
    private String serverOwnerId;
    private String ticketCategoryId;

    public GuildObject(String guildId, String prefix, int ticketLimit, String serverOwnerId, String ticketCategoryId) {
        this.guildId = guildId;
        this.prefix = prefix;
        this.ticketLimit = ticketLimit;
        this.serverOwnerId = serverOwnerId;
        this.ticketCategoryId = ticketCategoryId;
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
}
