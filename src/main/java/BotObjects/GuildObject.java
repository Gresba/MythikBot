package BotObjects;

public class GuildObject {
    private String guildId;
    private String prefix;
    private int ticketLimit;
    private String serverOwnerId;

    private String ticketCategoyId;

    public GuildObject(String guildId, String prefix, int ticketLimit, String serverOwnerId, String ticketCategoyId) {
        this.guildId = guildId;
        this.prefix = prefix;
        this.ticketLimit = ticketLimit;
        this.serverOwnerId = serverOwnerId;
        this.ticketCategoyId = ticketCategoyId;
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
    public String getTicketCategoyId() { return ticketCategoyId; }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public void setTicketLimit(int ticketLimit) {
        this.ticketLimit = ticketLimit;
    }
    public void setServerOwnerId(String serverOwnerId) {
        this.serverOwnerId = serverOwnerId;
    }
    public void setTicketCategoyId(String ticketCategoyId) {this.ticketCategoyId = ticketCategoyId; }
}
